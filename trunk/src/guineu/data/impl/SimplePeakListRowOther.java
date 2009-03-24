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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowOther implements PeakListRow {

    private int ID;
    private boolean control, selection;
    private Hashtable<String, String> peaks;

    public SimplePeakListRowOther() {
        this.peaks = new Hashtable<String, String>();
        this.ID = -1;
    }

    @Override
    public PeakListRow clone() {
        PeakListRow peakListRow = new SimplePeakListRowOther();
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getRT() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getNumFound() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getStandard() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getLipidClass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPeak(String ExperimentName) {
        return this.peaks.get(ExperimentName);
    }

    public void setName(String Name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setAllNames(String allNames) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setPeak(String name, String value) {
        this.peaks.put(name, value);
    }

    public String getFAComposition() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setFAComposition(String FAComposition) {
        throw new UnsupportedOperationException("Not supported yet.");
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNumberAligment(int aligment) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getRT1() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getRT2() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getRTI() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getMaxSimilarity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getMeanSimilarity() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getSimilaritySTDDev() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getAllNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getPubChemID() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getMass() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getDifference() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getSpectrum() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removePeaks() {
        this.peaks = new Hashtable<String, String>();
    }

    public String[] getPeaks() {
        String[] aPeaks = new String[this.peaks.size()];
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

    public void setPeak(String name, Double value) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object getPeak(int col, Vector<String> sampleNames) {
        return this.peaks.get(sampleNames.elementAt(col));
    }

    public int getNumberFixColumns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNumberFixColumns(int columnNum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isSelected() {
        return this.selection;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selection = selectionMode;
    }

	public void removeNoSamplePeaks(String[] group) {
		for (String name : group) {
			if (this.peaks.containsKey(name)) {
				this.peaks.remove(name);
			}
		}
	}
}
