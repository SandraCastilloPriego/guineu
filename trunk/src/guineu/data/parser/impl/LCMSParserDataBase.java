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
package guineu.data.parser.impl;


import guineu.data.parser.Parser;
import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.database.ask.DBask;
import guineu.database.ask.DataBase;
import java.util.Vector;


/**
 *
 * @author scsandra
 */
public class LCMSParserDataBase implements Parser{
    private DataBase db;
    private SimpleLCMSDataset dataset;
    private int datasetID;
    private String datasetName;    
    private float progress = 0.0f;
    
    public LCMSParserDataBase(int datasetID){
        db = new DBask();
        this.datasetID = datasetID; 
        this.dataset = new SimpleLCMSDataset(db.getDatasetName(datasetID));		
    }
    
    public void fillData(){         
        progress = 0.1f;
		
        db.get_samplenames(datasetID, dataset);
			
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
