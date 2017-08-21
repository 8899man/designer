package com.fr.design.widget.ui.designer;

import com.fr.design.data.DataCreatorUI;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UIPropertyTextField;
import com.fr.form.ui.ComboBox;
import com.fr.general.Inter;

import javax.swing.*;
import java.awt.*;

public class ComboBoxDefinePane extends DictEditorDefinePane<ComboBox> {
	private UICheckBox removeRepeatCheckBox;
	private UIPropertyTextField waterMarkField;

	public ComboBoxDefinePane(XCreator xCreator) {
		super(xCreator);
	}

	public UICheckBox createRepeatCheckBox(){
		removeRepeatCheckBox = new UICheckBox(Inter.getLocText("FR-Designer_Widget_No_Repeat"));
		removeRepeatCheckBox.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		return removeRepeatCheckBox;
	}

	public Component[] createWaterMarkPane() {
		waterMarkField = new UIPropertyTextField();
		return new Component[]{new UILabel(Inter.getLocText("FR-Designer_WaterMark")), waterMarkField};
	}

	protected  void populateSubDictionaryEditorBean(ComboBox ob){
		removeRepeatCheckBox.setSelected(ob.isRemoveRepeat());
		waterMarkField.setText(ob.getWaterMark());
		formWidgetValuePane.populate(ob);
	}

	protected  ComboBox updateSubDictionaryEditorBean(){
		ComboBox combo = (ComboBox) creator.toData();
		combo.setWaterMark(waterMarkField.getText());
		combo.setRemoveRepeat(removeRepeatCheckBox.isSelected());
		formWidgetValuePane.update(combo);
		return combo;
	}


	@Override
	public String title4PopupWindow() {
		return "ComboBox";
	}

    @Override
    public DataCreatorUI dataUI() {
        return null;
    }
}