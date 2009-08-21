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




import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.scorer.SpectrumDotProd;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jmjarkko
 */
public class PeakFilterer implements GCGCDatumFilter
{

	private SpectrumDotProd filterer;
	private GCGCDatum filterPeaks[];
	private AlignmentParameters params;
	
	public PeakFilterer(AlignmentParameters p, List<GCGCData> peaksToFilter, double sensitivity)
	{
		filterer = new SpectrumDotProd();
		params = p.setMinSpectrumMatch(sensitivity);
		int len = 0;
		for (GCGCData d: peaksToFilter)
		{
			len += d.compoundCount();
		}
		
		filterPeaks = new GCGCDatum[len];
		int i = 0;
		for (GCGCData d : peaksToFilter)
		{
			for (GCGCDatum datum : d)
			{
				filterPeaks[i++] = datum;
			}
		}
		java.util.Arrays.sort(filterPeaks);
	}

	public GCGCData filter(GCGCData toFilter)
	{
		return new GCGCData(filter(toFilter.toList()), toFilter.getName());
	}
	
	public List<GCGCDatum> filter(List<GCGCDatum> listToFilter)
	{
		ArrayList<GCGCDatum> validPeaks = new ArrayList<GCGCDatum>(listToFilter.size());
		for (GCGCDatum d : listToFilter)
		{
			if (include(d))
			{
				validPeaks.add(d);
			}
		}
		validPeaks.trimToSize();
		return validPeaks;
	}

	/**
	 */
	public GCGCDatum[] filter(GCGCDatum[] arrayToFilter)
	{
		ArrayList<GCGCDatum> validPeaks = new ArrayList<GCGCDatum>(arrayToFilter.length);
		for (GCGCDatum d : arrayToFilter)
		{
			if (include(d))
			{
				validPeaks.add(d);
			}
		}
		GCGCDatum validPeaksArray[] = new GCGCDatum[validPeaks.size()];
		validPeaks.toArray(validPeaksArray);
		return validPeaksArray;
	}

	/**
	 * @see gcgcaligner.filter.GCGCDatumFilter#getName()
	 */
	public String getName()
	{
		return "Spectrum filter";
	}


	/**
	 * return true if peak is to be excluded.
	 * @param d
	 * @return
	 */
	private boolean include(GCGCDatum d)
	{
		int startIx = 0;
		double rt1 = d.getRT1();
		double rt1Lax = params.getRT1Lax();
		double worstScore = filterer.getWorstScore();
		
		for (; startIx < filterPeaks.length; startIx++)
		{
			double rt1Diff = Math.abs(rt1 - filterPeaks[startIx].getRT1());
			if (rt1Diff < rt1Lax){break;}
		}
		for (int i = startIx; i < filterPeaks.length; i++)
		{
			GCGCDatum peak = filterPeaks[i]; 
			double rt1Diff = Math.abs(rt1 - peak.getRT1());
			if (rt1Diff > rt1Lax){break;}
			if (filterer.calculateScore(d, peak, params) < worstScore)
			{
				return false;
			}
		}
		return true;
	}
}
