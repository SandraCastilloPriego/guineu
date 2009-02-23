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


package guineu.taskcontrol.impl;

import guineu.main.GuineuCore;
import guineu.taskcontrol.TaskListener;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Task controller worker thread
 */
class WorkerThread extends Thread {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    
    private WrappedTask currentTask;

    WorkerThread(int workerNumber) {
        super("Worker thread #" + workerNumber);
    }

    /**
     * @return Returns the currentTask.
     */
    WrappedTask getCurrentTask() {
        return currentTask;
    }

    /**
     * @param currentTask The currentTask to set.
     */
    void setCurrentTask(WrappedTask newTask) {
        assert currentTask == null;
        currentTask = newTask;
        newTask.assignTo(this);
        synchronized (this) {
            notify();
        }
    }

    /**
     * @see java.lang.Runnable#run()
     */
    public void run() {

        while (true) {

            while (currentTask == null) {
                try {
                    synchronized (this) {
                        wait();
                    }
                } catch (InterruptedException e) {
                    // nothing happens
                }
            }

            try {
                
                TaskListener listener = currentTask.getListener();
                
                if (listener != null)
                    listener.taskStarted(currentTask.getTask());
                
                currentTask.getTask().run();
                
                if (listener != null)
                    listener.taskFinished(currentTask.getTask());
                
            } catch (Throwable e) {

                // this should never happen!

                logger.log(Level.SEVERE, "Unhandled exception " + e + " while processing task "
                        + currentTask, e);

                if (GuineuCore.getDesktop() != null) {
                    
                    String errorMessage = "Unhandled exception while processing task "
                        + currentTask + ": " + e;

                    GuineuCore.getDesktop().displayErrorMessage(errorMessage);
                }

            }

            /* discard the task, so that garbage collecter can collect it */
            currentTask = null;

        }

    }
    
    public String toString() {
        return this.getName();
    }

}
