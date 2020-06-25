package com.seany.appcaesar.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.seany.appcaesar.AdbLib.AdbBase64;
import com.seany.appcaesar.AdbLib.AdbConnection;
import com.seany.appcaesar.AdbLib.AdbCrypto;
import com.seany.appcaesar.AdbLib.AdbStream;
import com.seany.appcaesar.exception.CaesarApplication;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class AdbUtils {

    private static String TAG = "AdbUtils";
    private static final SimpleDateFormat LOG_FILE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.CHINA);
    private static volatile AdbConnection connection;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static Boolean isRoot = null;
    private static List<AdbStream> streams = new ArrayList<>();
    private static ExecutorService cachedExecutor = Executors.newCachedThreadPool();
    private static ExecutorService executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE,0, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());


    /**
     * 是否已初始化
     * @return
     */
    public static boolean isInitialized() {
        return connection != null;
    }

    public static boolean grantAdbPermission(Context context) {
        if (!AdbUtils.isInitialized()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    AdbUtils.generateConnection();
                }
            });
            Log.e(TAG, "ADB failed");
            return false;
        }
        return true;
    }


    public static void executeCmd(final String cmd) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    AdbUtils.execAdbCmd(cmd, 0);
                }
            });
        } else {
            AdbUtils.execAdbCmd(cmd, 0);
        }
    }

    private static AdbBase64 getBase64Impl() {
        return new AdbBase64() {
            @Override
            public String encodeToString(byte[] data) {
                return Base64.encodeToString(data, 2);
            }
        };
    }


    public static synchronized boolean generateConnection() {
        if (connection != null && connection.isFine()) {
            return true;
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (IOException e1) {
                Log.e(TAG, "generateConnection: " + e1.getMessage(), e1 );
            } finally {
                connection = null;
            }
        }

        Socket socket;
        AdbCrypto crypto;
        AdbBase64 base64 = getBase64Impl();

        //获取连接的公私钥
        File privateKey = new File(CaesarApplication.getInstance().getFilesDir(),"privateKey");
        File publicKey = new File(CaesarApplication.getInstance().getFilesDir(), "publicKey");

        if (!privateKey.exists() || !publicKey.exists()) {
            try {
                crypto = AdbCrypto.generateAdbKeyPair(base64);
                privateKey.delete();
                publicKey.delete();
                crypto.saveAdbKeyPair(privateKey,publicKey);
            } catch (NoSuchAlgorithmException | IOException e) {
                Log.e(TAG, "generateConnection: " + e.getMessage(),e );
                return false;
            }
        } else {
            try {
                crypto = AdbCrypto.loadAdbKeyPair(base64, privateKey, publicKey);
            } catch (Exception e) {
                Log.e(TAG, "generateConnection: " + e.getMessage(), e );
                try {
                    crypto = AdbCrypto.generateAdbKeyPair(base64);
                    privateKey.delete();
                    publicKey.delete();
                    crypto.saveAdbKeyPair(privateKey, publicKey);
                } catch (NoSuchAlgorithmException | IOException ex) {
                    Log.e(TAG, "generateConnection: " + ex.getMessage(), ex );
                    return false;
                }
            }
        }

        //开始连接adb
        Log.i(TAG, "Socket Connecting...");
        try {
            socket = new Socket("localhost", 5555);
        } catch (IOException e) {
            Log.e(TAG, "generateConnection: ", e);
            return false;
        }
        Log.i(TAG, "Socket connected");

        AdbConnection conn;
        try {
            conn = AdbConnection.create(socket, crypto);
            Log.i(TAG, "ADB connecting...");
            //10s超时
            conn.connect(10 * 1000);
        } catch (Exception e) {
            Log.e(TAG, "ADB connect failed", e );
            //socket 关闭
            if (socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException e1) {
                    Log.e(TAG, "generateConnection: " + e1.getMessage(), e1 );
                }
            }
            return false;
        }
        connection = conn;
        Log.i(TAG, "ADB connected");
        return true;
    }

    public static String execAdbCmd(final String cmd, int wait) {
        // 主线程的话走Callable
        if (Looper.myLooper() == Looper.getMainLooper()) {
            if (wait > 5000 || wait == 0) {
                Log.w(TAG, "主线程配置的等待时间" + wait + "ms过长，修改为5000ms");
                wait = 5000;
            }

            final int finalWait = wait;
            Callable<String> callable = new Callable<String>() {
                @Override
                public String call() {
                    return _execAdbCmd(cmd, finalWait);
                }
            };
            Future<String> result = cachedExecutor.submit(callable);

            // 等待执行完毕
            try {
                return result.get();
            } catch (InterruptedException e) {
                Log.e(TAG, "Catch java.lang.InterruptedException: " + e.getMessage(), e);
            } catch (ExecutionException e) {
                Log.e(TAG, "Catch java.util.concurrent.ExecutionException: " + e.getMessage(), e);
            }
            return null;
        }
        return _execAdbCmd(cmd, wait);
    }

    /**
     * 执行Adb命令
     * @param cmd 对应命令
     * @param wait 等待执行时间，0表示一直等待
     * @return 命令行输出
     */
    public static String _execAdbCmd(final String cmd, final int wait) {
        if (connection == null) {
            Log.e(TAG, "no connection when execAdbCmd");
            return "";
        }

        try {
            AdbStream stream = connection.open("shell:" + cmd);
            logcatCmd(stream.getLocalId() + "@" + "shell:" + cmd);
            streams.add(stream);

            // 当wait为0，每个10ms观察一次stream状况，直到shutdown
            if (wait == 0) {
                while (!stream.isClosed()) {
                    Thread.sleep(10);
                }
            } else {
                // 等待最长wait毫秒后强制退出
                long start = System.currentTimeMillis();
                while (!stream.isClosed() && System.currentTimeMillis() - start < wait) {
                    Thread.sleep(10);
                }

                if (!stream.isClosed()) {
                    stream.close();
                }
            }

            // 获取stream所有输出
            Queue<byte[]> results = stream.getReadQueue();
            StringBuilder sb = new StringBuilder();
            for (byte[] bytes: results) {
                if (bytes != null) {
                    sb.append(new String(bytes));
                }
            }
            streams.remove(stream);
            return sb.toString();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Throw IllegalStateException: " + e.getMessage(), e);

            Log.e(TAG, "illegal", e);

            if (connection != null) {
                connection.setFine(false);
            }
            boolean result = generateConnection();
            if (result) {
                return retryExecAdb(cmd, wait);
            } else {
                Log.e(TAG, "regenerateConnection failed");
                return "";
            }
        } catch (Exception e){
            Log.e(TAG, "Throw Exception: " + e.getMessage()
                    , e);
            return "";
        }
    }

    protected static void logcatCmd(String cmd){
        Log.i("ADB CMD", cmd);
    }

    private static String retryExecAdb(String cmd, long wait) {
        AdbStream stream = null;
        try {
            stream = connection.open("shell:" + cmd);
            logcatCmd(stream.getLocalId() + "@shell:" + cmd);
            streams.add(stream);

            // 当wait为0，每个10ms观察一次stream状况，直到shutdown
            if (wait == 0) {
                while (!stream.isClosed()) {
                    Thread.sleep(10);
                }
            } else {
                // 等待wait毫秒后强制退出
                long start = System.currentTimeMillis();
                while (!stream.isClosed() && System.currentTimeMillis() - start < wait) {
                    Thread.sleep(10);
                }
                if (!stream.isClosed()) {
                    stream.close();
                }
            }

            // 获取stream所有输出
            Queue<byte[]> results = stream.getReadQueue();
            StringBuilder sb = new StringBuilder();
            for (byte[] bytes: results) {
                if (bytes != null) {
                    sb.append(new String(bytes));
                }
            }
            streams.remove(stream);
            return sb.toString();
        } catch (IOException e) {
            Log.e(TAG, "抛出异常 " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Log.e(TAG, "抛出异常 " + e.getMessage(), e);
        }

        return "";
    }


}
