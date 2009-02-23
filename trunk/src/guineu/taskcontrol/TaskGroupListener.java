/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.taskcontrol;

public interface TaskGroupListener {

    public void taskGroupStarted(TaskGroup group);
    
    public void taskGroupFinished(TaskGroup group);

}
