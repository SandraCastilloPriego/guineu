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
package guineu.modules.mylly.filter.peakCounter;

import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentRow;
import guineu.modules.mylly.filter.NameFilter.FilterFunction;





/**
 * Filters out {@link AlignmentRow}s with at most <code>n</code> peaks.
 * Here <code>n</code> is an parameter given to PeakCountFilter
 * @author jmjarkko
 *
 */
public class PeakCount implements FilterFunction<AlignmentRow>
{

	private int _minPeakCount;
	
	public PeakCount(int minimumNumberOfPeaks)
	{
		_minPeakCount = minimumNumberOfPeaks;
	}
	
	public String getName()
	{
		return "Filter alignment by peak count";
	}

	public boolean include(AlignmentRow obj)
	{
		return obj.nonNullPeakCount() > _minPeakCount;
	}

	public AlignmentRow map(AlignmentRow obj)
	{
		if (include(obj))
		{
			return obj;
		}
		return null;
	}

	public boolean exclude(AlignmentRow obj)
	{
		return !include(obj);
	}


}
