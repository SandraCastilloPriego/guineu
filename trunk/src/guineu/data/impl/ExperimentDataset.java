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
package guineu.data.impl;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class ExperimentDataset implements Dataset {

    String datasetName;
    Vector<Bexperiments> experiments;
    DatasetType type = DatasetType.EXPERIMENTINFO;
	Vector <String> columnNames;

    public ExperimentDataset(String datasetName) {
        this.datasetName = datasetName;
        this.experiments = new Vector<Bexperiments>();
		columnNames = new Vector<String>();
		columnNames.add("Name");
        columnNames.add("Type");
        columnNames.add("Project");
        columnNames.add("Person");
        columnNames.add("Replicate");
        columnNames.add("Amount");
        columnNames.add("Unit");
        columnNames.add("Method");
        columnNames.add("Sample");
        columnNames.add("Date");
    }

    public void addExperiment(Bexperiments experiment) {
        this.experiments.addElement(experiment);
    }

    public Vector<Bexperiments> getExperiments() {
        return this.experiments;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String name) {
        this.datasetName = name;
    }

    public DatasetType getType() {
        return this.type;
    }

    public Vector<String> getNameExperiments() {
		return columnNames;
    }

    public int getNumberCols() {
        return columnNames.size();
    }

    public int getNumberRows() {
        return experiments.size();
    }

    public PeakListRow getRow(int row) {
        return null;
    }

    public void setType(DatasetType type) {
        this.type = type;
    }
 
    public void removeRow(PeakListRow row) {
         
    }

    public void AddNameExperiment(String nameExperiment) {
        this.columnNames.add(nameExperiment);
    }

    public Vector<PeakListRow> getRows() {
        return null;
    }

    public void AddNameExperiment(String nameExperiment, int position) {
       this.columnNames.set(position, nameExperiment);
    }

	public Dataset clone() {
		Dataset newDataset = new ExperimentDataset(datasetName);
		for(Bexperiments row: experiments){
			((ExperimentDataset)newDataset).addExperiment(row.clone());
		}
		return newDataset;
	}

	public void AddRow(PeakListRow peakListRow) {
		
	}
}
