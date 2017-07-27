package com.fr.design.present.dict;

import com.fr.base.BaseUtils;
import com.fr.base.Formula;
import com.fr.data.impl.FormulaDictionary;
import com.fr.design.beans.FurtherBasicBeanPane;
import com.fr.design.editor.editor.FormulaEditor;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class FormulaDictPane extends FurtherBasicBeanPane<FormulaDictionary> {

    private static final int EDITOR_COLUMN = 15;
    private FormulaEditor keyFormulaEditor;
    private FormulaEditor valueFormulaEditor;

    public FormulaDictPane() {
        initComponents();
    }

    public static void main(String[] args) {
        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = (JPanel) jf.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(new FormulaDictPane(), BorderLayout.NORTH);
        GUICoreUtils.centerWindow(jf);
        jf.setSize(250, 400);
        jf.setVisible(true);
    }

    private void initComponents() {
        keyFormulaEditor = new FormulaEditor();
        keyFormulaEditor.setColumns(EDITOR_COLUMN);
        JPanel keyFormulaContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        keyFormulaEditor.setPreferredSize(new Dimension(148, 20));
        Icon icon = BaseUtils.readIcon("/com/fr/design/images/m_insert/formula.png");
        keyFormulaContainer.add(new JLabel(icon));
        keyFormulaContainer.add(keyFormulaEditor);

        valueFormulaEditor = new FormulaEditor();
        valueFormulaEditor.setColumns(EDITOR_COLUMN);

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p, p, p, p, p, p};

        UILabel tag = new UILabel(Inter.getLocText("Formula_Dictionary_Display_Examples_Html"));
        JPanel t = new JPanel(new BorderLayout());
        t.add(tag, BorderLayout.NORTH);

        Formula vf = new Formula("$$$");
        valueFormulaEditor = new FormulaEditor("", vf);

        JPanel valueFormulaContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        valueFormulaEditor.setPreferredSize(new Dimension(148, 20));
        valueFormulaContainer.add(new JLabel(icon));
        valueFormulaContainer.add(valueFormulaEditor);


        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Actual_Value"), UILabel.LEFT), keyFormulaContainer},
                new Component[]{null, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Display_Value"), UILabel.LEFT), valueFormulaContainer},
                new Component[]{null, null},
                new Component[]{t, null}
        };
        JPanel panel = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.CENTER);

    }

    public void addChangeListener(DocumentListener l) {
        keyFormulaEditor.addDocumentListener(l);
        valueFormulaEditor.addDocumentListener(l);
    }

    @Override
    public String title4PopupWindow() {
        return Inter.getLocText("FR-Designer_Formula");
    }

    @Override
    public void populateBean(FormulaDictionary dict) {
        keyFormulaEditor.setValue(new Formula(dict.getProduceFormula() == null ? StringUtils.EMPTY : dict.getProduceFormula()));
        valueFormulaEditor.setValue(new Formula(dict.getExcuteFormula() == null ? StringUtils.EMPTY : dict.getExcuteFormula()));
    }

    @Override
    public FormulaDictionary updateBean() {
        FormulaDictionary dict = new FormulaDictionary();
        if (keyFormulaEditor.getValue() != null) {
            dict.setProduceFormula(keyFormulaEditor.getValue().getContent());
        }
        if (valueFormulaEditor.getValue() != null) {
            dict.setExcuteFormula(valueFormulaEditor.getValue().getContent());
        }

        return dict;
    }

    @Override
    public boolean accept(Object ob) {
        return ob instanceof FormulaDictionary;
    }

    @Override
    public void reset() {
        keyFormulaEditor.reset();
        valueFormulaEditor.reset();
    }
}