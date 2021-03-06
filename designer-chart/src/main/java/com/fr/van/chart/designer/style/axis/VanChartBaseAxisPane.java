package com.fr.van.chart.designer.style.axis;

import com.fr.base.BaseFormula;
import com.fr.base.BaseUtils;
import com.fr.base.Utils;
import com.fr.chart.base.TextAttr;
import com.fr.chart.chartattr.Title;
import com.fr.design.beans.FurtherBasicBeanPane;
import com.fr.design.formula.TinyFormulaPane;
import com.fr.design.gui.frpane.UINumberDragPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ibutton.UIToggleButton;
import com.fr.design.gui.icombobox.LineComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.gui.style.FormatPane;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.PaneTitleConstants;
import com.fr.design.mainframe.chart.gui.style.ChartTextAttrPane;
import com.fr.design.style.color.ColorSelectBox;

import com.fr.plugin.chart.attr.axis.VanChartAxis;
import com.fr.plugin.chart.base.VanChartConstants;
import com.fr.plugin.chart.type.AxisTickLineType;
import com.fr.stable.Constants;
import com.fr.stable.CoreConstants;
import com.fr.stable.StableUtils;
import com.fr.van.chart.designer.TableLayout4VanChartHelper;
import com.fr.van.chart.designer.component.VanChartHtmlLabelPane;
import com.fr.van.chart.designer.style.VanChartStylePane;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 坐标轴的基础配置项。分类，时间，值等公共的部分。
 */
public class VanChartBaseAxisPane extends FurtherBasicBeanPane<VanChartAxis> {

    private static final long serialVersionUID = -5717246802333308973L;
    private static final double ROTATION_MAX = 90.0;
    protected TinyFormulaPane titleContent;
    protected UIButtonGroup<Integer> titleAlignPane;
    protected UIToggleButton titleUseHtml;
    protected ChartTextAttrPane titleTextAttrPane;
    protected UINumberDragPane titleTextRotation;

    protected UIButtonGroup showLabel;
    protected ChartTextAttrPane labelTextAttrPane;
    protected UINumberDragPane labelTextRotation;
    protected UIButtonGroup<Integer> labelGapStyle;
    protected UITextField labelGapValue;
    protected JPanel labelPanel;


    protected LineComboBox axisLineStyle;
    protected ColorSelectBox axisLineColor;
    protected UIButtonGroup<AxisTickLineType> mainTick;
    protected UIButtonGroup<AxisTickLineType> secondTick;

    protected UIButtonGroup<Integer> position;
    protected UIButtonGroup<Boolean> reversed;

    protected UIButtonGroup<Integer> axisLimitSize;
    protected UISpinner maxProportion;
    protected JPanel maxProportionPane;

    protected UIButtonGroup valueFormatStyle;
    protected FormatPane valueFormat;
    protected JPanel centerPane;
    private VanChartHtmlLabelPane htmlLabelPane;
    private JPanel labelGapValuePane;

    public VanChartBaseAxisPane(){
        this(true);
    }

    public VanChartBaseAxisPane(boolean isXAxis){
        this.setLayout(new BorderLayout());
        this.add(createContentPane(isXAxis), BorderLayout.CENTER);
    }

    public void setParentPane(VanChartStylePane parent) {
        htmlLabelPane.setParent(parent);
    }
    protected void reLayoutPane(boolean isXAxis){
        this.removeAll();
        this.add(createContentPane(isXAxis), BorderLayout.CENTER);
    }

    protected JPanel createContentPane(boolean isXAxis){

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double e = TableLayout4VanChartHelper.EDIT_AREA_WIDTH;
        double s = TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH;
        double[] columnSize = {f, e};
        double[] column = {f, s};
        double[] rowSize = {p, p, p, p, p, p, p,p};
        Component[][] components = new Component[][]{
                new Component[]{createTitlePane(new double[]{p, p, p, p, p, p}, columnSize, isXAxis), null},
                new Component[]{createLabelPane(new double[]{p, p}, column), null},
                new Component[]{createLineStylePane(new double[]{p, p, p, p, p}, columnSize), null},
                new Component[]{createAxisPositionPane(new double[]{p, p, p}, columnSize, isXAxis), null},
                new Component[]{createDisplayStrategy(new double[]{p, p, p}, columnSize), null},
                new Component[]{createValueStylePane(), null},
        };

        return TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
    }

    protected JPanel createTitlePane(double[] row, double[] col, boolean isXAxis){
        titleAlignPane = isXAxis ? getXAxisTitleAlignPane() : getYAxisTitleAlignPane();
        titleAlignPane.setSelectedItem(Constants.CENTER);
        titleContent = new TinyFormulaPane();
        titleUseHtml = new UIToggleButton(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Html"));
        titleTextAttrPane = new ChartTextAttrPane();
        titleTextRotation = new UINumberDragPane(-ROTATION_MAX,ROTATION_MAX);
        if(isXAxis){
            titleTextRotation.populateBean(0.0);
        } else {
            titleTextRotation.populateBean(-ROTATION_MAX);
        }
        Component[][] components = new Component[][]{
                new Component[]{null,null},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Content")),titleContent},
                new Component[]{null,titleUseHtml},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Layout_Position")),titleAlignPane},
                new Component[]{titleTextAttrPane,null},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_TextRotation")),titleTextRotation},
        };

        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(PaneTitleConstants.CHART_STYLE_TITLE_TITLE, panel);
    }

    private UIButtonGroup<Integer> getXAxisTitleAlignPane(){
        Icon[] alignmentIconArray = {BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_left_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_center_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_right_normal.png")};
        Integer[] alignment = new Integer[]{Constants.LEFT, Constants.CENTER, Constants.RIGHT};

        return new UIButtonGroup<Integer>(alignmentIconArray, alignment);
    }
    private UIButtonGroup<Integer> getYAxisTitleAlignPane(){
        Icon[] alignmentIconArray = {BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/v_top_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/v_center_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/v_down_normal.png")};
        Integer[] alignment = new Integer[]{Constants.TOP, Constants.CENTER, Constants.BOTTOM};

        return new UIButtonGroup<Integer>(alignmentIconArray, alignment);
    }

    protected JPanel createLabelPane(double[] row, double[] col){
        showLabel = new UIButtonGroup(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Use_Show"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Hidden")});
        labelTextAttrPane = getChartTextAttrPane();

        labelTextRotation = new UINumberDragPane(-ROTATION_MAX,ROTATION_MAX);
        labelGapStyle = new UIButtonGroup<Integer>(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Automatic"),com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Fixed")});
        labelGapValue = new UITextField();
        Component[][] gapComponents = new Component[][]{
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_TextRotation")), labelTextRotation},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Label_Interval")), labelGapStyle},
        };
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(gapComponents, row, col);
        labelGapValuePane= TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText(""),labelGapValue, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);
        JPanel gapPanel = new JPanel(new BorderLayout());
        gapPanel.add(panel, BorderLayout.CENTER);
        gapPanel.add(labelGapValuePane, BorderLayout.SOUTH);

                Component[][] components = new Component[][]{
                new Component[]{labelTextAttrPane, null},
                new Component[]{gapPanel,null},
        };

        JPanel showLabelPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Label"),showLabel);
        labelPanel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, col);
        labelPanel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        showLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkLabelPane();
            }
        });
        labelGapStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkLabelGapValuePane();
            }
        });
        JPanel jPanel = new JPanel(new BorderLayout());
        jPanel.add(showLabelPane, BorderLayout.NORTH);
        jPanel.add(labelPanel, BorderLayout.CENTER);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(PaneTitleConstants.CHART_STYLE_LABEL_TITLE, jPanel);
    }

    protected ChartTextAttrPane getChartTextAttrPane(){
        return new ChartTextAttrPane(){

            @Override
            protected JPanel getContentPane (JPanel buttonPane) {
                double p = TableLayout.PREFERRED;
                double f = TableLayout.FILL;
                double e = TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH;
                double[] columnSize = {f, e};
                double[] rowSize = {p, p, p};

                return TableLayout4VanChartHelper.createGapTableLayoutPane(getComponents(buttonPane), rowSize, columnSize);
            }
        };
    }

    protected JPanel createLineStylePane(double[] row, double[] col){
        axisLineStyle = createLineComboBox();
        axisLineColor = new ColorSelectBox(100);
        String[] strings = new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Open"),com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Close")};
        AxisTickLineType[] values = new AxisTickLineType[]{AxisTickLineType.TICK_LINE_OUTSIDE, AxisTickLineType.TICK_LINE_NONE};
        mainTick = new UIButtonGroup<AxisTickLineType>(strings, values);
        secondTick = new UIButtonGroup<AxisTickLineType>(strings, values);

        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(getLineStylePaneComponents(), row, col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Line_Style"), panel);
    }

    protected LineComboBox createLineComboBox() {
        return new LineComboBox(CoreConstants.LINE_STYLE_ARRAY_4_AXIS);
    }

    protected Component[][] getLineStylePaneComponents() {
        return new Component[][]{
                new Component[]{null,null} ,
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Type")),axisLineStyle} ,
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Color")),axisLineColor},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Main_Graduation_Line")),mainTick},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Second_Graduation_Line")),secondTick},
        };
    }

    protected JPanel createAxisPositionPane(double[] row, double[] col, boolean isXAxis){
        position = new UIButtonGroup<Integer>(getAxisPositionNameArray(isXAxis), getAxisPositionValueArray(isXAxis));
        reversed = new UIButtonGroup<Boolean>(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_On"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Off")}, new Boolean[]{true, false});
        Component[][] components = new Component[][]{
                new Component[]{null, null},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Label_Position")),position},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_AxisReversed")),reversed},
        } ;

        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components, row, col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Layout_Position"), panel);
    }

    private String[] getAxisPositionNameArray(boolean isXAxis){
        if(isXAxis){
            return new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Top"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Bottom"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Vertical_Zero")};
        } else {
            return new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Layout_Left"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Layout_Right"), com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Vertical_Zero")};
        }
    }

    private Integer[] getAxisPositionValueArray(boolean isXAxis){
        if(isXAxis){
            return new Integer[]{VanChartConstants.AXIS_TOP, VanChartConstants.AXIS_BOTTOM, VanChartConstants.AXIS_VERTICAL_ZERO};
        } else {
            return new Integer[]{VanChartConstants.AXIS_LEFT, VanChartConstants.AXIS_RIGHT, VanChartConstants.AXIS_VERTICAL_ZERO};
        }
    }

    protected JPanel createDisplayStrategy(double[] row, double[] col){
        maxProportion = new UISpinner(0,100,1,30);
        axisLimitSize = new UIButtonGroup<Integer>(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Limit"),com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Not_Limit")});

        JPanel limitSizePane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Area_Size"),axisLimitSize);
        maxProportionPane = TableLayout4VanChartHelper.createGapTableLayoutPane(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Max_Proportion"),maxProportion, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);
        maxProportionPane.setBorder(BorderFactory.createEmptyBorder(0,12,0,0));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(limitSizePane, BorderLayout.NORTH);
        panel.add(maxProportionPane, BorderLayout.CENTER);

        axisLimitSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkMaxProPortionUse();
            }
        });

        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Display_Strategy"), panel);
    }

    protected JPanel createValueStylePane(){
        valueFormatStyle = new UIButtonGroup<Integer>(new String[]{com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Common"),
                com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Custom")});

        valueFormat = createFormatPane();
        checkFormatType();

        htmlLabelPane = new VanChartHtmlLabelPane();

        centerPane = new JPanel(new CardLayout());
        centerPane.add(valueFormat,com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Common"));
        centerPane.add(htmlLabelPane, com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Custom"));

        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p,f};
        double[] rowSize = {p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{null,null},
                new Component[]{new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Axis_Label_Format"), SwingConstants.LEFT), valueFormatStyle},
                new Component[]{null, centerPane},
        };
        JPanel contentPane = TableLayout4VanChartHelper.createGapTableLayoutPane(components,rowSize,columnSize);


        valueFormatStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCardPane();
            }
        });

        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Use_Format"), contentPane);
    }

    protected FormatPane createFormatPane(){
        return new FormatPane(){
            protected Component[][] getComponent (JPanel fontPane, JPanel centerPane, JPanel typePane) {
                typePane.setBorder(BorderFactory.createEmptyBorder());
                return new Component[][]{
                        new Component[]{typePane,null},
                        new Component[]{centerPane, null},
                };
            }
        };
    }

    protected void checkFormatType() {

    }

    protected void checkAllUse() {
        checkCardPane();
        checkLabelPane();
        checkMaxProPortionUse();
    }

    protected void checkCardPane() {
        if(centerPane != null && valueFormatStyle != null){
            CardLayout cardLayout = (CardLayout) centerPane.getLayout();
            if (valueFormatStyle.getSelectedIndex() == 1) {
                cardLayout.show(centerPane,com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Custom"));
            } else {
                cardLayout.show(centerPane, com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Common"));
            }
        }
    }

    protected void checkLabelPane() {
        if(showLabel != null){
            boolean enabled = showLabel.getSelectedIndex() == 0;
            if(labelPanel != null){
                labelPanel.setVisible(enabled);
            }
            if(enabled){
                checkLabelGapValuePane();
            }
        }
    }

    protected void checkLabelGapValuePane() {
        if(labelGapValuePane != null && labelGapStyle != null){
            labelGapValuePane.setVisible(labelGapStyle.getSelectedIndex() == 1);
        }
    }

    //检查最大显示占比是否可用
    private void checkMaxProPortionUse() {
        if(maxProportionPane != null && axisLimitSize != null){
            maxProportionPane.setVisible(axisLimitSize.getSelectedIndex() == 0 && axisLimitSize.isEnabled());
        }
    }

    /**
     * 是否是指定类型
     * @param ob 对象
     * @return 是否是指定类型
     */
    public boolean accept(Object ob){
        return false;
    }

    /**
     * title应该是一个属性，不只是对话框的标题时用到，与其他组件结合时，也会用得到
     * @return 绥化狂标题
     */
    @Override
    public String title4PopupWindow(){
        return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Chart_Category_Axis");
    }

    /**
     * 重置
     */
    public void reset(){

    }
    @Override
    public void populateBean(VanChartAxis axis) {
        populateTitle(axis);

        populateLabel(axis);

        populateLineStyle(axis);

        populatePosition(axis);

        populateDisplayStrategy(axis);

        populateFormat(axis);

        checkAllUse();
    }

    //标题
    private void populateTitle(VanChartAxis axis){
        Title axisTitle = axis.getTitle();
        if(axisTitle != null){
            if (axisTitle.getTextObject() instanceof BaseFormula && titleContent != null) {
                titleContent.populateBean(((BaseFormula) axisTitle.getTextObject()).getContent());
            } else if(titleContent != null){
                titleContent.populateBean(Utils.objectToString(axisTitle.getTextObject()));
            }

            if(titleAlignPane != null){
                titleAlignPane.setSelectedItem(axisTitle.getPosition());
            }
            if(titleTextAttrPane != null){
                titleTextAttrPane.populate(axisTitle.getTextAttr());
            }
            if(titleUseHtml != null){
                titleUseHtml.setSelected(axis.isTitleUseHtml());
            }
            if(titleTextRotation != null){
                titleTextRotation.populateBean((double)axisTitle.getTextAttr().getRotation());
            }
        }
    }

    //标签
    private void populateLabel(VanChartAxis axis){
        if(showLabel != null){
            showLabel.setSelectedIndex(axis.isShowAxisLabel() ? 0 : 1);
        }
        TextAttr labelTextAttr = axis.getTextAttr();
        if(labelTextAttrPane != null){
            labelTextAttrPane.populate(labelTextAttr);
        }
        if(labelTextRotation != null){
            labelTextRotation.populateBean((double)labelTextAttr.getRotation());
        }
        if(labelGapStyle != null){
            labelGapStyle.setSelectedIndex(axis.isAutoLabelGap() ? 0 : 1);
        }
        if(labelGapValue != null){
            labelGapValue.setText(axis.getLabelNumber().getContent());
        }
    }

    //轴线样式
    private void populateLineStyle(VanChartAxis axis){
        if(axisLineStyle != null){
            axisLineStyle.setSelectedLineStyle(axis.getAxisStyle());
        }
        if(axisLineColor != null){
            axisLineColor.setSelectObject(axis.getAxisColor());
        }
        if(mainTick != null){
            mainTick.setSelectedItem(axis.getMainTickLine());
        }
        if(secondTick != null){
            secondTick.setSelectedItem(axis.getSecTickLine());
        }
    }

    //位置
    private void populatePosition(VanChartAxis axis){
        if(position != null){
            position.setSelectedItem(axis.getPosition());
            if(position.getSelectedItem() == null){
                position.setSelectedIndex(1);
            }
        }
        if(reversed != null){
            reversed.setSelectedIndex(axis.hasAxisReversed() == true ? 0 : 1);
        }
    }

    //显示策略
    private void populateDisplayStrategy(VanChartAxis axis) {
        if(axisLimitSize != null){
            axisLimitSize.setSelectedIndex(axis.isLimitSize() ? 0 : 1);
        }
        if(maxProportion != null){
            maxProportion.setValue(axis.getMaxHeight());
        }

    }

    //格式
    protected void populateFormat(VanChartAxis axis) {
        if(valueFormatStyle != null){
            valueFormatStyle.setSelectedIndex(axis.isCommonValueFormat() ? 0 : 1);
        }
        if(valueFormat != null){
            valueFormat.populateBean(axis.getFormat());
        }
        if(htmlLabelPane != null){
            htmlLabelPane.populate(axis.getHtmlLabel());
        }
    }

    public void updateBean(VanChartAxis axis) {
        updateTitle(axis);

        updateLabel(axis);

        updateLineStyle(axis);

        updatePosition(axis);

        updateDisplayStrategy(axis);

        updateFormat(axis);
    }
    //标题
    private void updateTitle(VanChartAxis axis){
        Title axisTitle = axis.getTitle();
        if(axisTitle == null){
            axisTitle = new Title();
            axis.setTitle(axisTitle);
        }

        if(titleContent != null){
            String titleString = titleContent.updateBean();
            Object titleObj;
            if (StableUtils.maybeFormula(titleString)) {
                titleObj = BaseFormula.createFormulaBuilder().build(titleString);
            } else {
                titleObj = titleString;
            }
            axisTitle.setTextObject(titleObj);
        }
        if(titleAlignPane != null){
            axisTitle.setPosition(titleAlignPane.getSelectedItem());
        }

        TextAttr textAttr = axisTitle.getTextAttr();
        if(titleTextAttrPane != null){
            titleTextAttrPane.update(textAttr);
        }
        if(titleUseHtml != null){
            axis.setTitleUseHtml(titleUseHtml.isSelected());
        }
        if(titleTextRotation != null){
            textAttr.setRotation(titleTextRotation.updateBean().intValue());
        }
    }

    //标签
    private void updateLabel(VanChartAxis axis){
        if(showLabel != null){
            axis.setShowAxisLabel(showLabel.getSelectedIndex() == 0);
        }
        TextAttr labelTextAttr = axis.getTextAttr();
        if(labelTextAttrPane != null){
            labelTextAttrPane.update(labelTextAttr);
        }
        if(labelTextRotation != null){
            labelTextAttr.setRotation(labelTextRotation.updateBean().intValue());
        }
        if(labelGapStyle != null){
            axis.setAutoLabelGap(labelGapStyle.getSelectedIndex() == 0);
        }
        if(labelGapValue != null){
            if(axis.isAutoLabelGap()){
                axis.setLabelIntervalNumber(BaseFormula.createFormulaBuilder().build("1"));
            } else {
                axis.setLabelIntervalNumber(BaseFormula.createFormulaBuilder().build(labelGapValue.getText()));
            }
        }
    }

    //轴线样式
    private void updateLineStyle(VanChartAxis axis){
        if(axisLineStyle != null){
            axis.setAxisStyle(axisLineStyle.getSelectedLineStyle());
        }
        if(axisLineColor != null){
            axis.setAxisColor(axisLineColor.getSelectObject());
        }
        if(mainTick != null){
            axis.setMainTickLine(mainTick.getSelectedItem());
        }
        if(secondTick != null){
            axis.setSecTickLine(secondTick.getSelectedItem());
        }
    }

    //位置
    private void updatePosition(VanChartAxis axis){
        if(position != null){
            axis.setPosition(position.getSelectedItem());
        }
        if(reversed != null){
            axis.setAxisReversed(reversed.getSelectedItem());
        }
    }

    //显示策略
    private void updateDisplayStrategy(VanChartAxis axis){
        if(axisLimitSize != null){
            axis.setLimitSize(axisLimitSize.getSelectedIndex() == 0);
        }
        if(maxProportion != null){
            axis.setMaxHeight(maxProportion.getValue());
        }
    }

    protected void updateFormat(VanChartAxis axis) {
        if(valueFormatStyle != null){
            axis.setCommonValueFormat(valueFormatStyle.getSelectedIndex() == 0);
        }
        if(valueFormat != null){
            axis.setFormat(valueFormat.update());
        }
        if(htmlLabelPane != null){
            htmlLabelPane.update(axis.getHtmlLabel());
        }
    }

    /**
     * X坐标轴不同类型切换,new一个新的
     * @param axisName 坐标轴名称
     * @return 新的axis
     */
    public VanChartAxis updateBean(String axisName, int position){
        VanChartAxis axis = new VanChartAxis(axisName, position);
        this.updateBean(axis);
        return axis;
    }

    public VanChartAxis updateBean(){
        return null;
    }
}
