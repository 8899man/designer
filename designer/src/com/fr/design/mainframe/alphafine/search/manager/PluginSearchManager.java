package com.fr.design.mainframe.alphafine.search.manager;

import com.fr.design.DesignerEnvManager;
import com.fr.design.mainframe.alphafine.AlphaFineConstants;
import com.fr.design.mainframe.alphafine.AlphaFineHelper;
import com.fr.design.mainframe.alphafine.CellType;
import com.fr.design.mainframe.alphafine.cell.model.MoreModel;
import com.fr.design.mainframe.alphafine.cell.model.PluginModel;
import com.fr.design.mainframe.alphafine.model.SearchResult;
import com.fr.general.ComparatorUtils;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.general.http.HttpClient;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by XiaXiang on 2017/3/27.
 */
public class PluginSearchManager implements AlphaFineSearchProcessor {
    private static PluginSearchManager pluginSearchManager = null;
    private SearchResult lessModelList;
    private SearchResult moreModelList;


    public synchronized static PluginSearchManager getPluginSearchManager() {
        if (pluginSearchManager == null) {
            pluginSearchManager = new PluginSearchManager();
        }
        return pluginSearchManager;

    }

    private static PluginModel getPluginModel(JSONObject object, boolean isFromCloud) {
        String name = object.optString("name");
        String content = object.optString("description");
        int pluginId = object.optInt("id");
        int searchCount = object.optInt("searchCount");
        String imageUrl = null;
        try {
            imageUrl = isFromCloud ? AlphaFineConstants.PLUGIN_IMAGE_URL + URLEncoder.encode(object.optString("pic").toString().substring(AlphaFineConstants.PLUGIN_IMAGE_URL.length()), "utf8") : object.optString("pic");
        } catch (UnsupportedEncodingException e) {
            FRLogger.getLogger().error(e.getMessage());
        }
        String version = null;
        String jartime = null;
        CellType type;
        String link = object.optString("link");
        if (ComparatorUtils.equals(link, "plugin")) {
            version = isFromCloud ? object.optString("pluginversion") : object.optString("version");
            jartime = object.optString("jartime");
            type = CellType.PLUGIN;
        } else {
            type = CellType.REUSE;
        }
        int price = object.optInt("price");
        return new PluginModel(name, content, imageUrl, version, jartime, link, type, price, pluginId, searchCount);
    }

    /**
     * 根据json获取对应的插件model
     *
     * @param object
     * @return
     */
    public static PluginModel getModelFromCloud(JSONObject object) {
        JSONObject jsonObject = object.optJSONObject("result");
        if (jsonObject != null) {
            return getPluginModel(jsonObject, true);
        } else {
            return getPluginModel(object, false);
        }

    }

    @Override
    public synchronized SearchResult getLessSearchResult(String searchText) {
        this.lessModelList = new SearchResult();
        this.moreModelList = new SearchResult();
        if (StringUtils.isBlank(searchText)) {
            lessModelList.add(new MoreModel(Inter.getLocText("FR-Designer-Plugin_Addon")));
            return lessModelList;
        }
        if (DesignerEnvManager.getEnvManager().getAlphaFineConfigManager().isContainPlugin()) {
            String result;
            try {
                String encodedKey = URLEncoder.encode(searchText, "UTF-8");
                String url = AlphaFineConstants.PLUGIN_SEARCH_URL + "?keyword=" + encodedKey;
                HttpClient httpClient = new HttpClient(url);
                httpClient.asGet();
                if (!httpClient.isServerAlive()) {
                    return getNoConnectList();
                }
                httpClient.setTimeout(5000);
                result = httpClient.getResponseText();
                AlphaFineHelper.checkCancel();
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("result");
                if (jsonArray != null) {
                    SearchResult searchResult = new SearchResult();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        PluginModel cellModel = getPluginModel(jsonArray.optJSONObject(i), false);
                        if (!AlphaFineHelper.getFilterResult().contains(cellModel)) {
                            searchResult.add(cellModel);
                        }
                    }
                    if (searchResult.size() < AlphaFineConstants.SHOW_SIZE + 1) {
                        lessModelList.add(0, new MoreModel(Inter.getLocText("FR-Designer-Plugin_Addon")));
                        if (searchResult.size() == 0) {
                            lessModelList.add(AlphaFineHelper.NO_RESULT_MODEL);
                        } else {
                            lessModelList.addAll(searchResult);
                        }
                    } else {
                        lessModelList.add(0, new MoreModel(Inter.getLocText("FR-Designer-Plugin_Addon"), Inter.getLocText("FR-Designer_AlphaFine_ShowAll"), true, CellType.PLUGIN));
                        lessModelList.addAll(searchResult.subList(0, AlphaFineConstants.SHOW_SIZE));
                        moreModelList.addAll(searchResult.subList(AlphaFineConstants.SHOW_SIZE, searchResult.size()));
                    }
                }
            } catch (JSONException e) {
                FRLogger.getLogger().error("plugin search json error :" + e.getMessage());
            } catch (UnsupportedEncodingException e) {
                FRLogger.getLogger().error("plugin search encode error :" + e.getMessage());
            }
        }
        return this.lessModelList;
    }

    private SearchResult getNoConnectList() {
        SearchResult result = new SearchResult();
        result.add(0, new MoreModel(Inter.getLocText("FR-Designer-Plugin_Addon")));
        result.add(AlphaFineHelper.NO_CONNECTION_MODEL);
        return result;
    }

    @Override
    public SearchResult getMoreSearchResult() {
        return this.moreModelList;
    }
}
