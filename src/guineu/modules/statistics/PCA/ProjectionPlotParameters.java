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

package guineu.modules.statistics.PCA;

import guineu.data.Dataset;
import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;
import java.util.List;
import java.util.Vector;

public class ProjectionPlotParameters extends SimpleParameterSet {

	// Normal (stored) parameters
	public static final String ColoringTypeSingleColor = "No coloring";
	public static final String ColoringTypeByParameterValue = "Color by parameter value";
	public static final String ColoringTypeByFile = "Color by file";
	private static final String[] ColoringTypePossibleValues = {
			ColoringTypeSingleColor, ColoringTypeByParameterValue,
			ColoringTypeByFile };

	public static final Parameter coloringType = new SimpleParameter(
			ParameterType.STRING, "Coloring type", "Measure peaks using",
			ColoringTypeSingleColor, ColoringTypePossibleValues);

	public static final Integer[] componentPossibleValues = { 1, 2, 3, 4, 5 };

	public static final Parameter xAxisComponent = new SimpleParameter(
			ParameterType.INTEGER, "X-axis component",
			"Component on the X-axis", componentPossibleValues[0],
			componentPossibleValues);

	public static final Parameter yAxisComponent = new SimpleParameter(
			ParameterType.INTEGER, "Y-axis component",
			"Component on the Y-axis", componentPossibleValues[1],
			componentPossibleValues);

	// Non-stored parameter values

	private Dataset sourcePeakList;

	private Parameter selectedParameter; // Parameter used when coloring by
	// parameter value
	private Vector<String> selectedDataFiles;
	private List<PeakListRow> selectedRows;

	public ProjectionPlotParameters(Dataset sourcePeakList) {
		this();
		
		this.sourcePeakList = sourcePeakList;
		this.selectedDataFiles = sourcePeakList.getNameExperiments();
		this.selectedRows = sourcePeakList.getRows();

		selectedParameter = null;
	}

	private ProjectionPlotParameters(Dataset sourcePeakList,
			Object coloringTypeValue, Object xAxisComponentNumber, Object yAxisComponentNumber,
			Parameter selectedParameter, Vector<String> selectedDataFiles,
			List<PeakListRow> selectedRows) {

		this();
		
		setParameterValue(coloringType, coloringTypeValue);		
		setParameterValue(xAxisComponent, xAxisComponentNumber);
		setParameterValue(yAxisComponent, yAxisComponentNumber);

		this.sourcePeakList = sourcePeakList;
		this.selectedParameter = selectedParameter;
		this.selectedDataFiles = selectedDataFiles;
		this.selectedRows = selectedRows;
	}

	/**
	 * Represent method's parameters and their values in human-readable format
	 */
	public String toString() {
		return "Coloring mode: " + getParameterValue(coloringType)			
				+ ", selected parameter: " + selectedParameter;
	}

	public ProjectionPlotParameters clone() {
		return new ProjectionPlotParameters(sourcePeakList, coloringType,
				xAxisComponent, yAxisComponent,
				selectedParameter, selectedDataFiles, selectedRows);
	}

	public Vector<String> getSelectedDataFiles() {
		return selectedDataFiles;
	}

	public void setSelectedDataFiles(Vector<String> selectedDataFiles) {
		this.selectedDataFiles = selectedDataFiles;
	}

	public Parameter getSelectedParameter() {
		return selectedParameter;
	}

	public void setSelectedParameter(Parameter selectedParameter) {
		this.selectedParameter = selectedParameter;
	}

	public List<PeakListRow> getSelectedRows() {
		return selectedRows;
	}

	public void setSelectedRows(List<PeakListRow> selectedRows) {
		this.selectedRows = selectedRows;
	}

	public Dataset getSourcePeakList() {
		return sourcePeakList;
	}

	public void setSourcePeakList(Dataset sourcePeakList) {
		this.sourcePeakList = sourcePeakList;
	}

	
	public ProjectionPlotParameters() {
	    super(new Parameter[] { coloringType, xAxisComponent,
	    		yAxisComponent });
	}
}
