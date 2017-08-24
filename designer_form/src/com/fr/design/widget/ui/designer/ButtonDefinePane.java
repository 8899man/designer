package com.fr.design.widget.ui.designer;

import com.fr.design.designer.creator.XCreator;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.mainframe.widget.accessibles.AccessibleIconEditor;
import com.fr.design.widget.btn.ButtonConstants;
import com.fr.form.ui.Button;
import com.fr.general.Inter;
import com.fr.stable.StableUtils;

import javax.swing.*;
import java.awt.*;

public abstract class ButtonDefinePane<T extends Button> extends AbstractDataModify<T> {
    private UITextField hotkeysTextField;
    private UITextField buttonNameTextField;
    private AccessibleIconEditor iconPane;
    protected UITextField labelNameTextField;


    public ButtonDefinePane(XCreator creator){
        super(creator);
        this.initComponent();
    }

    private void initComponent() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double rowSize[] = {p, p, p, p, p, p, p, p};
        double columnSize[] = {p, f};
        int[][] rowCount = {{1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}};
        iconPane = new AccessibleIconEditor();
        hotkeysTextField = new UITextField();
        buttonNameTextField = new UITextField();
        labelNameTextField = new UITextField();
        Component[] backgroundCompPane = createBackgroundComp();
        Component[] frFont = createFontPane();
        UILabel backgroundLabel = new UILabel(Inter.getLocText("FR-Designer_Background"));
        backgroundLabel.setVerticalAlignment(SwingConstants.TOP);
        Component[][] n_components = {
                {new UILabel(Inter.getLocText("FR-Designer_Button-Name") + ":"), buttonNameTextField},
                {new UILabel(Inter.getLocText("FR-Designer_Label_Name")), labelNameTextField},
                backgroundCompPane,
                frFont,
                {new UILabel(Inter.getLocText("FR-Designer_Icon") + ":"), iconPane},
                {new UILabel(Inter.getLocText("FR-Designer_Button-Hotkeys") + ":"), hotkeysTextField}
        };
        hotkeysTextField.setToolTipText(StableUtils.join(ButtonConstants.HOTKEYS, ","));
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(n_components, rowSize, columnSize, rowCount, 10, 10);
        JPanel boundsPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        boundsPane.add(panel);
        UIExpandablePane advancedPane = new UIExpandablePane(Inter.getLocText("FR-Designer_Advanced"), 280, 20, boundsPane);
        this.add(advancedPane);
    }

    @Override
    public String title4PopupWindow() {
        return "Button";
    }

    public Component[] createBackgroundComp(){
        return new Component[]{null, null};
    }

    public Component[] createFontPane(){
        return new Component[]{null, null};
    }

    @Override
    public void populateBean(T btn) {
        hotkeysTextField.setText(btn.getHotkeys());
        buttonNameTextField.setText(btn.getText());
        labelNameTextField.setText(btn.getLabelName());
        iconPane.setValue(btn.getIconName());
        populateSubButtonPane(btn);
    }

    public abstract void populateSubButtonPane(T e);

    public abstract T updateSubButtonPane();

    @Override
    public T updateBean() {
        T btn = updateSubButtonPane();
        btn.setHotkeys(hotkeysTextField.getText());
        btn.setLabelName(labelNameTextField.getText());
        btn.setIconName((String)iconPane.getValue());
        btn.setText(buttonNameTextField.getText());
        return btn;
    }

}