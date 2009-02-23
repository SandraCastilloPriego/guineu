/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.statistics.standardVariation;

import guineu.data.PeakListRow;
import guineu.data.impl.SimplePeakListRowLCMS;
import java.util.Hashtable;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class StandardUmol {

    Hashtable<String, PeakListRow> standards;
    Vector<PeakListRow> mols;
    String[] groupExperimentName;
    Vector<String> standardsNames;

    public StandardUmol(String[] groupExperimentName) {
        this.standards = new Hashtable<String, PeakListRow>();
        this.groupExperimentName = groupExperimentName;
        this.mols = new Vector<PeakListRow>();
        this.standardsNames = new Vector<String>();
    }

    public void setStandard(PeakListRow mol, String name) {
        this.standards.put(name, mol);
        this.standardsNames.addElement(name);
    }

    public Vector<PeakListRow> getMols() {
        return mols;
    }

    public void run() {
        for (String stdName : standardsNames) {
            for (String stdName2 : standardsNames) {
                if (!stdName.matches(stdName2)) {
                    PeakListRow newRow = new SimplePeakListRowLCMS();
                    newRow.setName(stdName + " - " + stdName2);
                    PeakListRow row1 = this.standards.get(stdName);
                    PeakListRow row2 = this.standards.get(stdName2);

                    for (String experimentName : groupExperimentName) {
                        double concentration = (Double)row1.getPeak(experimentName) / (Double)row2.getPeak(experimentName);
                        newRow.setPeak(experimentName, concentration);
                    }
                    mols.addElement(newRow);
                }
            }
        }
    }
}
