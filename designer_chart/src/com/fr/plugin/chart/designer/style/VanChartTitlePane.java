package com.fr.plugin.chart.designer.style;

import com.fr.base.BaseUtils;
import com.fr.base.Formula;
import com.fr.base.Style;
import com.fr.base.Utils;
import com.fr.chart.base.TextAttr;
import com.fr.design.formula.TinyFormulaPane;
import com.fr.design.gui.frpane.UIBubbleFloatPane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ibutton.UIToggleButton;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.chart.PaneTitleConstants;
import com.fr.design.mainframe.chart.gui.style.ChartTextAttrPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.attr.VanChartTitle;
import com.fr.plugin.chart.designer.AbstractVanChartScrollPane;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.VanChartFloatPositionPane;
import com.fr.plugin.chart.designer.component.background.VanChartBackgroundWithOutShadowWithRadiusPane;
import com.fr.plugin.chart.vanchart.VanChart;
import com.fr.stable.Constants;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// 属性表-样式 标题界面
public class VanChartTitlePane extends AbstractVanChartScrollPane<VanChart> {
    private static final long serialVersionUID = -2438898431228882682L;

    private static final int WIDTH = 165;
    private static final int HEIGHT = 100;
    private static final int GAP = 20;

    private UICheckBox isTitleVisible;
    private JPanel titlePane;

    private TinyFormulaPane titleContent;
    private ChartTextAttrPane textAttrPane;
    private UIButtonGroup<Integer> alignmentPane;
    private VanChartBackgroundWithOutShadowWithRadiusPane backgroundPane;
    private UIToggleButton useHtml;
    private UIToggleButton customFloatPositionButton;
    private VanChartFloatPositionPane customFloatPositionPane;
    private UIButtonGroup<Integer> limitSize;
    private UISpinner maxProportion;
    private JPanel maxProportionPane;

    protected VanChartStylePane parent;


    public VanChartTitlePane(VanChartStylePane parent) {
        super();
        this.parent = parent;
    }


    private class ContentPane extends JPanel {

        private static final long serialVersionUID = 5601169655874455336L;

        public ContentPane() {
            initComponents();
        }

        private void initComponents() {
            isTitleVisible = new UICheckBox(Inter.getLocText("Chart-Title_Is_Visible"));
            titlePane = createTitlePane();

            double p = TableLayout.PREFERRED;
            double f = TableLayout.FILL;
            double[] columnSize = {f};
            double[] rowSize = {p, p, p};
            Component[][] components = new Component[][]{
                    new Component[]{isTitleVisible},
                    new Component[]{titlePane}
            };

            JPanel panel = TableLayoutHelper.createTableLayoutPane(components, rowSize, columnSize);
            this.setLayout(new BorderLayout());
            this.add(panel,BorderLayout.CENTER);

            isTitleVisible.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    checkTitlePaneUse();
                }
            });
        }
    }

    private JPanel createTitlePane(){
        backgroundPane = new VanChartBackgroundWithOutShadowWithRadiusPane();


        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double e = TableLayout4VanChartHelper.EDIT_AREA_WIDTH;
        double[] columnSize = {p, f};
        double[] column = {f, e};
        double[] rowSize = {p,p,p,p,p,p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{createTitleContentPane(new double[]{p,p,p},column),null},
                new Component[]{createTitlePositionPane(new double[]{p,p,p},column),null},
                new Component[]{createTitleStylePane(),null},
                new Component[]{TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Background"), backgroundPane),null},
                new Component[]{createDisplayStrategy(),null}
        };

        return TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
    }

    private JPanel createTitleContentPane(double[] row, double[] col){
        titleContent = new TinyFormulaPane();
        useHtml = new UIToggleButton(Inter.getLocText("Plugin-ChartF_Html"));
        Component[][] components = new Component[][]{
                new Component[]{null ,null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Text"), SwingConstants.LEFT), titleContent},
                new Component[]{null ,useHtml},
        };
        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components,row,col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_Content"), panel);
    }

    private JPanel createTitlePositionPane(double[] row, double[] col){
        Icon[] alignmentIconArray = {BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_left_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_center_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_right_normal.png")};
        Integer[] alignment = new Integer[]{Constants.LEFT, Constants.CENTER, Constants.RIGHT};

        alignmentPane = new UIButtonGroup<Integer>(alignmentIconArray, alignment);
        customFloatPositionButton = new UIToggleButton(Inter.getLocText("Plugin-ChartF_CustomFloatPosition"));
        customFloatPositionButton.setEventBannded(true);

        Component[][] components = new Component[][]{
                new Component[]{null,null},
                new Component[]{new UILabel(Inter.getLocText("BorderLayout-Constraints"), SwingConstants.LEFT),alignmentPane},
                new Component[]{null,customFloatPositionButton}
        };

        customFloatPositionPane =  new VanChartFloatPositionPane();

        initPositionListener();

        JPanel panel = TableLayout4VanChartHelper.createGapTableLayoutPane(components,row,col);
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("FR-Chart_Layout"), panel);
    }

    private JPanel createTitleStylePane(){
        textAttrPane = new ChartTextAttrPane();
        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("FR-Designer-Widget_Style"), textAttrPane);
    }

    private JPanel createDisplayStrategy(){
        maxProportion = new UISpinner(0,100,1,30);
        limitSize = new UIButtonGroup<Integer>(new String[]{Inter.getLocText("Plugin-ChartF_Limit"),Inter.getLocText("Plugin-ChartF_NotLimit")});

        JPanel limitSizePane = TableLayout4VanChartHelper.createGapTableLayoutPane(Inter.getLocText("Plugin-ChartF_AreaSize"),limitSize);
        maxProportionPane = TableLayout4VanChartHelper.createGapTableLayoutPane(Inter.getLocText("Plugin-ChartF_MaxProportion"),maxProportion, TableLayout4VanChartHelper.SECOND_EDIT_AREA_WIDTH);
        maxProportionPane.setBorder(BorderFactory.createEmptyBorder(0,12,0,0));
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(limitSizePane, BorderLayout.NORTH);
        panel.add(maxProportionPane, BorderLayout.CENTER);

        limitSize.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkMaxProPortionUse();
            }
        });

        return TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Plugin-ChartF_DisplayStrategy"), panel);
    }

    private void initPositionListener(){

        alignmentPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                customFloatPositionButton.setSelected(false);
                checkDisplayStrategyUse();
            }
        });

        customFloatPositionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!customFloatPositionButton.isSelected()) {
                    customFloatPositionButton.setSelected(true);
                    checkDisplayStrategyUse();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                alignmentPane.setSelectedIndex(-1);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);

                if (customFloatPositionPane == null) {
                    customFloatPositionPane = new VanChartFloatPositionPane();
                }
                Point comPoint = customFloatPositionButton.getLocationOnScreen();
                Point arrowPoint = new Point(comPoint.x + customFloatPositionButton.getWidth() / 2 - GAP, comPoint.y + customFloatPositionButton.getHeight());
                UIBubbleFloatPane<Style> pane = new UIBubbleFloatPane(Constants.TOP, arrowPoint, customFloatPositionPane, WIDTH, HEIGHT) {
                    @Override
                    public void updateContentPane() {
                        parent.attributeChanged();
                    }
                };
                pane.show(VanChartTitlePane.this, null);
            }
        });
    }

    private void checkAllUse() {
        checkTitlePaneUse();
        checkDisplayStrategyUse();
        this.repaint();
    }

    // 检查标题界面是否可用.
    private void checkTitlePaneUse() {
        isTitleVisible.setVisible(true);
        isTitleVisible.setEnabled(true);
        titlePane.setVisible(isTitleVisible.isSelected());
    }

    //检查显示策略界面是否可用
    private void checkDisplayStrategyUse() {
        limitSize.setEnabled(!customFloatPositionButton.isSelected());
        checkMaxProPortionUse();
    }

    //检查最大显示占比是否可用
    private void checkMaxProPortionUse() {
        maxProportion.setVisible(limitSize.getSelectedIndex() == 0 && limitSize.isEnabled());
        maxProportionPane.setVisible(limitSize.getSelectedIndex() == 0 && limitSize.isEnabled());
    }

    /**
     * 弹出框的界面标题
     * @return 界面标题
     */
    public String title4PopupWindow() {
        return PaneTitleConstants.CHART_STYLE_TITLE_TITLE;
    }

    @Override
    protected JPanel createContentPane() {
        return new ContentPane();
    }

    @Override
    public void populateBean(VanChart chart) {
        VanChartTitle title = (VanChartTitle)chart.getTitle();
        if (title == null) {
            return;
        }
        isTitleVisible.setSelected(title.isTitleVisible());
        if (title.getTextObject() instanceof Formula) {
            titleContent.populateBean(((Formula) title.getTextObject()).getContent());
        } else {
            titleContent.populateBean(Utils.objectToString(title.getTextObject()));
        }
        if(!title.isFloating()){
            alignmentPane.setSelectedItem(title.getPosition());
        } else {
            alignmentPane.setSelectedIndex(-1);
        }
        TextAttr textAttr = title.getTextAttr();
        if (textAttr == null) {
            textAttr = new TextAttr();
        }
        textAttrPane.populate(textAttr);
        backgroundPane.populate(title);

        useHtml.setSelected(title.isUseHtml());
        customFloatPositionButton.setSelected(title.isFloating());
        customFloatPositionPane.setFloatPosition_x(title.getFloatPercentX());
        customFloatPositionPane.setFloatPosition_y(title.getFloatPercentY());
        limitSize.setSelectedIndex(title.isLimitSize() ? 0 : 1);
        maxProportion.setValue(title.getMaxHeight());

        checkAllUse();
    }

    @Override
    public void updateBean(VanChart chart) {
        if (chart == null) {
            chart = new VanChart();
        }
        VanChartTitle title = (VanChartTitle)chart.getTitle();
        if (title == null) {
            title = new VanChartTitle(StringUtils.EMPTY);
        }

        title.setTitleVisible(isTitleVisible.isSelected());
        String titleString = titleContent.updateBean();
        Object titleObj;
        if (StableUtils.maybeFormula(titleString)) {
            titleObj = new Formula(titleString);
        } else {
            titleObj = titleString;
        }
        title.setTextObject(titleObj);
        TextAttr textAttr = title.getTextAttr();
        if (textAttr == null) {
            textAttr = new TextAttr();
        }
        if(!customFloatPositionButton.isSelected()){
            title.setPosition(alignmentPane.getSelectedItem());
        }
        title.setUseHtml(useHtml.isSelected());
        title.setFloating(customFloatPositionButton.isSelected());
        title.setLimitSize(limitSize.getSelectedIndex() == 0);
        title.setMaxHeight(maxProportion.getValue());
        title.setFloatPercentX(customFloatPositionPane.getFloatPosition_x());
        title.setFloatPercentY(customFloatPositionPane.getFloatPosition_y());
        textAttrPane.update(textAttr);
        backgroundPane.update(title);
    }

}