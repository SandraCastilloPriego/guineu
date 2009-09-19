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

import guineu.data.PeakListRow;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.modules.mylly.filter.NameFilter.AlignmentRowFilter;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.List;

public class Similarity {

	public final static String MAX_SIMILARITY = "maximum similarity";
	public final static String MEAN_SIMILARITY = "mean similarity";
	public final static String REMOVE = "Remove";
	public final static String RENAME = "Rename";
	private double minValue;
	private String mode;
	private String action;

	public Similarity(double minValue, String action, String mode) {
		this.minValue = minValue;
		this.mode = mode;
		this.action = action;
	}

	protected SimpleGCGCDataset actualMap(SimpleGCGCDataset input) {
		//we don't want to apply this filter in the peaks with Quant Mass
		List<SimplePeakListRowGCGC> QuantMassOnes = input.getQuantMassAlignments();
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);
		SimpleGCGCDataset datasetNoMass = filterQuantMass.actualMap(input); //Filter the quant mass alignments out

		List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();
		for (PeakListRow row : datasetNoMass.getAlignment()) {
			SimplePeakListRowGCGC newRow = (SimplePeakListRowGCGC) row.clone();
			double curVal = 0.0;
			if (MAX_SIMILARITY.equals(mode)) {
				curVal = newRow.getMaxSimilarity();
			} else if (MEAN_SIMILARITY.equals(mode)) {
				curVal = newRow.getMeanSimilarity();
			}
			if (REMOVE.equals(action)) {
				if (curVal >= minValue) {
					als.add(newRow);
				}
			} else if(RENAME.equals(action)) {
				if (curVal < minValue) {
					newRow.setName(GCGCDatum.UNKOWN_NAME);
					newRow.setAllNames("");
				}
				als.add(newRow);
			}
		}
		SimpleGCGCDataset filtered = new SimpleGCGCDataset(datasetNoMass.getColumnNames(), datasetNoMass.getParameters(), datasetNoMass.getAligner());
		filtered.addAll(als);
		for(SimplePeakListRowGCGC row : QuantMassOnes){
			filtered.addAlignmentRow((SimplePeakListRowGCGC)row.clone());
		}
		return filtered;
	}

	public String getName() {
		return "Filter by similarity";
	}
}
