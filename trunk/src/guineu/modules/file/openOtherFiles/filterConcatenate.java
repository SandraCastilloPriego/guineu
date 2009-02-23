/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.modules.file.openOtherFiles;

import guineu.data.ParameterSet;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.logging.Logger;


/**
 *
 * @author scsandra
 */
public class filterConcatenate implements GuineuModule, TaskListener, ActionListener {
   private Logger logger = Logger.getLogger(this.getClass().getName());
 

    private Desktop desktop;
    private String FilePath;
    
    public void initModule() {

        this.desktop = GuineuCore.getDesktop();        
        desktop.addMenuItem(GuineuMenu.FILTER, "concatenate File..",
                "TODO write description", KeyEvent.VK_L, this, null);

    }

    
    public void taskStarted(Task task) {
        logger.info("Running concatenate File");
    }

    public void taskFinished(Task task) {
        if (task.getStatus() == Task.TaskStatus.FINISHED) {
            logger.info("Finished concatenate File on "
                    + ((filterConcatenateTask) task).getTaskDescription());
        }

        if (task.getStatus() == Task.TaskStatus.ERROR) {

            String msg = "Error while concatenate File on .. "
                    + ((filterConcatenateTask) task).getErrorMessage();
            logger.severe(msg);
            desktop.displayErrorMessage(msg);

        }
    }

    public void actionPerformed(ActionEvent e) {
        runModule(null);
    }

    public ExitCode setupParameters() {
        DesktopParameters deskParameters = (DesktopParameters) GuineuCore
						.getDesktop().getParameterSet();
        String lastPath = deskParameters.getLastOpenProjectPath();
        if (lastPath == null)
                lastPath = "";
        File lastFilePath = new File(lastPath);
        DatasetOpenDialog dialog = new DatasetOpenDialog(lastFilePath);
        dialog.setVisible(true);
        try{
            this.FilePath = dialog.getCurrentDirectory();      
        }catch(Exception e){}
        return dialog.getExitCode();
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
        
    }
    
    public String toString() {
        return "concatenate File";
    }

    public TaskGroup runModule( TaskGroupListener taskGroupListener) {        
       
            Task tasks[] = new filterConcatenateTask[1];       
            tasks[0] = new filterConcatenateTask(desktop);

            TaskGroup newGroup = new TaskGroup(tasks, this, taskGroupListener);

            // start the group
            newGroup.start();

            return newGroup;
       

    }
    
  

}
