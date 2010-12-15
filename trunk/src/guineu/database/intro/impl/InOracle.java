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
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.SimpleParameterSet;
import guineu.data.impl.datasets.SimpleQualityControlDataset;
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

    private WriteDataBase writer;
    private String task;

    /**
     * Returns a connection with the database
     *
     * @return Database connection
     */
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
        if (writer != null) {
            return (float) writer.getProgress();
        } else {
            return 0.0f;
        }
    }

    public void lcms(Connection conn, SimpleLCMSDataset dataset, String type, String author, String DatasetName, String parameters, String study) throws IOException {
        writer = new WriteDataBase();
        String excel_name = DatasetName;
        if (excel_name == null) {
            excel_name = "unknown";
        }

        //Intro table DATASET
        task = "Writing data set information into the database";
        int excel_id = writer.tableDATASET(conn, excel_name, type, author, parameters, study, dataset.getInfo(), dataset.getNumberRows());

        task = "Writing experiment information into the database";
        if (excel_id != -1) {
            writer.tableEXPERIMENT(conn, dataset, excel_id);
            task = "Writing compound information into the database";
            //Intro table MOL_LCMMS
            int[] mol_ID = writer.tableMOL_LCMS(conn, dataset, excel_id);
            task = "Writing measurements into the database";
            //Intro table MEASUREMENT
            writer.tableMEASUREMENT(conn, dataset, mol_ID, excel_id);
        }
    }

    public void gcgctof(Connection conn, SimpleGCGCDataset dataset, String type, String author, String DatasetName, String study) throws IOException {
        try {
            writer = new WriteDataBase();
            Statement st = null;

            //Intro table DATASET
            task = "Writing data set information into the database";
            String excel_name = DatasetName;
            int exp_id = writer.tableDATASET(conn, excel_name, type, author, null, study, dataset.getInfo(), dataset.getNumberRows());

            //Intro table DATASET_EXPERIMENTS
            task = "Writing experiment information into the database";
            writer.tableEXPERIMENT(conn, dataset, exp_id);

            //Intro table GCGCTof
            task = "Writing compound information into the database";
            int[] mol_ID = writer.tableMOL_GCGCTOF(conn, dataset, exp_id);

            //Intro table MEASUREMENT
            task = "Writing measurements into the database";
            writer.tableMEASUREMENT(conn, dataset, mol_ID, exp_id);

            //Intro table SPECTRUM
            task = "Writing spectra information into the database";
            writer.tableSPECTRUM(conn, dataset, st, mol_ID);

        } catch (Exception exception) {
            System.out.println("Inoracle.java ---> gcgctof() " + exception);
        }
    }

    public void qualityControlFiles(Connection conn, SimpleBasicDataset QCDataset) throws IOException {
        writer = new WriteDataBase();
        int QC_ID = writer.TableQUALITYC(conn, (SimpleQualityControlDataset) QCDataset);
        writer.TableQCSample(conn, (SimpleQualityControlDataset) QCDataset, QC_ID);
    }

    public void WriteExcelFile(Dataset dataset, String path, SimpleParameterSet parameters) {
        WriteFile fileWriter = new WriteFile();
        if (dataset.getType() == DatasetType.LCMS) {
            fileWriter.WriteExcelFileLCMS(dataset, path, parameters);
        } else if (dataset.getType() == DatasetType.GCGCTOF) {
            fileWriter.WriteExcelFileGCGC(dataset, path, parameters);
        } else {
            fileWriter.WriteXLSFileBasicDataset(dataset, path);
        }
    }

    public void WriteCommaSeparatedFile(Dataset dataset, String path, SimpleParameterSet parameters) {
        WriteFile fileWriter = new WriteFile();
        if (dataset.getType() == DatasetType.LCMS) {
            fileWriter.WriteCommaSeparatedFileLCMS(dataset, path, parameters);
        } else if (dataset.getType() == DatasetType.GCGCTOF) {
            fileWriter.WriteCommaSeparatedFileGCGC(dataset, path, parameters);
        } else {
            fileWriter.WriteCommaSeparatedBasicDataset(dataset, path);
        }
    }

    public void deleteDataset(Connection conn, int datasetID) {
        try {
            Statement statement = conn.createStatement();

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

        } catch (Exception exception) {
        }
    }

    public String getTaskDescription() {
        return task;
    }

    public void WriteExpressionData(Dataset dataset, String path, SimpleParameterSet parameters) {
        WriteFile fileWriter = new WriteFile();
        fileWriter.writeExpressionData(dataset, path, parameters);
    }
}
