/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.mylly.alignment.scoreAligner.scorer;

import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.modules.mylly.datastruct.Peak;
import guineu.modules.mylly.datastruct.Spectrum;

public class RTScore implements ScoreCalculator {

        /**
         * Creates new ScoreCalculator.
         */
        private final static double WORST_SCORE = Double.MAX_VALUE;

        public RTScore() {
        }

        /**
         * @see gcgcaligner.scorer.ScoreCalculator#calculateScore(gcgcaligner.alignment.AlignmentPath, gcgcaligner.datastruct.GCGCDatum, gcgcaligner.alignment.AlignmentParameters)
         */
        public double calculateScore(Peak path, Peak peak,
                ScoreAlignmentParameters params) {



                double score;

                score = ((1 - path.getRT1() / peak.getRT1()) + (1 - path.getRT2() / peak.getRT2()) + (1 / this.compareSpectraVal(path.getSpectrum(), peak.getSpectrum())));



                /*double rtiDiff = Math.abs(path.getRTI() - peak.getRTI());
                if (rtiDiff > (Double) params.getParameterValue(ScoreAlignmentParameters.rtiLax)) {
                return WORST_SCORE;
                }
                double rt2Diff = Math.abs(path.getRT2() - peak.getRT2());
                if (rt2Diff > (Double) params.getParameterValue(ScoreAlignmentParameters.rt2Lax)) {
                return WORST_SCORE;
                }
                double rt1Diff = Math.abs(path.getRT1() - peak.getRT1());
                if (rt1Diff > (Double) params.getParameterValue(ScoreAlignmentParameters.rt1Lax)) {
                return WORST_SCORE;
                }
                double comparison = compareSpectraVal(path.getSpectrum(), peak.getSpectrum());
                /*System.out.println("path names: " + path.names());
                System.out.println("peak names: " + peak.names());
                System.out.println("path spectra: " + path.getSpectrum());
                System.out.println("peak spectra: " + peak.getSpectrum());
                System.out.println("Spectra comparation Value: " + comparison);
                System.out.println("-------------------------------------");*/

                /*   if (comparison > (Double) params.getParameterValue(ScoreAlignmentParameters.minSpectrumMatch)) {
                score = rtiDiff * (Double) params.getParameterValue(ScoreAlignmentParameters.rtiPenalty) +
                rt1Diff * (Double) params.getParameterValue(ScoreAlignmentParameters.rt1Penalty) +
                rt2Diff * (Double) params.getParameterValue(ScoreAlignmentParameters.rt2Penalty);

                if (path.matchesWithName(peak)) {
                score -= (Double) params.getParameterValue(ScoreAlignmentParameters.nameMatchBonus);
                }
                } else {
                score = WORST_SCORE;
                }*/
                return score;
        }

        /**
         * @see gcgcaligner.scorer.ScoreCalculator#matches(Peak, Peak, gcgcaligner.alignment.AlignmentParameters)
         */
        public boolean matches(Peak path, Peak peak, ScoreAlignmentParameters params) {
                return calculateScore(path, peak, params) < getWorstScore();
        }

        public double getWorstScore() {
                return WORST_SCORE;
        }

        /**
         * Assumes that params#getMinSpectrumMatch() returns a value in [0,1]
         * @param s1
         * @param s2
         * @return
         */
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

        /* (non-Javadoc)
         * @see gcgcaligner.ScoreCalculator#isValid(gcgcaligner.GCGCDatum)
         */
        public boolean isValid(GCGCDatum peak) {
                return peak.getSpectrum() != null;
        }

        public String name() {
                return "Uses spectrum and retention times";
        }
}


