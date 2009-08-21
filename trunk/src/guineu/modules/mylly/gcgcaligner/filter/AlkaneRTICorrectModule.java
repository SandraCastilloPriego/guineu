package guineu.modules.mylly.gcgcaligner.filter;

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

import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import guineu.modules.mylly.gcgcaligner.process.ListenerHelper;
import guineu.modules.mylly.gcgcaligner.process.StatusChange;
import guineu.modules.mylly.gcgcaligner.process.StatusChangeListener;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CancellationException;

import javax.swing.JFrame;



public class AlkaneRTICorrectModule extends AbstractStatusReportingModule<InputSet, InputSet>
{
	private AlkaneRTICorrector corr;

	public AlkaneRTICorrectModule()
	{
		corr = new AlkaneRTICorrector();
		lh = new ListenerHelper();
	}
	
	public void askParameters(Frame parentWindow) throws ParameterInputException
	{
		File f = null;
		if (f != null)
		{
			try
			{
				corr = AlkaneRTICorrector.createCorrector(f);
				for (StatusChangeListener listener : lh.getListeners())
				{
					corr.addStatusChangeListener(listener);
				}
			} catch (IOException e)
			{
				throw new ParameterInputException("Encountered an IO error reading " + f.getName(), e);
			}
		}
	}
	
	protected void doCancellingActions()
	{
		final long TIME_OUT = 50;
		corr.cancel(TIME_OUT);
	}

	public String getName()
	{
		return corr.getName();
	}
	
	public AlkaneRTICorrectModule clone()
	{
		AlkaneRTICorrectModule clone = new AlkaneRTICorrectModule();
		clone.corr = corr.clone();
		clone.lh = lh.clone();
		return clone;
	}

	public boolean isConfigurable()
	{
		return true;
	}

	@Override
	protected InputSet actualMap(InputSet input) throws CancellationException
	{
		lh.fireStatusChange(new StatusChange(corr, StatusChange.STATUS.NOT_STARTED, StatusChange.STATUS.RUNNING, getName()));
		InputSet output = corr.map(input);
		lh.fireStatusChange(new StatusChange(corr, StatusChange.STATUS.RUNNING, StatusChange.STATUS.FINISHED, getName()));
		return output;
	}

	public void addStatusChangeListener(StatusChangeListener listener)
	{
		super.addStatusChangeListener(listener);
		if (corr != null){corr.addStatusChangeListener(listener);}
	}
	
	public void removeStatusChangeListener(StatusChangeListener listener)
	{
		super.removeStatusChangeListener(listener);
		if (corr != null){corr.removeStatusChangeListener(listener);}
	}

}
