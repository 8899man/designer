package com.fr.design.mainframe.widget.accessibles;

import java.awt.Dimension;
import javax.swing.SwingUtilities;
import com.fr.base.background.ColorBackground;
import com.fr.design.mainframe.widget.wrappers.BackgroundWrapper;
import com.fr.design.dialog.BasicDialog;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.style.background.BackgroundButtonPane;
import com.fr.general.Background;

public class AccessibleImgBackgroundEditor extends UneditableAccessibleEditor {
	private BackgroundButtonPane choosePane;
	
	public AccessibleImgBackgroundEditor() {
		super(new BackgroundWrapper());
	}
	
	@Override
	protected void showEditorPane() {
		choosePane = new BackgroundButtonPane();
		choosePane.setPreferredSize(new Dimension(600, 400));
		BasicDialog dlg = choosePane.showWindow(SwingUtilities.getWindowAncestor(this));
		dlg.addDialogActionListener(new DialogActionAdapter() {

			@Override
			public void doOk() {
				setValue(choosePane.update());
				fireStateChanged();
			}
		});
		choosePane.populate(getValue() instanceof Background ? (Background) getValue() : new ColorBackground());
		dlg.setVisible(true);
	}
}