package com.fr.design.mainframe;

import com.fr.base.BaseUtils;
import com.fr.base.FRContext;
import com.fr.design.gui.frpane.UITabbedPane;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.ibutton.UIHeadGroup;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.imenu.UIMenuItem;
import com.fr.design.gui.imenu.UIPopupMenu;
import com.fr.design.icon.IconPathConstants;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.form.share.ShareLoader;
import com.fr.form.ui.ElCaseBindInfo;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.general.SiteCenter;
import com.fr.share.ShareConstants;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: zx
 * Date: 14-7-8
 * Time: 下午8:18
 */
public class FormWidgetDetailPane extends FormDockView{

    private JPanel tabbedPane;
    private UIScrollPane downPane;
    private JPanel reuWidgetPanel;
    private UIComboBox comboBox;
    private ElCaseBindInfo[] elCaseBindInfoList;
    private UIButton deleteButton;
    private UIButton resetButton;
    private JPanel editPanel;
    private JPanel resetPanel;
    private JPanel menutPanel;
    private JPanel menutPanelNorthPane;
    private static final int OFFSET_X = 140;
    private static final int OFFSET_Y = 26;
    private SwingWorker sw;
    //组件面板是否可以编辑
    private boolean isEdit;
    private CardLayout card;

    private static final String REPORT_TAB = Inter.getLocText("FR-Engine_Report");
    private static final String CHART_TAB = Inter.getLocText("FR-Designer-Form-ToolBar_Chart");

    public static FormWidgetDetailPane getInstance() {
        if (HOLDER.singleton == null) {
            HOLDER.singleton = new FormWidgetDetailPane();
        }
        return HOLDER.singleton;
    }

    private  FormWidgetDetailPane(){
        setLayout(FRGUIPaneFactory.createBorderLayout());
    }


    public static FormWidgetDetailPane getInstance(FormDesigner formEditor) {
        HOLDER.singleton.setEditingFormDesigner(formEditor);
        HOLDER.singleton.refreshDockingView();
        return HOLDER.singleton;
    }

    private static class HOLDER {
        private static FormWidgetDetailPane singleton = new FormWidgetDetailPane();
    }

    public String getViewTitle() {
        return Inter.getLocText("FR-Widget_Tree_And_Table");
    }

    @Override
    public Icon getViewIcon() {
        return BaseUtils.readIcon("/com/fr/design/images/m_report/attributes.png");
    }

    /**
     * 初始化
     */
    public void refreshDockingView(){
        FormDesigner designer = this.getEditingFormDesigner();
        removeAll();
        if(designer == null){
            clearDockingView();
            return;
        }
        reuWidgetPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        reuWidgetPanel.setBorder(null);
        if (elCaseBindInfoList == null) {
            if (sw != null) {
                sw.cancel(true);
            }
            sw = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    elCaseBindInfoList = ShareLoader.getLoader().getAllBindInfoList();
                    refreshDownPanel(false);
                    return null;
                }
            };
            sw.execute();
        }
        initReuWidgetPanel();
        initMenuPanel();

        card = new CardLayout();
        tabbedPane = new JPanel();
        tabbedPane.setLayout(card);
        tabbedPane.add(REPORT_TAB, reuWidgetPanel);
        tabbedPane.add(CHART_TAB, new JPanel());
        UIHeadGroup tabsHeaderIconPane = new UIHeadGroup(new String[] {REPORT_TAB, CHART_TAB}) {
            @Override
            public void tabChanged(int index) {
                card.show(tabbedPane, labelButtonList.get(index).getText());
            }
        };
        tabsHeaderIconPane.setNeedLeftRightOutLine(false);

        add(tabsHeaderIconPane, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

    }

    /**
     * 初始化组件共享和复用面板
     */
    private void initReuWidgetPanel() {
        elCaseBindInfoList = ShareLoader.getLoader().getAllBindInfoList();
        downPane = new UIScrollPane(new ShareWidgetPane(elCaseBindInfoList, false));
        reuWidgetPanel.add(downPane);
    }

    /**
     * 初始化菜单栏面板
     */
    private void initMenuPanel() {
        menutPanel = new JPanel();
        menutPanel.setLayout(FRGUIPaneFactory.createBorderLayout());
        menutPanel.setBorder(BorderFactory.createEmptyBorder(3, 10, 10, 15));
//        menutPanel.setPreferredSize(new Dimension(240, 48));

        menutPanelNorthPane = new JPanel(new BorderLayout());
        menutPanelNorthPane.add(new UILabel(Inter.getLocText("FR-Designer_LocalWidget"),
                SwingConstants.HORIZONTAL), BorderLayout.WEST);
        menutPanelNorthPane.add(initEditButtonPane(), BorderLayout.EAST);
        menutPanelNorthPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        menutPanel.add(menutPanelNorthPane, BorderLayout.NORTH);
        comboBox = new UIComboBox(getFormCategories());
        comboBox.setPreferredSize(new Dimension(240, comboBox.getPreferredSize().height));
        initComboBoxSelectedListener();
        menutPanel.add(comboBox, BorderLayout.CENTER);
        reuWidgetPanel.add(menutPanel, BorderLayout.NORTH);

    }

    /**
     * 创建菜单栏按钮面板
     */
    private JPanel initEditButtonPane() {
        editPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));

        editPanel.add(createRefreshButton());
        editPanel.add(createDownloadButton());
        editPanel.add(createInstallButton());
        editPanel.add(createDeleteButton());

        return editPanel;
    }

    /**
     * 创建取消删除面板
     */
    private JPanel initResetButtonPane() {
        resetPanel = new JPanel();
        resetButton = new UIButton(Inter.getLocText("FR-Designer_Reset"));
        resetPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        resetButton.set4ToolbarButton();
        resetButton.setOpaque(true);
        resetButton.setBackground(new Color(184, 220, 242));
        resetButton.setForeground(Color.WHITE);
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshDownPanel(false);
                replaceButtonPanel(false);
                reuWidgetPanel.remove(deleteButton);
            }
        });

        deleteButton = new UIButton(Inter.getLocText("FR-Designer_Remove_Item"));
        deleteButton.set4ToolbarButton();
        deleteButton.setOpaque(true);
        deleteButton.setBackground(Color.red);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ShareLoader.getLoader().removeModulesFromList()) {
                    refreshShareMoudule();
                    reuWidgetPanel.remove(deleteButton);
                    elCaseBindInfoList = ShareLoader.getLoader().getAllBindInfoList();
                    JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Share_Module_Removed_Successful"));
                    refreshDownPanel(false);
                    replaceButtonPanel(false);
                    refreshComboxData();
                } else {
                    JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Share_Module_Removed_Failed"));
                }

            }
        });
        JPanel deletePane = new JPanel(new BorderLayout());
        deletePane.add(deleteButton, BorderLayout.CENTER);
        deletePane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        resetPanel.setLayout(FRGUIPaneFactory.createBorderLayout());
        resetPanel.add(resetButton, BorderLayout.CENTER);
        resetPanel.add(deletePane, BorderLayout.WEST);

        refreshDownPanel(true);

        return resetPanel;

    }


    private void initComboBoxSelectedListener() {
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                ShareLoader.getLoader().resetRemovedModuleList();
                int filterIndex = comboBox.getSelectedIndex();
                if (filterIndex == 0) {
                    elCaseBindInfoList = ShareLoader.getLoader().getAllBindInfoList();
                } else {
                    String filterName = comboBox.getSelectedItem().toString();
                    elCaseBindInfoList = ShareLoader.getLoader().getFilterBindInfoList(filterName);
                }
                refreshDownPanel(isEdit);

            }
        });
    }

    /**
     * 创建工具条按钮
     */
    private UIButton createToolButton(Icon icon, String toolTip, ActionListener actionListener) {
        UIButton toolButton = new UIButton();
        toolButton.setIcon(icon);
        toolButton.setToolTipText(toolTip);
        toolButton.set4ToolbarButton();
        toolButton.addActionListener(actionListener);
        return toolButton;

    }

    /**
     * 创建刷新按钮
     */
    private UIButton createRefreshButton() {
        return createToolButton(
                BaseUtils.readIcon("/com/fr/design/form/images/refresh.png"),
                Inter.getLocText("FR-Designer_Refresh"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (sw != null) {
                            sw.cancel(true);
                        }
                        sw = new SwingWorker() {
                            @Override
                            protected Object doInBackground() throws Exception {
                                ShareLoader.getLoader().refreshModule();
                                elCaseBindInfoList = ShareLoader.getLoader().getAllBindInfoList();
                                refreshComboxData();
                                refreshDownPanel(false);
                                return null;
                            }
                        };
                        sw.execute();
                    }
                }
        );
    }

    private void refreshComboxData() {
        comboBox.setSelectedIndex(0);
        comboBox.setModel(new DefaultComboBoxModel(getFormCategories()));
    }

    /**
     * 创建下载模板的按钮
     */
    private UIButton createDownloadButton() {
        UIButton downloadButton = new UIButton();
        downloadButton.setIcon(BaseUtils.readIcon("/com/fr/design/form/images/download icon.png"));
        downloadButton.set4ToolbarButton();
        downloadButton.setToolTipText(Inter.getLocText("FR-Designer_Download_Template"));
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String url = SiteCenter.getInstance().acquireUrlByKind("reuse.url");
                if (StringUtils.isEmpty(url)) {
                    FRContext.getLogger().info("The URL is empty!");
                    return;
                }
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException exp) {
                    JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Designer_Set_default_browser"));
                    FRContext.getLogger().errorWithServerLevel(exp.getMessage(), exp);
                } catch (URISyntaxException exp) {
                    FRContext.getLogger().errorWithServerLevel(exp.getMessage(), exp);
                } catch (Exception exp) {
                    FRContext.getLogger().errorWithServerLevel(exp.getMessage(), exp);
                    FRContext.getLogger().error("Can not open the browser for URL:  " + url);
                }
            }
        });
        return downloadButton;
    }

    /**
     * 创建安装模板的按钮
     */
    private UIButton createInstallButton() {
        return createToolButton(
                BaseUtils.readIcon("/com/fr/design/form/images/install icon.png"),
                Inter.getLocText("FR-Designer_Install_Template"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                        fileChooser.setFileFilter(new FileNameExtensionFilter(".reu", "reu"));
                        int returnValue = fileChooser.showDialog(new UILabel(), Inter.getLocText("FR-Designer_Select"));
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            final File chosenFile = fileChooser.getSelectedFile();
                            installFromDiskZipFile(chosenFile);
                        }
                    }
                }
        );
    }

    /**
     * 创建删除模板的按钮
     */
    private UIButton createDeleteButton() {
        return createToolButton(
                BaseUtils.readIcon("/com/fr/design/form/images/delete icon.png"),
                Inter.getLocText("FR-Designer_Delete_Template"),
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        replaceButtonPanel(true);
                    }
                }
        );
    }

    private void replaceButtonPanel(boolean isEdit) {
        this.isEdit = isEdit;
        if (isEdit) {
            menutPanelNorthPane.remove(editPanel);
            menutPanelNorthPane.add(initResetButtonPane(), BorderLayout.EAST);
        } else {
            menutPanelNorthPane.remove(resetPanel);
            menutPanelNorthPane.add(initEditButtonPane(), BorderLayout.EAST);
            ShareLoader.getLoader().resetRemovedModuleList();
        }
    }

    private void installFromDiskZipFile(File chosenFile) {
        if (chosenFile != null && chosenFile.getName().endsWith(ShareConstants.SUFFIX_MODULE)) {
            try {
                if (ShareLoader.getLoader().installModuleFromDiskZipFile(chosenFile)) {
                    refreshShareMoudule();
                    elCaseBindInfoList = ShareLoader.getLoader().getAllBindInfoList();
                    refreshDownPanel(false);
                    refreshComboxData();
                    JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Share_Module_OK"));
                } else {
                    JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Share_Module_Error"));
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Share_Module_Error"));
                FRLogger.getLogger().error(e.getMessage(), e);
            }
        }
    }

    private void refreshShareMoudule() {
        try {
            ShareLoader.getLoader().refreshModule();
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage(), e);
        }
    }

    /**
     * 获取报表块组件分类
     */
    public String[] getFormCategories() {
        return ArrayUtils.addAll(new String[] {Inter.getLocText("FR-Designer_AllCategories")}, ShareLoader.getLoader().getModuleCategory());
    }

    public void refreshDownPanel(boolean isEdit) {
        reuWidgetPanel.remove(downPane);
        downPane = new UIScrollPane(new ShareWidgetPane(elCaseBindInfoList, isEdit));
        reuWidgetPanel.add(downPane);
        repaintContainer();

    }

    public void repaintContainer() {
        validate();
        repaint();
        revalidate();
    }

    /**
     * 清除数据
     */
    public void clearDockingView() {
        JScrollPane psp = new JScrollPane();
        psp.setBorder(null);
        this.add(psp, BorderLayout.CENTER);
    }



    /**
     * 定位
     * @return  位置
     */
    public Location preferredLocation() {
        return Location.WEST_BELOW;
    }


}