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

import guineu.data.ParameterType;

public enum GCGCColumnName {

    SELECTION("Selection", true, "isSelected", "setSelectionMode", "", ParameterType.BOOLEAN),
    ID("ID", true, "getID", "setID", "^ID.*", ParameterType.INTEGER),
    RT1("RT1", true, "getRT1", "setRT1", ".*RT1.*", ParameterType.DOUBLE),
    RT2("RT2", true, "getRT2", "setRT2", ".*RT2.*", ParameterType.DOUBLE),
    RTI("RTI", true, "getRTI", "setRTI", ".*RTI.*", ParameterType.DOUBLE),
    NFOUND("N Found", true, "getNumFound", "setNumFound", ".*Num Found.*|.*Number of detected peaks.*|.*n_found.*|.*number of detected peaks.*", ParameterType.DOUBLE),
    CAS("Cas Number", true, "getCAS", "setCAS", ".*CAS.*", ParameterType.STRING),
    MAXSIM("Max similarity", true, "getMaxSimilarity", "setMaxSimilarity", ".*Max sim.*|.*Max Similarity.*", ParameterType.DOUBLE),
    MEANSIM("Mean similarity", true, "getMeanSimilarity", "setMeanSimilarity", ".*Mean sim.*|.*Mean Sim.*", ParameterType.DOUBLE),
    SIMSTD("Similarity std dev", true, "getSimilaritySTDDev", "setSimilaritySTDDev", ".*Similarity std dev.*", ParameterType.DOUBLE),
    NAME("Metabolite name", true, "getName", "setName", ".*LipidName.*|.*Lipid name.*|.*Lipid Name.*|^Name.*|^name.*|^Metabolite name.*|.*row compound name.*|^Metabolite Name.*|", ParameterType.STRING),
    ALLNAMES("Metabolite all names", true, "getAllNames", "setAllNames", ".*Identity.*|.*All Names.*|.*All names.*|.*all Names.*|.*row all compound names.*|.*Metabolite all Names.*", ParameterType.STRING),
    CLASS("Class", true, "getMolClass", "setMolClass", ".*Class.*", ParameterType.STRING),
    PUBCHEM("Pubchem ID", true, "getPubChemID", "setPubChemID", ".*Pubchem.*", ParameterType.STRING),
    MASS("Mass", true, "getMass", "setMass", ".*Mass.*", ParameterType.DOUBLE),
    DIFFERENCE("Difference", true, "getDifference", "setDifference", ".*Difference.*", ParameterType.DOUBLE),
    SPECTRUM("Spectrum", true, "getSpectrumString", "setSpectrumString", ".*Spectrum.*|.*Spectra.*", ParameterType.STRING);
    private final String columnName;
    private final String getFunctionName,  setFunctionName;
    private final boolean common;
    private final String regExp;
    private final ParameterType type;

    GCGCColumnName(String columnName, boolean common, String getFunctionName,
            String setFunctionName, String regExp, ParameterType type) {
        this.columnName = columnName;
        this.getFunctionName = getFunctionName;
        this.setFunctionName = setFunctionName;
        this.common = common;
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

    public boolean isCommon() {
        return this.common;
    }

    public String getRegularExpression() {
        return this.regExp;
    }

    public ParameterType getType() {
        return this.type;
    }

    public String toString() {
        return this.columnName;
    }
}

