/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.data.impl;


import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.PeakListRow_concatenate;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class SimpleDataset_concatenate implements Dataset {

    String datasetName;
    Vector<PeakListRow_concatenate> PeakList;
    Vector<String> nameExperiments;
    DatasetType type;

    public SimpleDataset_concatenate(String datasetName) {
        this.datasetName = datasetName;
        this.PeakList = new Vector<PeakListRow_concatenate>();
        this.nameExperiments = new Vector<String>();
        type = DatasetType.LCMS;
    }

    public String getDatasetName() {
        return this.datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public void AddRow(PeakListRow_concatenate peakListRow) {
        this.PeakList.addElement(peakListRow);
    }

    public void AddNameExperiment(String nameExperiment) {
        this.nameExperiments.addElement(nameExperiment);
    }

    public PeakListRow getRow(int i) {
        return (PeakListRow) this.PeakList.elementAt(i);
    }

    public Vector<PeakListRow_concatenate> getRows() {
        return this.PeakList;
    }

    public int getNumberRows() {
        return this.PeakList.size();
    }

    public int getNumberCols() {
        return this.nameExperiments.size();
    }

    public Vector<String> getNameExperiments() {
        return this.nameExperiments;
    }

    public void setNameExperiments(Vector<String> experimentNames) {
        this.nameExperiments = experimentNames;
    }

    public DatasetType getType() {
        return type;
    }

    public void setType(DatasetType type) {
        this.type = type;
    }

    public boolean containtName(String Name){
        for(String name: this.nameExperiments){
            if(name.compareTo(Name) == 0){
                return true;
            }
            if(name.matches(".*"+Name+".*")){
                return true;
            }
            if(Name.matches(".*"+name+".*")){
                return true;
            }
        }
        return false;
    }
    
    public boolean containRowName(String Name){
        for(PeakListRow_concatenate row : this.getRows()){
            if(row.getPeak("Name").contains(Name) || Name.contains(row.getPeak("Name"))){
                return true;
            }
        }
        return false;
    }
    
    @Override
    public SimpleDataset_concatenate clone() {
        SimpleDataset_concatenate newDataset = new SimpleDataset_concatenate(this.datasetName);
        for (String experimentName : this.nameExperiments) {
            newDataset.AddNameExperiment(experimentName);
        }
        for (PeakListRow_concatenate peakListRow : this.PeakList) {
            newDataset.AddRow(peakListRow.clone());
        }
        newDataset.setType(this.type);
        return newDataset;
    }

    
}
