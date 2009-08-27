/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.data.impl;

import guineu.data.PeakListRow;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentPath;
import guineu.modules.mylly.alignment.scoreAligner.functions.DistValue;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.Spectrum;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowGCGC implements Comparable<SimplePeakListRowGCGC>, Iterable<GCGCDatum>, Cloneable, PeakListRow {

	private final static Comparator<Entry<String[], Integer>> comp = new Comparator<Entry<String[], Integer>>() {

		public int compare(Entry<String[], Integer> o1, Entry<String[], Integer> o2) {
			int comparison = o2.getValue() - o1.getValue();
			if (comparison == 0) {
				comparison = o1.getKey()[0].compareTo(o2.getKey()[0]);
			}
			return comparison;
		}
	};
	private int ID,  numberFixColumns;
	private double RT1,  RT2,  RTI,  maxSimilarity,  meanSimilarity,  similaritySTDDev,  numFound,  mass,  difference;
	private String name,  allNames,  spectra,  pubChemID;
	private boolean control,  selection;
	private String CAS;
	private Spectrum spectrum;
	private GCGCDatum row[];
	private String[] names;
	private String CASnumbers[];
	private DistValue _distValue;
	private boolean modified;
	private int nonNullPeaks = 0;

	public SimplePeakListRowGCGC(int ID, double RT1, double RT2, double RTI,
			double maxSimilarity, double meanSimilarity, double similaritySTDDev,
			double numFound, double mass, double difference, String name,
			String allNames, String spectra, String pubChemID, String CAS) {
		this.ID = ID;
		this.RT1 = RT1;
		this.RT2 = RT2;
		this.RTI = RTI;
		this.maxSimilarity = maxSimilarity;
		this.meanSimilarity = meanSimilarity;
		this.similaritySTDDev = similaritySTDDev;
		this.numFound = numFound;
		this.mass = mass;
		this.difference = difference;
		this.name = name;
		this.allNames = allNames;
		this.spectra = spectra;
		this.pubChemID = pubChemID;
		this.control = true;
		this.numberFixColumns = 15;
		this.CAS = CAS;
	}

	public SimplePeakListRowGCGC() {
		this.ID = -1;
		this.control = true;
	}

	public int getID() {
		return this.ID;
	}

	public double getNumFound() {
		return this.numFound;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setNumFound(double numFound) {
		this.numFound = numFound;
	}

	public String getName() {
		return this.name;
	}

	public String getCAS() {
		return this.CAS;
	}

	public void setCAS(String CAS) {
		this.CAS = CAS;
	}

	public Double getPeak(String ExperimentName) {
		for (GCGCDatum datum : this.row) {			
			if (datum.getColumnName().compareTo(ExperimentName) ==0) {
				if (datum.useConcentration()&& datum.getConcentration()!= 0.0) {
					return datum.getConcentration();
				} else {
					return datum.getArea();
				}
			}
		}
		return 0.0;
	}

	public void setPeak(String experimentName, Double value) {
		for (GCGCDatum datum : this.row) {
			if (datum.getName().matches(experimentName)) {
				if (datum.useConcentration()) {
					datum.setConcentration(value);
				} else {
					datum.setArea(value);
				}
			}
		}
	}

	public boolean getControl() {
		return this.control;
	}

	public void getControl(boolean control) {
		this.control = control;
	}

	public int getNumberPeaks() {
		return this.row.length;
	}

	public void setName(String Name) {
		this.name = Name;
	}

	@Override
	public PeakListRow clone() {
		PeakListRow newPeakListRow = new SimplePeakListRowGCGC(this.ID, this.RT1, this.RT2, this.RTI,
				this.maxSimilarity, this.meanSimilarity, this.similaritySTDDev,
				this.numFound, this.mass, this.difference, this.name,
				this.allNames, this.spectra, this.pubChemID, this.CAS);

		((SimplePeakListRowGCGC) newPeakListRow).modified = modified;
		((SimplePeakListRowGCGC) newPeakListRow).nonNullPeaks = nonNullPeaks;
		((SimplePeakListRowGCGC) newPeakListRow).names = names == null ? null : names.clone();
		((SimplePeakListRowGCGC) newPeakListRow).spectrum = spectrum == null ? null : spectrum.clone();
		((SimplePeakListRowGCGC) newPeakListRow).row = row == null ? null : row.clone();
		((SimplePeakListRowGCGC) newPeakListRow)._distValue = _distValue;
		((SimplePeakListRowGCGC) newPeakListRow).row = this.row.clone();

		return newPeakListRow;

	}

	public double getRT1() {
		return this.RT1;
	}

	public double getRT2() {
		return this.RT2;
	}

	public double getRTI() {
		return this.RTI;
	}

	public double getMaxSimilarity() {
		return this.maxSimilarity;
	}

	public double getMeanSimilarity() {
		return this.meanSimilarity;
	}

	public double getSimilaritySTDDev() {
		return this.similaritySTDDev;
	}

	public String getAllNames() {
		return this.allNames;
	}

	public String getPubChemID() {
		return this.pubChemID;
	}

	public double getMass() {
		return this.mass;
	}

	public double getDifference() {
		return this.difference;
	}

	public String getSpectrumString() {
		return this.spectra;
	}

	public void setRT1(double RT1) {
		this.RT1 = RT1;
	}

	public void setRT2(double RT2) {
		this.RT2 = RT2;
	}

	public void setRTI(double RTI) {
		this.RTI = RTI;
	}

	public void setMaxSimilarity(double maxSimilarity) {
		this.maxSimilarity = maxSimilarity;
	}

	public void setMeanSimilarity(double meanSimilarity) {
		this.meanSimilarity = meanSimilarity;
	}

	public void setSimilaritySTDDev(double similaritySTDDev) {
		this.similaritySTDDev = similaritySTDDev;
	}

	public void setAllNames(String allNames) {
		this.allNames = allNames;
	}

	public void setPubChemID(String pubChemID) {
		this.pubChemID = pubChemID;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}

	public void setDifference(double difference) {
		this.difference = difference;
	}

	public void setSpectrum(String spectra) {
		this.spectra = spectra;
	}

	public double getMZ() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public double getRT() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getStandard() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getLipidClass() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getFAComposition() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public String getIdentity() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int getNumberAlignment() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void setNumberAligment(int aligment) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void removePeaks() {
		this.row = new GCGCDatum[1];
	}

	public Double[] getPeaks() {
		Double[] aPeaks = new Double[this.row.length];
		int cont = 0;
		for (GCGCDatum datum : this.row) {
			if (datum.useConcentration() && datum.getConcentration()!= 0.0) {
				aPeaks[cont++] = datum.getConcentration();
			} else {
				aPeaks[cont++] = datum.getArea();
			}
		}
		return aPeaks;
	}

	public Object getPeak(int col, Vector<String> sampleNames) {
		return this.getPeak(sampleNames.elementAt(col));
	}

	public int getNumberFixColumns() {
		return this.numberFixColumns;
	}

	public void setNumberFixColumns(int columnNum) {
		this.numberFixColumns = columnNum;
	}

	public boolean isSelected() {
		return this.selection;
	}

	public void setSelectionMode(boolean selectionMode) {
		this.selection = selectionMode;
	}

	public void setPeak(String str, String get) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void removeNoSamplePeaks(String[] group) {
		for (GCGCDatum datum : this.row) {
			for (String experimentName : group) {
				if (datum.getName().compareTo(experimentName) == 0) {
					datum = null;
				}
			}
		}

	}

	public SimplePeakListRowGCGC(AlignmentPath p) {
		this.RT1 = p.getRT1();
		this.RT2 = p.getRT2();
		this.RTI = p.getRTI();
		this.mass = p.getQuantMass();
		this.meanSimilarity = p.getMeanSimilarity();
		this.similaritySTDDev = p.getSimilarityStdDev();
		this.maxSimilarity = p.getMaxSimilarity();
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
		this.allNames = "";
		for (int i = 0; i < names.length; i++) {
			if (!GCGCDatum.UNKOWN_NAME.equals(names[i])) {
				if (!this.allNames.isEmpty()) {
					this.allNames += " \\\\ ";
				}
				this.allNames += names[i];
			}
		}

		this.spectrum = p.getSpectrum() == null ? null : p.getSpectrum().clone();
		this.spectra = this.spectrum.toString();
		_distValue = DistValue.getNull();
	}

	public String[] getNames() {
		return names;
	}

	public SimplePeakListRowGCGC reArrange(int newIndices[]) {
		if (newIndices == null) {
			throw new IllegalArgumentException("Index array was null!");
		}
		if (newIndices.length != row.length) {
			throw new IllegalArgumentException("New index arrays length (" +
					newIndices.length + ") was not " + row.length + ".");
		}
		SimplePeakListRowGCGC newRow = (SimplePeakListRowGCGC) clone();
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

	public GCGCDatum[] getRow() {
		return (row == null) ? new GCGCDatum[0] : row.clone();
	}

	public GCGCDatum getDatum(int ix) {
		return row[ix];
	}

	public int compareTo(SimplePeakListRowGCGC o) {
		int comparison = 0;
		if (this.RT1 < o.RT1) {
			comparison = -1;
		} else if (this.RT1 > o.RT1) {
			comparison = 1;
		} else {
			if (this.RT2 < o.RT2) {
				comparison = -1;
			} else if (this.RT2 > o.RT2) {
				comparison = 1;
			}
		}
		return comparison;
	}

	public int nonNullPeakCount() {
		return nonNullPeaks;
	}

	/**
	 * Calls toString method of each peak in this row. If peak is null, write \t instead
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('\t').append(getRT1());
		sb.append('\t').append(getRT2()).append('\t');
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

	public SimplePeakListRowGCGC setDistValue(DistValue val) {
		SimplePeakListRowGCGC c = (SimplePeakListRowGCGC) clone();
		c._distValue = val;
		return c;
	}

	public DistValue getDistValue() {
		return _distValue;
	}

	public SimplePeakListRowGCGC scaleArea(double[] scalings) {
		SimplePeakListRowGCGC c = (SimplePeakListRowGCGC) clone();
		for (int i = 0; i < row.length; i++) {
			c.row[i] = row[i].setArea(row[i].getArea() * scalings[i]);
		}
		return c;
	}

	public void setDatum(GCGCDatum[] rows){
		this.row = rows;
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
