package com.fr.design.extra;

import com.fr.base.FRContext;
import com.fr.design.DesignerEnvManager;
import com.fr.design.extra.exe.callback.*;
import com.fr.design.extra.exe.extratask.InstallPluginTask;
import com.fr.design.extra.exe.extratask.UpdatePluginTask;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.general.SiteCenter;
import com.fr.general.http.HttpClient;
import com.fr.json.JSONObject;
import com.fr.plugin.context.PluginContext;
import com.fr.plugin.context.PluginMarker;
import com.fr.plugin.manage.PluginManager;
import com.fr.plugin.manage.bbs.BBSPluginLogin;
import com.fr.plugin.manage.control.PluginTaskCallback;
import com.fr.plugin.manage.control.PluginTaskResult;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.json.JSONArray;
import sun.plugin2.main.server.Plugin;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ibm on 2017/5/26.
 */
public class PluginOperateUtils {

    public static void installPluginOnline(final String pluginInfo, JSCallback jsCallback) {
        //下载插件
        if (!BBSPluginLogin.getInstance().hasLogin()) {
            LoginCheckContext.fireLoginCheckListener();
        }
        PluginMarker pluginMarker = PluginUtils.createPluginMarker(pluginInfo);
        if (BBSPluginLogin.getInstance().hasLogin()) {
            PluginManager.getController().download(pluginMarker, new DownloadCallback(new InstallPluginTask(pluginMarker, jsCallback), jsCallback));
        }
    }

    public static void installPluginFromDisk(final String filePath, JSCallback jsCallback) {
        PluginManager.getController().install(new File(filePath), new InstallFromDiskCallback(new File(filePath), jsCallback));
    }

    public static void updatePluginOnline(JSObject pluginIDs, JSCallback jsCallback) {
        String[] pluginInfos = jsObjectToStringArray(pluginIDs);
        if (!(BBSPluginLogin.getInstance().hasLogin())) {
            LoginCheckContext.fireLoginCheckListener();
        }
        if (BBSPluginLogin.getInstance().hasLogin()) {
            List<PluginMarker> pluginMarkerList = new ArrayList<PluginMarker>();
            for (int i = 0; i < pluginInfos.length; i++) {
                pluginMarkerList.add(PluginUtils.createPluginMarker(pluginInfos[i]));
            }
            for (int i = 0; i < pluginMarkerList.size(); i++) {
                try {
                    //todo check下此插件的最新版本
                    String latestPluginInfo = PluginUtils.getLatestPluginInfo(pluginMarkerList.get(i).getPluginID());
                    if (StringUtils.isEmpty(latestPluginInfo) || PluginConstants.CONNECTION_404.equals(latestPluginInfo)) {
                        JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Designer-Plugin_Connect_Failed"), Inter.getLocText("FR-Designer-Plugin_Warning"), JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JSONObject resultArr = new JSONObject(latestPluginInfo);
                    String latestPluginVersion = (String) resultArr.get("version");
                    PluginMarker pluginMarker = pluginMarkerList.get(i);
                    PluginMarker toPluginMarker = PluginMarker.create(pluginMarkerList.get(i).getPluginID(), latestPluginVersion);
                    PluginManager.getController().download(pluginMarkerList.get(i), new DownloadCallback(new UpdatePluginTask(pluginMarker, toPluginMarker, jsCallback), jsCallback));
                } catch (Exception e) {
                    FRContext.getLogger().error(e.getMessage(), e);
                }
            }
        }
    }

    public static void updatePluginFromDisk(final String filePath, JSCallback jsCallback) {
        PluginManager.getController().update(new File(filePath), new UpdateFromDiskCallback(new File(filePath), jsCallback));
    }

    public static void setPluginActive(String pluginInfo, JSCallback jsCallback) {
        PluginMarker pluginMarker = PluginUtils.createPluginMarker(pluginInfo);
        PluginContext plugin = PluginManager.getContext(pluginMarker);
        boolean active = !plugin.isActive();
        PluginTaskCallback modifyStatusCallback = new ModifyStatusCallback(active);
        if (active) {
            PluginManager.getController().forbid(pluginMarker, modifyStatusCallback);
        } else {
            PluginManager.getController().enable(pluginMarker, modifyStatusCallback);
        }
    }

    public static void uninstallPlugin(final String pluginInfo, final boolean isForce, JSCallback jsCallback) {
        PluginMarker pluginMarker = PluginUtils.createPluginMarker(pluginInfo);
        PluginManager.getController().uninstall(pluginMarker, isForce, new UnistallPluginCallback());
    }

    public static void readUpdateOnline(JSCallback jsCallback){
        try {
            String [] plugins = PluginReaderForDesigner.readPluginsForUpdate();
            JSONArray jsonArray = new JSONArray();
            for (String plugin : plugins) {
                org.json.JSONObject jsonObject = new org.json.JSONObject();
                jsonObject.put("pluginid", plugin);
                jsonArray.put(jsonObject);
            }
            String result = jsonArray.toString();
            jsCallback.execute(result);
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage());
        }

    }

    public static void searchPlugin(String keyword, JSCallback jsCallback) {
        try {
            HttpClient httpClient = new HttpClient(SiteCenter.getInstance().acquireUrlByKind("plugin.plist") + "&keyword=" + keyword);
            String result = httpClient.getResponseText();
            jsCallback.execute(result);
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage());
        }
    }

    public static void getPluginFromStore(String category, String seller, String fee, JSCallback jsCallback){
        String plistUrl = SiteCenter.getInstance().acquireUrlByKind("plugin.plist");
        if (StringUtils.isNotBlank(plistUrl)) {
            StringBuilder url = new StringBuilder();
            url.append(plistUrl);
            if (StringUtils.isNotBlank(category)) {
                url.append("&cid=").append(category.split("-")[1]);
            }
            if (StringUtils.isNotBlank(seller)) {
                url.append("&seller=").append(seller.split("-")[1]);
            }
            if (StringUtils.isNotBlank(fee)) {
                url.append("&fee=").append(fee.split("-")[1]);
            }
            try {
                HttpClient httpClient = new HttpClient(url.toString());
                String result = httpClient.getResponseText();
                jsCallback.execute(result);
            } catch (Exception e) {
                FRLogger.getLogger().error(e.getMessage());
            }
        } else {
            String result = PluginConstants.CONNECTION_404;
            jsCallback.execute(result);
        }
    }

    public static void getPluginCategories(JSCallback jsCallback){
        String result;
        String url = SiteCenter.getInstance().acquireUrlByKind("plugin.category");
        if (url != null) {
            HttpClient httpClient = new HttpClient(url);
            result = httpClient.getResponseText();
        } else {
            result = PluginConstants.CONNECTION_404;
        }
        jsCallback.execute(result);
    }

    public static void getLoginInfo(JSCallback jsCallback){
        String username = BBSPluginLogin.getInstance().getUserInfo().getUserName();
        String inShowUsername = DesignerEnvManager.getEnvManager().getInShowBBsName();
        if (StringUtils.isEmpty(username) && StringUtils.isEmpty(inShowUsername)) {
            return;
        }else {
           String result = StringUtils.isEmpty(inShowUsername) ? username : inShowUsername;
           jsCallback.execute(result);
        }
    }

    private static String[] jsObjectToStringArray(JSObject obj) {
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

}
