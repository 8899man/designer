package com.fr.design.mainframe;

import com.fr.design.constants.UIConstants;
import com.fr.design.designer.TargetComponent;
import com.fr.design.gui.icontainer.UIScrollPane;
import com.fr.design.gui.ilable.UILabel;
import com.fr.design.layout.FRGUIPaneFactory;


import javax.swing.*;
import java.awt.*;

/**
 * Author : daisy
 * Date: 13-9-12
 * Time: 下午6:14
 */
public class AuthorityPropertyPane extends JPanel {
	private static final int TITLE_HEIGHT = 19;
	private AuthorityEditPane authorityEditPane = null;

	public AuthorityPropertyPane(TargetComponent t) {
		this.setLayout(new BorderLayout());
		this.setBorder(null);
		UILabel authorityTitle = new UILabel(com.fr.design.i18n.Toolkit.i18nText("Fine-Design_Privilege_Preference")) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, TITLE_HEIGHT);
			}
		};
		authorityTitle.setHorizontalAlignment(SwingConstants.CENTER);
		authorityTitle.setVerticalAlignment(SwingConstants.CENTER);
		JPanel northPane = FRGUIPaneFactory.createBorderLayout_S_Pane();
		northPane.add(authorityTitle, BorderLayout.CENTER);
		northPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.LINE_COLOR));
//		this.add(northPane, BorderLayout.NORTH);
		authorityEditPane = t.createAuthorityEditPane();
		UIScrollPane scrollPane = new UIScrollPane(authorityEditPane);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void populate() {
		authorityEditPane.populateDetials();
	}


}