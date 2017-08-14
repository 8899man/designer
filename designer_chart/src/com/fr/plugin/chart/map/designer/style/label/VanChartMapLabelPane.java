package com.fr.plugin.chart.map.designer.style.label;

import com.fr.chart.chartattr.Chart;
import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.ConditionAttr;
import com.fr.design.dialog.BasicScrollPane;
import com.fr.design.mainframe.chart.PaneTitleConstants;
import com.fr.plugin.chart.designer.style.VanChartStylePane;
import com.fr.plugin.chart.designer.style.label.VanChartPlotLabelPane;
import com.fr.plugin.chart.map.VanChartMapPlot;
import com.fr.plugin.chart.map.attr.AttrMapLabel;
import com.fr.plugin.chart.map.designer.VanMapAreaAndPointGroupPane;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mitisky on 16/5/20.
 */
public class VanChartMapLabelPane extends BasicScrollPane<Chart> {
    private VanChartPlotLabelPane areaLabelPane;
    private VanChartPlotLabelPane pointLabelPane;

    private VanChartMapPlot mapPlot;
    protected VanChartStylePane parent;
    private static final long serialVersionUID = -5449293740965811991L;


    public VanChartMapLabelPane(VanChartStylePane parent) {
        super();
        this.parent = parent;
    }

    @Override
    protected JPanel createContentPane() {
        JPanel contentPane = new JPanel(new BorderLayout());
        if (mapPlot == null) {
            return contentPane;
        }
        switch (mapPlot.getAllLayersMapType()){
            case POINT:
                pointLabelPane = new VanChartPlotLabelPane(mapPlot, parent);
                contentPane.add(pointLabelPane, BorderLayout.NORTH);
                break;
            case CUSTOM:
                areaLabelPane = createAreaMapPlotLabelPane();
                pointLabelPane = new VanChartPlotLabelPane(mapPlot, parent);
                contentPane.add(new VanMapAreaAndPointGroupPane(areaLabelPane, pointLabelPane), BorderLayout.NORTH);
                break;
            case DRILL_CUSTOM:
                areaLabelPane = new VanChartPlotLabelPane(mapPlot, parent);
                pointLabelPane = new VanChartPlotLabelPane(mapPlot, parent);
                contentPane.add(new VanMapAreaAndPointGroupPane(areaLabelPane, pointLabelPane), BorderLayout.NORTH);
                break;
            default:
                areaLabelPane = createAreaMapPlotLabelPane();
                contentPane.add(areaLabelPane, BorderLayout.NORTH);
                break;
        }

        return contentPane;
    }

    private VanChartPlotLabelPane createAreaMapPlotLabelPane() {
        return new VanChartPlotLabelPane(mapPlot, parent){
            @Override
            protected boolean checkEnabled4Large() {
                return false;
            }
        };
    }

    @Override
    public void populateBean(Chart chart) {
        Plot plot = chart.getPlot();
        if(plot instanceof VanChartMapPlot){
            mapPlot = (VanChartMapPlot) plot;
        }

        if (areaLabelPane == null && pointLabelPane == null) {
            this.remove(leftcontentPane);
            layoutContentPane();
            parent.initAllListeners();
        }

        AttrMapLabel attrMapLabel = (AttrMapLabel)plot.getConditionCollection().getDefaultAttr().getExisted(AttrMapLabel.class);
        if(attrMapLabel == null){
            attrMapLabel = new AttrMapLabel();
        }
        if(pointLabelPane != null){
            pointLabelPane.populate(attrMapLabel.getPointLabel());
        }
        if (areaLabelPane != null) {
            areaLabelPane.populate(attrMapLabel.getAreaLabel());
        }
    }


    public void updateBean(Chart chart) {
        if (chart == null) {
            return;
        }
        ConditionAttr defaultAttr = chart.getPlot().getConditionCollection().getDefaultAttr();
        AttrMapLabel attrMapLabel = (AttrMapLabel)defaultAttr.getExisted(AttrMapLabel.class);
        if (attrMapLabel != null) {
            defaultAttr.remove(attrMapLabel);
        } else {
            attrMapLabel = new AttrMapLabel();
        }

        if(areaLabelPane != null){
            attrMapLabel.setAreaLabel(areaLabelPane.update());
        }
        if(pointLabelPane != null){
            attrMapLabel.setPointLabel(pointLabelPane.update());
        }

        defaultAttr.addDataSeriesCondition(attrMapLabel);
    }

    @Override
    protected String title4PopupWindow() {
        return PaneTitleConstants.CHART_STYLE_LABEL_TITLE;
    }
}

