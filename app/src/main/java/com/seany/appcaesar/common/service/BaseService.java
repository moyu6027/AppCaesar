package com.seany.appcaesar.common.service;

import android.content.Context;

public interface BaseService {
    void onCreate(Context context);

    void onDestroy(Context context);
}
