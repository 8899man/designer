package com.fr.quickeditor.floatquick;

import com.fr.base.Style;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.report.SelectImagePane;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.quickeditor.FloatQuickEditor;
import com.fr.report.cell.cellattr.CellImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FloatImageQuickEditor extends FloatQuickEditor {

    public FloatImageQuickEditor() {
        super();
        UIButton editbutton = new UIButton(Inter.getLocText("FR-Designer_Edit"));
        editbutton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showEditingDialog();
            }
        });
        editbutton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        editbutton.setMargin(null);
        editbutton.setOpaque(false);
        Component[][] components = new Component[][]{
                new Component[]{editbutton}
        };
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {f};
        double[] rowSize = {p};
        JPanel pane = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
        this.setLayout(new BorderLayout());

        this.setBorder(BorderFactory.createEmptyBorder(10, 78, 10, 17));
        this.add(pane, BorderLayout.CENTER);


    }

    private void showEditingDialog() {
        final SelectImagePane imageEditorPane = new SelectImagePane();
        imageEditorPane.populate(floatElement);
        final Object oldValue = floatElement.getValue();
        final Style oldStyle = floatElement.getStyle();
        imageEditorPane.showWindow(DesignerContext.getDesignerFrame(), new DialogActionAdapter() {

            @Override
            public void doOk() {
                CellImage cellImage = imageEditorPane.update();
                if (!ComparatorUtils.equals(cellImage.getImage(), oldValue) || !ComparatorUtils.equals(cellImage.getStyle(), oldStyle)) {
                    floatElement.setValue(cellImage.getImage());
                    floatElement.setStyle(cellImage.getStyle());
                    fireTargetModified();
                }
            }

        }).setVisible(true);
    }

    @Override
    protected void refreshDetails() {
        // TODO Auto-generated method stub

    }

}