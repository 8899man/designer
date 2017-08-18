package com.fr.plugin.chart.designer.component;

import com.fr.chart.base.AttrColor;
import com.fr.chart.base.AttrLineStyle;
import com.fr.chart.base.LineStyleInfo;
import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.icombobox.LineComboBox;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.style.color.ColorSelectBox;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.TrendLineType;
import com.fr.plugin.chart.base.VanChartAttrTrendLine;
import com.fr.plugin.chart.base.VanChartConstants;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mitisky on 15/10/19.
 */
public class VanChartTrendLinePane extends BasicPane{
    private static final TrendLineType[] TYPES = new TrendLineType[] {TrendLineType.EXP, TrendLineType.LINE, TrendLineType.LOG, TrendLineType.POLY};

    private UITextField trendLineName;
    private ColorSelectBox trendLineColor;
    private LineComboBox trendLineStyle;//线型

    private UIComboBox trendLineType;//趋势线函数类型
    private UISpinner prePeriod;
    private UISpinner afterPeriod;

    public VanChartTrendLinePane() {
        this.setLayout(new BorderLayout());
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] row = {p,p,p,p,p,p};
        double[] col = {p,f};
        trendLineName = new UITextField();
        trendLineColor = new ColorSelectBox(100);
        trendLineStyle = new LineComboBox(VanChartConstants.ALERT_LINE_STYLE);

        trendLineType = new UIComboBox(TYPES);
        prePeriod = new UISpinner(0,Integer.MAX_VALUE,1,0);
        afterPeriod = new UISpinner(0,Integer.MAX_VALUE,1,0);
        double[] r = {p,p};
        double[] c = {p,f,p};
        Component[][] periodComps = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Chart_TrendLine_Forward")), prePeriod, new UILabel(Inter.getLocText("Plugin-ChartF_Cycle"))},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_After_Period")), afterPeriod, new UILabel(Inter.getLocText("Plugin-ChartF_Cycle"))},
        };
        JPanel periodPane = TableLayoutHelper.createTableLayoutPane(periodComps, r, c);


        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Name")), trendLineName},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_LineStyle")), trendLineStyle},
                new Component[]{new UILabel(Inter.getLocText("FR-Chart-Color_Color")), trendLineColor},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_type")), trendLineType},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Period")), periodPane}
        };

        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, col);
        this.add(panel, BorderLayout.CENTER);
    }
    protected String title4PopupWindow(){
        return Inter.getLocText("Chart-Trend_Line");
    }

    public void populate(VanChartAttrTrendLine trendLine) {
        if(trendLine != null){
            trendLineName.setText(trendLine.getTrendLineName());
            LineStyleInfo lineStyleInfo = trendLine.getLineStyleInfo();
            trendLineColor.setSelectObject(lineStyleInfo.getAttrLineColor().getSeriesColor());
            trendLineStyle.setSelectedLineStyle(lineStyleInfo.getAttrLineStyle().getLineStyle());
            trendLineType.setSelectedItem(trendLine.getTrendLineType());
            prePeriod.setValue(trendLine.getPrePeriod());
            afterPeriod.setValue(trendLine.getAfterPeriod());
        }
    }

    public VanChartAttrTrendLine update() {
        VanChartAttrTrendLine  trendLine = new VanChartAttrTrendLine();
        trendLine.setTrendLineName(trendLineName.getText());

        LineStyleInfo lineStyleInfo = trendLine.getLineStyleInfo();
        lineStyleInfo.setAttrLineStyle(new AttrLineStyle(trendLineStyle.getSelectedLineStyle()));
        lineStyleInfo.setAttrLineColor(new AttrColor(trendLineColor.getSelectObject()));

        trendLine.setTrendLineType((TrendLineType) trendLineType.getSelectedItem());
        trendLine.setPrePeriod((int) prePeriod.getValue());
        trendLine.setAfterPeriod((int) afterPeriod.getValue());

        return trendLine;
    }

}