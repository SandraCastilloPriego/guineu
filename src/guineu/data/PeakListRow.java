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
package guineu.data;

import guineu.modules.mylly.alignment.scoreAligner.functions.DistValue;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public interface PeakListRow {

    public int getID();

    public double getMZ();

    public double getRT();

    public double getNumFound();

    public int getStandard();

    public String getMolClass();

    public String getFAComposition();

    public String getName();

	public void removeNoSamplePeaks(String[] group);

    public void setID(int i);

    public void setPeak(String name, Double value);

    public Object getPeak(String ExperimentName);

    public Object getPeak(int col, Vector<String> sampleNames);

    public Object[] getPeaks();

    public void removePeaks();

    public boolean getControl();

    public int getNumberPeaks();

    public int getNumberAlignment();

    public void setName(String Name);

    public void setNumberAligment(int aligment);

    public void setAllNames(String Names);

    public PeakListRow clone();

    public double getRT1();

    public double getRT2();

    public double getRTI();

    public double getMaxSimilarity();

    public double getMeanSimilarity();

    public double getSimilaritySTDDev();

    public String getAllNames();

    public String getPubChemID();

	public void setPubChemID(String pubchemID);

    public double getMass();

    public double getDifference();

    public String getSpectrumString();

    public int getNumberFixColumns();

    public void setNumberFixColumns(int columnNum);

    public boolean isSelected();

    public void setPeak(String str, String get);

    public void setSelectionMode(boolean selectionMode);

	public String getCAS();

	public void setCAS(String CAS);

	public int nonNullPeakCount();

	public DistValue getDistValue();

	public List<GCGCDatum> getRow();

	public String getVTTID();

	public void setVTTD(String VTTID);

	public String getAllVTTID();

	public void setAllVTTD(String AllVTTID);
}
