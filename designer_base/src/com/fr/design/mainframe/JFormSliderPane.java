package com.fr.design.mainframe;

import com.fr.base.BaseUtils;
import com.fr.design.constants.LayoutConstants;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.gui.ibutton.UIRadioButton;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.islider.UISlider;
import com.fr.design.gui.ispinner.UIBasicSpinner;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.TableLayout;
import com.fr.design.layout.TableLayoutHelper;
import com.fr.design.utils.gui.GUICoreUtils;
import com.fr.general.Inter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.math.BigDecimal;

/**
 * Created by MoMeak on 2017/7/13.
 */
public class JFormSliderPane extends JPanel {

    private static final double ONEPOINTEIGHT = 1.8;
    private static final int SIX = 6;
    private static final int TEN = 10;
    private static final int ONE_EIGHT = 18;
    private static final int FONT_SIZE = 12;
    private static final int SPINNER_WIDTH = 45;
    private static final int SPINNER_HEIGHT = 20;
    private static final int HALF_HUNDRED = 50;
    private static final int HUNDRED = 100;
    private static final int TWO_HUNDRED = 200;
    private static final int THREE_HUNDRED = 300;
    private static final int FOUR_HUNDRED = 400;
    private static final int DIALOG_WIDTH = 157;
    private static final int DIALOG_HEIGHT = 172;
    private static final int SLIDER_WIDTH = 220;
    private static final int SLIDER_HEIGHT = 20;
    private static final int SHOWVALBUTTON_WIDTH = 40;
    private static final int SHOWVALBUTTON_HEIGHTH = 20;
    private static final int SLIDER_GAP = 5;
    private static final Color BACK_COLOR = new Color(245, 245, 247);
    public int showValue = 100;
    public double resolutionTimes = 1.0;
    private static JFormSliderPane THIS;
    private UITextField showVal;
    private JSpinner showValSpinner;
    private UISlider slider;
    private int times;
    private int sliderValue;
    private UIButton downButton;
    private UIButton upButton;
    private JButton showValButton;
    private UIRadioButton twoHundredButton;
    private UIRadioButton oneHundredButton;
    private UIRadioButton SevenFiveButton;
    private UIRadioButton fiveTenButton;
    private UIRadioButton twoFiveButton;
    private UIRadioButton selfAdaptButton;
    private UIRadioButton customButton;
    //拖动条处理和button、直接输入不一样
    private boolean isButtonOrIsTxt = true;
    private FormPopupPane dialog;
    private int upButtonX;
    private JPanel dialogContentPanel;


    public JFormSliderPane() {
        this.setLayout(new BorderLayout());
        slider = new UISlider(0, HUNDRED, HALF_HUNDRED);
        slider.setUI(new JSliderPaneUI(slider));
        slider.addChangeListener(listener);
        slider.setPreferredSize(new Dimension(220, 20));
        //去掉虚线框
        slider.setFocusable(false);
        slider.setToolTipText(Inter.getLocText("FR-Designer_Scale_Slider"));
        showValSpinner = new UIBasicSpinner(new SpinnerNumberModel(HUNDRED, TEN, FOUR_HUNDRED, 1));
        showValSpinner.setEnabled(true);
        showValSpinner.addChangeListener(showValSpinnerChangeListener);
        showValSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
        downButton = new UIButton(BaseUtils.readIcon("com/fr/design/images/data/source/normalDown20.png"), BaseUtils.readIcon("com/fr/design/images/data/source/hoverDown20.png"), BaseUtils.readIcon("com/fr/design/images/data/source/hoverDown20.png"));
        downButton.setOpaque(false);
        downButton.setBorderPainted(false);
        downButton.setToolTipText(Inter.getLocText("FR-Designer_Scale_Down"));
        upButton = new UIButton(BaseUtils.readIcon("com/fr/design/images/data/source/normalUp20.png"), BaseUtils.readIcon("com/fr/design/images/data/source/hoverUp20.png"), BaseUtils.readIcon("com/fr/design/images/data/source/hoverUp20.png"));
        upButton.setOpaque(false);
        upButton.setBorderPainted(false);
        upButton.setToolTipText(Inter.getLocText("FR-Designer_Scale_Up"));
        downButton.setActionCommand("less");
        upButton.setActionCommand("more");
        downButton.addActionListener(buttonActionListener);
        upButton.addActionListener(buttonActionListener);
        showValButton = new JButton(showValSpinner.getValue() + "%");
        showValButton.setOpaque(false);
        showValButton.setMargin(new Insets(0, 0, 0, 0));
        showValButton.setFont(new Font("SimSun", Font.PLAIN, 12));
        showValButton.setBackground(BACK_COLOR);
        showValButton.setBorderPainted(false);
        showValButton.setPreferredSize(new Dimension(SHOWVALBUTTON_WIDTH, SHOWVALBUTTON_HEIGHTH));
        showValButton.addActionListener(showValButtonActionListener);
        //TODO 先注释，需要自定义tooltip
        showValButton.setToolTipText(Inter.getLocText("FR-Designer_Scale_Grade"));
        initUIRadioButton();
        initPane();
        JPanel panel = new JPanel(new FlowLayout(1, 5, 0));
        panel.add(downButton);
        panel.add(slider);
        panel.add(upButton);
        panel.add(showValButton);
        panel.setBackground(BACK_COLOR);
        this.add(panel, BorderLayout.NORTH);
    }

    public static final JFormSliderPane getInstance() {
//        if (THIS == null) {
//            THIS = new JSliderPane();
//        }
        THIS = new JFormSliderPane();
        return THIS;
    }

    private void initUIRadioButton() {
        twoHundredButton = new UIRadioButton("200%");
        oneHundredButton = new UIRadioButton("100%");
        SevenFiveButton = new UIRadioButton("75%");
        fiveTenButton = new UIRadioButton("50%");
        twoFiveButton = new UIRadioButton("25%");
        selfAdaptButton = new UIRadioButton(Inter.getLocText("FR-Designer_Scale_selfAdaptButton"));
        selfAdaptButton.setFont(new Font("SimSun", Font.PLAIN, FONT_SIZE));
        customButton = new UIRadioButton(Inter.getLocText("FR-Designer_Scale_customButton"));
        customButton.setFont(new Font("SimSun", Font.PLAIN, FONT_SIZE));
        twoHundredButton.addItemListener(radioButtonItemListener);
        oneHundredButton.addItemListener(radioButtonItemListener);
        SevenFiveButton.addItemListener(radioButtonItemListener);
        fiveTenButton.addItemListener(radioButtonItemListener);
        twoFiveButton.addItemListener(radioButtonItemListener);
        //TODO
//        selfAdaptButton.addItemListener();

        ButtonGroup bg = new ButtonGroup();// 初始化按钮组
        bg.add(twoHundredButton);// 加入按钮组
        bg.add(oneHundredButton);
        bg.add(SevenFiveButton);
        bg.add(fiveTenButton);
        bg.add(twoFiveButton);
        bg.add(selfAdaptButton);
        bg.add(customButton);
        customButton.setSelected(true);
    }

    private void initPane() {
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = {p, f};
        double[] rowSize = {p, p, p, p, p, p, p, p};
        UILabel upLabel = new UILabel(" " + Inter.getLocText("FR-Designer_Scale_EnlargeOrReduce"));
        upLabel.setOpaque(false);
        JPanel septPane = new JPanel(new BorderLayout());
        JSeparator sept = new JSeparator();
        sept.setBackground(new Color(232, 232, 233));
        septPane.add(sept, BorderLayout.NORTH);
        septPane.setBorder(BorderFactory.createEmptyBorder(2, 5, 1, 10));
        septPane.setBackground(BACK_COLOR);
        twoHundredButton.setBackground(BACK_COLOR);
        oneHundredButton.setBackground(BACK_COLOR);
        SevenFiveButton.setBackground(BACK_COLOR);
        fiveTenButton.setBackground(BACK_COLOR);
        twoFiveButton.setBackground(BACK_COLOR);
//        selfAdaptButton.setBackground(BACK_COLOR);
        customButton.setBackground(BACK_COLOR);
        Component[][] components = new Component[][]{
                new Component[]{upLabel, null},
                new Component[]{septPane, null},
                new Component[]{twoHundredButton, null},
                new Component[]{oneHundredButton, null},
                new Component[]{SevenFiveButton, null},
                new Component[]{fiveTenButton, null},
                new Component[]{twoFiveButton, null},
                new Component[]{customButton, createSpinnerPanel()}
        };
        dialogContentPanel = TableLayoutHelper.createGapTableLayoutPane(components, rowSize, columnSize, LayoutConstants.VGAP_MEDIUM, 0);
        dialogContentPanel.setBackground(BACK_COLOR);
        dialogContentPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
    }

    private JPanel createSpinnerPanel() {
        JPanel spinnerPanel = new JPanel(new FlowLayout());
        spinnerPanel.add(showValSpinner);
        UILabel percent = new UILabel("%");
        percent.setFont(new Font("SimSun", Font.PLAIN, FONT_SIZE));
        spinnerPanel.add(percent);
        spinnerPanel.setBackground(BACK_COLOR);
        return spinnerPanel;
    }

    ActionListener showValButtonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            popupDialog();
        }
    };

    ChangeListener showValSpinnerChangeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            int val = (int) ((UIBasicSpinner) e.getSource()).getValue();
            isButtonOrIsTxt = true;
            resolutionTimes = divide(showValue, 100, 2);
            if (val > FOUR_HUNDRED) {
                showValSpinner.setValue(FOUR_HUNDRED);
                val = FOUR_HUNDRED;
            }
            if (val < TEN) {
                showValSpinner.setValue(TEN);
                val = TEN;
            }
            refreshSlider(val);
            refreshBottun(val);
        }
    };


    //定义一个监听器，用于监听所有滑动条
    ChangeListener listener = new ChangeListener() {
        public void stateChanged(ChangeEvent event) {
            //取出滑动条的值，并在文本中显示出来
            if (!isButtonOrIsTxt) {
                customButton.setSelected(true);
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        sliderValue = slider.getValue();
                        getTimes(sliderValue);
                        showValue = times;
                        showValSpinner.setValue(times);
                    }
                });
            } else {
                isButtonOrIsTxt = false;
            }
        }
    };

    ItemListener radioButtonItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            JRadioButton temp = (JRadioButton) e.getSource();
            if (temp.isSelected()) {
                showValSpinner.setValue(Integer.valueOf(temp.getText().substring(0, temp.getText().indexOf("%"))));
            }
        }
    };

    private void refreshSlider(int val) {
        showValue = val;
        if (showValue > HUNDRED) {
            slider.setValue((int) (showValue + TWO_HUNDRED) / SIX);
        } else if (showValue < HUNDRED) {
            slider.setValue((int) ((showValue - TEN) / ONEPOINTEIGHT));
        } else if (showValue == HUNDRED) {
            slider.setValue(HALF_HUNDRED);
        }
    }


    private void refreshBottun(int val) {
        showValButton.setText(val + "%");
    }

    public double getResolutionTimes() {
        return this.resolutionTimes;
    }

    public int getshowValue() {
        return this.showValue;
    }

    public static double divide(double v1, double v2, int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale).doubleValue();
    }

    ActionListener buttonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showValue = (int) showValSpinner.getValue();
            isButtonOrIsTxt = true;
            if (e.getActionCommand().equals("less")) {
                int newDownVal = showValue - TEN;
                if (newDownVal >= TEN) {
                    showValue = newDownVal;
                    showValSpinner.setValue(newDownVal);
                } else {
                    showValue = newDownVal;
                    showValSpinner.setValue(TEN);
                }
            }
            if (e.getActionCommand().equals("more")) {
                int newUpVal = showValue + TEN;
                if (newUpVal <= FOUR_HUNDRED) {
                    showValue = newUpVal;
                    showValSpinner.setValue(newUpVal);
                } else {
                    showValue = newUpVal;
                    showValSpinner.setValue(FOUR_HUNDRED);
                }
            }
            isButtonOrIsTxt = true;
            customButton.setSelected(true);
        }
    };


    private void getTimes(int value) {
        if (value == HALF_HUNDRED) {
            times = HUNDRED;
        } else if (value < HALF_HUNDRED) {
            times = (int) Math.round(ONEPOINTEIGHT * value + TEN);
        } else {
            times = (int) (SIX * value - TWO_HUNDRED);
        }
    }


    public JSpinner getShowVal() {
        return this.showValSpinner;
    }

    public UIRadioButton getSelfAdaptButton() {
        return this.selfAdaptButton;
    }

    private void popupDialog() {
        Point btnCoords = upButton.getLocationOnScreen();
        if (dialog == null) {
            dialog = new FormPopupPane(upButton, dialogContentPanel);
            if (upButtonX == 0) {
                upButtonX = btnCoords.x;
                GUICoreUtils.showPopupMenu(dialog, upButton, -DIALOG_WIDTH + upButton.getWidth() + SHOWVALBUTTON_WIDTH + SLIDER_GAP, -DIALOG_HEIGHT);
            }
        } else {
            if (upButtonX == 0) {
                upButtonX = btnCoords.x;
                GUICoreUtils.showPopupMenu(dialog, upButton, -DIALOG_WIDTH + upButton.getWidth() + SHOWVALBUTTON_WIDTH + SLIDER_GAP, -DIALOG_HEIGHT);
            } else {
                GUICoreUtils.showPopupMenu(dialog, upButton, -DIALOG_WIDTH + upButton.getWidth() + SHOWVALBUTTON_WIDTH + SLIDER_GAP, -DIALOG_HEIGHT);
            }
        }
    }

    public static void main(String[] args) {
        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = (JPanel) jf.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(JFormSliderPane.getInstance(), BorderLayout.CENTER);
        GUICoreUtils.centerWindow(jf);
        jf.setSize(400, 80);
        jf.setVisible(true);

    }
}


class FormPopupPane extends JPopupMenu {
    private static final int DIALOG_WIDTH = 157;
    private static final int DIALOG_HEIGHT = 172;

    FormPopupPane(UIButton b, JPanel dialogContentPanel) {
        this.add(dialogContentPanel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        this.setBackground(new Color(245, 245, 247));
    }


}