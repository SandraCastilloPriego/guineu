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

package guineu.modules.identification.AdductSearch;

import guineu.main.GuineuCore;


public enum AdductType {

	ALLRELATED("All related peaks", 0.0),
        Difference1("[M+NH4]", 17.027),
        Difference2("[M+Na]", 21.982),
        Difference3("[[M+NH4] - [M+Na]]", 4.955),
        NegDifference1("[M-CH3]", 14.016),
        NegDifference2("[M+HCOO]", 46.006),
        NegDifference3("[M+C2H3OO], [[M-CH3] - [M+HCOO]]", 60.022),
        NegDifference4("[[M-CH3] - [M+C2H3OO]]", 74.038),
	Na("[M+Na-H]", 21.9825),
	K("[M+K-H]", 37.9559),
	Mg("[M+Mg-2H]", 21.9694),
	NH3("[M+NH3]", 17.0265),
	Phosphate("[M+H3PO4]", 97.9769),
	Sulfate("[M+H2SO4]", 97.9674),
	Carbonate("[M+H2CO3]", 62.0004),
	Glycerol("[(Deuterium)]glycerol", 5.0),
	CUSTOM("Custom", 0.0);

	private final String name;
	private final double massDifference;

	AdductType(String name, double massDifference) {
		this.name = name;
		this.massDifference = massDifference;
	}

	public String getName() {
		return this.name;
	}

	public double getMassDifference() {
		return this.massDifference;
	}

	public String toString() {
		return this.name + " "
				+ GuineuCore.getMZFormat().format(this.massDifference) + " m/z";
	}
}
