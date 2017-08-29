package com.fr.plugin.chart.designer.style.background;

import com.fr.chart.chartattr.Plot;
import com.fr.design.gui.controlpane.NameableCreator;
import com.fr.design.gui.controlpane.ShortCut4JControlPane;
import com.fr.design.gui.controlpane.UIListControlPane;
import com.fr.design.mainframe.DesignerContext;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.general.NameObject;
import com.fr.plugin.chart.attr.DefaultAxisHelper;
import com.fr.plugin.chart.attr.axis.VanChartAlertValue;
import com.fr.plugin.chart.attr.axis.VanChartAxis;
import com.fr.plugin.chart.attr.plot.VanChartPlot;
import com.fr.plugin.chart.attr.plot.VanChartRectanglePlot;
import com.fr.stable.Nameable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mengao on 2017/8/22.
 */
public class AlertLineListControlPane extends UIListControlPane {


    @Override
    public void saveSettings() {
        if (isPopulating) {
            return;
        }
        update((VanChartPlot) plot);
        DesignerContext.getDesignerFrame().getSelectedJTemplate().fireTargetModified();
    }

    @Override
    public NameableCreator[] createNameableCreators() {
        return new ChartNameObjectCreator[]{new ChartNameObjectCreator(new String[]{Inter.getLocText("ChartF-X_Axis"), Inter.getLocText("ChartF-Y_Axis")},
                Inter.getLocText("Plugin-ChartF_AlertLine"), VanChartAlertValue.class, VanChartAlertValuePane.class)};
    }

    @Override
    protected String title4PopupWindow() {
        return Inter.getLocText("Plugin-ChartF_AlertLine");
    }

    protected String getAddItemText() {
        return Inter.getLocText(new String[]{"Plugin-Chart_Add_Line","Plugin-ChartF_AlertLine"});
    }

    protected ShortCut4JControlPane[] createShortcuts() {
        return new ShortCut4JControlPane[]{
                moveUpItemShortCut(),
                moveDownItemShortCut(),
                removeItemShortCut()
        };
    }

    public void populate(Plot plot) {
        this.plot = plot;
        VanChartRectanglePlot rectanglePlot = (VanChartRectanglePlot) plot;
        List<VanChartAxis> xAxisList = rectanglePlot.getXAxisList();
        List<VanChartAxis> yAxisList = rectanglePlot.getYAxisList();
        String[] axisNames = DefaultAxisHelper.getAllAxisNames(xAxisList, yAxisList);

        ChartNameObjectCreator[] creators = {new ChartNameObjectCreator(axisNames, Inter.getLocText("Plugin-ChartF_AlertLine"), VanChartAlertValue.class, VanChartAlertValuePane.class)};

        refreshNameableCreator(creators);

        java.util.List<NameObject> nameObjects = new ArrayList<NameObject>();

        for (VanChartAxis axis : xAxisList) {
            List<VanChartAlertValue> values = axis.getAlertValues();
            for (VanChartAlertValue alertValue : values) {
                alertValue.setAxisNamesArray(axisNames);
                alertValue.setAxisName(axis.getAxisName());
                nameObjects.add(new NameObject(alertValue.getAlertPaneSelectName(), alertValue));
            }
        }

        for (VanChartAxis axis : yAxisList) {
            List<VanChartAlertValue> values = axis.getAlertValues();
            for (VanChartAlertValue alertValue : values) {
                alertValue.setAxisNamesArray(axisNames);
                alertValue.setAxisName(axis.getAxisName());
                nameObjects.add(new NameObject(alertValue.getAlertPaneSelectName(), alertValue));
            }
        }

        populate(nameObjects.toArray(new NameObject[nameObjects.size()]));
        doLayout();
    }

    public void update(Plot plot) {

        Nameable[] nameables = this.update();

        VanChartRectanglePlot rectanglePlot = (VanChartRectanglePlot) plot;
        List<VanChartAxis> xAxisList = rectanglePlot.getXAxisList();
        List<VanChartAxis> yAxisList = rectanglePlot.getYAxisList();

        for (VanChartAxis axis : xAxisList) {
            List<VanChartAlertValue> axisAlerts = new ArrayList<VanChartAlertValue>();
            for (int i = 0; i < nameables.length; i++) {
                VanChartAlertValue value = (VanChartAlertValue) ((NameObject) nameables[i]).getObject();
                if (ComparatorUtils.equals(value.getAxisName(), axis.getAxisName())) {
                    value.setAlertPaneSelectName(nameables[i].getName());
                    axisAlerts.add(value);
                }
            }
            axis.setAlertValues(axisAlerts);
        }
        for (VanChartAxis axis : yAxisList) {
            List<VanChartAlertValue> axisAlerts = new ArrayList<VanChartAlertValue>();
            for (int i = 0; i < nameables.length; i++) {
                VanChartAlertValue value = (VanChartAlertValue) ((NameObject) nameables[i]).getObject();
                if (ComparatorUtils.equals(value.getAxisName(), axis.getAxisName())) {
                    value.setAlertPaneSelectName(nameables[i].getName());
                    axisAlerts.add(value);
                }
            }
            axis.setAlertValues(axisAlerts);
        }
    }
}
