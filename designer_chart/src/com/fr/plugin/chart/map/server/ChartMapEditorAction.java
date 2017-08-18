package com.fr.plugin.chart.map.server;

import com.fr.base.ConfigManager;
import com.fr.design.DesignerEnvManager;
import com.fr.design.actions.UpdateAction;
import com.fr.general.GeneralContext;
import com.fr.general.IOUtils;
import com.fr.general.Inter;
import com.fr.start.StartServer;

import java.awt.event.ActionEvent;

/**
 * Created by eason on 2017/5/23.
 */
public class ChartMapEditorAction extends UpdateAction {

    public ChartMapEditorAction(){
        this.setSmallIcon(IOUtils.readIcon("/com/fr/plugin/chart/map/images/mapData.png"));
        this.setName(Inter.getLocText("Plugin-ChartF_Map_Data"));
    }

    public void actionPerformed(ActionEvent evt) {
        int port = DesignerEnvManager.getEnvManager().getJettyServerPort();
        String web = GeneralContext.getCurrentAppNameOfEnv();
        String serverlet = ConfigManager.getProviderInstance().getServletMapping();
        StartServer.browserURLWithLocalEnv(String.format("http://localhost:%d/%s/%s?op=map", port, web, serverlet));
    }

}
