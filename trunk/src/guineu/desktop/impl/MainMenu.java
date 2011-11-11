/*
 * Copyright 2007-2011 VTT Biotechnology
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
import java.util.logging.Level;
import javax.swing.JDesktopPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import ca.guydavis.swing.desktop.CascadingWindowPositioner;
import ca.guydavis.swing.desktop.JWindowsMenu;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.util.dialogs.ExitCode;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
public class MainMenu extends JMenuBar implements ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private JMenu fileMenu, /*msmsMenu, */ myllyMenu, myllyToolsMenu,
                lcmsIdentificationSubMenu, gcgcIdentificationSubMenu, normalizationMenu,
                identificationFilterMenu, databaseMenu, filterMenu, alignmentMenu,
                identificationMenu, helpMenu, statisticsMenu, configurationMenu/*,
                reportMenu*/, visualizationMenu;
        private JWindowsMenu windowsMenu;
        private JMenuItem showAbout;
        private Map<JMenuItem, GuineuProcessingModule> moduleMenuItems = new Hashtable<JMenuItem, GuineuProcessingModule>();

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

                alignmentMenu = new JMenu("LC-MS Alignment");
                alignmentMenu.setIcon(new ImageIcon("icons/alignment.png"));
                alignmentMenu.setMnemonic(KeyEvent.VK_A);
                filterMenu.add(alignmentMenu);

                identificationMenu = new JMenu("Identification");
                identificationMenu.setMnemonic(KeyEvent.VK_I);
                this.add(identificationMenu);

                lcmsIdentificationSubMenu = new JMenu("LC-MS");
                lcmsIdentificationSubMenu.setMnemonic(KeyEvent.VK_L);
                lcmsIdentificationSubMenu.setIcon(new ImageIcon("icons/lcmsident.png"));
                identificationMenu.add(lcmsIdentificationSubMenu);

                gcgcIdentificationSubMenu = new JMenu("GCxGC-MS");
                gcgcIdentificationSubMenu.setMnemonic(KeyEvent.VK_G);
                gcgcIdentificationSubMenu.setIcon(new ImageIcon("icons/gcgcident.png"));
                identificationMenu.add(gcgcIdentificationSubMenu);

                identificationFilterMenu = new JMenu("Identification Filters");
                identificationFilterMenu.setMnemonic(KeyEvent.VK_I);
                //  lcmsIdentificationSubMenu.add(identificationFilterMenu);


                normalizationMenu = new JMenu("Normalization");
                normalizationMenu.setMnemonic(KeyEvent.VK_N);
                normalizationMenu.setIcon(new ImageIcon("icons/linearnorm.png"));
                identificationMenu.add(normalizationMenu);

                statisticsMenu = new JMenu("Data analysis");
                statisticsMenu.setMnemonic(KeyEvent.VK_S);
                this.add(statisticsMenu);

                visualizationMenu = new JMenu("Visualization");
                visualizationMenu.setMnemonic(KeyEvent.VK_V);
                this.add(visualizationMenu);

                /*reportMenu = new JMenu("LC-MS Reports");
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
                myllyToolsMenu.setIcon(new ImageIcon("icons/tools.png"));
                myllyMenu.add(myllyToolsMenu);
                myllyMenu.addSeparator();


                JDesktopPane mainDesktopPane = ((MainWindow) GuineuCore.getDesktop()).getDesktopPane();
                windowsMenu = new JWindowsMenu(mainDesktopPane);
                CascadingWindowPositioner positioner = new CascadingWindowPositioner(
                        mainDesktopPane);
                windowsMenu.setWindowPositioner(positioner);
                windowsMenu.setMnemonic(KeyEvent.VK_W);
                this.add(windowsMenu);
                

                /*
                 * Help menu
                 */
                helpMenu = new JMenu("Help");
                helpMenu.setMnemonic(KeyEvent.VK_H);               
                this.add(helpMenu);

                showAbout = new JMenuItem("About Guineu ...");
                showAbout.addActionListener(this);
                showAbout.setIcon(new ImageIcon("icons/help.png"));
                addMenuItem(GuineuModuleCategory.HELPSYSTEM, showAbout);

               
        }

        public synchronized void addMenuItem(GuineuModuleCategory parentMenu,
                JMenuItem newItem) {
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
                        case FILTERING:
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
                        case DATAANALYSIS:
                                statisticsMenu.add(newItem);
                                break;
                        case VISUALIZATION:
                                visualizationMenu.add(newItem);
                                break;
                        case REPORT:
                                //   reportMenu.add(newItem);
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

        /*public JMenuItem addMenuItem(GuineuMenu parentMenu, String text,
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
        
        }*/
        public void addMenuSeparator(GuineuModuleCategory parentMenu) {
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
                        case FILTERING:
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
                        case DATAANALYSIS:
                                statisticsMenu.addSeparator();
                                break;
                        case VISUALIZATION:
                                visualizationMenu.addSeparator();
                                break;
                        case REPORT:
                                //   reportMenu.addSeparator();
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

                GuineuProcessingModule module = moduleMenuItems.get(src);
                if (module != null) {
                        ParameterSet moduleParameters = module.getParameterSet();

                        if (moduleParameters == null) {
                                logger.log(Level.FINEST, "Starting module {0} with no parameters", module);
                                module.runModule(null);
                                return;
                        }

                        boolean allParametersOK = true;
                        LinkedList<String> errorMessages = new LinkedList<String>();

                        if (!allParametersOK) {
                                StringBuilder message = new StringBuilder();
                                for (String m : errorMessages) {
                                        message.append(m);
                                        message.append("\n");
                                }
                                GuineuCore.getDesktop().displayMessage(message.toString());
                                return;
                        }

                        logger.log(Level.FINEST, "Setting parameters for module {0}", module);
                        ExitCode exitCode = moduleParameters.showSetupDialog();
                        if (exitCode == ExitCode.OK) {
                                ParameterSet parametersCopy = moduleParameters.clone();
                                logger.log(Level.FINEST, "Starting module {0} with parameters {1}", new Object[]{module, parametersCopy});
                                module.runModule(parametersCopy);
                        }
                        return;
                }

                if (src == showAbout) {
                        MainWindow mainWindow = (MainWindow) GuineuCore.getDesktop();
                        mainWindow.showAboutDialog();
                }
        }

        public void addMenuItemForModule(GuineuProcessingModule module) {

                GuineuModuleCategory parentMenu = module.getModuleCategory();
                String menuItemText = module.toString();
                String menuItemIcon = module.getIcon();
                boolean separator = module.setSeparator();

                JMenuItem newItem = new JMenuItem(menuItemText);
                if (menuItemIcon != null) {
                        newItem.setIcon(new ImageIcon(menuItemIcon));
                }
                newItem.addActionListener(this);

                moduleMenuItems.put(newItem, module);

                addMenuItem(parentMenu, newItem);

                if (separator) {
                        this.addMenuSeparator(parentMenu);
                }

        }
}
