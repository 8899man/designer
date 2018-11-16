/*
 * Copyright(c) 2001-2010, FineReport Inc, All Rights Reserved.
 */
package com.fr.design.actions.file.export;

import com.fr.base.BaseUtils;
import com.fr.base.extension.FileExtension;
import com.fr.design.i18n.Toolkit;
import com.fr.design.mainframe.JWorkBook;
import com.fr.design.menu.KeySetUtils;
import com.fr.file.filter.ChooseFileFilter;
import com.fr.io.exporter.DesignExportType;

/**
 * Export pdf
 */
public class PDFExportAction extends AbstractJWorkBookExportAction {
    /**
     * Constructor
     */
    public PDFExportAction(JWorkBook jwb) {
        super(jwb);
        this.setMenuKeySet(KeySetUtils.PDF_EXPORT);
        this.setName(getMenuKeySet().getMenuKeySetName() + "...");
        this.setMnemonic(getMenuKeySet().getMnemonic());
        this.setSmallIcon(BaseUtils.readIcon("/com/fr/design/images/m_file/pdf.png"));
    }

    @Override
    protected ChooseFileFilter getChooseFileFilter() {
        return new ChooseFileFilter(FileExtension.PDF, Toolkit.i18nText("Fine-Design_Report_Export_PDF"));
    }

    @Override
    protected String getDefaultExtension() {
        return FileExtension.PDF.getExtension();
    }

    @Override
    public DesignExportType exportType() {
        return DesignExportType.PDF;
    }
}
