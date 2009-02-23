/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.database.ask;

import guineu.data.impl.SimpleDataset;
import java.sql.Connection;
import java.util.Vector;


/**
 *
 * @author scsandra
 */
public interface DataBase {
    /**
     * Connects to the database
     * @return Return the connection 
     */
    public Connection connect();
    
    /**
     * 
     * @return
     */
    public String[][] get_dataset();    
    /**
     * 
     * @param sample_names
     * @return
     */
   // public Vector<String> get_dataset_experiment(Vector sample_names);
    
    /**
     * 
     * @param ID
     * @return
     */
    public Vector<String> get_samplenames(int ID);
    public Vector get_allsamplenames();
    public String get_ID(String sampleName);
    public boolean getType(int ID);
    public Vector get_lipid_info(String commonName);
    public String getDatasetName(Vector ExperimentNames);
    public String getDatasetName(int ID);
    public Vector<Double> get_concentration(String sampleName, int exp_id);
    public void getLCMSRows(int ID, SimpleDataset dataset); 
    
}
