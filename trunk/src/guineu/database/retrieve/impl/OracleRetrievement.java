/*
 * Copyright 2007-2011 VTT Biotechnology
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
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.database.retrieve.*;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.main.GuineuCore;
import guineu.modules.database.openQualityControlFileDB.SampleInfo;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.swing.JOptionPane;
import oracle.jdbc.pool.OracleDataSource;

/**
 * @author scsandra
 *
 */
public class OracleRetrievement implements DataBase {

    private int totalRows;
    private int completedRows;
    private Connection conn;
    private static int passes = 0;
    private String password = "sandra";

    public OracleRetrievement() {
        conn = this.connect();
    }

    public synchronized Connection connect() {
        try {
            OracleDataSource oracleDataSource;
            String ORACLE_DATABASE_URL = "jdbc:oracle:thin:@sboracle1.ad.vtt.fi:1521:BfxDB";
            String ORACLE_QUERY_USER = "sandra";
            String ORACLE_QUERY_PASSWORD = password;
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
            conn = this.connect();
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
            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM DATASET ORDER BY DATASETID asc");

            Vector<String[]> vt = new Vector<String[]>();
            while (r.next()) {
                String[] data = new String[7];
                data[0] = r.getString("DATASETID");
                data[1] = r.getString("EXCEL_NAME");
                data[2] = r.getString("D_TYPE");
                data[3] = r.getString("AUTHOR");
                data[4] = r.getString("D_DATE");
                data[5] = r.getString("NUMBER_ROWS");
                data[6] = r.getString("STUDY");
                data[6] = getStudyName(data[6], conn);
                vt.add(data);
            }
            String[][] datafinal = new String[vt.size()][7];
            for (int i = 0; i < vt.size(); i++) {
                String[] data = (String[]) vt.elementAt(i);
                for (int e = 0; e < 7; e++) {
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

    public String getStudyName(String ID, Connection conn) {
        Statement st = null;
        try {

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM QBIXSTUDIES WHERE ID = '" + ID + "'");
            String name = " ";
            if (r.next()) {
                try {
                    name = r.getString("NAMES");
                } catch (Exception ee) {
                }
            }

            r.close();
            st.close();

            return name;
        } catch (Exception e) {
            return " ";
        }

    }

    public Vector<String> getSampleNames(int datasetID) {
        Statement st = null;
        try {
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
                    if (dataset.getAllColumnNames().contains(r.getString("NAME"))) {
                        vt.put(r.getInt("COLUMN_ID"), r.getString("NAME"));
                    }

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

    private synchronized void getParameters(Dataset dataset) {

        Statement st = null;
        for (String columnName : dataset.getAllColumnNames()) {
            String[] tempStr = columnName.split("_");
            String barcode = null;
            try {
                barcode = (tempStr[0] + "_" + tempStr[1]).toUpperCase();
            } catch (Exception e) {
            }
            if (barcode != null) {
                try {
                    st = conn.createStatement();
                    ResultSet r = st.executeQuery("SELECT * FROM SAMPLE WHERE UPPER(BARCODE) LIKE '%" + barcode + "%'");
                    if (r.next()) {
                        dataset.addParameterValue(columnName, "Label", r.getString("LABEL"));
                        dataset.addParameterValue(columnName, "Type", r.getString("TYPE"));
                        dataset.addParameterValue(columnName, "Subtype", r.getString("SUBTYPE"));
                        dataset.addParameterValue(columnName, "Organism", r.getString("ORGANISM"));
                    }
                    r.close();
                    st = conn.createStatement();
                    r = st.executeQuery("SELECT * FROM SAMPLEPS WHERE UPPER(BARCODE) = '" + barcode + "'");
                    while (r.next()) {
                        dataset.addParameterValue(columnName, r.getString("FIELD"), r.getString("DATA"));
                    }
                    r.close();
                    st.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public synchronized String[] getParameters(String sampleName) {

        Statement st = null;

        String[] tempStr = sampleName.split("_");
        String barcode = null;
        try {
            barcode = (tempStr[0] + "_" + tempStr[1]).toUpperCase();
        } catch (Exception e) {
        }
        if (barcode != null) {
            try {
                st = conn.createStatement();
                ResultSet r = st.executeQuery("SELECT * FROM SAMPLE WHERE UPPER(BARCODE) LIKE '%" + barcode + "%'");
                String[] parameters = new String[4];
                if (r.next()) {
                    parameters[0] = r.getString("LABEL");
                    parameters[1] = r.getString("TYPE");
                    parameters[2] = r.getString("SUBTYPE");
                    parameters[3] = r.getString("ORGANISM");
                }

                r.close();
                st.close();
                return parameters;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;

    }

    public synchronized void getLCMSRows(SimpleLCMSDataset dataset) {
        this.totalRows = dataset.getNumberRowsdb();

        Statement st = null;

        try {

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
                this.setPeaks(experimentIDs, peakListRow, r.getInt("ID"), conn, dataset.getType());
                dataset.addRow(peakListRow);
                completedRows++;
            }

            r.close();
            st.close();
            this.getParameters(dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized void setPeaks(Hashtable<Integer, String> experimentIDs, PeakListRow peakListRow, int molID, Connection conn, DatasetType type) {
        Statement st = null;
        try {

            st = conn.createStatement();
            ResultSet r = null;
            if (type == DatasetType.LCMS) {
                r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE MOL_LCMS_ID = '" + molID + "' ORDER BY ID asc");
            } else if (type == DatasetType.GCGCTOF) {
                r = st.executeQuery("SELECT * FROM MEASUREMENT WHERE MOL_GCGCTOF_ID = '" + molID + "' ORDER BY ID asc");
            }
            while (r.next()) {
                try {
                    if (experimentIDs.containsKey(new Integer(r.getInt("DATASET_CID")))) {
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

    public synchronized List<String[]> getStudiesInfo() {
        Statement st = null;
        try {
            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM QBIXSTUDIES ORDER BY ID asc");
            Hashtable<String, String[]> studies = new Hashtable<String, String[]>();

            String[] studiesNames = getStudies();

            while (r.next()) {
                try {
                    String[] studyInfo = new String[2];
                    studyInfo[0] = r.getString("NAMES");
                    studyInfo[1] = r.getString("PROJECT");
                    studies.put(studyInfo[0], studyInfo);
                } catch (Exception ee) {
                }
            }

            List<String[]> studiesInfo = new ArrayList<String[]>();
            for (String names : studiesNames) {
                studiesInfo.add(studies.get(names));
            }

            r.close();
            st.close();

            return studiesInfo;
        } catch (Exception e) {
            return null;
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
            ArrayList<String> studies = new ArrayList<String>();
            while (r.next()) {
                try {
                    studies.add(r.getString(2));
                } catch (Exception ee) {
                }
            }

            r.close();
            st.close();

            try {
                OracleRetrievement.<String>sortArrayList(studies, true);
            } catch (Throwable t) {
            }
            String[] studiesString = {""};
            if (!studies.isEmpty()) {
                studiesString = studies.toArray(new String[0]);
            }

            return studiesString;
        } catch (Exception e) {
            return null;
        }

    }

    static <T extends Comparable<T>> void sortArrayList(ArrayList<T> arg, boolean order) throws Exception {

        int turn = 0;

        while (!OracleRetrievement.<T>isSorted(arg)) {

            for (int i = 0; i < arg.size() - 1; i++) {

                T temp = (order)
                        ? (arg.get(i).compareTo(arg.get(i + 1)) < arg.get(i + 1).compareTo(arg.get(i))
                        ? (arg.get(i)) : (arg.get(i + 1))) : ((arg.get(i).compareTo(arg.get(i + 1)) < arg.get(i + 1).compareTo(arg.get(i)))
                        ? (arg.get(i)) : (arg.get(i + 1))),
                        temp2 = (order)
                        ? (arg.get(i).compareTo(arg.get(i + 1)) > arg.get(i + 1).compareTo(arg.get(i))
                        ? (arg.get(i)) : (arg.get(i + 1))) : ((arg.get(i).compareTo(arg.get(i + 1)) < arg.get(i + 1).compareTo(arg.get(i)))
                        ? (arg.get(i)) : (arg.get(i + 1)));

                arg.set(i, temp);

                arg.set(i + 1, temp2);

                //Thread.sleep(250); // for debug purposes

            }

            //   System.out.println(arg);

            turn++;

        }

        passes = turn;

    }

    private static <T extends Comparable<T>> boolean isSorted(ArrayList<T> arg) {

        int count = 0;

        for (int i = 0; i < arg.size() - 1; i++) {
            count = (arg.get(i).compareTo(arg.get(i + 1)) <= arg.get(i + 1).compareTo(arg.get(i))) ? ++count : count;
        }
        return count == (arg.size() - 1);

    }

    public synchronized void getGCGCRows(SimpleGCGCDataset dataset) {
        this.totalRows = dataset.getNumberRowsdb();

        Statement st = null;

        try {

            Hashtable<Integer, String> experimentIDs = this.getExperimentsID(dataset, conn);

            st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM MOL_GCGCTOF WHERE EPID = '" + dataset.getID() + "'");

            while (r.next()) {
                SimplePeakListRowGCGC peakListRow = new SimplePeakListRowGCGC();
                peakListRow.setRT1(r.getFloat("RT1"));
                peakListRow.setRT2(r.getFloat("RT2"));
                peakListRow.setRTI(r.getFloat("RTI"));
                peakListRow.setNumFound(r.getDouble("N_FOUND"));
                peakListRow.setMaxSimilarity(r.getDouble("MAX_SIMILARITY"));
                peakListRow.setMeanSimilarity(r.getFloat("MEAN_SIMILARITY"));
                peakListRow.setSimilaritySTDDev(r.getFloat("SIMILARITY_STD_DEV"));
                String name = r.getString("METABOLITE_NAME");
                try {
                    name = name.replaceAll("ç", "'");
                } catch (Exception exception) {
                }
                peakListRow.setName(name);
                peakListRow.setPubChemID(r.getString("PUBCHEM_ID"));
                name = r.getString("METABOLITE_ALLNAMES");
                try {
                    name = name.replaceAll("ç", "'");
                } catch (Exception exception) {
                }
                peakListRow.setAllNames(name);
                peakListRow.setMass(r.getFloat("MASS"));
                peakListRow.setDifference(r.getFloat("DIFFERENCE"));
                peakListRow.setSpectrumString(r.getString("SPECTRUM"));
                peakListRow.setCAS(r.getString("CAS"));
                peakListRow.setMolClass(r.getString("CLASS"));
                this.setPeaks(experimentIDs, peakListRow, r.getInt("ID"), conn, dataset.getType());
                dataset.addRow(peakListRow);
                completedRows++;
            }

            r.close();
            st.close();
            this.getParameters(dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDataset(String datasetName, String password) {
        if (password.equals(this.password)) {
            Statement st = null;
            try {
                st = conn.createStatement();
                st.executeQuery("DELETE FROM DATASET WHERE EXCEL_NAME = '" + datasetName + "' ");
                st.close();
            } catch (Exception e) {
            }
        } else {
            JOptionPane.showMessageDialog(GuineuCore.getDesktop().getMainFrame(), "Password not valid",
                    "Password not valid", JOptionPane.DEFAULT_OPTION);
        }
    }

    public List<SampleInfo> getQualityControlRows(int QC_ID) {
        try {
            Statement st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM QCSAMPLE WHERE DATASETID = '" + QC_ID + "' ORDER BY DATAID asc");
            ArrayList<SampleInfo> rows = new ArrayList<SampleInfo>();
            while (r.next()) {
                try {
                    SampleInfo info = new SampleInfo();
                    String name = r.getString("SAMPLE_NAME");
                    String date = r.getString("DATESET");
                    info.setSampleName(name, date);
                    String[] lyso = new String[6];
                    lyso[0] = r.getString("SAMPLE_NAME");
                    lyso[1] = String.valueOf(r.getFloat("LYSOPC_RT"));
                    lyso[2] = String.valueOf(r.getFloat("LYSOPC_HEIGHT"));
                    lyso[3] = String.valueOf(r.getFloat("LYSOPC_HEIGHT_RATIO"));
                    lyso[4] = String.valueOf(r.getFloat("LYSOPC_HEIGHT_AREA"));
                    lyso[5] = r.getString("TIMESET");
                    info.setLysoPC(lyso);

                    String[] pc = new String[6];
                    pc[0] = r.getString("SAMPLE_NAME");
                    pc[1] = String.valueOf(r.getFloat("PC_RT"));
                    pc[2] = String.valueOf(r.getFloat("PC_HEIGHT"));
                    pc[3] = String.valueOf(r.getFloat("PC_HEIGHT_RATIO"));
                    pc[4] = String.valueOf(r.getFloat("PC_HEIGHT_AREA"));
                    pc[5] = r.getString("TIMESET");
                    info.setPC(pc);

                    String[] tg = new String[6];
                    tg[0] = r.getString("SAMPLE_NAME");
                    tg[1] = String.valueOf(r.getFloat("TG_RT"));
                    tg[2] = String.valueOf(r.getFloat("TG_HEIGHT"));
                    tg[3] = String.valueOf(r.getFloat("TG_HEIGHT_RATIO"));
                    tg[4] = String.valueOf(r.getFloat("TG_HEIGHT_AREA"));
                    tg[5] = r.getString("TIMESET");
                    info.setTG(tg);

                    lyso = new String[6];
                    lyso[0] = r.getString("SAMPLE_NAME");
                    lyso[1] = String.valueOf(r.getFloat("S_LYSOPC_RT"));
                    lyso[2] = String.valueOf(r.getFloat("S_LYSOPC_HEIGHT"));
                    lyso[3] = "0";
                    lyso[4] = "0";
                    lyso[5] = r.getString("TIMESET");
                    info.setLPC(lyso);

                    pc = new String[6];
                    pc[0] = r.getString("SAMPLE_NAME");
                    pc[1] = String.valueOf(r.getFloat("S_PC_RT"));
                    pc[2] = String.valueOf(r.getFloat("S_PC_HEIGHT"));
                    pc[3] = "0";
                    pc[4] = "0";
                    pc[5] = r.getString("TIMESET");
                    info.setPCD(pc);

                    tg = new String[6];
                    tg[0] = r.getString("SAMPLE_NAME");
                    tg[1] = String.valueOf(r.getFloat("S_TG_RT"));
                    tg[2] = String.valueOf(r.getFloat("S_TG_HEIGHT"));
                    tg[3] = "0";
                    tg[4] = "0";
                    tg[5] = r.getString("TIMESET");
                    info.setTGC(tg);

                    rows.add(info);
                } catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
            r.close();
            return rows;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public ArrayList<String[]> getQualityControlDatasets() {
        try {
            Statement st = conn.createStatement();
            ResultSet r = st.executeQuery("SELECT * FROM QUALITYC ORDER BY QC_ID asc");
            ArrayList<String[]> datasets = new ArrayList<String[]>();
            while (r.next()) {
                try {
                    String[] dataset = new String[7];
                    dataset[0] = r.getString("SAMPLESET");
                    dataset[1] = r.getString("DATESET");
                    dataset[2] = r.getString("ION_MODE");
                    dataset[3] = r.getString("INJECTION_VOLUME");
                    dataset[4] = r.getString("SAMPLE_TYPE");
                    dataset[5] = r.getString("COMMENTS");
                    dataset[6] = String.valueOf(r.getInt("QC_ID"));
                    datasets.add(dataset);
                } catch (Exception ee) {
                }
            }
            r.close();
            return datasets;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void deleteQualityControlDataset(int QC_ID) {
        try {
            Statement st = conn.createStatement();
            ResultSet r = st.executeQuery("DELETE FROM QUALITYC WHERE QC_ID = '" + QC_ID + "'");
            r.close();
            st.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}




