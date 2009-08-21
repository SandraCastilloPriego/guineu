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
package guineu.modules.mylly.gcgcaligner.alignment;



import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import java.util.Comparator;

public class AlignmentSorterFactory
{

	public static enum SORT_MODE
	{
		name { public String getName(){return "name";}},                
		peaks { public String getName(){return "number of peaks";}},
		rt1 { public String getName(){return "RT 1";}}, 
		rt2 { public String getName(){return "RT 2";}},
		rti { public String getName(){return "RTI";}},
		quantMass { public String getName(){return "Quant Mass";}},
		diffToIdeal { public String getName(){return "Difference to ideal peak";}},
		maxSimilarity { public String getName(){return "maximum similarity";}},
		meanSimilarity { public String getName(){return "mean similarity";}},
		stdSimilarity { public String getName(){return "similarity std dev";}},
		none { public String getName(){return "nothing";}};

		public abstract String getName();
	}
	
	public static Comparator<AlignmentRow> getComparator(final SORT_MODE mode)
	{
		return getComparator(mode, true);
	}

	/**
	 * Return a comparator that <b>is</b> inconsistent with equals.
	 * @param mode
	 * @param ascending
	 * @return
	 */
	public static Comparator<AlignmentRow> getComparator(final SORT_MODE mode, final boolean ascending)
		{
			switch(mode)
			{
			case name:
				return getNameComparator(ascending);
			case peaks:
				return getPeakCountComparator(ascending);
			case rt1:
			case rt2:
			case rti:
			case quantMass:
			case maxSimilarity:
			case meanSimilarity:
			case stdSimilarity:
				//All these use the same sort of comparator
				return getDoubleValComparator(ascending, mode);
			case diffToIdeal:
				return distToIdealComparator(ascending);
			default:
				return nullComparator();
			}
		}

	private static Comparator<AlignmentRow> getNameComparator(final boolean ascending)
	{
		return new Comparator<AlignmentRow>()
		{
			public int compare(AlignmentRow o1, AlignmentRow o2)
			{
				int comparison = 0;
				//This if...else if -pair causes unknown peaks to appear 
				//last in the list.
				if (GCGCDatum.UNKOWN_NAME.equals(o1.getName()) &&
						!o1.getName().equals(o2.getName()))
				{
					comparison = 1;
				}
				else if (GCGCDatum.UNKOWN_NAME.equals(o2.getName()) &&
						!o1.getName().equals(o2.getName()))
				{
					comparison = -1;
				}
				else
				{
					comparison = o1.getName().compareToIgnoreCase(o2.getName());
				}				
				return ascending ? comparison : -comparison;
			}
		};
	}
	
	private static Comparator<AlignmentRow> getPeakCountComparator(final boolean ascending)
	{
		return new Comparator<AlignmentRow>()
		{
			public int compare(AlignmentRow o1, AlignmentRow o2)
			{
				int comp = o1.nonNullPeakCount() - o2.nonNullPeakCount();
				return ascending ? comp : -comp;
			}
		};
	}
	
	private static Comparator<AlignmentRow> getDoubleValComparator(final boolean ascending, final SORT_MODE mode)
	{
		return new Comparator<AlignmentRow>()
		{
			public int compare(AlignmentRow o1, AlignmentRow o2)
			{
				int comparison = 0;
				double val1 = 0.0;
				double val2 = 0.0;
				switch (mode)
				{
				case rt1:
					val1 = o1.getMeanRT1();
					val2 = o2.getMeanRT1();
					break;
				case rt2:
					val1 = o1.getMeanRT2();
					val2 = o2.getMeanRT2();
					break;
				case rti:
					val1 = o1.getMeanRTI();
					val2 = o2.getMeanRTI();
					break;
				case quantMass:
					val1 = o1.getQuantMass();
					val2 = o2.getQuantMass();
					break;
				case maxSimilarity:
					val1 = o1.getMaxSimilarity();
					val2 = o2.getMaxSimilarity();
					break;
				case meanSimilarity:
					val1 = o1.getMeanSimilarity();
					val2 = o2.getMeanSimilarity();
					break;
				case stdSimilarity:
					val1 = o1.getSimilarityStdDev();
					val2 = o2.getSimilarityStdDev();
				}
				if (val1 < val2){comparison = -1;}
				if (val1 > val2){comparison = 1;}
				return ascending ? comparison : -comparison;
			}			
		};
	}
	
	private static Comparator<AlignmentRow> distToIdealComparator(final boolean ascending)
	{
		return new Comparator<AlignmentRow>()
		{
			public int compare(AlignmentRow o1, AlignmentRow o2)
			{
				DistValue val1 = o1.getDistValue();
				DistValue val2 = o2.getDistValue();
				return ascending ? val1.compareTo(val2) : -val1.compareTo(val2);
			}
		};
	}
	
	private static Comparator<AlignmentRow> nullComparator()
	{
		return new Comparator<AlignmentRow>()
		{
			public int compare(AlignmentRow o1, AlignmentRow o2)
			{
				return 0;
			}
		};
	}
	
}
