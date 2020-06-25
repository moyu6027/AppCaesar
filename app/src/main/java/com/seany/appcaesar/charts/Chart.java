package com.seany.appcaesar.charts;

import android.content.res.Resources;

import com.seany.appcaesar.R;

import java.util.ArrayList;

public class Chart {
    private String name;
    private Class activityClass;

    private Chart(String name, Class activityClass) {
        this.name = name;
        this.activityClass = activityClass;
    }

    public String getName() {
        return name;
    }

    Class getActivityClass() {
        return activityClass;
    }

    public static ArrayList<Chart> createChartList(Resources resources) {
        ArrayList<Chart> chartList = new ArrayList<>();
        chartList.add(new Chart(resources.getString(R.string.cpu_chart), CpuActivity.class));
        chartList.add(new Chart(resources.getString(R.string.mem_chart), MemActivity.class));
        chartList.add(new Chart(resources.getString(R.string.response_chart), ResponseActivity.class));
        return chartList;
    }
}
