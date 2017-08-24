package com.fr.plugin.chart.multilayer.data;

import com.fr.chart.base.ChartConstants;
import com.fr.chart.chartattr.ChartCollection;
import com.fr.chart.chartdata.TopDefinition;
import com.fr.data.util.function.AbstractDataFunction;
import com.fr.design.event.UIObserver;
import com.fr.design.event.UIObserverListener;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.TableLayout;
import com.fr.design.mainframe.chart.gui.ChartDataPane;
import com.fr.design.mainframe.chart.gui.data.CalculateComboBox;
import com.fr.design.mainframe.chart.gui.data.table.AbstractTableDataContentPane;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.stable.ArrayUtils;
import com.fr.stable.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fangjie on 2016/6/15.
 */
public class MultiPiePlotTableDataContentPane extends AbstractTableDataContentPane implements UIObserver {
    private static final int HT = 20;
    private static final int WD = 100;
    private UISpinner levelNumEdit;
    private UITextField nameField;
    protected UIComboBox value;
    private CalculateComboBox calculateCombox;
    private int levelNum = 3; //默认三层
    private List<UIComboBox> levelNameList = null;

    private JPanel contentPane;
    private JPanel center;

    //监听器，当重新布局层级名控件时需要用到
    private UIObserverListener listener;

    //将数据列名保存下来，在update时会删除层级名列表，需要用保存的list重新初始化新的层级名列表
    private List columnNameList;

    public MultiPiePlotTableDataContentPane() {
    }

    public MultiPiePlotTableDataContentPane(ChartDataPane parent){
        this.setLayout(new BorderLayout());
        initContentPane();
        this.add(contentPane, BorderLayout.CENTER);
    }

    private void initLevelNameList() {
        levelNameList = new ArrayList<UIComboBox>();
        for (int i = 0; i < levelNum; i++){
            levelNameList.add(new UIComboBox());
            levelNameList.get(i).setPreferredSize(new Dimension(WD, HT));
            levelNameList.get(i).addItem(Inter.getLocText("Chart-Use_None"));
        }
    }

    private void initContentPane() {
        JPanel north = createNorthPane();

        center = createCenterPane();

        contentPane = new JPanel();

        contentPane.setLayout(new BorderLayout(0, 4));

        contentPane.add(north, BorderLayout.NORTH);
        contentPane.add(center, BorderLayout.CENTER);
    }

    private JPanel createCenterPane() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize_center = {p, f};
        double[] rowSize_center = new double[levelNum  + 3];

        initLevelNameList();

        for (int i = 0; i < levelNum + 3; i++){
            rowSize_center[i] = p;
        }
        Component[][] components_center = new Component[levelNum + 3][];

        for (int i = 0; i < levelNum; i++){
            components_center[i] = new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Level") + String.valueOf(i+1)), levelNameList.get(i)};
        }

        value = new UIComboBox();
        value.setPreferredSize(new Dimension(WD, HT));
        calculateCombox = new CalculateComboBox();
        calculateCombox.reset();
        calculateCombox.setPreferredSize(new Dimension(WD, HT));

        components_center[levelNum] = new Component[]{getJSeparator(), null};
        components_center[levelNum+1] = new Component[]{new UILabel(Inter.getLocText("FR-Chart_Bubble_Size")), value};
        components_center[levelNum+2] = new Component[]{new UILabel(Inter.getLocText("Chart-Summary_Method")), calculateCombox};


        initCenterItemListener();
        registerListener4Center();


        return TableLayout4VanChartHelper.createGapTableLayoutPane(components_center,rowSize_center,columnSize_center);


    }

    /**
     * 为每个组件注册一个监听器
     */
    private void registerListener4Center() {
        for (int i = 0; i < levelNameList.size(); i++){
            levelNameList.get(i).registerChangeListener(listener);
        }
        value.registerChangeListener(listener);
    }

    /**
     * 为没处于中间面板的组件创建监听
     */
    private void initCenterItemListener() {
        value.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                calculateCombox.setEnabled(value.getSelectedItem() != null);
                if (value.getSelectedItem() != null) {
                    value.setToolTipText(value.getSelectedItem().toString());
                } else {
                    value.setToolTipText(null);
                }
            }
        });
    }

    private JPanel createNorthPane() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize_north = {p, f};
        double[] rowSize_north = {p, p, p};

        levelNumEdit = new UISpinner(1, 15, 1, levelNum){
            @Override
            protected void fireStateChanged() {
                //先处理自身的空间布局
                refreshCenterPane();
                //然后更新数据
                super.fireStateChanged();
            }

            @Override
            public void setTextFieldValue(double value) {
                //如果为0，则没有改变值
                if (value == 0){
                    return;
                }
                super.setTextFieldValue(value);
            }
        };
        levelNumEdit.setPreferredSize(new Dimension(WD, HT));

        nameField = new UITextField();
        nameField.setPreferredSize(new Dimension(WD, HT));

        Component[][] components_north = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_MultiPie_Series_Name")), nameField},
                new Component[]{getJSeparator(),null },
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Level_Number")), levelNumEdit},
        };

        return TableLayout4VanChartHelper.createGapTableLayoutPane(components_north, rowSize_north, columnSize_north);
    }

    private void refreshCenterPane(){
        if (levelNumEdit == null){
            return;
        }

        int newLevelNum = (int) levelNumEdit.getValue();
        //更新界面
        if (newLevelNum != levelNum){
            levelNum = newLevelNum;
            contentPane.remove(center);
            center = createCenterPane();
            contentPane.add(center, BorderLayout.CENTER);
        }

        if (columnNameList != null){
            refreshBoxListWithSelectTableData(columnNameList);
        }

        refreshPane();
    }

    private void refreshPane() {
        this.validate();
        this.repaint();
        this.revalidate();
    }

    @Override
    /**
     * 检查 某些Box是否可用
     * @param hasUse  是否使用.
     */
    public void checkBoxUse(boolean hasUse) {
        levelNumEdit.setEnabled(hasUse);
        nameField.setEnabled(hasUse);
        value.setEnabled(hasUse);
        for (int i = 0; i < levelNameList.size(); i++){
            levelNameList.get(i).setEnabled(hasUse);
        }
        calculateCombox.setEnabled(hasUse && value.getSelectedItem() != null);
    }

    @Override
    public void clearAllBoxList() {

    }

    @Override
    protected void refreshBoxListWithSelectTableData(List list) {
        columnNameList = list;
        for (int i = 0; i < levelNum; i++) {
            refreshBoxItems(levelNameList.get(i), list);
            levelNameList.get(i).addItem(Inter.getLocText("Chart-Use_None"));
        }
        refreshBoxItems(value, list);
    }

    /**
     * 將监听器保存下来
     * @param listener 观察者监听事件
     */
    @Override
    public void registerChangeListener(UIObserverListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean shouldResponseChangeListener() {
        return false;
    }

    @Override
    public void populateBean(ChartCollection collection) {
        super.populateBean(collection);
        TopDefinition top = (TopDefinition)collection.getSelectedChart().getFilterDefinition();

        if(top == null || !(top instanceof MultiPieValueDefinition)) {
            return;
        }
        MultiPieValueDefinition data = (MultiPieValueDefinition)top;

        //层级数
        levelNumEdit.setValue(data.getLevelCount());
        levelNum = (int) levelNumEdit.getValue();
        //指标名
        nameField.setText(data.getSeriesName());
        //各个层级名
        populateAllLevelName(data);

        //值
        value.setEditable(true);
        value.setSelectedItem(this.boxItemsContainsObject(value,data.getValueColumnName())
                ? data.getValueColumnName() : null);
        value.setEditable(false);
        //汇总方式
        if(isNeedSummaryCaculateMethod()){
            calculateCombox.populateBean((AbstractDataFunction) data.getDataFunction());
        }

    }

    @Override
    /**
     * 保存界面内容到ChartCollection
     */
    public void updateBean(ChartCollection collection) {
        MultiPieValueDefinition definition = new MultiPieValueDefinition();
        collection.getSelectedChart().setFilterDefinition(definition);

        //层级数
        definition.setLevelCount((int) levelNumEdit.getValue());
        //指标名
        definition.setSeriesName(nameField.getText());
        //各个层级名
        updateAllLevelName(definition);

        //值
        String valueName = (String) this.value.getSelectedItem();
        definition.setValueColumnName(valueName);
        //汇总方式
        if(isNeedSummaryCaculateMethod()) {
            definition.setDataFunction(calculateCombox.updateBean());
        }

    }


    private void populateAllLevelName(MultiPieValueDefinition definition) {
        if (levelNameList == null){
            initLevelNameList();
        }

        List<String> levelColumnNameList = definition.getLevelColumnNameList();

        for (int i = 0; i < levelNameList.size(); i++){
            if(!definition.hasLevelIndex(i) || ComparatorUtils.equals(levelColumnNameList.get(i), StringUtils.EMPTY)) {
                levelNameList.get(i).setSelectedItem(Inter.getLocText("Chart-Use_None"));
            } else if(definition.hasLevelIndex(i) && !this.boxItemsContainsObject(levelNameList.get(i),levelColumnNameList.get(i))){
                levelNameList.get(i).setSelectedItem(null);
            }else {
                combineCustomEditValue(levelNameList.get(i), definition.hasLevelIndex(i) ? levelColumnNameList.get(i) : null);
            }
        }
    }

    private void updateAllLevelName(MultiPieValueDefinition definition) {
        if (levelNameList == null){
            return;
        }

        //按顺序添加，顺序代表层级数
        for (int i = 0; i < levelNameList.size(); i++){
            Object levelName = levelNameList.get(i).getSelectedItem();

            if(ArrayUtils.contains(ChartConstants.getNoneKeys(), levelName)) {
                definition.addLevelName(StringUtils.EMPTY);
            } else {
                definition.addLevelName(levelName == null ? null : levelName.toString());
            }
        }
    }

    private boolean boxItemsContainsObject(UIComboBox box,Object item){
        if(box == null){
            return false;
        }

        ComboBoxModel dataModel = box.getModel();
        for (int i = 0; i < dataModel.getSize(); i++) {
            if(ComparatorUtils.equals(dataModel.getElementAt(i),item)){
                return true;
            }
        }
        return false;
    }

    protected JSeparator getJSeparator() {
        JSeparator jSeparator = new JSeparator();
        jSeparator.setPreferredSize(new Dimension(220, 2));
        return jSeparator;
    }
}
