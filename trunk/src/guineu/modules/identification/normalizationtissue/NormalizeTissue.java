/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.identification.normalizationtissue;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.taskcontrol.TaskStatus;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class NormalizeTissue {

        private Dataset dataset;
        private double cont = 0;
        private List<StandardUmol> stdMol;
        HashMap<String, Double> weights;

        public NormalizeTissue(Dataset dataset, List<StandardUmol> stdMol, HashMap<String, Double> weights) {
                this.dataset = dataset;
                this.stdMol = stdMol;
                this.weights = weights;
        }

        public Dataset getDataset() {
                return dataset;
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

        public StandardUmol getUnknownName(PeakListRow row) {
                double RT = (Double) row.getVar("getRT");
                for (StandardUmol std : this.stdMol) {
                        if (std.getRange().contains(RT)) {
                                return std;
                        }
                }
                return null;
        }

        public void normalize(TaskStatus status) {
                for (PeakListRow row : dataset.getRows()) {
                        StandardUmol standard = this.getStd(row);
                        if (standard != null) {
                                // System.out.println(row.getVar("getName") + " - " + standard.getName());
                                for (String experimentName : dataset.getAllColumnNames()) {
                                        if (status == TaskStatus.CANCELED || status == TaskStatus.ERROR) {
                                                return;
                                        }

                                        try {
                                                Double valueNormalized = (Double) row.getPeak(experimentName);
                                                valueNormalized = this.getNormalizedValue(standard.getRealAmount(), standard.getIntensity(experimentName), valueNormalized / weights.get(experimentName));
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
         * Each lipid has its own standard. This function return a different
         * number for each standard.
         *
         * @param lipid
         * @return
         */
        private StandardUmol getStd(PeakListRow row) {
                String compoundName = (String) row.getVar("getName");
                // In the case the lipid is identified as an adduct, the program search the
                // main peak to get the standard class and change the name to the field "lipid"
                if (dataset.getType() == DatasetType.LCMS) {

                        if (compoundName.matches(".*adduct.*")) {
                                double num = 0;
                                Pattern adduct = Pattern.compile("\\d+[.,]\\d+");
                                Matcher matcher = adduct.matcher(compoundName);
                                if (matcher.find()) {
                                        String m = compoundName.substring(matcher.start(), matcher.end());
                                        m = m.replace(",", ".");
                                        num = Double.valueOf(m);
                                }
                                double difference = Double.POSITIVE_INFINITY;
                                for (PeakListRow parentRow : dataset.getRows()) {
                                        double mzRow = ((SimplePeakListRowLCMS) parentRow).getMZ();
                                        double dif = Math.abs(num - mzRow);
                                        if (dif < difference) {
                                                difference = dif;
                                                compoundName = ((SimplePeakListRowLCMS) parentRow).getName();
                                        }
                                }
                        }
                }

                // In the case the main lipid is unknown or another adduct..
                if (compoundName.matches(".*unknown.*") || compoundName.matches(".*adduct.*")) {
                        return this.getUnknownName(row);
                }

                return getMostSimilar(compoundName);
        }

        private StandardUmol getMostSimilar(String compoundName) {
                StandardUmol minDistanceStd = null;
                if (compoundName.contains("LP") || compoundName.contains("Lyso")) {
                        StandardUmol std = getStandard("LP");
                        if (std == null) {
                                return getStandard("Lyso");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("PC")) {
                        if (compoundName.contains("+PC")) {
                                return getStandard("PE");
                        }
                        return getStandard("PC");
                } else if (compoundName.contains("SM")) {
                        StandardUmol std = getStandard("SM");
                        if (std == null) {
                                return getStandard("PC");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("HexCer")) {
                        StandardUmol std = getStandard("HexCer");
                        if (std == null) {
                                return getStandard("Cer");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("LacCer")) {
                        StandardUmol std = getStandard("LacCer");
                        if (std == null) {
                                return getStandard("Cer");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("Cer")) {
                        return getStandard("Cer");
                } else if (compoundName.contains("PG")) {
                        StandardUmol std = getStandard("PG");
                        if (std == null) {
                                return getStandard("PE");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("PI")) {
                        StandardUmol std = getStandard("PI");
                        if (std == null) {
                                return getStandard("PE");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("PE")) {
                        return getStandard("PE");
                } else if (compoundName.contains("TG") || compoundName.contains("TAG")) {
                        return getStandard("TG");
                } else if (compoundName.contains("ChoE")) {
                        if (compoundName.contains("fragm")) {
                                StandardUmol std = getStandardFragm("ChoE");
                                if (std == null) {
                                        return getStandard("ChoE");
                                } else {
                                        return std;
                                }
                        }
                        return getStandard("ChoE");

                } else if (compoundName.contains("DG")) {
                        StandardUmol std = getStandard("DG");
                        if (std == null) {
                                return getStandard("TG");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("MG")) {
                        StandardUmol std = getStandard("MG");
                        if (std == null) {
                                return getStandard("TG");
                        } else {
                                return std;
                        }
                } else if (compoundName.contains("CL")) {
                        StandardUmol std = getStandard("CL");
                        if (std == null) {
                                return getStandard("TG");
                        } else {
                                return std;
                        }
                }

                return minDistanceStd;
        }

        public double getProgress() {
                return (cont / (dataset.getNumberRows()));
        }

        private StandardUmol getStandard(String name) {
                for (StandardUmol standard : this.stdMol) {
                        if (name.equals("PC")) {
                                if (standard.getName().contains(name) && (!standard.getName().contains("Lyso") && !standard.getName().contains("LP"))) {
                                        return standard;
                                }
                        } else if (name.equals("ChoE")) {
                                if (standard.getName().contains(name) && !standard.getName().contains("fragm")) {
                                        return standard;
                                }
                        } else if (standard.getName().contains(name)) {
                                return standard;
                        }
                }
                return null;
        }

        private StandardUmol getStandardFragm(String name) {
                for (StandardUmol standard : this.stdMol) {
                        String stdName = standard.getName();
                        if (stdName.contains("fragm") && stdName.contains(name)) {
                                return standard;
                        }
                }
                return null;
        }
}
