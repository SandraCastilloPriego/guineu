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

import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import java.awt.Frame;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CancellationException;


public class AlignmentRowFilter extends AbstractStatusReportingModule<Alignment, Alignment>
{
	
	public AlignmentRowFilter(Collection<AlignmentRow> unwanted)
	{
		final Set<AlignmentRow> unwantedAlignmentRows = new HashSet<AlignmentRow>(unwanted);
		FilterFunction<AlignmentRow> f = new FilterFunction<AlignmentRow>()
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
		mapper = FilterFactory.createPostFilter(f);
	}
	
	public AlignmentRowFilter()
	{
		this(new HashSet<AlignmentRow>());
	}
	
	@Override
	protected Alignment actualMap(Alignment input) throws CancellationException
	{
		return mapper.map(input);
	}
	
	@Override
	public String getName()
	{
		return "Filter out unwanted alignment rows";
	}
}
