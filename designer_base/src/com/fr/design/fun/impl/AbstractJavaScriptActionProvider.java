package com.fr.design.fun.impl;

import com.fr.design.fun.JavaScriptActionProvider;
import com.fr.design.javascript.JavaScriptActionPane;
import com.fr.stable.fun.impl.AbstractProvider;
import com.fr.stable.fun.mark.API;

/**
 * Created by zack on 2015/8/20.
 */
@API(level = JavaScriptActionProvider.CURRENT_LEVEL)
public abstract class AbstractJavaScriptActionProvider extends AbstractProvider implements JavaScriptActionProvider {

    public int currentAPILevel() {
        return CURRENT_LEVEL;
    }

    @Override
    public String mark4Provider() {
        return getClass().getName();
    }

    @Override
    public void setJavaScriptActionPane(JavaScriptActionPane pane) {
    }
}