package com.fr.design.designer.properties;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;


import com.fr.design.beans.GroupModel;
import com.fr.design.mainframe.widget.editors.DoubleEditor;
import com.fr.design.mainframe.widget.editors.PropertyCellEditor;
import com.fr.form.ui.container.WSplitLayout;

public class VerticalSplitProperties implements GroupModel {
	private DefaultTableCellRenderer renderer;
	private PropertyCellEditor editor;
	private WSplitLayout layout;

	public VerticalSplitProperties(WSplitLayout wLayout) {
		this.layout = wLayout;
		renderer = new DefaultTableCellRenderer();
		editor = new PropertyCellEditor(new DoubleEditor());
	}

	@Override
	public String getGroupName() {
		return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Form_Vertical_Split_Layout");
	}

	@Override
	public int getRowCount() {
		return 3;
	}

	@Override
	public TableCellRenderer getRenderer(int row) {
		return renderer;
	}

	@Override
	public TableCellEditor getEditor(int row) {
		return editor;
	}

	@Override
	public Object getValue(int row, int column) {
		if (column == 0) {
			switch (row) {
			case 0:
				return com.fr.design.i18n.Toolkit.i18nText("Ratio");
			case 1:
				return com.fr.design.i18n.Toolkit.i18nText("Hgap");
			case 2:
				return com.fr.design.i18n.Toolkit.i18nText("Vgap");
			}
		} else {
			switch (row) {
			case 0:
				return layout.getRatio();
			case 1:
				return layout.getHgap();
			case 2:
				return layout.getVgap();
			}
		}
		return null;
	}

	@Override
	public boolean setValue(Object value, int row, int column) {
		if (column == 0) {
			return false;
		} else {
			switch (row) {
			case 0:
				double r = 0.5;
				if (value != null) {
					r = ((Number) value).doubleValue();
				}
				layout.setRatio(r);
				return true;
			case 1:
				int v = 0;
				if (value != null) {
					v = ((Number) value).intValue();
				}
				layout.setHgap(v);
				return true;
			case 2:
				v = 0;
				if (value != null) {
					v = ((Number) value).intValue();
				}
				layout.setVgap(v);
				return true;
			default:
				return false;
			}
		}
	}

	@Override
	public boolean isEditable(int row) {
		return true;
	}
}
