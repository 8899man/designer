package com.fr.design.report.mobile;

import com.fr.design.beans.BasicBeanPane;
import com.fr.design.dialog.mobile.MobileRadioCheckPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.general.Inter;
import com.fr.report.mobile.ElementCaseMobileAttr;

import javax.swing.*;
import java.awt.*;

/**
 * Created by 方磊 on 2016/11/8.
 */
public class MobileToolBarPane extends BasicBeanPane<ElementCaseMobileAttr> {
    //缩放选项面板
    private MobileRadioCheckPane zoomCheckPane;

    //刷新选项面板
    private MobileRadioCheckPane refreshCheckPane;

    public MobileToolBarPane() {
        this.initComponents();
    }

    private void initComponents() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        JPanel borderPane = FRGUIPaneFactory.createTitledBorderPane(this.title4PopupWindow());
        JPanel toobarsPane = FRGUIPaneFactory.createBorderLayout_S_Pane();

        UILabel uiLabel = new UILabel("html5");
        uiLabel.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
        zoomCheckPane = new MobileRadioCheckPane(Inter.getLocText("FR-Designer_Mobile-Zoom"));
        refreshCheckPane = new MobileRadioCheckPane(Inter.getLocText("FR-Designer_Mobile-Refresh"));

        toobarsPane.add(uiLabel, BorderLayout.WEST);
        toobarsPane.add(zoomCheckPane, BorderLayout.CENTER);
        toobarsPane.add(refreshCheckPane, BorderLayout.EAST);
        borderPane.add(toobarsPane);
        this.add(borderPane);
    }

    @Override
    public void populateBean(ElementCaseMobileAttr ob) {
        if (ob == null) {
            ob = new ElementCaseMobileAttr();
        }
        this.zoomCheckPane.populateBean(ob.isZoom());
        this.refreshCheckPane.populateBean(ob.isRefresh());
    }

    @Override
    public ElementCaseMobileAttr updateBean() {
        return null;
    }

    @Override
    public void updateBean(ElementCaseMobileAttr mobileAttr) {
        if(mobileAttr != null) {
            mobileAttr.setZoom(this.zoomCheckPane.updateBean());
            mobileAttr.setRefresh(this.refreshCheckPane.updateBean());
        }
    }

    @Override
    protected String title4PopupWindow() {
        return Inter.getLocText("FR-Designer_Mobile-Toolbar");
    }
}
