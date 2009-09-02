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
package guineu.data.parser.impl;

public enum RegExp {

    ID("^ID.*"),
    MZ(".*Average M/Z.*|.*Average m/z.*|.*row m/z.*"),
    RT(".*Average RT.*|.*Average retention time.*|.*retention time*"),
    NAME(".*LipidName.*|.*Lipid name.*|.*Lipid Name.*|^Name.*|^name.*|^Metabolite name.*|.*row compound name.*|^Metabolite Name.*|"),
    ALLNAMES(".*Identity.*|.*All Names.*|.*All names.*|.*all Names.*|.*row all compound names.*|.*Metabolite all Names.*"),
    CLASS(".*Class.*"),
    NFOUND(".*Num Found.*|.*Number of detected peaks.*|.*n_found.*|.*number of detected peaks.*"),
    STANDARD(".*Standard.*"),
    FA(".*FAComposition.*"),
    ALIGNMENT(".*Aligment.*|.*Alignment.*"),
    MASS(".*Mass.*"),
    RT1(".*RT1.*"),
    RT2(".*RT2.*"),
    RTI(".*RTI.*"),
    DIFFERENCE(".*Difference.*"),
    PUBCHEM(".*Pubchem.*"),
    MAXSIM(".*Max sim.*|.*Max Similarity.*"),
    MEANSIM(".*Mean sim.*|.*Mean Sim.*"),
    SIMSTD(".*Similarity std dev.*"),
    SPECTRUM(".*Spectrum.*|.*Spectra.*"),
	CAS(".*CAS.*");
    private final String columnName;

    RegExp(String columnName) {
        this.columnName = columnName;
    }

    public String getREgExp() {
        return this.columnName;
    }
}
