/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
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
	
    
}
