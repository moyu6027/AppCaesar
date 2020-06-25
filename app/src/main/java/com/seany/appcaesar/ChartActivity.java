package com.seany.appcaesar;

import android.app.Activity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.seany.appcaesar.charts.Chart;
import com.seany.appcaesar.charts.ChartsAdapter;

import java.util.List;


public class ChartActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        List<Chart> chartList = Chart.createChartList(getResources());
        final ChartsAdapter adapter = new ChartsAdapter(this, chartList);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }



}
