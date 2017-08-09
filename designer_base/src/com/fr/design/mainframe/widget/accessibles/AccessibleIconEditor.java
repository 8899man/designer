package com.fr.design.mainframe.widget.accessibles;

import com.fr.base.Env;
import com.fr.base.FRContext;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.widget.editors.ITextComponent;
import com.fr.design.mainframe.widget.renderer.IconCellRenderer;
import com.fr.design.mainframe.widget.wrappers.IconWrapper;
import com.fr.design.web.CustomIconPane;
import com.fr.design.dialog.BasicDialog;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.form.ui.WidgetManager;

import javax.swing.*;
import java.awt.*;

public class AccessibleIconEditor extends UneditableAccessibleEditor {

    private CustomIconPane customIconPane;

    public AccessibleIconEditor() {
        super(new IconWrapper());
    }
    
    @Override
	protected ITextComponent createTextField() {
        return new RendererField(new IconCellRenderer());
    }

    protected void setBorderVisible(){
		this.setBorder(BorderFactory.createLineBorder(Color.lightGray));
    }


    @Override
    protected void showEditorPane() {
        if (customIconPane == null) {
            customIconPane = new CustomIconPane();
        }
        BasicDialog editDialog = customIconPane.showWindow(DesignerContext.getDesignerFrame());
        editDialog.addDialogActionListener(new DialogActionAdapter() {

            @Override
            public void doOk() {
                setValue(customIconPane.update());
                fireStateChanged();

				Env currentEnv = FRContext.getCurrentEnv();
				try {
					currentEnv.writeResource(WidgetManager.getProviderInstance());
				} catch (Exception ex) {
					FRContext.getLogger().error(ex.getMessage(), ex);
				}
            }
        });
        customIconPane.populate((String) this.getValue());
        editDialog.setVisible(true);
    }
}