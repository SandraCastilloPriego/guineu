/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.data;

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

    public int getLipidClass();

    public String getFAComposition();

    public String getName();

    public void setPeak(String name, Double value);

    public Object getPeak(String ExperimentName);
    
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

    public double getMass();

    public double getDifference();

    public String getSpectrum();
}
