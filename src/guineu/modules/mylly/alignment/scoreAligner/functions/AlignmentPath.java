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
package guineu.modules.mylly.alignment.scoreAligner.functions;

import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.alignment.scoreAligner.scorer.ScoreCalculator;
import guineu.modules.mylly.datastruct.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jmjarkko
 */
public class AlignmentPath implements Comparable<AlignmentPath>, Cloneable, Peak {

        public final static int NOT_USED = -1;
        private GCGCDatum peaks[];
        private int indices[];
        private int nonGapCount;
        private double rt1sum;
        private double rt2sum;
        private double rtisum;
        private double meanRT1;
        private double meanRT2;
        private double meanRTI;
        private double similaritySum;
        private double maxSimiliarity;
        private double score;
        private boolean isEmpty;
        private boolean identified;
        private String CAS;
        private GCGCDatum base;
        private Set<String> names;
        private List<String> CASes;
        private ConsensusSpectrum spectrum;

        @Override
        public AlignmentPath clone() {
                AlignmentPath p = new AlignmentPath();
                p.peaks = peaks.clone();
                p.nonGapCount = nonGapCount;
                p.rt1sum = rt1sum;
                p.rt2sum = rt2sum;
                p.rtisum = rtisum;
                p.similaritySum = similaritySum;
                p.score = score;
                p.isEmpty = isEmpty;
                p.identified = identified;
                p.base = base;
                p.CAS = CAS;
                p.names = new HashSet<String>(names);
                p.CASes = new ArrayList<String>(CASes);
                p.spectrum = spectrum;
                p.maxSimiliarity = maxSimiliarity;
                p.meanRT1 = meanRT1;
                p.meanRT2 = meanRT2;
                p.meanRTI = meanRTI;
                return p;
        }

        private AlignmentPath() {
        }

        private AlignmentPath(int n) {
                peaks = new GCGCDatum[n];
                isEmpty = true;
                names = new HashSet<String>();
                CASes = new ArrayList<String>();
        }

        /**
         * @param len
         * @param base
         * @param startCol
         * @param params2
         */
        public AlignmentPath(int len, GCGCDatum base, int startCol) {
                this(len);
                this.base = base;
                this.spectrum = new ConsensusSpectrum();

                this.add(startCol, this.base, 0);
        }

        public int nonEmptyPeaks() {
                return nonGapCount;
        }

        public boolean containsSame(AlignmentPath anotherPath) {
                boolean same = false;
                for (int i = 0; i < peaks.length; i++) {
                        GCGCDatum d = peaks[i];
                        if (d != null) {
                                same = d.equals(anotherPath.peaks[i]);
                        }
                        if (same) {
                                break;
                        }
                }
                return same;
        }

        public void addGap(int col, double score) {
                this.score += score;
        }

        /**
         * No peaks with differing mass should reach this point.
         * @param col column in peak table that contains this peak
         * @param peak
         * @param matchScore
         */
        public void add(int col, GCGCDatum peak, double matchScore) {
                if (peaks[col] != null) {
                        throw new RuntimeException("Peak " + col + " is already filled.");
                }
                if (peak.isIdentified()) {
                        names.add(peak.getName());
                        CASes.add(peak.getCAS());
                        identified = true;
                }
                if (peak.getSimilarity() > maxSimiliarity) {
                        maxSimiliarity = peak.getSimilarity();
                }

                peaks[col] = peak;

                if (peak != GCGCDatum.getGAP()) {
                        nonGapCount++;
                        rt1sum += peak.getRT1();
                        rt2sum += peak.getRT2();
                        rtisum += peak.getRTI();
                        similaritySum += peak.getSimilarity();
                        spectrum.addPeak(peak);
                        meanRTI = rtisum / nonGapCount;
                        meanRT1 = rt1sum / nonGapCount;
                        meanRT2 = rt2sum / nonGapCount;
                }

                isEmpty = false;
                score += matchScore;
        }

        public double getRT1() {
                return meanRT1;
        }

        public double getRT2() {
                return meanRT2;
        }

        public double getRTI() {
                return meanRTI;
        }

        public String getCAS() {
                return CAS;
        }

        public List<String> getCASes() {
                return CASes;
        }

        public Spectrum getSpectrum() {
                return spectrum;
        }

        public boolean matchesWithName(GCGCDatum peak) {
                boolean matches = false;
                if (names != null && peak.isIdentified()) {
                        matches = names.contains(peak.getName());
                }
                return matches;
        }

        public double getMeanSimilarity() {
                return similaritySum / nonGapCount;
        }

        public double getMaxSimilarity() {
                return maxSimiliarity;
        }

        public double getSimilarityStdDev() {
                if (nonGapCount == 1) {
                        return 0.0;
                }
                double similarityMean = getMeanSimilarity();
                double var = 0.0;
                int count = 0;
                for (int i = 0; i < peaks.length; i++) {
                        GCGCDatum peak = peaks[i];
                        if (peak != null) {
                                double temp = peak.getSimilarity() - similarityMean;
                                var += temp * temp;
                                count++;
                        }
                }
                assert (count == nonGapCount);
                var /= nonGapCount - 1;
                return Math.sqrt(var);
        }

        public double getScore() {
                return score;
        }

        public int getIndex(int i) {
                return indices[i];
        }

        /**
         * Is the whole alignment path still empty?
         * @return
         */
        public boolean isEmpty() {
                return isEmpty;
        }

        public boolean isFull() {
                return nonEmptyPeaks() == length();
        }

        public int length() {
                return peaks.length;
        }

        public SimplePeakListRowGCGC convertToAlignmentRow(int ID) {
                SimplePeakListRowGCGC row = new SimplePeakListRowGCGC(this);
                row.setID(ID);
                return row;
        }

        public GCGCDatum getPeak(int index) {
                return peaks[index];
        }

        @Override
        public String toString() {
                StringBuilder sb = new StringBuilder();
                for (GCGCDatum d : peaks) {
                        sb.append(d != null ? d.toString() : "GAP").append(' ');
                }
                return sb.toString();
        }

        public boolean isIdentified() {
                return identified;
        }


        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(AlignmentPath o) {
                double diff = score - o.score;
                return (diff < 0) ? -1 : (diff == 0) ? 0 : 1;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.Peak#getArea()
         */
        public double getArea() {
                double areaSum = 0.0;
                for (GCGCDatum d : peaks) {
                        if (d != null) {
                                areaSum += d.getArea();
                        }
                }
                return areaSum;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.Peak#getConcentration()
         */
        public double getConcentration() {
                double concentrationSum = 0.0;
                for (GCGCDatum d : peaks) {
                        if (d != null) {
                                concentrationSum += d.getConcentration();
                        }
                }
                return concentrationSum;
        }


        /* (non-Javadoc)
         * @see gcgcaligner.Peak#getQuantMass()
         */
        public double getQuantMass() {
                double mass = -1.0;
                if (hasQuantMass()) {
                        for (Peak p : peaks) {
                                if (p != null) {
                                        mass = p.getQuantMass();
                                        break;
                                }
                        }
                }
                return mass;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.Peak#hasQuantMass()
         */
        public boolean hasQuantMass() {
                double lastMass = -1;
                for (int i = 0; i < peaks.length; i++) {
                        Peak p = peaks[i];
                        if (p != null) {
                                if (!p.hasQuantMass()) {
                                        return false;
                                }
                                double pMass = p.getQuantMass();
                                if (lastMass == -1) {
                                        lastMass = pMass;
                                }
                                if (lastMass != pMass) {
                                        return false;
                                }
                        }
                }
                return true;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.Peak#matchesWithName(gcgcaligner.Peak)
         */
        public boolean matchesWithName(Peak p) {
                for (String curName : p.names()) {
                        if (names.contains(curName) &&
                                !curName.equals(GCGCDatum.UNKOWN_NAME)) {
                                return true;
                        }
                }
                return false;
        }

        public static boolean isQuantified(AlignmentPath p) {
                boolean isQuantified = false;
                for (GCGCDatum d : p.peaks) {
                        if (d instanceof GCGCDatumWithConcentration) {
                                isQuantified = true;
                                break;
                        }
                }
                return isQuantified;
        }

        /**
         * Should be called only for complete alignment paths, that is for paths that have
         * gone through the alignment process.
         * @param other
         * @param calc
         * @param params
         * @return
         */
        public AlignmentPath mergeWith(AlignmentPath other, ScoreCalculator calc, ScoreAlignmentParameters params) {
                if (other == null) {
                        return clone();
                }
                AlignmentPath combinedPath = null;
                int i = 0;
                for (; i < peaks.length; i++) {
                        GCGCDatum peak = null;
                        if (peaks[i] != null && other.peaks[i] != null) {
                                peak = peaks[i].combineWith(other.peaks[i]);
                        } else if (peaks[i] != null) {
                                peak = peaks[i];
                        } else if (other.peaks[i] != null) {
                                peak = other.peaks[i];
                        }
                        if (peak != null) {
                                combinedPath = new AlignmentPath(length(),
                                        peak, i);
                                break;
                        }
                }
                for (i = i + 1; i < peaks.length; i++) {
                        double matchScore = 0;
                        GCGCDatum peak = null;
                        boolean foundPeak = false;

                        if (peaks[i] != null) {
                                peak = peaks[i].combineWith(other.peaks[i]);
                                matchScore = calc.calculateScore(combinedPath, peak, params);
                                foundPeak = true;
                        } else if (other.peaks[i] != null) {
                                peak = other.peaks[i];
                                matchScore = calc.calculateScore(combinedPath, peak, params);
                                foundPeak = true;
                        }
                        if (foundPeak) {
                                combinedPath.add(i, peak, matchScore);
                        } else {
                                double gapPenalty = params.getParameter(ScoreAlignmentParameters.rt1Lax).getValue() * params.getParameter(ScoreAlignmentParameters.rt1Penalty).getValue() + params.getParameter(ScoreAlignmentParameters.rt2Lax).getValue() * params.getParameter(ScoreAlignmentParameters.rt2Penalty).getValue() + params.getParameter(ScoreAlignmentParameters.rtiLax).getValue() * params.getParameter(ScoreAlignmentParameters.rtiPenalty).getValue();

                                combinedPath.addGap(i, gapPenalty);
                        }
                }
                return combinedPath;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.Peak#names()
         */
        public List<String> names() {
                ArrayList<String> nameList = new ArrayList<String>(names);
                return nameList;
        }
}
