/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.filter.dataselection;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author scsandra
 */
public class DataSelection implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;

    public void initModule() {

        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.FILTER, "Selection Tools..",
                "TODO write description", KeyEvent.VK_S, this, null);

    }

    public void taskStarted(Task task) {
        logger.info("Running Selection Tools..");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Simplify Lipid Name on ");
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while Simplify Lipid Name on .. ";
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        SelectionBar bar = new SelectionBar(desktop);
        JFrame frame = desktop.getMainFrame();
        frame.add(bar, BorderLayout.EAST);
        frame.validate();
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {

    }

    public String toString() {
        return "Simplify Lipid Name Filter";
    }
}
