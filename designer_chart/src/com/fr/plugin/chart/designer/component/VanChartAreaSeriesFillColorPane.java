package com.fr.plugin.chart.designer.component;

import com.fr.design.dialog.BasicPane;
import com.fr.design.gui.frpane.UINumberDragPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.general.Inter;
import com.fr.plugin.chart.VanChartAttrHelper;
import com.fr.plugin.chart.base.AttrAreaSeriesFillColorBackground;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.background.VanChartMarkerBackgroundPane;

import javax.swing.*;
import java.awt.*;

/**
 * 面积图填充色设置界面
 */
public class VanChartAreaSeriesFillColorPane extends BasicPane {
    private static final long serialVersionUID = 9166866984438854779L;
    private static final int PANE_WIDTH = 221;
    private VanChartMarkerBackgroundPane fillColorPane;
    private UINumberDragPane transparent;

    public VanChartAreaSeriesFillColorPane() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] row = {p,p};
        double[] col = {f};
        fillColorPane = new VanChartMarkerBackgroundPane(){
            protected Component[][] getPaneComponents() {
                return  new Component[][]{
                        new Component[]{null, null},
                        new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_FillColor")), typeComboBox},
                        new Component[]{null, centerPane},
                };
            }
        };
        transparent = new UINumberDragPane(0, 100);

        JPanel transparentPane = TableLayout4VanChartHelper.createGapTableLayoutPane(Inter.getLocText("Plugin-Chart_Alpha"), transparent);

        Component[][] components = new Component[][]{
                new Component[]{fillColorPane},
                new Component[]{transparentPane},
        };

        JPanel contentPane = TableLayoutHelper.createTableLayoutPane(components, row, col);
        contentPane.setPreferredSize(new Dimension(PANE_WIDTH, (int)contentPane.getPreferredSize().getHeight()));

        this.add(contentPane);
    }

    protected String title4PopupWindow(){
        return Inter.getLocText("Plugin-ChartF_FillColor");
    }

    public void populate(AttrAreaSeriesFillColorBackground fillColorBackground) {
        if(fillColorBackground == null){
            fillColorBackground = new AttrAreaSeriesFillColorBackground();
        }
        fillColorPane.populate(fillColorBackground.getColorBackground());
        transparent.populateBean(fillColorBackground.getAlpha() * VanChartAttrHelper.PERCENT);
    }

    public AttrAreaSeriesFillColorBackground update() {
        AttrAreaSeriesFillColorBackground fillColorBackground = new AttrAreaSeriesFillColorBackground();
        fillColorBackground.setColorBackground(fillColorPane.update());
        fillColorBackground.setAlpha(transparent.updateBean()/VanChartAttrHelper.PERCENT);
        return fillColorBackground;
    }

    /**
     * 检查透明的设置是否有效
     * @param enable
     */
    public void checkoutAlpha(boolean enable) {
        transparent.setEnabled(enable);
    }
}