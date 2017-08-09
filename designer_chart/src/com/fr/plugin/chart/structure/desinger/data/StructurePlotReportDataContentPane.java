package com.fr.plugin.chart.structure.desinger.data;

import com.fr.base.chart.chartdata.TopDefinitionProvider;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.design.formula.TinyFormulaPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.data.report.AbstractReportDataContentPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.structure.data.StructureReportDefinition;

import javax.swing.*;
import java.awt.*;

/**
 * Created by shine on 2017/2/15.
 */
public class StructurePlotReportDataContentPane extends AbstractReportDataContentPane {

    private TinyFormulaPane nodeName;
    private TinyFormulaPane nodeID;
    private TinyFormulaPane parentID;
    private UITextField seriesName;
    private TinyFormulaPane nodeValue;

    public StructurePlotReportDataContentPane() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p, p, p, p, p};

        nodeName = new TinyFormulaPane();
        nodeID = new TinyFormulaPane();
        parentID = new TinyFormulaPane();
        seriesName = new UITextField();
        nodeValue = new TinyFormulaPane();

        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Node_Name"), SwingConstants.RIGHT), nodeName},
                new Component[]{new UILabel("id", SwingConstants.RIGHT), nodeID},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Parent_ID"), SwingConstants.RIGHT), parentID},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_MultiPie_Series_Name"), SwingConstants.RIGHT), seriesName},
                new Component[]{new UILabel(Inter.getLocText("Chart-Series_Value"), SwingConstants.RIGHT), nodeValue},
        };

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void populateBean(ChartCollection ob) {
        TopDefinitionProvider provider = ob.getSelectedChart().getFilterDefinition();
        if(provider instanceof StructureReportDefinition){
            StructureReportDefinition reportDefinition = (StructureReportDefinition)provider;

            seriesName.setText(reportDefinition.getSeriesName());

            populateFormulaPane(nodeID, reportDefinition.getNodeID());
            populateFormulaPane(parentID, reportDefinition.getParentID());
            populateFormulaPane(nodeName, reportDefinition.getNodeName());
            populateFormulaPane(nodeValue, reportDefinition.getNodeValue());
        }

    }

    private void populateFormulaPane(TinyFormulaPane pane, Object o){
        if(o != null){
            pane.populateBean(o.toString());
        }
    }

    public void updateBean(ChartCollection collection) {
        if (collection != null) {
            StructureReportDefinition reportDefinition = new StructureReportDefinition();

            reportDefinition.setSeriesName(seriesName.getText());

            reportDefinition.setNodeID(canBeFormula(nodeID.getUITextField().getText()));
            reportDefinition.setParentID(canBeFormula(parentID.getUITextField().getText()));
            reportDefinition.setNodeName(canBeFormula(nodeName.getUITextField().getText()));
            reportDefinition.setNodeValue(canBeFormula(nodeValue.getUITextField().getText()));

            collection.getSelectedChart().setFilterDefinition(reportDefinition);
        }
    }

    @Override
    protected String[] columnNames() {
        return new String[0];
    }
}
