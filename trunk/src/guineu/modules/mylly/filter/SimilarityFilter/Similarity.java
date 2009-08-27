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
package guineu.modules.mylly.filter.SimilarityFilter;

import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.modules.mylly.filter.NameFilter.AlignmentRowFilter;
import java.util.ArrayList;
import java.util.List;




public class Similarity 		
{
	private final static String MAX_SIMILARITY = "maximum similarity";
	private final static String MEAN_SIMILARITY = "mean similarity";

	private double minValue;
	private String mode;
	
	
	public Similarity(double minValue, int modeInt)
	{
		this.minValue = minValue;
		if(modeInt == 1){
			mode = MAX_SIMILARITY;
		}else{
			mode = MEAN_SIMILARITY;
		}
	}
	
	protected SimpleGCGCDataset actualMap(SimpleGCGCDataset input)
	{
		//we don't want to apply this filter in the peaks with Quant Mass
		List<SimplePeakListRowGCGC> QuantMassOnes = input.getQuantMassAlignments();
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);		
		input = filterQuantMass.actualMap(input); //Filter the quant mass alignments out
		
		List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();
		for (SimplePeakListRowGCGC row : input.getAlignment())
		{			
			
			double curVal = 0.0;
			if (MAX_SIMILARITY.equals(mode)){curVal = row.getMaxSimilarity();}
			else if (MEAN_SIMILARITY.equals(mode)){curVal = row.getMeanSimilarity();}
			if (curVal >= minValue)
			{
				als.add(row);
			}
		}
		SimpleGCGCDataset filtered = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
		filtered.addAll(als);
		filtered.addAll(QuantMassOnes);
		return filtered;
	}

	
	public String getName()
	{
		return "Filter by similarity";
	}
	

}
