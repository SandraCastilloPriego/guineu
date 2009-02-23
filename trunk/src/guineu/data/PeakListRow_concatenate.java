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

import guineu.data.*;

/**
 *
 * @author SCSANDRA
 */
public interface PeakListRow_concatenate extends PeakListRow{

    public int getID();

    public double getMZ();

    public double getRT();

    public double getNumFound();

    public int getStandard();

    public int getLipidClass();

    public String getFAComposition();

    public String getName();

    public void setPeak(String name, String value);

    public String getPeak(String ExperimentName);
    
    public String[] getPeaks();

    public void removePeaks();

    public boolean getControl();

    public int getNumberPeaks();

    public int getNumberAlignment();

    public void setName(String Name);

    public void setNumberAligment(int aligment);
    
    public void setAllNames(String Names);

    public PeakListRow_concatenate clone();

    public double getRT1();

    public double getRT2();

    public double getRTI();

    public double getMaxSimilarity();

    public double getMeanSimilarity();

    public double getSimilaritySTDDev();

    public String getAllNames();

    public String getPubChemID();

    public double getMass();

    public double getDifference();

    public String getSpectrum();
}
