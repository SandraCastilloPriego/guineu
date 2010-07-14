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
package guineu.database.intro.impl;

import guineu.database.intro.*;
import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimpleBasicDataset;
import guineu.data.impl.SimpleParameterSet;
import guineu.modules.filter.report.qualityReport.SimpleQualityControlDataset;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import oracle.jdbc.pool.OracleDataSource;

/**
 * Oracle connection
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

        public void lcms(Connection conn, SimpleLCMSDataset LipidMol, String type, String author, String DatasetName, String parameters, String study) throws IOException {
                WriteDataBase writer = new WriteDataBase();
                String excel_name = DatasetName;
                if (excel_name == null) {
                        excel_name = "unknown";
                }

                //Intro table DATASET
                int excel_id = writer.tableDATASET(conn, excel_name, type, author, parameters, study, LipidMol.getInfo());
                progress = 0.25f;

                if (excel_id != -1) {
                        writer.tableEXPERIMENT(conn, LipidMol, excel_id);
                        progress = 0.50f;

                        //Intro table MOL_LCMMS
                        int[] mol_ID = writer.tableMOL_LCMS(conn, LipidMol, excel_id);
                        progress = 0.75f;

                        //Intro table MEASUREMENT
                        writer.tableMEASUREMENT(conn, LipidMol, mol_ID, excel_id);
                }
                progress = 1f;
        }

        public void gcgctof(Connection conn, SimpleGCGCDataset mol, String type, String author, String DatasetName, String study) throws IOException {
                try {
                        WriteDataBase writer = new WriteDataBase();
                        Statement st = null;

                        //Intro table DATASET
                        String excel_name = DatasetName;
                        progress = 0.15f;
                        int exp_id = writer.tableDATASET(conn, excel_name, type, author, null, study, mol.getInfo());
                        progress = 0.25f;

                        //Intro table DATASET_EXPERIMENTS
                        writer.tableEXPERIMENT(conn, mol, exp_id);

                        //Intro table GCGCTof
                        int[] mol_ID = writer.tableMOL_GCGCTOF(conn, mol, exp_id);
                        progress = 0.50f;

                        //Intro table MEASUREMENT
                        writer.tableMEASUREMENT(conn, mol, mol_ID, exp_id);
                        progress = 0.75f;

                        //Intro table SPECTRUM
                        writer.tableSPECTRUM(conn, mol, st, mol_ID);

                        progress = 1f;
                } catch (Exception exception) {
                        System.out.println("Inoracle.java ---> gcgctof() " + exception);
                }
        }

        public void qualityControlFiles(Connection conn, SimpleBasicDataset QCDataset) throws IOException {
                WriteDataBase writer = new WriteDataBase();
                progress = 0.25f;
                int QC_ID = writer.TableQUALITYC(conn, (SimpleQualityControlDataset) QCDataset);

                progress = 1f;
        }

        public void WriteExcelFile(Dataset dataset, String path, SimpleParameterSet parameters) {
                WriteFile writer = new WriteFile();
                if (dataset.getType() == DatasetType.LCMS) {
                        writer.WriteExcelFileLCMS(dataset, path, parameters);
                } else if (dataset.getType() == DatasetType.GCGCTOF) {
                        writer.WriteExcelFileGCGC(dataset, path, parameters);
                } else {
                        writer.WriteXLSFileBasicDataset(dataset, path);
                }
        }

        public void WriteCommaSeparatedFile(Dataset dataset, String path, SimpleParameterSet parameters) {
                WriteFile writer = new WriteFile();
                if (dataset.getType() == DatasetType.LCMS) {
                        writer.WriteCommaSeparatedFileLCMS(dataset, path, parameters);
                } else if (dataset.getType() == DatasetType.GCGCTOF) {
                        writer.WriteCommaSeparatedFileGCGC(dataset, path, parameters);
                } else {
                        writer.WriteCommaSeparatedBasicDataset(dataset, path);
                }
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
}
