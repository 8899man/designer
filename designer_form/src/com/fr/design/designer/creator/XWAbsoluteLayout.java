/*
 * Copyright(c) 2001-2010, FineReport Inc, All Rights Reserved.
 */
package com.fr.design.designer.creator;

import java.awt.*;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import com.fr.design.designer.beans.AdapterBus;
import com.fr.design.designer.beans.ComponentAdapter;
import com.fr.design.designer.beans.LayoutAdapter;
import com.fr.design.designer.beans.adapters.layout.AbsoluteLayoutAdapter;
import com.fr.design.designer.beans.adapters.layout.FRAbsoluteLayoutAdapter;
import com.fr.design.designer.beans.location.Direction;
import com.fr.design.designer.beans.models.SelectionModel;
import com.fr.design.form.layout.FRAbsoluteLayout;
import com.fr.design.icon.IconPathConstants;
import com.fr.design.mainframe.EditingMouseListener;
import com.fr.design.mainframe.FormDesigner;
import com.fr.form.ui.Connector;
import com.fr.form.ui.Widget;
import com.fr.form.ui.container.WAbsoluteLayout;
import com.fr.form.ui.container.WAbsoluteLayout.BoundsWidget;
import com.fr.general.IOUtils;
import com.fr.general.Inter;

/**
 * @author richer
 * @since 6.5.3
 */
public class XWAbsoluteLayout extends XLayoutContainer {

	private static final int EDIT_BTN_WIDTH = 60;
	private static final int EDIT_BTN_HEIGHT = 24;
	
	private HashMap<Connector,XConnector> xConnectorMap;
	
	public XWAbsoluteLayout() {
		this(new WAbsoluteLayout(),new Dimension());
	}

	public XWAbsoluteLayout(WAbsoluteLayout widget) {
		this(widget,new Dimension());
	}

	public XWAbsoluteLayout(WAbsoluteLayout widget, Dimension initSize) {
		super(widget, initSize);
		this.xConnectorMap =  new HashMap<Connector,XConnector>();
		Connector connector;
		for (int i = 0; i < widget.connectorCount(); i++) {
			connector = widget.getConnectorIndex(i);
			xConnectorMap.put(connector, new XConnector(connector, this));
		}
	}
	
	/**
	 * 增加对齐线
	 * @param connector 对齐线
	 */
	public void addConnector(Connector connector) {
		xConnectorMap.put(connector, new XConnector(connector, this));
		((WAbsoluteLayout) data).addConnector(connector);
	}
	
	public XConnector getXConnector(Connector connector) {
		return xConnectorMap.get(connector);
	}
	
	/**
	 * 去除对齐线
	 * @param connector 对齐线
	 */
	public void removeConnector(Connector connector) {
		((WAbsoluteLayout) data).removeConnector(connector);
		xConnectorMap.remove(connector);
	}
	
	/**
	 * 返回对应的widget容器
	 * @return 返回WAbsoluteLayout
	 */
	@Override
	public WAbsoluteLayout toData() {
		return (WAbsoluteLayout) data;
	}

	@Override
	protected String getIconName() {
		return "layout_absolute.png";
	}

	/**
	 * 返回默认的容器name
	 * @return 返回绝对布局容器名
	 */
	@Override
	public String createDefaultName() {
		return "absolute";
	}

	@Override
	protected void initLayoutManager() {
		this.setLayout(new FRAbsoluteLayout());
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		WAbsoluteLayout layout = (WAbsoluteLayout) data;
		Connector[] connector = layout.getConnector();
		for (int i = 0, size = connector.length; i < size; i++) {
			connector[i].draw(g);
		}
	}
	
	/**
	 * 转换保存组件信息的wlayout为对应的container
	 */
	@Override
	public void convert() {
		isRefreshing = true;
		WAbsoluteLayout abs = toData();
		this.removeAll();
		for (int i = 0, count = abs.getWidgetCount(); i < count; i++) {
			BoundsWidget bw = (BoundsWidget)abs.getWidget(i);
			if (bw != null) {
				Rectangle bounds = bw.getBounds();
				XWidgetCreator comp = (XWidgetCreator)XCreatorUtils.createXCreator(bw.getWidget());
				if (!comp.acceptType(XWParameterLayout.class)) {
					comp.setDirections(Direction.ALL);		
				}
				add(comp);
				comp.setBounds(bounds);	
			}
		}
		isRefreshing = false;
	}
	
	/**
	 * 当前组件zorder位置替换新的控件
	 * @param widget 控件
	 * @param  oldcreator 旧组件
	 * @return 组件
	 */
	@Override
	public XCreator replace(Widget widget, XCreator oldcreator) {
		int i = this.getComponentZOrder(oldcreator);
		if (i != -1) {
			this.toData().replace(new BoundsWidget(widget, oldcreator.getBounds()),
					new BoundsWidget(oldcreator.toData(), oldcreator.getBounds()));
			this.convert();
			return (XCreator) this.getComponent(i);
		}
		return null;
	}

	 /**
     * 组件增加
     * @param e 容器事件
     */
	@Override
	public void componentAdded(ContainerEvent e) {
		if (isRefreshing) {
			return;
		}
		XWidgetCreator creator = (XWidgetCreator) e.getChild();
		WAbsoluteLayout wabs = this.toData();
		if (!creator.acceptType(XWFitLayout.class)) {
			creator.setDirections(Direction.ALL);		
		}
		wabs.addWidget(new BoundsWidget(creator.toData(), creator.getBounds()));
	}
	
	 /**
     * 在设计界面中有组件移除的时候，需要通知WLayout容器重新paint
     * @param e 容器事件
     */
    @Override
    public void componentRemoved(ContainerEvent e) {
        if (isRefreshing) {
            return;
        }
        WAbsoluteLayout wlayout = this.toData();
        XWidgetCreator xwc = ((XWidgetCreator) e.getChild());
        Widget wgt = xwc.toData();
        BoundsWidget bw = new BoundsWidget(wgt, xwc.getBounds());
        wlayout.removeWidget(bw);
    }
    
	@Override
	public Dimension getMinimumSize() {
		return toData().getMinDesignSize();
	}

	@Override
	public LayoutAdapter getLayoutAdapter() {
		return new FRAbsoluteLayoutAdapter(this);
	}

	@Override
	public XLayoutContainer getTopLayout() {
		XLayoutContainer xTopLayout = XCreatorUtils.getParentXLayoutContainer(this).getTopLayout();
		if (xTopLayout != null && !xTopLayout.isEditable()){
			return xTopLayout;
		}
		else{
			return this;
		}
	}

	public void paint(Graphics g) {
		super.paint(g);
		//如果鼠标移动到布局内且布局不可编辑，画出编辑蒙层
		if (isMouseEnter && !this.editable) {
			int x = 0;
			int y = 0;
			int w = getWidth();
			int h = getHeight();

			Graphics2D g2d = (Graphics2D) g;
			Composite oldComposite = g2d.getComposite();
			//画白色的编辑层
			g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 60 / 100.0F));
			g2d.setColor(Color.WHITE);
			g2d.fillRect(x, y, w, h);
			//画编辑按钮所在框
			g2d.setComposite(oldComposite);
			g2d.setColor(new Color(176, 196, 222));
			g2d.fillRect((x + w / 2 - EDIT_BTN_WIDTH / 2), (y + h / 2 - EDIT_BTN_HEIGHT / 2), EDIT_BTN_WIDTH, EDIT_BTN_HEIGHT);
			//画编辑按钮图标
			BufferedImage image = IOUtils.readImage(IconPathConstants.TD_EDIT_ICON_PATH);
			g2d.drawImage(
					image,
					(x + w / 2 - 23),
					(y + h / 2 - image.getHeight() / 2),
					image.getWidth(),
					image.getHeight(),
					null,
					this
			);
			g2d.setColor(Color.BLACK);
			//画编辑文字
			g2d.drawString(Inter.getLocText("Edit"), x + w / 2 - 2, y + h / 2 + 5);
		}
	}

	/**
	 * 响应点击事件
	 *
	 * @param editingMouseListener 鼠标点击，位置处理器
	 * @param e 鼠标点击事件
	 */
	public void respondClick(EditingMouseListener editingMouseListener,MouseEvent e){
		FormDesigner designer = editingMouseListener.getDesigner();
		SelectionModel selectionModel = editingMouseListener.getSelectionModel();
		boolean isEditing = isEditable() || designer.getCursor().getType() == Cursor.HAND_CURSOR || e.getClickCount() == 2;
		setEditable(isEditing);

		selectionModel.selectACreatorAtMouseEvent(e);
		designer.repaint();

		if (editingMouseListener.stopEditing()) {
			if (this != designer.getRootComponent()) {
				ComponentAdapter adapter = AdapterBus.getComponentAdapter(designer, this);
				editingMouseListener.startEditing(this, isEditing ? adapter.getDesignerEditor() : null, adapter);
			}
		}
	}
}