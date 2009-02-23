/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.taskcontrol;


public interface Task extends Runnable {

    /**
     * WAITING - task is ready and waiting to start
     * PROCESSING - task is running
     * FINISHED - task finished succesfully, results can be obtained by getResult()
     * CANCELED - task was canceled by user
     * ERROR - task finished with error, error message can be obtained by getErrorMessage()
     *
     */
    public static enum TaskStatus {
        WAITING, PROCESSING, FINISHED, CANCELED, ERROR
    };

    public static enum TaskPriority {
        HIGH, NORMAL, LOW
    };

    public String getTaskDescription();

    public double getFinishedPercentage();

    public TaskStatus getStatus();

    public String getErrorMessage();

    /**
     * Cancel a running task by user request.
     * This method must clean up everything and return to 
     * a state prior to starting the task.
     *
     */
    public void cancel();

}
