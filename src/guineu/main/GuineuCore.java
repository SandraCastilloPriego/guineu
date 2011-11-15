/*
 * Copyright 2007-2011 VTT Biotechnology
 *
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
package guineu.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import guineu.desktop.Desktop;
import guineu.desktop.impl.MainWindow;
import guineu.desktop.impl.helpsystem.HelpImpl;
import guineu.desktop.numberFormat.RTFormatter;
import guineu.desktop.numberFormat.RTFormatterType;
import guineu.desktop.preferences.ColumnsGCGCParameters;
import guineu.desktop.preferences.ColumnsLCMSParameters;
import guineu.modules.GuineuModule;
import guineu.modules.GuineuProcessingModule;
import guineu.modules.configuration.general.GeneralconfigurationParameters;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.TaskController;
import guineu.taskcontrol.impl.TaskControllerImpl;
import guineu.util.Range;
import guineu.util.dialogs.ExitCode;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

/**
 * This interface represents Guineu core modules - I/O, task controller and GUI.
 */
/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
public class GuineuCore implements Runnable {

        public static final File CONFIG_FILE = new File("conf/config.xml");
        public static final String STANDARD_RANGE = "standard_ranges";
        public static final String STANDARD_NAME = "standard_name";
        private static Logger logger = Logger.getLogger(GuineuCore.class.getName());
        private static GeneralconfigurationParameters preferences;
        private static ColumnsLCMSParameters LCMSColumns;
        private static ColumnsGCGCParameters GCGCColumns;
        private static TaskControllerImpl taskController;
        private static GuineuModule[] initializedModules;
        private static HelpImpl help;
        private static Hashtable<String, Range> standards;
        private static MainWindow desktop;

        public static void setStandard(String name, Range range) {
                standards.put(name, range);
        }

        public static Hashtable getStandards() {
                return standards;
        }

        /**
         * Returns a reference to local task controller.
         *
         * @return TaskController reference
         */
        public static TaskController getTaskController() {
                return taskController;
        }

        /**
         * Returns a reference to Desktop.
         */
        public static Desktop getDesktop() {
                return desktop;
        }

        /**
         * Returns an array of all initialized Guineu modules
         *
         * @return Array of all initialized Guineu modules
         */
        public static GuineuModule[] getAllModules() {
                return initializedModules;
        }

        /**
         *
         *
         * @return
         */
        public static HelpImpl getHelpImpl() {
                return help;
        }

        /**
         * Saves configuration and exits the application.
         *
         */
        public static ExitCode exitGuineu() {

                // If we have GUI, ask if use really wants to quit
                int selectedValue = JOptionPane.showInternalConfirmDialog(desktop.getMainFrame().getContentPane(),
                        "Are you sure you want to exit?", "Exiting...",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

                if (selectedValue != JOptionPane.YES_OPTION) {
                        return ExitCode.CANCEL;
                }

                desktop.getMainFrame().dispose();

                logger.info("Exiting Guineu");

                System.exit(0);

                return ExitCode.OK;

        }

        /**
         * Main method
         */
        public static void main(String args[]) {
                // create the GUI in the event-dispatching thread
                GuineuCore core = new GuineuCore();
                SwingUtilities.invokeLater(core);
                standards = new Hashtable<String, Range>();

        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {
                logger.info("Starting Guineu " + getGuineuVersion());

                logger.fine("Loading core classes..");

                // create instance of preferences
                preferences = new GeneralconfigurationParameters();

                LCMSColumns = new ColumnsLCMSParameters();

                GCGCColumns = new ColumnsGCGCParameters();

                // create instances of core modules

                // load configuration from XML
                taskController = new TaskControllerImpl();
                desktop = new MainWindow();
                help = new HelpImpl();

                logger.fine("Initializing core classes..");


                // Second, initialize desktop, because task controller needs to add
                // TaskProgressWindow to the desktop
                desktop.initModule();

                // Last, initialize task controller
                taskController.initModule();

                logger.fine("Loading modules");

                Vector<GuineuModule> moduleSet = new Vector<GuineuModule>();

                for (Class<?> moduleClass : GuineuModulesList.MODULES) {

                        try {

                                logger.finest("Loading module " + moduleClass.getName());

                                // create instance and init module
                                GuineuModule moduleInstance =  (GuineuModule) moduleClass.newInstance();

                                // add desktop menu icon
                                if (moduleInstance instanceof GuineuProcessingModule) {
                                        desktop.getMainMenu().addMenuItemForModule(
                                                (GuineuProcessingModule) moduleInstance);
                                }

                                // add to the module set
                                moduleSet.add(moduleInstance);

                        } catch (Throwable e) {
                                logger.log(Level.SEVERE,
                                        "Could not load module " + moduleClass, e);
                                e.printStackTrace();
                                continue;
                        }

                }

                GuineuCore.initializedModules = moduleSet.toArray(new GuineuModule[0]);

                if (CONFIG_FILE.canRead()) {
                        try {
                                loadConfiguration(CONFIG_FILE);
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }

                // register shutdown hook
                ShutDownHook shutDownHook = new ShutDownHook();
                Runtime.getRuntime().addShutdownHook(shutDownHook);

                // show the GUI
                logger.info("Showing main window");
                ((MainWindow) desktop).setVisible(true);

                // show the welcome message
                desktop.setStatusBarText("Welcome to Guineu!");
                preferences.setProxy();

        }

        public static void saveConfiguration(File file)
                throws ParserConfigurationException, TransformerException,
                FileNotFoundException {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

                Document configuration = dBuilder.newDocument();
                Element configRoot = configuration.createElement("configuration");
                configuration.appendChild(configRoot);

                Element standardElement = configuration.createElement("Standards");
                configRoot.appendChild(standardElement);
                Set<String> set = standards.keySet();
                Iterator<String> itr = set.iterator();
                while (itr.hasNext()) {
                        String str = itr.next();
                        Element standard = configuration.createElement("parameter");
                        standard.setAttribute("name", str);
                        Element range = configuration.createElement("item");
                        range.setTextContent(standards.get(str).toString());
                        standard.appendChild(range);
                        standardElement.appendChild(standard);
                }

                Element modulesElement = configuration.createElement("modules");
                configRoot.appendChild(modulesElement);

                // traverse modules
                for (GuineuModule module : getAllModules()) {

                        String className = module.getClass().getName();

                        Element moduleElement = configuration.createElement("module");
                        moduleElement.setAttribute("class", className);
                        modulesElement.appendChild(moduleElement);

                        Element paramElement = configuration.createElement("parameters");
                        moduleElement.appendChild(paramElement);

                        ParameterSet moduleParameters = module.getParameterSet();
                        if (moduleParameters != null) {
                                moduleParameters.saveValuesToXML(paramElement);
                        }

                }

                TransformerFactory transfac = TransformerFactory.newInstance();
                Transformer transformer = transfac.newTransformer();
                transformer.setOutputProperty(OutputKeys.METHOD, "xml");
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                transformer.setOutputProperty(
                        "{http://xml.apache.org/xslt}indent-amount", "4");

                StreamResult result = new StreamResult(new FileOutputStream(file));
                DOMSource source = new DOMSource(configuration);
                transformer.transform(source, result);

                logger.info("Saved configuration to file " + file);

        }

        public static void loadConfiguration(File file)
                throws ParserConfigurationException, SAXException, IOException,
                XPathExpressionException {

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document configuration = dBuilder.parse(file);

                XPathFactory factory = XPathFactory.newInstance();
                XPath xpath = factory.newXPath();

                logger.finest("Loading desktop configuration");

                XPathExpression expr = xpath.compile("//configuration/Standards");
                NodeList nodes = (NodeList) expr.evaluate(configuration,
                        XPathConstants.NODESET);
                if (nodes.getLength() == 1) {
                        Element preferencesElement = (Element) nodes.item(0);
                        NodeList list = preferencesElement.getElementsByTagName("parameter");
                        for (int i = 0; i < list.getLength(); i++) {
                                Element nextElement = (Element) list.item(i);
                                String paramName = nextElement.getAttribute("name");
                                try {
                                        String rangeString = nextElement.getTextContent();
                                        String[] range = rangeString.split(" - ");
                                        Range r = new Range(Double.valueOf(range[0]), Double.valueOf(range[1]));
                                        standards.put(paramName, r);
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }

                        }
                }


                logger.finest("Loading modules configuration");

                for (GuineuModule module : getAllModules()) {

                        String className = module.getClass().getName();
                        expr = xpath.compile("//configuration/modules/module[@class='" + className + "']/parameters");
                        nodes = (NodeList) expr.evaluate(configuration,
                                XPathConstants.NODESET);
                        if (nodes.getLength() != 1) {
                                continue;
                        }

                        Element moduleElement = (Element) nodes.item(0);

                        ParameterSet moduleParameters = module.getParameterSet();
                        if (moduleParameters != null) {
                                moduleParameters.loadValuesFromXML(moduleElement);
                        }
                }

                logger.info("Loaded configuration from file " + file);
        }

        // Number formatting functions
        public static NumberFormat getIntensityFormat() {
                return preferences.getParameter(GeneralconfigurationParameters.intensityFormat).getValue();
        }

        public static NumberFormat getMZFormat() {
                return new DecimalFormat("0.000");
        }

        public static NumberFormat getRTFormat() {
                return new RTFormatter(RTFormatterType.NumberInSec, "0.0");
        }

        public static String getGuineuVersion() {
                return GuineuVersion.GUINEU;
        }

        public static GeneralconfigurationParameters getPreferences() {
                return preferences;
        }

        public static void setPreferences(GeneralconfigurationParameters preferences2) {
                preferences = preferences2;
        }

        public static ColumnsLCMSParameters getLCMSColumnsParameters() {
                return LCMSColumns;
        }

        public static ColumnsGCGCParameters getGCGCColumnsParameters() {
                return GCGCColumns;
        }
}
