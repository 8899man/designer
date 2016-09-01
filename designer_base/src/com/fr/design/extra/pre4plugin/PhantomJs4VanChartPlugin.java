package com.fr.design.extra.pre4plugin;

/**
 * Created by hufan on 2016/8/31.
 */
import com.fr.base.FRContext;
import com.fr.design.extra.PluginConstants;
import com.fr.design.extra.PluginHelper;
import com.fr.design.mainframe.DesignerContext;
import com.fr.general.IOUtils;
import com.fr.general.Inter;
import com.fr.general.SiteCenter;
import com.fr.general.http.HttpClient;
import com.fr.stable.StableUtils;
import com.fr.stable.StringUtils;

import java.awt.*;

import java.awt.event.ActionEvent;



import java.awt.event.ActionListener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import javax.swing.*;


public class PhantomJs4VanChartPlugin implements ActionListener, PreEnv4Plugin {
    private static final String ID = "plugin.phantomjs";
    //链接服务器的客户端
    private HttpClient httpClient;
    //已读文件字节数
    private int totalBytesRead = 0;
    //文件总长度
    private int totalSize = 0;
    //进度显示界面
    private JDialog frame = null;
    //进度条
    private JProgressBar progressbar;
    //进度信息
    private JLabel label;
    //进度条更新时钟
    private Timer timer;
    //文件路径
    private String filePath = StringUtils.EMPTY;
    //是否继续下载
    private boolean flag = true;
    //安装结果
    boolean result = false;

    private static final String WEB_INFO = FRContext.getCurrentEnv().getPath();
    private static final String WEB_REPORT = new File(WEB_INFO).getParent();
    public static String PHANTOM_ENV = WEB_REPORT + File.separator + "phantomjs";

    // 定义加载窗口大小
    private static final int LOAD_WIDTH = 455;
    private static final int LOAD_HEIGHT = 295;

    public String getFilePath() {
        return filePath;
    }

    public PhantomJs4VanChartPlugin() {
    }

    //是否可以连接服务器
    private boolean serverReached(){
        connectToServer();
        return totalSize != -1;
    }

    private void init(){
        // 创建标签,并在标签上放置一张图片
        BufferedImage image =  IOUtils.readImage("/com/fr/design/extra/pre4plugin/image/background.png");
        ImageIcon imageIcon = new ImageIcon(image);
        label = new JLabel(imageIcon);
        label.setBounds(0, 0, LOAD_WIDTH, LOAD_HEIGHT - 15);

        progressbar = new JProgressBar();
        // 显示当前进度值信息
        progressbar.setStringPainted(true);
        // 设置进度条边框不显示
        progressbar.setBorderPainted(false);
        // 设置进度条的前景色
        progressbar.setForeground(new Color(0x38aef5));
        // 设置进度条的背景色
        progressbar.setBackground(new Color(188, 190, 194));
        progressbar.setBounds(0, LOAD_HEIGHT - 15, LOAD_WIDTH, 15);
        progressbar.setMinimum(0);
        progressbar.setMaximum(totalSize);
        progressbar.setValue(0);

        timer = new Timer(100, this);

        frame = new JDialog(DesignerContext.getDesignerFrame(), true);
        frame.setTitle("在线安装phantomjs");
        frame.setSize(LOAD_WIDTH, LOAD_HEIGHT);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenSize.width/2-LOAD_WIDTH/2,screenSize.height/2-LOAD_HEIGHT/2);
        frame.setResizable(false);
        // 设置布局为空
        frame.setLayout(new BorderLayout(0,0));
        frame.getContentPane().add(label, BorderLayout.CENTER);
        frame.getContentPane().add(progressbar, BorderLayout.SOUTH);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                //取消下载
                flag = false;
                frame.dispose();
            }
        });
    }

    private void connectToServer(){
        httpClient = new HttpClient(SiteCenter.getInstance().acquireUrlByKind(ID));
        if (httpClient.getResponseCode() == HttpURLConnection.HTTP_OK) {
            totalSize =  httpClient.getContentLength();
        }else {
            totalSize = -1;
        }
    }

    private int getFileLength(){
        httpClient = new HttpClient(SiteCenter.getInstance().acquireUrlByKind(ID));
        if (httpClient.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return  httpClient.getContentLength();
        }
        return -1;
    }

    //安装
    private boolean install() {
        //连接服务器
        connectToServer();
        //初始化安装进度界面
        init();
        //开始时钟
        timer.start();
        //开始下载
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
               installPhantomJsOnline(httpClient);
            }
        });
        thread.start();

        frame.setVisible(true);
        //等待下载线程处理结束
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        //停止时钟
        timer.stop();
        return result;
    }

    private boolean downloadPluginPhantomJSFile(HttpClient httpClient) throws Exception {
        if (httpClient.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream reader = httpClient.getResponseStream();
            String temp = StableUtils.pathJoin(PluginHelper.DOWNLOAD_PATH, PluginHelper.TEMP_FILE);
            StableUtils.makesureFileExist(new File(temp));
            FileOutputStream writer = new FileOutputStream(temp);
            byte[] buffer = new byte[PluginConstants.BYTES_NUM];
            int bytesRead = 0;
            totalBytesRead = 0;

            while ((bytesRead = reader.read(buffer)) > 0 && flag) {
                writer.write(buffer, 0, bytesRead);
                buffer = new byte[PluginConstants.BYTES_NUM];
                totalBytesRead += bytesRead;
            }
            reader.close();
            writer.flush();
            writer.close();
            filePath = temp;

            //下载被取消
            if (flag == false){
                result =  false;
                return false;
            }

            //准备安装

        } else {
            result = false;
            throw new com.fr.plugin.PluginVerifyException(Inter.getLocText("FR-Designer-Plugin_Connect_Server_Error"));
        }
        return true;
    }

    public void installPhantomJsOnline(HttpClient httpClient){
        try {
            if (downloadPluginPhantomJSFile(httpClient)){
                //安装文件
                installPluginPhantomJsFile(filePath);
                result = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //安装已经下载好的文件
    private void installPluginPhantomJsFile(String filePath){
        IOUtils.unzip(new File(filePath), PHANTOM_ENV);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == timer) {
            int value = progressbar.getValue();
            if (value < totalSize) {
                progressbar.setValue(totalBytesRead);
            }
            else {
                timer.stop();
                frame.dispose();
            }
        }
    }



/*    public void stateChanged(ChangeEvent e1) {
        double value = (double)progressbar.getValue() / 1000000.0;
        if (e1.getSource() == progressbar) {
            label.setText("已下载：" + Double.toString(value) + " m");
            label.setForeground(Color.blue);
        }
    }*/


    @Override
    public boolean preOnline() {
        int fileLength = getFileLength();
        int choose = JOptionPane.showConfirmDialog(null, "新图表需要phantomjs支持。是否需要安装phantomjs(" + fileLength/1000000 + " m)？", "install tooltip", JOptionPane.YES_NO_OPTION);
        if (choose == 0){//下载安装
            if (!serverReached()){
                JOptionPane.showMessageDialog(null, "无法连接远程服务器！！", "警告", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            //安装phantomJs
            if (install()){
                JOptionPane.showMessageDialog(null, "安装成功！！");
                return true;
            }else {
                JOptionPane.showMessageDialog(null, "安装失败！！", "警告", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }else {//不安装。无需为用户准备环境
            return true;
        }
    }

    @Override
    public boolean checkEnv() {
        //只有图表插件在下载前需要准备环境
        String web_info = FRContext.getCurrentEnv().getPath();
        String web_report = new File(web_info).getParent();
        //检测路径下有没有需要的环境
        return checkPath(web_report, "phantomjs");
    }

    private boolean checkPath(String rootPath, String targetFileName) {
        //创建server环境路径
        String serverPath = rootPath + File.separator + targetFileName;
        File phantom = new File(serverPath);
        return phantom.exists() && phantom.isDirectory();
    }
}
