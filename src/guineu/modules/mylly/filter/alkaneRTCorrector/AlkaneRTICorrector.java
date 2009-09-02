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
package guineu.modules.mylly.filter.alkaneRTCorrector;



import guineu.modules.mylly.gcgcaligner.datastruct.ComparablePair;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;


/**
 * Corrects RTIs for all peaks based on give list on alkanes.
 * @author jmjarkko
 *
 */

public class AlkaneRTICorrector 
{

	private double minRTI;
	private double maxRTI;
	private Set<String> names; //Names of all the alkanes that we handle
	private IdealAlkanePeak[] sortedAlkanes;
	@SuppressWarnings("unchecked")
	private final static Comparator WrapperAndPeakComparator  = getIdealAlkanePeakAndGCGCDatumComparator();
	private static final double	DEF_DELTA	= 50;
	
	private volatile double _total;
	private volatile double _done;
	private volatile List<GCGCData> _input;

	
	private AlkaneRTICorrector(List<ComparablePair<String, Double>> alkanes)
	{
		names = new HashSet<String>(alkanes.size());
		this.sortedAlkanes = createAlkaneList(alkanes);
	}
	
	public AlkaneRTICorrector()
	{
		names = new HashSet<String>();
		this.sortedAlkanes = new IdealAlkanePeak[0];
	}
	
	private static double absDiff(IdealAlkanePeak idealPeak, GCGCDatum peak)
	{
		return Math.abs(idealPeak.getRTI() - peak.getRTI());
	}

	private static double diff(IdealAlkanePeak idealPeak, GCGCDatum peak)
	{
		return idealPeak.getRTI() - peak.getRTI();
	}
	
	private static double diff(GCGCDatum peak, IdealAlkanePeak idealPeak)
	{
		return peak.getRTI() - idealPeak.getRTI();
	}

	
	@SuppressWarnings("unchecked")
	private void handlePossible(GCGCDatum d, Map<IdealAlkanePeak, GCGCDatum> idealToFoundMap)
	{
		if (names.contains(d.getName()))
		{
			int ix = java.util.Arrays.binarySearch(sortedAlkanes, d, WrapperAndPeakComparator);
			if (ix > 0)
			{
				IdealAlkanePeak idealPeak = sortedAlkanes[ix];
				GCGCDatum curPeak = idealToFoundMap.get(idealPeak);
				if (curPeak == null || absDiff(idealPeak, d) < absDiff(idealPeak, curPeak))
				{
					idealToFoundMap.put(idealPeak, d);
				}
			}
		}
	}

	/**
	 * Converts given list of list of strings corresponding to alkane names and
	 * and doubles of retention time indices to list of <code>IdealAlkanePeak</code>s.
	 * Saves result to sortedAlkanes variable.
	 * @param alkanes
	 */
	private <T extends Pair<String, Double>>IdealAlkanePeak[] createAlkaneList(List<T> alkanes)
	{
		Comparator<Pair<String, Double>> alkaneSorter = new Comparator<Pair<String, Double>>()
		{
			public int compare(Pair<String, Double> o1, Pair<String, Double> o2)
			{
				int comparison = (o1.getSecond() < o2.getSecond()) ? -1 : (o2.getSecond() < o1.getSecond()) ? 1 : 0;
				return comparison;
			}
		};
		maxRTI = alkanes.get(alkanes.size()- 1).getSecond() + DEF_DELTA;
		minRTI = alkanes.get(0).getSecond() - DEF_DELTA; if(minRTI < 0){minRTI = 0;}

		java.util.Collections.sort(alkanes, alkaneSorter);

		IdealAlkanePeak[] sortedAlkaneArray = new IdealAlkanePeak[alkanes.size()];
		for (int i = 0; i < alkanes.size(); i++)
		{
			double lowerLimit;
			double upperLimit;
			double curRTI = alkanes.get(i).getSecond().doubleValue();
			if (i != 0)
			{
				lowerLimit = (alkanes.get(i-1).getSecond().doubleValue() +	curRTI) / 2;
			}
			else
			{
				lowerLimit = minRTI;
			}
			if (i == alkanes.size() - 1)
			{
				upperLimit = maxRTI;
			}
			else
			{
				upperLimit = (alkanes.get(i+1).getSecond().doubleValue() + curRTI) / 2;
			}
			names.add(alkanes.get(i).getFirst());
			sortedAlkaneArray[i] = new IdealAlkanePeak(alkanes.get(i).getFirst(), lowerLimit, upperLimit, curRTI);
		}
		//Freeze namelist
		names = Collections.unmodifiableSet(names);
		return sortedAlkaneArray;
	}

	/**
	 * Does not affect internal state of AlkaneRTICorrector
	 * @param listToFilter
	 * @return
	 */
	private List<GCGCDatum> filter(List<GCGCDatum> listToFilter)
	{
		List<GCGCDatum> newPeaks = new ArrayList<GCGCDatum>(listToFilter.size());
		Map<IdealAlkanePeak, GCGCDatum> idealToFoundMap = new HashMap<IdealAlkanePeak, GCGCDatum>();
		for (GCGCDatum d : listToFilter)
		{
			if (d.getRTI() > minRTI && d.getRTI() < maxRTI)
			{
				handlePossible(d, idealToFoundMap);
			}
			newPeaks.add(d);
		}
		List<ComparablePair<IdealAlkanePeak, GCGCDatum>> foundAlkanes = new ArrayList<ComparablePair<IdealAlkanePeak, GCGCDatum>>();
		for (Entry<IdealAlkanePeak, GCGCDatum> entry : idealToFoundMap.entrySet())
		{
			if (entry.getValue() != null)
			{
				foundAlkanes.add(new ComparablePair<IdealAlkanePeak, GCGCDatum>(entry.getKey(), entry.getValue()));
			}
		}
		java.util.Collections.sort(foundAlkanes);
		return correctRTIs(newPeaks, foundAlkanes);
	}

	private List<GCGCDatum> correctRTIs(List<GCGCDatum> originals, List<? extends Pair<IdealAlkanePeak, GCGCDatum>> alkanes)
	{
		if (alkanes.size() < 2)
		{
			//Cannot do any interpolation, so return unmodified originals
			return originals;
		}
		/**
		 * Precalculate slopes for each of the intervals
		 * between alkanes. Boundary-conditions are before first
		 * alkane and after last alkane. Before first alkane is handled
		 * in such a way that actual and found RTIs are both expected to
		 * be 0. After last alkane same slope is used as for between last
		 * and the one before last.
		 */
		double[] slopes = new double[alkanes.size() + 1];
		{
			double lower = 0;
			double lowerIdeal = 0;
			for (int i = 0; i < alkanes.size(); i++)
			{
				Pair<IdealAlkanePeak, GCGCDatum> curPair = alkanes.get(i);
				double upper = curPair.getSecond().getRTI();
				double upperIdeal = curPair.getFirst().getRTI();
				slopes[i] = (upper - lower) / (upperIdeal - lowerIdeal);

				lower = upper;
				lowerIdeal = upperIdeal;
			} 
			slopes[slopes.length - 1] = slopes[slopes.length - 2];
		}

		ListIterator<GCGCDatum> iter = originals.listIterator();
		while (iter.hasNext())
		{
			GCGCDatum current = iter.next();
			int i = 0;
			Pair<IdealAlkanePeak, GCGCDatum> curPair = null;
			for (; i < alkanes.size(); i++)
			{
				curPair = alkanes.get(i);
				if (current.getRTI() >= curPair.getFirst().getLowerBound() && 
						current.getRTI() < curPair.getFirst().getUpperBound())
				{
					//This peak fits here
					break;
				}
			}
			GCGCDatum scaledPeak = current.clone();
			if (curPair.getSecond().equals(current))
			{
				/**
				 * If we've found the actual alkane, correct the name
				 * just in case it is wrong.
				 */
				scaledPeak.setName(curPair.getFirst().getName());
			}
			else
			{
				scaledPeak.setRTI(current.getRTI() * slopes[i]);
			}
			iter.set(scaledPeak);
		}
		return originals;
	}

	public String getName()
	{
		return "Alkane RTI corrector";
	}

	private static ComparablePair<String, Double> parseString(String str)
	{
		ComparablePair<String, Double> parsed = null;
		if (str != null)
		{
			double rti;
			String name;
			String[] tokens = str.split("\t");
			if (tokens.length == 3)
			{
				try
				{
					name = tokens[1];
					rti = Double.parseDouble(tokens[2]);
					parsed = new ComparablePair<String, Double>(name, rti);
				}
				catch (NumberFormatException e)
				{
					//Ok, it wasn't actually a number so let us
					//skip this line then and return null
				}
			}
		}
		return parsed;
	}
	
	public static AlkaneRTICorrector createCorrector(File alkanesFile) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(alkanesFile));
		
		List<ComparablePair<String, Double>> validLines = new ArrayList<ComparablePair<String, Double>>();
		String line;
		while ((line = br.readLine()) != null)
		{
			ComparablePair<String, Double> curAlkane = parseString(line);
			if (curAlkane != null)
			{
				validLines.add(curAlkane);
			}
		}
		return new AlkaneRTICorrector(validLines);
	}

	@SuppressWarnings("unchecked")
	private static Comparator getIdealAlkanePeakAndGCGCDatumComparator()
	{
		return new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				//We want to compare IdealAlkanePeak to GCGCDatum
				//IdealAlkanePeak contains the ideal retention index
				//read from 
				int comparison = 0;
				double diff;
				
				if (o1 instanceof IdealAlkanePeak)
				{
					IdealAlkanePeak first;
					GCGCDatum second;
					first = (IdealAlkanePeak) o1;
					second = (GCGCDatum) o2;
					diff = diff(first, second);
				}
				else
				{
					IdealAlkanePeak second;
					GCGCDatum first;
					first = (GCGCDatum) o1;
					second = (IdealAlkanePeak) o2;
					diff = diff(first, second);
				}
				if (diff < 0){comparison = -1;}
				else if (diff > 0){comparison = 1;}
				return comparison;
			}
		};
	}

	protected List<GCGCData> actualMap(List<GCGCData> input)
	{
		synchronized (this)
		{
			_input = input;
			try
			{
				return call();
			} catch (Exception e)
			{
				throw new CancellationException(e.getLocalizedMessage());
			}
		}
	}	
	

	private class IdealAlkanePeak implements Comparable<IdealAlkanePeak>, Cloneable
	{
		private String name;
		private double lowerBound;
		private double upperBound;
		private double rti;
	
		private IdealAlkanePeak(String name, double lowerBound, double upperBound, double rti)
		{
			this.name = name;
			this.lowerBound = lowerBound;
			this.upperBound = upperBound;
			this.rti = rti;
		}
	
		public double getLowerBound(){return lowerBound;}
		public double getUpperBound(){return upperBound;}
		public double getRTI(){return rti;}
		public String getName(){return name;}
	
		public int compareTo(IdealAlkanePeak o)
		{
			int comparison = 0;
			if (o == null || rti > o.rti)
			{
				comparison = 1;
			}
			else if (rti < o.rti)
			{
				comparison = -1;
			}
			return comparison;
		}
		
		public String toString()
		{
			return name + " [" + lowerBound + ", " + upperBound + "] (" + rti + ")";
		}
		
		public IdealAlkanePeak clone()
		{
			return new IdealAlkanePeak(name, lowerBound, upperBound, rti);
		}
	}

	public List<GCGCData> call() throws Exception
	{
		List<GCGCData> input = _input;
		_total = input.size();
		ArrayList<GCGCData> tempList = new ArrayList<GCGCData>(input);
		ListIterator<GCGCData> iter = tempList.listIterator();
		int total = tempList.size();
		_total = tempList.size();
		int doneCount = 0;
		while(iter.hasNext())
		{			
			GCGCData data = iter.next();
			List<GCGCDatum> filtered = filter(data.toList());
			iter.set(new GCGCData(filtered, data.getName()));
			doneCount++;
			_done = doneCount;			
		}
		_done = _total;
		return tempList;
	}

	public double getDone()
	{
		return _done;
	}

	public double getTotal()
	{
		return _total;
	}
}
