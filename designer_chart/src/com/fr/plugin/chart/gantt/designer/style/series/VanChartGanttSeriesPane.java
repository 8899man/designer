package com.fr.plugin.chart.gantt.designer.style.series;

import com.fr.chart.chartattr.Plot;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.icombobox.LineComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.ChartStylePane;
import com.fr.design.mainframe.chart.gui.ColorSelectBoxWithOutTransparent;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.VanChartAttrMarker;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.VanChartMarkerPane;
import com.fr.plugin.chart.designer.style.series.VanChartAbstractPlotSeriesPane;
import com.fr.plugin.chart.gantt.VanChartGanttPlot;
import com.fr.stable.CoreConstants;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hufan on 2017/1/13.
 */
public class VanChartGanttSeriesPane extends VanChartAbstractPlotSeriesPane {
    private UIButtonGroup seriesNewLine;

    private LineComboBox lineWidth;//线型
    private ColorSelectBoxWithOutTransparent colorSelect;//颜色

    public VanChartGanttSeriesPane(ChartStylePane parent, Plot plot) {
        super(parent, plot);
    }

    @Override
    protected JPanel getContentInPlotType() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] row = {p,p,p,p,p};
        double[] col = {f};

        Component[][] components = new Component[][]{
                new Component[]{getColorPane()},
                new Component[]{createGanntStylePane()},
                new Component[]{createLinkLinePane()},
                new Component[]{createMarkerPane()}
        };

        contentPane = TableLayoutHelper.createTableLayoutPane(components, row, col);
        return contentPane;
    }

    //设置色彩面板内容
    protected void setColorPaneContent (JPanel panel) {
        panel.add(getFillStylePane(), BorderLayout.NORTH);
    }

    private JPanel createGanntStylePane(){
        seriesNewLine = new UIButtonGroup(new String[]{Inter.getLocText("Plugin-ChartF_Open"), Inter.getLocText("Plugin-ChartF_Close")});
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(new UILabel(Inter.getLocText("Plugin-ChartF_Series_New_Line")), BorderLayout.WEST);
        panel.add(seriesNewLine, BorderLayout.CENTER);

        JPanel ganntStylePane = TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Style"), panel);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,0,15));
        return ganntStylePane;
    }

    private JPanel createLinkLinePane(){
        lineWidth = new LineComboBox(CoreConstants.STRIKE_LINE_STYLE_ARRAY_4_CHART);
        colorSelect = new ColorSelectBoxWithOutTransparent(100);

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] row = {p,p,p};
        double[] col = {p,f};

        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_LineStyle")+":"), lineWidth},
                new Component[]{new UILabel(Inter.getLocText("FR-Chart-Color_Color")+":"), colorSelect}
        };

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components, row, col);
        JPanel linkLinePane = TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Link_Line"), panel);
        panel.setBorder(BorderFactory.createEmptyBorder(10,10,0,15));
        return linkLinePane;
    }

    //标记点类型
    protected JPanel createMarkerPane() {
        markerPane = new VanChartMarkerPane(){
            @Override
            protected BasicBeanPane<VanChartAttrMarker> createCommonMarkerPane() {
                return new VanChartGanttCommonMarkerPane();
            }

            @Override
            protected BasicBeanPane<VanChartAttrMarker> createImageMarkerPane() {
                return new VanChartImageMarkerWithoutWidthAndHeightPane();
            }
        };
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Gannt_Marker"), markerPane);
    }

    @Override
    public void populateBean(Plot plot) {
        super.populateBean(plot);

        if(plot instanceof VanChartGanttPlot){
            VanChartGanttPlot ganttPlot = (VanChartGanttPlot)plot;

            seriesNewLine.setSelectedIndex(ganttPlot.isSeriesNewLineEnable() ? 0 : 1);

            lineWidth.setSelectedLineStyle(ganttPlot.getLineWidth());
            colorSelect.setSelectObject(ganttPlot.getLineColor());

        }
    }

    @Override
    public void updateBean(Plot plot) {
        super.updateBean(plot);
        if(plot instanceof VanChartGanttPlot){
            VanChartGanttPlot ganttPlot = (VanChartGanttPlot)plot;

            ganttPlot.setSeriesNewLineEnable(seriesNewLine.getSelectedIndex() == 0);
            ganttPlot.setLineWidth(lineWidth.getSelectedLineStyle());
            ganttPlot.setLineColor(colorSelect.getSelectObject());

        }
    }
}
