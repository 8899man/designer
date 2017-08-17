package com.fr.plugin.chart.wordcloud.designer.data;

import com.fr.base.chart.chartdata.TopDefinitionProvider;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.design.formula.TinyFormulaPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.gui.data.report.AbstractReportDataContentPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.wordcloud.data.WordCloudReportDefinition;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Mitisky on 16/11/29.
 */
public class WordCloudPlotReportDataContentPane extends AbstractReportDataContentPane {

    private UITextField name;
    private TinyFormulaPane wordName;
    private TinyFormulaPane wordValue;

    public WordCloudPlotReportDataContentPane() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = { p, f };
        double[] rowSize = { p, p, p};

        name = new UITextField();
        wordName = new TinyFormulaPane();
        wordValue = new TinyFormulaPane();

        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_MultiPie_Series_Name"), SwingConstants.RIGHT), name},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Word_Name"), SwingConstants.RIGHT), wordName},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Word_Value"), SwingConstants.RIGHT), wordValue}
        };

        JPanel panel = TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);
    }

    @Override
    protected String[] columnNames() {
        return new String[0];
    }

    /**
     * Populate.
     *
     * @param ob
     */
    @Override
    public void populateBean(ChartCollection ob) {
        TopDefinitionProvider definition = ob.getSelectedChart().getFilterDefinition();
        if(definition instanceof WordCloudReportDefinition) {
            WordCloudReportDefinition wordCloudDefinition = (WordCloudReportDefinition)definition;

            name.setText(wordCloudDefinition.getName());

            if (wordCloudDefinition.getWordName() != null) {
                wordName.getUITextField().setText(wordCloudDefinition.getWordName().toString());
            }

            if(wordCloudDefinition.getWordValue() != null) {
                wordValue.getUITextField().setText(wordCloudDefinition.getWordValue().toString());
            }
        }

    }

    public void updateBean(ChartCollection collection) {

        if (collection != null) {
            WordCloudReportDefinition wordCloudDefinition = new WordCloudReportDefinition();

            wordCloudDefinition.setName(name.getText());

            wordCloudDefinition.setWordName(canBeFormula(wordName.getUITextField().getText()));

            wordCloudDefinition.setWordValue(canBeFormula(wordValue.getUITextField().getText()));

            collection.getSelectedChart().setFilterDefinition(wordCloudDefinition);
        }
    }
}
