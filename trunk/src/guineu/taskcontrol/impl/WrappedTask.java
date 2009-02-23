/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.taskcontrol.impl;

import guineu.taskcontrol.Task;
import guineu.taskcontrol.Task.TaskPriority;
import guineu.taskcontrol.TaskListener;
import java.util.Date;

/**
 * Wrapper class for Tasks that stores additional information
 */
class WrappedTask implements Comparable {

    private Task task;
    private Date addedTime;
    private TaskListener listener;
    private TaskPriority priority;
    private WorkerThread assignedTo;

    WrappedTask(Task task, TaskPriority priority, TaskListener listener) {
        addedTime = new Date();
        this.task = task;
        this.listener = listener;
        this.priority = priority;
    }

    /**
     * Tasks are sorted by priority order using this comparator method.
     * 
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object arg) {

        WrappedTask t = (WrappedTask) arg;
        int result;

        result = priority.compareTo(t.priority);
        if (result == 0)
            result = addedTime.compareTo(t.addedTime);
        return result;

    }

    /**
     * @return Returns the listener.
     */
    TaskListener getListener() {
        return listener;
    }

    /**
     * @return Returns the priority.
     */
    TaskPriority getPriority() {
        return priority;
    }

    /**
     * @param priority The priority to set.
     */
    void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    /**
     * @return Returns the assigned.
     */
    boolean isAssigned() {
        return assignedTo != null;
    }

    void assignTo(WorkerThread thread) {
        assignedTo = thread;
    }

    /**
     * @return Returns the task.
     */
    Task getTask() {
        return task;
    }

    public String toString() {
        return task.getTaskDescription();
    }

}
