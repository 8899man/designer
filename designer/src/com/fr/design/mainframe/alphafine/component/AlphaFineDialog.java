package com.fr.design.mainframe.alphafine.component;

import com.fr.base.ConfigManager;
import com.fr.base.FRContext;
import com.fr.design.DesignerEnvManager;
import com.fr.design.dialog.UIDialog;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.mainframe.alphafine.AlphaFineConstants;
import com.fr.design.mainframe.alphafine.AlphaFineHelper;
import com.fr.design.mainframe.alphafine.cell.CellModelHelper;
import com.fr.design.mainframe.alphafine.cell.model.AlphaCellModel;
import com.fr.design.mainframe.alphafine.cell.model.FileModel;
import com.fr.design.mainframe.alphafine.cell.model.MoreModel;
import com.fr.design.mainframe.alphafine.cell.model.PluginModel;
import com.fr.design.mainframe.alphafine.cell.render.ContentCellRender;
import com.fr.design.mainframe.alphafine.listener.DocumentAdapter;
import com.fr.design.mainframe.alphafine.model.SearchResult;
import com.fr.design.mainframe.alphafine.preview.DocumentPreviewPane;
import com.fr.design.mainframe.alphafine.preview.FilePreviewPane;
import com.fr.design.mainframe.alphafine.preview.NoResultPane;
import com.fr.design.mainframe.alphafine.preview.PluginPreviewPane;
import com.fr.design.mainframe.alphafine.search.manager.*;
import com.fr.form.main.Form;
import com.fr.form.main.FormIO;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.IOUtils;
import com.fr.general.Inter;
import com.fr.general.http.HttpClient;
import com.fr.io.TemplateWorkBookIO;
import com.fr.io.exporter.ImageExporter;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.main.impl.WorkBook;
import com.fr.stable.CodeUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.ExecutionException;

/**
 * Created by XiaXiang on 2017/3/21.
 */
public class AlphaFineDialog extends UIDialog {
    private static final String ADVANCED_SEARCH_MARK = "k:";
    private static final String ACTION_MARK_SHORT = "k:1 ";
    private static final String ACTION_MARK = "k:setting ";
    private static final String DOCUMENT_MARK_SHORT = "k:2 ";
    private static final String DOCUMENT_MARK = "k:help ";
    private static final String FILE_MARK_SHORT = "k:3 ";
    private static final String FILE_MARK = "k:reportlets ";
    private static final String CPT_MARK = "k:cpt ";
    private static final String FRM_MARK = "k:frm ";
    private static final String DS_MARK = "k:ds ";
    private static final String DS_NAME = "dsname=\"";
    private static final String PLUGIN_MARK_SHORT = "k:4 ";
    private static final String PLUGIN_MARK = "k:shop ";
    private static final String PLACE_HOLDER = Inter.getLocText("FR-Designer_AlphaFine");
    private static final int MAX_SHOW_SIZE = 12;

    private AlphaFineTextField searchTextField;
    private UIButton closeButton;
    private JPanel searchResultPane;
    private UIScrollPane leftSearchResultPane;

    private JPanel defaultPane;
    //分割线
    private UILabel splitLabel;
    private JPanel rightSearchResultPane;
    private AlphaFineList searchResultList;
    private SearchListModel searchListModel;
    private SwingWorker searchWorker;
    private SwingWorker showWorker;
    private String storeText;
    //是否强制打开，因为面板是否关闭绑定了全局鼠标事件，这里需要处理一下
    private boolean forceOpen;


    public AlphaFineDialog(Frame parent, boolean forceOpen) {
        super(parent);
        this.forceOpen = forceOpen;
        initProperties();
        initGlobalListener();
        initComponents();
    }

    /**
     * 全局快捷键
     *
     * @return
     */
    public static AWTEventListener listener() {
        return new AWTEventListener() {

            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof KeyEvent) {
                    KeyEvent e = (KeyEvent) event;
                    KeyStroke keyStroke = (KeyStroke) KeyStroke.getAWTKeyStrokeForEvent(e);
                    KeyStroke storeKeyStroke = DesignerEnvManager.getEnvManager().getAlphaFineConfigManager().getShortCutKeyStore();
                    if (ComparatorUtils.equals(keyStroke.toString(), storeKeyStroke.toString()) && AlphaFinePane.getAlphaFinePane().isVisible()) {
                        doClickAction();
                    }

                }
            }
        };
    }

    /**
     * 打开搜索框
     */
    private static void doClickAction() {
        AlphaFineHelper.showAlphaFineDialog(false);
    }

    /**
     * 初始化全部组件
     */
    private void initComponents() {
        initSearchTextField();
        JPanel topPane = new JPanel(new BorderLayout());
        UILabel iconLabel = new UILabel(new ImageIcon(getClass().getResource("/com/fr/design/mainframe/alphafine/images/bigsearch.png")));
        iconLabel.setPreferredSize(AlphaFineConstants.ICON_LABEL_SIZE);
        iconLabel.setOpaque(true);
        iconLabel.setBackground(Color.WHITE);
        topPane.add(iconLabel, BorderLayout.WEST);
        topPane.add(searchTextField, BorderLayout.CENTER);
        closeButton = new UIButton() {
            @Override
            public void paintComponent(Graphics g) {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, getSize().width, getSize().height);
                super.paintComponent(g);
            }
        };
        closeButton.setPreferredSize(AlphaFineConstants.CLOSE_BUTTON_SIZE);
        closeButton.setIcon(new ImageIcon(getClass().getResource("/com/fr/design/mainframe/alphafine/images/alphafine_close.png")));
        closeButton.set4ToolbarButton();
        closeButton.setBorderPainted(false);
        closeButton.setRolloverEnabled(false);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        topPane.add(closeButton, BorderLayout.EAST);
        add(topPane, BorderLayout.CENTER);
    }

    /**
     * 初始化输入框
     */
    private void initSearchTextField() {
        searchTextField = new AlphaFineTextField(PLACE_HOLDER);
        initTextFieldListener();
        searchTextField.setFont(AlphaFineConstants.GREATER_FONT);
        searchTextField.setBackground(Color.WHITE);
        searchTextField.setBorderPainted(false);
    }

    /**
     *
     */
    private void initProperties() {
        setUndecorated(true);
        //addComponentListener(new ComponentHandler());
        setSize(AlphaFineConstants.FIELD_SIZE);
        centerWindow(this);

    }

    /**
     * 设置面板位置
     *
     * @param win
     */
    private void centerWindow(Window win) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        Dimension winSize = win.getSize();

        if (winSize.height > screenSize.height) {
            winSize.height = screenSize.height;
        }
        if (winSize.width > screenSize.width) {
            winSize.width = screenSize.width;
        }
        //这里设置位置：水平居中，竖直偏上
        win.setLocation((screenSize.width - winSize.width) / 2, (screenSize.height - winSize.height) / AlphaFineConstants.SHOW_SIZE);
    }

    // TODO: 2017/5/8  xiaxiang: 窗体圆角setShape()有毛边，重写paint方法可以解决毛边问题，但带来了别的问题,处理比较麻烦，暂用setShape();
//    public void paint(Graphics g){
//        Graphics2D g2 = (Graphics2D) g.create();
//        RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//        g2.setRenderingHints(qualityHints);
//        g2.setPaint(Color.WHITE);
//        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
//        g2.dispose();
//    }

    /**
     * 执行搜索
     *
     * @param text
     */
    private void doSearch(String text) {

        if (isNeedSearch(text)) {
            removeSearchResult();
        } else {
            showSearchResult(text);
        }
    }

    boolean isNeedSearch(String text) {
        return ComparatorUtils.equals(PLACE_HOLDER, text) || text.contains("'") || StringUtils.isBlank(text);
    }

    @Override
    public void setVisible(boolean isVisible) {
        if (!isVisible) {
            dispose();
            return;
        }
        super.setVisible(isVisible);
    }

    @Override
    public void dispose() {
        resetDialog();
        super.dispose();
    }

    /**
     * 重置搜索框
     */
    private void resetDialog() {
        removeSearchResult();
        searchTextField.setText(null);
    }

    /**
     * 移除搜索结果
     */
    private void removeSearchResult() {
        if (searchResultPane != null) {
            remove(searchResultPane);
            searchResultPane = null;
        }
        setSize(AlphaFineConstants.FIELD_SIZE);
       refreshContainer();
    }

    /**
     * 展示搜索结果
     */
    private void showSearchResult(String text) {
        if (searchResultPane == null) {
            initSearchResultComponents();
        }
        initSearchWorker(text);
    }

    /**
     * 初始化搜索面板
     */
    private void initSearchResultComponents() {
        searchResultList = new AlphaFineList();
        searchResultList.setFixedCellHeight(AlphaFineConstants.CELL_HEIGHT);
        searchListModel = new SearchListModel(new SearchResult());
        searchResultList.setModel(searchListModel);
        searchResultPane = new JPanel();
        searchResultPane.setPreferredSize(AlphaFineConstants.CONTENT_SIZE);
        searchResultPane.setLayout(new BorderLayout());
        searchResultList.setCellRenderer(new ContentCellRender());

        leftSearchResultPane = new UIScrollPane(searchResultList);
        leftSearchResultPane.setBackground(Color.WHITE);
        leftSearchResultPane.setPreferredSize(new Dimension(AlphaFineConstants.LEFT_WIDTH, AlphaFineConstants.CONTENT_HEIGHT));
        rightSearchResultPane = new JPanel();
        rightSearchResultPane.setBackground(Color.WHITE);
        rightSearchResultPane.setPreferredSize(new Dimension(AlphaFineConstants.RIGHT_WIDTH - 1, AlphaFineConstants.CONTENT_HEIGHT));
        searchResultPane.add(leftSearchResultPane, BorderLayout.WEST);
        searchResultPane.add(rightSearchResultPane, BorderLayout.EAST);
        splitLabel = new UILabel();
        splitLabel.setPreferredSize(new Dimension(AlphaFineConstants.HEIGHT, 1));
        searchResultPane.add(splitLabel, BorderLayout.NORTH);
        add(searchResultPane, BorderLayout.SOUTH);
        setSize(AlphaFineConstants.FULL_SIZE);
    }

    /**
     * 异步加载搜索结果
     */
    private void initSearchWorker(final String text) {
        if (this.searchWorker != null && !this.searchWorker.isDone()) {
            this.searchWorker.cancel(true);
            this.searchWorker = null;
        }
        this.searchWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                rebuildList(searchTextField.getText().toLowerCase());
                return null;
            }

            @Override
            protected void done() {
                if (!isCancelled()) {
                    splitLabel.setIcon(null);
                    fireStopLoading();
                }
            }
        };
        this.searchWorker.execute();
    }

    /**
     * 恢复左侧列表面板
     */
    private void resumeLeftPane() {
        if (searchResultPane != null && defaultPane != null) {
            searchResultPane.remove(defaultPane);
            defaultPane = null;
            searchResultPane.add(leftSearchResultPane, BorderLayout.WEST);
        }
    }

    /**
     * 移除左侧列表面板
     */
    private void removeLeftPane() {
        if (searchListModel.isEmpty() && defaultPane == null) {
            defaultPane = new NoResultPane(Inter.getLocText("FR-Designer-AlphaFine_NO_Result"), IOUtils.readIcon("/com/fr/design/mainframe/alphafine/images/no_result.png"));
            searchResultPane.remove(leftSearchResultPane);
            searchResultPane.add(defaultPane, BorderLayout.WEST);
            refreshContainer();
        }
    }

    /**
     * 停止加载状态
     */
    private void fireStopLoading() {
        searchListModel.resetState();
        if (searchResultPane != null) {
            removeLeftPane();
        }
    }

    /**
     * 刷新容器
     */
    private void refreshContainer() {
        validate();
        repaint();
        revalidate();
    }

    /**
     * 重新构建搜索结果列表
     * 先根据输入判断是不是隐藏的搜索功能
     *
     * @param searchText
     */
    private void rebuildList(String searchText) {
        resetContainer();
        if (searchText.startsWith(ADVANCED_SEARCH_MARK)) {
            if (searchText.startsWith(ACTION_MARK_SHORT) || searchText.startsWith(ACTION_MARK)) {
                storeText = searchText.substring(searchText.indexOf(StringUtils.BLANK) + 1, searchText.length());
                buildActionList(storeText);
            } else if (searchText.startsWith(DOCUMENT_MARK_SHORT) || searchText.startsWith(DOCUMENT_MARK)) {
                storeText = searchText.substring(searchText.indexOf(StringUtils.BLANK) + 1, searchText.length());
                buildDocumentList(storeText);
            } else if (searchText.startsWith(FILE_MARK_SHORT) || searchText.startsWith(FILE_MARK)) {
                storeText = searchText.substring(searchText.indexOf(StringUtils.BLANK) + 1, searchText.length());
                buildFileList(storeText);
            } else if (searchText.startsWith(CPT_MARK) || searchText.startsWith(FRM_MARK)) {
                storeText = searchText.substring(searchText.indexOf(StringUtils.BLANK) + 1, searchText.length());
                buildFileList(searchText);
            } else if (searchText.startsWith(DS_MARK)) {
                storeText = searchText.substring(searchText.indexOf(StringUtils.BLANK) + 1, searchText.length());
                buildFileList(DS_NAME + storeText);
            } else if (searchText.startsWith(PLUGIN_MARK_SHORT) || searchText.startsWith(PLUGIN_MARK)) {
                storeText = searchText.substring(searchText.indexOf(StringUtils.BLANK) + 1, searchText.length());
                buildPluginList(storeText);
            }
        } else {
            storeText = searchText.trim();
            doNormalSearch(storeText);
        }

    }

    /**
     * 重置面板
     */
    private void resetContainer() {
        rightSearchResultPane.removeAll();
        splitLabel.setIcon(new ImageIcon(getClass().getResource("/com/fr/design/mainframe/alphafine/images/bigloading.gif")));
        resumeLeftPane();
        searchListModel.removeAllElements();
        searchListModel.resetSelectedState();
        refreshContainer();
    }

    /**
     * 普通搜索
     *
     * @param searchText
     */
    private void doNormalSearch(String searchText) {
        buildRecentList(searchText);
        buildRecommendList(searchText);
        buildActionList(searchText);
        buildFileList(searchText);
        buildDocumentList(searchText);
        buildPluginList(searchText);
    }

    private void buildDocumentList(final String searchText) {
        SearchResult documentModelList = DocumentSearchManager.getDocumentSearchManager().getLessSearchResult(searchText);
        for (AlphaCellModel object : documentModelList) {
            searchListModel.addElement(object);
        }
    }

    private void buildFileList(final String searchText) {
        SearchResult fileModelList = FileSearchManager.getFileSearchManager().getLessSearchResult(searchText);
        for (AlphaCellModel object : fileModelList) {
            searchListModel.addElement(object);
        }
    }

    private void buildActionList(final String searchText) {
        SearchResult actionModelList = ActionSearchManager.getActionSearchManager().getLessSearchResult(searchText);
        for (AlphaCellModel object : actionModelList) {
            searchListModel.addElement(object);
        }
    }

    private void buildPluginList(final String searchText) {
        SearchResult pluginModelList = PluginSearchManager.getPluginSearchManager().getLessSearchResult(searchText);
        for (AlphaCellModel object : pluginModelList) {
            searchListModel.addElement(object);
        }
    }

    private void buildRecommendList(final String searchText) {
        SearchResult recommendModelList = RecommendSearchManager.getRecommendSearchManager().getLessSearchResult(searchText);
        for (AlphaCellModel object : recommendModelList) {
            searchListModel.addElement(object);
        }
    }

    private void buildRecentList(final String searchText) {
        SearchResult recentModelList = RecentSearchManager.getRecentSearchManger().getLessSearchResult(searchText);
        for (AlphaCellModel object : recentModelList) {
            searchListModel.addElement(object);
        }

    }

    /**
     * 右侧面板展示搜索结果的内容
     *
     * @param selectedValue
     */
    private void showResult(final AlphaCellModel selectedValue) {
        switch (selectedValue.getType()) {
            case FILE:
                final String fileName = ((FileModel) selectedValue).getFilePath().substring(ProjectConstants.REPORTLETS_NAME.length() + 1);
                showDefaultPreviewPane();
                if (fileName.endsWith(ProjectConstants.FRM_SUFFIX)) {
                    checkWorker();
                    this.showWorker = new SwingWorker<BufferedImage, Void>() {
                        @Override
                        protected BufferedImage doInBackground() {
                            Form form = null;
                            try {
                                form = FormIO.readForm(FRContext.getCurrentEnv(), fileName);
                            } catch (Exception e) {
                                FRLogger.getLogger().error(e.getMessage());
                            }
                            return FormIO.exportFormAsImage(form);
                        }

                        @Override
                        protected void done() {
                            if (!isCancelled()) {
                                rightSearchResultPane.removeAll();
                                try {
                                    rightSearchResultPane.add(new FilePreviewPane(get()));
                                } catch (InterruptedException e) {
                                    FRLogger.getLogger().error(e.getMessage());
                                } catch (ExecutionException e) {
                                    FRLogger.getLogger().error(e.getMessage());
                                }
                                validate();
                                repaint();
                            }

                        }
                    };
                    this.showWorker.execute();
                } else if (fileName.endsWith(ProjectConstants.CPT_SUFFIX)) {
                    checkWorker();
                    this.showWorker = new SwingWorker<BufferedImage, Void>() {
                        @Override
                        protected BufferedImage doInBackground() {
                            WorkBook workBook = null;
                            try {
                                workBook = (WorkBook) TemplateWorkBookIO.readTemplateWorkBook(FRContext.getCurrentEnv(), fileName);
                            } catch (Exception e) {
                                FRLogger.getLogger().error(e.getMessage());
                            }
                            BufferedImage bufferedImage = new ImageExporter().exportToImage(workBook);
                            return bufferedImage;
                        }

                        @Override
                        protected void done() {
                            if (!isCancelled()) {
                                rightSearchResultPane.removeAll();
                                try {
                                    rightSearchResultPane.add(new FilePreviewPane(get()));
                                    validate();
                                    repaint();
                                } catch (InterruptedException e) {
                                    FRLogger.getLogger().error(e.getMessage());
                                } catch (ExecutionException e) {
                                    FRLogger.getLogger().error(e.getMessage());
                                }
                            }

                        }
                    };
                    this.showWorker.execute();
                }
                break;
            case ACTION:
                rightSearchResultPane.removeAll();
                rightSearchResultPane.add(new NoResultPane(Inter.getLocText("FR-Designer_NoResult"), IOUtils.readIcon("/com/fr/design/mainframe/alphafine/images/noresult.png")));
                validate();
                repaint();
                break;
            case DOCUMENT:
                rightSearchResultPane.removeAll();
                rightSearchResultPane.add(new DocumentPreviewPane((selectedValue).getName(), (selectedValue).getContent()));
                validate();
                repaint();
                break;
            case PLUGIN:
            case REUSE:
                showDefaultPreviewPane();
                checkWorker();
                this.showWorker = new SwingWorker<Image, Void>() {
                    @Override
                    protected Image doInBackground() {
                        BufferedImage bufferedImage = null;
                        try {
                            bufferedImage = ImageIO.read(new URL(((PluginModel) selectedValue).getImageUrl()));
                        } catch (IOException e) {
                            try {
                                bufferedImage = ImageIO.read(getClass().getResource("/com/fr/design/mainframe/alphafine/images/default_product.png"));
                            } catch (IOException e1) {
                                FRLogger.getLogger().error(e.getMessage());
                            }
                        }
                        return bufferedImage;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!isCancelled()) {
                                rightSearchResultPane.removeAll();
                                rightSearchResultPane.add(new PluginPreviewPane((selectedValue).getName(), get(), ((PluginModel) selectedValue).getVersion(), ((PluginModel) selectedValue).getJartime(), ((PluginModel) selectedValue).getType(), ((PluginModel) selectedValue).getPrice()));
                                validate();
                                repaint();
                            }
                        } catch (InterruptedException e) {
                            FRLogger.getLogger().error(e.getMessage());
                        } catch (ExecutionException e) {
                            FRLogger.getLogger().error(e.getMessage());
                        }

                    }
                };
                this.showWorker.execute();
                break;
            default:
                rightSearchResultPane.removeAll();

        }

    }

    /**
     * 检查
     */
    private void checkWorker() {
        if (this.showWorker != null && !this.showWorker.isDone()) {
            this.showWorker.cancel(true);
            this.showWorker = null;
        }
    }

    private void dealWithMoreOrLessResult(int index, MoreModel selectedValue) {
        if (ComparatorUtils.equals(Inter.getLocText("FR-Designer_AlphaFine_ShowAll"), selectedValue.getContent())) {
            selectedValue.setContent(Inter.getLocText("FR-Designer_AlphaFine_ShowLess"));
            rebuildShowMoreList(index, selectedValue);
        } else {
            selectedValue.setContent(Inter.getLocText("FR-Designer_AlphaFine_ShowAll"));
            rebuildShowMoreList(index, selectedValue);
        }
    }

    private void showDefaultPreviewPane() {
        rightSearchResultPane.removeAll();
        UILabel label = new UILabel(new ImageIcon(getClass().getResource("/com/fr/design/mainframe/alphafine/images/opening.gif")));
        label.setBorder(BorderFactory.createEmptyBorder(120, 0, 0, 0));
        rightSearchResultPane.add(label, BorderLayout.CENTER);
        refreshContainer();
    }

    /**
     * 为面板添加全局监听器
     */
    private void initGlobalListener() {
        initAWTEventListener();
    }

    /**
     * 为textfield添加监听器
     */
    private void initTextFieldListener() {
        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    searchResultList.requestFocus();
                    searchResultList.setSelectedIndex(searchResultList.getSelectedIndex() + 1);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_ESCAPE) {
                    if (StringUtils.isBlank(searchTextField.getText()) || ComparatorUtils.equals(searchTextField.getText(), searchTextField.getPlaceHolder())) {
                        AlphaFineDialog.this.setVisible(false);
                    } else {
                        searchTextField.setText(null);
                        removeSearchResult();
                    }
                } else if (keyCode == KeyEvent.VK_SHIFT) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            doSearch(searchTextField.getText());
                        }
                    }, 50);
                } else if (keyCode == KeyEvent.VK_UP) {
                    return;
                } else {
                    doSearch(searchTextField.getText());

                }
            }
        });



    }

    /**
     * 当鼠标在搜索界面边界外点击时触发
     */
    private void initAWTEventListener() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!AlphaFineDialog.this.isVisible()) {
                    return;
                }
                if (event instanceof MouseEvent) {
                    MouseEvent k = (MouseEvent) event;
                    if (SwingUtilities.isLeftMouseButton(k)) {
                        Point p = k.getLocationOnScreen();
                        Rectangle dialogRectangle = AlphaFineDialog.this.getBounds();
                        Rectangle paneRectangle = new Rectangle(AlphaFinePane.getAlphaFinePane().getLocationOnScreen(), AlphaFinePane.getAlphaFinePane().getSize());
                        if (!dialogRectangle.contains(p) && !paneRectangle.contains(p) && !forceOpen) {
                            AlphaFineDialog.this.dispose();
                            forceOpen = false;
                        }
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.KEY_EVENT_MASK);
    }

    @Override
    public void checkValid() throws Exception {
        //不处理
    }

    private void doNavigate() {
        AlphaFineDialog.this.dispose();
        final AlphaCellModel model = searchResultList.getSelectedValue();
        model.doAction();
    }

    /**
     * 保存本地（本地常用）
     *
     * @param cellModel
     */
    private void saveHistory(AlphaCellModel cellModel) {
        RecentSearchManager recentSearchManager = RecentSearchManager.getRecentSearchManger();
        recentSearchManager.addRecentModel(storeText, cellModel);
        recentSearchManager.saveXMLFile();
        sendToServer(storeText, cellModel);

    }

    /**
     * 上传数据到服务器
     *
     * @param searchKey
     * @param cellModel
     */
    private void sendToServer(String searchKey, AlphaCellModel cellModel) {
        if (cellModel.isNeedToSendToServer()) {
            String username = ConfigManager.getProviderInstance().getBbsUsername();
            String uuid = DesignerEnvManager.getEnvManager().getUUID();
            String activityKey = DesignerEnvManager.getEnvManager().getActivationKey();
            String createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(Calendar.getInstance().getTime());
            String key = searchKey;
            int resultKind = cellModel.getType().getTypeValue();
            String resultValue = CellModelHelper.getResultValueFromModel(cellModel);
            JSONObject object = JSONObject.create();
            try {
                object.put("uuid", uuid).put("activityKey", activityKey).put("username", username).put("createTime", createTime).put("key", key).put("resultKind", resultKind).put("resultValue", resultValue);
            } catch (JSONException e) {
                FRLogger.getLogger().error(e.getMessage());
            }
            final HashMap<String, String> para = new HashMap<>();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
            para.put("token", CodeUtils.md5Encode(date, StringUtils.EMPTY, "MD5"));
            para.put("content", object.toString());
            HttpClient httpClient = new HttpClient(AlphaFineConstants.CLOUD_SERVER_URL, para, true);
            httpClient.asGet();
            if (!httpClient.isServerAlive()) {
                FRLogger.getLogger().error("Failed to sent data to server!");
            }
        }


    }

    /**
     * 点击显示更多时，添加对应的model到list；点击收起是移除model
     *
     * @param index
     * @param selectedValue
     */
    private void rebuildShowMoreList(int index, MoreModel selectedValue) {
        SearchResult moreResult = getMoreResult(selectedValue);
        if ((selectedValue).getContent().equals(Inter.getLocText("FR-Designer_AlphaFine_ShowLess"))) {
            for (int i = 0; i < moreResult.size(); i++) {
                this.searchListModel.add(index + AlphaFineConstants.SHOW_SIZE + 1 + i, moreResult.get(i));
            }
        } else {
            for (int i = 0; i < moreResult.size(); i++) {
                this.searchListModel.remove(index + AlphaFineConstants.SHOW_SIZE + 1);

            }
        }
    }

    private SearchResult getMoreResult(MoreModel selectedValue) {
        SearchResult moreResult;
        switch (selectedValue.getContentType()) {
            case PLUGIN:
                moreResult = PluginSearchManager.getPluginSearchManager().getMoreSearchResult();
                break;
            case DOCUMENT:
                moreResult = DocumentSearchManager.getDocumentSearchManager().getMoreSearchResult();
                break;
            case FILE:
                moreResult = FileSearchManager.getFileSearchManager().getMoreSearchResult();
                break;
            case ACTION:
                moreResult = ActionSearchManager.getActionSearchManager().getMoreSearchResult();
                break;
            default:
                moreResult = new SearchResult();
        }
        return moreResult;
    }

    private SearchListModel getModel() {
        return (SearchListModel) searchResultList.getModel();
    }

    public SearchListModel setListModel(SearchListModel jListModel) {
        this.searchListModel = jListModel;
        return this.searchListModel;
    }

    public SwingWorker getSearchWorker() {
        return searchWorker;
    }

    public void setSearchWorker(SwingWorker searchWorker) {
        this.searchWorker = searchWorker;
    }


    public boolean isForceOpen() {
        return forceOpen;
    }

    public void setForceOpen(boolean forceOpen) {
        this.forceOpen = forceOpen;
    }

    public String getStoreText() {
        return storeText;
    }

    public void setStoreText(String storeText) {
        this.storeText = storeText;
    }

    public UILabel getSplitLabel() {
        return splitLabel;
    }

    public void setSplitLabel(UILabel splitLabel) {
        this.splitLabel = splitLabel;
    }


    /**
     * +-------------------------------------+
     * |             自定义JList              |
     * +-------------------------------------+
     */
    private class AlphaFineList extends JList<AlphaCellModel> {

        public AlphaFineList() {
            initListListener();
        }

        /**
         * 重写选中的方法
         *
         * @param index
         */
        @Override
        public void setSelectedIndex(int index) {
            if (index > 0 && checkSelectedIndex(index)) {
                int previousIndex = getSelectedIndex();
                super.setSelectedIndex(index);
                AlphaCellModel cellModel = getSelectedValue();
                if (cellModel != null && !cellModel.hasAction()) {
                    if (previousIndex <= getSelectedIndex()) {
                        setSelectedIndex(index + 1);
                    } else {
                        setSelectedIndex(index - 1);
                    }

                }
            }
            showResult(getSelectedValue());
            ensureIndexIsVisible(getSelectedIndex());
        }

        private boolean checkSelectedIndex(int index) {
            int size = getModel().getSize();
            return size > 0 && index < size;
        }

        private void initListListener() {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        doNavigate();
                        saveHistory(getSelectedValue());
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (getSelectedIndex() == 1) {
                            searchTextField.requestFocus();
                        }
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        searchTextField.requestFocus();
                    }
                }
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int selectedIndex = getSelectedIndex();
                    AlphaCellModel selectedValue = getSelectedValue();
                    if (e.getClickCount() == 2 && selectedValue.hasAction()) {
                        doNavigate();
                        saveHistory(selectedValue);
                    } else if (e.getClickCount() == 1) {
                        if (selectedValue instanceof MoreModel && ((MoreModel) selectedValue).isNeedMore()) {
                            dealWithMoreOrLessResult(selectedIndex, (MoreModel) selectedValue);
                        }
                    }
                }
            });

            addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (!e.getValueIsAdjusting() && getSelectedValue() != null) {
                        showResult(getSelectedValue());
                    }
                }
            });
        }
    }

    /**
     * +-------------------------------------+
     * |           自定义ListModel            |
     * +-------------------------------------+
     */
    private class SearchListModel extends DefaultListModel<AlphaCellModel> {
        SearchResult myDelegate;

        /**
         * 第一有效的项是否被选中
         */
        private boolean isValidSelected;

        public SearchListModel(SearchResult searchResult) {
            this.myDelegate = searchResult;
        }

        @Override
        public void addElement(AlphaCellModel element) {
            int index = myDelegate.size();
            myDelegate.add(element);
            fireContentsChanged(this, index, index);
            fireSelectedStateChanged(element, index);

        }

        @Override
        protected void fireContentsChanged(Object source, int index0, int index1) {
            if (myDelegate.size() > MAX_SHOW_SIZE) {
                leftSearchResultPane.getVerticalScrollBar().setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
                leftSearchResultPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 2));
            } else {
                leftSearchResultPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }
            super.fireContentsChanged(source, index0, index1);
        }

        /**
         * 触发选中第一有效的项
         *
         * @param element
         * @param index
         */
        private void fireSelectedStateChanged(AlphaCellModel element, int index) {
            if (element.hasAction() && !isValidSelected()) {
                searchResultList.setSelectedIndex(index);
                setValidSelected(true);
            }
        }

        @Override
        public AlphaCellModel getElementAt(int index) {
            return myDelegate.get(index);
        }

        @Override
        public synchronized void add(int index, AlphaCellModel element) {
            myDelegate.add(index, element);
            fireIntervalAdded(this, index, index);
        }

        @Override
        public AlphaCellModel remove(int index) {
            AlphaCellModel object = myDelegate.get(index);
            myDelegate.remove(object);
            fireContentsChanged(this, index, index);
            return object;
        }

        @Override
        public int getSize() {
            return this.myDelegate.size();
        }

        @Override
        public void removeAllElements() {
            this.myDelegate.clear();
        }

        /**
         * 重置选中状态
         */
        public void resetSelectedState() {
            setValidSelected(false);
        }

        private boolean isValidSelected() {
            return isValidSelected;
        }

        private void setValidSelected(boolean selected) {
            isValidSelected = selected;
        }

        @Override
        public boolean isEmpty() {
            return myDelegate.isEmpty();
        }

        public void resetState() {
            for (int i = 0; i < getSize(); i++) {
                getElementAt(i).resetState();
            }
        }
    }

}