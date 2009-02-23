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

/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowGCGC implements PeakListRow {

    private int ID;
    private double RT1,  RT2,  RTI,  maxSimilarity,  meanSimilarity,  similaritySTDDev,  numFound,  mass,  difference;
    private String name,  allNames,  spectra,  pubChemID;
    private Hashtable<String, Double> peaks;
    private boolean control;

    public SimplePeakListRowGCGC(int ID, double RT1, double RT2, double RTI,
            double maxSimilarity, double meanSimilarity, double similaritySTDDev,
            double numFound, double mass, double difference, String name,
            String allNames, String spectra, String pubChemID) {
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
        this.peaks = new Hashtable<String, Double>();
    }

    public SimplePeakListRowGCGC() {
        this.ID = -1;
        this.control = true;
        this.peaks = new Hashtable<String, Double>();
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

    public Double getPeak(String ExperimentName) {
        return this.peaks.get(ExperimentName);
    }

    public void setPeak(String experimentName, Double value) {
        this.peaks.put(experimentName, value);
    }

    public boolean getControl() {
        return this.control;
    }

    public void getControl(boolean control) {
        this.control = control;
    }

    public int getNumberPeaks() {
        return this.peaks.size();
    }

    public void setName(String Name) {
        this.name = Name;
    }

    @Override
    public PeakListRow clone() {
        PeakListRow newPeakListRow = new SimplePeakListRowGCGC(this.ID, this.RT1, this.RT2, this.RTI,
                this.maxSimilarity, this.meanSimilarity, this.similaritySTDDev,
                this.numFound, this.mass, this.difference, this.name,
                this.allNames, this.spectra, this.pubChemID);
        String str;
        Set<String> set = peaks.keySet();

        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            str = itr.next();
            newPeakListRow.setPeak(str, peaks.get(str));
        }
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

    public String getSpectrum() {
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
}
