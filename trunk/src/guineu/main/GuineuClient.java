/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.main;

import guineu.desktop.impl.MainWindow;
import guineu.taskcontrol.impl.TaskControllerImpl;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;




import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * Main client class
 */
public class GuineuClient extends GuineuCore implements Runnable {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Vector<GuineuModule> moduleSet;

    //private ProjectManager projectManager;

    // make GuineuClient a singleton
    private static GuineuClient client = new GuineuClient();

    private GuineuClient() {
    }

    public static GuineuClient getInstance() {
        return client;
    }

    /**
     * Main method
     */
    public static void main(String args[]) {

        // create the GUI in the event-dispatching thread
        SwingUtilities.invokeLater(client);

    }

    /**
     * @see java.lang.Runnable#run()
     */
    @SuppressWarnings("unchecked")
    public void run() {

        // load configuration from XML
        Document configuration = null;
        MainWindow desktop = null;
        try {
            SAXReader reader = new SAXReader();
            configuration = reader.read(CONFIG_FILE);
            Element configRoot = configuration.getRootElement();

            // get the configured number of computation nodes
            int numberOfNodes;

            Element nodes = configRoot.element(NODES_ELEMENT_NAME);
            String numberOfNodesConfigEntry = nodes.attributeValue(LOCAL_ATTRIBUTE_NAME);
            if (numberOfNodesConfigEntry != null) {
                numberOfNodes = Integer.parseInt(numberOfNodesConfigEntry);
            } else {
                numberOfNodes = Runtime.getRuntime().availableProcessors();
            }

            logger.info("Guineu starting with " + numberOfNodes + " computation nodes");

            logger.finer("Loading core classes");



            /*projectManager = new ProjectManagerImpl(ProjectType.xstream);
            projectManager.createTemporalProject();
            while (projectManager.getStatus() == ProjectStatus.Processing){
            // wait;
            Thread.sleep(500);
            }*/


            // create instances of core modules
            TaskControllerImpl taskController = new TaskControllerImpl(
                    numberOfNodes);
            //IOControllerImpl ioController=new IOControllerImpl();
            desktop = new MainWindow();

            // save static references to GuineuCore
            GuineuCore.taskController = taskController;
            //GineuCore.ioController = ioController;
            GuineuCore.desktop = desktop;

            logger.finer("Initializing core classes");

            taskController.initModule();
            //ioController.initModule();
            desktop.initModule();
            //projectManager.initModule();

            logger.finer("Loading modules");

            moduleSet = new Vector<GuineuModule>();

            Iterator<Element> modIter = configRoot.element(MODULES_ELEMENT_NAME).
                    elementIterator(MODULE_ELEMENT_NAME);

            while (modIter.hasNext()) {
                Element moduleElement = modIter.next();
                String className = moduleElement.attributeValue(CLASS_ATTRIBUTE_NAME);
                loadModule(className);
            }

            GuineuCore.initializedModules = moduleSet.toArray(new GuineuModule[0]);

            // load module configuration
            loadConfiguration(CONFIG_FILE);
//			GuineuCore.getCurrentProject().addProjectListener(desktop);



        } catch (Exception e) {
            logger.log(Level.SEVERE, "Could not parse configuration file " + CONFIG_FILE, e);
            System.exit(1);
        }

        // register the shutdown hook
        ShutDownHook shutDownHook = new ShutDownHook();
        Runtime.getRuntime().addShutdownHook(shutDownHook);

        // show the GUI
        logger.finest("Showing main window");
        desktop.setVisible(true);

        // show the welcome message
        desktop.setStatusBarText("Welcome to Guineu!");

    }

    /*public ProjectManager getProjectManager() {
    return projectManager;
    }*/
    public GuineuModule loadModule(String moduleClassName) {

        try {

            logger.finest("Loading module " + moduleClassName);

            // load the module class
            Class moduleClass = Class.forName(moduleClassName);

            // create instance
            GuineuModule moduleInstance = (GuineuModule) moduleClass.newInstance();

            // init module
            moduleInstance.initModule();

            // add to the module set
            moduleSet.add(moduleInstance);

            return moduleInstance;

        } catch (Exception e) {
            logger.log(Level.SEVERE,
                    "Could not load module " + moduleClassName, e);
            return null;
        }

    }

    /**
     * Shutdown hook - invoked on JRE shutdown. This method saves current
     * configuration to XML.
     * 
     */
    private class ShutDownHook extends Thread {

        public void start() {
            saveConfiguration(CONFIG_FILE);
        //GuineuProject project = GuineuCore.getCurrentProject();
        //if (project.getIsTemporal() == true) {
        //	projectManager.removeProjectDir(project.getLocation());
        //}
        }
    }
}
