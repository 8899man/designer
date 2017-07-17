package com.fr.design.mainframe.loghandler;

import com.fr.general.LogRecordTime;
import com.fr.stable.StringUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by Administrator on 2017/6/2/0002.
 */
public class DesignerLogAppendThread extends Thread{

    PipedReader reader;

    public DesignerLogAppendThread() {
        Logger root = Logger.getRootLogger();
        // ��ȡ�Ӽ�¼�������Դ
        Appender appender = root.getAppender("design");
        // ����һ��δ���ӵ��������ܵ�
        reader = new PipedReader();
        // ����һ�������ӵ���������������ӵ�reader
        Writer writer = null;
        try {
            writer = new PipedWriter(reader);
            // ���� appender �����
            ((WriterAppender) appender).setWriter(writer);
        } catch (Throwable e) {
        }
    }

    public void run() {
        // ����ϵ�ɨ��������
        Scanner scanner = new Scanner(reader);

        // ��ɨ�赽���ַ�����ӡ����Ŀ
        while (scanner.hasNext()) {
            try {
                Thread.sleep(100);
                String log = scanner.nextLine();
                if (StringUtils.isEmpty(log)) {
                    return;
                }

                LogRecordTime logRecordTime = new LogRecordTime(new Date(),new LogRecord(Level.INFO, log));
                DesignerLogHandler.getInstance().printRemoteLog(logRecordTime);
            } catch (Throwable e) {

            }
        }
    }
}
