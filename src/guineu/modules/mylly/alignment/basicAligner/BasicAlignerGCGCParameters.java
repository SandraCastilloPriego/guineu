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
package guineu.modules.mylly.alignment.basicAligner;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;
import guineu.main.GuineuCore;
import java.text.NumberFormat;

public class BasicAlignerGCGCParameters extends SimpleParameterSet {

        public static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
        public static final Parameter peakListName = new SimpleParameter(
                ParameterType.STRING, "Peak list name", "Peak list name", null,
                "Aligned peak list", null);
        public static final Parameter RT1Tolerance = new SimpleParameter(
                ParameterType.DOUBLE, "RT1 tolerance",
                "Maximum allowed RT1 difference", null, new Double(5.0),
                new Double(0.0), null, GuineuCore.getMZFormat());
        public static final Parameter RT2Tolerance = new SimpleParameter(
                ParameterType.DOUBLE, "RT2 tolerance",
                "Maximum allowed absolute RT2 difference", null, new Double(0.01),
                new Double(0.0), null, GuineuCore.getMZFormat());
        public static final Parameter rt1Lax = new SimpleParameter(
                ParameterType.DOUBLE, "RT Lax:",
                "RT Lax", "", new Double(15.0),
                new Double(0.0), null);
        public static final Parameter rt2Lax = new SimpleParameter(
                ParameterType.DOUBLE, "RT2 Lax:",
                "RT2 Lax", "", new Double(0.3),
                new Double(0.0), null);
        public static final Parameter nameMatchBonus = new SimpleParameter(
                ParameterType.DOUBLE, "Bonus for matching names:",
                "Bonus for matching names", "", new Double(100.0),
                new Double(0.0), null);

        public BasicAlignerGCGCParameters() {
                super(new Parameter[]{peakListName, RT1Tolerance, RT2Tolerance, rt1Lax,rt2Lax, nameMatchBonus});
        }
}
