/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.database.intro;

import guineu.data.Dataset;
import guineu.data.impl.Bexperiments;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimpleParameterSet;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;
import oracle.jdbc.*;
import oracle.jdbc.pool.OracleDataSource;

/**
 *
 * @author scsandra
 */
public class InOracle implements InDataBase {

    float progress;

    public Connection connect() {
        try {
            OracleDataSource oracleDataSource;
            String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@sboracle1.ad.vtt.fi:1521:BfxDB";
            String ORACLE_QUERY_USER = "sandra";
            String ORACLE_QUERY_PASSWORD = "sandra";
            oracleDataSource = new OracleDataSource();
            oracleDataSource.setURL(ORACLE_DATABASE_URL);
            oracleDataSource.setUser(ORACLE_QUERY_USER);
            oracleDataSource.setPassword(ORACLE_QUERY_PASSWORD);
            Connection con = oracleDataSource.getConnection();
            return con;
        } catch (SQLException exception) {
            System.out.println("ERROR : " + exception);
            exception.printStackTrace(System.out);
            return null;
        }
    }

    public float getProgress() {
        return progress;
    }

    /**
     * 
     * @param conn
     * @param lmcs_known
     * @param tipe
     * @param author
     * @throws java.io.IOException
     */
    public void lcms(Connection conn, SimpleDataset LipidMol, String type, String author) throws IOException {
        WriteDataBase writer = new WriteDataBase();
        String excel_name = LipidMol.getDatasetName();
        if (excel_name == null) {
            excel_name = "unknown";
        }
        //Intro table DATASET_EXPERIMENTS
        int excel_id = writer.tableDATASET(conn, excel_name, type, author);        
        progress = 0.25f;
        writer.tableEXPERIMENT(conn, LipidMol, excel_id);
        progress = 0.50f;
        //Intro table MOL_LCMMS	
        int[] mol_ID = writer.tableMOL_LCMS(conn, LipidMol, excel_id);
        progress = 0.75f;
        //Intro table MEASUREMENT       
        writer.tableMEASUREMENT(conn, LipidMol, mol_ID, excel_id);
        progress = 1f;
    }

    /**
     * All tables to GCGC-Tof files
     * @param conn
     * @param mol
     * @param type
     * @param author
     */
    public void gcgctof(Connection conn, SimpleDataset mol, String type, String author) throws IOException {
        try {
            WriteDataBase writer = new WriteDataBase();
            Statement st = null;
            //Intro table DATASET
            String excel_name = mol.getDatasetName();
            //Intro table DATASET_EXPERIMENTS
           // int sampleID = writer.tableEXPERIMENT(conn, mol);
            progress = 0.15f;
         //   int exp_id = writer.tableDATASET(conn, excel_name, type, author, sampleID);
            progress = 0.25f;
            //Intro table GCGCTof
           // int[] mol_ID = writer.tableMOL_GCGCTof(conn, mol, exp_id);
            progress = 0.50f;
            //Intro table MEASUREMENT
         //   writer.tableMEASUREMENT(conn, mol, mol_ID, exp_id);
            progress = 0.75f;
            //Intro table SPECTRUM
         //   writer.tableSPECTRUM(conn, mol, st, mol_ID);
         //   writer.get_spectrum(""/*((SimpleMetabolite) mol.getMolecule(0)).getSpectrum()*/);
            progress = 1f;
        } catch (Exception exception) {
            System.out.println("Inoracle2.java ---> gcgctof() " + exception);
        }
    }

    /**
     * 
     * @param dataset
     * @param path
     */
    public void WriteExcelFile(Dataset dataset, String path, SimpleParameterSet parameters) {
        WriteFile writer = new WriteFile();
        if (dataset.getType() == DatasetType.LCMS) {
            writer.WriteExcelFileLCMS(dataset, path, parameters);
        } else if(dataset.getType() == DatasetType.GCGCTOF){
            writer.WriteExcelFileGCGC(dataset, path, parameters);
        }else{
			writer.WriteXLSFileconcatenate(dataset, path);
		}
    }

    /**
     * 
     * @param dataset
     * @param path
     */
    public void WriteCommaSeparatedFile(Dataset dataset, String path, SimpleParameterSet parameters) {
        WriteFile writer = new WriteFile();
        if (dataset.getType() == DatasetType.LCMS) {
            writer.WriteCommaSeparatedFileLCMS(dataset, path, parameters);
        } else if (dataset.getType() == DatasetType.GCGCTOF){
            writer.WriteCommaSeparatedFileGCGC(dataset, path, parameters);
        }else{
            writer.WriteCommaSeparatedFileconcatenate(dataset, path);
        }
    }

    /**
     * From "x || y || w || ..." 
     * Return:
     * 		string[0] = x
     * 		string[1] = y
     * 		  ...
     */
    public String[] get_metname(String met_name) {
        if (met_name == null) {
            return null;
        }
        String[] str = new String[100];
        int i = 0;
        for (i = 0; met_name.indexOf("||") > -1; i++) {
            str[i] = met_name.substring(0, met_name.indexOf("||") - 1);
            met_name = met_name.substring(met_name.indexOf("||") + 3);
        }
        str[i] = met_name.substring(0);

        return str;
    }

    public void deleteDataset(Connection conn, int datasetID) {
        progress = 0.1f;
        try {
            Statement statement = conn.createStatement();
            progress = 0.2f;

            //Updating EXPERIMENT table
            int experimentID = 0;
            ResultSet r = statement.executeQuery("SELECT * FROM DATASET WHERE DATASETID = '" + datasetID + "'");
            if (r.next()) {
                experimentID = r.getInt("EXPERIMENTID");
            }

            //removing the dataset 
            statement.executeUpdate("UPDATE EXPERIMENT SET" +
                    " ID_DATASET = '" +
                    "' where ID_DATASET = '" + experimentID + "'");


            statement.executeUpdate("DELETE FROM DATASET WHERE DATASETID = '" + datasetID + "'");
            progress = 0.9f;
        } catch (Exception exception) {
        }
        progress = 1f;
    }

    public void tableEXPERIMENT(Connection conn, Vector<Bexperiments> experiment, String logidir, String sysdir) {
        ExperimentTable table = new ExperimentTable();
        table.IntroExperimentTable(conn, experiment, logidir, sysdir, progress);
    }
}
