package com.fr.plugin.chart.designer.component;

import com.fr.chart.base.ChartConstants;
import com.fr.design.beans.BasicBeanPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mitisky on 15/9/8.
 */
//系列-风格
public class VanChartBeautyPane extends BasicBeanPane<Integer> {
    private UIButtonGroup styleBox;

    public VanChartBeautyPane() {
        styleBox = new UIButtonGroup(getNameArray());

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Plugin-Chart_Style")), styleBox},
        } ;
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, rowSize, columnSize);
        this.setLayout(new BorderLayout());
        this.add(panel,BorderLayout.CENTER);
    }

    protected String[] getNameArray(){
        return new String[]{Inter.getLocText("Chart-Default_Name"),
                Inter.getLocText("Plugin-Chart_TopDownShade")
        };
    }
    @Override
    public void populateBean(Integer ob) {
        int finalIndex;
        switch (ob){
            case ChartConstants.STYLE_NONE: finalIndex = 0; break;
            case ChartConstants.STYLE_SHADE: finalIndex = 1; break;
            default: finalIndex = 0;
        }
        styleBox.setSelectedIndex(finalIndex);
    }

    @Override
    public Integer updateBean() {
        int index =  styleBox.getSelectedIndex();
        int style;
        switch (index){
            case 0: style = ChartConstants.STYLE_NONE; break;
            case 1: style = ChartConstants.STYLE_SHADE; break;
            default: style = ChartConstants.STYLE_NONE;
        }
        return style;
    }

    @Override
    protected String title4PopupWindow() {
        return "";
    }

}