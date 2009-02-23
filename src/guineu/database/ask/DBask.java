/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.database.ask;

import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import oracle.jdbc.pool.OracleDataSource;

/**
 * @author scsandra
 *
 */
public class DBask implements DataBase {

    public DBask() {
    }

    public synchronized Connection connect() {
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

    public String[][] get_dataset() {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM DATASET ORDER BY DATASETID asc");

            Vector<String[]> vt = new Vector<String[]>();
            while (r.next()) {
                String[] data = new String[5];               
                for (int i = 0; i < 5; i++) {
                    data[i] = r.getString(i + 1);
                }
                vt.add(data);
            }
            String[][] datafinal = new String[vt.size()][5];
            for (int i = 0; i < vt.size(); i++) {
                String[] data = (String[]) vt.elementAt(i);
                for (int e = 0; e < 5; e++) {
                    datafinal[i][e] = data[e];
                }
            }
            r.close();
            st.close();
            return datafinal;

        } catch (Exception e) {
            return null;
        }
    }

    public Vector<Double> get_concentration(String sampleName) {
        Statement st = null;
        Connection conn = this.connect();
        int id_exp = 0;
        Vector<Double> vt = new Vector<Double>();
        try {
            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT WHERE NAME = '" + sampleName + "' ORDER BY EPID asc");

            if (r.next()) {
                id_exp = r.getInt(1);
            }
            r.close();

            if (id_exp != 0) {
                r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE SAMPLE_ID = '" + id_exp + "' ORDER BY ID asc");

                while (r.next()) {
                    vt.add(r.getDouble(5));
                }

                r.close();
                st.close();
            }
            return vt;
        } catch (Exception exception) {
            return null;
        }
    }

    /**
     * 
     * @param sampleName
     * @param exp_id
     * @return
     */
    public Vector<Double> get_concentration(String sampleName, int datasetID) {
        Statement st = null;
        Connection conn = this.connect();
        int id_exp = 0;
        Vector<Double> vt = new Vector<Double>();
        try {
            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT WHERE NAME = '" + sampleName + "' ORDER BY EPID asc");

            if (r.next()) {
                id_exp = r.getInt(1);
            }
            r.close();

            if (id_exp != 0) {
                r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE SAMPLE_ID = '" + id_exp + "'AND DATASETID ='" + datasetID + "' ORDER BY ID asc");

                while (r.next()) {
                    vt.add(r.getDouble(5));
                }

                r.close();
                st.close();
            }
            return vt;
        } catch (Exception exception) {
            return null;
        }
    }

    public Vector<String> get_dataset_experiment(Vector sample_names, int exp_id) {
        Statement st = null;
        try {
            Connection conn = this.connect();
            int id_exp = 0;
            Vector<String> vt = new Vector<String>();
            for (int i = 0; i < sample_names.size(); i++) {
                st = conn.createStatement();
                ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT WHERE NAME = '" + sample_names.elementAt(i) + "' DATASETID = '" + exp_id + "' ORDER BY EPID asc");

                if (r.next()) {
                    id_exp = r.getInt(1);
                }
                r.close();
                if (id_exp != 0) {
                    r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE SAMPLE_ID = '" + id_exp + "' ORDER BY ID asc");

                    while (r.next()) {
                        vt.add(r.getString(5));
                    }

                    r.close();
                    st.close();
                }
            }
            return vt;

        } catch (Exception e) {
            return null;
        }
    }

    public Vector<String> get_dataset_experiment(int exp_id) {
        Statement st = null;
        try {
            Connection conn = this.connect();
            int id_exp = 0;
            Vector<String> vt = new Vector<String>();

            st = conn.createStatement();
            if (id_exp != 0) {
                ResultSet r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE DATASETID = '" + exp_id + "' ORDER BY ID asc");

                while (r.next()) {
                    vt.add(r.getString(5));
                }
                r.close();
                st.close();
            }
            return vt;

        } catch (Exception e) {
            return null;
        }
    }

    public Vector<String> get_samplenames(int datasetID) {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT WHERE ID_DATASET = '" + datasetID + "'ORDER BY EPID asc");

            Vector<String> vt = new Vector<String>();
            while (r.next()) {
                vt.add(r.getString("NAME"));
            }
            r.close();
            st.close();
            return vt;

        } catch (Exception e) {
            return null;
        }
    }

    public Vector<Integer> get_sampleID(int datasetID) {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM DATASET WHERE DATASETID = '" + datasetID + "'");
            Vector<Integer> vt = new Vector<Integer>();
            if (r.next()) {
                vt.addElement(r.getInt("EXPERIMENTID"));
            }
            r.close();
            st.close();
            return vt;

        } catch (Exception e) {
            return null;
        }
    }

    public Vector get_allsamplenames() {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT ORDER BY EPID asc");

            Vector<String[]> vt = new Vector<String[]>();
            while (r.next()) {
                String[] dataSample = new String[2];
                dataSample[0] = r.getString(2);
                dataSample[1] = r.getString(1);
                vt.add(dataSample);
            }
            r.close();
            st.close();
            return vt;

        } catch (Exception e) {
            return null;
        }
    }

    public String get_ID(String sampleName) {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT WHERE NAME = '" + sampleName + "'ORDER BY EPID asc");

            String ID = new String();
            if (r.next()) {
                ID = r.getString(14);
            }
            r.close();
            st.close();
            return ID;

        } catch (Exception e) {
            return null;
        }
    }

    public boolean getType(int ID) {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM DATASET WHERE DATASETID = '" + ID + "'");

            String type = new String();
            if (r.next()) {
                type = r.getString(3);
            }

            r.close();
            st.close();
            if (type.compareTo("LCMS") == 0) {
                return false;
            } else {
                return true;
            }

        } catch (Exception e) {
            return false;
        }
    }

    public Vector get_lipid_info(String commonName) {
        Statement st = null;

        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM LIPID_NAME WHERE AVR_COMMON_NAME = '" + commonName + "'");
            Vector<String[]> vt = new Vector<String[]>();
            while (r.next()) {
                String[] lipid_info = new String[5];
                for (int i = 3; i < 8; i++) {
                    lipid_info[i - 3] = r.getString(i);
                }
                vt.addElement(lipid_info);
            }

            r.close();
            st.close();

            return vt;

        } catch (Exception e) {
            return null;
        }

    }

    public String getDatasetName(Vector ExperimentNames) {
        Statement st = null;

        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            String query = "SELECT * FROM EXPERIMENT WHERE NAME LIKE '" + ExperimentNames.elementAt(0) + "'";
            for (int i = 1; i < ExperimentNames.size(); i++) {
                query = query + "OR NAME LIKE '" + ExperimentNames.elementAt(i) + "'";
            }
            query = query + "ORDER BY EPID asc";

            ResultSet r = st.executeQuery(query);

            Vector<String> vt = new Vector<String>();
            while (r.next()) {
                vt.addElement(r.getString(3));
            }
            String DatasetID = vt.elementAt(0);
            for (int i = 0; i < vt.size(); i++) {
                if (DatasetID.compareTo(vt.elementAt(i)) != 0) {
                    return "unknown";
                }
            }

            r = st.executeQuery("SELECT * FROM DATASET WHERE DATASETID = '" + vt.elementAt(0) + "'");

            String DatasetName = new String();
            if (r.next()) {
                DatasetName = r.getString(2);
            }

            r.close();
            st.close();

            return DatasetName;

        } catch (Exception e) {
            return null;
        }

    }

    public String getDatasetName(int ID) {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM DATASET WHERE DATASETID = '" + ID + "'");

            String name = new String();
            if (r.next()) {
                name = r.getString(2);
            }

            r.close();
            st.close();
            return name;

        } catch (Exception e) {
            return null;
        }

    }

    /*public synchronized Vector<String> get_spectrum(String ID){
    Statement st = null;	
    try {
    Connection conn = this.connect();
    st = conn.createStatement();
    ResultSet r = st.executeQuery ("SELECT * FROM MOL_GCGCTOF WHERE EPID = '"+ ID +"'ORDER BY ID asc");						
    Vector<String> vt = new Vector<String>();
    while(r.next()){				
    vt.addElement(r.getString(1));									
    }			
    r.close();		   	
    st.close();
    return vt;
    } catch (Exception e) {	
    return null;
    }  	
    }*/
    private synchronized Hashtable<Integer, String> getExperimentsID(int datasetID) {
        Statement st = null;
        try {
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM EXPERIMENT WHERE ID_DATASET = '" + datasetID + "'ORDER BY EPID asc");

            Hashtable<Integer, String> vt = new Hashtable<Integer, String>();
            while (r.next()) {
                try {                   
                    vt.put(r.getInt("EPID"), r.getString("NAME"));
                } catch (Exception ee) {
                }
            }
            r.close();
            st.close();
            return vt;

        } catch (Exception e) {
            return null;
        }
    }

    public synchronized void getLCMSRows(int DatasetID, SimpleDataset dataset) {
        Statement st = null;

        try {
            Hashtable<Integer, String> experimentIDs = this.getExperimentsID(DatasetID);
            Connection conn = this.connect();

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM MOL_LCMS WHERE EPID = '" + DatasetID + "'");
            while (r.next()) {
                SimplePeakListRowLCMS peakListRow = new SimplePeakListRowLCMS();
                peakListRow.setMZ(r.getFloat("AVERAGE_MZ"));
                peakListRow.setRT(r.getFloat("AVERAGE_RT"));
                peakListRow.setNumFound(r.getInt("N_FOUND"));
                peakListRow.setLipidClass(r.getInt("LIPID_CLASS"));
                peakListRow.setAllNames(r.getString("IDENTITY"));
                peakListRow.setStandard(r.getInt("STD"));
                peakListRow.setName(r.getString("LIPID_NAME"));
                peakListRow.setFAComposition(r.getString("FA_COMPOSITION"));
                peakListRow.setID(r.getInt("DATASET_ID"));
                this.setPeaks(experimentIDs, peakListRow, r.getInt("ID"), conn);
                dataset.AddRow(peakListRow);
            }
            r.close();
            st.close();
        } catch (Exception e) {

        }
    }

    private synchronized void setPeaks(Hashtable<Integer, String> experimentIDs, SimplePeakListRowLCMS peakListRow, int molID, Connection conn) {
        Statement st = null;
        try {

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE MOL_LCMS_ID = '" + molID + "' ORDER BY ID asc");

            while (r.next()) {
                try {                   
                    if (experimentIDs.containsKey(new Integer(r.getInt("SAMPLE_ID")))) {
                        System.out.println(new Double(r.getFloat("CONCENTRATION")));
                        peakListRow.setPeak(experimentIDs.get(new Integer(r.getInt("SAMPLE_ID"))), new Double(r.getFloat("CONCENTRATION")));
                    }
                } catch (Exception ee) {

                }
            }
            r.close();
            st.close();


        } catch (Exception e) {

        }

    }
}



