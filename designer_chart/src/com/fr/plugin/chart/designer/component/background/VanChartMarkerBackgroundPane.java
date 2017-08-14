package com.fr.plugin.chart.designer.component.background;

import com.fr.base.background.ColorBackground;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.backgroundpane.BackgroundQuickPane;
import com.fr.design.mainframe.backgroundpane.ColorBackgroundQuickPane;
import com.fr.design.mainframe.backgroundpane.NullBackgroundQuickPane;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;

/**
 * 系列色（无背景颜色）、颜色面板
 */
public class VanChartMarkerBackgroundPane extends VanChartBackgroundPane {
    private static final long serialVersionUID = -1032221277140976934L;

    public VanChartMarkerBackgroundPane(){
        initComponents();

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;

        double[] columnSize = {p, f};
        double[] rowSize = {p, p};

        JPanel panel =  TableLayoutHelper.createTableLayoutPane(getPaneComponents(), rowSize, columnSize);
        this.setLayout(new BorderLayout());
        this.add(panel,BorderLayout.CENTER);
    }

    protected Component[][] getPaneComponents() {
        return  new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Chart-Shape_Fill")), typeComboBox},
                new Component[]{null, centerPane},
        };
    }

    protected void initList() {
        paneList.add(new NullBackgroundQuickPane(){
            @Override
            public String title4PopupWindow() {
                return Inter.getLocText("Plugin-ChartF_SeriesColor");
            }

        });
        paneList.add(new ColorBackgroundQuickPane());
    }

    public void populate(ColorBackground colorBackground) {
        if(colorBackground == null) {
            return;
        }

        for (int i = 0; i < paneList.size(); i++) {
            BackgroundQuickPane pane = paneList.get(i);
            if (pane.accept(colorBackground)) {
                pane.populateBean(colorBackground);
                typeComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    public ColorBackground update() {
        return (ColorBackground)paneList.get(typeComboBox.getSelectedIndex()).updateBean();
    }
}