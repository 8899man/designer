package com.fr.design.mainframe.chart.gui.item;

import com.fr.general.ComparatorUtils;

/**
 * Created by hufan on 2016/10/11.
 */
public enum ItemEvenType {
    REACTOR("reactor"),//重构选项
    DEFAULT("default")//默认选项操作
    ;

    //这个String会存起来的，不能随意更改。
    private String type;

    private ItemEvenType(String type){
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    private static ItemEvenType[] types;

    public static ItemEvenType parse(String type){
        if(types == null){
            types = ItemEvenType.values();
        }
        for(ItemEvenType itemEvenType : types){
            if(ComparatorUtils.equals(itemEvenType.getType(), type)){
                return itemEvenType;
            }
        }
        return DEFAULT;
    }
}
