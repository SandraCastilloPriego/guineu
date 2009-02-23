/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.data.impl;

import guineu.data.PeakListRow_concatenate;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;


/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowConcatenate implements PeakListRow_concatenate {

    private String FAComposition, allNames, Name;
    private double averageMZ,  averageRT,  numFound;
    private int standard,  lipidClass,  ID,  aligment;
    private boolean control;
    private Hashtable<String, String> peaks;    
    

    public SimplePeakListRowConcatenate(int ID, double averageMZ, double averageRT, double numFound,
            int standard, int lipidClass, String Name, String identity, String FAComposition) {
        this.ID = ID;
        this.FAComposition = FAComposition;
        this.averageMZ = averageMZ;
        this.averageRT = averageRT;
        this.numFound = numFound;
        this.standard = standard;
        this.lipidClass = lipidClass;
        this.Name = Name;
        this.allNames = identity;
        this.peaks = new Hashtable<String, String>();
        this.aligment = -1;
    }

    public SimplePeakListRowConcatenate() {
        this.peaks = new Hashtable<String, String>();
        this.ID = -1;
        this.aligment = -1;
    }

    @Override
    public PeakListRow_concatenate clone() {
        PeakListRow_concatenate peakListRow = new SimplePeakListRowConcatenate(this.ID, this.averageMZ, this.averageRT,
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

    public int getLipidClass() {
        return this.lipidClass;
    }

    public String getName() {
        return this.Name;
    }  
    
    public String getPeak(String ExperimentName) {
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

    public void setLipidClass(int lipidClass) {
        this.lipidClass = lipidClass;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setAllNames(String allNames) {
        this.allNames = allNames;
    }

    public void setPeak(String name, String value) {
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
        return this.allNames;
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

   
}
