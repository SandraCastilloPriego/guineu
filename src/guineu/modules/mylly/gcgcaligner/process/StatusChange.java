/*
    Copyright 2006-2007 VTT Biotechnology

    This file is part of MYLLY.

    MYLLY is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    MYLLY is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MYLLY; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package guineu.modules.mylly.gcgcaligner.process;

/**
 * Used to report changes in task status and completion rates
 * @author jmjarkko
 *
 */
public class StatusChange
{
	private final static int DEF_TASKS = 0;
	private final static int DEF_DONE = 0;
	
	public static enum STATUS {NONE, NOT_STARTED, CANCELED, RUNNING, FINISHED};

	private STATUS _currentStatus;
	private STATUS _oldStatus;
	private Object _source;
	private String _name;
	
	private int _totalTaskCount;
	private int _doneTaskCount;
	
	public StatusChange(Object source, STATUS oldStatus, STATUS currentStatus, String name)
	{
		this(source, oldStatus, currentStatus, name, DEF_DONE, DEF_TASKS);
	}
	
	public StatusChange(Object source, 
			STATUS oldStatus, 
			STATUS currentStatus, 
			String name, 
			int tasksDone, int tasks)
	{
		_source = source;
		_oldStatus = oldStatus;
		_currentStatus = currentStatus;
		_name = name;
		_doneTaskCount = tasksDone;
		_totalTaskCount = tasks;
	}

	
	public Object getSource()
	{
		return _source;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public STATUS getNewStatus()
	{
		return _currentStatus;
	}
	
	public STATUS getOldStatus()
	{
		return _oldStatus;
	}
	
	public int getTotalTaskCount()
	{
		return _totalTaskCount;
	}
	
	public int getDoneTaskCount()
	{
		return _doneTaskCount;
	}
	
	public double completionState()
	{
		return ((double) _doneTaskCount) / _totalTaskCount;
	}
}
