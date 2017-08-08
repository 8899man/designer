package com.fr.design.report;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.fr.page.ReportSettingsProvider;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.dialog.BasicPane;
import com.fr.general.Inter;
import com.fr.design.style.background.BackgroundPane;

public class ReportBackgroundPane extends BasicPane {
	private UICheckBox isPrintBackgroundCheckBox;
    private UICheckBox isExportBackgroundCheckBox;
    private BackgroundPane backgroundPane;

    public ReportBackgroundPane() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());

        backgroundPane = new BackgroundPane();
        this.add(backgroundPane, BorderLayout.CENTER);

        isPrintBackgroundCheckBox = new UICheckBox(Inter.getLocText("FR-Designer_Print_Background"));
        isExportBackgroundCheckBox = new UICheckBox(Inter.getLocText("FR-Designer_Export_Background"));
        JPanel sourth = new JPanel();
        sourth.add(isExportBackgroundCheckBox);
        sourth.add(isPrintBackgroundCheckBox);
        this.add(sourth, BorderLayout.SOUTH);
    }
    
    @Override
    protected String title4PopupWindow() {
    	return Inter.getLocText(new String[]{"paper", "Background"});
    }

    /**
     * Populate
     */
    public void populate(ReportSettingsProvider reportSettings) {
        this.backgroundPane.populate(reportSettings.getBackground());
        this.isPrintBackgroundCheckBox.setSelected(reportSettings.isPrintBackground());
        this.isExportBackgroundCheckBox.setSelected(reportSettings.isExportBackground());
    }

    /**
     * update
     */
    public void update(ReportSettingsProvider reportSettings) {
        reportSettings.setBackground(this.backgroundPane.update());
        reportSettings.setPrintBackground(this.isPrintBackgroundCheckBox.isSelected());
        reportSettings.setExportBackground(this.isExportBackgroundCheckBox.isSelected());
    }
}