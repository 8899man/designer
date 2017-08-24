package com.fr.design.mainframe.errorinfo;

import com.fr.base.ConfigManager;
import com.fr.base.FRContext;
import com.fr.base.io.IOFile;
import com.fr.base.io.XMLReadHelper;
import com.fr.design.DesignerEnvManager;
import com.fr.general.FRLogLevel;
import com.fr.general.FRLogManager;
import com.fr.general.Inter;
import com.fr.general.LogDuration;
import com.fr.stable.StringUtils;
import com.fr.stable.project.ProjectConstants;
import com.fr.stable.xml.XMLPrintWriter;
import com.fr.stable.xml.XMLableReader;
import com.fr.web.core.SessionDealWith;
import com.fr.web.core.SessionIDInfor;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import java.io.InputStream;

/**
 * 收集设计器报错信息的appender.
 * <p>
 * Created by Administrator on 2017/7/24 0024.
 */
public class ErrorInfoLogAppender extends AppenderSkeleton {

    private static final int ERROR_LEN = 8;

    // 缓存下不变的, 没必要频繁取.
    private String username;
    private String uuid;
    private String activekey;

    public ErrorInfoLogAppender() {
        this.layout = new org.apache.log4j.PatternLayout("%d{HH:mm:ss} %t %p [%c] %m%n");

        DesignerEnvManager envManager = DesignerEnvManager.getEnvManager();
        this.username = ConfigManager.getProviderInstance().getBbsUsername();
        this.uuid = envManager.getUUID();
        this.activekey = envManager.getActivationKey();
    }

    protected void append(LoggingEvent event) {
        this.subAppend(event);
    }

    public boolean requiresLayout() {
        return true;
    }

    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;

    }

    public void subAppend(LoggingEvent event) {
        Level level = event.getLevel();
        // 只分析上传记录error以上的.
        if (level.isGreaterOrEqual(FRLogLevel.ERROR)) {
            String msg = this.layout.format(event);
            // 这个id并不是一定会有的, 有就记录下, 说明是预览模板出的错.
            String templateid = readTemplateID();
            String logid = readLogID(msg);
            ErrorInfo errorInfo = new ErrorInfo(username, uuid, activekey);
            errorInfo.setTemplateid(templateid);
            errorInfo.setLog(msg);
            errorInfo.setLogid(logid);
            errorInfo.saveAsJSON();
        }
    }

    private String readLogID(String log) {
        String errorCode = Inter.getLocText("FR-Engine_ErrorCode-Prefix");
        // 报错信息国际化不规范, 有些是中文分号, 有些是英文
        String[] matchs = log.split(errorCode + ".*?[:,：]");
        if (matchs.length <= 1) {
            return StringUtils.EMPTY;
        }

        String includeIDStr = matchs[1].trim();
        try {
            return Long.parseLong(includeIDStr.substring(0, ERROR_LEN)) + StringUtils.EMPTY;
        } catch (Exception ignore) {

        }

        return StringUtils.EMPTY;
    }

    private String readTemplateID() {
        LogDuration logDuration = FRLogManager.getSession();
        if (logDuration == null) {
            return StringUtils.EMPTY;
        }

        String sessionID = logDuration.getSessionID();
        SessionIDInfor infor = SessionDealWith.getSessionIDInfor(sessionID);
        if (infor == null) {
            return StringUtils.EMPTY;
        }

        String bookPath = infor.getBookPath();
        // 这个iofile只读一个templateid, 其他以后有需要再读.
        IOFile file = new IOFile() {
            @Override
            public void readStream(InputStream in) throws Exception {
                XMLableReader xmlReader = XMLReadHelper.createXMLableReader(in, XMLPrintWriter.XML_ENCODER);
                xmlReader.readXMLObject(this);
                xmlReader.close();
                in.close();
            }

            @Override
            public void readXML(XMLableReader reader) {
                super.readXML(reader);
                readDesign(reader);
            }

            @Override
            protected String openTag() {
                return StringUtils.EMPTY;
            }

            @Override
            protected void mainContent(XMLPrintWriter writer) {
            }
        };
        try {
            file.readStream(FRContext.getCurrentEnv().readBean(bookPath, ProjectConstants.REPORTLETS_NAME));
            return file.getTemplateID();
        } catch (Exception ignore) {
        }

        return StringUtils.EMPTY;
    }
}