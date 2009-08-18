/*
 * Copyright 2007-2008 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.desktop.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import ca.guydavis.swing.desktop.CascadingWindowPositioner;
import ca.guydavis.swing.desktop.JWindowsMenu;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.util.GUIUtils;

/**
 * 
 */
class MainMenu extends JMenuBar implements ActionListener {

	private JMenu fileMenu,  msmsMenu,  databaseMenu,  filterMenu,  IdentificationMenu,  helpMenu,  statisticsMenu;
	private JWindowsMenu windowsMenu;
	private JMenuItem hlpAbout;

	MainMenu() {

		fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		add(fileMenu);

		databaseMenu = new JMenu("Database");
		databaseMenu.setMnemonic(KeyEvent.VK_D);
		add(databaseMenu);

		filterMenu = new JMenu("Filter");
		filterMenu.setMnemonic(KeyEvent.VK_L);
		this.add(filterMenu);

		IdentificationMenu = new JMenu("Identification");
		IdentificationMenu.setMnemonic(KeyEvent.VK_C);
		this.add(IdentificationMenu);

		statisticsMenu = new JMenu("Statistics");
		statisticsMenu.setMnemonic(KeyEvent.VK_S);
		this.add(statisticsMenu);

		msmsMenu = new JMenu("MS/MS Filters");
		msmsMenu.setMnemonic(KeyEvent.VK_F);
		add(msmsMenu);


		JDesktopPane mainDesktopPane = ((MainWindow) GuineuCore.getDesktop()).getDesktopPane();
		windowsMenu = new JWindowsMenu(mainDesktopPane);
		CascadingWindowPositioner positioner = new CascadingWindowPositioner(
				mainDesktopPane);
		windowsMenu.setWindowPositioner(positioner);
		windowsMenu.setMnemonic(KeyEvent.VK_W);
		this.add(windowsMenu);

		helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);
		this.add(helpMenu);

		hlpAbout = GUIUtils.addMenuItem(helpMenu, "About Guineu...", this,
				KeyEvent.VK_A);
	}

	public void addMenuItem(GuineuMenu parentMenu, JMenuItem newItem) {
		switch (parentMenu) {
			case FILE:
				fileMenu.add(newItem);
				break;
			case DATABASE:
				databaseMenu.add(newItem);
				break;
			case FILTER:
				filterMenu.add(newItem);
				break;
			case IDENTIFICATION:
				IdentificationMenu.add(newItem);
				break;
			case STATISTICS:
				statisticsMenu.add(newItem);
				break;
			case MSMS:
				msmsMenu.add(newItem);
				break;
			case HELP:
				helpMenu.add(newItem);
				break;
		}
	}

	public JMenuItem addMenuItem(GuineuMenu parentMenu, String text,
			String toolTip, int mnemonic, ActionListener listener,
			String actionCommand) {

		JMenuItem newItem = new JMenuItem(text);
		if (listener != null) {
			newItem.addActionListener(listener);
		}
		if (actionCommand != null) {
			newItem.setActionCommand(actionCommand);
		}
		if (toolTip != null) {
			newItem.setToolTipText(toolTip);
		}
		if (mnemonic > 0) {
			newItem.setMnemonic(mnemonic);
			newItem.setAccelerator(KeyStroke.getKeyStroke(mnemonic,
					ActionEvent.CTRL_MASK));
		}
		addMenuItem(parentMenu, newItem);
		return newItem;

	}

	public void addMenuSeparator(GuineuMenu parentMenu) {
		switch (parentMenu) {
			case FILE:
				fileMenu.addSeparator();
				break;
			case DATABASE:
				databaseMenu.addSeparator();
				break;
			case FILTER:
				filterMenu.addSeparator();
				break;
			case IDENTIFICATION:
				IdentificationMenu.addSeparator();
				break;
			case STATISTICS:
				statisticsMenu.addSeparator();
				break;
			case MSMS:
				msmsMenu.addSeparator();
				break;
			case HELP:
				helpMenu.addSeparator();
				break;

		}
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {

		Object src = e.getSource();

		// Help->About
		if (src == hlpAbout) {
			AboutDialog dialog = new AboutDialog();
			dialog.setVisible(true);
		}

	}
}
