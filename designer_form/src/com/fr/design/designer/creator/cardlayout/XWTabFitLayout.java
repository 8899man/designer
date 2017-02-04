package com.fr.design.designer.creator.cardlayout;

import java.awt.*;
import java.beans.IntrospectionException;

import javax.swing.border.Border;

import com.fr.base.ScreenResolution;
import com.fr.base.background.ColorBackground;
import com.fr.design.designer.beans.LayoutAdapter;
import com.fr.design.designer.beans.adapters.layout.FRTabFitLayoutAdapter;
import com.fr.design.designer.beans.models.SelectionModel;
import com.fr.design.designer.creator.CRPropertyDescriptor;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.designer.creator.XLayoutContainer;
import com.fr.design.designer.creator.XWFitLayout;
import com.fr.design.form.util.XCreatorConstants;
import com.fr.design.fun.WidgetPropertyUIProvider;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.mainframe.FormDesigner;
import com.fr.design.mainframe.FormHierarchyTreePane;
import com.fr.design.mainframe.widget.editors.ButtonTypeEditor;
import com.fr.design.mainframe.widget.editors.FontEditor;
import com.fr.design.mainframe.widget.editors.ImgBackgroundEditor;
import com.fr.design.mainframe.widget.renderer.FontCellRenderer;
import com.fr.design.utils.gui.LayoutUtils;
import com.fr.form.ui.CardSwitchButton;
import com.fr.form.ui.container.WAbsoluteLayout.BoundsWidget;
import com.fr.form.ui.container.cardlayout.WCardTagLayout;
import com.fr.form.ui.container.cardlayout.WTabFitLayout;
import com.fr.general.Background;
import com.fr.general.FRFont;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.stable.ArrayUtils;
import com.fr.stable.core.PropertyChangeAdapter;


/**
 * @author focus
 * @date 2014-6-23
 */
public class XWTabFitLayout extends XWFitLayout {
	
	private static final int MIN_SIZE = 1;
	// tab布局在拖拽导致的缩放里（含间隔时），如果拖拽宽高大于组件宽高，会导致调整的时候找不到原来的组件
	// 这里先将拖拽之前的宽高先做备份
	private static final Color NORMAL_GRAL = new Color(236,236,236);
	private static final String DEFAULT_FONT_NAME = "SimSun";
	public final static Font DEFAULTFT = new Font("Song_TypeFace",0,12);
	public final static FRFont DEFAULT_FRFT = FRFont.getInstance(DEFAULT_FONT_NAME, 0, 9);
	private Dimension referDim;
	private Background initialBackground;
	private Background overBackground;
	private Background clickBackground;
	private FRFont font;
	private XCardSwitchButton xCardSwitchButton;

	public Dimension getReferDim() {
		return referDim;
	}

	public void setReferDim(Dimension referDim) {
		this.referDim = referDim;
	}

	public Background getInitialBackground() {
		return initialBackground;
	}

	public void setInitialBackground(Background initialBackground) {
		this.initialBackground = initialBackground;
	}

	public Background getOverBackground() {
		return overBackground;
	}

	public void setOverBackground(Background overBackground) {
		this.overBackground = overBackground;
	}

	public Background getClickBackground() {
		return clickBackground;
	}

	public void setClickBackground(Background clickBackground) {
		this.clickBackground = clickBackground;
	}

	@Override
	public FRFont getFont() {
		return font;
	}

	public void setFont(FRFont font) {
		this.font = font;
	}

	public XCardSwitchButton getxCardSwitchButton() {
		return xCardSwitchButton;
	}

	public void setxCardSwitchButton(XCardSwitchButton xCardSwitchButton) {
		this.xCardSwitchButton = xCardSwitchButton;
	}

	public XWTabFitLayout(){
		this(new WTabFitLayout(), new Dimension());
	}
	
	public XWTabFitLayout(WTabFitLayout widget, Dimension initSize) {
		super(widget, initSize);
	}

	/**
	 *  得到属性名
	 * @return 属性名
	 * @throws IntrospectionException
	 */
	public CRPropertyDescriptor[] supportedDescriptor() throws IntrospectionException {
		checkButonType();
		CRPropertyDescriptor[] crp = ((WTabFitLayout) data).isCustomStyle() ? getisCustomStyle() : getisnotCustomStyle();
		return ArrayUtils.addAll(defaultDescriptor(), crp);
	}

	protected CRPropertyDescriptor[] getisCustomStyle() throws IntrospectionException {
		return new CRPropertyDescriptor[]{
				//标题样式
				creatNonListenerStyle(0).setPropertyChangeListener(new PropertyChangeAdapter() {
					@Override
					public void propertyChange() {
						checkButonType();
					}
				}),
				//初始背景
				creatNonListenerStyle(1).setPropertyChangeListener(new PropertyChangeAdapter() {
					@Override
					public void propertyChange() {
						initialBackground = ((WTabFitLayout) data).getInitialBackground();
						xCardSwitchButton.setSelectBackground(null);
						xCardSwitchButton.setSelectBackground(initialBackground);
						CardSwitchButton cardSwitchButton = (CardSwitchButton) xCardSwitchButton.toData();
						cardSwitchButton.setInitialBackground(initialBackground);
					}
				}),
				//鼠标浮动背景
				creatNonListenerStyle(2).setPropertyChangeListener(
						new PropertyChangeAdapter() {
							@Override
							public void propertyChange() {
								overBackground = ((WTabFitLayout) data).getOverBackground();
								CardSwitchButton cardSwitchButton = (CardSwitchButton) xCardSwitchButton.toData();
								cardSwitchButton.setOverBackground(overBackground);
							}
						}),
				//鼠标点击背景
				creatNonListenerStyle(3).setPropertyChangeListener(
						new PropertyChangeAdapter() {
							@Override
							public void propertyChange() {
								clickBackground = ((WTabFitLayout) data).getClickBackground();
								CardSwitchButton cardSwitchButton = (CardSwitchButton) xCardSwitchButton.toData();
								cardSwitchButton.setClickBackground(clickBackground);
							}
						}
				),
				//字体
				creatNonListenerStyle(4).setPropertyChangeListener(
						new PropertyChangeAdapter() {
							@Override
							public void propertyChange() {
								font = ((WTabFitLayout) data).getFont();
								CardSwitchButton cardSwitchButton = (CardSwitchButton) xCardSwitchButton.toData();
								cardSwitchButton.setFont(font);
								UILabel uiLabel = xCardSwitchButton.getLabel();
								uiLabel.setFont(font.applyResolutionNP(ScreenResolution.getScreenResolution()));
								uiLabel.setForeground(font.getForeground());
								xCardSwitchButton.setLabel(uiLabel);
							}
						}),
		};
	}

	protected CRPropertyDescriptor[] getisnotCustomStyle() throws IntrospectionException {
		return new CRPropertyDescriptor[]{
				new CRPropertyDescriptor("customStyle", this.data.getClass()).setI18NName(
						Inter.getLocText(new String[]{"Title", "Style"})).setEditorClass(
						ButtonTypeEditor.class).putKeyValue(XCreatorConstants.PROPERTY_CATEGORY, "Advanced")
						.setPropertyChangeListener(new PropertyChangeAdapter() {
					@Override
					public void propertyChange() {
						checkButonType();
					}
				})
		};

	}

	protected CRPropertyDescriptor creatNonListenerStyle(int i) throws IntrospectionException{
		CRPropertyDescriptor[] crPropertyDescriptors = {
				new CRPropertyDescriptor("customStyle", this.data.getClass()).setI18NName(
						Inter.getLocText(new String[]{"Title", "Style"})).setEditorClass(
						ButtonTypeEditor.class).putKeyValue(XCreatorConstants.PROPERTY_CATEGORY, "Advanced"),
				new CRPropertyDescriptor("initialBackground", this.data.getClass()).setEditorClass(
						ImgBackgroundEditor.class).setI18NName(Inter.getLocText("FR-Designer_Background-Initial")).putKeyValue(
						XCreatorConstants.PROPERTY_CATEGORY, "Advanced"),
				new CRPropertyDescriptor("overBackground", this.data.getClass()).setEditorClass(
						ImgBackgroundEditor.class).setI18NName(Inter.getLocText("FR-Designer_Background-Over")).putKeyValue(
						XCreatorConstants.PROPERTY_CATEGORY, "Advanced"),
				new CRPropertyDescriptor("clickBackground", this.data.getClass()).setEditorClass(
						ImgBackgroundEditor.class).setI18NName(Inter.getLocText("FR-Designer_Background-Click")).putKeyValue(
						XCreatorConstants.PROPERTY_CATEGORY, "Advanced"),
				new CRPropertyDescriptor("font", this.data.getClass()).setI18NName(Inter.getLocText("FR-Designer_FRFont"))
						.setEditorClass(FontEditor.class).setRendererClass(FontCellRenderer.class).putKeyValue(
						XCreatorConstants.PROPERTY_CATEGORY, "Advanced")
		};
		return crPropertyDescriptors[i];
	}

	protected CRPropertyDescriptor[] defaultDescriptor() throws IntrospectionException {
		CRPropertyDescriptor[] crPropertyDescriptors = {
				super.createWidgetNameDescriptor(),
				super.createMarginDescriptor()
		};
		return crPropertyDescriptors;
	}

	private void checkButonType() {
		if (this.xCardSwitchButton == null) {
			//假如为空，默认获取第一个tab的cardBtn属性
			try {
				xCardSwitchButton = (XCardSwitchButton) ((XWCardMainBorderLayout) this.getTopLayout()).getTitlePart().getTagPart().getComponent(0);
			}catch (Exception e){
				FRLogger.getLogger().error(e.getMessage());
			}
			return;
		}
		boolean isStyle = ((WTabFitLayout) data).isCustomStyle();
		Background bg;
		bg = ColorBackground.getInstance(NORMAL_GRAL);
		CardSwitchButton cardSwitchButton = (CardSwitchButton) this.xCardSwitchButton.toData();
		if (!isStyle) {
			this.xCardSwitchButton.setCustomStyle(false);
			this.xCardSwitchButton.setSelectBackground(bg);
			this.xCardSwitchButton.getLabel().setFont(DEFAULTFT);
			cardSwitchButton.setInitialBackground(null);
			cardSwitchButton.setClickBackground(null);
			cardSwitchButton.setOverBackground(null);
			cardSwitchButton.setFont(DEFAULT_FRFT);
		} else {
			Background initialBackground = cardSwitchButton.getInitialBackground();
			bg = initialBackground == null ? bg : initialBackground;
			this.xCardSwitchButton.setSelectBackground(bg);
			this.xCardSwitchButton.setCustomStyle(true);
			cardSwitchButton.setCustomStyle(true);
			if (font != null) {
				cardSwitchButton.setFont(font);
			}
			if (this.initialBackground != null){
				this.xCardSwitchButton.setSelectBackground(this.initialBackground);
				cardSwitchButton.setInitialBackground(this.initialBackground);
			}
			if (this.overBackground != null){
				cardSwitchButton.setOverBackground(this.overBackground);
			}
			if (this.clickBackground != null) {
				cardSwitchButton.setClickBackground(this.clickBackground);
			}
		}
	}

	@Override
	public LayoutAdapter getLayoutAdapter() {
		return new FRTabFitLayoutAdapter(this);
	}
	
	/**
	 * tab布局里删除XWTabFitLayout对应的tab按钮
	 * 
	 * @param creator 当前组件
	 * @param designer 表单设计器
	 * 
	 */
	public void deleteRelatedComponent(XCreator creator,FormDesigner designer){
		//逐层回溯找出相关的layout和对应的tab按钮
    	XWTabFitLayout fitLayout = (XWTabFitLayout)creator;
    	WTabFitLayout fit = (WTabFitLayout) fitLayout.toData();
    	//关联tabfitLayout和tab按钮的index
    	int index = fit.getIndex();
    	//放置tabFitLayout的cardLayout
    	XWCardLayout cardLayout = (XWCardLayout) fitLayout.getBackupParent();
    	XWCardMainBorderLayout mainLayout = (XWCardMainBorderLayout) cardLayout.getBackupParent();
    	XWCardTitleLayout titleLayout = mainLayout.getTitlePart();
    	//放置tab按钮的tagLayout
    	XWCardTagLayout tagLayout = titleLayout.getTagPart();
    	WCardTagLayout tag = (WCardTagLayout) tagLayout.toData();
    	
    	//删除整个tab布局
    	if(tag.getWidgetCount() <= MIN_SIZE){
    		deleteTabLayout(mainLayout,designer);
    		return;
    	}
    	
    	//先删除对应的tab按钮
    	for(int i=0;i<tagLayout.getComponentCount();i++){
    		CardSwitchButton button = tag.getSwitchButton(i);
    		if(button.getIndex()==index){
    			tagLayout.remove(i);
    			break;
    		}
    	}
    	//刷新tab按钮和tabFitLayout的index
    	refreshIndex(tag,cardLayout,index);
    	
    	LayoutUtils.layoutRootContainer(designer.getRootComponent());
	}
	
	
	private void deleteTabLayout(XLayoutContainer mainLayout,FormDesigner designer){
		SelectionModel selectionModel = designer.getSelectionModel();
		if(mainLayout != null){
			selectionModel.setSelectedCreator(mainLayout);
			selectionModel.deleteSelection();
		}
		LayoutUtils.layoutRootContainer(designer.getRootComponent());
		FormHierarchyTreePane.getInstance().refreshRoot();
		selectionModel.setSelectedCreator(designer.getRootComponent());
	}
	
	private void refreshIndex(WCardTagLayout tag,XWCardLayout cardLayout,int index){
    	for(int i=0;i<tag.getWidgetCount();i++){
    		CardSwitchButton button = tag.getSwitchButton(i);
    		XWTabFitLayout tempFit = (XWTabFitLayout) cardLayout.getComponent(i);
    		WTabFitLayout tempFitLayout = (WTabFitLayout) tempFit.toData();
    		int currentFitIndex = tempFitLayout.getIndex();
    		int buttonIndex = button.getIndex();
    		if(buttonIndex > index){
    			button.setIndex(--buttonIndex);
    		}
    		if(currentFitIndex > index){
    			tempFitLayout.setIndex(--currentFitIndex);
    		}
    	}
	}
	
	/**
	 * tab布局里切换到相应的tab按钮
	 * @param comp 当前组件
	 * void
	 */
    public void seleteRelatedComponent(XCreator comp){
    	XWTabFitLayout fitLayout = (XWTabFitLayout)comp;
    	WTabFitLayout fit = (WTabFitLayout) fitLayout.toData();
    	int index = fit.getIndex();
    	XWCardLayout cardLayout = (XWCardLayout) fitLayout.getBackupParent();
    	XWCardMainBorderLayout mainLayout = (XWCardMainBorderLayout) cardLayout.getBackupParent();
    	XWCardTitleLayout titleLayout = mainLayout.getTitlePart();
    	XWCardTagLayout tagLayout = titleLayout.getTagPart();
    	WCardTagLayout layout = (WCardTagLayout) tagLayout.toData();
    	for(int i=0;i<tagLayout.getComponentCount();i++){
    		CardSwitchButton button = layout.getSwitchButton(i);
    		button.setShowButton(button.getIndex()==index);
    	}
    }
    
    
    /**
	 * 寻找最近的为自适应布局的父容器
	 * 
	 * @return 布局容器
	 * 
	 *
	 * @date 2014-12-30-下午3:15:28
	 * 
	 */
    public XLayoutContainer findNearestFit(){
    	XLayoutContainer parent = this.getBackupParent();
    	return parent == null ? null : parent.findNearestFit();
    } 
    
	/**
	 * 非顶层自适应布局的缩放
	 * @param percent 百分比
	 */
	public void adjustCompSize(double percent) {
		this.adjustCreatorsWhileSlide(percent);
	}
	
	/**
	 * 该布局需要隐藏，无需对边框进行操作
	 * @param border 边框
	 * 
	 */
    public void setBorder(Border border) {
    	return;
    }
    
	/**
	 * 按照百分比缩放内部组件宽度
	 * 
	 * @param percent 宽度变化的百分比
	 */
	public void adjustCreatorsWidth(double percent) {
		if (this.getComponentCount()==0) {
			// 初始化没有拖入控件时，实际宽度依然调整
			this.toData().setContainerWidth(this.getWidth());
			return;
		}
		updateWidgetBackupBounds();
		int gap = toData().getCompInterval();
		if (gap >0 && hasCalGap) {
			moveCompInterval(getAcualInterval());
			updateCompsWidget();
		}
		layoutWidthResize(percent); 
		if (percent < 0 && this.getNeedAddWidth() > 0) {
			this.setSize(this.getWidth()+this.getNeedAddWidth(), this.getHeight());
			modifyEdgemostCreator(true);
		}
		addCompInterval(getAcualInterval());
		// 本次缩放结束，参照宽高清掉
		this.setReferDim(null);
		updateCompsWidget();
		this.toData().setContainerWidth(this.getWidth());
		updateWidgetBackupBounds();
		LayoutUtils.layoutContainer(this);
	}
	
	
	/**
	 * 布局容器高度手动修改时，
	 * 同时调整容器内的组件们,缩小时需要考虑有的组件高度不满足缩小高度
	 * @param percent 高度变化的百分比
	 */
	public void adjustCreatorsHeight(double percent) {
		if (this.getComponentCount()==0) {
			//调整高度后，wlayout那边记录下
			this.toData().setContainerHeight(this.getHeight());
			return;
		}
		updateWidgetBackupBounds();
		int gap = toData().getCompInterval();
		if (gap >0 && hasCalGap) {
			moveCompInterval(getAcualInterval());
			updateCompsWidget();
		}
		layoutHeightResize(percent);
		if (percent < 0 && this.getNeedAddHeight() > 0) {
			this.setSize(this.getWidth(), this.getHeight()+this.getNeedAddHeight());
			modifyEdgemostCreator(false);
		}
		addCompInterval(getAcualInterval());
		updateCompsWidget();
		this.toData().setContainerHeight(this.getHeight());
		updateWidgetBackupBounds();
		LayoutUtils.layoutContainer(this);
	}
	
	public XLayoutContainer getOuterLayout(){
		XWCardLayout cardLayout = (XWCardLayout) this.getBackupParent();
		return cardLayout.getBackupParent();
	}
	
	// 更新内部组件的widget
	private void updateCompsWidget(){
		for(int m=0;m<this.getComponentCount();m++){
			XCreator childCreator = this.getXCreator(m);
			BoundsWidget wgt = this.toData().getBoundsWidget(childCreator.toData());
			wgt.setBounds(this.getComponent(m).getBounds());
			wgt.setBackupBounds(this.getComponent(m).getBounds());
		}
	}
	
    /**
     * 去除原有的间隔
     * @param gap 间隔
     */
    public void moveCompInterval(int gap) {
    	if (gap == 0) {
    		return;
    	}
    	int val = gap/2;
    	
    	// 比较组件大小和tab布局的大小的参照宽高
    	double referWidth = getReferWidth();
    	double referHeight = getReferHeight();
    	
    	for (int i=0, len=this.getComponentCount(); i<len; i++) {
    		Component comp = this.getComponent(i);
    		Rectangle rec = comp.getBounds();
    		Rectangle bound = new Rectangle(rec);
    		if (rec.x > 0) {
    			bound.x -= val;
    			bound.width += val;
    		}
    		if (rec.width+rec.x < referWidth) {
    			bound.width  += val;
    		}
    		if (rec.y > 0) {
    			bound.y -= val;
    			bound.height += val;
    		}
    		if (rec.height+rec.y < referHeight) {
    			bound.height += val;
    		}
    		comp.setBounds(bound);
    	}
	
    	this.hasCalGap = false;
    }
    
    private double getReferWidth(){
    	if(referDim != null){
    		return referDim.getWidth();
    	}else{
    		return this.getWidth();
    	}
    }
    
    private double getReferHeight(){
    	if(referDim != null){
    		return referDim.getHeight();
    	}else{
    		return this.getHeight();
    	}
    }
    
    
    /**
     * 间隔大于0时，界面处加上间隔
     * 界面的间隔是针对显示，实际保存的大小不受间隔影响
     * ps:改变布局大小或者拖入、删除、拉伸都要重新考虑间隔
     * @param gap 间隔
     */
    public void addCompInterval(int gap) {
    	if (gap == 0) {
    		return;
    	}
    	int val = gap/2;
    	double referWidth = getReferWidth();
    	double referHeight = getReferHeight();
    	for (int i=0, len=this.getComponentCount(); i<len; i++) {
    		Component comp = this.getComponent(i);
    		Rectangle rec = comp.getBounds();
    		Rectangle bound = new Rectangle(rec);
    		if (rec.x > 0) {
    			bound.x += val;
    			bound.width -= val;
    		}
    		if (rec.width+rec.x < referWidth) {
    			bound.width  -= val;
    		}
    		if (rec.y > 0) {
    			bound.y += val;
    			bound.height -= val;
    		}
    		if (rec.height+rec.y < referHeight) {
    			bound.height -= val;
    		}
    		comp.setBounds(bound);
    	}
	
    	this.hasCalGap = true;
    }

	@Override
	public XLayoutContainer getTopLayout() {
		return this.getBackupParent().getTopLayout();
	}

	/**
	 * 重写这个方法，解决tab块底下仍然显示手机重布局的bug
	 * @return
	 */
	@Override
	public WidgetPropertyUIProvider[] getWidgetPropertyUIProviders() {
		return new WidgetPropertyUIProvider[0];
	}
}