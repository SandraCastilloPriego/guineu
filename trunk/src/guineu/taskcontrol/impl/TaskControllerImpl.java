/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.taskcontrol.impl;

import guineu.desktop.impl.MainWindow;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.Task.TaskPriority;
import guineu.taskcontrol.Task.TaskStatus;
import guineu.taskcontrol.TaskController;
import guineu.taskcontrol.TaskListener;
import guineu.util.components.TaskProgressWindow;
import java.util.logging.Logger;



/**
 * Task controller implementation
 */
public class TaskControllerImpl implements TaskController, Runnable {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    // TODO: always create a worker thread for high priority tasks

    private final int TASKCONTROLLER_THREAD_SLEEP = 200;

    private Thread taskControllerThread;

    private WorkerThread[] workerThreads;

    private TaskQueue taskQueue;

    /**
     * 
     */
    public TaskControllerImpl(int numberOfThreads) {

        taskQueue = new TaskQueue();

        taskControllerThread = new Thread(this, "Task controller thread");
        taskControllerThread.setPriority(Thread.MIN_PRIORITY);
        taskControllerThread.start();

        workerThreads = new WorkerThread[numberOfThreads];
        for (int i = 0; i < numberOfThreads; i++) {
            workerThreads[i] = new WorkerThread(i + 1);
            workerThreads[i].start();
        }

    }

    public void addTask(Task task) {
        addTask(task, TaskPriority.NORMAL, null);
    }

    public void addTask(Task task, TaskPriority priority) {
        addTask(task, priority, null);
    }

    public void addTask(Task task, TaskListener listener) {
        addTask(task, TaskPriority.NORMAL, listener);
    }

    public void addTask(Task task, TaskPriority priority, TaskListener listener) {

        assert task != null;

        WrappedTask newQueueEntry = new WrappedTask(task, priority, listener);

        logger.finest("Adding task \"" + task.getTaskDescription()
                + "\" to the task controller queue");

        taskQueue.addWrappedTask(newQueueEntry);

        synchronized (this) {
            this.notifyAll();
        }

        /*
         * show the task list component
         */
        MainWindow mainWindow = (MainWindow) GuineuCore.getDesktop();
        if (mainWindow != null) {
            // JInternalFrame selectedFrame = desktop.getSelectedFrame();

            TaskProgressWindow tlc = mainWindow.getTaskList();
            tlc.setVisible(true);

            /*
             * if ((selectedFrame != null) && (desktop.getSelectedFrame() ==
             * tlc)) { try { selectedFrame.setSelected(true); } catch
             * (PropertyVetoException e) { // do nothing } }
             */
        }

    }

    /**
     * Task controller thread main method.
     * 
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while (true) {

            /* if the queue is empty, we can sleep */
            synchronized (this) {
                while (taskQueue.isEmpty()) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }

            WrappedTask[] queueSnapshot = taskQueue.getQueueSnapshot();

            // for each task, check if it's assigned and not canceled
            for (WrappedTask task : queueSnapshot) {

                if (!task.isAssigned()
                        && (task.getTask().getStatus() != TaskStatus.CANCELED)) {
                    // poll local threads

                    for (WorkerThread worker : workerThreads) {

                        if (worker.getCurrentTask() == null) {
                            logger.finest("Assigning task \""
                                    + task.getTask().getTaskDescription()
                                    + "\" to " + worker.toString());
                            worker.setCurrentTask(task);
                            break;
                        }

                    }

                    // TODO: poll remote nodes

                }

            }

            // check if all tasks are finished
            if (taskQueue.allTasksFinished()) {

                MainWindow mainWindow = (MainWindow) GuineuCore.getDesktop();

                if (mainWindow != null) {
                    TaskProgressWindow tlc = mainWindow.getTaskList();
                    tlc.setVisible(false);
                    taskQueue.clear();
                }

            } else {
                taskQueue.refresh();
            }

            try {
                Thread.sleep(TASKCONTROLLER_THREAD_SLEEP);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

    }

    public void setTaskPriority(Task task, TaskPriority priority) {
        WrappedTask wt = taskQueue.getWrappedTask(task);
        if (wt != null) {
            logger.finest("Setting priority of task \""
                    + task.getTaskDescription() + "\" to " + priority);
            wt.setPriority(priority);
            taskQueue.refresh();
        }
    }

    public TaskQueue getTaskQueue() {
        return taskQueue;
    }

    public Task getTask(int index) {
        WrappedTask wt = taskQueue.getWrappedTask(index);
        if (wt != null)
            return wt.getTask();
        else
            return null;
    }
    public boolean getTaskExists (){
    	if ( taskQueue.allTasksFinished()){
    		return false;
    	}else{
    		return true;
    	}
    }
    /**
     */
    public void initModule() {

    }

}
