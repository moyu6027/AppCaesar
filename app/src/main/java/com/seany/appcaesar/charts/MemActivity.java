package com.seany.appcaesar.charts;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.seany.appcaesar.R;

import java.util.ArrayList;
import java.util.List;

public class MemActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_common);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        APIlib.getInstance().setActiveAnyChartView(anyChartView);

        Pie pie = AnyChart.pie();
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("PSS", 10000));
        data.add(new ValueDataEntry("VSS", 12000));
        data.add(new ValueDataEntry("FREE", 18000));
        pie.data(data);

        anyChartView.setChart(pie);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
