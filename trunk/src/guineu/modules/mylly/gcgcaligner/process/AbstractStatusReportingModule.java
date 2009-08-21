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



import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import java.awt.Frame;
import java.util.concurrent.CancellationException;

public abstract class AbstractStatusReportingModule<T,Y> 
extends AbstractStatusReportingProcess<T,Y> 
implements StatusReportingModule<T, Y>
{
	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

//	public void cancel()
//	{
//		// TODO Auto-generated method stub
//		
//	}
//
//	public gcgcaligner.process.HasStatus.STATUS currentStatus()
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}

	public double done()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	public double total()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	protected StatusReportingProcess<T, Y> mapper;

	public AbstractStatusReportingModule()
	{
		super();
	}

	/**
	 * Cancels current {@link #actualMap(Object)} and invokes {@link #mapper}'s 
	 * {@link StatusReportingProcess#cancel(long)}
	 */
	public boolean cancel(long timeOut)
	{
		if (currentStatus == StatusChange.STATUS.RUNNING)
		{
			currentStatus = StatusChange.STATUS.CANCELED;
			synchronized(cancelSync)
			{
				if (mapper != null)
				{
					mapper.cancel(100);
				}
				try
				{
					cancelSync.wait();
				} catch (InterruptedException e)
				{
					//NOP
				}
			}
		}
		return currentStatus == StatusChange.STATUS.CANCELED;
	}

	protected Y actualMap(T input) throws CancellationException
	{
		lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.NOT_STARTED, StatusChange.STATUS.RUNNING, getName()));
		Y result = mapper.map(input);
		lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.RUNNING, StatusChange.STATUS.FINISHED, getName()));
		return result;
	}

	/**
	 * @param listener {@link StatusChangeListener} that is added to
	 * both this and {@link #mapper} that does the actual mapping.
	 */
	public void addStatusChangeListener(StatusChangeListener listener)
	{
		lh.addListener(listener);
		if (mapper != null)
		{
			mapper.addStatusChangeListener(listener);
		}
	}

	/**
	 * @param listener {@link StatusChangeListener} that is removed from
	 * both this and {@link #mapper} that does the actual mapping.
	 */
	public void removeStatusChangeListener(StatusChangeListener listener)
	{
		lh.removeListener(listener);
		if (mapper != null)
		{
			mapper.removeStatusChangeListener(listener);
		}
	}

	@SuppressWarnings("unchecked")
	public AbstractStatusReportingModule<T, Y> clone()
	{
		AbstractStatusReportingModule<T, Y> clone;
		clone = (AbstractStatusReportingModule<T, Y>) super.clone();
		if (mapper != null){clone.mapper = mapper.clone();}
		return clone;
	}

	public void askParameters(Frame parentWindow) throws ParameterInputException {}
	public boolean isConfigurable(){return false;}

}
