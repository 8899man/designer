package com.fr.design.fun;

import com.fr.stable.fun.Level;

/**
 * ��env����֮ǰ, �޸�env�����Ľӿ�
 * ��: https [��Ҫ�ڼ���֮ǰ����ϵͳ֤��];
 * cas [��Ҫ��̬��ȡsessionid���޸�path]
 *
 * Created by Administrator on 2016/3/31/0031.
 */
public interface DesignerEnvProcessor extends Level{

    String XML_TAG = "DesignerEnvProcessor";
    int CURRENT_LEVEL = 1;

    /**
     * ��Զ��env����֮ǰ, �޸�env��path, ��ͨ��Ȩ����֤. ���֮ǰû��jsessionid, ��ô�ͼ���, �������, �͸��³��µ�.
     * ��: localhost:8080/WebReport/ReportServer? -> localhost:8080/WebReport/ReportServer?jsessionid=abcdegf;
     *
     * @return �޸ĺ��jsessionid
     */
    String changeEnvPathBeforeConnect(String userName, String password, String path);
}
