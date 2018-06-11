package com.fr.start;

import com.fr.general.IOUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在设计器框架加载之前, 先用多个线程把相关的资源文件全部load到缓存中.
 */
public class ImagePreLoader {

    public static final int THRESHOLD = 50;

    public ImagePreLoader() {
        ExecutorService service = Executors.newCachedThreadPool();

        int len = preLoadImages.length;
        int start = 0;
        for (int i = 0; i < len; i++) {
            if (i != 0 && i % THRESHOLD == 0) {
                loadImage(start,i, service);
                start = i;
            }
            if (len - i < THRESHOLD) {
                loadImage(start, len, service);
                break;
            }

        }
        service.shutdown();
    }

    private void loadImage(final int start, final int end, ExecutorService service) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    IOUtils.readImageWithCache(preLoadImages[i]);
                }
            }
        });
    }


    private String[] preLoadImages = {
            "com/fr/design/images/m_file/formExport.png",
            "com/fr/base/images/oem/cpt.png",
            "com/fr/design/images/data/store_procedure.png",
            "/com/fr/design/images/m_file/preview.png",
            "com/fr/base/images/cell/blank.gif",
            "com/fr/design/images/data/dock/serverdatabase.png",
            "/com/fr/design/images/data/dock/serverclasstabledata.png",
            "/com/fr/design/images/data/dock/serverdatatable.png",
            "/com/fr/design/images/data/file.png",
            "/com/fr/design/images/data/tree.png",
            "/com/fr/design/images/buttonicon/minus.png",
            "/com/fr/design/images/buttonicon/plus.png",
            "/com/fr/design/images/data/multi.png",
            "/com/fr/design/images/data/store_procedure.png",
            "com/fr/design/images/buttonicon/arrowdown.png",
            "com/fr/design/images/buttonicon/arrowup.png",
            "com/fr/design/images/buttonicon/yes.png",
            "com/fr/design/images/buttonicon/select_item.png",
            "com/fr/design/images/buttonicon/prewidget.png",
            "com/fr/design/images/buttonicon/editn.png",
            "com/fr/design/images/buttonicon/editp.png",
            "com/fr/design/images/buttonicon/hiden.png",
            "com/fr/design/images/buttonicon/hidep.png",
            "com/fr/design/images/buttonicon/viewn.png",
            "com/fr/design/images/buttonicon/viewp.png",
            "com/fr/design/images/buttonicon/run24.png",
            "com/fr/design/images/buttonicon/runs.png",
            "com/fr/design/mainframe/alphafine/images/smallsearch.png",
            "com/fr/design/images/buttonicon/pageb24.png",
            "com/fr/design/images/buttonicon/writeb24.png",
            "com/fr/design/images/buttonicon/anab24.png",
            "com/fr/design/images/buttonicon/pages.png",
            "com/fr/design/images/buttonicon/writes.png",
            "com/fr/design/images/buttonicon/anas.png",
            "com/fr/design/images/buttonicon/refresh.png",
            "com/fr/design/images/gui/color/foreground.png",
            "com/fr/design/images/buttonicon/history.png",
            "com/fr/design/images/m_file/close.png",
            "com/fr/design/images/m_file/edit.png",
            "com/fr/design/images/data/search.png",
            "com/fr/design/images/data/black_search.png",
            "com/fr/design/images/data/source/delete.png",
            "com/fr/design/images/control/edit.png",
            "com/fr/design/images/control/edit_white.png",
            "com/fr/design/images/buttonicon/prevew_down_icon.png",
            "com/fr/design/images/m_report/close.png",
            "com/fr/design/images/m_report/close_over.png",
            "com/fr/design/images/m_report/close_press.png",
            "com/fr/design/images/buttonicon/save.png",
            "com/fr/design/images/buttonicon/undo.png",
            "com/fr/design/images/buttonicon/redo.png",
            "com/fr/design/images/buttonicon/additicon_grey.png",
            "com/fr/design/images/buttonicon/addicon.png",
            "com/fr/design/images/buttonicon/list_normal.png",
            "com/fr/design/images/buttonicon/list_pressed.png",
            "com/fr/design/images/buttonicon/close_icon.png",
            "com/fr/design/images/buttonicon/mouseoverclose icon.png",
            "com/fr/design/images/buttonicon/pressclose icon.png",
            "com/fr/design/images/buttonicon/open.png",
            "com/fr/design/images/m_file/view_folder.png",
            "com/fr/design/images/data/source/rename.png",
            "com/fr/design/images/buttonicon/minus.png",
            "com/fr/design/images/buttonicon/plus.png",
            "com/fr/design/images/data/database.png",
            "com/fr/design/images/data/source/classTableData.png",
            "com/fr/design/images/data/dataTable.png",
            "com/fr/design/images/data/multi.png",
            "com/fr/design/images/data/file.png",
            "com/fr/design/images/data/tree.png",
            "com/fr/design/images/control/tab/remove.png",
            "com/fr/design/images/m_file/preview.png",
            "com/fr/design/images/m_web/connection.png",
            "com/fr/design/images/control/addPopup.png",
            "com/fr/design/images/buttonicon/propertiestab/cellelement_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/cellattr_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/floatelement_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/widgetsettings_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/conditionattr_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/hyperlink_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/widgetlib_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/authorityedit_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/configuredroles_normal.png",
            "com/fr/design/images/buttonicon/propertiestab/cellelement_selected.png",
            "com/fr/design/images/log/selectedall.png",
            "com/fr/design/images/m_edit/copy.png",
            "com/fr/design/images/log/clear.png",
            "com/fr/design/images/log/clear.png",
            "com/fr/design/images/log/selectedall.png",
            "com/fr/design/images/log/setting.png",
            "com/fr/design/images/buttonicon/newcpts.png",
            "com/fr/base/images/oem/logo.png",
            "com/fr/design/images/data/bind/localconnect.png",
            "com/fr/design/images/gui/tab_add_normal.png",
            "com/fr/design/images/m_help/demo.png",
            "com/fr/design/images/gui/tab_add_hover.png",
            "com/fr/design/images/gui/tab_add_click.png",
            "com/fr/design/images/gui/tab_delete.png",
            "com/fr/design/images/bbs.png",
            "com/fr/design/images/video.png",
            "com/fr/design/images/help.png",
            "com/fr/design/images/questions.png",
            "com/fr/design/images/update.png",
            "com/fr/design/images/need.png",
            "com/fr/design/images/bug.png",
            "com/fr/design/images/signature.png",
            "com/fr/design/images/m_file/switch.png",
            "com/fr/design/images/gui/blank.gif",
            "com/fr/design/images/gui/folder.png",
            "com/fr/design/images/gui/javaFile.gif",
            "com/fr/design/images/gui/classFile.gif",
            "com/fr/design/images/gui/jspFile.gif",
            "com/fr/design/images/gui/jsFile.gif",
            "com/fr/design/images/gui/xmlFile.gif",
            "com/fr/design/images/gui/htmlFile.gif",
            "com/fr/design/images/gui/jarFile.gif",
            "com/fr/design/images/gui/gifFile.gif",
            "com/fr/design/images/gui/jpgFile.gif",
            "com/fr/design/images/gui/bmpFile.gif",
            "com/fr/design/images/gui/cptFile.png",
            "com/fr/design/images/gui/frm.png",
            "com/fr/design/images/gui/cht.png",
            "com/fr/design/images/chart/ChartType.png",
            "com/fr/van/chart/map/images/mapData.png",
            "com/fr/design/images/buttonicon/add.png",
            "com/fr/base/images/cell/control/remove.png",
            "com/fr/design/images/control/up.png",
            "com/fr/design/images/control/down.png",
            "com/fr/design/images/control/sortAsc.png",
            "com/fr/design/images/m_insert/formula.png",
            "com/fr/design/images/calender/year_reduce.png",
            "com/fr/design/images/calender/year_reduce_hover.png",
            "com/fr/design/images/calender/year_reduce_click.png",
            "com/fr/design/images/calender/month_reduce.png",
            "com/fr/design/images/calender/month_reduce_hover.png",
            "com/fr/design/images/calender/month_reduce_click.png",
            "com/fr/design/images/calender/month_add.png",
            "com/fr/design/images/calender/month_add_hover.png",
            "com/fr/design/images/calender/month_add_click.png",
            "com/fr/design/images/calender/year_add.png",
            "com/fr/design/images/calender/year_add_hover.png",
            "com/fr/design/images/calender/year_add_click.png",
            "com/fr/design/images/buttonicon/type_string.png",
            "com/fr/base/images/cell/control/add.png",
            "com/fr/design/images/expand/cellAttr.gif",
            "com/fr/design/images/expand/none16x16.png",
            "com/fr/design/images/expand/asc.png",
            "com/fr/design/images/expand/des.png",
            "com/fr/design/images/form/toolbar/pie.png",
            "com/fr/design/images/buttonicon/widget/date_16.png",
            "com/fr/design/images/buttonicon/widget/tree_16.png",
            "com/fr/design/images/buttonicon/widget/comboboxtree.png",
            "com/fr/design/images/buttonicon/widget/checkbox_group_16.png",
            "com/fr/design/images/buttonicon/widget/button_group_16.png",
            "com/fr/design/images/buttonicon/widget/number_field_16.png",
            "com/fr/design/images/buttonicon/widget/label_16.png",
            "com/fr/web/images/form/resources/button_16.png",
            "com/fr/design/images/buttonicon/widget/button_16.png",
            "com/fr/design/images/buttonicon/widget/files_up.png",
            "com/fr/design/images/buttonicon/widget/combo_box_16.png",
            "com/fr/design/images/buttonicon/widget/combo_check_16.png",
            "com/fr/design/images/buttonicon/widget/check_box_16.png",
            "com/fr/web/images/form/resources/list_16.png",
            "com/fr/design/images/buttonicon/widget/text_field_16.png",
            "com/fr/design/images/buttonicon/widget/text_area_16.png",
            "com/fr/design/images/buttonicon/widget/password_field_16.png",
            "com/fr/web/images/form/resources/iframe_16.png",
            "com/fr/design/images/form/toolbar/column.png",
            "com/fr/design/images/form/toolbar/bar.png",
            "com/fr/design/images/form/toolbar/line.png",
            "com/fr/design/images/form/toolbar/area.png",
            "com/fr/design/images/form/toolbar/gauge.png",
            "com/fr/design/images/form/toolbar/radar.png",
            "com/fr/design/images/form/toolbar/scatter.png",
            "com/fr/design/images/form/toolbar/bubble.png",
            "com/fr/design/images/form/toolbar/custom.png",
            "com/fr/design/images/form/toolbar/multiPie.png",
            "com/fr/design/images/form/toolbar/map.png",
            "com/fr/design/images/form/toolbar/drillmap.png",
            "com/fr/design/images/form/toolbar/treeMap.png",
            "com/fr/design/images/form/toolbar/funnel.png",
            "com/fr/design/images/form/toolbar/heatmap.png",
            "com/fr/design/images/form/toolbar/wordcloud.png",
            "com/fr/design/images/form/toolbar/gantt.png",
            "com/fr/design/images/form/toolbar/structure.png",
            "com/fr/design/images/control/help_open.png",
            "com/fr/web/images/form/forbid.png",
            "com/fr/web/images/form/resources/layout_absolute.png",
            "com/fr/design/images/m_edit/cut.png",
            "com/fr/design/images/m_edit/paste.png",
            "com/fr/design/images/m_report/delete.png",
            "com/fr/design/images/toolbarbtn/parametersetting.png",
            "com/fr/base/images/oem/addworksheet.png",
            "com/fr/design/images/sheet/addpolysheet.png",
            "com/fr/base/images/oem/worksheet.png",
            "com/fr/design/images/sheet/polysheet.png",
            "com/fr/design/images/sheet/left_normal@1x.png",
            "com/fr/design/images/sheet/right_normal@1x.png",
            "com/fr/design/images/sheet/left_hover@1x.png",
            "com/fr/design/images/sheet/right_hover@1x.png",
            "com/fr/design/images/data/source/normalDown20.png",
            "com/fr/design/images/data/source/hoverDown20.png",
            "com/fr/design/images/data/source/normalUp20.png",
            "com/fr/design/images/data/source/hoverUp20.png",
            "com/fr/design/images/m_edit/formatBrush.png",
            "com/fr/design/images/expand/none16x16_selected@1x.png",
            "com/fr/design/images/expand/vertical.png",
            "com/fr/design/images/expand/vertical_selected@1x.png",
            "com/fr/design/images/expand/landspace.png",
            "com/fr/design/images/expand/landspace_selected@1x.png",
            "com/fr/design/images/buttonicon/select.png",
            "com/fr/design/images/expand/asc_selected.png",
            "com/fr/design/images/expand/des_selected.png",
            "com/fr/design/images/m_format/cellstyle/bold.png",
            "com/fr/design/images/m_format/cellstyle/italic.png",
            "com/fr/design/images/m_format/cellstyle/underline.png",
            "com/fr/design/images/m_format/cellstyle/strikethrough.png",
            "com/fr/design/images/m_format/cellstyle/shadow.png",
            "com/fr/design/images/m_format/cellstyle/sup.png",
            "com/fr/design/images/m_format/cellstyle/sub.png",
            "com/fr/base/images/dialog/border/top.png",
            "com/fr/design/images/m_format/cellstyle/top_white.png",
            "com/fr/base/images/dialog/border/left.png",
            "com/fr/design/images/m_format/cellstyle/left_white.png",
            "com/fr/base/images/dialog/border/bottom.png",
            "com/fr/design/images/m_format/cellstyle/bottom_white.png",
            "com/fr/base/images/dialog/border/right.png",
            "com/fr/design/images/m_format/cellstyle/right_white.png",
            "com/fr/base/images/dialog/border/horizontal.png",
            "com/fr/design/images/m_format/cellstyle/horizontal_white.png",
            "com/fr/base/images/dialog/border/vertical.png",
            "com/fr/design/images/m_format/cellstyle/vertical_white.png",
            "com/fr/design/images/m_format/out.png",
            "com/fr/design/images/m_format/cellstyle/out_white.png",
            "com/fr/design/images/m_format/in.png",
            "com/fr/design/images/m_format/cellstyle/in_white.png",
            "com/fr/design/images/m_format/cellstyle/h_left_normal.png",
            "com/fr/design/images/m_format/cellstyle/h_left_normal_white.png",
            "com/fr/design/images/m_format/cellstyle/h_center_normal.png",
            "com/fr/design/images/m_format/cellstyle/h_center_normal_white.png",
            "com/fr/design/images/m_format/cellstyle/h_right_normal.png",
            "com/fr/design/images/m_format/cellstyle/h_right_normal_white.png",
            "com/fr/design/images/m_format/cellstyle/h_s_normal.png",
            "com/fr/design/images/m_format/cellstyle/h_s_normal_white.png",
            "com/fr/design/images/m_format/cellstyle/defaultAlignment.png",
            "com/fr/design/images/m_format/cellstyle/defaultAlignment_white.png",
            "com/fr/design/images/m_format/cellstyle/v_top_normal.png",
            "com/fr/design/images/m_format/cellstyle/v_top_normal_white.png",
            "com/fr/design/images/m_format/cellstyle/v_center_normal.png",
            "com/fr/design/images/m_format/cellstyle/v_center_normal_white.png",
            "com/fr/design/images/m_format/cellstyle/v_down_normal.png",
            "com/fr/design/images/m_format/cellstyle/v_down_normal_white.png",
            "com/fr/design/images/control/refresh.png",
            "com/fr/design/images/toolbarbtn/close.png",
            "com/fr/design/images/buttonicon/new_form3.png",
            "com/fr/design/images/m_file/save.png",
            "com/fr/design/images/m_file/saveAs.png",
            "com/fr/design/images/m_edit/undo.png",
            "com/fr/design/images/m_edit/redo.png",
            "com/fr/design/images/m_file/excel.png",
            "com/fr/design/images/m_file/pdf.png",
            "com/fr/design/images/m_file/word.png",
            "com/fr/design/images/m_file/svg.png",
            "com/fr/design/images/m_file/csv.png",
            "com/fr/design/images/m_file/text.png",
            "com/fr/design/images/m_web/datasource.png",
            "com/fr/design/images/m_report/webreportattribute.png",
            "com/fr/design/images/m_report/exportAttr.png",
            "com/fr/design/images/m_report/p.png",
            "com/fr/design/images/m_report/mobile.png",
            "com/fr/web/images/print.png",
            "com/fr/design/images/m_report/watermark.png",
            "com/fr/design/images/m_file/pageSetup.png",
            "com/fr/design/images/m_report/header.png",
            "com/fr/design/images/m_report/footer.png",
            "com/fr/design/images/m_report/background.png",
            "com/fr/design/images/m_report/reportWriteAttr.png",
            "com/fr/design/images/m_report/linearAttr.png",
            "com/fr/design/images/m_report/reportEngineAttr.png",
            "com/fr/design/images/m_report/allow_authority_edit.png",
            "com/fr/design/images/m_insert/bindColumn.png",
            "com/fr/design/images/m_insert/text.png",
            "com/fr/design/images/m_insert/richtext.png",
            "com/fr/design/images/m_insert/chart.png",
            "com/fr/design/images/m_insert/image.png",
            "com/fr/design/images/m_insert/bias.png",
            "com/fr/design/images/arrow/arrow_up.png",
            "com/fr/design/images/m_insert/subReport.png",
            "com/fr/design/images/arrow/arrow_down.png",
            "com/fr/design/images/toolbarbtn/toolbarbtnsetting.png",
            "com/fr/design/images/control/addPopup.png",
            "com/fr/design/images/toolbarbtn/toolbarbtnclear.png",
            "com/fr/design/images/m_insert/insertRow.png",
            "com/fr/design/images/m_insert/insertColumn.png",
            "com/fr/design/images/m_format/modified.png",
            "com/fr/design/images/m_format/highlight.png",
            "com/fr/web/images/form/resources/preview_16.png",
            "com/fr/web/images/save.png",
            "com/fr/design/images/m_insert/hyperLink.png",
            "com/fr/web/images/flashPrint.png",
            "com/fr/design/images/m_edit/merge.png",
            "com/fr/web/images/appletPrint.png",
            "com/fr/design/images/m_edit/unmerge.png",
            "com/fr/web/images/pdf.png",
            "com/fr/design/images/m_format/cellAttr.png",
            "com/fr/web/images/pdfPrint.png",
            "com/fr/web/images/serverPrint.png",
            "com/fr/web/images/email.png",
            "com/fr/web/images/preview.png",
            "com/fr/design/images/server/platform_16_16.png",
            "com/fr/design/images/data/user_widget.png",
            "com/fr/design/images/server/plugin.png",
            "com/fr/web/images/excel.png",
            "com/fr/design/images/m_web/function.png",
            "com/fr/web/images/word.png",
            "com/fr/web/images/pageSetup.png",
            "com/fr/web/images/export.png",
            "com/fr/design/images/m_web/edit.png",
            "com/fr/web/images/pageNumber.png",
            "com/fr/web/images/first.png",
            "com/fr/design/images/m_web/style.png",
            "com/fr/web/images/last.png",
            "com/fr/web/images/previous.png",
            "com/fr/web/images/next.png",
            "com/fr/web/images/scale.png",
            "com/fr/web/images/appendRow.png",
            "com/fr/web/images/deleteRow.png",
            "com/fr/web/images/verify.gif",
            "com/fr/web/images/save2.png",
            "com/fr/design/images/m_file/export.png",
            "com/fr/web/images/showValue.png",
            "com/fr/web/images/pianyi.png",
            "com/fr/web/images/writeOffline.png",
            "com/fr/web/images/edit/stash.png",
            "com/fr/web/images/edit/clearstash.png",
            "com/fr/design/images/m_insert/cell.png",
            "com/fr/design/images/m_insert/float.png",
            "com/fr/design/images/gui/color/background.png",
            "com/fr/design/images/m_format/cell.png",
            "com/fr/design/images/data/source/dataDictionary.png",
            "com/fr/design/images/m_format/cellstyle/bold_white.png",
            "com/fr/design/images/m_format/cellstyle/italic_white.png",
            "com/fr/design/images/m_format/cellstyle/underline_white.png",
            "com/fr/design/images/m_format/noboder.png",
            "com/fr/design/images/gui/popup.gif",
            "com/fr/design/images/m_insert/floatPop.png",
            "com/fr/web/images/platform/demo.png",
            "com/fr/base/images/dialog/pagesetup/down.png",
            "com/fr/base/images/dialog/pagesetup/over.png",
            "com/fr/web/images/reportlet.png",
            "com/fr/design/images/buttonicon/switchShortCuts.png",
            "com/fr/design/images/buttonicon/ds_column_name.png",
            "com/fr/base/images/cell/control/add.png",
            "com/fr/base/images/cell/control/rename.png",
            "com/fr/base/images/cell/control/remove.png",
            "com/fr/design/images/control/up.png",
            "com/fr/design/images/control/down.png",
            "com/fr/design/images/condition/bracket.png",
            "com/fr/design/images/condition/unBracket.png"
    };
}
