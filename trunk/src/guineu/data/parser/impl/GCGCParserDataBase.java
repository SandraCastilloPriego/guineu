/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.data.parser.impl;


import guineu.data.parser.Parser;
import guineu.data.Dataset;
import guineu.database.ask.DBask;
import guineu.database.ask.DataBase;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class GCGCParserDataBase implements Parser{
    private DataBase db;
    private int datasetID;
    private float progress = 0.0f;
    private String datasetName;
 
    
    public GCGCParserDataBase(int ID){
        db = new DBask();
        this.datasetID = ID;    
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void fillData() {
        progress = 0.1f;
        datasetName = db.getDatasetName(datasetID);
        progress = 0.4f;
  
        
        progress = 1.0f;
    }

    

    public float getProgress() {
        return progress;
    }

    public Dataset getDataset() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
   /* public void fillExperimentVector(){
        Vector<String> experimentNames = db.get_samplenames(datasetID); 
        for(String Name : experimentNames){
            progress += 0.2f/experimentNames.size();
            SimpleExperiment experiment = new SimpleExperiment(Name);
            Vector<Double> concentration = db.get_concentration(Name, datasetID);            
            for(int i = 0; i < concentration.size(); i++){
                experiment.setConcentration(concentration.elementAt(i).doubleValue());
            }
            Experiments.addElement(experiment);
        }
    }*/

}
