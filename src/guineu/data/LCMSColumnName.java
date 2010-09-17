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
package guineu.data;

public enum LCMSColumnName {

        /**
         * Fix columns for LC-MS files. Each column has:
         * Column name, isShown, getVar function, setVar function, regular Expresion to parse files and type of data. *
         */
        SELECTION("Selection", true, "isSelected", "setSelectionMode", "Selection", ParameterType.BOOLEAN),
        ID("Id", true, "getID", "setID", "^ID.*|^Id|.*row ID.*", ParameterType.INTEGER),
        MZ("Average m/z", true, "getMZ", "setMZ", ".*Average M/Z.*|.*Average m/z.*|.*row m/z.*", ParameterType.DOUBLE),
        RT("Average Retention time", true, "getRT", "setRT", ".*Average RT.*|.*Average retention time.*|.*row retention time*|.*Average Retention time.*", ParameterType.DOUBLE),
        NAME("Name", true, "getName", "setName", ".*LipidName.*|.*Lipid name.*|.*Lipid Name.*|^Name.*|^name.*|^Metabolite name.*|.*row compound name.*|^Metabolite Name.*|", ParameterType.STRING),
        ALLNAMES("All names", true, "getAllNames", "setAllNames", ".*Identity.*|.*All Names.*|.*All names.*|.*all Names.*|.*row all compound names.*|.*Metabolite all Names.*", ParameterType.STRING),
        IDENTIFICATION("Identification type", true, "getIdentificationType", "setIdentificationType", ".*Identification type.*", ParameterType.STRING),
        PUBCHEM("PubChem ID", true, "getPubChemID", "setPubChemID", ".*Pubchem.*|.*ubChem.*|.*PubChem ID.*", ParameterType.STRING),
        VTT("VTT ID", true, "getVTTID", "setVTTID", "^VTT ID.*", ParameterType.STRING),
        ALLVTT("All VTT IDs", true, "getAllVTTID", "setAllVTTD", ".*All VTT IDs.*", ParameterType.STRING),
        LIPIDCLASS("Lipid class", true, "getMolClass", "setLipidClass", ".*Class.*|.*Lipid class.*", ParameterType.STRING),
        NFOUND("Num found", true, "getNumFound", "setNumFound", ".*um found.*|.*umber of detected peaks.*|.*n_found.*|.*Num Found.*", ParameterType.DOUBLE),
        STANDARD("Standard", true, "getStandard", "setStandard", ".*Standard.*", ParameterType.INTEGER),
        FA("FA Composition", true, "getFAComposition", "setFAComposition", ".*FA Composition.*", ParameterType.STRING),
        ALIGNMENT("Alignment", true, "getNumberAlignment", "setNumberAligment", ".*Aligment.*|.*Alignment.*", ParameterType.INTEGER);
        private final String columnName;
        private final String getFunctionName, setFunctionName;
        private final boolean isShown;
        private final String regExp;
        private final ParameterType type;

        LCMSColumnName(String columnName, boolean isShown,
                String getFunctionName, String setFunctionName,
                String regExp, ParameterType type) {
                this.columnName = columnName;
                this.getFunctionName = getFunctionName;
                this.setFunctionName = setFunctionName;
                this.isShown = isShown;
                this.regExp = regExp;
                this.type = type;
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

        public boolean isColumnShown() {
                return this.isShown;
        }

        public String getRegularExpression() {
                return this.regExp;
        }

        public ParameterType getType() {
                return this.type;
        }

        @Override
        public String toString() {
                return this.columnName;
        }
}
