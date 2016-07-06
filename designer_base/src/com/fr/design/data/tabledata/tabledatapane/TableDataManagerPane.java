package com.fr.design.data.tabledata.tabledatapane;

import com.fr.base.FRContext;
import com.fr.design.DesignModelAdapter;
import com.fr.design.ExtraDesignClassManager;
import com.fr.design.data.datapane.TableDataPaneController;
import com.fr.design.data.datapane.TableDataPaneListPane;
import com.fr.design.fun.TableDataPaneProcessor;
import com.fr.design.gui.frpane.LoadingBasicPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.gui.itextfield.UITextField;
import com.fr.design.layout.FRGUIPaneFactory;
import com.fr.file.DatasourceManagerProvider;
import com.fr.general.Inter;
import com.fr.stable.project.ProjectConstants;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class TableDataManagerPane extends LoadingBasicPane {

	private UITextField tableDataTextField;
	private TableDataPaneController tableDataPane;

	@Override
	protected void initComponents(JPanel container) {
		this.initTableDataManagerPane(container);
	}

	private void initTableDataManagerPane(JPanel container) {
		container.setLayout(FRGUIPaneFactory.createBorderLayout());

		JPanel tableDataPathPane = FRGUIPaneFactory.createBorderLayout_L_Pane();
		container.add(tableDataPathPane, BorderLayout.NORTH);


		tableDataPathPane.add(new UILabel(Inter.getLocText("FR-Designer_Save_Path") + ":"), BorderLayout.WEST);
		this.tableDataTextField = new UITextField();
		tableDataPathPane.add(tableDataTextField, BorderLayout.CENTER);
		this.tableDataTextField.setEditable(false);
		TableDataPaneProcessor paneProcessor = ExtraDesignClassManager.getInstance().getSingle(TableDataPaneProcessor.XML_TAG);
		TableDataPaneController pane = null;
		if (paneProcessor != null) {
			pane = paneProcessor.createServerTableDataPane(DesignModelAdapter.getCurrentModelAdapter()
			);
		}
		tableDataPane = pane == null ? new TableDataPaneListPane() {
			public void rename(String oldName, String newName) {
				super.rename(oldName, newName);
				renameConnection(oldName, newName);
			}
		} : pane;
		container.add(tableDataPane.getPanel(), BorderLayout.CENTER);
	}


    /**
     * 名字是否允许
     * @return 是则返回true
     */
    public  boolean isNamePermitted(){
        return tableDataPane.isNamePermitted();
    }

    /**
     * 检查
     * @throws Exception 异常
     */
	public void checkValid() throws Exception {
		tableDataPane.checkValid();
	}
	
	@Override
	protected String title4PopupWindow() {
		return Inter.getLocText("DS-Server_TableData");
	}

	public void populate(DatasourceManagerProvider datasourceManager) {
		this.tableDataTextField.setText(FRContext.getCurrentEnv().getPath() + File.separator + ProjectConstants.RESOURCES_NAME
				+ File.separator + datasourceManager.fileName());
		this.tableDataPane.populate(datasourceManager);
	}

	public void update(DatasourceManagerProvider datasourceManager) {
		this.tableDataPane.update(datasourceManager);
	}

    public Map<String, String> getDsChangedNameMap () {
        return this.tableDataPane.getDsNameChangedMap();
    }

    /**
     * 设置选中项
     *
     * @param index 选中项的序列号
     */
    public void setSelectedIndex(int index) {
        this.tableDataPane.setSelectedIndex(index);
    }
}