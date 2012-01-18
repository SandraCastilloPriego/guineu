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

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.impl.helpsystem.GuineuHelpSet;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModule;
import guineu.modules.GuineuModuleCategory;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.impl.TaskProgressWindow;
import guineu.util.ExceptionUtils;
import guineu.util.TextUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.help.HelpBroker;
import javax.imageio.ImageIO;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.border.EtchedBorder;
import org.w3c.dom.Document;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class is the main window of application
 *
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 * 
 */
public class MainWindow extends JFrame implements GuineuModule, Desktop,
        WindowListener {

        static final String aboutHelpID = "guineu/desktop/help/AboutGuineu.html";
        private JDesktopPane desktopPane;
        private JSplitPane split;
        private ItemSelector itemSelector;
        private TaskProgressWindow taskList;
        private String parametersFilePath;

        public TaskProgressWindow getTaskList() {
                return taskList;
        }
        private MainMenu menuBar;
        private Statusbar statusBar;

        public MainMenu getMainMenu() {
                return menuBar;
        }

        public void addInternalFrame(JInternalFrame frame) {
                try {
                        desktopPane.add(frame);
                        frame.setVisible(true);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void setParameteresPath(String path) {
                this.parametersFilePath = path;
        }

        public String getParameteresPath() {
                return this.parametersFilePath;
        }

        public void loadParameterPathFromXML(Element xmlElement) {
                NodeList list = xmlElement.getElementsByTagName("parameter");
                for (int i = 0; i < list.getLength(); i++) {
                        Element nextElement = (Element) list.item(i);
                        String paramName = nextElement.getAttribute("name");
                        if ("Path".equals(paramName)) {
                                try {
                                        String fileString = xmlElement.getTextContent();
                                        if (fileString.length() == 0) {
                                                return;
                                        }
                                        this.parametersFilePath = new File(fileString).getPath();
                                } catch (Exception e) {
                                }
                        }
                }
        }

        public void saveParameterPathToXML(Element xmlElement) {
                Document parentDocument = xmlElement.getOwnerDocument();
                Element paramElement = parentDocument.createElement("parameter");
                paramElement.setAttribute("name", "Path");               
                if (this.parametersFilePath == null) {
                        return;
                }
                paramElement.setTextContent(this.parametersFilePath);
                xmlElement.appendChild(paramElement);
        }

        /**
         * This method returns the desktop
         */
        public JDesktopPane getDesktopPane() {
                return desktopPane;
        }

        /**
         * WindowListener interface implementation
         */
        public void windowOpened(WindowEvent e) {
        }

        public void windowClosing(WindowEvent e) {
                GuineuCore.exitGuineu();
        }

        public void windowClosed(WindowEvent e) {
        }

        public void windowIconified(WindowEvent e) {
        }

        public void windowDeiconified(WindowEvent e) {
        }

        public void windowActivated(WindowEvent e) {
        }

        public void windowDeactivated(WindowEvent e) {
        }

        public void setStatusBarText(String text) {
                setStatusBarText(text, Color.black);
        }

        /**
         */
        public void displayMessage(String msg) {
                displayMessage("Message", msg, JOptionPane.INFORMATION_MESSAGE);
        }

        /**
         */
        public void displayMessage(String title, String msg) {
                displayMessage(title, msg, JOptionPane.INFORMATION_MESSAGE);
        }

        public void displayErrorMessage(String msg) {
                displayMessage("Error", msg);
        }

        public void displayErrorMessage(String title, String msg) {
                displayMessage(title, msg, JOptionPane.ERROR_MESSAGE);
        }

        public void displayMessage(String title, String msg, int type) {
                String wrappedMsg = TextUtils.wrapText(msg, 80);
                JOptionPane.showMessageDialog(this, wrappedMsg, title, type);
        }

        public void addMenuItem(GuineuModuleCategory parentMenu, JMenuItem newItem) {
                menuBar.addMenuItem(parentMenu, newItem);
        }

        /**
         */
        public void initModule() {

                SwingParameters.initSwingParameters();

                try {
                        BufferedImage GuineuIcon = ImageIO.read(new File(
                                "icons/GuineuIcon.png"));
                        setIconImage(GuineuIcon);
                } catch (IOException e) {
                        e.printStackTrace();
                }

                // Initialize item selector
                itemSelector = new ItemSelector(this);


                // Place objects on main window
                desktopPane = new JDesktopPane();

                split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, itemSelector,
                        desktopPane);

                desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

                desktopPane.setBorder(new EtchedBorder(EtchedBorder.RAISED));
                //desktopPane.setBackground(new Color(251, 161, 82));
                Container c = getContentPane();
                c.setLayout(new BorderLayout());
                c.add(split, BorderLayout.CENTER);

                statusBar = new Statusbar();
                c.add(statusBar, BorderLayout.SOUTH);

                // Construct menu
                menuBar = new MainMenu();
                setJMenuBar(menuBar);

                // Initialize window listener for responding to user events
                addWindowListener(this);

                pack();

                // TODO: check screen size?
                setBounds(0, 0, 1000, 700);
                setLocationRelativeTo(null);

                // Application wants to control closing by itself
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                updateTitle();

                setTitle("Guineu");
        }

        void updateTitle() {
                setTitle("Guineu " + GuineuCore.getGuineuVersion());
        }

        public JFrame getMainFrame() {
                return this;
        }

        public JInternalFrame getSelectedFrame() {
                return desktopPane.getSelectedFrame();
        }

        public JInternalFrame[] getInternalFrames() {
                return desktopPane.getAllFrames();
        }

        public void setStatusBarText(String text, Color textColor) {
                statusBar.setStatusText(text, textColor);
        }

        public ParameterSet getParameterSet() {
                // return parameters;
                return null;
        }

        public void setParameters(ParameterSet parameterValues) {
                // this.parameters = (DesktopParameters) parameterValues;
        }

        public ItemSelector getItemSelector() {
                return itemSelector;
        }

        public Dataset[] getSelectedDataFiles() {
                return this.itemSelector.getSelectedDatasets();
        }

        /*public Vector[] getSelectedExperiments() {
        return this.itemSelector.getSelectedExperiments();
        }*/
        public void AddNewFile(Dataset dataset) {
                this.itemSelector.addNewFile(dataset);
        }

        public void removeData(Dataset file) {
                this.itemSelector.removeData(file);
        }

        public void displayException(Exception e) {
                displayErrorMessage(ExceptionUtils.exceptionToString(e));
        }

        public void showAboutDialog() {
                GuineuHelpSet hs = GuineuCore.getHelpImpl().getHelpSet();
                if (hs == null) {
                        return;
                }

                HelpBroker hb = hs.createHelpBroker();
                hs.setHomeID(aboutHelpID);

                hb.setDisplayed(true);
        }
}
