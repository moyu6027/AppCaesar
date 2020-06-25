package com.seany.appcaesar.AAChartCoreLib.AAOptionsModel;

public class AAStyle {

    public String color;
    public String fontSize;
    public String fontWeight;
    public String textOutLine;

    public AAStyle color(String prop) {
        color = prop;
        return this;
    }

    public AAStyle fontSize(Float prop) {
        fontSize = prop + "px";
        return this;
    }

    public AAStyle fontWeight(String prop) {
        fontWeight = prop;
        return this;
    }

    public AAStyle textOutline(String prop) {
        textOutLine = prop;
        return this;
    }

}
