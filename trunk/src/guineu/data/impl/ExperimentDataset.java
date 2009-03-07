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
    
    public ExperimentDataset(String datasetName){
        this.datasetName = datasetName;
        this.experiments = new Vector<Bexperiments>();
    }
    
    public void addExperiment(Bexperiments experiment){
        this.experiments.addElement(experiment);
    }
    
    public Vector<Bexperiments> getExperiments(){
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

    public Iterable<String> getNameExperiments() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumberCols() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumberRows() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public PeakListRow getRow(int row) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

	public void setType(DatasetType type) {
		this.type = type;
	}

    public int getNumberFixColumns() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setNumberFixColumns(int columnNum) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
	
    
}
