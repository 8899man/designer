package com.fr.design.widget.ui.designer;

import com.fr.design.designer.beans.AdapterBus;
import com.fr.design.designer.beans.ComponentAdapter;
import com.fr.design.designer.creator.PropertyGroupPane;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.mainframe.FormDesigner;
import com.fr.form.ui.Widget;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.ArrayList;

/**
 * Created by kerry on 2017/9/27.
 */
public class WidgetDefinePane extends AbstractDataModify<Widget> {
    private ArrayList<PropertyGroupPane> groupPanes;
    private static final int START_INDEX = 1;

    public WidgetDefinePane(XCreator source, FormDesigner designer) {
        super(source, designer);
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        initComponent(source, designer);
    }

    public void initComponent(XCreator source, FormDesigner designer) {
        ComponentAdapter adapter = AdapterBus.getComponentAdapter(designer, source);
        groupPanes = adapter.getXCreatorPropertyPane();
        //todo 留着做兼容，以后删掉
        for (int i = 0; i < groupPanes.size(); i++) {
            if (ComparatorUtils.equals(groupPanes.get(i).getGroupName(), "Form-Basic_Properties")) {
                groupPanes.remove(i);
            }
        }

        this.add(createCenterPane(START_INDEX), BorderLayout.CENTER);
    }

    private JPanel createCenterPane(int index) {
        JPanel jPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        if (index != groupPanes.size()) {
            jPanel.add(createExpandPane(groupPanes.get(index - 1)), BorderLayout.NORTH);
            jPanel.add(createCenterPane(index + 1), BorderLayout.CENTER);
        } else {
            jPanel.add(createExpandPane(groupPanes.get(index - 1)), BorderLayout.CENTER);
        }
        return jPanel;
    }


    public JPanel createExpandPane(PropertyGroupPane propertyGroupPane) {
        JPanel jPanel = new UIExpandablePane(Inter.getLocText(propertyGroupPane.getGroupName()), 280, 24, propertyGroupPane);
        return jPanel;
    }


    public void populateBean(Widget ob) {
        for (int i = 0; i < groupPanes.size(); i++) {
            groupPanes.get(i).populate(ob);
        }
    }

    public Widget updateBean() {
        return creator.toData();
    }

}
