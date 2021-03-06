package com.fr.design.style.background.impl;

import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.style.background.BackgroundDetailPane;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.Background;


import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * Null background pane.
 */
public class NullBackgroundPane extends BackgroundDetailPane {

    public NullBackgroundPane() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());

        UILabel centerLabel = new UILabel(
            com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Background_Background_Is_Null") + "...");
        this.add(centerLabel);
        centerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerLabel.setBorder(BorderFactory.createLineBorder(GUICoreUtils.getTitleLineBorderColor()));
    }

    public void populate(Background background) {
        // do nothing.
    }

    public Background update() throws Exception {
        return null;
    }

    public void addChangeListener(ChangeListener changeListener) {
        // do nothing.
    }
}
