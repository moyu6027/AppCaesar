package com.seany.appcaesar.charts;

import android.app.Activity;
import android.os.Bundle;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Anchor;
import com.anychart.graphics.vector.Stroke;
import com.seany.appcaesar.R;

import java.util.ArrayList;
import java.util.List;

public class CpuActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts_common);
        AnyChartView anyChartView = (AnyChartView) findViewById(R.id.any_chart_view);
        anyChartView.setProgressBar(findViewById(R.id.progress_bar));
        Cartesian cartesian = AnyChart.line();

        cartesian.animation(true);

        cartesian.padding(10d, 20d, 5d, 20d);

        cartesian.crosshair().enabled(true);
        cartesian.crosshair()
                .yLabel(true)
                // TODO ystroke
                .yStroke((Stroke) null, null, null, (String) null, (String) null);

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

        cartesian.title("CPU Usage");

        cartesian.yAxis(0).title("Usage(%)");
        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

        List<DataEntry> seriesData = new ArrayList<>();
        seriesData.add(new CustomDataEntry("15:24:00", 36, 23, 28));
        seriesData.add(new CustomDataEntry("15:24:05", 71, 40, 41));
        seriesData.add(new CustomDataEntry("15:24:10", 85, 62, 51));
        seriesData.add(new CustomDataEntry("15:24:15", 92, 90, 65));
        seriesData.add(new CustomDataEntry("15:24:20", 90, 90, 65));
        seriesData.add(new CustomDataEntry("15:24:25", 86, 89, 80));
        seriesData.add(new CustomDataEntry("15:24:30", 84, 80, 80));
        seriesData.add(new CustomDataEntry("15:24:35", 70, 73, 73));
        seriesData.add(new CustomDataEntry("15:24:40", 72, 77, 72));
        seriesData.add(new CustomDataEntry("15:24:45", 60, 60, 64));
        seriesData.add(new CustomDataEntry("15:24:50", 52, 51, 52));
        seriesData.add(new CustomDataEntry("15:24:55", 41, 43, 49));
        seriesData.add(new CustomDataEntry("15:25:00", 33, 32, 32));


        Set set = Set.instantiate();
        set.data(seriesData);
        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
        Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

        Line series1 = cartesian.line(series1Mapping);
        series1.name("Total");
        series1.hovered().markers().enabled(true);
        series1.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series1.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        Line series2 = cartesian.line(series2Mapping);
        series2.name("App");
        series2.hovered().markers().enabled(true);
        series2.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series2.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        Line series3 = cartesian.line(series3Mapping);
        series3.name("idle");
        series3.hovered().markers().enabled(true);
        series3.hovered().markers()
                .type(MarkerType.CIRCLE)
                .size(4d);
        series3.tooltip()
                .position("right")
                .anchor(String.valueOf(Anchor.LEFT_CENTER))
                .offsetX(5d)
                .offsetY(5d);

        cartesian.legend().enabled(true);
        cartesian.legend().fontSize(13d);
        cartesian.legend().padding(0d, 0d, 10d, 0d);

        anyChartView.setChart(cartesian);
    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, Number value, Number value2, Number value3) {
            super(x, value);
            setValue("value2", value2);
            setValue("value3", value3);
        }

    }
}
