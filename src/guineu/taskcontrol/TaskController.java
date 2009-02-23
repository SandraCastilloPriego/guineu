/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.taskcontrol;

import guineu.taskcontrol.Task.TaskPriority;


/**
 * 
 */
public interface TaskController {

    
    public void addTask(Task task);
        
    public void addTask(Task task, TaskPriority priority);

    public void addTask(Task task, TaskListener listener);

    public void addTask(Task task, TaskPriority priority, TaskListener listener);

    public void setTaskPriority(Task task, TaskPriority priority);
    
}
