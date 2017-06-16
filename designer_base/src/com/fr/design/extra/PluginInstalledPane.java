package com.fr.design.extra;

import com.fr.design.RestartHelper;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.general.Inter;
import com.fr.plugin.context.PluginContext;
import com.fr.plugin.context.PluginMarker;
import com.fr.plugin.manage.PluginManager;
import com.fr.plugin.manage.control.PluginTaskCallback;
import com.fr.plugin.manage.control.PluginTaskResult;
import com.fr.plugin.view.PluginView;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author richie
 * @date 2015-03-10
 * @since 8.0
 */
public class PluginInstalledPane extends PluginAbstractViewPane {

    private int num;
    private UIButton disableButton;
    private UIButton deleteButton;
    private PluginControlPane controlPane;


    public PluginInstalledPane() {
        setLayout(new BorderLayout());
        controlPane = new PluginControlPane();
        add(controlPane, BorderLayout.CENTER);

        JPanel panel = createOperationPane();

        add(panel, BorderLayout.SOUTH);

        disableButton = new UIButton(Inter.getLocText("FR-Designer-Plugin_Disable"));
        disableButton.setEnabled(false);
        deleteButton = new UIButton(Inter.getLocText("FR-Designer-Plugin_Delete"));
        deleteButton.setEnabled(false);
        panel.add(disableButton);
        panel.add(deleteButton);
        controlPane.addPluginSelectionListener(new PluginSelectListener() {
            @Override
            public void valueChanged(PluginView plugin) {
                disableButton.setEnabled(true);
                deleteButton.setEnabled(true);
                changeTextForButton(plugin);
            }
        });
        disableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PluginView plugin = controlPane.getSelectedPlugin();
                if (plugin != null) {
                    boolean isActive = plugin.isActive();
                    PluginMarker pluginMarker = PluginMarker.create(plugin.getID(), plugin.getVersion());
                    String modifyMessage = isActive ? Inter.getLocText("FR-Designer-Plugin_Has_Been_Actived") : Inter.getLocText("FR-Designer-Plugin_Has_Been_Disabled");
                    if (isActive) {
                        PluginManager.getController().forbid(pluginMarker, new PluginTaskCallback() {
                            @Override
                            public void done(PluginTaskResult result) {
                                if (result.isSuccess()) {
                                    JOptionPane.showMessageDialog(null, modifyMessage);
                                } else {
                                    JOptionPane.showMessageDialog(null, result.getMessage(), Inter.getLocText("FR-Designer-Plugin_Warning"), JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
                    } else {
                        PluginManager.getController().enable(pluginMarker, new PluginTaskCallback() {
                            @Override
                            public void done(PluginTaskResult result) {
                                if (result.isSuccess()) {
                                    JOptionPane.showMessageDialog(null, modifyMessage);
                                } else {
                                    JOptionPane.showMessageDialog(null, result.getMessage(), Inter.getLocText("FR-Designer-Plugin_Warning"), JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
                    }
                    changeTextForButton(plugin);
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doDelete(controlPane.getSelectedPlugin());
            }
        });

        List<PluginContext> plugins = PluginManager.getContexts();
        List<PluginView> pluginViews = new ArrayList<>();
        for (PluginContext plugin : plugins) {
            pluginViews.add((PluginView) plugin);
        }
        controlPane.loadPlugins(pluginViews);
        num = plugins.size();
    }

    /**
     * tab标题
     *
     * @return 同上
     */
    public String tabTitle() {
        return Inter.getLocText("FR-Designer-Plugin_Installed") + "(" + num + ")";
    }

    private void doDelete(PluginView plugin) {
        int rv = JOptionPane.showOptionDialog(
                PluginInstalledPane.this,
                Inter.getLocText("FR-Designer-Plugin_Will_Be_Delete"),
                Inter.getLocText("FR-Designer-Plugin_Warning"),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new String[]{Inter.getLocText("FR-Designer-Basic_Restart_Designer"),
                        Inter.getLocText("FR-Designer-Basic_Restart_Designer_Later"),
                        Inter.getLocText("FR-Designer-Basic_Cancel")
                },
                null
        );
        if (rv == JOptionPane.CANCEL_OPTION || rv == JOptionPane.CLOSED_OPTION) {
            return;
        }

        try {
            controlPane.deletePlugin(plugin);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(PluginInstalledPane.this, e.getMessage(), Inter.getLocText("FR-Designer-Plugin_Warning"), JOptionPane.ERROR_MESSAGE);
        }

        if (rv == JOptionPane.OK_OPTION) {
            RestartHelper.restart();
        }
    }

    private void changeTextForButton(PluginView plugin) {
        if (plugin.isActive()) {
            disableButton.setText(Inter.getLocText("FR-Designer-Plugin_Disable"));
        } else {
            disableButton.setText(Inter.getLocText("FR-Designer-Plugin_Active"));
        }
    }

    @Override
    protected String title4PopupWindow() {
        return "Installed";
    }
}