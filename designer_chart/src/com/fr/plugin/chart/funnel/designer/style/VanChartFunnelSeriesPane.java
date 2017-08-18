package com.fr.plugin.chart.funnel.designer.style;

import com.fr.chart.chartattr.Plot;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.ChartStylePane;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.style.series.VanChartAbstractPlotSeriesPane;
import com.fr.plugin.chart.funnel.VanChartFunnelPlot;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mitisky on 16/10/10.
 */
public class VanChartFunnelSeriesPane extends VanChartAbstractPlotSeriesPane {
    private UIButtonGroup useSameSlantAngle;
    private UIButtonGroup sort;
    public VanChartFunnelSeriesPane(ChartStylePane parent, Plot plot) {
        super(parent, plot);
    }

    protected JPanel getContentInPlotType() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {f};
        double[] rowSize = {p,p,p,p,p,p,p,p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{getColorPane()},
                new Component[]{createFunnelStylePane()},
                new Component[]{createBorderPane()},
        };

        contentPane = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);

        return contentPane;
    }

    //设置色彩面板内容
    protected void setColorPaneContent (JPanel panel) {
        panel.add(getFillStylePane(), BorderLayout.NORTH);
        panel.add(createStylePane(), BorderLayout.CENTER);
        panel.add(createAlphaPane(), BorderLayout.SOUTH);
    }

    private JPanel createFunnelStylePane() {
        useSameSlantAngle = new UIButtonGroup(new String[]{Inter.getLocText("Plugin-ChartF_UseSameSlantAngle"), Inter.getLocText("Plugin-ChartF_UseDiffSlantAngle")});
        sort = new UIButtonGroup(new String[]{Inter.getLocText("Plugin-ChartF_AutoSort"), Inter.getLocText("Plugin-ChartF_Origin")});

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Continuity")), useSameSlantAngle},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Sort")), sort},
        };

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);

        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Chart-Style_Name"), panel);
    }

    public void populateBean(Plot plot) {
        if(plot == null) {
            return;
        }

        super.populateBean(plot);

        if(plot instanceof VanChartFunnelPlot){
            useSameSlantAngle.setSelectedIndex(((VanChartFunnelPlot) plot).isUseSameSlantAngle() ? 0 : 1);
            sort.setSelectedIndex(((VanChartFunnelPlot) plot).isSort() ? 0 : 1);
        }

    }



    public void updateBean(Plot plot) {
        if(plot == null) {
            return;
        }

        super.updateBean(plot);

        if(plot instanceof VanChartFunnelPlot){
            ((VanChartFunnelPlot) plot).setUseSameSlantAngle(useSameSlantAngle.getSelectedIndex() == 0);
            ((VanChartFunnelPlot) plot).setSort(sort.getSelectedIndex() == 0);
        }
    }
}
