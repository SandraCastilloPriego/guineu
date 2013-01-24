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
package guineu.modules.filter.Alignment.RANSACGCGC;

import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.modules.mylly.datastruct.Spectrum;

/**
 * This class represents a score between peak list row and aligned peak list row
 */
class RowVsRowGCGCScore implements Comparable<RowVsRowGCGCScore> {

        private SimplePeakListRowGCGC peakListRow, alignedRow;
        double score;
        private String errorMessage;

        RowVsRowGCGCScore(PeakListRow peakListRow, PeakListRow alignedRow,
                double mzMaxDiff, double rtMaxDiff, double correctedRT) throws Exception {


                this.alignedRow = (SimplePeakListRowGCGC) alignedRow;
                this.peakListRow = (SimplePeakListRowGCGC) peakListRow;

                // Calculate differences between m/z and RT values
                //double rt1Diff = Math.abs(this.peakListRow.getRT1() - this.alignedRow.getRT1());
                /// double rt2Diff = Math.abs(this.peakListRow.getRT2() - this.alignedRow.getRT2());
                double rtiDiff = Math.abs(correctedRT - this.alignedRow.getRT1());
                double comparison = compareSpectraVal(this.peakListRow.getSpectrum(), this.alignedRow.getSpectrum());
                if(comparison < 0.75){
                        score = -10;
                }else{
                        score =  comparison + (1 - rtiDiff / rtMaxDiff);
                }                
        }

        public double compareSpectraVal(Spectrum s1, Spectrum s2) {
                if (s1.getSortingMode() != Spectrum.SortingMode.REVERSEMASS) {
                        s1.sort(Spectrum.SortingMode.REVERSEMASS);
                }
                if (s2.getSortingMode() != Spectrum.SortingMode.REVERSEMASS) {
                        s2.sort(Spectrum.SortingMode.REVERSEMASS);
                }
                int masses1[] = s1.getMasses();
                int masses2[] = s2.getMasses();
                int int1[] = s1.getIntensities();
                int int2[] = s2.getIntensities();

                double pathMaxIntensity = int1[0];
                double peakMaxIntensity = int2[0];

                double spec1Sum = 0.0;
                double spec2Sum = 0.0;
                double bothSpecSum = 0.0;

                int i = 0;
                int j = 0;
                int len1 = masses1.length;
                int len2 = masses2.length;
                double mass1 = masses1[0];
                double mass2 = masses2[0];

                while (i < len1 || j < len2) {
                        while ((mass1 > mass2 || j == len2) && i < len1) {
                                double relInt1 = int1[i++] / pathMaxIntensity;
                                spec1Sum += dotTerm(mass1, relInt1);
                                if (i < len1) {
                                        mass1 = masses1[i];
                                }
                        }
                        while ((mass2 > mass1 || i == len1) && j < len2) {
                                double relInt2 = int2[j++] / peakMaxIntensity;
                                spec2Sum += dotTerm(mass2, relInt2);
                                if (j < len2) {
                                        mass2 = masses2[j];
                                }
                        }
                        while (mass1 == mass2 && i < len1 && j < len2) {
                                double relInt1 = int1[i++] / pathMaxIntensity;
                                double relInt2 = int2[j++] / peakMaxIntensity;
                                spec1Sum += dotTerm(mass1, relInt1);
                                spec2Sum += dotTerm(mass2, relInt2);
                                bothSpecSum += dotTerm(mass1, Math.sqrt(relInt1 * relInt2));
                                if (i < len1) {
                                        mass1 = masses1[i];
                                }
                                if (j < len2) {
                                        mass2 = masses2[j];
                                }
                        }
//			if (i == len1 && j == len2){break;}
                }
                double dotSum = (bothSpecSum * bothSpecSum / (spec1Sum * spec2Sum));
                return dotSum;
        }

        private double dotTerm(final double mass, final double intensity) {
                return mass * mass * intensity;
        }

        /**
         * This method returns the peak list row which is being aligned
         */
        PeakListRow getPeakListRow() {
                return peakListRow;
        }

        /**
         * This method returns the row of aligned peak list
         */
        PeakListRow getAlignedRow() {
                return alignedRow;
        }

        /**
         * This method returns score between the these two peaks (the lower
         * score, the better match)
         */
        double getScore() {
                return score;
        }

        String getErrorMessage() {
                return errorMessage;
        }

        /**
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(RowVsRowGCGCScore object) {

                // We must never return 0, because the TreeSet in JoinAlignerTask would
                // treat such elements as equal
                if (score < object.getScore()) {
                        return 1;
                } else {
                        return -1;
                }

        }
}
