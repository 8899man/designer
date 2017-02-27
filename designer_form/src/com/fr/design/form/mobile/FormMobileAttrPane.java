package com.fr.design.form.mobile;

import com.fr.design.beans.BasicBeanPane;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.form.main.mobile.FormMobileAttr;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;

/**
 * Created by fanglei on 2016/11/17.
 */
public class FormMobileAttrPane extends BasicBeanPane<FormMobileAttr>{
    //工具栏容器
    private MobileToolBarPane mobileToolBarPane;

    //h5解析容器
    private MobileUseHtmlGroupPane mobileUseHtmlGroupPane;

    static final int PADDINGHEIGHT = 10;

    public FormMobileAttrPane() {
        this.initComponents();
    }

    //现在只有两个panel，填不满自适应对话框，只能为工具栏Panel和h5解析方式panel分别包裹上一层Panel再计算高度，不然会自动
    //拉长两个Panel的高度去填满整个对话框。
    private void initComponents() {
        JPanel jPanel1 = new JPanel();
        JPanel jPanel2 = new JPanel();
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        jPanel1.setLayout(FRGUIPaneFactory.createBorderLayout());
        jPanel1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        jPanel2.setLayout(FRGUIPaneFactory.createBorderLayout());
        jPanel2.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.mobileToolBarPane = new MobileToolBarPane();
        this.mobileUseHtmlGroupPane = new MobileUseHtmlGroupPane(Inter.getLocText("FR-Designer_Mobile_Form_Analysis_Annotation"));
        //设置一个JPanel包裹mobileToolBarPane这个Panel，让jPanel的高度等于mobileToolBarPane高度加10，再放入this中
        jPanel1.setPreferredSize(new Dimension(0, (int)this.mobileToolBarPane.getPreferredSize().getHeight() + PADDINGHEIGHT));
        jPanel2.setPreferredSize(new Dimension(0, (int)this.mobileUseHtmlGroupPane.getPreferredSize().getHeight() + PADDINGHEIGHT));
        jPanel1.add("North", this.mobileUseHtmlGroupPane);
        jPanel2.add("North", this.mobileToolBarPane);
        this.add("North", jPanel1);
        this.add("Center", jPanel2);
    }

    @Override
    public void populateBean(FormMobileAttr ob) {
        if (ob == null) {
            ob = new FormMobileAttr();
        }
        this.mobileToolBarPane.populateBean(ob);
        this.mobileUseHtmlGroupPane.populateBean(ob);
    }

    @Override
    public FormMobileAttr updateBean() {
        FormMobileAttr formMobileAttr = new FormMobileAttr();
        this.mobileToolBarPane.updateBean(formMobileAttr);
        this.mobileUseHtmlGroupPane.updateBean(formMobileAttr);
        return formMobileAttr;
    }

    @Override
    protected String title4PopupWindow() {
        return Inter.getLocText("FR-Designer_Mobile-Attr");
    }
}
