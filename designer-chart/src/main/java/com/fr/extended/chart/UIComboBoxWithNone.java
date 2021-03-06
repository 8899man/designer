package com.fr.extended.chart;

import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.i18n.Toolkit;

import java.util.List;

/**
 * Created by shine on 2018/9/27.
 */
public class UIComboBoxWithNone extends UIComboBox {

    private static String getNoneLocaleString() {
        return Toolkit.i18nText("Fine-Design_Chart_Use_None");
    }

    @Override
    public void refreshBoxItems(List list) {
        super.refreshBoxItems(list);
        addNoneItem();
    }

    @Override
    public void clearBoxItems() {
        super.clearBoxItems();
        addNoneItem();
    }


    private void addNoneItem() {
        addItem(getNoneLocaleString());

    }

    @Override
    public void setSelectedItem(Object anObject) {
        super.setSelectedItem(anObject);

        if (getSelectedIndex() == -1) {//找不到的都选中无。中文的无 英文下是none。
            super.setSelectedItem(getNoneLocaleString());
        }
    }

}
