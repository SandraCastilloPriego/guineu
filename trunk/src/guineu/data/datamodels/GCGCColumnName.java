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

public enum GCGCColumnName {

	SELECTION("Selection", true, "isSelected", "setSelectionMode"),
	ID("ID", true, "getID", "setID"),
	RT1("RT1", true, "getRT1", "setRT1"),
	RT2("RT2", true, "getRT2", "setRT2"),
	RTI("RTI", true, "getRTI", "setRTI"),
	NFOUND("N Found", true, "getNumFound", "setNumFound"),
	CAS("Cas Number", true, "getCAS", "setCAS"),
	MAXSIM("Max similarity", true, "getMaxSimilarity", "setMaxSimilarity"),
	MEANSIM("Mean similarity", true, "getMeanSimilarity", "setMeanSimilarity"),
	SIMSTD("Similarity std dev", true, "getSimilaritySTDDev", "setSimilaritySTDDev"),
	NAME("Metabolite name", true, "getName", "setName"),
	ALLNAMES("Metabolite all names", true, "getAllNames", "setAllNames"),
	CLASS("Class", true, "getMolClass", "setMolClass"),
	PUBCHEM("Pubchem ID", true, "getPubChemID", "setPubChemID"),
	MASS("Mass", true, "getMass", "setMass"),
	DIFFERENCE("Difference", true, "getDifference", "setDifference"),
	SPECTRUM("Spectrum", true, "getSpectrumString", "setSpectrumString");
	private final String columnName;
	private final String getFunctionName,  setFunctionName;
	private final boolean common;

	GCGCColumnName(String columnName, boolean common, String getFunctionName, String setFunctionName) {
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

