package com.fr.design.extra;

import com.fr.base.FRContext;
import com.fr.design.DesignerEnvManager;
import com.fr.design.dialog.UIDialog;
import com.fr.design.extra.ucenter.Client;
import com.fr.design.extra.ucenter.XMLHelper;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.mainframe.DesignerContext;
import com.fr.general.SiteCenter;
import com.fr.general.http.HttpClient;
import com.fr.json.JSONObject;
import com.fr.stable.EncodeConstants;
import com.fr.stable.StringUtils;
import javafx.scene.web.WebEngine;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import javax.swing.*;
import java.awt.*;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;

public class LoginWebBridge {

    //默认查询消息时间, 30s
    private static final long CHECK_MESSAGE_TIME = 30 * 1000L;
    //数据查询正常的标志 ok
    private static final String SUCCESS_MESSAGE_STATUS = "ok";
    //数据通讯失败
    private static final String FAILED_MESSAGE_STATUS = "error";
    //消息条数
    private int messageCount;
    //最低消息的条数
    private static final int MIN_MESSAGE_COUNT = 0;
    //登录成功
    private static final String LOGININ = "0";
    //用户名不存在
    private static final String USERNAME_NOT_EXSIT = "-1";
    //密码错误
    private static final String PASSWORD_ERROR = "-2";
    //未知错误
    private static final String UNKNOWN_ERROR = "-3";
    //网络连接失败
    private static final String NET_FAILED = "-4";
    //用户名，密码为空
    private static final String LOGIN_INFO_EMPTY = "-5";
    private static final int TIME_OUT = 10000;

    private static com.fr.design.extra.LoginWebBridge helper;
    private UIDialog uiDialog;
    private UILabel uiLabel;
    private String userName;

    public int getMessageCount() {
        return messageCount;
    }

    /**
     * 测试论坛网络连接
     * @return
     */
    private boolean testConnection() {
        HttpClient client = new HttpClient(SiteCenter.getInstance().acquireUrlByKind("bbs.test"));
        return client.isServerAlive();
    }

    public static com.fr.design.extra.LoginWebBridge getHelper() {
        if (helper != null) {
                return helper;
        }
        synchronized (com.fr.design.extra.LoginWebBridge.class) {
            if (helper == null) {
                    helper = new com.fr.design.extra.LoginWebBridge();
            }
            return helper;
        }
    }

    public static com.fr.design.extra.LoginWebBridge getHelper(WebEngine webEngine) {
        getHelper();
        helper.setEngine(webEngine);
        return helper;
    }

    private WebEngine webEngine;

    public void setEngine(WebEngine webEngine) {
        this.webEngine = webEngine;
    }

    public void setDialogHandle(UIDialog uiDialog) {
        this.uiDialog = uiDialog;
    }

    public void setUILabel(UILabel uiLabel) {
        this.uiLabel = uiLabel;
    }

    public LoginWebBridge() {
        String username = DesignerEnvManager.getEnvManager().getBBSName();
        setUserName(username, uiLabel);
    }

    /**
     * 设置显示的用户名
     * @param userName 登录用户名
     * @param label label显示
     */
    public void setUserName(String userName, UILabel label) {
        if (uiLabel == null) {
            this.uiLabel = label;
        }
        if(StringUtils.isEmpty(userName)){
            return;
        }
        if(!StringUtils.isEmpty(this.userName)){
            updateMessageCount();
        }
        DesignerEnvManager.getEnvManager().setBBSName(userName);
        this.userName = userName;
    }

    /**
     * 定时取后台论坛消息
     */
    private void updateMessageCount(){
        //启动获取消息更新的线程
        //登陆状态, 根据存起来的用户名密码, 每1分钟发起一次请求, 更新消息条数.
        Thread updateMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                sleep(CHECK_MESSAGE_TIME);
                while(StringUtils.isNotEmpty(DesignerEnvManager.getEnvManager().getBBSName())){
                    HashMap<String, String> para = new HashMap<>();
                    int uid = DesignerEnvManager.getEnvManager().getBbsUid();
                    para.put("uid", String.valueOf(uid));
                    HttpClient getMessage = new HttpClient(SiteCenter.getInstance().acquireUrlByKind("bbs.message"), para);
                    getMessage.asGet();
                    if(getMessage.isServerAlive()){
                        try {
                            String res = getMessage.getResponseText();
                            if (res.equals(FAILED_MESSAGE_STATUS)) {
                            }else {
                                JSONObject jo = new JSONObject(res);
                                if (jo.getString("status").equals(SUCCESS_MESSAGE_STATUS)) {
                                    setMessageCount(Integer.parseInt(jo.getString("message")));
                                }
                            }
                        } catch (Exception e) {
                            FRContext.getLogger().info(e.getMessage());
                        }
                    }
                    sleep(CHECK_MESSAGE_TIME);
                }
            }
        });
        updateMessageThread.start();
    }

    /**
     * 设置获取的消息长度，并设置显示
     * @param count
     */
    public void setMessageCount(int count) {
        if (count == MIN_MESSAGE_COUNT) {
            uiLabel.setText(DesignerEnvManager.getEnvManager().getBBSName());
            DesignerEnvManager.getEnvManager().setInShowBBsName(DesignerEnvManager.getEnvManager().getBBSName());
            return;
        }
        this.messageCount = count;
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.BLANK).append(this.userName)
                .append("(").append(this.messageCount)
                .append(")").append(StringUtils.BLANK);
        DesignerEnvManager.getEnvManager().setInShowBBsName(sb.toString());
        uiLabel.setText(sb.toString());
    }

    private String encode(String str){
        try {
            return URLEncoder.encode(str, EncodeConstants.ENCODING_UTF_8);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            FRContext.getLogger().error(e.getMessage());
        }
    }

    /**
     * 注册页面
     */
    public void registerHref() {
        try {
            Desktop.getDesktop().browse(new URI(SiteCenter.getInstance().acquireUrlByKind("bbs.register")));
        }catch (Exception e) {
            FRContext.getLogger().info(e.getMessage());
        }
    }

    /**
     * 忘记密码
     */
    public void forgetHref() {
        try {
            Desktop.getDesktop().browse(new URI(SiteCenter.getInstance().acquireUrlByKind("bbs.default")));
        }catch (Exception e) {
            FRContext.getLogger().info(e.getMessage());
        }
    }

    /**
     * 设计器端的用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录信息标志
     */
    public String defaultLogin(String username, String password) {
        return login(username, password, uiLabel);
    }

    /**
     * 插件管理的用户登录部分
     * @param username 用户名
     * @param password 密码
     * @param uiLabel 设计器端的label
     * @return 登录信息标志
     */
    public String pluginManageLogin(String username, String password, UILabel uiLabel) {
        return login(username, password, uiLabel);
    }

    /**
     * 登录操作
     * @param username 用户名
     * @param password 密码
     * @param uiLabel 两边的label显示
     * @return 登录信息标志
     */
    private String login(String username, String password, UILabel uiLabel) {
        if (!StringUtils.isNotBlank(username) && !StringUtils.isNotBlank(password)) {
            return LOGIN_INFO_EMPTY;
        }
        if (!testConnection()) {
            return NET_FAILED;
        }
        String loginResult = login(username, password);
        if (loginResult.equals(LOGININ)) {
            updateUserInfo(username, password);
            loginSuccess(username, uiLabel);
            setUserName(username, uiLabel);
        }
        return loginResult;
    }

    /**
     * 关闭窗口
     */
    public void closeWindow() {
        if (uiDialog != null) {
            uiDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            uiDialog.setVisible(false);
            uiDialog.dispose();
        }
    }

    /**
     * 更新后台的用户信息
     * @param username 用户名
     * @param password 密码
     */
    public void updateUserInfo(String username,String password) {
        DesignerEnvManager.getEnvManager().setBBSName(username);
        DesignerEnvManager.getEnvManager().setBBSPassword(password);
        DesignerEnvManager.getEnvManager().setInShowBBsName(username);
        this.userName = username;
    }

    /**
     * 关闭窗口并且重新赋值
     * @param username
     */
    public void loginSuccess(String username, UILabel uiLabel) {
        closeWindow();
        uiLabel.setText(username);
    }

    /**
     * 弹出QQ授权页面
     */
    public void showQQ() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //弹出qq登录的窗口
                QQLoginPane managerPane = new QQLoginPane();
                UIDialog qqlog = new QQLoginDialog(DesignerContext.getDesignerFrame(),managerPane);
                QQLoginWebBridge.getHelper().setDialogHandle(uiDialog);
                QQLoginWebBridge.getHelper().setQQDialogHandle(qqlog);
                QQLoginWebBridge.getHelper().setUILabel(uiLabel);
                qqlog.setVisible(true);
            }
        });
    }

    public String login(String username, String password) {
        try {
            Client uc = new Client();
            String result = uc.uc_user_login(username, password);
            result = new String(result.getBytes("iso-8859-1"), "gbk");
            LinkedList<String> list = XMLHelper.uc_unserialize(result);
            if (list.size() > 0) {
                int $uid = Integer.parseInt(list.get(0));
                if ($uid > 0) {
                    DesignerEnvManager.getEnvManager().setBbsUid($uid);
                    return LOGININ;//登录成功，0
                } else if ($uid == -1) {
                    return USERNAME_NOT_EXSIT;//用户名不存在，-1
                } else if ($uid == -2) {
                    return PASSWORD_ERROR;//密码错误，-2
                } else {
                    return UNKNOWN_ERROR;//未知错误，-3
                }
            }else {
                return NET_FAILED;
            }
        }catch (Exception e) {
            FRContext.getLogger().info(e.getMessage());
        }
        return UNKNOWN_ERROR;
    }
}