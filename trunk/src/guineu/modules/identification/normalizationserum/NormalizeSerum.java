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
package guineu.modules.identification.normalizationserum;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.taskcontrol.TaskStatus;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class NormalizeSerum {

    private Dataset dataset;
    private double cont = 0;
    private Hashtable<String, StandardUmol> stdMol;

    public NormalizeSerum(Dataset dataset, Hashtable<String, StandardUmol> stdMol) {
        this.dataset = dataset;
        this.stdMol = stdMol;
    }

    public Dataset getDataset() {
        return dataset;
    }

    private String getLipidName(PeakListRow row) {
        String olipid = (String) row.getVar("getName");
        if (olipid.matches(".*unknown.*")) {
            olipid = this.getUnknownName(row);
            return olipid;
        }
        return olipid;
    }

    public double getNormalizedValue(double stdRealConcentration, double stdConcentration, double concentration) {
        try {
            if (stdConcentration == 0) {
                return 0;
            }
            return (stdRealConcentration / stdConcentration) * concentration;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getUnknownName(PeakListRow row) {
        double RT = (Double) ((SimplePeakListRowLCMS) this.dataset.getRow(row)).getRT();

    }

    public void normalize(TaskStatus status) {
        for (PeakListRow row : dataset.getRows()) {
            StandardUmol standard = this.getStd(row);
            if (standard != null) {
                for (String experimentName : dataset.getNameExperiments()) {
                    if (status == TaskStatus.CANCELED || status == TaskStatus.ERROR) {
                        return;
                    }

                    try {
                        Double valueNormalized = (Double) row.getPeak(experimentName);
                        valueNormalized = this.getNormalizedValue(standard.getRealAmount(), standard.getIntensity(experimentName), valueNormalized);
                        row.setPeak(experimentName, new Double(valueNormalized));
                    } catch (Exception exception2) {
                        exception2.printStackTrace();
                    }
                }
            }
            cont++;
        }
    }

    /**
     * Each lipid has its own standard. This function return a different number for each
     * standard.
     * @param lipid
     * @return
     */
    private StandardUmol getStd(PeakListRow lipid) {
        // In the case the lipid is identified as an adduct, the program search the
        // main peak to get the standard class and change the name to the field "lipid"
        int newRow = -1;
        String lipidName = 
        if (lipid.getVar(null).matches(".*adduct.*")) {
            double num = 0;
            Pattern adduct = Pattern.compile("\\d+.\\d+");
            Matcher matcher = adduct.matcher(lipid);
            if (matcher.find()) {
                num = Double.valueOf(lipid.substring(matcher.start(), matcher.end()));
            }
            double difference = Double.POSITIVE_INFINITY;
            int counter = 0;
            for (PeakListRow row : dataset.getRows()) {
                double mzRow = ((SimplePeakListRowLCMS) row).getMZ();
                double dif = Math.abs(num - mzRow);
                if (dif < difference) {
                    difference = dif;
                    lipid = ((SimplePeakListRowLCMS) row).getName();
                    newRow = counter;
                }
                counter++;
            }
        }

        // In the case the main lipid is unknown or another adduct..
        if (lipid.matches(".*unknown.*") || lipid.matches(".*adduct.*") && newRow >= 0) {
            lipid = this.getUnknownName(newRow);
        }



    }

    public double getProgress() {
        return (cont / (dataset.getNumberRows()));
    }
}
