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

import java.util.concurrent.CancellationException;

public abstract class AbstractStatusReportingProcess<T,Y> implements StatusReportingProcess<T, Y>
{
	protected volatile StatusChange.STATUS currentStatus;
	protected ListenerHelper lh;

	protected Object cancelSync;

	public AbstractStatusReportingProcess()
	{
		cancelSync = new Object();
		
		currentStatus = StatusChange.STATUS.NOT_STARTED;
		lh = new ListenerHelper();
	}

	/**
	 * Waits 200 ms for cancellation.
	 */
	public synchronized boolean cancel(long timeOut)
	{
		final long WAIT_TIME = 200L;
		if (currentStatus == StatusChange.STATUS.RUNNING)
		{
			currentStatus = StatusChange.STATUS.CANCELED;
			doCancellingActions();
			synchronized(cancelSync)
			{
				try
				{
					cancelSync.wait(WAIT_TIME);
				} catch (InterruptedException e)
				{
					//NOP
				}
			}
		}
		return currentStatus == StatusChange.STATUS.CANCELED;
	}
	
	/**
	 * Things done when cancel is called before we wait that
	 * cancellation has been acknowledged using {@link #cancelSync}.
	 * This does not do anything, subclasses may want to override it.
	 */
	protected void doCancellingActions()
	{
		//NOP
	}

	protected final void checkForCancellation() throws CancellationException
	{
		if (isCanceled())
		{
			throw new CancellationException(getName() + " was cancelled");
		}
	}
	
	public boolean isCanceled()
	{
		return currentStatus == StatusChange.STATUS.CANCELED;
	}

	/**
	 * Does the actual mapping. Should throw CancellationException in case of cancellation
	 * and call {@link #checkForCancellation()} so that response time will not be too large.
	 * @param input
	 * @return
	 * @throws CancellationException
	 */
	protected abstract Y actualMap(T input) throws CancellationException;
	
	public Y map(T input) throws CancellationException
	{
		Y result = null;
		try
		{
			result = actualMap(input);
		}
		finally
		{
			synchronized(cancelSync)
			{
				cancelSync.notifyAll();
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public AbstractStatusReportingProcess<T, Y> clone()
	{
		try{
			AbstractStatusReportingProcess<T, Y> clone = (AbstractStatusReportingProcess<T, Y>) super.clone();
			clone.cancelSync = new Object();
			clone.lh = lh.clone();
			clone.currentStatus = currentStatus;
			return clone;
		}
		catch (CloneNotSupportedException e)
		{
			e.printStackTrace();
			throw new InternalError("Superclass " + getClass().getSuperclass() + " does not support " +
			                        "cloning although it should");
		}
	}
	
	protected void updateStatus(int done, int total)
	{
		updateStatus(done, total, "");
	}
	
	protected void updateStatus(int done, int total, String name)
	{
		lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.RUNNING, StatusChange.STATUS.RUNNING, name, done, total));
	}

	public abstract String getName();

	public void addStatusChangeListener(StatusChangeListener listener)
	{
		lh.addListener(listener);
	}

	public void removeStatusChangeListener(StatusChangeListener listener)
	{
		lh.removeListener(listener);
	}

}
