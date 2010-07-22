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
package guineu.modules.mylly.filter.singlingFilter;

import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.mylly.filter.NameFilter.AlignmentRowFilter;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.modules.mylly.datastruct.Pair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Singling {

	private boolean filterUnknowns;
	private double minSimilarity;
	private SimpleGCGCDataset input;

	public SimpleGCGCDataset actualMap(SimpleGCGCDataset input) {
		this.input = input;
		try {
			return call();
		} catch (Exception e) {
			return null;
		}

	}

	public Singling(double minSimilarity, boolean filterUnknowns) {
		this.filterUnknowns = filterUnknowns;
		this.minSimilarity = minSimilarity;
	}

	public String getName() {
		return "Leave only uniques filter";
	}

	private static class PeakReducer {

		private final boolean _containsMainPeaks;
		private final boolean _filterUnknowns;

		//First one will contain the largest peak, second one the peak nearest
		//to ideal. Those can be the same peak
		private Map<String, Pair<SimplePeakListRowGCGC, SimplePeakListRowGCGC>> _peaks;
		private List<SimplePeakListRowGCGC> _unknownsList;
		private final double _minSimilarity;

		public PeakReducer(boolean containsMainPeaks, boolean filterUnknowns) {
			this(containsMainPeaks, filterUnknowns, 0);
		}

		public PeakReducer(boolean containsMainPeaks, boolean filterUnknowns, double minSimilarity) {
			_containsMainPeaks = containsMainPeaks;
			_filterUnknowns = filterUnknowns;
			_minSimilarity = minSimilarity;
			_peaks = new HashMap<String, Pair<SimplePeakListRowGCGC, SimplePeakListRowGCGC>>();
			_unknownsList = new ArrayList<SimplePeakListRowGCGC>();
		}

		public void addAlignment(SimplePeakListRowGCGC row) {
			if (row.getMaxSimilarity() < _minSimilarity) {
				return;
			}

			if (row.getName().contains(GCGCDatum.UNKOWN_NAME)) {
				if (!_filterUnknowns) {
					_unknownsList.add(row);
				}
				return;
			}

			Pair<SimplePeakListRowGCGC, SimplePeakListRowGCGC> pair = _peaks.get(row.getName());
			if (!_peaks.containsKey(row.getName())) {
				pair = new Pair<SimplePeakListRowGCGC, SimplePeakListRowGCGC>(null, null);
				_peaks.put(row.getName(), pair);
			}

			//First of pair, the one with most peaks
			boolean setFirst = false;
			if (pair.getFirst() != null) {
				int peakCountDiff = (int) (row.nonNullPeakCount() - pair.getFirst().nonNullPeakCount());
				if (peakCountDiff > 0 || (peakCountDiff == 0 && row.getMaxSimilarity() >
						pair.getFirst().getMaxSimilarity())) {
					setFirst = true;
				}
			} else {
				setFirst = true;
			}
			if (setFirst) {
				pair.setFirst(row);
			}

			//Second of pair, the closest to ideal by RTI alignment.
			boolean distanceLess = (pair.getSecond() == null) || row.getDistValue().compareTo(pair.getSecond().getDistValue()) < 0;

			boolean similarityMore = (pair.getSecond() == null) || row.getDistValue().compareTo(pair.getSecond().getDistValue()) == 0 &&
					row.getMaxSimilarity() > pair.getSecond().getMaxSimilarity();

			boolean setSecond = _containsMainPeaks && (distanceLess || similarityMore);
			if (setSecond) {
				pair.setSecond(row);
			}

		}

		public List<SimplePeakListRowGCGC> getAlignmentRows() {
			ArrayList<SimplePeakListRowGCGC> rows = _containsMainPeaks ? new ArrayList<SimplePeakListRowGCGC>(2 * _peaks.size()) : new ArrayList<SimplePeakListRowGCGC>(_peaks.size());

			for (Map.Entry<String, Pair<SimplePeakListRowGCGC, SimplePeakListRowGCGC>> peak : _peaks.entrySet()) {
				SimplePeakListRowGCGC first = peak.getValue().getFirst();
				rows.add(first);
				SimplePeakListRowGCGC second = peak.getValue().getSecond();

				if (_containsMainPeaks && !(first.equals(second))) {
					rows.add(second);
				}
			}
			for (SimplePeakListRowGCGC row : _unknownsList) {
				rows.add(row);
			}
			return rows;
		}
	}

	public SimpleGCGCDataset call() throws Exception {
		//we don't want to apply this filter in the peaks with Quant Mass
		List<SimplePeakListRowGCGC> QuantMassOnes = input.getQuantMassAlignments();
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);
		input = filterQuantMass.actualMap(input); //Filter the quant mass alignments out

		PeakReducer reducer = new PeakReducer(input.containsMainPeaks(), filterUnknowns, minSimilarity);
		
		for (PeakListRow row : input.getAlignment()) {
			SimplePeakListRowGCGC newRow = (SimplePeakListRowGCGC) row.clone();
			reducer.addAlignment(newRow);
		}
		SimpleGCGCDataset modified = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
		
		modified.addAll(reducer.getAlignmentRows());
		for(SimplePeakListRowGCGC row : QuantMassOnes){
			modified.addAlignmentRow((SimplePeakListRowGCGC)row.clone());
		}
		return modified;
	}
}
