package com.fr.plugin.chart.gauge;

import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.Plot;
import com.fr.design.mainframe.chart.gui.type.ChartImagePane;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.type.AbstractVanChartTypePane;

/**
 * Created by Mitisky on 15/11/27.
 */
public class VanChartGaugePlotPane extends AbstractVanChartTypePane {
    public static final String TITLE = Inter.getLocText("Plugin-ChartF_NewGauge");

    private static final long serialVersionUID = -4599483879031804911L;

    @Override
    protected String[] getTypeIconPath() {
        return new String[]{"/com/fr/plugin/chart/gauge/images/pointer.png",
                "/com/fr/plugin/chart/gauge/images/pointer_180.png",
                "/com/fr/plugin/chart/gauge/images/ring.png",
                "/com/fr/plugin/chart/gauge/images/slot.png",
                "/com/fr/plugin/chart/gauge/images/cuvette.png",
        };
    }

    @Override
    protected String[] getTypeTipName() {
        return new String[]{Inter.getLocText("Plugin-ChartF_Gauge_Pointer"),
                Inter.getLocText("Plugin-ChartF_Gauge_Pointer180"),
                Inter.getLocText("Plugin-ChartF_Gauge_Ring"),
                Inter.getLocText("Plugin-ChartF_Gauge_Slot"),
                Inter.getLocText("Plugin-ChartF_Gauge_Cuvette")
        };
    }

    /**
     * 返回界面标题
     * @return 界面标题
     */
    public String title4PopupWindow() {
        return Inter.getLocText("Plugin-ChartF_NewGauge");
    }

    /**
     * 更新界面内容
     */
    public void populateBean(Chart chart) {
        for(ChartImagePane imagePane : typeDemo) {
            imagePane.isPressing = false;
        }
        Plot plot = chart.getPlot();
        if(plot instanceof VanChartGaugePlot) {
            lastTypeIndex = ((VanChartGaugePlot)plot).getGaugeStyle().ordinal();
            typeDemo.get(lastTypeIndex).isPressing = true;
        }
        checkDemosBackground();
    }

    protected Plot getSelectedClonedPlot(){
        VanChartGaugePlot newPlot = null;
        Chart[] GaugeChart = GaugeIndependentVanChart.GaugeVanChartTypes;
        for(int i = 0, len = GaugeChart.length; i < len; i++){
            if(typeDemo.get(i).isPressing){
                newPlot = (VanChartGaugePlot)GaugeChart[i].getPlot();
            }
        }

        Plot cloned = null;
        try {
            cloned = (Plot)newPlot.clone();
        } catch (CloneNotSupportedException e) {
            FRLogger.getLogger().error("Error In GaugeChart");
        }
        return cloned;
    }

    /**
     * 保存界面属性
     */
    public void updateBean(Chart chart) {
        boolean oldISMulti = chart.getPlot() instanceof VanChartGaugePlot && ((VanChartGaugePlot)chart.getPlot()).isMultiPointer();
        super.updateBean(chart);
        boolean newISMulti = chart.getPlot() instanceof VanChartGaugePlot && ((VanChartGaugePlot)chart.getPlot()).isMultiPointer();
        if(oldISMulti != newISMulti){
            chart.setFilterDefinition(null);
        }
    }

    /**
     * 获取各图表类型界面ID, 本质是plotID
     *
     * @return 图表类型界面ID
     */
    @Override
    protected String getPlotTypeID() {
        return VanChartGaugePlot.VAN_CHART_GAUGE_PLOT;
    }

    protected void cloneOldConditionCollection(Plot oldPlot, Plot newPlot) throws CloneNotSupportedException{
    }

    public Chart getDefaultChart() {
        return GaugeIndependentVanChart.GaugeVanChartTypes[0];
    }

    @Override
    protected void cloneHotHyperLink(Plot oldPlot, Plot newPlot) throws CloneNotSupportedException {
        if(oldPlot instanceof VanChartGaugePlot && newPlot instanceof VanChartGaugePlot){
            if(((VanChartGaugePlot) oldPlot).isMultiPointer() == ((VanChartGaugePlot) newPlot).isMultiPointer()){
                super.cloneHotHyperLink(oldPlot, newPlot);
            }
        }
    }
}