package com.fr.design.extra.exe.callback;

import com.fr.design.extra.PluginUtils;
import com.fr.design.extra.PluginOperateUtils;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.plugin.context.PluginMarker;
import com.fr.plugin.error.PluginErrorCode;
import com.fr.plugin.manage.control.AbstractDealPreTaskCallback;
import com.fr.plugin.manage.control.PluginTask;
import com.fr.plugin.manage.control.PluginTaskResult;

import javax.swing.*;


/**
 * Created by ibm on 2017/5/26.
 */
public class InstallOnlineCallback extends AbstractDealPreTaskCallback {
    protected JSCallback jsCallback;
    private static int HUNDRED_PERCENT = 100;

    public InstallOnlineCallback(PluginTask pluginTask, JSCallback jsCallback){
        super(pluginTask);
        this.jsCallback = jsCallback;
    }

    @Override
    public void updateProgress(String description, double aProgress) {
        jsCallback.execute(String.valueOf(aProgress * HUNDRED_PERCENT + "%"));
    }


    @Override
    protected void allDone(PluginTaskResult result) {
        if (result.isSuccess()) {
            String pluginInfo = PluginOperateUtils.getSuccessInfo(result);
            jsCallback.execute("success");
            FRLogger.getLogger().info(pluginInfo + Inter.getLocText("FR-Designer-Plugin_Install_Success"));
            JOptionPane.showMessageDialog(null, pluginInfo + Inter.getLocText("FR-Designer-Plugin_Install_Success"));
        } else if(result.errorCode() == PluginErrorCode.HasLowerPluginWhenInstall){
            int rv = JOptionPane.showOptionDialog(
                    null,
                    Inter.getLocText("FR-Designer-Plugin_Has_Install_Lower"),
                    Inter.getLocText("FR-Designer-Plugin_Warning"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    null,
                    null
            );
            if (rv == JOptionPane.CANCEL_OPTION || rv == JOptionPane.CLOSED_OPTION) {
                return;
            }
            PluginMarker pluginMarker = result.getCurrentTask().getMarker();
            PluginOperateUtils.updatePluginOnline(pluginMarker, jsCallback);
        }else {
            jsCallback.execute("failed");
            FRLogger.getLogger().info(Inter.getLocText("FR-Designer-Plugin_Install_Failed"));
            JOptionPane.showMessageDialog(null, PluginUtils.getMessageByErrorCode(result.errorCode()), Inter.getLocText("FR-Designer-Plugin_Warning"), JOptionPane.ERROR_MESSAGE);
        }
    }



}
