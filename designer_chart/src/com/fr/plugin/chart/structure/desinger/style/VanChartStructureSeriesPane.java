package com.fr.plugin.chart.structure.desinger.style;

import com.fr.chart.chartattr.Plot;
import com.fr.chart.chartglyph.ConditionAttr;
import com.fr.design.gui.frpane.UINumberDragPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.ChartStylePane;
import com.fr.design.style.color.ColorSelectBox;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.AttrNode;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.style.series.VanChartAbstractPlotSeriesPane;
import com.fr.plugin.chart.structure.VanChartStructurePlot;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by shine on 2017/2/15.
 */
public class VanChartStructureSeriesPane extends VanChartAbstractPlotSeriesPane {
    private ColorSelectBox linkColor;
    private UIButtonGroup<Integer> linkWidthType;
    private UISpinner linkWidth;
    private UINumberDragPane linkOpacity;
    private JPanel linkWidthPane;

    private StructureNodeStylePane nodeStylePane;

    public VanChartStructureSeriesPane(ChartStylePane parent, Plot plot) {
        super(parent, plot);
    }

    @Override
    protected JPanel getContentInPlotType() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {f};
        double[] rowSize = {p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{createLinkPane()},
                new Component[]{createNodePane()}
        };

        contentPane = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);

        return contentPane;
    }

    private JPanel createLinkPane() {
        linkColor = new ColorSelectBox(100);

        linkWidthType = new UIButtonGroup<Integer>(new String[]{Inter.getLocText("Plugin-ChartF_Automatic"),
                Inter.getLocText("Plugin-ChartF_Custom")});
        linkWidth = new UISpinner(0,Double.MAX_VALUE,0.5,0);
        linkOpacity = new UINumberDragPane(0,100);

        linkWidthType.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                checkWidth();
            }
        });

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double e = TableLayout4VanChartHelper.EDIT_AREA_WIDTH;
        double[] columnSize = {f, e};
        double[] rowSize = {p,p,p,p};

        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(TableLayout4VanChartHelper.createGapTableLayoutPane(Inter.getLocText("FR-Designer-Tree_Width")+"   ", linkWidthType), BorderLayout.NORTH);
        linkWidthPane = TableLayout4VanChartHelper.createGapTableLayoutPane("         ", linkWidth);
        jPanel.add(linkWidthPane, BorderLayout.CENTER);

        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Chart-Color_Color")), linkColor},
                new Component[]{jPanel, null},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Alpha")), linkOpacity}
        };

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);

        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Link"), panel);
    }

    private JPanel createNodePane() {
        nodeStylePane = new StructureNodeStylePane();
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Node"), nodeStylePane);
    }

    private void checkWidth() {
        linkWidthPane.setVisible(linkWidthType.getSelectedIndex() == 1);
    }

    @Override
    public void populateBean(Plot plot) {
        super.populateBean(plot);
        if(plot instanceof VanChartStructurePlot){
            VanChartStructurePlot structurePlot = (VanChartStructurePlot)plot;
            linkColor.setSelectObject(structurePlot.getLinkColor());
            linkWidthType.setSelectedIndex(structurePlot.isAutoLinkWidth() ? 0 : 1);
            linkWidth.setValue(structurePlot.getLinkWidth());
            linkOpacity.populateBean(structurePlot.getLinkOpacity());
        }
        checkWidth();
    }

    @Override
    public void updateBean(Plot plot) {
        super.updateBean(plot);
        if(plot instanceof VanChartStructurePlot){
            VanChartStructurePlot structurePlot = (VanChartStructurePlot)plot;
            structurePlot.setLinkColor(linkColor.getSelectObject());
            structurePlot.setAutoLinkWidth(linkWidthType.getSelectedIndex() == 0);
            structurePlot.setLinkWidth(linkWidth.getValue());
            structurePlot.setLinkOpacity(linkOpacity.updateBean());
        }
    }

    protected void populateCondition(ConditionAttr defaultAttr){
        if(nodeStylePane != null){
            AttrNode attrNode = defaultAttr.getExisted(AttrNode.class);
            nodeStylePane.populateBean(attrNode);
        }
    }

    protected void updateCondition(ConditionAttr defaultAttr){
        if(nodeStylePane != null){
            AttrNode attrNode =defaultAttr.getExisted(AttrNode.class);
            defaultAttr.remove(attrNode);
            defaultAttr.addDataSeriesCondition(nodeStylePane.updateBean());
        }
    }
}
