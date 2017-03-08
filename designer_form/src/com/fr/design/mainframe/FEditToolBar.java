package com.fr.design.mainframe;

import com.fr.base.BaseUtils;
import com.fr.design.actions.UpdateAction;
import com.fr.design.dialog.BasicDialog;
import com.fr.design.dialog.BasicPane;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.gui.itoolbar.UIToolbar;
import com.fr.design.javascript.JavaScriptActionPane;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.menu.ToolBarDef;
import com.fr.design.style.background.BackgroundPane;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.design.widget.IconDefinePane;
import com.fr.form.ui.*;
import com.fr.form.ui.Button;
import com.fr.form.web.button.Export;
import com.fr.general.Background;
import com.fr.general.Inter;
import com.fr.stable.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by harry on 2017-3-2.
 */
public class FEditToolBar extends BasicPane {

    private JList list;
    private DefaultListModel listModel;
    private JPanel right;
    private CardLayout card;
    private ButtonPane bp;
    private FToolBarButton lastButton;
    private Background background = null;
    private UICheckBox defaultCheckBox;

    private ListSelectionListener listSelectionListener = new ListSelectionListener() {
        public void valueChanged(ListSelectionEvent evt) {
            if (lastButton != null) {
                lastButton.setWidget(bp.update());
            }
            if (list.getSelectedValue() instanceof FToolBarButton) {
                lastButton = (FToolBarButton) list.getSelectedValue();
                if (lastButton.getWidget() instanceof Button) {
                    card.show(right, "button");
                    bp.populate(lastButton.getWidget());
                } else {
                    bp.populate(lastButton.getWidget());
                    card.show(right, "none");
                }
            }
        }
    };


    private ActionListener actioner = new ActionListener() {
        /**
         *
         */
        public void actionPerformed(ActionEvent arg0) {
            final BackgroundPane backgroundPane = new BackgroundPane();
            BasicDialog dialog = backgroundPane.showWindow(DesignerContext.getDesignerFrame());
            backgroundPane.populate(FEditToolBar.this.background);
            dialog.addDialogActionListener(new DialogActionAdapter() {
                public void doOk() {
                    FEditToolBar.this.background = backgroundPane.update();
                    if (FEditToolBar.this.background != null) {
                        FEditToolBar.this.defaultCheckBox.setSelected(false);
                    }
                }
            });
            dialog.setVisible(true);
        }
    };

    public FEditToolBar() {
        initComponent();
    }

    /**
     * 初始化
     */
    public void initComponent() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        JPanel left = FRGUIPaneFactory.createBorderLayout_S_Pane();
        listModel = new DefaultListModel();
        list = new JList(listModel);
        list.setCellRenderer(render);
        left.add(new JScrollPane(list), BorderLayout.CENTER);
        if (listModel.getSize() > 0) {
            list.setSelectedIndex(0);
        }

        ToolBarDef toolbarDef = new ToolBarDef();
        toolbarDef.addShortCut(new MoveUpItemAction());
        toolbarDef.addShortCut(new MoveDownItemAction());
        toolbarDef.addShortCut(new RemoveAction());
        UIToolbar toolBar = ToolBarDef.createJToolBar();
        toolbarDef.updateToolBar(toolBar);
        left.add(toolBar, BorderLayout.NORTH);

        right = FRGUIPaneFactory.createCardLayout_S_Pane();
        card = new CardLayout();
        right.setLayout(card);
        bp = new ButtonPane();
        right.add("none", FRGUIPaneFactory.createNormalFlowInnerContainer_S_Pane());
        right.add("button", bp);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true, left, right);
        // splitPane.setDividerLocation(left.getMinimumSize().width);
        splitPane.setDividerLocation(120);
        this.add(splitPane);
        list.addListSelectionListener(listSelectionListener);
        JPanel backgroundPane = FRGUIPaneFactory.createNormalFlowInnerContainer_S_Pane();
        UIButton bgButton = new UIButton(Inter.getLocText(new String[]{"Background", "Set"}));
        defaultCheckBox = new UICheckBox(Inter.getLocText(new String[]{"Default", "Background"}));
        bgButton.addActionListener(actioner);
        backgroundPane.add(defaultCheckBox);
        backgroundPane.add(bgButton);
        backgroundPane.setBorder(BorderFactory.createTitledBorder(Inter.getLocText(new String[]{"Background", "Set"})));
        this.add(backgroundPane, BorderLayout.SOUTH);
    }

    ListCellRenderer render = new DefaultListCellRenderer() {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof FToolBarButton) {
                FToolBarButton button = (FToolBarButton) value;
                this.setText(button.getNameOption().optionName());
                this.setIcon(button.getNameOption().optionIcon());
            }
            return this;
        }
    };

    @Override
    protected String title4PopupWindow() {
        return Inter.getLocText("FR-Designer_Edit");
    }

    public void populate(FToolBar ftoolbar) {
        this.populate(ftoolbar, null);
    }

    public void populate(FToolBar ftoolbar, FToolBarButton button) {
        if (ftoolbar == null) {
            return;
        }
        for (int i = 0; i < ftoolbar.getButtonlist().size(); i++) {
            listModel.addElement(ftoolbar.getButtonlist().get(i));
        }
        this.list.validate();
        this.list.repaint();
        if (ftoolbar.getButtonlist().size() > 0) {
            this.list.setSelectedIndex(0);
        }
        if (button != null) {
            this.list.setSelectedValue(button, true);
        }
        this.background = ftoolbar.getBackground();

        this.defaultCheckBox.setSelected(ftoolbar.isDefault() ? true : false);
    }

    public FToolBar update() {
        if (this.list.getSelectedIndex() > -1) {
            for (int i = 0; i < listModel.getSize(); i++) {
                this.list.setSelectedIndex(i);
                FToolBarButton toolBarButton = (FToolBarButton) this.list.getSelectedValue();
                Widget widget = this.bp.update();
                toolBarButton.setWidget(widget);
                if (widget instanceof Button) {
                    String iconname = ((Button) widget).getIconName();
                    if (StringUtils.isNotBlank(iconname)) {
                        Image iimage = WidgetManager.getProviderInstance().getIconManager().getIconImage(iconname);
                        toolBarButton.setIcon(new ImageIcon(iimage));
                    }
                }
            }
        }
        java.util.List<FToolBarButton> list = new ArrayList<FToolBarButton>();
        for (int i = 0; i < listModel.size(); i++) {
            list.add((FToolBarButton) listModel.get(i));
        }
        FToolBar ftoolBar = new FToolBar();
        ftoolBar.setButtonlist(list);

        ftoolBar.setDefault(this.defaultCheckBox.isSelected());
        if (!ftoolBar.isDefault()) {
            ftoolBar.setBackground(this.background);
        }
        return ftoolBar;
    }

    private class MoveUpItemAction extends UpdateAction {
        public MoveUpItemAction() {
            this.setName(Inter.getLocText("Utils-Move_Up"));
            this.setMnemonic('U');
            this.setSmallIcon(BaseUtils.readIcon("/com/fr/design/images/control/up.png"));
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent evt) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex == -1) {
                return;
            }

            // 上移
            if (selectedIndex > 0) {
                DefaultListModel listModel = (DefaultListModel) list.getModel();

                Object selecteObj1 = listModel.get(selectedIndex - 1);
                listModel.set(selectedIndex - 1, listModel.get(selectedIndex));
                listModel.set(selectedIndex, selecteObj1);

                list.setSelectedIndex(selectedIndex - 1);
                list.ensureIndexIsVisible(selectedIndex - 1);
                list.validate();
            }
        }
    }

    private class MoveDownItemAction extends UpdateAction {
        public MoveDownItemAction() {
            this.setName(Inter.getLocText("Utils-Move_Down"));
            this.setMnemonic('D');
            this.setSmallIcon(BaseUtils.readIcon("/com/fr/design/images/control/down.png"));
        }

        /**
         *
         */
        public void actionPerformed(ActionEvent evt) {
            int selectedIndex = list.getSelectedIndex();
            if (selectedIndex == -1) {
                return;
            }

            // 下移
            if (selectedIndex == -1) {
                return;
            }

            if (selectedIndex < list.getModel().getSize() - 1) {
                DefaultListModel listModel = (DefaultListModel) list.getModel();

                Object selecteObj1 = listModel.get(selectedIndex + 1);
                listModel.set(selectedIndex + 1, listModel.get(selectedIndex));
                listModel.set(selectedIndex, selecteObj1);

                list.setSelectedIndex(selectedIndex + 1);
                list.ensureIndexIsVisible(selectedIndex + 1);
                list.validate();
            }
        }
    }

    public class RemoveAction extends UpdateAction {
        public RemoveAction() {
            this.setName(Inter.getLocText("FR-Designer_Delete"));
            this.setSmallIcon(BaseUtils.readIcon("/com/fr/base/images/cell/control/remove.png"));
        }

        /**
         * 动作
         * @param e 事件
         */
        public void actionPerformed(ActionEvent e) {
            int i = list.getSelectedIndex();
            if (i < 0 || !(listModel.getElementAt(i) instanceof FToolBarButton)) {
                return;
            }
            int val = JOptionPane.showConfirmDialog(FEditToolBar.this, Inter.getLocText("FR-Designer_Are_You_Sure_To_Delete_The_Data") + "?", "Message", JOptionPane.YES_NO_OPTION);
            if (val != JOptionPane.YES_OPTION) {
                return;
            }
            listModel.removeElementAt(i);
            list.validate();
            if (listModel.size() > 0) {
                list.setSelectedIndex(0);
            } else {
                card.show(right, "none");
            }
        }
    }

    public class ButtonPane extends BasicPane {
        private CardLayout card;
        private JPanel centerPane;
        private UICheckBox icon, text, excelP, excelO;
        private Widget widget;
        private UITextField nameField;
        private IconDefinePane iconPane;
        private UIButton button;
        private JavaScriptActionPane javaScriptPane;

        public ButtonPane() {
            this.initComponents();
        }

        /**
         * 初始化元素
         */
        public void initComponents() {
            this.setLayout(FRGUIPaneFactory.createBorderLayout());
            JPanel north = FRGUIPaneFactory.createBorderLayout_S_Pane();
            icon = new UICheckBox(Inter.getLocText("FR-Designer_Show_Icon"));
            text = new UICheckBox(Inter.getLocText("FR-Designer_Show_Text"));

            north.add(icon, BorderLayout.NORTH);
            north.add(text, BorderLayout.CENTER);

            nameField = new UITextField(8);
            iconPane = new IconDefinePane();
            javaScriptPane = JavaScriptActionPane.createDefault();

            double p = TableLayout.PREFERRED;
            double rowSize[] = {p, p};
            double columnSize[] = {p, p};

            Component[][] coms = new Component[][]{{new UILabel(Inter.getLocText(new String[]{"Widget", "Printer-Alias"}) + ":"), nameField}, {new UILabel(Inter.getLocText(new String[]{"Widget", "Icon"}) + ":"), iconPane}};

            JPanel nameIconPane = TableLayoutHelper.createTableLayoutPane(coms, rowSize, columnSize);

            north.add(nameIconPane, BorderLayout.SOUTH);

            north.setBorder(BorderFactory.createTitledBorder(Inter.getLocText(new String[]{"Form-Button", "Property", "Set"})));
            this.add(north, BorderLayout.NORTH);
            JPanel none = FRGUIPaneFactory.createNormalFlowInnerContainer_S_Pane();
            centerPane = FRGUIPaneFactory.createCardLayout_S_Pane();
            card = new CardLayout();
            centerPane.setLayout(card);
            centerPane.add("custom", getCustomPane());
            centerPane.add("export", getExport());
            centerPane.add("none", none);

            this.add(centerPane, BorderLayout.CENTER);
        }


        private JPanel getCustomPane() {
            JPanel customPane = FRGUIPaneFactory.createCenterFlowInnerContainer_S_Pane();

            button = new UIButton(Inter.getLocText("FR-Designer_User_Defined_Event"));
            customPane.add(button);
            customPane.setBorder(GUICoreUtils.createTitledBorder(Inter.getLocText("FR-Designer_Edit") + "JS", null));
            button.addActionListener(l);
            return customPane;
        }

        private JPanel getExport() {
            JPanel export = FRGUIPaneFactory.createY_AXISBoxInnerContainer_L_Pane();
            // export.setLayout(new BoxLayout(export, BoxLayout.Y_AXIS));
            excelP = new UICheckBox(Inter.getLocText("FR-Designer-Output_Excel_Page"));
            excelO = new UICheckBox(Inter.getLocText("FR-Designer-Output_Excel_Simple"));
            export.add(excelP);
            export.add(Box.createVerticalStrut(2));
            export.add(excelO);
            export.add(Box.createVerticalStrut(2));

            export.setBorder(GUICoreUtils.createTitledBorder(Inter.getLocText(new String[]{"Form-Button", "Property", "Set"}), null));
            return export;
        }

        @Override
        protected String title4PopupWindow() {
            return "Button";
        }

        ActionListener l = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(widget instanceof CustomToolBarButton)) {
                    return;
                }
                if (javaScriptPane == null || ((CustomToolBarButton)widget).getJSImpl() == null) {
                    javaScriptPane = JavaScriptActionPane.createDefault();
                }
                javaScriptPane.setPreferredSize(new Dimension(750, 500));
                BasicDialog dialog = javaScriptPane.showWindow(SwingUtilities.getWindowAncestor(ButtonPane.this));
                dialog.addDialogActionListener(new DialogActionAdapter() {
                    @Override
                    public void doOk() {
                        ((CustomToolBarButton) widget).setJSImpl(javaScriptPane.updateBean());
                    }
                });
                dialog.setVisible(true);
            }
        };

        /**
         * 更新
         * @param widget 对应组件
         */
        public void populate(Widget widget) {
            this.widget = widget;
            card.show(centerPane, "none");
            if (widget instanceof Button) {
                populateDefault();
            }
            if (widget instanceof Export) {
                populateExport();
            } else if (widget instanceof CustomToolBarButton) {
                populateCustomToolBarButton();
            }
        }

        private void populateExport(){
            card.show(centerPane, "export");
            Export export = (Export) widget;
            this.excelP.setSelected(export.isExcelPAvailable());
            this.excelO.setSelected(export.isExcelOAvailable());
        }

        private void populateCustomToolBarButton(){
            card.show(centerPane, "custom");
            CustomToolBarButton customToolBarButton = (CustomToolBarButton) widget;
            if (customToolBarButton.getJSImpl() != null) {
                this.javaScriptPane.populateBean(customToolBarButton.getJSImpl());
            }
        }



        private void populateDefault(){
            Button button = (Button) widget;
            this.icon.setSelected(button.isShowIcon());
            this.text.setSelected(button.isShowText());
            this.nameField.setText(button.getText());
            this.iconPane.populate(((Button) widget).getIconName());
        }

        /**
         * 更新
         *
         * @return 对应组件
         */
        public Widget update() {
            if (widget instanceof Export) {
                updateExport();
            } else if (widget instanceof CustomToolBarButton) {
                ((CustomToolBarButton) widget).setJSImpl(this.javaScriptPane.updateBean());
            }
            if (widget instanceof Button) {
                updateDefault();
            }

            return widget;
        }

        private void updateDefault(){
            ((Button) widget).setShowIcon(this.icon.isSelected());
            ((Button) widget).setShowText(this.text.isSelected());
            ((Button) widget).setText(this.nameField.getText());
            ((Button) widget).setIconName(this.iconPane.update());
        }


        private void updateExport(){
            Export export = (Export) widget;
            export.setExcelPAvailable(this.excelP.isSelected());
            export.setExcelOAvailable(this.excelO.isSelected());
        }
    }
}
