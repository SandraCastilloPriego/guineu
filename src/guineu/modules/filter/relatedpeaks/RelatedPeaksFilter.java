/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.modules.filter.relatedpeaks;

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskGroup;
import guineu.taskcontrol.TaskGroupListener;
import guineu.taskcontrol.TaskListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class RelatedPeaksFilter implements GuineuModule, TaskListener, ActionListener {
   private Logger logger = Logger.getLogger(this.getClass().getName());
 

    private Desktop desktop;     
    
    public void initModule() {

        this.desktop = GuineuCore.getDesktop();         
        desktop.addMenuItem(GuineuMenu.FILTER, "Related Peaks Filter..",
                "TODO write description", KeyEvent.VK_R, this, null);

    }

    
    public void taskStarted(Task task) {
        logger.info("Running Related Peaks filter");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished Related Peaks filter on "
                    + ((RelatedPeaksTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while Related Peaks filter on .. "
                    + ((RelatedPeaksTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
               runModule(null);
    }   

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
        
    }
    
    public String toString() {
        return "Related Peaks filter";
    }

    public TaskGroup runModule( TaskGroupListener taskGroupListener) {
        
        // prepare a new group of tasks
        Dataset[] datasets = desktop.getSelectedDataFiles();
        Task tasks[] = new RelatedPeaksTask[datasets.length];       
        for(int i = 0; i < datasets.length; i++){
            tasks[i] = new RelatedPeaksTask(datasets[i], desktop);
        }
        TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

        // start the group
        newGroup.start();

        return newGroup;
       

    }
    
  

}
