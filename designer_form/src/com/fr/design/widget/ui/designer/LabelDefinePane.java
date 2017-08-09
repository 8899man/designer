package com.fr.design.widget.ui.designer;

import com.fr.base.BaseUtils;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.style.FRFontPane;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.widget.ui.designer.component.FormWidgetValuePane;
import com.fr.form.ui.Label;
import com.fr.general.Inter;
import com.fr.stable.Constants;

import javax.swing.*;
import java.awt.*;


/**
 * Created by ibm on 2017/8/3.
 */
public class LabelDefinePane extends AbstractDataModify<Label> {
    private FormWidgetValuePane formWidgetValuePane;
    private UICheckBox isPageSetupVertically;
    private UICheckBox isStyleAlignmentWrapText;
    private UIButtonGroup hAlignmentPane;
    private FRFontPane frFontPane;

    public LabelDefinePane(XCreator xCreator) {
        super(xCreator);
        initComponent();
    }

    public void initComponent() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        JPanel advancePane = createAdvancePane();
        UIExpandablePane advanceExpandablePane = new UIExpandablePane(Inter.getLocText("FR-Designer_Advanced"), 280, 20, advancePane);
        this.add(advanceExpandablePane, BorderLayout.CENTER);
    }

    public JPanel createAdvancePane() {
        formWidgetValuePane = new FormWidgetValuePane(creator.toData(), false);
        isPageSetupVertically = new UICheckBox(Inter.getLocText("FR-Designer_PageSetup-Vertically"));
        isStyleAlignmentWrapText = new UICheckBox(Inter.getLocText("FR-Designer_StyleAlignment-Wrap_Text"));
        Icon[] hAlignmentIconArray = {BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_left_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_center_normal.png"),
                BaseUtils.readIcon("/com/fr/design/images/m_format/cellstyle/h_right_normal.png"),};
        Integer[] hAlignment = new Integer[]{Constants.LEFT, Constants.CENTER, Constants.RIGHT};
        hAlignmentPane = new UIButtonGroup<Integer>(hAlignmentIconArray, hAlignment);
        hAlignmentPane.setAllToolTips(new String[]{Inter.getLocText("FR-Designer-StyleAlignment_Left")
                , Inter.getLocText("FR-Designer-StyleAlignment_Center"), Inter.getLocText("FR-Designer-StyleAlignment_Right")});
        frFontPane = new FRFontPane();
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[] rowSize = {p, p, p, p, p, p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 3}, {1, 1}, {1, 1}, {1, 1}, {1, 1}};
        UILabel fontLabel = new UILabel(Inter.getLocText("FR-Designer_Font-Size"));
        fontLabel.setVerticalAlignment(SwingConstants.TOP);
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer-Estate_Widget_Value")), formWidgetValuePane},
                new Component[]{isStyleAlignmentWrapText, null},
                new Component[]{isPageSetupVertically, null},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Widget_Display_Position")), hAlignmentPane},
                new Component[]{fontLabel, frFontPane},
        };
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 20, 7);
        JPanel boundsPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        boundsPane.add(panel);
        return boundsPane;
    }

    @Override
    public String title4PopupWindow() {
        return "label";
    }

    @Override
    public void populateBean(Label ob) {
        formWidgetValuePane.populate(ob);
        isStyleAlignmentWrapText.setSelected(ob.isAutoLine());
        isPageSetupVertically.setSelected(ob.isVerticalCenter());
        hAlignmentPane.setSelectedIndex(ob.getTextalign());
        frFontPane.populateBean(ob.getFont());
    }


    @Override
    public Label updateBean() {
        Label layout = (Label) creator.toData();
        formWidgetValuePane.update(layout);
        layout.setAutoLine(isStyleAlignmentWrapText.isSelected());
        layout.setVerticalCenter(isPageSetupVertically.isSelected());
        layout.setTextalign(hAlignmentPane.getSelectedIndex());
        layout.setFont(frFontPane.update(layout.getFont()));
        return layout;
    }
}
