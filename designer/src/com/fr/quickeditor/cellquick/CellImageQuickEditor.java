package com.fr.quickeditor.cellquick;

import com.fr.base.Style;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.report.SelectImagePane;
import com.fr.general.ComparatorUtils;
import com.fr.general.IOUtils;
import com.fr.general.Inter;
import com.fr.quickeditor.CellQuickEditor;
import com.fr.report.cell.cellattr.CellImage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 单元格元素图片编辑器
 *
 * @author yaoh.wu
 * @version 2017年8月7日10点53分
 */
public class CellImageQuickEditor extends CellQuickEditor {

    private CellImageQuickEditor() {
        super();
    }

    @Override
    public JComponent createCenterBody() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 15));
        UIButton editButton = new UIButton(Inter.getLocText("Edit"), IOUtils.readIcon("/com/fr/design/images/m_insert/image.png"));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditingDialog();
            }
        });
        editButton.setOpaque(false);
        content.add(editButton, BorderLayout.CENTER);
        return content;
    }

    @SuppressWarnings("Duplicates")
    private void showEditingDialog() {
        final SelectImagePane imageEditorPane = new SelectImagePane();
        imageEditorPane.populate(cellElement);
        final Object oldValue = cellElement.getValue();
        final Style oldStyle = cellElement.getStyle();
        imageEditorPane.showWindow(DesignerContext.getDesignerFrame(), new DialogActionAdapter() {
            @Override
            public void doOk() {
                CellImage cellImage = imageEditorPane.update();
                if (!ComparatorUtils.equals(cellImage.getImage(), oldValue) || !ComparatorUtils.equals(cellImage.getStyle(), oldStyle)) {
                    cellElement.setValue(cellImage.getImage());
                    cellElement.setStyle(cellImage.getStyle());
                    fireTargetModified();
                }
            }
        }).setVisible(true);
    }

    @Override
    protected void refreshDetails() {

    }

}