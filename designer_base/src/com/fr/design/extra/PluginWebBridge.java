package com.fr.design.extra;

import com.fr.base.FRContext;
import com.fr.design.DesignerEnvManager;
import com.fr.design.RestartHelper;
import com.fr.design.dialog.UIDialog;
import com.fr.design.extra.exe.*;
import com.fr.design.extra.exe.callback.JSCallback;
import com.fr.design.gui.ilable.UILabel;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.general.SiteCenter;
import com.fr.plugin.PluginLicense;
import com.fr.plugin.PluginLicenseManager;
import com.fr.plugin.context.PluginContext;
import com.fr.plugin.manage.PluginManager;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.List;

/**
 * 开放给Web组件的接口,用于安装,卸载,更新以及更改插件可用状态
 */
public class PluginWebBridge {

    private static PluginWebBridge helper;

    private UIDialog uiDialog;
    private ACTIONS action;
    private String ACTION = "action";
    private String KEYWORD = "keyword";
    private Map<String, Object> config;
    private WebEngine webEngine;

    private UILabel uiLabel;

    /**
     * 动作枚举
     */
    public enum ACTIONS {
        SEARCH("search");
        private String context;

        ACTIONS(String context) {
            this.context = context;
        }

        public String getContext() {
            return context;
        }
    }

    public static PluginWebBridge getHelper() {
        if (helper != null) {
            return helper;
        }
        synchronized (PluginWebBridge.class) {
            if (helper == null) {
                helper = new PluginWebBridge();
            }
            return helper;
        }
    }

    public static PluginWebBridge getHelper(WebEngine webEngine) {
        getHelper();
        helper.setEngine(webEngine);
        return helper;
    }

    private PluginWebBridge() {
    }

    /**
     * 获取打开动作配置
     *
     * @return 配置信息
     */
    public String getRunConfig() {
        if (action != null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ACTION, action.getContext());
            Set<String> keySet = config.keySet();
            for (String key : keySet) {
                jsonObject.put(key, config.get(key).toString());
            }
            return jsonObject.toString();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 配置打开动作
     *
     * @param action 动作
     * @param config 参数
     */
    public void setRunConfig(ACTIONS action, Map<String, Object> config) {
        this.action = action;
        this.config = config;
    }

    /**
     * 清楚打开动作
     */
    public void clearRunConfig() {
        this.action = null;
        this.config = null;
    }

    /**
     * 打开时搜索
     *
     * @param keyword 关键词
     */
    public void openWithSearch(String keyword) {
        HashMap<String, Object> map = new HashMap<String, Object>(2);
        map.put(KEYWORD, keyword);
        setRunConfig(ACTIONS.SEARCH, map);
    }

    public void setEngine(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void setDialogHandle(UIDialog uiDialog) {
        closeWindow();
        this.uiDialog = uiDialog;
    }

    /**
     * 从插件服务器上安装插件
     *
     * @param pluginInfo 插件的ID
     * @param callback 回调函数
     */
    public void installPluginOnline(final String pluginInfo, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.installPluginOnline(pluginInfo,jsCallback);
    }



    /**
     * 从磁盘上选择插件安装包进行安装
     *
     * @param filePath 插件包的路径
     */
    public void installPluginFromDisk(final String filePath, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.installPluginFromDisk(filePath,jsCallback);
    }

    /**
     * 卸载当前选中的插件
     *
     * @param pluginInfo 插件信息
     */
    public void uninstallPlugin(final String pluginInfo, final boolean isForce, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.uninstallPlugin(pluginInfo, isForce, jsCallback);
    }


    /**
     * 从插件服务器上更新选中的插件
     *
     * @param pluginIDs 插件集合
     */
    public void updatePluginOnline(JSObject pluginIDs, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.updatePluginOnline(pluginIDs, jsCallback);
    }

    /**
     * 从磁盘上选择插件安装包进行插件升级
     *
     * @param filePath 插件包的路径
     */
    public void updatePluginFromDisk(String filePath, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.updatePluginFromDisk(filePath, jsCallback);
    }

    /**
     * 修改选中的插件的活跃状态
     *
     * @param pluginID 插件ID
     */
    public void setPluginActive(String pluginID, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.setPluginActive(pluginID, jsCallback);
    }

    /**
     * 已安装插件检查更新
     */
    public void readUpdateOnline(final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.readUpdateOnline(jsCallback);
    }

    /**
     * 选择文件对话框
     *
     * @return 选择的文件的路径
     */
    public String showFileChooser() {
        return showFileChooserWithFilter(StringUtils.EMPTY, StringUtils.EMPTY);
    }

    /**
     * 选择文件对话框
     *
     * @param des    过滤文件描述
     * @param filter 文件的后缀
     * @return 选择的文件的路径
     * 这里换用JFileChooser会卡死,不知道为什么
     */
    public String showFileChooserWithFilter(String des, String filter) {
        FileChooser fileChooser = new FileChooser();

        if (StringUtils.isNotEmpty(filter)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(des, filter));
        }

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) {
            return null;
        }
        return selectedFile.getAbsolutePath();
    }

    /**
     * 选择文件对话框
     *
     * @param des  过滤文件描述
     * @param args 文件的后缀
     * @return 选择的文件的路径
     */
    public String showFileChooserWithFilters(String des, JSObject args) {
        FileChooser fileChooser = new FileChooser();
        String[] filters = jsObjectToStringArray(args);
        if (ArrayUtils.isNotEmpty(filters)) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(des, filters));
        }

        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile == null) {
            return null;
        }
        return selectedFile.getAbsolutePath();
    }

    /**
     * 获取已经安装的插件的数组
     *
     * @return 已安装的插件组成的数组
     */
    public PluginContext[] getInstalledPlugins() {
        List<PluginContext> plugins = PluginManager.getContexts();
        return plugins.toArray(new PluginContext[plugins.size()]);
    }


    /**
     * 获取已经安装的插件的授权情况
     *
     * @return 已安装的插件授权对象
     */
    public PluginLicense getPluginLicenseByID(String pluginID) {
        return PluginLicenseManager.getInstance().getPluginLicenseByID(pluginID);
    }

    private String[] jsObjectToStringArray(JSObject obj) {
        if (obj == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        int len = (int) obj.getMember("length");
        List<String> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            list.add(obj.getSlot(i).toString());
        }
        return list.toArray(new String[len]);
    }

    /**
     * 搜索在线插件
     *
     * @param keyword 关键字
     */
    public void searchPlugin(String keyword, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.searchPlugin(keyword, jsCallback);
    }

    /**
     * 根据条件获取在线插件的
     *
     * @param category 分类
     * @param seller   卖家性质
     * @param fee      收费类型
     * @param callback 回调函数
     */
    public void getPluginFromStore(String category, String seller, String fee, final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.getPluginFromStore(category, seller, fee, jsCallback);
    }

    /**
     * 在线获取插件分类
     *
     * @param callback 回调函数
     */
    public void getPluginCategories(final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.getPluginCategories(jsCallback);
    }

    /**
     * 展示一个重启的对话框(少用,莫名其妙会有bug)
     *
     * @param message 展示的消息
     */
    public void showRestartMessage(String message) {
        int rv = JOptionPane.showOptionDialog(
                null,
                message,
                Inter.getLocText("FR-Designer-Plugin_Warning"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{Inter.getLocText("FR-Designer-Basic_Restart_Designer"), Inter.getLocText("FR-Designer-Basic_Restart_Designer_Later")},
                null
        );
        if (rv == JOptionPane.OK_OPTION) {
            RestartHelper.restart();
        }
    }

    /**
     * 关闭窗口
     */
    public void closeWindow() {
        if (uiDialog != null) {
            uiDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            uiDialog.setVisible(false);
        }
    }

    /**
     * 窗口是否无装饰(判断是否使用系统标题栏)
     */
    public boolean isCustomTitleBar() {
        if (uiDialog != null) {
            return uiDialog.isUndecorated();
        }
        return false;
    }

    /**
     * 获取系统登录的用户名
     *
     * @param callback
     */
    public void getLoginInfo(final JSObject callback) {
        JSCallback jsCallback = new JSCallback(webEngine, callback);
        PluginOperateUtils.getLoginInfo(jsCallback);
    }

    /**
     * 打开论坛消息界面
     */
    public void getPriviteMessage() {
        try {
            String loginUrl = SiteCenter.getInstance().acquireUrlByKind("bbs.default");
            Desktop.getDesktop().browse(new URI(loginUrl));
        } catch (Exception exp) {
            FRContext.getLogger().info(exp.getMessage());
        }
    }

    /**
     * 打开登录页面
     */
    public void loginContent() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                UserLoginContext.fireLoginContextListener();
            }
        });
    }

    /**
     * 在本地浏览器里打开url
     * tips:重载的时候,需要给js调用的方法需要放在前面,否则可能不会被调用(此乃坑)
     * 所以最好的是不要重载在js可以访问的接口文件中
     *
     * @param url 要打开的地址
     */
    public void openShopUrlAtWebBrowser(String url) {
        openUrlAtLocalWebBrowser(webEngine, url);
    }

    /**
     * 在本地浏览器里打开url
     *
     * @param eng web引擎
     * @param url 要打开的地址
     */
    public void openUrlAtLocalWebBrowser(WebEngine eng, String url) {
        if (Desktop.isDesktopSupported()) {
            try {
                //创建一个URI实例,注意不是URL
                URI uri = URI.create(url);
                //获取当前系统桌面扩展
                Desktop desktop = Desktop.getDesktop();
                //判断系统桌面是否支持要执行的功能
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    //获取系统默认浏览器打开链接
                    desktop.browse(uri);
                }
            } catch (NullPointerException e) {
                //此为uri为空时抛出异常
                FRLogger.getLogger().error(e.getMessage());
            } catch (IOException e) {
                //此为无法获取系统默认浏览器
                FRLogger.getLogger().error(e.getMessage());
            }
        }
    }


    /*-------------------------------登录部分的处理----------------------------------*/

    /**
     * 注册页面
     */
    public void registerHref() {
        try {
            Desktop.getDesktop().browse(new URI(SiteCenter.getInstance().acquireUrlByKind("bbs.register")));
        } catch (Exception e) {
            FRContext.getLogger().info(e.getMessage());
        }
    }

    /**
     * 忘记密码
     */
    public void forgetHref() {
        try {
            Desktop.getDesktop().browse(new URI(SiteCenter.getInstance().acquireUrlByKind("bbs.reset")));
        } catch (Exception e) {
            FRContext.getLogger().info(e.getMessage());
        }
    }

    public void setUILabel(UILabel uiLabel) {
        this.uiLabel = uiLabel;
    }

    /**
     * 登录操作的回调
     *
     * @param username
     * @param password
     * @return
     */
    public String defaultLogin(String username, String password) {
        return LoginWebBridge.getHelper().pluginManageLogin(username, password, uiLabel);
    }

    /**
     * 弹出QQ授权页面
     */
    public void showQQ() {
        LoginWebBridge.getHelper().showQQ();
    }

    /**
     * 清除用户信息
     */
    public void clearUserInfo() {
        DesignerEnvManager.getEnvManager().setBBSName(StringUtils.EMPTY);
        DesignerEnvManager.getEnvManager().setBBSPassword(StringUtils.EMPTY);
        DesignerEnvManager.getEnvManager().setInShowBBsName(StringUtils.EMPTY);
        uiLabel.setText(Inter.getLocText("FR-Base_UnSignIn"));
    }
}
