package com.fr.plugin.chart.designer.style.tooltip;

import com.fr.chart.chartattr.Plot;
import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.style.ChartTextAttrPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.AttrTooltip;
import com.fr.plugin.chart.designer.PlotFactory;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.VanChartTooltipContentPane;
import com.fr.plugin.chart.designer.component.background.VanChartBackgroundWithOutImagePane;
import com.fr.plugin.chart.designer.component.border.VanChartBorderWithRadiusPane;
import com.fr.plugin.chart.designer.style.VanChartStylePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class VanChartPlotTooltipPane extends BasicPane {
    private static final long serialVersionUID = 6087381131907589370L;

    protected UICheckBox isTooltipShow;

    protected VanChartTooltipContentPane tooltipContentPane;

    protected UIButtonGroup<Integer> style;
    protected ChartTextAttrPane textFontPane;

    protected VanChartBorderWithRadiusPane borderPane;

    protected VanChartBackgroundWithOutImagePane backgroundPane;

    protected UICheckBox showAllSeries;
    protected UIButtonGroup followMouse;

    protected VanChartStylePane parent;

    protected JPanel tooltipPane;

    public VanChartPlotTooltipPane(Plot plot, VanChartStylePane parent) {
        this.parent = parent;
        addComponents(plot);
    }

    protected  void addComponents(Plot plot) {
        isTooltipShow = new UICheckBox(Inter.getLocText("Plugin-ChartF_UseTooltip"));
        tooltipPane = createTooltipPane(plot);

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {f};
        double[] rowSize = {p,p};
        Component[][] components = new Component[][]{
                new Component[]{isTooltipShow},
                new Component[]{tooltipPane}
        };

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
        this.setLayout(new BorderLayout());
        this.add(panel,BorderLayout.CENTER);

        isTooltipShow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                checkBoxUse();
            }
        });
    }

    protected JPanel createTooltipPane(Plot plot) {
        borderPane = new VanChartBorderWithRadiusPane();
        backgroundPane = new VanChartBackgroundWithOutImagePane();

        initTooltipContentPane(plot);

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p,p,p,p,p,p,p,p,p};

        Component[][] components = createComponents(plot);

        return TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
    }

    protected Component[][] createComponents(Plot plot) {
        Component[][] components = new Component[][]{
                new Component[]{tooltipContentPane,null},
                new Component[]{createLabelStylePane(),null},
                new Component[]{TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Border"),borderPane),null},
                new Component[]{TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Background"), backgroundPane),null},
                new Component[]{createDisplayStrategy(plot),null},
        };
        return components;
    }


    protected void initTooltipContentPane(Plot plot){
        tooltipContentPane = PlotFactory.createPlotTooltipContentPane(plot, parent, VanChartPlotTooltipPane.this);
    }

    protected JPanel createLabelStylePane() {
        style = new UIButtonGroup<Integer>(new String[]{Inter.getLocText("Plugin-ChartF_Automatic"),Inter.getLocText("Plugin-ChartF_Custom")});
        textFontPane = new ChartTextAttrPane() {
            protected Component[][] getComponents(JPanel buttonPane) {
                return new Component[][]{
                        new Component[]{null, null},
                        new Component[]{null, fontNameComboBox},
                        new Component[]{null, buttonPane}
                };
            }
        };
        UILabel text = new UILabel(Inter.getLocText("Plugin-Chart_Character"), SwingConstants.LEFT);
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p, p, p};
        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{text, style},
                new Component[]{null, textFontPane},
        };

        initStyleListener();

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("FR-Designer-Widget_Style"), panel);
    }


    private void initStyleListener() {
        style.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkStyleUse();
            }
        });
    }

    protected JPanel createDisplayStrategy(Plot plot) {
        showAllSeries = new UICheckBox(getShowAllSeriesLabelText());
        followMouse = new UIButtonGroup(new String[]{Inter.getLocText("Plugin-ChartF_FollowMouse"),
                Inter.getLocText("Plugin-ChartF_NotFollowMouse")});
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = { p,f };
        double[] rowSize = { p,p,p};
        Component[][] components = new Component[3][2];
        components[0] = new Component[]{null,null};
        components[1] = new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Prompt_Box")),followMouse};

        if(plot.isSupportTooltipSeriesType() && hasTooltipSeriesType()){
            components[2] = new Component[]{showAllSeries,null};
        }
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components,rowSize,columnSize);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_DisplayStrategy"), panel);
    }

    protected String getShowAllSeriesLabelText() {
        return Inter.getLocText("Plugin-ChartF_ShowAllSeries");
    };

    protected boolean hasTooltipSeriesType() {
        return true;
    }

    @Override
    protected String title4PopupWindow() {
        return null;
    }

    private void checkAllUse() {
        checkBoxUse();
        checkStyleUse();
    }
    /**
     * 检查box使用.
     */
    private void checkBoxUse() {
        tooltipPane.setVisible(isTooltipShow.isSelected());
    }

    private void checkStyleUse() {
        textFontPane.setEnabled(style.getSelectedIndex() == 1);
    }

    protected AttrTooltip getAttrTooltip() {
        return new AttrTooltip();
    }

    public void populate(AttrTooltip attr) {
        if(attr == null) {
            attr = getAttrTooltip();
        }

        isTooltipShow.setSelected(attr.isEnable());
        if (tooltipContentPane != null) {
            tooltipContentPane.populateBean(attr.getContent());
        }

        style.setSelectedIndex(attr.isCustom() ? 1 : 0);
        textFontPane.populate(attr.getTextAttr());
        borderPane.populate(attr.getGeneralInfo());
        backgroundPane.populate(attr.getGeneralInfo());
        if(showAllSeries != null) {
            showAllSeries.setSelected(attr.isShowMutiSeries());
        }
        if(followMouse != null) {
            followMouse.setSelectedIndex(attr.isFollowMouse() ? 0 : 1);
        }

        checkAllUse();
    }

    public AttrTooltip update() {
        AttrTooltip attrTooltip = getAttrTooltip();

        attrTooltip.setEnable(isTooltipShow.isSelected());
        if (tooltipContentPane != null) {
            attrTooltip.setContent(tooltipContentPane.updateBean());
        }

        attrTooltip.setCustom(style.getSelectedIndex() == 1);
        if(textFontPane != null){
            attrTooltip.setTextAttr(textFontPane.update());
        }
        borderPane.update(attrTooltip.getGeneralInfo());
        backgroundPane.update(attrTooltip.getGeneralInfo());
        if(showAllSeries != null) {
            attrTooltip.setShowMutiSeries(showAllSeries.isSelected());
        }
        if(followMouse != null) {
            attrTooltip.setFollowMouse(followMouse.getSelectedIndex() == 0);
        }

        return attrTooltip;
    }
}