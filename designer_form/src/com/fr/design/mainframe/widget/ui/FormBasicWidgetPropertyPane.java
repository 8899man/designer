package com.fr.design.mainframe.widget.ui;

import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.form.ui.Widget;
import com.fr.general.Inter;
import javax.swing.BorderFactory;

/**
 * Created by ibm on 2017/7/26.
 */
public class FormBasicWidgetPropertyPane extends BasicSetVisiblePropertyPane {
    private UICheckBox enableCheckBox;

    public FormBasicWidgetPropertyPane() {
        super();
    }

    public UICheckBox createOtherConfig() {
        enableCheckBox = new UICheckBox(Inter.getLocText("Enabled"), true);
        enableCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        return enableCheckBox;
    }

    @Override
    public String title4PopupWindow() {
        return "basicProperty";
    }

    public void populate(Widget widget) {
        super.populate(widget);
        enableCheckBox.setSelected(widget.isEnabled());

    }

    public void update(Widget widget) {
        super.update(widget);
        widget.setEnabled(enableCheckBox.isSelected());
    }

}
