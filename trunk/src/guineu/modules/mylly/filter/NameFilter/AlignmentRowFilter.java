/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.mylly.filter.NameFilter;


import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.datasets.SimpleGCGCDataset;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class AlignmentRowFilter
{
	FilterFunction<SimplePeakListRowGCGC> f;
	public AlignmentRowFilter(Collection<SimplePeakListRowGCGC> unwanted)
	{
		final Set<SimplePeakListRowGCGC> unwantedAlignmentRows = new HashSet<SimplePeakListRowGCGC>(unwanted);
		f = new FilterFunction<SimplePeakListRowGCGC>()
		{
			public boolean exclude(SimplePeakListRowGCGC obj)
			{
				return unwantedAlignmentRows.contains(obj);
			}
			public boolean include(SimplePeakListRowGCGC obj){return !exclude(obj);}
			public String getName(){return AlignmentRowFilter.this.getName();}
			public SimplePeakListRowGCGC map(SimplePeakListRowGCGC obj)
			{
				if (include(obj)){return obj;}
				return null;
			}
		};
	
	}
	
	public AlignmentRowFilter()
	{
		this(new HashSet<SimplePeakListRowGCGC>());
	}
	

	public SimpleGCGCDataset actualMap(SimpleGCGCDataset input)
	{
		SimpleGCGCDataset newAlignment = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(),input.getAligner());

		for(PeakListRow row: input.getAlignment()){
			if(f.include((SimplePeakListRowGCGC)row)){
				newAlignment.addRow(row);
			}
		}
		return newAlignment;
	}
	

	public String getName()
	{
		return "Filter out unwanted alignment rows";
	}
}
