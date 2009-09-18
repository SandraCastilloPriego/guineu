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

   
    public double getNumFound() {
        return -1;
    }

    public String getName() {
        return null;
    }

    public String getPeak(String ExperimentName) {
        return this.peaks.get(ExperimentName);
    }

    public void setName(String Name) {
        
    }

    public void setAllNames(String allNames) {
       
    }

    public void setPeak(String name, String value) {
        this.peaks.put(name, value);
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
        return -1;
    }

    public void setNumberAligment(int aligment) {
       
    }

   
    public String getAllNames() {
        return null;
    }

    public String getPubChemID() {
        return null;
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
        
    }

    public Object getPeak(int col, Vector<String> sampleNames) {
        return this.peaks.get(sampleNames.elementAt(col));
    }

    public int getNumberFixColumns() {
        return -1;
    }

    public void setNumberFixColumns(int columnNum) {
        
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

	public double getMZ() {
		return -1;
	}

	public double getRT() {
		return -1;
	}

	public int getStandard() {
		return -1;
	}

	public String getMolClass() {
		return null;
	}

	public String getFAComposition() {
		return null;
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
}
