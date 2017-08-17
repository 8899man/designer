package com.fr.plugin.chart.map.line;

import com.fr.design.gui.icombobox.UIComboBox;
import com.fr.design.gui.ilable.UILabel;
import com.fr.general.ComparatorUtils;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.AttrEffect;
import com.fr.plugin.chart.designer.TableLayout4VanChartHelper;
import com.fr.plugin.chart.designer.component.marker.VanChartImageMarkerPane;
import com.fr.plugin.chart.designer.style.series.VanChartEffectPane;
import com.fr.plugin.chart.map.line.condition.AttrLineEffect;
import com.fr.plugin.chart.type.LineMapAnimationType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by hufan on 2016/12/20.
 */
public class VanChartLineMapEffectPane extends VanChartEffectPane{

    //运动方式
    private JPanel typeContentPane;
    private VanChartImageMarkerPane customContentPane;
    private UIComboBox animationType;

    public VanChartLineMapEffectPane() {
        super(true);
        this.add(TableLayout4VanChartHelper.createGapTableLayoutPane(Inter.getLocText("Plugin-ChartF_Line_Map_Animation"), enabledButton), BorderLayout.NORTH);
    }

    protected JPanel createContentPane() {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        JPanel periodPane = createPeriodPane();

        animationType = new UIComboBox(LineMapAnimationType.getTypes());
        initTypeContentPane();

        JPanel animationTypePane = new JPanel(new BorderLayout(0, 5));

        animationTypePane.add(createAnimationSelectPane(), BorderLayout.NORTH);
        animationTypePane.add(typeContentPane, BorderLayout.CENTER);

        panel.add(periodPane, BorderLayout.CENTER);
        panel.add(animationTypePane, BorderLayout.SOUTH);

        return panel;
    }

    private Component createAnimationSelectPane() {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(new UILabel(Inter.getLocText("Plugin-ChartF_Animation_Type")), BorderLayout.WEST);
        panel.add(animationType, BorderLayout.CENTER);
        return panel;
    }

    private void initTypeContentPane() {
        customContentPane = new VanChartImageMarkerPane();

        CardLayout cardLayout = new CardLayout();
        typeContentPane = new JPanel(cardLayout){
            @Override
            public Dimension getPreferredSize() {
                if(ComparatorUtils.equals(animationType.getSelectedItem(), LineMapAnimationType.DEFAULT)){
                    return new Dimension((int) customContentPane.getPreferredSize().getWidth(), 0);
                }else {
                    return customContentPane.getPreferredSize();
                }
            }
        };

        typeContentPane.add(new JPanel(), LineMapAnimationType.DEFAULT.getStringType());
        typeContentPane.add(customContentPane, LineMapAnimationType.CUSTOM.getStringType());

        animationType.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                checkCardLayout();
                //fireStateChanged();
            }
        });
    }

    private void checkCardLayout(){
        CardLayout cardLayout = (CardLayout) typeContentPane.getLayout();
        cardLayout.show(typeContentPane,  LineMapAnimationType.getTypes()[animationType.getSelectedIndex()].getStringType());
    }

    @Override
    public void populateBean(AttrEffect ob) {
        super.populateBean(ob);
        if (ob instanceof AttrLineEffect){
            animationType.setSelectedItem(((AttrLineEffect) ob).getAnimationType());
            customContentPane.populateBean(((AttrLineEffect) ob).getAttrMarker());
        }
        checkCardLayout();
    }

    public AttrEffect updateBean() {
        AttrLineEffect lineEffect = new AttrLineEffect();
        lineEffect.setEnabled(enabledButton.getSelectedIndex() == 0);
        lineEffect.setPeriod(period.getValue());
        lineEffect.setAnimationType(animationType.getSelectedIndex() == 0 ? LineMapAnimationType.DEFAULT : LineMapAnimationType.CUSTOM);
        lineEffect.setAttrMarker(customContentPane.updateBean());
        return lineEffect;
    }

    @Override
    protected String title4PopupWindow() {
        return Inter.getLocText("Plugin-ChartF_Line_Map_Animation");
    }
}
