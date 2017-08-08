package com.fr.design.widget.ui.designer;

import com.fr.base.FRContext;
import com.fr.base.Formula;
import com.fr.data.core.FormatField;
import com.fr.design.designer.creator.XCreator;
import com.fr.design.gui.ibutton.UIHeadGroup;
import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.ispinner.UISpinner;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.widget.component.DateValuePane;
import com.fr.design.widget.ui.designer.component.FormWidgetValuePane;
import com.fr.form.ui.DateEditor;
import com.fr.general.DateUtils;
import com.fr.general.Inter;
import com.fr.script.Calculator;
import com.fr.stable.ArrayUtils;
import com.fr.stable.UtilEvalError;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateEditorDefinePane extends DirectWriteEditorDefinePane<DateEditor> {
    private UIHeadGroup returnTypeComboBox;
    private DateValuePane startDv;
    private DateValuePane endDv;
    private WaterMarkDictPane waterMarkDictPane;
    private FormWidgetValuePane formWidgetValuePane;
    private UISpinner fontSizePane;
    private UIComboBox currentFormatComboBox;
    private UILabel currentSamplelabel;
    private UIHeadGroup fomatHeadGroup;

    public DateEditorDefinePane(XCreator xCreator) {
        super(xCreator);
    }


    @Override
    public String title4PopupWindow() {
        return "Date";
    }

    @Override
    protected JPanel setFirstContentPane() {
        waterMarkDictPane = new WaterMarkDictPane();
        formWidgetValuePane = new FormWidgetValuePane(creator.toData(), false);
        fontSizePane = new UISpinner(0, 20, 1, 0);
        JPanel returnTypePane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        returnTypePane.add(new UILabel(Inter.getLocText("Widget-Date_Selector_Return_Type") + ":"), BorderLayout.WEST);
        returnTypeComboBox = new UIHeadGroup(new String[] {Inter.getLocText("Date") ,  Inter.getLocText("String")}){
            protected void tabChanged(int newSelectedIndex) {
                //todo
            }
        };
        JPanel formatHead =  createFormatHead();

        startDv = new DateValuePane();
        endDv = new DateValuePane();
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        Component[][] components = new Component[][]{
                new Component[]{new UILabel(Inter.getLocText("FR-Designer-Estate_Widget_Value")), formWidgetValuePane},
                new Component[]{new UILabel(Inter.getLocText("FR-Engine_Format") + ":"), formatHead},
                new Component[]{new UILabel(Inter.getLocText("FS_Start_Date") + ":"), startDv},
                new Component[]{new UILabel(Inter.getLocText("FS_End_Date") + ":"), endDv},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_WaterMark") + ":"), waterMarkDictPane},
                new Component[]{new UILabel(Inter.getLocText("FR-Designer_Font-Size")), fontSizePane},
                new Component[]{new UILabel(Inter.getLocText("Widget-Date_Selector_Return_Type") + ":"), returnTypeComboBox}

        };
        double[] rowSize = {p, p, p, p, p, p, p, p, p};
        double[] columnSize = {p, f};
        int[][] rowCount = {{1, 3}, {1, 3}, {1, 1}, {1, 1}, {1, 1}, {1, 1}, {1, 1}};
        JPanel panel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, rowCount, 10, 10);
        JPanel boundsPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        boundsPane.add(panel);
        return boundsPane;
    }


    private JPanel createFormatPane(UIComboBox formatComboBox, UILabel sampleLabel){
        JPanel previewPane = FRGUIPaneFactory.createTitledBorderPane(Inter.getLocText("FR-Base_StyleFormat_Sample"));
        previewPane.add(sampleLabel);
        JPanel jPanel = FRGUIPaneFactory.createBorderLayout_S_Pane();
        jPanel.add(previewPane, BorderLayout.NORTH);
        jPanel.add(formatComboBox, BorderLayout.CENTER);
        return jPanel;
    }

    private UILabel createSamplePane(){
        UILabel sampleLabel = new UILabel("");
        sampleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sampleLabel.setFont(FRContext.getDefaultValues().getFRFont());
        return sampleLabel;
    }

    private JPanel createFormatHead(){
        String[] dateArray = FormatField.getInstance().getFormatArray(FormatField.FormatContents.DATE);
        String[] timeArray = FormatField.getInstance().getFormatArray(FormatField.FormatContents.TIME);
        final UIComboBox dateFormatComboBox = new UIComboBox(dateArray);
        final UIComboBox timeFormatComboBox = new UIComboBox(timeArray);
        dateFormatComboBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                refreshPreviewLabel();
            }
        });
        timeFormatComboBox.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                refreshPreviewLabel();
            }
        });
        final UILabel dateSampleLabel = createSamplePane();
        final UILabel timeSampleLabel = createSamplePane();
        JPanel fomatHeadPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
        final CardLayout cardLayout = new CardLayout();
        final JPanel customPane = new JPanel(cardLayout);
        JPanel dateFormatPane = createFormatPane(dateFormatComboBox, dateSampleLabel);
        JPanel timeFormatPane = createFormatPane(timeFormatComboBox, timeSampleLabel);
        customPane.add(dateFormatPane, Inter.getLocText("StyleFormat-Date"));
        customPane.add(timeFormatPane, Inter.getLocText("StyleFormat-Time"));
        final String[] tabTitles = new String[]{Inter.getLocText("StyleFormat-Date"), Inter.getLocText("StyleFormat-Time")};
        fomatHeadGroup = new UIHeadGroup(new String[]{Inter.getLocText("StyleFormat-Date"), Inter.getLocText("StyleFormat-Time")}){
            protected void tabChanged(int newSelectedIndex) {
                cardLayout.show(customPane, tabTitles[newSelectedIndex]);
                if(newSelectedIndex == 0){
                    currentFormatComboBox = dateFormatComboBox;
                    currentSamplelabel = dateSampleLabel;
                }else{
                    currentFormatComboBox = timeFormatComboBox;
                    currentSamplelabel = timeSampleLabel;
                }
                refreshPreviewLabel();
            }
        };
        fomatHeadPane.add(fomatHeadGroup, BorderLayout.NORTH);
        fomatHeadPane.add(customPane, BorderLayout.CENTER);
        return fomatHeadPane;
    }



    private void refreshPreviewLabel() {
        String text = (String) currentFormatComboBox.getSelectedItem();
        if (text != null && text.length() > 0) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(text);
                String sample = simpleDateFormat.format(new Date());
                Color c = Color.black;
                if (!ArrayUtils.contains(FormatField.getInstance().getDateFormatArray(), text)) {
                    sample += " " + Inter.getLocText("DateFormat-Custom_Warning");
                    c = Color.red;
                }
                currentSamplelabel.setText(sample);
                currentSamplelabel.setForeground(c);
            } catch (Exception exp) {
                currentSamplelabel.setForeground(Color.red);
                currentSamplelabel.setText(exp.getMessage());
            }
        } else {
            currentSamplelabel.setText(new Date().toString());
        }
    }


    @Override
    protected void populateSubDirectWriteEditorBean(DateEditor e) {
        String formatText = e.getFormatText();
        fomatHeadGroup.setSelectedIndex(getDateType(e));
        currentFormatComboBox.setSelectedItem(formatText);

		returnTypeComboBox.setSelectedIndex(e.isReturnDate() ? 0 : 1);
        formWidgetValuePane.populate(e);
        startDv.populate(e.getStartDate());
        endDv.populate(e.getEndDate());
    }

    @Override
    protected DateEditor updateSubDirectWriteEditorBean() {
        DateEditor ob = new DateEditor();

        ob.setFormatText(this.getSimpleDateFormat().toPattern());
        ob.setReturnDate(returnTypeComboBox.getSelectedIndex() == 0);
        formWidgetValuePane.update(ob);
        ob.setStartDate(startDv.update());
        ob.setEndDate(endDv.update());

        return ob;
    }


    /**
     * 更新日期控件的起止日期
     *
     * @param dateWidgetEditor 日期控件
     */
    public void updateStartEnd(DateEditor dateWidgetEditor) {
        Object startObject = startDv.update();
        Object endObject = endDv.update();
        // wei : 对公式的处理
        Calculator cal = null;
        if (startObject instanceof Formula) {
            cal = Calculator.createCalculator();
            Formula startFormula = (Formula) startObject;
            try {
                startFormula.setResult(cal.evalValue(startFormula.getContent()));
            } catch (UtilEvalError e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
            startObject = startFormula.getResult();
            dateWidgetEditor.setStartDateFM(startFormula);
            dateWidgetEditor.setStartText(null);
        } else {
            try {
                dateWidgetEditor.setStartText(startObject == null ? "" : DateUtils.getDate2Str("MM/dd/yyyy", (Date) startObject));
            } catch (ClassCastException e) {
                //wei : TODO 说明应用的公式不能转化成日期格式，应该做些处理。
            }
        }
        if (endObject instanceof Formula) {
            cal = Calculator.createCalculator();
            Formula endFormula = (Formula) endObject;
            try {
                endFormula.setResult(cal.evalValue(endFormula.getContent()));
            } catch (UtilEvalError e) {
                FRContext.getLogger().error(e.getMessage(), e);
            }
            endObject = endFormula.getResult();
            dateWidgetEditor.setEndDateFM(endFormula);
            dateWidgetEditor.setEndText(null);
        } else {
            try {
                dateWidgetEditor.setEndText(endObject == null ? "" : DateUtils.getDate2Str("MM/dd/yyyy", (Date) endObject));
            } catch (ClassCastException e) {

            }
        }
    }

    private SimpleDateFormat getSimpleDateFormat() {
        String text = (String) currentFormatComboBox.getSelectedItem();
        SimpleDateFormat simpleDateFormat;
        if (text != null && text.length() > 0) {
            try {
                simpleDateFormat = new SimpleDateFormat(text);
                this.currentSamplelabel.setText(simpleDateFormat.format(new Date()));
            } catch (Exception exp) {
                simpleDateFormat = new SimpleDateFormat("");
            }
        } else {
            simpleDateFormat = new SimpleDateFormat("");
        }

        return simpleDateFormat;

    }

    private int getDateType(DateEditor e){
        String[] timeArray = FormatField.getInstance().getFormatArray(FormatField.FormatContents.TIME);
        if(e == null){
            return 0;
        }
        String formatText = e.getFormatText();
        if(ArrayUtils.contains(timeArray, formatText)){
            return 1;
        }
        return 0;
    }

}