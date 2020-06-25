package com.seany.appcaesar.event.bean;

import android.util.ArrayMap;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class UniversalNodeBean {
    /**
     * NodeInfo
     */
    private AccessibilityNodeInfo nodeInfo;

    /**
     * 权重
     */
    private Integer weight;



    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setNodeInfo(AccessibilityNodeInfo nodeInfo) {
        this.nodeInfo = nodeInfo;
    }

    public AccessibilityNodeInfo getNodeInfo() {
        return nodeInfo;
    }

    @NonNull
    @Override
    public String toString() {
        return "UniversalNodeBean{" +
                "NodeInfo=" + getNodeInfo() +
                ", Weight='" + getWeight() + '\'' +
                '}';
    }

}
