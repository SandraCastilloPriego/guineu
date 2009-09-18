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
import guineu.modules.mylly.alignment.scoreAligner.functions.DistValue;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowLCMS implements PeakListRow {

	private String FAComposition,  allNames,  Name, lipidClass ="0";
	private double averageMZ,  averageRT,  numFound;
	private int standard, ID,  aligment,  numFixColumns;
	private boolean control,  selection;
	private String VTTid, VTTAllIDs;
	private Hashtable<String, Double> peaks;
	private String pubchemID;

	public SimplePeakListRowLCMS(int ID, double averageMZ, double averageRT, double numFound,
			int standard, String lipidClass, String Name, String identity, String FAComposition) {
		this.ID = ID;
		this.FAComposition = FAComposition;
		this.averageMZ = averageMZ;
		this.averageRT = averageRT;
		this.numFound = numFound;
		this.standard = standard;
		this.lipidClass = String.valueOf(lipidClass);
		this.Name = Name;
		this.allNames = identity;
		this.peaks = new Hashtable<String, Double>();
		this.aligment = -1;
		this.numFixColumns = 11;
	}

	public SimplePeakListRowLCMS() {
		this.peaks = new Hashtable<String, Double>();
		this.ID = -1;
		this.aligment = -1;
	}

	@Override
	public PeakListRow clone() {
		PeakListRow peakListRow = new SimplePeakListRowLCMS(this.ID, this.averageMZ, this.averageRT,
				this.numFound, this.standard, this.lipidClass, this.Name, this.allNames,
				this.FAComposition);
		peakListRow.setNumberAligment(aligment);
		String str;
		Set<String> set = peaks.keySet();

		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			str = itr.next();
			peakListRow.setPeak(str, peaks.get(str));
		}
		return peakListRow;
	}

	public double getMZ() {
		return this.averageMZ;
	}

	public double getRT() {
		return this.averageRT;
	}

	public double getNumFound() {
		return this.numFound;
	}

	public int getStandard() {
		return this.standard;
	}

	public String getMolClass() {
		return this.lipidClass;
	}

	public String getName() {
		return this.Name;
	}

	public Double getPeak(String ExperimentName) {
		return this.peaks.get(ExperimentName);
	}

	public void setMZ(double averageMZ) {
		this.averageMZ = averageMZ;
	}

	public void setRT(double averageRT) {
		this.averageRT = averageRT;
	}

	public void setNumFound(double numFound) {
		this.numFound = numFound;
	}

	public void setStandard(int standard) {
		this.standard = standard;
	}

	public void setLipidClass(String lipidClass) {
		this.lipidClass = lipidClass;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public void setAllNames(String allNames) {
		this.allNames = allNames;
	}

	public void setPeak(String name, Double value) {
		this.peaks.put(name, value);
	}

	public String getFAComposition() {
		return this.FAComposition;
	}

	public void setFAComposition(String FAComposition) {
		this.FAComposition = FAComposition;
	}

	public int getID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public void setControl(boolean control) {
		this.control = control;
	}

	public boolean getControl() {
		return this.control;
	}

	public int getNumberPeaks() {
		return this.peaks.size();
	}

	public int getNumberAlignment() {
		return this.aligment;
	}

	public void setNumberAlignment(int aligment) {
		this.aligment = aligment;
	}

	public void setNumberAligment(int aligment) {
		this.aligment = aligment;
	}

	public String getAllNames() {
		return this.allNames;
	}

	public String getPubChemID() {
		return this.pubchemID;
	}
	
	public void removePeaks() {
		this.peaks = new Hashtable<String, Double>();
	}

	public Double[] getPeaks() {
		Double[] aPeaks = new Double[this.peaks.size()];
		String str;
		Set<String> set = peaks.keySet();
		int cont = 0;
		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			str = itr.next();
			aPeaks[cont++] = peaks.get(str);
		}
		return aPeaks;
	}

	public Object getPeak(int col, Vector<String> sampleNames) {
		return this.peaks.get(sampleNames.elementAt(col));
	}

	public int getNumberFixColumns() {
		return this.numFixColumns;
	}

	public void setNumberFixColumns(int columnNum) {
		this.numFixColumns = columnNum;
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
		for (String name : group) {
			if (this.peaks.containsKey(name)) {
				this.peaks.remove(name);
			}
		}
	}

	public double getRT1() {
		return -1;
	}

	public double getRT2() {
		return -1;
	}

	public double getRTI() {
		return -1;
	}

	public double getMaxSimilarity() {
		return -1;
	}

	public double getMeanSimilarity() {
		return -1;
	}

	public double getSimilaritySTDDev() {
		return -1;
	}

	public double getMass() {
		return -1;
	}

	public double getDifference() {
		return -1;
	}

	public String getSpectrumString() {
		return null;
	}

	public String getCAS() {
		return null;
	}

	public void setCAS(String CAS) {
		
	}

	public int nonNullPeakCount() {
		return -1;
	}

	public DistValue getDistValue() {
		return null;
	}

	public List<GCGCDatum> getRow() {
		return null;
	}

	public String getVTTID() {
		return this.VTTid;
	}

	public void setVTTD(String VTTID) {
		this.VTTid = VTTID;
	}

	public String getAllVTTID() {
		return this.VTTAllIDs;
	}

	public void setAllVTTD(String AllVTTIDs) {
		this.VTTAllIDs = AllVTTIDs;
	}

	public void setPubChemID(String pubchemID) {
		this.pubchemID = pubchemID;
	}
	
}
