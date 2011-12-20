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
package guineu.data;

public enum LCMSColumnName {

        /**
         * Fix columns for LC-MS files. Each column has:
         * Column name, getVar function, setVar function, regular Expresion to parse files and type of data. *
         */
        SELECTION("Selection", "isSelected", "setSelectionMode", "Selection", ParameterType.BOOLEAN),
        ID("Id", "getID", "setID", "^ID.*|^Id|.*row ID.*", ParameterType.INTEGER),
        MZ("Average m/z", "getMZ", "setMZ", ".*Average M/Z.*|.*Average m/z.*|.*row m/z.*", ParameterType.DOUBLE),
        RT("Average Retention time", "getRT", "setRT", ".*Average RT.*|.*Average retention time.*|.*row retention time*|.*Average Retention time.*", ParameterType.DOUBLE),
        NAME("Name", "getName", "setName", ".*identity.*|.*LipidName.*|.*Lipid name.*|.*Lipid Name.*|^Name.*|^name.*|^Metabolite name.*|.*row compound name.*|^Metabolite Name.*|^Name", ParameterType.STRING),
        ALLNAMES("All names", "getAllNames", "setAllNames", ".*Identity.*|.*All Names.*|.*All names.*|.*all Names.*|.*row all compound names.*|.*Metabolite all Names.*", ParameterType.STRING),
        IDENTIFICATION("Identification type", "getIdentificationType", "setIdentificationType", ".*Identification type.*", ParameterType.STRING),
        PUBCHEM("PubChem ID", "getPubChemID", "setPubChemID", ".*Pubchem.*|.*ubChem.*|.*PubChem ID.*", ParameterType.STRING),
        VTT("VTT ID", "getVTTID", "setVTTID", "^VTT ID.*", ParameterType.STRING),
        ALLVTT("All VTT IDs", "getAllVTTID", "setAllVTTD", ".*All VTT IDs.*", ParameterType.STRING),
        LIPIDCLASS("Lipid class", "getMolClass", "setLipidClass", ".*Class.*|.*Lipid class.*", ParameterType.STRING),
        NFOUND("Num found", "getNumFound", "setNumFound", ".*um found.*|.*umber of detected peaks.*|.*n_found.*|.*Num Found.*", ParameterType.DOUBLE),
        STANDARD("Standard", "getStandard", "setStandard", ".*Standard.*", ParameterType.INTEGER),
        FA("FA Composition", "getFAComposition", "setFAComposition", ".*FA Composition.*", ParameterType.STRING),
        ALIGNMENT("Alignment", "getNumberAlignment", "setNumberAligment", ".*Aligment.*|.*Alignment.*", ParameterType.INTEGER),
        P("P-value", "getPValue", "setPValue", ".*p-value.*|.*P-value.*", ParameterType.DOUBLE),
        Q("Q-value", "getQValue", "setQValue", ".*q-value.*|.*Q-value.*", ParameterType.DOUBLE);
        
        private final String columnName;
        private final String getFunctionName, setFunctionName;
        private final String regExp;
        private final ParameterType type;

        LCMSColumnName(String columnName,
                String getFunctionName, String setFunctionName,
                String regExp, ParameterType type) {
                this.columnName = columnName;
                this.getFunctionName = getFunctionName;
                this.setFunctionName = setFunctionName;
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
