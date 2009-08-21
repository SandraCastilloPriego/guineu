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
import guineu.modules.mylly.gcgcaligner.datastruct.Spectrum;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

/**
 * Immutable
 * @author jmjarkko
 */
public class AlignmentRow implements Comparable<AlignmentRow>, Iterable<GCGCDatum>, Cloneable {

	private final static Comparator<Entry<String[], Integer>> comp = new Comparator<Entry<String[], Integer>>() {

		public int compare(Entry<String[], Integer> o1, Entry<String[], Integer> o2) {
			int comparison = o2.getValue() - o1.getValue();
			if (comparison == 0) {
				comparison = o1.getKey()[0].compareTo(o2.getKey()[0]);
			}
			return comparison;
		}
	};
	public final static String DEFAULT_NAME = "UNKNOWN";
	private final static Double NOT_USED = Double.NaN;
	private double rt1mean;
	private double rt2mean;
	private double rtimean;
	private double quantMass;
	private double similarityMean;
	private double similarityStdDev;
	private double maxSimilarity;
	private boolean modified;
	private int nonNullPeaks = 0;
	private String CAS;
	private String name = DEFAULT_NAME;
	private String CASnumbers[];
	private String names[];
	private Spectrum spectrum;
	private GCGCDatum row[];
	private DistValue _distValue;

	
	public AlignmentRow(AlignmentPath p) {
		rt1mean = p.getRT1();
		rt2mean = p.getRT2();
		rtimean = p.getRTI();
		quantMass = p.getQuantMass();
		similarityMean = p.getMeanSimilarity();
		similarityStdDev = p.getSimilarityStdDev();
		maxSimilarity = p.getMaxSimilarity();
		row = new GCGCDatum[p.length()];
		CAS = p.getCAS();
		if (CAS == null) {
			CAS = "0-00-0";
		}
		Map<String[], Integer> nameFrequencies = new HashMap<String[], Integer>();

		for (int i = 0; i < p.length(); i++) {
			GCGCDatum d = p.getPeak(i);
			if (d != null && d != GCGCDatum.getGAP()) {
				row[i] = d;
				nonNullPeaks++;
				String curName = (d.isIdentified()) ? d.getName() : null;
				String curCAS = d.getCAS();
				if (curCAS == null) {
					curCAS = "0-00-0";
				}
				if (curName != null) {
					if (nameFrequencies.containsKey(curName)) {
						String[] data = {curName, curCAS};
						nameFrequencies.put(data, nameFrequencies.get(curName) + 1);
					} else {
						String[] data = {curName, curCAS};
						nameFrequencies.put(data, 1);
					}
				}

			} else {
				row[i] = GCGCDatum.getGAP();
			}
		}
		names = new String[nameFrequencies.size()];
		CASnumbers = new String[nameFrequencies.size()];

		List<Entry<String[], Integer>> toSort = new ArrayList<Entry<String[], Integer>>(nameFrequencies.size());

		for (Entry<String[], Integer> entry : nameFrequencies.entrySet()) {
			toSort.add(entry);
		}
		java.util.Collections.sort(toSort, comp);

		for (int i = 0; i < toSort.size(); i++) {
			names[i] = toSort.get(i).getKey()[0];
			CASnumbers[i] = toSort.get(i).getKey()[1];
		}

		name = GCGCDatum.UNKOWN_NAME;
		for (int i = 0; i < names.length; i++) {
			if (!GCGCDatum.UNKOWN_NAME.equals(names[i])) {
				name = names[i];
				CAS = CASnumbers[i];
				break;
			}
		}


		this.spectrum = p.getSpectrum() == null ? null : p.getSpectrum().clone();
		_distValue = DistValue.getNull();
	}

	private AlignmentRow() {
	}

	public AlignmentRow clone() {
		AlignmentRow clone = new AlignmentRow();
		clone.rt1mean = rt1mean;
		clone.rt2mean = rt2mean;
		clone.rtimean = rtimean;
		clone.quantMass = quantMass;
		clone.similarityMean = similarityMean;
		clone.similarityStdDev = similarityStdDev;
		clone.maxSimilarity = maxSimilarity;
		clone.modified = modified;
		clone.nonNullPeaks = nonNullPeaks;
		clone.CAS = CAS;
		clone.name = name;
		clone.names = names == null ? null : names.clone();
		clone.spectrum = spectrum == null ? null : spectrum.clone();
		clone.row = row == null ? null : row.clone();
		clone._distValue = _distValue;
		return clone;
	}

	public String[] getNames() {
		return names;
	}

	public AlignmentRow reArrange(int newIndices[]) {
		if (newIndices == null) {
			throw new IllegalArgumentException("Index array was null!");
		}
		if (newIndices.length != row.length) {
			throw new IllegalArgumentException("New index arrays length (" +
					newIndices.length + ") was not " + row.length + ".");
		}
		AlignmentRow newRow = clone();
		for (int i = 0; i < newIndices.length; i++) {
			int newIx = newIndices[i];
			if (newIx < 0) {
				throw new IllegalArgumentException("New index was less than 0 (" +
						newIx + ")");
			} else if (newIx >= newIndices.length) {
				throw new IllegalArgumentException("New index was too large (" +
						newIx + ")");
			} else {
				newRow.row[newIx] = row[i];
			}
		}
		return newRow;
	}

	private void calculateMeans() {
		rt1mean = 0;
		rt2mean = 0;
		int count = 0;

		for (GCGCDatum d : row) {
			if (d != null) {
				rt1mean += d.getRT1();
				rt2mean += d.getRT2();
				rtimean += d.getRTI();
				count++;
			}
		}
		rt1mean /= count;
		rt2mean /= count;
		rtimean /= count;
		modified = false;
	}

	public GCGCDatum[] getRow() {
		return (row == null) ? new GCGCDatum[0] : row.clone();
	}

	public GCGCDatum getDatum(int ix) {
		return row[ix];
	}

	public int compareTo(AlignmentRow o) {
		int comparison = 0;
		if (rt1mean < o.rt1mean) {
			comparison = -1;
		} else if (rt1mean > o.rt1mean) {
			comparison = 1;
		} else {
			if (rt2mean < o.rt2mean) {
				comparison = -1;
			} else if (rt2mean > o.rt2mean) {
				comparison = 1;
			}
		}
		return comparison;
	}

	public double getMeanRT1() {
		if (modified) {
			calculateMeans();
		}
		return rt1mean;
	}

	public String getCAS() {
		return CAS;
	}

	public String getName() {
		return name;
	}

	public double getMeanRT2() {
		if (modified) {
			calculateMeans();
		}
		return rt2mean;
	}

	public double getMeanRTI() {
		if (modified) {
			calculateMeans();
		}
		return rtimean;
	}

	public double getMeanSimilarity() {
		return similarityMean;
	}

	public double getSimilarityStdDev() {
		return similarityStdDev;
	}

	public double getMaxSimilarity() {
		return maxSimilarity;
	}

	public double getQuantMass() {
		return quantMass;
	}

	public int nonNullPeakCount() {
		return nonNullPeaks;
	}

	/**
	 * Calls toString method of each peak in this row. If peak is null, write \t instead
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\t').append(getMeanRT1());
		sb.append('\t').append(getMeanRT2()).append('\t');
		for (GCGCDatum d : row) {
			if (d != null) {
				if (d.useConcentration()) {
					sb.append(d.getConcentration());
				} else {
					sb.append(d.getArea());
				}
			} else {
				sb.append("NA");
			}
			sb.append('\t');
		}
		return sb.toString();
	}

	public int length() {
		return row == null ? 0 : row.length;
	}

	public Spectrum getSpectrum() {
		return spectrum.clone();
	}

	public AlignmentRow setDistValue(DistValue val) {
		AlignmentRow c = clone();
		c._distValue = val;
		return c;
	}

	public DistValue getDistValue() {
		return _distValue;
	}

	public AlignmentRow scaleArea(double[] scalings) {
		AlignmentRow c = clone();
		for (int i = 0; i < row.length; i++) {
			c.row[i] = row[i].setArea(row[i].getArea() * scalings[i]);
		}
		return c;
	}

	/**
	 * Doesn't support remove.
	 */
	public Iterator<GCGCDatum> iterator() {
		class Iter implements Iterator<GCGCDatum> {

			private GCGCDatum ownArray[];
			private int index;
			private boolean iterationFinished;

			public Iter(GCGCDatum[] row) {
				ownArray = row;
				if (row == null || row.length == 0) {
					iterationFinished = true;
				} else {
					iterationFinished = false;
					index = 0;
				}
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext() {
				return !iterationFinished;
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#next()
			 */
			public GCGCDatum next() {
				if (iterationFinished) {
					throw new NoSuchElementException();
				} else {
					GCGCDatum returned = ownArray[index++];
					if (index >= ownArray.length) {
						iterationFinished = true;
					}
					return returned;
				}
			}

			/* (non-Javadoc)
			 * @see java.util.Iterator#remove()
			 */
			public void remove() {
				throw new UnsupportedOperationException("Remove not supported");
			}
		}
		return new Iter(row);
	}
}
