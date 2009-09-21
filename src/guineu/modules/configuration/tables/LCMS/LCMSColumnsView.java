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
package guineu.modules.configuration.tables.LCMS;

import guineu.data.ParameterSet;
import guineu.data.datamodels.DatasetLCMSDataModel;
import guineu.data.impl.SimpleParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskGroup;
import guineu.taskcontrol.TaskGroupListener;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author scsandra
 */
public class LCMSColumnsView implements GuineuModule, TaskListener, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private SimpleParameterSet parameters;

    public void initModule() {
        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.CONFIGURATION, "LCMS Table View..",
                "TODO write description", KeyEvent.VK_L, this, null);
		parameters = new LCMSColumnsViewParameters();

    }

    public void taskStarted(Task task) {
        logger.info("Running LCMS Table View");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished LCMS Table View ");
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while LCMS Table View  .. ";
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {

        ExitCode exitCode = setupParameters();
        if (exitCode != ExitCode.OK) {
            return;
        }
       ((DesktopParameters) desktop.getParameterSet()).setViewLCMSParameters((LCMSColumnsViewParameters) parameters);
        runModule(null);
    }

    public ExitCode setupParameters() {
        try {         
            ParameterSetupDialog dialog = new ParameterSetupDialog("LCMS Table View parameters", parameters);
            dialog.setVisible(true);

            return dialog.getExitCode();
        } catch (Exception exception) {
            return ExitCode.CANCEL;
        }
    }

    public ParameterSet getParameterSet() {
        return parameters;
    }

    public void setParameters(ParameterSet parameterValues) {
        parameters = (LCMSColumnsViewParameters) parameterValues;
    }

    public String toString() {
        return "LCMS Table View";
    }

    public TaskGroup runModule(TaskGroupListener taskGroupListener) {

		/* if(selectedTable == null || selectedTable != ((DataInternalFrame)desktop.getSelectedFrame()).getTable()){
            selectedTable = ((DataInternalFrame)desktop.getSelectedFrame()).getTable();
        }*/
		
       // JInternalFrame[] frames = desktop.getInternalFrames();
       // for (int i = 0; i < frames.length; i++) {
            JTable table = ((DataInternalFrame) desktop.getSelectedFrame()).getTable();
            TableModel model = table.getModel();
            if(model.getClass().toString().contains("DatasetLCMSDataModel")){
                ((DatasetLCMSDataModel)model).setParameters();
            }
			table.setModel(model);
			table.createDefaultColumnsFromModel();
			table.revalidate();
       // }
        return null;
    }
}
