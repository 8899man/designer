package com.fr.design.actions.server;


import com.fr.data.impl.Connection;
import com.fr.design.actions.UpdateAction;
import com.fr.design.data.datapane.connect.ConnectionManagerPane;
import com.fr.design.data.datapane.connect.ConnectionShowPane;
import com.fr.design.data.datapane.connect.DatabaseConnectionPane;
import com.fr.design.dialog.BasicDialog;
import com.fr.design.dialog.DialogActionAdapter;
import com.fr.design.gui.NameInspector;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.DesignerFrame;
import com.fr.design.menu.MenuKeySet;
import com.fr.file.ConnectionConfig;
import com.fr.general.IOUtils;
import com.fr.transaction.CallBackAdaptor;
import com.fr.transaction.Configurations;
import com.fr.transaction.WorkerFacade;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;

/**
 * DatasourceList Action
 */
public class ConnectionListAction extends UpdateAction {

    public ConnectionListAction() {
        this.setMenuKeySet(DEFINE_DATA_CONNECTION);
        this.setName(getMenuKeySet().getMenuKeySetName());
        this.setMnemonic(getMenuKeySet().getMnemonic());
        this.setSmallIcon(IOUtils.readIcon("/com/fr/design/images/m_web/connection.png"));
        this.generateAndSetSearchText(DatabaseConnectionPane.JDBC.class.getName());
    }

    public static final MenuKeySet DEFINE_DATA_CONNECTION = new MenuKeySet() {
        @Override
        public char getMnemonic() {
            return 'D';
        }

        @Override
        public String getMenuName() {
            return com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Basic_Server_Define_Data_Connection");
        }

        @Override
        public KeyStroke getKeyStroke() {
            return null;
        }
    };

    /**
     * 执行动作
     *
     * @param evt 事件
     */
    public void actionPerformed(ActionEvent evt) {
        DesignerFrame designerFrame = DesignerContext.getDesignerFrame();
        final ConnectionConfig datasourceManager = ConnectionConfig.getInstance();
        final ConnectionManagerPane databaseManagerPane = new ConnectionManagerPane() {
            public void complete() {
                ConnectionConfig connectionConfig = datasourceManager.mirror();
                populate(connectionConfig);
            }

            protected void renameConnection(String oldName, String newName) {
                datasourceManager.renameConnection(oldName, newName);
            }
        };
        final BasicDialog databaseListDialog = databaseManagerPane.showLargeWindow(designerFrame, null);
        databaseListDialog.addDialogActionListener(new DialogActionAdapter() {
            public void doOk() {
                if (!databaseManagerPane.isNamePermitted()) {
                    databaseListDialog.setDoOKSucceed(false);
                    return;
                }
                Configurations.modify(new WorkerFacade(ConnectionConfig.class) {
                    @Override
                    public void run() {
                        databaseManagerPane.update(datasourceManager);
                    }
                }.addCallBack(new CallBackAdaptor() {
                    @Override
                    public boolean beforeCommit() {
                        //如果更新失败，则不关闭对话框，也不写xml文件，并且将对话框定位在请重命名的那个对象页面
                        return doWithDatasourceManager(datasourceManager, databaseManagerPane, databaseListDialog);
                    }

                    @Override
                    public void afterCommit() {
                        DesignerContext.getDesignerBean("databasename").refreshBeanElement();
                    }
                }));
            }
        });
        databaseListDialog.setVisible(true);
    }


    /**
     * 更新datasourceManager
     *
     * @param datasourceManager  datasource管理对象
     * @param connectionShowPane datasource面板
     * @param databaseListDialog datasource管理对话框
     * @return boolean 是否更新成功
     */
    public static boolean doWithDatasourceManager(ConnectionConfig datasourceManager, ConnectionShowPane connectionShowPane, BasicDialog databaseListDialog) {
        boolean isFailed = false;
        //存在请重命名则不能更新
        int index = isConnectionMapContainsRename(datasourceManager);
        if (index != -1) {
            isFailed = true;
            connectionShowPane.setSelectedIndex(index);
        }
        databaseListDialog.setDoOKSucceed(!isFailed);

        return !isFailed;
    }


    /**
     * 是否包含重命名key
     *
     * @return 包含则返回序列 ,若返回-1则说明不包含重命名key
     */
    public static int isConnectionMapContainsRename(ConnectionConfig datasourceManager) {
        Map<String, Connection> tableDataMap = datasourceManager.getConnections();
        if (tableDataMap.containsKey(NameInspector.ILLEGAL_NAME_HOLDER)) {
            return datasourceManager.getConnectionIndex(NameInspector.ILLEGAL_NAME_HOLDER);
        }
        return -1;
    }


    public void update() {
        this.setEnabled(true);
    }
}