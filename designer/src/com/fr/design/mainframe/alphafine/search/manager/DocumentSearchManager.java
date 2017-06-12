package com.fr.design.mainframe.alphafine.search.manager;

import com.fr.design.DesignerEnvManager;
import com.fr.design.mainframe.alphafine.AlphaFineConstants;
import com.fr.design.mainframe.alphafine.AlphaFineHelper;
import com.fr.design.mainframe.alphafine.CellType;
import com.fr.design.mainframe.alphafine.cell.model.DocumentModel;
import com.fr.design.mainframe.alphafine.cell.model.MoreModel;
import com.fr.design.mainframe.alphafine.model.SearchResult;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.general.http.HttpClient;
import com.fr.json.JSONArray;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;
import com.fr.stable.StringUtils;

/**
 * Created by XiaXiang on 2017/3/27.
 */
public class DocumentSearchManager implements AlphaFineSearchProcessor {
    private static DocumentSearchManager documentSearchManager = null;
    private SearchResult lessModelList;
    private SearchResult moreModelList;
    private static final MoreModel TITLE_MODEL = new MoreModel(Inter.getLocText("FR-Designer_COMMUNITY_HELP"), CellType.DOCUMENT);

    public synchronized static DocumentSearchManager getDocumentSearchManager() {
        if (documentSearchManager == null) {
            documentSearchManager = new DocumentSearchManager();

        }
        return documentSearchManager;
    }

    @Override
    public synchronized SearchResult getLessSearchResult(String searchText) {
        lessModelList = new SearchResult();
        moreModelList = new SearchResult();
        if (StringUtils.isBlank(searchText)) {
            lessModelList.add(TITLE_MODEL);
            return lessModelList;
        }
        if (DesignerEnvManager.getEnvManager().getAlphaFineConfigManager().isContainDocument()) {
            String result;
            String url = AlphaFineConstants.DOCUMENT_SEARCH_URL + searchText + "-1";
            HttpClient httpClient = new HttpClient(url);
            httpClient.setTimeout(5000);
            httpClient.asGet();
            if (!httpClient.isServerAlive()) {
                return getNoConnectList();
            }
            result = httpClient.getResponseText();
            AlphaFineHelper.checkCancel();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.optJSONArray("docdata");
                if (jsonArray != null) {
                    SearchResult searchResult = new SearchResult();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        AlphaFineHelper.checkCancel();
                        DocumentModel cellModel = getModelFromCloud(jsonArray.optJSONObject(i));
                        if (!AlphaFineHelper.getFilterResult().contains(cellModel)) {
                            searchResult.add(cellModel);
                        }
                    }
                    if (searchResult.size() < AlphaFineConstants.SHOW_SIZE + 1) {
                        lessModelList.add(0, TITLE_MODEL);
                        if (searchResult.size() == 0) {
                            lessModelList.add(AlphaFineHelper.NO_RESULT_MODEL);
                        } else {
                            lessModelList.addAll(searchResult);
                        }
                    } else {
                        lessModelList.add(0, new MoreModel(Inter.getLocText("FR-Designer_COMMUNITY_HELP"), Inter.getLocText("FR-Designer_AlphaFine_ShowAll"),true, CellType.DOCUMENT));
                        lessModelList.addAll(searchResult.subList(0, AlphaFineConstants.SHOW_SIZE));
                        moreModelList.addAll(searchResult.subList(AlphaFineConstants.SHOW_SIZE, searchResult.size()));
                    }
                }
            } catch (JSONException e) {
                FRLogger.getLogger().error("document search error: " + e.getMessage());
                return lessModelList;
            }
        }
        return lessModelList;
    }

    /**
     * 无连接
     * @return
     */
    private SearchResult getNoConnectList() {
        SearchResult result = new SearchResult();
        result.add(0, TITLE_MODEL);
        result.add(AlphaFineHelper.NO_CONNECTION_MODEL);
        return result;
    }

    /**
     * 根据json信息获取文档model
     * @param object
     * @return
     */
    public static DocumentModel getModelFromCloud(JSONObject object) {
        String name = object.optString("title");
        String content = object.optString("summary");
        int documentId = object.optInt("did");
        return new DocumentModel(name, content, documentId);
    }

    @Override
    public SearchResult getMoreSearchResult() {
        return moreModelList;
    }

}
