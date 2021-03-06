/*
 * Copyright 2007-2013 VTT Biotechnology
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
/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */

package guineu.taskcontrol;

import java.util.EventObject;

/**
 * A class for relaying Changes in TaskStatus to listeners
 */
public class TaskEvent extends EventObject {
	
	private TaskStatus status;

	/**
	 * Creates a new TaskEvent
	 * 
	 * @param source
	 *            The Task which caused this event.
	 */
	public TaskEvent(Task source) {
		super(source);
		this.status = source.getStatus();
	}

	/**
	 * Creates a new TaskEvent
	 * 
	 * @param source
	 *            The Task which caused this event.
	 * @param status
	 *            The new TaskStatus of the Task.
	 */
	public TaskEvent(Task source, TaskStatus status) {
		super(source);
		this.status = status;
	}

	/**
	 * Get the source of this TaskEvent
	 * 
	 * @return The Task which caused this event
	 */
	public Task getSource() {
		return (Task) this.source;
	}

	/**
	 * Get the new TaskStatus of the source Task
	 * 
	 * @return The new TaskStatus
	 */
	public TaskStatus getStatus() {
		return status;
	}

}
