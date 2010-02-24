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
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.modules.mylly.datastruct.Spectrum;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowGCGC implements Comparable<PeakListRow>, PeakListRow {

    private final static Comparator<Entry<String[], Integer>> comp = new Comparator<Entry<String[], Integer>>() {

        public int compare(Entry<String[], Integer> o1, Entry<String[], Integer> o2) {
            int comparison = o2.getValue() - o1.getValue();
            if (comparison == 0) {
                comparison = o1.getKey()[0].compareTo(o2.getKey()[0]);
            }
            return comparison;
        }
    };
    private int ID;
    private double RT1 = 0.0,  RT2 = 0.0,  RTI = 0.0,  maxSimilarity = 0,  meanSimilarity = 0,  similaritySTDDev = 0,  mass = 0;
    private String name,  allNames,  spectra,  pubChemID,  molClass;
    private boolean control,  selection = false;
    private String CAS;
    private Spectrum spectrum;
    private List<GCGCDatum> row;
    private String[] names;
    private String CASnumbers[];
    private DistValue _distValue;
    private double numFound = 0;

    public SimplePeakListRowGCGC(int ID, double RT1, double RT2, double RTI,
            double maxSimilarity, double meanSimilarity, double similaritySTDDev,
            double numFound, double mass, DistValue _distValue, String name,
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
        this.setDistValue(_distValue);
        this.name = name;
        this.allNames = allNames;
        this.spectra = spectra;
        this.pubChemID = pubChemID;
        this.control = true;
        this.CAS = CAS;
    }

    public boolean useConcentrations() {
        return this.row.get(0).useConcentration();
    }

    public SimplePeakListRowGCGC() {
        this.ID = -1;
        this.control = true;
        this.row = new ArrayList<GCGCDatum>();
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
            if (datum.getColumnName().compareTo(ExperimentName) == 0) {
                if (datum.useConcentration() && datum.getConcentration() != 0.0) {
                    return datum.getConcentration();
                } else {
                    return datum.getArea();
                }
            }
        }
        return 0.0;
    }

    public void setPeak(String experimentName, Double value) {
        boolean isFound = false;

        for (int i = 0; i < this.row.size(); i++) {
            GCGCDatum datum = this.row.get(i);
            if (datum != null) {
                if (datum.getColumnName() != null && datum.getColumnName().matches(experimentName)) {
                    if (datum.useConcentration()) {
                        datum.setConcentration(value);
                    } else {
                        datum.setArea(value);
                    }
                    isFound = true;
                    break;
                }
            }
        }
        if (!isFound) {
            GCGCDatum datum2 = new GCGCDatum(0, this.RT1, this.RT2, this.RTI,
                    value, value, true, 0, CAS, name, experimentName, null);

            this.row.add(datum2);
        }


    }

    public boolean getControl() {
        return this.control;
    }

    public void getControl(boolean control) {
        this.control = control;
    }

    public int getNumberPeaks() {
        return this.row.size();
    }

    public void setName(String Name) {
        this.name = Name;
    }

    public void setMolClass(String molClass) {
        this.molClass = molClass;
    }

    @Override
    public PeakListRow clone() {
        PeakListRow newPeakListRow = new SimplePeakListRowGCGC(ID, RT1, RT2, RTI,
                maxSimilarity, meanSimilarity, similaritySTDDev,
                numFound, mass, _distValue, name, allNames, spectra, pubChemID, CAS);

        ((SimplePeakListRowGCGC) newPeakListRow).numFound = numFound;
        ((SimplePeakListRowGCGC) newPeakListRow).names = names == null ? null : names.clone();
        ((SimplePeakListRowGCGC) newPeakListRow).spectrum = spectrum == null ? null : spectrum.clone();
        List<GCGCDatum> clonedRow = new ArrayList<GCGCDatum>();
        for (GCGCDatum datum : row) {
            clonedRow.add(datum.clone());
        }
        ((SimplePeakListRowGCGC) newPeakListRow).row = clonedRow;
        ((SimplePeakListRowGCGC) newPeakListRow).setDistValue(_distValue);
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
        try {
            return this._distValue.distance();
        } catch (Exception e) {
            return 0;
        }
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
        this._distValue.setDistance(difference);
    }

    public void setSpectrumString(String spectra) {
        this.spectra = spectra;
        Spectrum newSpectrum = new Spectrum(spectra);
        this.spectrum = newSpectrum;
    }

    public String getMolClass() {
        return this.molClass;
    }

    public void removePeaks() {
        this.row.removeAll(row);
    }

    public Double[] getPeaks() {
        Double[] aPeaks = new Double[this.row.size()];
        int cont = 0;
        for (GCGCDatum datum : this.row) {
            if (datum.useConcentration() && datum.getConcentration() != 0.0) {
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

    public boolean isSelected() {
        return this.selection;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selection = selectionMode;
    }

    public void setPeak(String str, String get) {
    }

    public void removeNoSamplePeaks(String[] group) {
        for (GCGCDatum datum : this.row) {
            for (String experimentName : group) {
                if (datum.getName().compareTo(experimentName) == 0) {
                    this.row.remove(datum);
                }
            }
        }

    }

    public SimplePeakListRowGCGC(AlignmentPath p) {
        this.ID = 0;
        this.RT1 = p.getRT1();
        this.RT2 = p.getRT2();
        this.RTI = p.getRTI();
        this.mass = p.getQuantMass();
        this.meanSimilarity = p.getMeanSimilarity();
        this.similaritySTDDev = p.getSimilarityStdDev();
        this.maxSimilarity = p.getMaxSimilarity();
        row = new ArrayList<GCGCDatum>();
        CAS = p.getCAS();
        if (CAS == null) {
            CAS = "0-00-0";
        }
        Map<String[], Integer> nameFrequencies = new HashMap<String[], Integer>();

        for (int i = 0; i < p.length(); i++) {
            GCGCDatum d = p.getPeak(i);
            if (d != null) {
                row.add(d);
                numFound++;
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
        _distValue = new DistValue(0);
    }

    public String[] getNames() {
        return names;
    }

    public List<GCGCDatum> getDatumArray() {
        return row;
    }

    public GCGCDatum getDatum(int ix) {
        return row.get(ix);
    }

    public int compareTo(PeakListRow o) {
        int comparison = 0;
        if (this.RT1 < (Double) o.getVar("RT1")) {
            comparison = -1;
        } else if (this.RT1 > (Double) o.getVar("RT1")) {
            comparison = 1;
        } else {
            if (this.RT2 < (Double) o.getVar("RT2")) {
                comparison = -1;
            } else if (this.RT2 > (Double) o.getVar("RT2")) {
                comparison = 1;
            }
        }
        return comparison;
    }

    public double nonNullPeakCount() {
        return numFound;
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
        return row == null ? 0 : row.size();
    }

    public Spectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(Spectrum spectrum) {
        this.spectrum = spectrum;
    }

    public void setDistValue(DistValue val) {
        _distValue = val;
    }

    public DistValue getDistValue() {
        return _distValue;
    }

    public void scaleArea(double[] scalings) {
        if (!this.useConcentrations() || this.mass == -1) {
            for (int i = 0; i < row.size(); i++) {
                this.row.get(i).setArea(row.get(i).getArea() * scalings[i]);
            }
        }
    }

    public void setDatum(GCGCDatum[] rows) {
        this.row = new ArrayList<GCGCDatum>();
        for (GCGCDatum datum : rows) {
            this.row.add(datum);
        }
    }

    /**
     * Doesn't support remove.
     */
    public Iterator<GCGCDatum> iterator() {
        class Iter implements Iterator<GCGCDatum> {

            private List<GCGCDatum> ownArray;
            private int index;
            private boolean iterationFinished;

            public Iter(List<GCGCDatum> row) {
                ownArray = row;
                if (row == null || row.size() == 0) {
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
                    GCGCDatum returned = ownArray.get(index++);
                    if (index >= ownArray.size()) {
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

    public Object getVar(String varName) {
        try {
            Method m = this.getClass().getMethod(varName, new Class[]{});
            return m.invoke(this);

        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setVar(String varName, Object value) {
        try {
            if (value != null) {
                Class partypes[] = new Class[1];
                if (value.getClass().toString().contains("Double")) {
                    partypes[0] = Double.TYPE;
                } else if (value.getClass().toString().contains("Integer")) {
                    partypes[0] = Integer.TYPE;
                } else if (value.getClass().toString().contains("String")) {
                    partypes[0] = String.class;
                } else if (value.getClass().toString().contains("Boolean")) {
                    partypes[0] = Boolean.TYPE;
                } else {
                    partypes[0] = Object.class;
                }
                System.out.println(partypes[0] + " - " + varName + " - " + value);
                Method m = this.getClass().getMethod(varName, partypes);
                Object[] parameters = new Object[1];
                parameters[0] = value;
                m.invoke(this, parameters);
            }
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(SimplePeakListRowGCGC.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
