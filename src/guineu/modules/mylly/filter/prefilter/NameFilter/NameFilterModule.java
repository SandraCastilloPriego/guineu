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
package guineu.modules.mylly.filter.prefilter.NameFilter;

import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;




public class NameFilterModule 
{
	
	private NameFilterTool curNameFilter;

	public NameFilterModule()
	{
		curNameFilter = new NameFilterTool(new ArrayList<String>());
	}
	
	public void generateNewFilter(Collection<String> names)
	{
		Set<String> filteredNames = curNameFilter.filteredNames();
		filteredNames.addAll(names);
		curNameFilter = new NameFilterTool(filteredNames);
	}
	
	
	public String getName()
	{
		return curNameFilter.getName();
	}

	
	protected List<GCGCData> actualMap(List<GCGCData> obj)
	{

		int done = 0;
		int totalCount = 0;
		
		List<GCGCData> gcgcdata = obj;
		for (GCGCData gcdata : gcgcdata)
		{
			totalCount += gcdata.compoundCount();
		}		
		
		List<GCGCData> filtered = new ArrayList<GCGCData>();
		
		for (GCGCData gcdata : gcgcdata)
		{
			List<GCGCDatum> filteredFile = new ArrayList<GCGCDatum>();
			for (GCGCDatum d : gcdata)
			{
				
				if (curNameFilter.include(d))
				{
					filteredFile.add(d);
				}
				done++;
			}
			filtered.add(new GCGCData(filteredFile, gcdata.getName()));
		}		
		return filtered;
	}
	
	
	
}
