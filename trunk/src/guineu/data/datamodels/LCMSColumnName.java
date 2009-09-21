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
package guineu.data.datamodels;

public enum LCMSColumnName {

    SELECTION("Selection", true, "isSelected", "setSelectionMode"),
    ID("Id", true, "getID", "setID"),
    MZ("m/z", true, "getMZ", "setMZ"),
    RT("Retention time", true, "getRT", "setRT"),
    NAME("Name", true, "getName", "setName"),
    ALLNAMES("All names", true, "getAllNames", "setAllNames"),
    PUBCHEM("PubChem ID", true, "getPubChemID", "setPubChemID"),
    VTT("VTT ID", true, "getVTTID", "setVTTD"),
    ALLVTT("All VTT IDs", true, "getAllVTTID", "setAllVTTD"),
    LIPIDCLASS("Lipid class", true, "getMolClass", "setLipidClass"),
    NFOUND("Num found", true, "getNumFound", "setNumFound"),
    STANDARD("Standard", true, "getStandard", "setStandard"),
    FA("FA Composition", true, "getFAComposition", "setFAComposition"),
    ALIGNMENT("Alignment", true, "getNumberAlignment", "setNumberAligment");
    private final String columnName;
    private final String getFunctionName,  setFunctionName;
    private final boolean common;

    LCMSColumnName(String columnName, boolean common, String getFunctionName, String setFunctionName) {
        this.columnName = columnName;
        this.getFunctionName = getFunctionName;
        this.setFunctionName = setFunctionName;
        this.common = common;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public String getGetFunctionName() {
        return this.getFunctionName;
    }

    public String getSetFunctionName() {
        return this.setFunctionName;
    }

    public boolean isCommon() {
        return this.common;
    }

    public String toString() {
        return this.columnName;
    }
}
