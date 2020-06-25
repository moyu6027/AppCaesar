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
import com.seany.appcaesar.AAChartCoreLib.AAChartConfiger.AAChartModel;
import com.seany.appcaesar.AAChartCoreLib.AAChartConfiger.AAChartView;
import com.seany.appcaesar.AAChartCoreLib.AAChartConfiger.AASeriesElement;
import com.seany.appcaesar.AAChartCoreLib.AAChartEnum.AAChartType;
import com.seany.appcaesar.R;

import java.util.ArrayList;
import java.util.List;

public class ResponseActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_common_aa);
        AAChartView aaChartView = (AAChartView) findViewById(R.id.AAChartView);
        aaChartView.aa_drawChartWithChartModel(aaChartModel);
    }

    AAChartModel aaChartModel = new AAChartModel()
            .chartType(AAChartType.Line)
            .title("THE HEAT OF PROGRAMMING LANGUAGE")
            .subtitle("Virtual Data")
            .backgroundColor("#4b2b7f")
            .gradientColorEnable(true)
            .categories(new String[]{"Java","Swift","Python","Ruby", "PHP","Go","C","C#","C++"})
            .dataLabelsEnabled(false)
            .yAxisGridLineWidth(0f)
            .series(new AASeriesElement[]{
                    new AASeriesElement()
                            .name("Tokyo")
                            .data(new Object[]{7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6}),
                    new AASeriesElement()
                            .name("NewYork")
                            .data(new Object[]{0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5}),
                    new AASeriesElement()
                            .name("London")
                            .data(new Object[]{0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0}),
                    new AASeriesElement()
                            .name("Berlin")
                            .data(new Object[]{3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8})
            });

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
