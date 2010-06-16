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
                    newRow.setVar("setName", stdName + " - " + stdName2);
                    PeakListRow row1 = this.standards.get(stdName);
                    PeakListRow row2 = this.standards.get(stdName2);

                    for (String experimentName : groupExperimentName) {
                        try {
                            double concentration = (Double) row1.getPeak(experimentName) / (Double) row2.getPeak(experimentName);
                            newRow.setPeak(experimentName, concentration);
                        } catch (Exception e) {
                        }
                    }
                    mols.addElement(newRow);
                }
            }
        }
    }
}
