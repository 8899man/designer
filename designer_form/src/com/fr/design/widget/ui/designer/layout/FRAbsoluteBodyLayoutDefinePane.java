package com.fr.design.widget.ui.designer.layout;

import com.fr.design.data.DataCreatorUI;
import com.fr.design.designer.creator.*;
import com.fr.design.designer.creator.cardlayout.XWCardMainBorderLayout;
import com.fr.design.designer.properties.items.FRLayoutTypeItems;
import com.fr.design.designer.properties.items.Item;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.FormDesigner;
import com.fr.design.mainframe.WidgetPropertyPane;
import com.fr.form.ui.container.WAbsoluteBodyLayout;
import com.fr.form.ui.container.WAbsoluteLayout;
import com.fr.form.ui.container.WBodyLayoutType;
import com.fr.general.FRLogger;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by ibm on 2017/8/2.
 */
public class FRAbsoluteBodyLayoutDefinePane extends FRAbsoluteLayoutDefinePane {
    private XWAbsoluteBodyLayout xwAbsoluteBodyLayout;
    private WAbsoluteBodyLayout wAbsoluteBodyLayout;
    private UIComboBox layoutCombox;
    private WBodyLayoutType layoutType = WBodyLayoutType.ABSOLUTE;
    private static final int EACH_ROW_COUNT = 4;

    public FRAbsoluteBodyLayoutDefinePane(XCreator xCreator) {
        super(xCreator);
        this.xwAbsoluteBodyLayout = (XWAbsoluteBodyLayout) xCreator;
        wAbsoluteBodyLayout = xwAbsoluteBodyLayout.toData();
    }


    public JPanel createThirdPane() {
        initLayoutComboBox();
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 1}, {1, 1}};
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Attr_Layout_Type")), layoutCombox},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer-Widget_Scaling_Mode")), comboBox},
        };
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 20, 10);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        return panel;

    }

    public void initLayoutComboBox() {
        Item[] items = FRLayoutTypeItems.ITEMS;
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (Item item : items) {
            model.addElement(item);
        }
        layoutCombox = new UIComboBox(model);
        layoutCombox.setSelectedIndex(1);
    }

    @Override
    public String title4PopupWindow() {
        return "absoluteBodyLayout";
    }

    public void populateSubPane(WAbsoluteLayout ob) {
//        WAbsoluteBodyLayout layout = (WAbsoluteBodyLayout) ob;
        layoutCombox.setSelectedIndex(1);
    }

    public WAbsoluteBodyLayout updateSubPane() {
        WAbsoluteBodyLayout layout = (WAbsoluteBodyLayout) creator.toData();
        Item item = (Item) layoutCombox.getSelectedItem();
        Object value = item.getValue();
        int state = 0;
        if (value instanceof Integer) {
            state = (Integer) value;
        }

        if (layoutType == WBodyLayoutType.ABSOLUTE) {
            if (state == WBodyLayoutType.FIT.getTypeValue()) {
                switch2FitBodyLayout();
            }
        }
        return layout;
    }

    @Override
    public DataCreatorUI dataUI() {
        return null;
    }

    private boolean switch2FitBodyLayout() {
        try {
            XWFitLayout xfl = (XWFitLayout) creator.getBackupParent();
            //备份一下组件间隔
            int compInterval = xfl.toData().getCompInterval();
            Component[] components = creator.getComponents();

            Arrays.sort(components, new ComparatorComponentLocation());

            xfl.getLayoutAdapter().removeBean(creator, creator.getWidth(), creator.getHeight());
            xfl.remove(creator);

            for (Component comp : components) {
                XCreator xCreator = (XCreator) comp;
                if (xCreator.shouldScaleCreator()) {
                    XLayoutContainer parentPanel = xCreator.initCreatorWrapper(xCreator.getHeight());
                    xfl.add(parentPanel, xCreator.toData().getWidgetName());
                    parentPanel.updateChildBound(xfl.getActualMinHeight());
                    continue;
                }
                xfl.add(xCreator);
            }
            //这边计算的时候会先把组件间隔去掉
            moveComponents2FitLayout(xfl);
            FormDesigner formDesigner = WidgetPropertyPane.getInstance().getEditingFormDesigner();
            formDesigner.getSelectionModel().setSelectedCreator(xfl);

            for (int i = 0; i < components.length; i++) {
                Component comp = xfl.getComponent(i);
                XCreator creator = (XCreator) comp;
                creator.setBackupBound(components[i].getBounds());
            }

            //把组件间隔加上
            if (xfl.toData().getCompInterval() != compInterval) {
                xfl.moveContainerMargin();
                xfl.moveCompInterval(xfl.getAcualInterval());
                xfl.toData().setCompInterval(compInterval);
                xfl.addCompInterval(xfl.getAcualInterval());
            }
            xfl.toData().setLayoutType(WBodyLayoutType.FIT);
            return true;
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage());
            return false;
        }
    }

    // 把绝对布局中的元素按规则移动到自适应布局中
    // 规则：各元素按顺序放置，其中每行最多4个元素，超出则换行，各元素均分body的高度和宽度
    private void moveComponents2FitLayout(XWFitLayout xwFitLayout) {
        Component[] components = xwFitLayout.getComponents();
        if (components.length == 0) {
            xwFitLayout.updateBoundsWidget();
            return;
        }
        int layoutWidth = xwFitLayout.getWidth() - xwFitLayout.toData().getMargin().getLeft() - xwFitLayout.toData().getMargin().getRight();
        int layoutHeight = xwFitLayout.getHeight() - xwFitLayout.toData().getMargin().getTop() - xwFitLayout.toData().getMargin().getBottom();
        int leftMargin = xwFitLayout.toData().getMargin().getLeft();
        int topMargin = xwFitLayout.toData().getMargin().getTop();
        xwFitLayout.toData().setCompInterval(0);
        int row = (components.length / EACH_ROW_COUNT) + (components.length % EACH_ROW_COUNT == 0 ? 0 : 1);
        //最后一行的列数不定
        int column = components.length % EACH_ROW_COUNT == 0 ? EACH_ROW_COUNT : components.length % EACH_ROW_COUNT;
        int componentWidth = layoutWidth / EACH_ROW_COUNT;
        int componentHeight = layoutHeight / row;
        for (int i = 0; i < row - 1; i++) {
            for (int j = 0; j < EACH_ROW_COUNT; j++) {
                components[EACH_ROW_COUNT * i + j].setBounds(
                        leftMargin + componentWidth * j,
                        topMargin + componentHeight * i,
                        j == EACH_ROW_COUNT - 1 ? layoutWidth - componentWidth * (EACH_ROW_COUNT - 1) : componentWidth,
                        componentHeight
                );
            }
        }
        //最后一行列数是特殊的，要单独处理
        int lastRowWidth = layoutWidth / column;
        int lastRowHeight = layoutHeight - componentHeight * (row - 1);
        for (int i = 0; i < column; i++) {
            components[EACH_ROW_COUNT * (row - 1) + i].setBounds(
                    leftMargin + lastRowWidth * i,
                    topMargin + componentHeight * (row - 1),
                    i == column - 1 ? layoutWidth - lastRowWidth * (column - 1) : lastRowWidth,
                    lastRowHeight
            );
        }
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof XWCardMainBorderLayout) {
                ((XWCardMainBorderLayout) components[i]).recalculateChildWidth(components[i].getWidth());
                ((XWCardMainBorderLayout) components[i]).recalculateChildHeight(components[i].getHeight());
            }
            xwFitLayout.dealDirections((XCreator) components[i], false);
        }
        xwFitLayout.updateBoundsWidget();
    }

    //以组件的位置来确定先后顺序，y小的在前，x小的在前
    private class ComparatorComponentLocation implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if (((Component) o1).getY() < ((Component) o2).getY()) {
                return -1;
            } else if (((Component) o1).getY() > ((Component) o2).getY()) {
                return 1;
            } else {
                if (((Component) o1).getX() < ((Component) o2).getX()) {
                    return -1;
                } else if (((Component) o1).getX() > ((Component) o2).getX()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
    }


}
