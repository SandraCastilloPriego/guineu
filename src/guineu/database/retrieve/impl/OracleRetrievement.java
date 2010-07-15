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
package guineu.database.retrieve.impl;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.database.retrieve.*;
import guineu.data.impl.SimpleLCMSDataset;
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
public class OracleRetrievement implements DataBase {

        private int totalRows;
        private int completedRows;

        public OracleRetrievement() {
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
                        exception.printStackTrace();
                        return null;
                }
        }

        public String[] getProjectList() {
                Statement st = null;
                String[] noProjects = {"No Projects"};
                List<String> projectNames = new ArrayList<String>();


                try {
                        Connection conn = this.connect();
                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT NAME FROM PROJECTS");

                        while (r.next()) {
                                projectNames.add(r.getString("NAME"));
                        }
                        r.close();
                        st.close();
                        conn.close();
                        if (projectNames.size() == 0) {
                                return noProjects;
                        }
                        return projectNames.toArray(new String[0]);
                } catch (Exception exception) {
                        return noProjects;
                }
        }

        public int[] getStudiesFromProject() {
                return null;
        }

        public String[][] getDatasetInfo() {
                Statement st = null;
                try {
                        Connection conn = this.connect();

                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT * FROM DATASET ORDER BY DATASETID asc");

                        Vector<String[]> vt = new Vector<String[]>();
                        while (r.next()) {
                                String[] data = new String[6];
                                data[0] = r.getString("DATASETID");
                                data[1] = r.getString("EXCEL_NAME");
                                data[2] = r.getString("D_TYPE");
                                data[3] = r.getString("AUTHOR");
                                data[4] = r.getString("D_DATE");
                                data[5] = r.getString("NUMBER_ROWS");
                                vt.add(data);
                        }
                        String[][] datafinal = new String[vt.size()][6];
                        for (int i = 0; i < vt.size(); i++) {
                                String[] data = (String[]) vt.elementAt(i);
                                for (int e = 0; e < 6; e++) {
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
                        ResultSet r = st.executeQuery("SELECT * FROM DATASET_COLUMNS WHERE DATASET_ID = '" + datasetID + "'ORDER BY EXPERIMENT_ID asc");

                        Vector<String> vt = new Vector<String>();
                        while (r.next()) {
                                vt.add(r.getString("NAME"));
                        }
                        r.close();
                        st.close();
                        return vt;

                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }

        public void get_samplenames(int datasetID, SimpleLCMSDataset dataset) {
                Statement st = null;
                try {
                        Connection conn = this.connect();

                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT * FROM DATASET_COLUMNS WHERE DATASET_ID = '" + datasetID + "' ");

                        while (r.next()) {
                                try {
                                        dataset.addColumnName(r.getString("NAME"));
                                } catch (Exception exception) {
                                }
                        }
                        r.close();
                        st.close();


                } catch (Exception e) {
                        e.printStackTrace();
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
        private synchronized Hashtable<Integer, String> getExperimentsID(Dataset dataset, Connection conn) {
                Statement st = null;
                try {
                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT * FROM DATASET_COLUMNS WHERE DATASET_ID = '" + dataset.getID() + "'ORDER BY EXPERIMENT_ID asc");

                        Hashtable<Integer, String> vt = new Hashtable<Integer, String>();
                        while (r.next()) {
                                try {
                                        vt.put(r.getInt("COLUMN_ID"), r.getString("NAME"));

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

        public float getProgress() {
                return (float) completedRows / totalRows;
        }

        public synchronized void getLCMSRows(SimpleLCMSDataset dataset) {
                this.totalRows = dataset.getNumberRowsdb();

                Statement st = null;

                try {

                        Connection conn = this.connect();

                        Hashtable<Integer, String> experimentIDs = this.getExperimentsID(dataset, conn);

                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT * FROM MOL_LCMS WHERE EPID = '" + dataset.getID() + "'");

                        while (r.next()) {
                                SimplePeakListRowLCMS peakListRow = new SimplePeakListRowLCMS();
                                peakListRow.setMZ(r.getFloat("AVERAGE_MZ"));
                                peakListRow.setRT(r.getFloat("AVERAGE_RT"));
                                peakListRow.setNumFound(r.getInt("N_FOUND"));
                                peakListRow.setLipidClass(String.valueOf(r.getInt("LIPID_CLASS")));
                                peakListRow.setAllNames(r.getString("ALL_NAMES"));
                                peakListRow.setStandard(r.getInt("STD"));
                                peakListRow.setName(r.getString("LIPID_NAME"));
                                peakListRow.setFAComposition(r.getString("FA_COMPOSITION"));
                                peakListRow.setVTTID(r.getString("VTTID"));
                                peakListRow.setAllVTTD(r.getString("VTTALLIDS"));
                                peakListRow.setIdentificationType(r.getString("IDENTIFICATION_TYPE"));
                                peakListRow.setPubChemID(r.getString("PUBCHEM_ID"));
                                this.setPeaks(experimentIDs, peakListRow, r.getInt("ID"), conn);
                                dataset.addRow(peakListRow);
                                completedRows++;
                        }

                        r.close();
                        st.close();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public synchronized void setPeaks(int rowID, PeakListRow row, Hashtable<Integer, String> experimentIDs, Connection conn) {
                Statement st = null;
                try {
                        st = conn.createStatement();

                        ResultSet r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE MOL_LCMS_ID = '" + rowID + "' ORDER BY ID asc");

                        if (r.next()) {
                                try {
                                        //System.out.println(experimentIDs.get(r.getInt("DATASET_CID")));
                                        row.setPeak(experimentIDs.get(r.getInt("DATASET_CID")), r.getDouble("CONCENTRATION"));
                                } catch (Exception ee) {
                                        ee.printStackTrace();
                                }
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
                                        if (experimentIDs.containsKey(new Integer(r.getInt("DATASET_CID")))) {
                                                // System.out.println(new Double(r.getFloat("CONCENTRATION")));
                                                peakListRow.setPeak(experimentIDs.get(new Integer(r.getInt("DATASET_CID"))), new Double(r.getFloat("CONCENTRATION")));
                                        }
                                } catch (Exception ee) {
                                }
                        }
                        r.close();
                        st.close();


                } catch (Exception e) {
                }

        }

        public static synchronized int getStudyID(String StudyName, Connection conn) {
                Statement st = null;
                try {

                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT * FROM QBIXSTUDIES WHERE NAMES = '" + StudyName + "'");
                        int ID = 0;
                        if (r.next()) {
                                try {
                                        ID = r.getInt(1);
                                } catch (Exception ee) {
                                }
                        }

                        r.close();
                        st.close();

                        return ID;
                } catch (Exception e) {
                        return 0;
                }

        }

        public static synchronized String[] getStudies() {
                Statement st = null;
                try {
                        OracleDataSource oracleDataSource;
                        String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@sboracle1.ad.vtt.fi:1521:BfxDB";
                        String ORACLE_QUERY_USER = "sandra";
                        String ORACLE_QUERY_PASSWORD = "sandra";
                        oracleDataSource = new OracleDataSource();
                        oracleDataSource.setURL(ORACLE_DATABASE_URL);
                        oracleDataSource.setUser(ORACLE_QUERY_USER);
                        oracleDataSource.setPassword(ORACLE_QUERY_PASSWORD);
                        Connection conn = oracleDataSource.getConnection();


                        st = conn.createStatement();
                        ResultSet r = st.executeQuery("SELECT * FROM QBIXSTUDIES ORDER BY ID asc");
                        Vector<String> studies = new Vector<String>();
                        while (r.next()) {
                                try {
                                        studies.addElement(r.getString(2));
                                } catch (Exception ee) {
                                }
                        }

                        r.close();
                        st.close();
                        String[] studiesString = {""};
                        if (!studies.isEmpty()) {
                                studiesString = studies.toArray(new String[0]);
                        }

                        return studiesString;
                } catch (Exception e) {
                        return null;
                }

        }
}




