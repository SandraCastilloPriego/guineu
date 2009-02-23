/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.taskcontrol;

import guineu.main.GuineuCore;
import java.util.logging.Logger;



/**
 * 
 */
public class TaskGroup implements TaskListener {

    public enum TaskGroupStatus {
        WAITING, RUNNING, ERROR, CANCELED, FINISHED
    };

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Task tasks[];
    private TaskListener taskListener;
    private TaskGroupListener taskGroupListener;
    private int finishedTasks = 0;

    private TaskGroupStatus status = TaskGroupStatus.WAITING;

    /**
     * @param tasks
     * @param taskController
     */
    public TaskGroup(Task task, TaskListener taskListener) {
        this(new Task[] { task }, taskListener, null);
    }
    
    /**
     * @param tasks
     * @param taskController
     */
    public TaskGroup(Task[] tasks, TaskListener taskListener) {
        this(tasks, taskListener, null);
    }

    /**
     * @param tasks
     * @param taskController
     */
    public TaskGroup(Task task, TaskListener taskListener,
            TaskGroupListener groupListener) {
        this(new Task[] { task }, taskListener, groupListener);
    }
    
    /**
     * @param tasks
     * @param taskController
     */
    public TaskGroup(Task[] tasks, TaskListener taskListener,
            TaskGroupListener groupListener) {
        this.tasks = tasks;
        this.taskListener = taskListener;
        this.taskGroupListener = groupListener;
    }

    public TaskGroupStatus getStatus() {
        return status;
    }

    public void taskStarted(Task task) {
        if (taskListener != null)
            taskListener.taskStarted(task);
    }

    public synchronized void taskFinished(Task task) {

        if (taskListener != null)
            taskListener.taskFinished(task);

        if (task.getStatus() == Task.TaskStatus.ERROR) {
            status = TaskGroupStatus.ERROR;
        } else if (task.getStatus() == Task.TaskStatus.CANCELED) {
            status = TaskGroupStatus.CANCELED;
        }

        finishedTasks++;

        if (finishedTasks == tasks.length) {

            if (status == TaskGroupStatus.RUNNING)
                status = TaskGroupStatus.FINISHED;

            if (taskGroupListener != null)
                taskGroupListener.taskGroupFinished(this);

        }

        logger.finest("Task group: finished " + finishedTasks + "/"
                + tasks.length + " tasks, status " + status);

    }

    /**
     */
    public void start() {

        logger.finest("Starting " + tasks.length + " task group");

        status = TaskGroupStatus.RUNNING;

        for (Task t : tasks) {
            GuineuCore.getTaskController().addTask(t, this);
        }

    }

    public String toString() {
        return "Task group: " + tasks;
    }

}
