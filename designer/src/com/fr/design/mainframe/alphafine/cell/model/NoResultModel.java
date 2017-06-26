package com.fr.design.mainframe.alphafine.cell.model;

import com.fr.design.mainframe.alphafine.CellType;
import com.fr.json.JSONException;
import com.fr.json.JSONObject;

/**
 * Created by XiaXiang on 2017/6/8.
 */
public class NoResultModel extends AlphaCellModel {
    public NoResultModel(String name) {
        super(name, null, CellType.NO_RESULT);
    }

    @Override
    public JSONObject ModelToJson() throws JSONException {
        return null;
    }

    @Override
    public String getStoreInformation() {
        return null;
    }

    @Override
    public boolean hasNoResult() {
        return true;
    }
}
