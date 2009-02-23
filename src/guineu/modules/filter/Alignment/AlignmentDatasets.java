/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.modules.filter.Alignment;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskGroup;
import guineu.taskcontrol.TaskGroupListener;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class AlignmentDatasets implements GuineuModule, TaskListener, ActionListener{
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;
    private AlignmentParameters parameters;
    
    public void initModule() {
        this.desktop = GuineuCore.getDesktop();        
        desktop.addMenuItem(GuineuMenu.FILTER, "Combine datasets..",
                "TODO write description", KeyEvent.VK_A, this, null);       
        parameters = new AlignmentParameters();
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void taskStarted(Task task) {
        logger.info("Running alignment");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished alignment on "
                    + ((AlignmentDatasetsTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while alignment on .. "
                    + ((AlignmentDatasetsTask ) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        ExitCode exitCode = setupParameters();
        
        if (exitCode != ExitCode.OK)
            return;

        runModule(null);
    }

    public ExitCode setupParameters() {
        GetAlignmentParameters parametersDialog = new GetAlignmentParameters(parameters);
        parametersDialog.setVisible(true);
        return parametersDialog.getExitCode();
    }
   

    public void setParameters(ParameterSet parameterValues) {
        
    }
    
    public String toString() {
        return "Alingment";
    }

    public TaskGroup runModule( TaskGroupListener taskGroupListener) {
        
        // prepare a new group of tasks       
        Task tasks[] = new AlignmentDatasetsTask[1];       
        tasks[0] = new AlignmentDatasetsTask(desktop.getSelectedDataFiles(), desktop, parameters);

        TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

        // start the group
        newGroup.start();

        return newGroup;
       

    }
    

}
