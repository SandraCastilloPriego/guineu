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
package guineu.modules.mylly.alignment.scoreAligner;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.DoubleParameter;

/**
 *
 * @author scsandra
 */
public class ScoreAlignmentParameters extends SimpleParameterSet {

        public static final DoubleParameter rt1Lax = new DoubleParameter(
                "RT Lax:",
                "RT Lax", 15.0);
        public static final DoubleParameter rt2Lax = new DoubleParameter(
                "RT2 Lax:",
                "RT2 Lax", 0.3);
        public static final DoubleParameter rt1Penalty = new DoubleParameter(
                "RT penalty:",
                "RT penalty", 5.0);
        public static final DoubleParameter rt2Penalty = new DoubleParameter(
                "RT2 penalty:",
                "RT2 penalty", 35.0);
        public static final DoubleParameter minSpectrumMatch = new DoubleParameter(
                "Minimum Spectrum Match:",
                "Minimun Spectrum Match", 0.75);
        public static final DoubleParameter nameMatchBonus = new DoubleParameter(
                "Bonus for matching names:",
                "Bonus for matching names");
        public static final DoubleParameter rtiPenalty = new DoubleParameter(
                "RTI penalty",
                "RTI penalty", 40.0);
        public static final DoubleParameter minSimilarity = new DoubleParameter(
                "Drop peaks with similarity less than:",
                "Drop peaks with low similarity", 600.0);
        public static final DoubleParameter rtiLax = new DoubleParameter(
                "RI Lax:",
                "RI Lax", 20.0);
        public static final BooleanParameter useConcentration = new BooleanParameter(
                "Use Concentrations: ",
                "Use Concentrations", true);

        public ScoreAlignmentParameters() {
                super(new UserParameter[]{rt1Lax, rt2Lax, rtiLax, rt1Penalty, rt2Penalty, rtiPenalty, minSpectrumMatch, nameMatchBonus, minSimilarity, useConcentration});
        }
}
