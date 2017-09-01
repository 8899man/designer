package com.fr.design.mainframe;

import com.fr.base.FRContext;
import com.fr.design.actions.utils.ReportActionUtils;
import com.fr.design.dialog.BasicDialog;
import com.fr.design.dialog.BasicPane;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.present.CellWriteAttrPane;
import com.fr.design.widget.WidgetPane;
import com.fr.form.ui.NoneWidget;
import com.fr.form.ui.Widget;
import com.fr.general.FRLogger;
import com.fr.general.Inter;
import com.fr.grid.selection.CellSelection;
import com.fr.grid.selection.FloatSelection;
import com.fr.grid.selection.Selection;
import com.fr.privilege.finegrain.WidgetPrivilegeControl;
import com.fr.report.cell.CellElement;
import com.fr.report.cell.DefaultTemplateCellElement;
import com.fr.report.cell.TemplateCellElement;
import com.fr.report.elementcase.TemplateElementCase;

import java.awt.*;

/**
 * Created by ibm on 2017/7/20.
 */
public class CellWidgetPropertyPane extends BasicPane {

    private static CellWidgetPropertyPane singleton;

    private TemplateCellElement cellElement;
    private WidgetPane cellEditorDefPane;
    private ElementCasePane ePane;

    public static CellWidgetPropertyPane getInstance(){
        if (singleton == null) {
            singleton = new CellWidgetPropertyPane();
        }
        return singleton;
    }

    public CellWidgetPropertyPane() {
        this.setLayout(FRGUIPaneFactory.createBorderLayout());
//        this.addAttributeChangeListener(listener);
//        cellEditorDefPane = new WidgetPane(elementCasePane);
    }

    public void clear (){
        singleton = null;
    }

    public WidgetPane getCellEditorDefPane() {
        return cellEditorDefPane;
    }

    public void setCellEditorDefPane(WidgetPane cellEditorDefPane) {
        this.cellEditorDefPane = cellEditorDefPane;
    }


    @Override
    protected String title4PopupWindow() {
        return Inter.getLocText("FR-Designer-Widget_Settings");
    }

    public void populate(TemplateCellElement cellElement) {
        if (cellElement == null) {// 利用默认的CellElement.
            cellElement = new DefaultTemplateCellElement(0, 0, null);
        }

        Widget cellWidget = cellElement.getWidget();


        // 这里进行克隆的原因是为了保留原始的Widget以便和新的Widget做比较来判断是否发生了改变
        if (cellWidget != null) {
            try {
                cellWidget = (Widget) cellWidget.clone();
            } catch (CloneNotSupportedException e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
        }
        cellEditorDefPane.populate(cellWidget);

    }


    public void reInit(ElementCasePane ePane){
        this.ePane = ePane;
        cellEditorDefPane = new WidgetPane(ePane);
        this.removeAll();
        this.add(cellEditorDefPane, BorderLayout.CENTER);

        CellSelection cs = (CellSelection) ePane.getSelection();
        final TemplateElementCase tplEC = ePane.getEditingElementCase();
        TemplateCellElement editCellElement = tplEC.getTemplateCellElement(cs.getColumn(), cs.getRow());
        if (editCellElement == null) {
            editCellElement = new DefaultTemplateCellElement(cs.getColumn(), cs.getRow());
        }
        this.cellElement = editCellElement;
        this.populate(editCellElement);
    }

    public void populate(ElementCasePane ePane) {
        Selection editingSelection = ePane.getSelection();
        editingSelection.populateWidgetPropertyPane(ePane);
    }

    public void update() {
        if (cellElement == null) {// 利用默认的CellElement.
            return;
        }
        final CellSelection finalCS = (CellSelection) ePane.getSelection();
        final TemplateElementCase tplEC = ePane.getEditingElementCase();
        ReportActionUtils.actionIterateWithCellSelection(finalCS, tplEC, new ReportActionUtils.IterAction() {
            public void dealWith(CellElement editCellElement) {
                Widget cellWidget = cellEditorDefPane.update();
                // p:最后把这个cellEditorDef设置到CellGUIAttr.
                TemplateCellElement cellElement = (TemplateCellElement) editCellElement;
                if (cellWidget instanceof NoneWidget) {
                    cellElement.setWidget(null);
                } else {
                    if (cellElement.getWidget() != null) {
                        cellWidget = upDateWidgetAuthority(cellElement, cellWidget);
                    }
                    cellElement.setWidget(cellWidget);
                }
            }
        });
        DesignerContext.getDesignerFrame().getSelectedJTemplate().fireTargetModified();
    }


    private Widget upDateWidgetAuthority(TemplateCellElement cellElement, Widget newWidget) {
        try {
            Widget oldWidget = (Widget) cellElement.getWidget().clone();
            if (newWidget.getClass() == oldWidget.getClass()) {
                newWidget.setWidgetPrivilegeControl((WidgetPrivilegeControl) oldWidget.getWidgetPrivilegeControl().clone());
            }
        } catch (Exception e) {
            FRLogger.getLogger().error(e.getMessage());
        }
        return newWidget;
    }

    @Override
    /**
     *检测是否有效
     */
    public void checkValid() throws Exception {
        this.cellEditorDefPane.checkValid();
    }



}