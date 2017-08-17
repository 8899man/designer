package com.fr.plugin.chart.designer.component.format;

import com.fr.base.Style;
import com.fr.design.gui.frpane.UIBubbleFloatPane;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.icheckbox.UICheckBox;
import com.fr.design.gui.style.FormatPane;
import com.fr.general.Inter;
import com.fr.plugin.chart.base.format.AttrTooltipFormat;
import com.fr.plugin.chart.designer.style.VanChartStylePane;
import com.fr.stable.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.Format;

/**
 * Created by Mitisky on 16/2/23.
 */
public abstract class VanChartFormatPaneWithCheckBox extends JPanel{
    private static final long serialVersionUID = -6563172546340480058L;

    private UICheckBox isSelectedBox;
    private UIButton formatButton;
    private FormatPane formatPane;
    private Format format;

    private VanChartStylePane parent;
    private JPanel showOnPane;

    private boolean isDirty;

    public VanChartFormatPaneWithCheckBox(VanChartStylePane parent, JPanel showOnPane) {
        this.parent = parent;
        this.showOnPane = showOnPane;
        this.isDirty = true;

        this.setLayout(new BorderLayout());
        isSelectedBox = new UICheckBox(getCheckBoxText());
        formatButton = new UIButton(Inter.getLocText("Chart-Use_Format"));
        this.add(isSelectedBox, BorderLayout.CENTER);
        this.add(formatButton, BorderLayout.EAST);

        initFormatListener();
        isSelectedBox.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                isDirty = true;
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    protected abstract String getCheckBoxText();

    protected boolean isPercent() {
        return false;
    }

    private void initFormatListener() {
        if(formatButton != null) {
            formatButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!formatButton.isEnabled()) {
                        return;
                    }

                    if(formatPane == null) {
                        formatPane =  createFormatPane();
                    }
                    Point comPoint = formatButton.getLocationOnScreen();
                    Point arrowPoint = new Point(comPoint.x + formatButton.getWidth(), comPoint.y + formatButton.getHeight());
                    UIBubbleFloatPane<Style> pane = new UIBubbleFloatPane(Constants.LEFT, arrowPoint, formatPane, 258, 209) {

                        @Override
                        public void updateContentPane() {
                            format = formatPane.update();
                            if(parent != null){//条件属性没有parent
                                parent.attributeChanged();
                            }
                        }
                    };
                    pane.show(showOnPane, Style.getInstance(format));
                    super.mouseReleased(e);
                    if(isPercent()){
                        formatPane.justUsePercentFormat();
                    }
                }
            });
        }
    }

    protected FormatPane createFormatPane() {
        return new FormatPane();
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean isDirty) {
        this.isDirty = isDirty;
    }

    public void populate(AttrTooltipFormat tooltipFormat) {
        this.isSelectedBox.setSelected(tooltipFormat.isEnable());
        this.format = tooltipFormat.getFormat();
    }

    public void update(AttrTooltipFormat tooltipFormat) {
        tooltipFormat.setFormat(format);
        tooltipFormat.setEnable(isSelectedBox.isSelected());
    }
}
