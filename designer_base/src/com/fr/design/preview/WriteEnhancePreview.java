package com.fr.design.preview;

import com.fr.base.io.IOFile;
import com.fr.design.fun.impl.AbstractPreviewProvider;
import com.fr.general.Inter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by loy on 2017/7/7.
 */
public class WriteEnhancePreview extends AbstractPreviewProvider {
    @Override
    public String nameForPopupItem() {
        return Inter.getLocText("FR-Designer_Write_Enhance_Preview");
    }

    @Override
    public String iconPathForPopupItem() {
        return "com/fr/design/images/buttonicon/writes.png";
    }

    @Override
    public String iconPathForLarge() {
        return "com/fr/design/images/buttonicon/writeb24.png";
    }

    @Override
    public int previewTypeCode() {
        return IOFile.WRITE_PREVIEW_TYPE;
    }

    @Override
    public Map<String, Object> parametersForPreview() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("op", "write_ex");
        return map;
    }
}
