/*
 * Copyright 2007-2010 VTT Biotechnology
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

package guineu.data.parser.impl.database;


import guineu.data.parser.Parser;
import guineu.data.Dataset;
import guineu.database.retrieve.impl.OracleRetrievement;
import guineu.database.retrieve.DataBase;

/**
 *
 * @author scsandra
 */
public class GCGCParserDataBase implements Parser{
    private DataBase db;
    private int datasetID;
    private float progress = 0.0f;
    private String datasetName;
 
    
    public GCGCParserDataBase(Dataset dataset){
        db = new OracleRetrievement();       
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void fillData() {
        progress = 0.1f;
       // datasetName = db.getDatasetName(datasetID);
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
        Vector<String> experimentNames = db.getSampleNames(datasetID);
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
