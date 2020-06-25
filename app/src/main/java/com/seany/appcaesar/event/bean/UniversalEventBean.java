package com.seany.appcaesar.event.bean;

import androidx.annotation.NonNull;
import android.util.ArrayMap;

import com.seany.appcaesar.common.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UniversalEventBean {
    /**
     * 事件发生时间
     */
    private long eventTime;

    /**
     * 时间单位
     */
    private TimeUnit eventTimeUnit;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 额外参数
     */
    private Map<String, Object> extras = new ArrayMap<>(4);

    /**
     * 设置时间
     * @param time
     * @param timeUnit
     */
    public void setTime(long time, TimeUnit timeUnit) {
        this.eventTime = time;
        this.eventTimeUnit = timeUnit;
    }

    /**
     * 设置毫秒时间
     * @param time
     */
    public void setTime(long time) {
        setTime(time, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取毫秒制时间
     * @return
     */
    public long getTime() {
        return getTime(TimeUnit.MILLISECONDS);
    }

    /**
     * 获取特定格式时间
     * @param timeUnit
     * @return
     */
    public long getTime(TimeUnit timeUnit) {
        if (timeUnit == eventTimeUnit) {
            return eventTime;
        }

        return timeUnit.convert(eventTime, eventTimeUnit);
    }

    /**
     * 设置参数
     * @param key
     * @param value
     */
    public void setParam(String key, Object value) {
        extras.put(key, value);
    }

    /**
     * 获取参数
     * @param key
     */
    public <T> T getParam(String key) {
        return (T) extras.get(key);
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    public TimeUnit getEventTimeUnit() {
        return eventTimeUnit;
    }

    public void setEventTimeUnit(TimeUnit eventTimeUnit) {
        this.eventTimeUnit = eventTimeUnit;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void setExtras(Map<String, Object> extras) {
        this.extras = extras;
    }

    @NonNull
    @Override
    public String toString() {
        return "UniversalEventBean{" +
                "eventTime=" + getTime() +
                ", eventType='" + eventType + '\'' +
                ", extras=" + extras  +
                '}';
    }

}
