package com.fr.design.style.color;

import com.fr.stable.file.RemoteXMLFileManagerProvider;

import java.awt.Color;
import java.util.List;


/**
 * Created by yaohwu on 2017/2/8.
 */
public interface ColorSelectConfigManagerProvider extends RemoteXMLFileManagerProvider {

    public List<Color> getColorsFromFile();
    public void setColorsToFile(List<Color> colors);
}
