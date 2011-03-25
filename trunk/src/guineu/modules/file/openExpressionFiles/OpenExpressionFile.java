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
package guineu.modules.file.openExpressionFiles;


import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class OpenExpressionFile implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private SimpleParameterSet parameters;

    public OpenExpressionFile() {
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.FILE, "Open Gene Expression Files..",
                "Open Gene Expresion Files (Assaig, Feature and Pheno)", KeyEvent.VK_L, this, null, null);
        parameters = new OpenExpressionParameters();
    }

    public void taskStarted(Task task) {
        logger.info("Running Open Gene Expression Files");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == TaskStatus.FINISHED) {
            logger.info("Finished Open Gene Expression Files" + ((OpenExpressionFileTask) task).getTaskDescription());
        }

        if (task.getStatus() == TaskStatus.ERROR) {

            String msg = "Error while Open Gene Expression Files on .. " + ((OpenExpressionFileTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }
   
    public ParameterSet getParameterSet() {
        return parameters;
    }   

    @Override
    public String toString() {
        return "Open Gene Expression Files";
    }

    public Task[] runModule() {
        Task tasks[] = new OpenExpressionFileTask[1];
        tasks[0] = new OpenExpressionFileTask(parameters);
        GuineuCore.getTaskController().addTasks(tasks);
        return tasks;
    }

    public void actionPerformed(ActionEvent arg0) {
        ExitCode exitCode = parameters.showSetupDialog();
        if (exitCode != ExitCode.OK) {
            return;
        }
        runModule();
    }
}
