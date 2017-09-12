package com.fr.design.parameter;

import com.fr.design.constants.UIConstants;
import com.fr.design.designer.creator.XWParameterLayout;
import com.fr.design.dialog.BasicScrollPane;
import com.fr.design.gui.ibutton.UIButton;
import com.fr.design.mainframe.DesignerContext;
import com.fr.design.mainframe.FormDesigner;
import com.fr.design.mainframe.FormHierarchyTreePane;
import com.fr.design.mainframe.JForm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ParameterPropertyPane extends JPanel{
	private ParameterToolBarPane toolbarPane;
	private ParaDefinitePane paraPane;
	private JPanel formHierarchyTreePaneWrapper;  // 封装一层，加边框
    private JPanel addParaPane;


    private static ParameterPropertyPane THIS;
	private boolean isEditing = false;
    private static final int HIDE_HEIGHT = 40;
    private static final int PADDING_SMALL = 5;
    private static final int PADDING_MIDDLE = 10;
    private static final int PADDING_LARGE = 15;

	public static final ParameterPropertyPane getInstance() {
		if (THIS == null) {
			THIS = new ParameterPropertyPane();
		}
		return THIS;
	}

	public static final ParameterPropertyPane getInstance(FormDesigner editor) {
		if (THIS == null) {
			THIS = new ParameterPropertyPane();
		}
		THIS.setEditor(editor);
		return THIS;
	}

	public void repaintContainer() {
		validate();
		repaint();
		revalidate();
	}

	private ParameterPropertyPane() {
        init();
	}

    private void init() {
        toolbarPane = new ParameterToolBarPane();
        BasicScrollPane basicScrollPane = new BasicScrollPane() {
            @Override
            protected JPanel createContentPane() {
                return toolbarPane;
            }

            @Override
            public void populateBean(Object ob) {
                // do nothing
            }

            @Override
            protected String title4PopupWindow() {
                return null;
            }
        };
        JPanel scrollPaneWrapperInner = new JPanel(new BorderLayout());
        scrollPaneWrapperInner.setBorder(BorderFactory.createEmptyBorder(0, PADDING_MIDDLE, PADDING_MIDDLE, PADDING_SMALL));
        scrollPaneWrapperInner.add(basicScrollPane, BorderLayout.CENTER);
        addParaPane = new JPanel(new BorderLayout());
        addParaPane.add(scrollPaneWrapperInner, BorderLayout.CENTER);
        addParaPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.SPLIT_LINE));

        initParameterListener();
        this.setLayout(new BorderLayout(0, 6));
        this.setBorder(BorderFactory.createEmptyBorder(PADDING_MIDDLE, 0, PADDING_MIDDLE, 0));
        this.add(addParaPane, BorderLayout.CENTER);
    }

    // 显示或隐藏添加参数面板
	public void refreshState() {
		setAddParaPaneVisible(toolbarPane.hasSelectedLabelItem());
	}

    public void setAddParaPaneVisible(boolean isVisible) {
        if (isVisible == addParaPane.isVisible() || formHierarchyTreePaneWrapper == null) {
            return;
        }
        // 表单中，只有添加并选中参数面板时，才显示
		boolean hideInJForm;
        try {
            hideInJForm = DesignerContext.getDesignerFrame().getSelectedJTemplate() instanceof JForm &&
                    !(FormHierarchyTreePane.getInstance().getComponentTree().getSelectionPath().getLastPathComponent() instanceof XWParameterLayout);
        } catch (NullPointerException ex) {
            hideInJForm = true;
        }
        if (isVisible && toolbarPane.hasSelectedLabelItem() && !hideInJForm) {
            addParaPane.setVisible(true);
            this.setPreferredSize(null);
        } else {
            addParaPane.setVisible(false);
            this.setPreferredSize(new Dimension(getWidth(), formHierarchyTreePaneWrapper.getPreferredSize().height + UIConstants.GAP_NORMAL));
        }
        repaintContainer();
    }
	
	private void setEditor(FormDesigner editor) {
		if (formHierarchyTreePaneWrapper == null) {
			formHierarchyTreePaneWrapper = new JPanel(new BorderLayout());
			formHierarchyTreePaneWrapper.setBorder(BorderFactory.createEmptyBorder(0, PADDING_MIDDLE, 0, PADDING_LARGE));
			this.add(formHierarchyTreePaneWrapper, BorderLayout.SOUTH);
		}
		formHierarchyTreePaneWrapper.remove(FormHierarchyTreePane.getInstance());
		formHierarchyTreePaneWrapper.add(FormHierarchyTreePane.getInstance(editor), BorderLayout.CENTER);
	}

	private void initParameterListener() {
		toolbarPane.setParaMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (paraPane == null) {
					return;
				}
				final UIButton parameterSelectedLabel = (UIButton) e.getSource();
				// 不用多线程可能会出现死循环
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if (paraPane.isWithQueryButton()) {
							paraPane.addingParameter2Editor(toolbarPane.getTargetParameter(parameterSelectedLabel));
						} else {
							paraPane.addingParameter2EditorWithQueryButton(toolbarPane.getTargetParameter(parameterSelectedLabel));
						}
					}
				});
			}
		});

		toolbarPane.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (paraPane == null) {
					return;
				}
				paraPane.addingAllParameter2Editor();
			}
		});
	}

	public ParameterToolBarPane getParameterToolbarPane() {
		return toolbarPane;
	}

	public void populateBean(ParaDefinitePane paraPane) {
		this.isEditing = false;
		this.paraPane = paraPane;
		this.isEditing = true;
	}
}