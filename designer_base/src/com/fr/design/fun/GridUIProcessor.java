package com.fr.design.fun;

import com.fr.stable.fun.mark.Immutable;

import javax.swing.plaf.ComponentUI;

/**
 * �Զ��嵥Ԫ��ui�ӿ�
 *
 * @return
 */
public interface GridUIProcessor extends Immutable {

	String MARK_STRING = "GridUIProcessor";
	int CURRENT_LEVEL = 1;

	/**
	 * �Զ���gridui, ����ʵ��һЩ�Զ���ĸ��ӻ���.
	 *
	 * @return �Զ���gridui
	 */
	ComponentUI appearanceForGrid(int paramInt);
}