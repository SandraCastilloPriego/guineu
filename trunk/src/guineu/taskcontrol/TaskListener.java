/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.taskcontrol;

/**
 *
 */
public interface TaskListener {
    
    public void taskStarted(Task task);

    public void taskFinished(Task task);
    
}
