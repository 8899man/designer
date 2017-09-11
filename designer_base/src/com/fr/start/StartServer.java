package com.fr.start;

import com.fr.base.ConfigManager;
import com.fr.base.FRContext;
import com.fr.design.DesignModelAdapter;
import com.fr.design.DesignerEnvManager;
import com.fr.design.data.datapane.TableDataTreePane;
import com.fr.design.dialog.BasicPane;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.file.TemplateTreePane;
import com.fr.design.gui.itextarea.UITextArea;
import com.fr.design.mainframe.DesignerContext;
import com.fr.env.SignIn;
import com.fr.general.ComparatorUtils;
import com.fr.general.GeneralContext;
import com.fr.general.Inter;
import com.fr.stable.EnvChangedListener;
import com.fr.stable.ProductConstants;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;
import com.fr.stable.OperatingSystem;
import com.fr.stable.project.ProjectConstants;
import com.fr.start.server.JettyHost;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class StartServer {
    public static boolean NEED_LOAD_ENV = true;
    // 原先的jettyHost放在类JettyHost里面，很不方便操作，而且因为存在多个进程的原因，
    // 原先的getInstance()方法无多大意义
    private static JettyHost jettyHost = null;

    static {
        GeneralContext.addEnvChangedListener(new EnvChangedListener() {
            public void envChanged() {
                currentEnvChanged();
            }
        });
    }

    /**
     * 预览Demo
     * 找默认工作目录，不应该按照名字去找，而应该按照安装路径，因为默认工作目录的名字可能会改变。
     */
    public static void browserDemoURL() {
        if (ComparatorUtils.equals(StableUtils.getInstallHome(), ".")) {//august:供代码使用
            String web = GeneralContext.getCurrentAppNameOfEnv();
            browserURLWithLocalEnv("http://localhost:" + DesignerEnvManager.getEnvManager().getJettyServerPort() + "/" + web + "/" + ConfigManager.getProviderInstance().getServletMapping()
                    + "?op=fs");
            return;
        }
        DesignerEnvManager envManager = DesignerEnvManager.getEnvManager();
        if (!envManager.isCurrentEnvDefault()) {
            InformationPane inf = new InformationPane(envManager.getDefaultEnvName());
            inf.showSmallWindow(DesignerContext.getDesignerFrame(), new DialogActionAdapter() {
                @Override
                public void doOk() {
                    try {
                        SignIn.signIn(DesignerEnvManager.getEnvManager().getDefaultEnv());
                        TemplateTreePane.getInstance().refreshDockingView();
                        TableDataTreePane.getInstance(DesignModelAdapter.getCurrentModelAdapter());
                    } catch (Exception e) {
                        FRContext.getLogger().errorWithServerLevel(e.getMessage());
                    }
                    initDemoServerAndBrowser();
                }

            }).setVisible(true);
        } else {
            initDemoServerAndBrowser();
        }
    }

    private static void initDemoServerAndBrowser() {
        if (jettyHost != null) {
            if (!jettyHost.isDemoAppLoaded()) {
                jettyHost.exit();
                jettyHost = new JettyHost(DesignerEnvManager.getEnvManager().getJettyServerPort());
                jettyHost.addAndStartInstallHomeWebApp();
            }
        } else {
            jettyHost = new JettyHost(DesignerEnvManager.getEnvManager().getJettyServerPort());
            jettyHost.addAndStartInstallHomeWebApp();
        }
        try {
            if (!jettyHost.isStarted()) {
                jettyHost.start();
            }
        } catch (Exception e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage());
        } finally {
            //先访问Demo, 后访问报表, 不需要重置服务器.
            NEED_LOAD_ENV = false;
            browser("http://localhost:" + DesignerEnvManager.getEnvManager().getJettyServerPort() + "/" + ProjectConstants.WEBAPP_NAME + "/" + ConfigManager.getProviderInstance().getServletMapping()
                    + "?op=fs");
        }
    }

    /**
     * 本地环境浏览url
     *
     * @param url 指定路径
     */
    public static void browserURLWithLocalEnv(String url) {
        try {
            if (jettyHost != null) {
                if (NEED_LOAD_ENV) {
                    jettyHost.exit();
                    jettyHost = new JettyHost(DesignerEnvManager.getEnvManager().getJettyServerPort());
                    jettyHost.addAndStartLocalEnvHomeWebApp();

                }
            } else {
                jettyHost = new JettyHost(DesignerEnvManager.getEnvManager().getJettyServerPort());
                jettyHost.addAndStartLocalEnvHomeWebApp();

            }
            if (!jettyHost.isStarted()) {
                jettyHost.start();
            }
        } catch (InterruptedException e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage());
        } catch (Exception e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage());
        } finally {
            NEED_LOAD_ENV = false;
            browser(url);
        }
    }

    public static JettyHost getInstance() {
        // august： 正确的逻辑能保证jettyHost不为null，不然就有bug，不允许这儿加是否等于null判断
        return jettyHost;
    }

    /**
     * 运行环境改变事件
     */
    public static void currentEnvChanged() {
        if (!NEED_LOAD_ENV) {
            NEED_LOAD_ENV = true;
        }
    }

    private static void browser(String uri) {
        if (StringUtils.isEmpty(uri)) {
            FRContext.getLogger().info("The URL is empty!");
            return;
        }
        try {
            Desktop.getDesktop().browse(new URI(uri));

        } catch (IOException e) {
            startBrowserFromCommand(uri, e);
        } catch (URISyntaxException e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
        } catch (Exception e) {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
            FRContext.getLogger().error("Can not open the browser for URL:  " + uri);
        }
    }

    private static void startBrowserFromCommand(String uri, IOException e) {
        if (OperatingSystem.isWindows()) {
            try {
                // win10 内存用到到80%左右的时候, Desktop.browser经常提示"存储空间不足, 无法处理改命令", 用rundll32可以打开.
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + uri);
            } catch (IOException ee) {
                JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Designer_Set_default_browser"));
                FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
            }
        } else {
            FRContext.getLogger().errorWithServerLevel(e.getMessage(), e);
        }
    }

    private static class InformationPane extends BasicPane {
        private static final long serialVersionUID = 1L;

        public InformationPane(String message) {
            this.setLayout(new BorderLayout(10, 10));
            this.setBorder(BorderFactory.createEmptyBorder(15, 5, 5, 5));
            String text;
            if (!ComparatorUtils.equals(message, Inter.getLocText(new String[]{"Default", "Utils-Report_Runtime_Env"}))) {
                text = new StringBuffer(Inter.getLocText("FR-Designer_Open"))
                        .append(ProductConstants.APP_NAME)
                        .append(Inter.getLocText("FR-Designer_Utils-OpenDemoEnv"))
                        .append(message).append(Inter.getLocText("FR-Designer_Utils-switch")).toString();
            } else {
                text = new StringBuffer(Inter.getLocText("FR-Designer_Open"))
                        .append(ProductConstants.APP_NAME)
                        .append(Inter.getLocText("FR-Designer_Utils-NewDemoEnv"))
                        .append(message).append(Inter.getLocText("FR-Designer_Utils-switch")).toString();
            }
            UITextArea a = new UITextArea(text);
            a.setFont(new Font("Dialog", Font.PLAIN, 12));
            a.setEditable(false);
            a.setBackground(this.getBackground());
            a.setLineWrap(true);
            this.add(a);
        }

        @Override
        protected String title4PopupWindow() {
            return Inter.getLocText("FR-Designer_Tooltips");
        }

    }

}