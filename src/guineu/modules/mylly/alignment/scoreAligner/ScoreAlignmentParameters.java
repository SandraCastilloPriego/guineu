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
package guineu.modules.mylly.alignment.scoreAligner;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.NumberParameter;

/**
 *
 * @author scsandra
 */
public class ScoreAlignmentParameters extends SimpleParameterSet {

        public static final NumberParameter rt1Lax = new NumberParameter(
                "RT Lax:",
                "RT Lax", null, new Double(15.0));
        public static final NumberParameter rt2Lax = new NumberParameter(
                "RT2 Lax:",
                "RT2 Lax", null, new Double(0.3));
        public static final NumberParameter rt1Penalty = new NumberParameter(
                "RT penalty:",
                "RT penalty", null, new Double(5.0));
        public static final NumberParameter rt2Penalty = new NumberParameter(
                "RT2 penalty:",
                "RT2 penalty", null, new Double(35.0));
        public static final NumberParameter minSpectrumMatch = new NumberParameter(
                "Minimum Spectrum Match:",
                "Minimun Spectrum Match", null, new Double(0.75));
        public static final NumberParameter nameMatchBonus = new NumberParameter(
                "Bonus for matching names:",
                "Bonus for matching names");
        public static final NumberParameter rtiPenalty = new NumberParameter(
                "RTI penalty",
                "RTI penalty", null, new Double(40.0));
        public static final NumberParameter minSimilarity = new NumberParameter(
                "Drop peaks with similarity less than:",
                "Drop peaks with low similarity", null, new Double(600.0));
        public static final NumberParameter rtiLax = new NumberParameter(
                "RI Lax:",
                "RI Lax", null, new Double(20.0));
        public static final BooleanParameter useConcentration = new BooleanParameter(
                "Use Concentrations: ",
                "Use Concentrations", true);

        public ScoreAlignmentParameters() {
                super(new UserParameter[]{rt1Lax, rt2Lax, rtiLax, rt1Penalty, rt2Penalty, rtiPenalty, minSpectrumMatch, nameMatchBonus, minSimilarity, useConcentration});
        }
}
