package com.fr.start;

import com.fr.design.mainframe.bbs.BBSConstants;
import com.fr.event.Event;
import com.fr.event.EventDispatcher;
import com.fr.event.Listener;
import com.fr.general.GeneralContext;
import com.fr.module.ModuleEvent;
import com.fr.stable.StringUtils;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 启动动画策略
 *
 * @author vito
 * @date 2018/6/5
 */
public class SplashContext {

    public static final String SPLASH_PATH = "/com/fr/design/images/splash_10.gif";
    public static final String SPLASH_CACHE_NAME = "splash_10.gif";

    private static final SplashContext SPLASH_CONTEXT = new SplashContext();

    private SplashStrategy splashStrategy;

    private String moduleID = "";
    private int loadingIndex = 0;
    private String[] loading = new String[]{"..", "....", "......"};

    private static final String GUEST = getRandomUser();

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private Listener<String> listener;


    public static SplashContext getInstance() {
        return SPLASH_CONTEXT;
    }

    private SplashContext() {

    }

    /**
     * 注册具体的启动动画
     */
    public void registerSplash(SplashStrategy splashStrategy) {
        this.splashStrategy = splashStrategy;
    }

    /**
     * 展示启动动画
     */
    public void show() {
        splashStrategy.show();
        //监听
        initListener();
    }

    /**
     * 隐藏启动动画
     */
    public void hide() {
        splashStrategy.hide();
        //取消监听
        EventDispatcher.stopListen(listener);
        // 窗口关闭后取消定时获取模块信息的timer
        scheduler.shutdown();
        // 一次性
        splashStrategy = null;
    }

    private void initListener() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                loadingIndex++;
                updateModuleLog(moduleID.isEmpty() ? StringUtils.EMPTY : moduleID + loading[loadingIndex % 3]);
            }
        }, 0, 300, TimeUnit.MILLISECONDS);

        listener = new Listener<String>() {

            @Override
            public void on(Event event, String i18n) {
                showThanks();
                moduleID = i18n;
                loadingIndex++;
                updateModuleLog(moduleID.isEmpty() ? StringUtils.EMPTY : moduleID + loading[loadingIndex % 3]);
            }
        };
        EventDispatcher.listen(ModuleEvent.MajorModuleStarting, listener);
    }

    private void updateModuleLog(String text) {
        splashStrategy.updateModuleLog(text);
    }

    private void updateThanksLog(String text) {
        splashStrategy.updateThanksLog(text);
    }

    /**
     * 获取随机感谢人员
     */
    private static String getRandomUser() {
        String[] allGuest = BBSConstants.getAllGuest();
        if (allGuest.length == 0) {
            return StringUtils.EMPTY;
        }
        int num = new Random().nextInt(allGuest.length);
        return StringUtils.BLANK + allGuest[num];
    }

    /**
     * 展示感谢信息
     */
    private void showThanks() {
        if (shouldShowThanks()) {
            updateThanksLog(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Report_Thanks_To") + GUEST);
        }
    }

    /**
     * 是否显示鸣谢面板
     */
    private boolean shouldShowThanks() {
        Locale[] hideLocales = {Locale.CHINA, Locale.TAIWAN};
        for (Locale loc : hideLocales) {
            if (GeneralContext.getLocale().equals(loc)) {
                return true;
            }
        }
        return false;
    }

}
