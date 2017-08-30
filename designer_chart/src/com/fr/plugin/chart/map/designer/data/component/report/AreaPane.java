package com.fr.plugin.chart.map.designer.data.component.report;

import com.fr.chart.chartattr.ChartCollection;
import com.fr.design.formula.TinyFormulaPane;
import com.fr.design.gui.ilable.BoldFontTextLabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.data.report.AbstractReportDataContentPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.map.designer.data.component.LongitudeLatitudeAndArea;

import javax.swing.*;
import java.awt.*;

/**
 * Created by hufan on 2016/12/21.
 */
public class AreaPane extends AbstractReportDataContentPane {
    protected TinyFormulaPane areaName;

    public AreaPane() {
        JPanel panel = createContentPane();
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    protected JPanel createContentPane() {
        areaName = new TinyFormulaPane();
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p};
        Component[][] components = getComponent ();
        return TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
    }

    protected Component[][] getComponent () {
       return new Component[][]{
                new Component[]{new BoldFontTextLabel(Inter.getLocText("FR-Chart-Area_Name")), areaName}
        };
    }

    @Override
    protected String[] columnNames() {
        return new String[0];
    }

    public void populate(LongitudeLatitudeAndArea longLatArea) {
        if (longLatArea.getArea() != null) {
            areaName.getUITextField().setText(longLatArea.getArea().toString());
        }
    }

    public LongitudeLatitudeAndArea update() {
        LongitudeLatitudeAndArea longLatArea = new LongitudeLatitudeAndArea();
        longLatArea.setArea(canBeFormula(areaName.getUITextField().getText()));
        return longLatArea;
    }

    @Override
    public void populateBean(ChartCollection ob) {

    }
}
