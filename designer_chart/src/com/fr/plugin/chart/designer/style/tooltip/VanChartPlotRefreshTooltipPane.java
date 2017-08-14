package com.fr.plugin.chart.designer.style.tooltip;

import com.fr.chart.chartattr.Plot;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.AttrTooltip;
import com.fr.plugin.chart.designer.PlotFactory;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.VanChartTooltipContentPane;
import com.fr.plugin.chart.designer.component.background.VanChartBackgroundWithOutImagePane;
import com.fr.plugin.chart.designer.component.border.VanChartBorderWithRadiusPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by mengao on 2017/6/5.
 */
public class VanChartPlotRefreshTooltipPane extends VanChartPlotTooltipNoCheckPane {

    private static final int P_W = 300;
    private static final int P_H = 400;

    private VanChartTooltipContentPane refreshTooltipContentPane;
    private UISpinner duration;

    public VanChartPlotRefreshTooltipPane(Plot plot) {
        super(plot, null);
        this.setSize(270,400);
    }

    protected JPanel createTooltipPane(Plot plot) {
        borderPane = new VanChartBorderWithRadiusPane();
        backgroundPane = new VanChartBackgroundWithOutImagePane();

        refreshTooltipContentPane = getTooltipContentPane(plot);

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p,p,p,p,p,p,p,p,p,p,p};

        Component[][] components = createComponents(plot);
        JPanel panel = TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
        JScrollPane jScrollPane = new JScrollPane(panel);
        jScrollPane.setPreferredSize(new Dimension(P_W, P_H));

        jScrollPane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        JPanel jPanel = new JPanel();
        jPanel.add(jScrollPane);
        return jPanel;
    }

    protected Component[][] createComponents(Plot plot) {
        Component[][] components = new Component[][]{
                new Component[]{refreshTooltipContentPane,null},
                new Component[]{initDurationPane(),null},
                new Component[]{createLabelStylePane(),null},
                new Component[]{TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Border"),borderPane),null},
                new Component[]{TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Background"), backgroundPane),null},

        };
        return components;
    }

    protected VanChartTooltipContentPane getTooltipContentPane(Plot plot){
         return PlotFactory.createPlotRefreshTooltipContentPane(plot, parent, VanChartPlotRefreshTooltipPane.this);
    }

    protected JPanel initDurationPane (){

        duration= new UISpinner(0, Integer.MAX_VALUE, 1, 0);

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p};

        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Duration_Time")), duration},
        };

        JPanel temp = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
        return TableLayout4VanChartHelper.createTableLayoutPaneWithTitle(Inter.getLocText("Plugin-ChartF_Duration_Time"), temp);

    }

    public void populate(AttrTooltip attr) {
        super.populate(attr);
        refreshTooltipContentPane.populateBean(attr.getContent());
        duration.setValue(attr.getDuration());

    }

    public AttrTooltip update() {
        AttrTooltip attrTooltip = super.update();
        attrTooltip.setContent(refreshTooltipContentPane.updateBean());
        attrTooltip.setDuration((int) duration.getValue());
        return attrTooltip;
    }



}
