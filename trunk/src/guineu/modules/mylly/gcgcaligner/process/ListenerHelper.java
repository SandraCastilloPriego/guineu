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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.SwingUtilities;

public class ListenerHelper implements Cloneable
{

	private List<StatusChangeListener> listeners;

	public ListenerHelper()
	{
		listeners = new CopyOnWriteArrayList<StatusChangeListener>();
	}

	public void addListener(StatusChangeListener listener)
	{
		if (listener != null)
		{
			listeners.add(listener);
		}
	}

	public boolean removeListener(StatusChangeListener listener)
	{
		if (listener != null)
		{
			return listeners.remove(listener);
		}
		return false;
	}

	public void fireStatusChange(final StatusChange change)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				for (StatusChangeListener l : listeners)
				{
					l.statusChanged(change);
				}
			}
		});
	}

	public ListenerHelper clone()
	{
		ListenerHelper clone = new ListenerHelper();
		clone.listeners.addAll(listeners);
		return clone;
	}

	public List<StatusChangeListener> getListeners()
	{
		return new ArrayList<StatusChangeListener>(listeners);
	}

}
