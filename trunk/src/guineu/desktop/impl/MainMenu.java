/*
 * Copyright 2007-2010 VTT Biotechnology
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
import ca.guydavis.swing.desktop.CascadingWindowPositioner;
import ca.guydavis.swing.desktop.JWindowsMenu;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import javax.swing.ImageIcon;

/**
 * 
 */
class MainMenu extends JMenuBar implements ActionListener {

    private JMenu fileMenu, /*msmsMenu, */ myllyMenu, myllyToolsMenu,
            lcmsIdentificationSubMenu, gcgcIdentificationSubMenu, normalizationMenu,
            identificationFilterMenu, databaseMenu, filterMenu, alignmentMenu,
            identificationMenu, helpMenu, statisticsMenu, configurationMenu,
            reportMenu;
    private JWindowsMenu windowsMenu;
    private JMenuItem hlpAbout;

    MainMenu() {

        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        add(fileMenu);

        configurationMenu = new JMenu("Configuration");
        configurationMenu.setMnemonic(KeyEvent.VK_C);
        add(configurationMenu);

        databaseMenu = new JMenu("Database");
        databaseMenu.setMnemonic(KeyEvent.VK_D);
        add(databaseMenu);

        filterMenu = new JMenu("Filter");
        filterMenu.setMnemonic(KeyEvent.VK_L);
        this.add(filterMenu);

        alignmentMenu = new JMenu("Alignment");
        alignmentMenu.setMnemonic(KeyEvent.VK_A);
        filterMenu.add(alignmentMenu);

        identificationMenu = new JMenu("Identification");
        identificationMenu.setMnemonic(KeyEvent.VK_I);
        this.add(identificationMenu);

        lcmsIdentificationSubMenu = new JMenu("LC-MS");
        lcmsIdentificationSubMenu.setMnemonic(KeyEvent.VK_L);
        identificationMenu.add(lcmsIdentificationSubMenu);

        gcgcIdentificationSubMenu = new JMenu("GCxGC-MS");
        gcgcIdentificationSubMenu.setMnemonic(KeyEvent.VK_G);
        identificationMenu.add(gcgcIdentificationSubMenu);

        identificationFilterMenu = new JMenu("Identification Filters");
        identificationFilterMenu.setMnemonic(KeyEvent.VK_I);
        lcmsIdentificationSubMenu.add(identificationFilterMenu);


        normalizationMenu = new JMenu("Normalization");
        normalizationMenu.setMnemonic(KeyEvent.VK_N);
        lcmsIdentificationSubMenu.add(normalizationMenu);

        statisticsMenu = new JMenu("Statistics");
        statisticsMenu.setMnemonic(KeyEvent.VK_S);
        this.add(statisticsMenu);

        reportMenu = new JMenu("LC-MS Reports");
        reportMenu.setMnemonic(KeyEvent.VK_R);
        this.add(reportMenu);

        /* msmsMenu = new JMenu("MS/MS Filters");
        msmsMenu.setMnemonic(KeyEvent.VK_M);
        add(msmsMenu);*/

        myllyMenu = new JMenu("GCxGC-MS");
        myllyMenu.setMnemonic(KeyEvent.VK_G);
        this.add(myllyMenu);

        myllyToolsMenu = new JMenu("Tools");
        myllyToolsMenu.setMnemonic(KeyEvent.VK_G);
        myllyMenu.add(myllyToolsMenu);
        myllyMenu.addSeparator();

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

        hlpAbout = addMenuItem(GuineuMenu.HELPSYSTEM, "About Guineu..",
                "About Guineu..", KeyEvent.VK_A, this,
                null, null);
    }

    public void addMenuItem(GuineuMenu parentMenu, JMenuItem newItem) {
        switch (parentMenu) {
            case FILE:
                fileMenu.add(newItem);
                break;
            case CONFIGURATION:
                configurationMenu.add(newItem);
                break;
            case DATABASE:
                databaseMenu.add(newItem);
                break;
            case FILTER:
                filterMenu.add(newItem);
                break;
            case ALIGNMENT:
                alignmentMenu.add(newItem);
                break;
            case IDENTIFICATION:
                identificationMenu.add(newItem);
                break;
            case LCMSIDENTIFICATIONSUBMENU:
                lcmsIdentificationSubMenu.add(newItem);
                break;
            case GCGCIDENTIFICATIONSUBMENU:
                gcgcIdentificationSubMenu.add(newItem);
                break;
            case IDENTIFICATIONFILTERS:
                identificationFilterMenu.add(newItem);
                break;
            case NORMALIZATION:
                normalizationMenu.add(newItem);
                break;
            case STATISTICS:
                statisticsMenu.add(newItem);
                break;
            case REPORT:
                reportMenu.add(newItem);
                break;
            /* case MSMS:
            msmsMenu.add(newItem);
            break;*/
            case MYLLY:
                myllyMenu.add(newItem);
                break;
            case MYLLYTOOLS:
                myllyToolsMenu.add(newItem);
                break;
            case HELPSYSTEM:
                helpMenu.add(newItem);
                break;
        }
    }

    public JMenuItem addMenuItem(GuineuMenu parentMenu, String text,
            String toolTip, int mnemonic,
            ActionListener listener, String actionCommand, String icon) {

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
        }

        if (icon != null) {
            newItem.setIcon(new ImageIcon(icon));
        }
        addMenuItem(parentMenu, newItem);
        return newItem;

    }

    public void addMenuSeparator(GuineuMenu parentMenu) {
        switch (parentMenu) {
            case FILE:
                fileMenu.addSeparator();
                break;
            case CONFIGURATION:
                configurationMenu.addSeparator();
                break;
            case DATABASE:
                databaseMenu.addSeparator();
                break;
            case FILTER:
                filterMenu.addSeparator();
                break;
            case ALIGNMENT:
                alignmentMenu.addSeparator();
                break;
            case IDENTIFICATION:
                identificationMenu.addSeparator();
                break;
            case LCMSIDENTIFICATIONSUBMENU:
                lcmsIdentificationSubMenu.addSeparator();
                break;
            case GCGCIDENTIFICATIONSUBMENU:
                gcgcIdentificationSubMenu.addSeparator();
                break;
            case IDENTIFICATIONFILTERS:
                identificationFilterMenu.addSeparator();
                break;
            case NORMALIZATION:
                normalizationMenu.addSeparator();
                break;
            case STATISTICS:
                statisticsMenu.addSeparator();
                break;
            case REPORT:
                reportMenu.addSeparator();
                break;
            /*  case MSMS:
            msmsMenu.addSeparator();
            break;*/
            case MYLLY:
                myllyMenu.addSeparator();
                break;
            case MYLLYTOOLS:
                myllyToolsMenu.addSeparator();
                break;
            case HELPSYSTEM:
                helpMenu.addSeparator();
                break;

        }
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == hlpAbout) {
            MainWindow mainWindow = (MainWindow) GuineuCore.getDesktop();
            mainWindow.showAboutDialog();
        }
    }
}
