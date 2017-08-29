package com.fr.plugin.chart.area;

import com.fr.chart.chartattr.Plot;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.ChartStylePane;
import com.fr.plugin.chart.column.VanChartCustomStackAndAxisConditionPane;
import com.fr.plugin.chart.line.VanChartLineSeriesPane;

import javax.swing.*;
import java.awt.*;

public class VanChartAreaSeriesPane extends VanChartLineSeriesPane{

    private static final long serialVersionUID = 5497989595104913025L;

    public VanChartAreaSeriesPane(ChartStylePane parent, Plot plot){
        super(parent, plot);
    }

    protected JPanel getContentInPlotType(){

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] row = {p,p,p,p,p,p,p};
        double[] col = {f};

        Component[][] components = new Component[][]{
                new Component[]{getColorPane()},
                new Component[]{createLineTypePane()},
                new Component[]{createMarkerPane()},
                new Component[]{createAreaFillColorPane()},
                new Component[]{createStackedAndAxisPane()},
                new Component[]{createLargeDataModelPane()},
                new Component[]{createTrendLinePane()},
        };

        contentPane = TableLayoutHelper.createTableLayoutPane(components, row, col);
        return contentPane;
    }

    //设置色彩面板内容
    protected void setColorPaneContent (JPanel panel) {
        panel.add(getFillStylePane(), BorderLayout.NORTH);
        panel.add(createStylePane(), BorderLayout.CENTER);
    }

    protected Class<? extends BasicBeanPane> getStackAndAxisPaneClass() {
        return VanChartCustomStackAndAxisConditionPane.class;
    }
}