package com.fr.design.mainframe;

import com.fr.base.BaseUtils;
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
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;

/**
 * Created by MoMeak on 2017/7/13.
 */
public class JSliderPane extends JPanel {

    private static final double ONEPOINTEIGHT = 1.8;
    private static final int SIX = 6;
    private static final int TEN = 10;
    private static final int ONE_EIGHT = 18;
    private static final int FONT_SIZE = 14;
    private static final int SPINNER_WIDTH= 45;
    private static final int SPINNER_HEIGHT = 20;
    private static final int HALF_HUNDRED = 50;
    private static final int HUNDRED = 100;
    private static final int TWO_HUNDRED = 200;
    private static final int THREE_HUNDRED = 300;
    private static final int FOUR_HUNDRED = 400;
    private static final int DIALOG_WIDTH = 150;
    private static final int DIALOG_HEIGHT = 240;
    private static final int SHOWVALBUTTON_WIDTH = 70;
    private static final int SHOWVALBUTTON_HEIGHTH = 25;
    public int showValue = 100;
    public double resolutionTimes = 1.0;
    private static JSliderPane THIS;
    private UITextField showVal;
    private JSpinner showValSpinner;
    private UISlider slider;
    private int times;
    private int sliderValue;
    private UIButton downButton;
    private UIButton upButton;
    private UIButton showValButton;
    private UIRadioButton twoHundredButton;
    private UIRadioButton oneHundredButton;
    private UIRadioButton SevenFiveButton;
    private UIRadioButton fiveTenButton;
    private UIRadioButton twoFiveButton;
    private UIRadioButton selfAdaptButton;
    private UIRadioButton customButton;
    //拖动条处理和button、直接输入不一样
    private boolean isButtonOrIsTxt = true;
    private PopupPane dialog;
    private int upButtonX;
    private JPanel dialogContentPanel;


    public JSliderPane() {
        this.setLayout(new BorderLayout());
        slider = new UISlider(0,HUNDRED,HALF_HUNDRED);
        slider.setUI(new JSliderPaneUI(slider));
        slider.addChangeListener(listener);

        showValSpinner = new UIBasicSpinner(new SpinnerNumberModel(HUNDRED, TEN, FOUR_HUNDRED, 1));
        showValSpinner.setEnabled(true);
        showValSpinner.addChangeListener(showValSpinnerChangeListener);
        showValSpinner.setPreferredSize(new Dimension(SPINNER_WIDTH, SPINNER_HEIGHT));
        //MoMeak：控制只能输入10-400，但是用起来感觉不舒服，先注释掉吧
//        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(showValSpinner, "0");
//        showValSpinner.setEditor(editor);
//        JFormattedTextField textField = ((JSpinner.NumberEditor) showValSpinner.getEditor()).getTextField();
//        textField.setEditable(true);
//        DefaultFormatterFactory factory = (DefaultFormatterFactory) textField .getFormatterFactory();
//        NumberFormatter formatter = (NumberFormatter) factory.getDefaultFormatter();
//        formatter.setAllowsInvalid(false);
        downButton = new UIButton(BaseUtils.readIcon("com/fr/design/images/data/source/moveDown.png"));
        upButton = new UIButton(BaseUtils.readIcon("com/fr/design/images/data/source/moveUp.png"));
        downButton.setActionCommand("less");
        upButton.setActionCommand("more");
        downButton.addActionListener(buttonActionListener);
        upButton.addActionListener(buttonActionListener);

        showValButton = new UIButton(showValSpinner.getValue()+"%");
        showValButton.setBorderPainted(false);
        showValButton.setPreferredSize(new Dimension(SHOWVALBUTTON_WIDTH,SHOWVALBUTTON_HEIGHTH));
        showValButton.addActionListener(showValButtonActionListener);
        initUIRadioButton();
        initPane();
        JPanel panel = new JPanel(new FlowLayout(1,1,0));
        panel.add(downButton);
        panel.add(slider);
        panel.add(upButton);
        panel.add(showValButton);
        this.add(panel,BorderLayout.NORTH);
        this.setBounds(0,0,THREE_HUNDRED,ONE_EIGHT);
    }

    public static final JSliderPane getInstance() {
//        if (THIS == null) {
//            THIS = new JSliderPane();
//        }
        THIS = new JSliderPane();
        return THIS;
    }

    private void initUIRadioButton(){
        twoHundredButton = new UIRadioButton("200%");
        oneHundredButton = new UIRadioButton("100%");
        SevenFiveButton = new UIRadioButton("75%");
        fiveTenButton = new UIRadioButton("50%");
        twoFiveButton = new UIRadioButton("25%");
        selfAdaptButton = new UIRadioButton(Inter.getLocText("FR-Designer_Scale_selfAdaptButton"));
        customButton = new UIRadioButton(Inter.getLocText("FR-Designer_Scale_customButton"));
        twoHundredButton.addItemListener(radioButtonItemListener);
        oneHundredButton.addItemListener(radioButtonItemListener);
        SevenFiveButton.addItemListener(radioButtonItemListener);
        fiveTenButton.addItemListener(radioButtonItemListener);
        twoFiveButton.addItemListener(radioButtonItemListener);
        //TODO
//        selfAdaptButton.addItemListener();

        ButtonGroup bg=new ButtonGroup();// 初始化按钮组
        bg.add(twoHundredButton);// 加入按钮组
        bg.add(oneHundredButton);
        bg.add(SevenFiveButton);
        bg.add(fiveTenButton);
        bg.add(twoFiveButton);
        bg.add(selfAdaptButton);
        bg.add(customButton);
        customButton.setSelected(true);
    }

    private void initPane(){
        double p = TableLayout.PREFERRED;
        double f = TableLayout.FILL;
        double[] columnSize = { p, f };
        double[] rowSize = { p,p,p,p,p,p,p};
        Component[][] components = new Component[][]{
                new Component[]{twoHundredButton,null},
                new Component[]{oneHundredButton,null},
                new Component[]{SevenFiveButton,null},
                new Component[]{fiveTenButton,null},
                new Component[]{twoFiveButton,null},
                new Component[]{selfAdaptButton,null},
                new Component[]{customButton,createSpinnerPanel()}
        };
        dialogContentPanel = TableLayoutHelper.createTableLayoutPane(components,rowSize,columnSize);
    }

    private JPanel createSpinnerPanel(){
        JPanel spinnerPanel = new JPanel(new FlowLayout());
        spinnerPanel.add(showValSpinner);
        UILabel percent = new UILabel("%");
        percent.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        spinnerPanel.add(percent);
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
            int val = (int) ((UIBasicSpinner)e.getSource()).getValue();
            isButtonOrIsTxt = true;
            resolutionTimes = divide(showValue,100,2);
            if (val > FOUR_HUNDRED){
                showValSpinner.setValue(FOUR_HUNDRED);
                val = FOUR_HUNDRED;
            }
            if (val < TEN){
                showValSpinner.setValue(TEN);
                val = TEN;
            }
            refreshSlider(val);
            refreshBottun(val);
        }
    };


    //定义一个监听器，用于监听所有滑动条
    ChangeListener listener = new ChangeListener()
    {
        public void stateChanged( ChangeEvent event) {
            //取出滑动条的值，并在文本中显示出来
            if (!isButtonOrIsTxt){
                customButton.setSelected(true);
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        sliderValue = slider.getValue();
                        getTimes(sliderValue);
                        showValue = times;
                        showValSpinner.setValue(times);
                    }
                });
            }else {
                isButtonOrIsTxt = false;
            }
        }
    };

    ItemListener  radioButtonItemListener = new ItemListener() {
        @Override
        public void itemStateChanged(ItemEvent e) {
            JRadioButton temp=(JRadioButton)e.getSource();
            if(temp.isSelected()){
                showValSpinner.setValue(Integer.valueOf(temp.getText().substring(0, temp.getText().indexOf("%"))));
            }
        }
    };

    private void refreshSlider(int val){
        showValue = val;
        if (showValue >HUNDRED){
            slider.setValue((int)(showValue+TWO_HUNDRED)/SIX);
        }else if (showValue <HUNDRED){
            slider.setValue((int)((showValue-TEN)/ONEPOINTEIGHT));
        }else if (showValue == HUNDRED){
            slider.setValue(HALF_HUNDRED);
        }
    }


    private void refreshBottun(int val){
        showValButton.setText(val+"%");
    }

    public double getResolutionTimes(){
        return this.resolutionTimes;
    }

    public int getshowValue(){
        return this.showValue;
    }

    public static double divide(double v1, double v2,int scale) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale).doubleValue();
    }

    ActionListener buttonActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            showValue = (int) showValSpinner.getValue();
            isButtonOrIsTxt = true;
            if(e.getActionCommand().equals("less")){
                int newDownVal = showValue - TEN;
                if (newDownVal >= TEN ){
                    showValue = newDownVal;
                    showValSpinner.setValue(newDownVal);
                }else {
                    showValue = newDownVal;
                    showValSpinner.setValue(TEN);
                }
            }
            if(e.getActionCommand().equals("more")){
                int newUpVal = showValue + TEN;
                if (newUpVal <= FOUR_HUNDRED ){
                    showValue = newUpVal;
                    showValSpinner.setValue(newUpVal);
                }else {
                    showValue = newUpVal;
                    showValSpinner.setValue(FOUR_HUNDRED);
                }
            }
            isButtonOrIsTxt = true;
            customButton.setSelected(true);
        }
    };



    private void getTimes(int value){
        if (value == HALF_HUNDRED){
            times=HUNDRED;
        }else if (value < HALF_HUNDRED){
            times = (int) Math.round(ONEPOINTEIGHT*value + TEN);
        }else {
            times = (int) (SIX*value - TWO_HUNDRED);
        }
    }


    public JSpinner getShowVal(){
        return this.showValSpinner;
    }

    public UIRadioButton getSelfAdaptButton(){
        return this.selfAdaptButton;
    }

    private void popupDialog(){
        Point btnCoords = upButton.getLocationOnScreen();
        if (dialog == null){
            dialog = new PopupPane(upButton,dialogContentPanel);
            if (upButtonX == 0) {
                upButtonX = btnCoords.x;
                GUICoreUtils.showPopupMenu(dialog, upButton,  - DIALOG_WIDTH + upButton.getWidth() + SHOWVALBUTTON_WIDTH , -DIALOG_HEIGHT);
            }
        }else {
            if (upButtonX == 0) {
                upButtonX = btnCoords.x;
                GUICoreUtils.showPopupMenu(dialog, upButton,  - DIALOG_WIDTH + upButton.getWidth() +SHOWVALBUTTON_WIDTH, -DIALOG_HEIGHT);
            } else {
                GUICoreUtils.showPopupMenu(dialog, upButton,  - DIALOG_WIDTH + upButton.getWidth() +SHOWVALBUTTON_WIDTH, -DIALOG_HEIGHT);
            }
        }
    }

    public static void main(String[] args)
    {
        JFrame jf = new JFrame("test");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = (JPanel)jf.getContentPane();
        content.setLayout(new BorderLayout());
        content.add(JSliderPane.getInstance(),BorderLayout.CENTER);
        GUICoreUtils.centerWindow(jf);
        jf.setSize(400, 80);
        jf.setVisible(true);

    }
}

class JSliderPaneUI extends BasicSliderUI {

    private static final int VERTICAL_WIDTH = 11;
    private static final int VERTICAL_HEIGHT = 16;
    private static final int FOUR = 4;
    private static final int FIVE = 5;
    private static final int SIX = 6;

    public JSliderPaneUI(UISlider b) {
        super(b);
    }

    /** */
    /**
     * 绘制指示物
     */

    public Dimension getThumbSize() {
        Dimension size = new Dimension();

        if ( slider.getOrientation() == JSlider.VERTICAL ) {
            size.width = VERTICAL_WIDTH;
            size.height = VERTICAL_HEIGHT;
        }
        else {
            size.width = VERTICAL_WIDTH;
            size.height = VERTICAL_HEIGHT;
        }

        return size;
    }

    public void paintThumb(Graphics g) {
        Rectangle knobBounds = thumbRect;
        int w = knobBounds.width;
        int h = knobBounds.height;

        g.translate(knobBounds.x, knobBounds.y);
        if ( slider.isEnabled() ) {
            g.setColor(slider.getBackground());
        }
        else {
            g.setColor(slider.getBackground().darker());
        }
        g.setColor(Color.darkGray);
        g.fillRect(0, 1, w-SIX, h+1);
    }

    /** */
    /**
     * 绘制刻度轨迹
     */
    public void paintTrack(Graphics g) {
        int cy, cw;
        Rectangle trackBounds = trackRect;
        if (slider.getOrientation() == UISlider.HORIZONTAL) {
            Graphics2D g2 = (Graphics2D) g;
            cy = (trackBounds.height / 2);
            cw = trackBounds.width;
            g.setColor(Color.lightGray);
            g.drawLine(0, cy, cw+FIVE, cy);
            g.drawLine(FIVE+cw/2, cy-FOUR, FIVE+cw/2, cy+FOUR);
        } else {
            super.paintTrack(g);
        }
    }

}

class PopupPane extends JPopupMenu {
    private JComponent contentPane;
    private static final int UPLABEL_HEIGHT = 25;
    private static final int DIALOG_WIDTH = 150;
    private static final int DIALOG_HEIGHT = 240;
    private static final int UPLABEL_WIDTH = 300;
    private JComponent centerPane;
    private UILabel upLabel;
    PopupPane(UIButton b,JPanel dialogContentPanel) {
        contentPane = new JPanel(new BorderLayout());
        centerPane = new JPanel(new BorderLayout());
        upLabel = new UILabel(" " + Inter.getLocText("FR-Designer_Scale_EnlargeOrReduce"));
        upLabel.setOpaque(true);
        upLabel.setPreferredSize(new Dimension(UPLABEL_WIDTH,UPLABEL_HEIGHT));
        upLabel.setBackground(Color.LIGHT_GRAY);
        upLabel.setBorder(new MatteBorder(0,0,1,0,Color.gray));
        centerPane.add(dialogContentPanel,BorderLayout.NORTH);
        contentPane.add(upLabel,BorderLayout.NORTH);
        contentPane.add(centerPane,BorderLayout.CENTER);
//        contentPane.setBorder(new MatteBorder(1,1,1,1,Color.darkGray));
        this.add(contentPane, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        this.setOpaque(false);
    }



}