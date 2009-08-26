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
package guineu.modules.mylly.filter.NameFilter;


import guineu.modules.mylly.alignment.scoreAligner.functions.Alignment;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentRow;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class AlignmentRowFilter
{
	FilterFunction<AlignmentRow> f;
	public AlignmentRowFilter(Collection<AlignmentRow> unwanted)
	{
		final Set<AlignmentRow> unwantedAlignmentRows = new HashSet<AlignmentRow>(unwanted);
		f = new FilterFunction<AlignmentRow>()
		{
			public boolean exclude(AlignmentRow obj)
			{
				return unwantedAlignmentRows.contains(obj);
			}
			public boolean include(AlignmentRow obj){return !exclude(obj);}
			public String getName(){return AlignmentRowFilter.this.getName();}
			public AlignmentRow map(AlignmentRow obj)
			{
				if (include(obj)){return obj;}
				return null;
			}
		};
	
	}
	
	public AlignmentRowFilter()
	{
		this(new HashSet<AlignmentRow>());
	}
	

	protected Alignment actualMap(Alignment input) 
	{
		Alignment newAlignment = new Alignment(input.getColumnNames(), input.getParameters(),input.getAligner());

		for(AlignmentRow row: input.getAlignment()){		
			if(f.include(row)){				
				newAlignment.addAlignmentRow(row);
			}
		}
		return newAlignment;
	}
	

	public String getName()
	{
		return "Filter out unwanted alignment rows";
	}
}
