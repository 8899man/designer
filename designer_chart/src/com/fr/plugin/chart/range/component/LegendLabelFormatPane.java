package com.fr.plugin.chart.range.component;

import com.fr.chart.base.LegendLabelFormat;
import com.fr.design.gui.ibutton.UIButtonGroup;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.style.FormatPane;
import com.fr.design.layout.TableLayout;
import com.fr.general.Inter;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.VanChartHtmlLabelPaneWithOutWidthAndHeight;
import com.fr.plugin.chart.designer.component.format.VanChartFormatPane;
import com.fr.plugin.chart.designer.style.VanChartStylePane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LegendLabelFormatPane extends JPanel{
    private static final long serialVersionUID = 1614283200308877354L;

    private UIButtonGroup labelFormatStyle;
    private FormatPane labelFormat;
    private VanChartHtmlLabelPaneWithOutWidthAndHeight htmlLabelPane;
    private JPanel centerPane;

    public void setParentPane(VanChartStylePane parent) {
        htmlLabelPane.setParent(parent);
    }

    public LegendLabelFormatPane(){
        labelFormatStyle = new UIButtonGroup<Integer>(new String[]{Inter.getLocText("Plugin-ChartF_Common"),
                Inter.getLocText("Plugin-ChartF_Custom")});
        labelFormat = new VanChartFormatPane();
        htmlLabelPane = new VanChartHtmlLabelPaneWithOutWidthAndHeight();

        centerPane = new JPanel(new CardLayout()){
            @Override
            public Dimension getPreferredSize() {
                if(labelFormatStyle.getSelectedIndex() == 0){
                    return labelFormat.getPreferredSize();
                }else{
                    return htmlLabelPane.getPreferredSize();
                }
            }
        };
        centerPane.add(labelFormat,Inter.getLocText("Plugin-ChartF_Common"));
        centerPane.add(htmlLabelPane, Inter.getLocText("Plugin-ChartF_Custom"));


        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p,f};
        double[] rowSize = {p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{null,null},
                new Component[]{new UILabel(Inter.getLocText("Plugin-ChartF_Label_Format"), SwingConstants.LEFT), labelFormatStyle},
                new Component[]{centerPane,null},
        };
        JPanel contentPane = TableLayout4VanChartHelper.createGapTableLayoutPane(components,rowSize,columnSize);
        labelFormatStyle.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkCardPane(false);
            }
        });
        labelFormatStyle.setSelectedIndex(0);

        JPanel panel =  TableLayout4VanChartHelper.createExpandablePaneWithTitle(Inter.getLocText("Chart-Use_Format"), contentPane);
        this.setLayout(new BorderLayout());
        this.add(panel,BorderLayout.CENTER);
    }

    private void checkCardPane(boolean populate) {
        if(centerPane != null && labelFormatStyle != null){
            CardLayout cardLayout = (CardLayout) centerPane.getLayout();
            if (labelFormatStyle.getSelectedIndex() == 1) {
                if(!populate) {
                    checkCustomLabelText();
                }
                cardLayout.show(centerPane,Inter.getLocText("Plugin-ChartF_Custom"));
            } else {
                cardLayout.show(centerPane, Inter.getLocText("Plugin-ChartF_Common"));
            }
        }
    }

    protected void checkCustomLabelText() {
    }

    protected void setCustomFormatterText(String text) {
        htmlLabelPane.setCustomFormatterText(text);
    }

    /*标签部分的更新和存储数据部分要重写*/
    public void populate(LegendLabelFormat legendLabelFormat){
        if (labelFormatStyle != null) {
            labelFormatStyle.setSelectedIndex(legendLabelFormat.isCommonValueFormat() ? 0 : 1);
        }
        if (labelFormat != null) {
            labelFormat.populateBean(legendLabelFormat.getFormat());
        }
        if (htmlLabelPane != null){
            htmlLabelPane.populate(legendLabelFormat.getHtmlLabel());
        }

        checkCardPane(true);
    }

    public void update(LegendLabelFormat legendLabelFormat){
        if (labelFormatStyle != null) {
            legendLabelFormat.setCommonValueFormat(labelFormatStyle.getSelectedIndex() == 0);
        }
        if (labelFormat != null) {
            legendLabelFormat.setFormat(labelFormat.update());
        }
        if (htmlLabelPane != null) {
            htmlLabelPane.update(legendLabelFormat.getHtmlLabel());
        }
    }
    /*标签部分的更新和存储数据部分要重写*/

}