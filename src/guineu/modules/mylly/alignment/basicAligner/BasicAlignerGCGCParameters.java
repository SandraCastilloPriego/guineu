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
package guineu.modules.mylly.alignment.basicAligner;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.NumberParameter;
import guineu.parameters.parametersType.RTToleranceParameter;
import guineu.parameters.parametersType.StringParameter;
import java.text.NumberFormat;

public class BasicAlignerGCGCParameters extends SimpleParameterSet {

        public static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
        public static final StringParameter peakListName = new StringParameter(
                "Peak list name", "Peak list name");
        public static final RTToleranceParameter RT1Tolerance = new RTToleranceParameter(
                "RT1 tolerance",
                "Maximum allowed RT1 difference");
        public static final RTToleranceParameter RT2Tolerance = new RTToleranceParameter(
                "RT2 tolerance",
                "Maximum allowed absolute RT2 difference");
        public static final NumberParameter rt1Lax = new NumberParameter(
                "RT Lax:",
                "RT Lax");
        public static final NumberParameter rt2Lax = new NumberParameter(
                "RT2 Lax:",
                "RT2 Lax");
        public static final NumberParameter nameMatchBonus = new NumberParameter(
                "Bonus for matching names:",
                "Bonus for matching names");

        public BasicAlignerGCGCParameters() {
                super(new UserParameter[]{peakListName, RT1Tolerance, RT2Tolerance, rt1Lax, rt2Lax, nameMatchBonus});
        }
}
