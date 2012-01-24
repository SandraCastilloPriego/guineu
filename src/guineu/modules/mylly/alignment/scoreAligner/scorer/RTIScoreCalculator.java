/*
 * Copyright 2007-2012 VTT Biotechnology
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

/**
 * @author jmjarkko
 */
public class RTIScoreCalculator implements ScoreCalculator {

        private double rt1Penalty;
        private double rt2Penalty;
        private double nameMatchBonus;
        private ScoreAlignmentParameters lastParams;
        private double rtiPenalty;

        public double calculateScore(Peak path, Peak peak, ScoreAlignmentParameters params) {
                if (params != null && params != lastParams) {
                        rtiPenalty = params.getParameter(ScoreAlignmentParameters.rtiPenalty).getValue();
                        rt1Penalty = params.getParameter(ScoreAlignmentParameters.rt1Penalty).getValue();
                        rt2Penalty = params.getParameter(ScoreAlignmentParameters.rt2Penalty).getValue();
                        nameMatchBonus = params.getParameter(ScoreAlignmentParameters.nameMatchBonus).getValue();
                }
                double rt1Diff = Math.abs(path.getRT1() - peak.getRT1());
                double score = rt1Diff * rt1Penalty;
                double rt2Diff = Math.abs(path.getRT2() - peak.getRT2());
                score += rt2Penalty * rt2Diff;
                if (path.matchesWithName(peak)) {
                        score += nameMatchBonus;
                }
                score += rtiPenalty * Math.abs(path.getRTI() - peak.getRTI());
                return score;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.ScoreCalculator#matches(gcgcaligner.AlignmentPath, gcgcaligner.GCGCDatum, gcgcaligner.AlignmentParameters)
         */
        public boolean matches(Peak path, Peak peak, ScoreAlignmentParameters params) {
                double gapPenalty = params.getParameter(ScoreAlignmentParameters.rt1Lax).getValue() * params.getParameter(ScoreAlignmentParameters.rt1Penalty).getValue() + params.getParameter(ScoreAlignmentParameters.rt2Lax).getValue() * params.getParameter(ScoreAlignmentParameters.rt2Penalty).getValue() + params.getParameter(ScoreAlignmentParameters.rtiLax).getValue() * params.getParameter(ScoreAlignmentParameters.rtiPenalty).getValue();

                return calculateScore(path, peak, params) < gapPenalty;
        }

        /* (non-Javadoc)
         * @see gcgcaligner.ScoreCalculator#getWorstScore()
         */
        public double getWorstScore() {
                return Double.MAX_VALUE;
        }

        public boolean isValid(GCGCDatum peak) {
                return true;
        }

        public String name() {
                return "Use retention times";
        }
}
