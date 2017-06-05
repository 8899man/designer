package com.fr.design.extra.exe.callback;

import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.plugin.error.PluginErrorCode;
import com.fr.plugin.manage.PluginManager;
import com.fr.plugin.manage.control.PluginTaskResult;

import javax.swing.*;
import java.io.File;

/**
 * Created by ibm on 2017/5/26.
 */
public class InstallFromDiskCallback extends AbstractPluginTaskCallback {
    private File zipFile;
    private JSCallback jsCallback;
    private static int HUNDRED_PERCENT = 100;

    public InstallFromDiskCallback(final File zipFile, final JSCallback jsCallback) {
        this.zipFile = zipFile;
        this.jsCallback = jsCallback;
    }

    @Override
    public void updateProgress(String description, double aProgress) {
        jsCallback.execute(String.valueOf(aProgress * HUNDRED_PERCENT +  "%"));
    }


    @Override
    public void done(PluginTaskResult result) {
        jsCallback.execute("success");
        if (result.isSuccess()) {
            FRLogger.getLogger().info(Inter.getLocText("FR-Designer-Plugin_Install_Success"));
            JOptionPane.showMessageDialog(null, Inter.getLocText("FR-Designer-Plugin_Install_Successful"));
        } else if (result.errorCode() == PluginErrorCode.OperationNotSupport.getCode()) {
            int rv = JOptionPane.showOptionDialog(
                    null,
                    Inter.getLocText(Inter.getLocText("FR-Designer-Plugin_Install_Dependence")),
                    Inter.getLocText("FR-Designer-Plugin_Install_Success"),
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    null,
                    null
            );
            if (rv == JOptionPane.CANCEL_OPTION || rv == JOptionPane.CLOSED_OPTION) {
                return;
            }
            PluginManager.getController().install(zipFile, new InstallFromDiskCallback(zipFile, jsCallback));
        } else {
            JOptionPane.showMessageDialog(null, result.getMessage(), Inter.getLocText("FR-Designer-Plugin_Warning"), JOptionPane.ERROR_MESSAGE);
        }
    }
}
