package com.fr.design.extra.exe;

import com.fr.design.extra.PluginReaderForDesigner;
import com.fr.design.extra.Process;
import com.fr.general.FRLogger;
import com.fr.stable.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by vito on 16/4/19.
 */
public class ReadUpdateOnlineExecutor implements Executor {
    private String[] plugins;
    private String result;

    @Override
    public String getTaskFinishMessage() {
        return result;
    }

    @Override
    public Command[] getCommands() {
        return new Command[]{
                new Command() {
                    @Override
                    public String getExecuteMessage() {
                        return StringUtils.EMPTY;
                    }

                    @Override
                    public void run(Process<String> process) {
                        try {
                            plugins = PluginReaderForDesigner.readPluginsForUpdate();
                            JSONArray jsonArray = new JSONArray();
                            for (String plugin : plugins) {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("pluginid", plugin);
                                jsonArray.put(jsonObject);
                            }
                            result = jsonArray.toString();
                        } catch (Exception e) {
                            FRLogger.getLogger().error(e.getMessage());
                        }
                    }
                }
        };
    }
}
