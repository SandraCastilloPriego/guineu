/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
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
