package com.fr.poly;

import com.fr.design.event.UIObserver;
import com.fr.design.event.UIObserverListener;
import com.fr.design.foldablepane.UIExpandablePane;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.mainframe.widget.BasicPropertyPane;
import com.fr.design.widget.WidgetBoundsPaneFactory;
import com.fr.general.Inter;
import com.fr.poly.group.PolyBoundsGroup;
import com.fr.report.poly.TemplateBlock;

import javax.swing.*;
import java.awt.*;

public class PolyBlockProperTable extends JPanel {
	private PolyDesigner designer;
	private UISpinner x;
	private UISpinner y;
	private UISpinner width;
	private UISpinner height;
	private BasicPropertyPane blockPropertyPane;

	private boolean isPopulating = false;

	public PolyBlockProperTable() {
		initPropertyPane();
		initListener(this);
	}

	private void initPropertyPane() {
		this.setLayout(new BorderLayout());

		blockPropertyPane = new BasicPropertyPane();
		UIExpandablePane basicPane = new UIExpandablePane(Inter.getLocText("FR-Designer_Basic"), 280, 24, blockPropertyPane);
		this.add(basicPane, BorderLayout.NORTH);

		x = new UISpinner(0, 1200, 1);
		y = new UISpinner(0, 1200, 1);
		width = new UISpinner(0, 1200, 1);
		height = new UISpinner(0, 1200, 1);
		UIExpandablePane boundsPane = WidgetBoundsPaneFactory.createAbsoluteBoundsPane(x, y, width, height);
		this.add(boundsPane, BorderLayout.CENTER);
	}

	private void initListener(Container parentComponent) {
		for (int i = 0; i < parentComponent.getComponentCount(); i++) {
			Component tmpComp = parentComponent.getComponent(i);

			if (tmpComp instanceof Container) {
				initListener((Container) tmpComp);
			}
//			if (tmpComp instanceof GlobalNameObserver) {
//				((GlobalNameObserver) tmpComp).registerNameListener(new GlobalNameListener() {
//					public void setGlobalName(String name) {
//						globalName = name;
//					}
//
//					public String getGlobalName() {
//						return globalName;
//					}
//				});
//			}
			if (tmpComp instanceof UIObserver) {
				((UIObserver) tmpComp).registerChangeListener(new UIObserverListener() {
					@Override
					public void doChange() {
						update();
					}
				});
			}
		}
	}

	/**
	 * 初始化属性表
	 * 
	 * @param source 指定的属性来源
	 * 
	 */
	public void initPropertyGroups(Object source) {
//		groups = new ArrayList<PropertyGroup>();
		if (source instanceof TemplateBlock) {
			TemplateBlock block = (TemplateBlock) source;
//			PolyNameGroup namegroup = new PolyNameGroup(block);
			blockPropertyPane.getWidgetNameField().setText(block.getBlockName());
//			groups.add(new PropertyGroup(namegroup));
			final PolyBoundsGroup boundsgroup = new PolyBoundsGroup(block, designer.getTarget());

			x.setValue((int)boundsgroup.getValue(0, 1));
			y.setValue((int)boundsgroup.getValue(1, 1));
			width.setValue((int)boundsgroup.getValue(2, 1));
			height.setValue((int)boundsgroup.getValue(3, 1));
//			groups.add(new PropertyGroup(boundsgroup));
		}
//		TableModel model = new BeanTableModel();
//		setModel(model);
		this.repaint();
	}

	/**
	 * 触发组件属性编辑事件
	 * 
	 */
	public void firePropertyEdit() {
		designer.fireTargetModified();
	}

	public void populate(PolyDesigner designer) {
		isPopulating = true;
		this.designer = designer;
		initPropertyGroups(this.designer.getEditingTarget());
		isPopulating = false;
	}

	public void update() {
		TemplateBlock block = this.designer.getEditingTarget();
		if (isPopulating || block == null) {
			return;
		}
		block.setBlockName(blockPropertyPane.getWidgetNameField().getText());
		PolyBoundsGroup boundsgroup = new PolyBoundsGroup(block, designer.getTarget());
		boundsgroup.setValue(x.getValue(), 0, 1);
		boundsgroup.setValue(y.getValue(), 1, 1);
		boundsgroup.setValue(width.getValue(), 2, 1);
		boundsgroup.setValue(height.getValue(), 3, 1);
		firePropertyEdit();
	}
}