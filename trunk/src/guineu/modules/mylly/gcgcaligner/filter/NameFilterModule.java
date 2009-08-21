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
package guineu.modules.mylly.gcgcaligner.filter;

import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import guineu.modules.mylly.gcgcaligner.process.StatusChange;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;




public class NameFilterModule extends AbstractStatusReportingModule<InputSet, InputSet>
{
	
	private NameFilter curNameFilter;

	public NameFilterModule()
	{
		curNameFilter = new NameFilter(new ArrayList<String>());
	}
	
	private void generateNewFilter(Collection<String> names)
	{
		Set<String> filteredNames = curNameFilter.filteredNames();
		filteredNames.addAll(names);
		curNameFilter = new NameFilter(filteredNames);
	}
	
	/**
	 * Silently fails on errors
	 */
	public void askParameters(Frame parentWindow)
	{
		File f = null; /*GCGCAlign.getMainWindow().launchSingleFileChooser(getClass());*/
		if (f != null)
		{
			try
			{
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				List<String> names = new ArrayList<String>();
				while ((line = (br.readLine())) != null)
				{
					names.add(line);
				}
				generateNewFilter(names);
			}
			catch (FileNotFoundException e)
			{
			//	GCGCAlign.getMainWindow().displayErrorDialog("File " + f + " was not found");
			} 
			catch (IOException e)
			{
			//	GCGCAlign.getMainWindow().displayErrorDialog(e);
			}
		}
	}

	public String getName()
	{
		return curNameFilter.getName();
	}

	@Override
	protected InputSet actualMap(InputSet obj)
	{
		
		int counter = 0;
		int done = 0;
		int totalCount = 0;
		
		List<GCGCData> gcgcdata = obj.getData();
		for (GCGCData gcdata : gcgcdata)
		{
			totalCount += gcdata.compoundCount();
		}
		
		final int checkFrequency = Math.max(1, Math.min(totalCount / 100, 1000));
		
		List<GCGCData> filtered = new ArrayList<GCGCData>();
		
		lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.NOT_STARTED, StatusChange.STATUS.RUNNING, getName()));
		for (GCGCData gcdata : gcgcdata)
		{
			List<GCGCDatum> filteredFile = new ArrayList<GCGCDatum>();
			for (GCGCDatum d : gcdata)
			{
				if (++counter == checkFrequency)
				{
					counter = 0;
					checkForCancellation();
					lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.RUNNING, StatusChange.STATUS.RUNNING,
					                                     getName(), done, totalCount));
				}
				if (curNameFilter.include(d))
				{
					filteredFile.add(d);
				}
				done++;
			}
			filtered.add(new GCGCData(filteredFile, gcdata.getName()));
		}
		InputSet result =  new InputSet(filtered);
		lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.RUNNING, StatusChange.STATUS.FINISHED, getName()));
		return result;
	}
	
	public NameFilterModule clone()
	{
		NameFilterModule clone = new NameFilterModule();
		clone.generateNewFilter(curNameFilter.filteredNames());
		clone.lh = lh.clone();
		return clone;
	}

	public boolean isConfigurable()
	{
		return true;
	}
}
