/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.data.impl.peaklists;


import guineu.data.PeakListRow;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SCSANDRA
 */
public class SimplePeakListRowExpression implements PeakListRow {

    private int ID;
    private boolean control, selection;
    private Hashtable<String, Double> peaks;
    private Hashtable<String, String> peaksString;
    private Hashtable<String, Object> metaData;

    public SimplePeakListRowExpression() {
        this.peaks = new Hashtable<String, Double>();
        this.peaksString = new Hashtable<String, String>();
        this.metaData = new Hashtable<String, Object>();
        this.ID = -1;
    }

    @Override
    public PeakListRow clone() {
        PeakListRow peakListRow = new SimplePeakListRowExpression();

        // Copy peaks
        String str;
        Set<String> set = peaks.keySet();
        Iterator<String> itr = set.iterator();
        while (itr.hasNext()) {
            str = itr.next();
            peakListRow.setPeak(str, peaks.get(str));
        }

        // Copy String peaks
        set = peaksString.keySet();
        itr = set.iterator();
        while (itr.hasNext()) {
            str = itr.next();
            peakListRow.setPeak(str, peaksString.get(str));
        }

        // Copy metadata
        set = metaData.keySet();
        itr = set.iterator();
        while (itr.hasNext()) {
            str = itr.next();
            ((SimplePeakListRowExpression)peakListRow).setMetaData(str, metaData.get(str));
        }

        return peakListRow;
    }

    public Hashtable<String, Object> getMetaData() {
        return this.metaData;
    }

    public Object getMetaData(String columnName){
        if(this.metaData.containsKey(columnName)){
            return this.metaData.get(columnName);
        }else{
            return null;
        }
    }

    public void setMetaData(String columnName, Object value){
        this.metaData.put(columnName, value);
    }


    public Object getPeak(String ExperimentName) {
        if (this.peaks.containsKey(ExperimentName)) {
            return this.peaks.get(ExperimentName);
        } else if (this.peaksString.containsKey(ExperimentName)) {
            return this.peaksString.get(ExperimentName);
        }
        return null;
    }

    public void setPeak(String name, Double value) {
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

    public boolean isSelected() {
        return this.selection;
    }

    public void setSelectionMode(boolean selectionMode) {
        this.selection = selectionMode;
    }

    public void setPeak(String str, String get) {
        this.peaksString.put(str, get);
    }

    public void removeNoSamplePeaks(String[] group) {
        Hashtable<String, Double> newPeaks = new Hashtable<String, Double>();
        for (String name : group) {
            if (this.peaks.containsKey(name)) {
                newPeaks.put(name, this.peaks.get(name));
            }
        }
        this.peaks = newPeaks;
    }

    public Hashtable<String, Double> getPeaksTable() {
        return this.peaks;
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
            Method m = this.getClass().getMethod(varName, partypes);
            Object[] parameters = new Object[1];
            parameters[0] = value;
            m.invoke(this, parameters);

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
