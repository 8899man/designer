package com.fr.design.parameter;import com.fr.base.Parameter;public interface ParaDefinitePane {    Parameter[] getNoRepeatParas(Parameter[] paras);    void setParameterArray(Parameter[] ps);    Parameter[] getParameterArray();    void refreshParameter();    boolean isWithQueryButton();    void addingParameter2Editor(Parameter p);    void addingParameter2EditorWithQueryButton(Parameter p);    void addingAllParameter2Editor();}