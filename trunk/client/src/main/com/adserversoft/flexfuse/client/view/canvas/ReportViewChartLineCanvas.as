package com.adserversoft.flexfuse.client.view.canvas {
import mx.charts.CategoryAxis;
import mx.charts.Legend;
import mx.charts.LineChart;
import mx.charts.series.LineSeries;

public class ReportViewChartLineCanvas extends BaseCanvas {
    [Bindable]
    public var lineChart:LineChart;
    public var categoryAxis:CategoryAxis;
    public var viewsLS:LineSeries;
    public var clicksLS:LineSeries;


    public function ReportViewChartLineCanvas() {
        super();
    }
}
}