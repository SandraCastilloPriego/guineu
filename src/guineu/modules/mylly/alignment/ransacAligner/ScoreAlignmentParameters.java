/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.modules.mylly.alignment.ransacAligner;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class ScoreAlignmentParameters extends SimpleParameterSet {


	public static final Parameter rt1Lax = new SimpleParameter(
			ParameterType.DOUBLE, "RT Lax:",
			"RT Lax", "", new Double(15.0),
			new Double(0.0), null);
	public static final Parameter rt2Lax = new SimpleParameter(
			ParameterType.DOUBLE, "RT2 Lax:",
			"RT2 Lax", "", new Double(0.3),
			new Double(0.0), null);
	public static final Parameter rt1Penalty = new SimpleParameter(
			ParameterType.DOUBLE, "RT penalty:",
			"RT penalty", "", new Double(5.0),
			new Double(0.0), null);
	public static final Parameter rt2Penalty = new SimpleParameter(
			ParameterType.DOUBLE, "RT2 penalty:",
			"RT2 penalty", "", new Double(35.0),
			new Double(0.0), null);
	public static final Parameter minSpectrumMatch = new SimpleParameter(
			ParameterType.DOUBLE, "Minimun Spectrum Match:",
			"Minimun Spectrum Match", "", new Double(0.75),
			new Double(0.0), null);
	public static final Parameter nameMatchBonus = new SimpleParameter(
			ParameterType.DOUBLE, "Bonus for matching names:",
			"Bonus for matching names", "", new Double(100.0),
			new Double(0.0), null);
	public static final Parameter rtiPenalty = new SimpleParameter(
			ParameterType.DOUBLE, "RTI penalty",
			"RTI penalty", "", new Double(40.0),
			new Double(0.0), null);
	public static final Parameter minSimilarity = new SimpleParameter(
			ParameterType.DOUBLE, "Drop peaks with similarity less than:",
			"Drop peaks with low similarity", "", new Double(600.0),
			new Double(0.0), null);
	public static final Parameter rtiLax = new SimpleParameter(
			ParameterType.DOUBLE, "RI Lax:",
			"RI Lax", "", new Double(20.0),
			new Double(0.0), null);
	
	public static final Parameter useConcentration= new SimpleParameter(
			ParameterType.BOOLEAN, "Use Concentrations: ",
			"Use Concentrations", null, true, null);
	
	
	public ScoreAlignmentParameters() {
		super(new Parameter[]{rt1Lax, rt2Lax, rtiLax, rt1Penalty, rt2Penalty, rtiPenalty, minSpectrumMatch, nameMatchBonus, minSimilarity, useConcentration});
	}
}
