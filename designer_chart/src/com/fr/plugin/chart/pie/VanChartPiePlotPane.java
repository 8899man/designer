package com.fr.plugin.chart.pie;


import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.Plot;
import com.fr.design.mainframe.chart.gui.type.ChartImagePane;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.plugin.chart.PiePlot4VanChart;
import com.fr.plugin.chart.designer.type.AbstractVanChartTypePane;

/**
 * 饼图(新特性) 属性表 选择类型 布局界面.
 */
public class VanChartPiePlotPane extends AbstractVanChartTypePane {
    public static final String TITLE = Inter.getLocText("Plugin-ChartF_NewPie");

    private static final long serialVersionUID = 6163246902689597259L;

    @Override
    protected String[] getTypeIconPath() {
        return new String[]{"/com/fr/plugin/chart/pie/images/pie.png",
                "/com/fr/plugin/chart/pie/images/same.png",
                "/com/fr/plugin/chart/pie/images/different.png"
        };
    }

    @Override
    protected String[] getTypeTipName() {
        return new String[]{
                Inter.getLocText("I-PieStyle_Normal"),
                Inter.getLocText("Plugin-ChartF_SameArcPie"),
                Inter.getLocText("Plugin-ChartF_DifferentArcPie")
        };
    }

    /**
     * 返回界面标题
     * @return 界面标题
     */
    public String title4PopupWindow() {
        return Inter.getLocText("Plugin-ChartF_NewPie");
    }


    /**
     * 更新界面内容
     */
    public void populateBean(Chart chart) {
        for(ChartImagePane imagePane : typeDemo) {
            imagePane.isPressing = false;
        }
        Plot plot = chart.getPlot();
        if(plot instanceof PiePlot4VanChart) {
            lastTypeIndex = ((PiePlot4VanChart)plot).getRoseType().ordinal();
            typeDemo.get(lastTypeIndex).isPressing = true;
        }
        checkDemosBackground();
    }

    /**
     * 获取各图表类型界面ID, 本质是plotID
     *
     * @return 图表类型界面ID
     */
    @Override
    protected String getPlotTypeID() {
        return PiePlot4VanChart.VAN_CHART_PIE_PLOT;
    }

    protected Plot getSelectedClonedPlot(){
        PiePlot4VanChart newPlot = null;
        Chart[] pieChart = PieIndependentVanChart.newPieChartTypes;
        for(int i = 0, len = pieChart.length; i < len; i++){
            if(typeDemo.get(i).isPressing){
                newPlot = (PiePlot4VanChart)pieChart[i].getPlot();
            }
        }

        Plot cloned = null;
        try {
            cloned = (Plot)newPlot.clone();
        } catch (CloneNotSupportedException e) {
            FRLogger.getLogger().error("Error In PieChart");
        }
        return cloned;
    }

    public Chart getDefaultChart() {
        return PieIndependentVanChart.newPieChartTypes[0];
    }
}