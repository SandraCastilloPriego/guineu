/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.data.parser.impl;


import guineu.data.parser.Parser;
import guineu.data.Dataset;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.database.ask.DBask;
import guineu.database.ask.DataBase;
import java.util.Vector;


/**
 *
 * @author scsandra
 */
public class LCMSParserDataBase implements Parser{
    private DataBase db;
    private SimpleDataset dataset;
    private int datasetID;
    private String datasetName;    
    private float progress = 0.0f;
    
    public LCMSParserDataBase(int datasetID){
        db = new DBask();
        this.datasetID = datasetID; 
        this.dataset = new SimpleDataset(db.getDatasetName(datasetID));        
    }
    
    public void fillData(){         
        progress = 0.1f;      
        Vector<String> experimentNames = db.get_samplenames(datasetID);       
        for(String experimentName : experimentNames){
            this.dataset.AddNameExperiment(experimentName);
        }     
        progress = 0.4f;
        db.getLCMSRows(this.datasetID, dataset);  
        progress = 0.8f;
        dataset.setType(DatasetType.LCMS);
        progress = 1f;
    }
    
    public String getDatasetName() {
        return datasetName;
    }
     
    public float getProgress() {
        return progress;
    }

    public Dataset getDataset() {
        return this.dataset;
    }
    
}
