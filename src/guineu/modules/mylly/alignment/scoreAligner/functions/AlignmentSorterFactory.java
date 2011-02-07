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
package guineu.modules.mylly.alignment.scoreAligner.functions;

import guineu.data.PeakListRow;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.Comparator;

/**
 *
 * @author Jarkko Miettinen
 */
public class AlignmentSorterFactory {

        public static enum SORT_MODE {

                name {

                        public String getName() {
                                return "name";
                        }
                },
                peaks {

                        public String getName() {
                                return "number of peaks";
                        }
                },
                rt {

                        public String getName() {
                                return "RT";
                        }
                },
                rt1 {

                        public String getName() {
                                return "RT 1";
                        }
                },
                rt2 {

                        public String getName() {
                                return "RT 2";
                        }
                },
                rti {

                        public String getName() {
                                return "RTI";
                        }
                },
                quantMass {

                        public String getName() {
                                return "Quant Mass";
                        }
                },
                diffToIdeal {

                        public String getName() {
                                return "Difference to ideal peak";
                        }
                },
                maxSimilarity {

                        public String getName() {
                                return "maximum similarity";
                        }
                },
                meanSimilarity {

                        public String getName() {
                                return "mean similarity";
                        }
                },
                stdSimilarity {

                        public String getName() {
                                return "similarity std dev";
                        }
                },
                none {

                        public String getName() {
                                return "nothing";
                        }
                };

                public abstract String getName();
        }

        public static Comparator<PeakListRow> getComparator(final SORT_MODE mode) {
                return getComparator(mode, true);
        }

        /**
         * Return a comparator that <b>is</b> inconsistent with equals.
         * @param mode
         * @param ascending
         * @return
         */
        public static Comparator<PeakListRow> getComparator(final SORT_MODE mode, final boolean ascending) {
                switch (mode) {
                        case name:
                                return getNameComparator(ascending);
                        case peaks:
                                return getPeakCountComparator(ascending);
                        case rt:
                        case rt1:
                        case rt2:
                        case rti:
                        case quantMass:
                        case maxSimilarity:
                        case meanSimilarity:
                        case stdSimilarity:
                                //All these use the same sort of comparator
                                return getDoubleValComparator(ascending, mode);
                        case diffToIdeal:
                                return distToIdealComparator(ascending);
                        default:
                                return nullComparator();
                }
        }

        private static Comparator<PeakListRow> getNameComparator(final boolean ascending) {
                return new Comparator<PeakListRow>() {

                        public int compare(PeakListRow o1, PeakListRow o2) {
                                int comparison = 0;
                                //This if...else if -pair causes unknown peaks to appear
                                //last in the list.
                                if (GCGCDatum.UNKOWN_NAME.equals(o1.getVar("getName")) &&
                                        !o1.getVar("getName").equals(o2.getVar("getName"))) {
                                        comparison = 1;
                                } else if (GCGCDatum.UNKOWN_NAME.equals(o2.getVar("getName")) &&
                                        !o1.getVar("getName").equals(o2.getVar("getName"))) {
                                        comparison = -1;
                                } else {
                                        comparison = ((String) o1.getVar("getName")).compareToIgnoreCase((String) o2.getVar("getName"));
                                }
                                return ascending ? comparison : -comparison;
                        }
                };
        }

        private static Comparator<PeakListRow> getPeakCountComparator(final boolean ascending) {
                return new Comparator<PeakListRow>() {

                        public int compare(PeakListRow o1, PeakListRow o2) {
                                int comp = (Integer) o1.getVar("nonNullPeakCount") - (Integer) o2.getVar("nonNullPeakCount");
                                return ascending ? comp : -comp;
                        }
                };
        }

        private static Comparator<PeakListRow> getDoubleValComparator(final boolean ascending, final SORT_MODE mode) {
                return new Comparator<PeakListRow>() {

                        public int compare(PeakListRow o1, PeakListRow o2) {
                                int comparison = 0;
                                double val1 = 0.0;
                                double val2 = 0.0;
                                switch (mode) {
                                        case rt:
                                                val1 = (Double) o1.getVar("getRT");
                                                break;
                                        case rt1:
                                                val1 = (Double) o1.getVar("getRT1");
                                                val2 = (Double) o2.getVar("getRT1");
                                                break;
                                        case rt2:
                                                val1 = (Double) o1.getVar("getRT2");
                                                val2 = (Double) o2.getVar("getRT2");
                                                break;
                                        case rti:
                                                val1 = (Double) o1.getVar("getRTI");
                                                val2 = (Double) o2.getVar("getRTI");
                                                break;
                                        case quantMass:
                                                val1 = (Double) o1.getVar("getMass");
                                                val2 = (Double) o2.getVar("getMass");
                                                break;
                                        case maxSimilarity:
                                                val1 = (Double) o1.getVar("getMaxSimilarity");
                                                val2 = (Double) o2.getVar("getMaxSimilarity");
                                                break;
                                        case meanSimilarity:
                                                val1 = (Double) o1.getVar("getMeanSimilarity");
                                                val2 = (Double) o2.getVar("getMeanSimilarity");
                                                break;
                                        case stdSimilarity:
                                                val1 = (Double) o1.getVar("getSimilaritySTDDev");
                                                val2 = (Double) o2.getVar("getSimilaritySTDDev");
                                }
                                if (val1 < val2) {
                                        comparison = -1;
                                }
                                if (val1 > val2) {
                                        comparison = 1;
                                }
                                return ascending ? comparison : -comparison;
                        }
                };
        }

        private static Comparator<PeakListRow> distToIdealComparator(final boolean ascending) {
                return new Comparator<PeakListRow>() {

                        public int compare(PeakListRow o1, PeakListRow o2) {
                                DistValue val1 = (DistValue) o1.getVar("getDistValue");
                                DistValue val2 = (DistValue) o2.getVar("getDistValue");
                                return ascending ? val1.compareTo(val2) : -val1.compareTo(val2);
                        }
                };
        }

        private static Comparator<PeakListRow> nullComparator() {
                return new Comparator<PeakListRow>() {

                        public int compare(PeakListRow o1, PeakListRow o2) {
                                return 0;
                        }
                };
        }
}
