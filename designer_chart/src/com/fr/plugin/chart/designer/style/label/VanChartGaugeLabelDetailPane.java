package com.fr.plugin.chart.designer.style.label;

import com.fr.chart.chartattr.Plot;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.style.ChartTextAttrPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.style.VanChartStylePane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mengao on 2017/8/13.
 */
public class VanChartGaugeLabelDetailPane extends VanChartPlotLabelDetailPane {

    public VanChartGaugeLabelDetailPane(Plot plot, VanChartStylePane parent) {
        super(plot, parent);
    }

    protected JPanel createLabelStylePane(double[] row, double[] col, Plot plot) {
        style = new UIButtonGroup<Integer>(new String[]{Inter.getLocText("Plugin-ChartF_Automatic"),
                Inter.getLocText("Plugin-ChartF_Custom")});
        textFontPane = new ChartTextAttrPane();

        initStyleListener();

        return TableLayoutHelper.createTableLayoutPane(getLabelStyleComponents(plot),row,col);
    }

    protected JPanel getLabelPositionPane (Component[][] comps, double[] row, double[] col){
        return TableLayoutHelper.createTableLayoutPane(comps,row,col);
    }

    protected JPanel createTableLayoutPaneWithTitle(String title, JPanel panel) {
        return TableLayout4VanChartHelper.createGapTableLayoutPane(title, panel);
    }
}
