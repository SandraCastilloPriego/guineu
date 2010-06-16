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
package guineu.database.intro;

import guineu.data.impl.Bexperiments;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Vector;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 *
 * @author scsandra
 */
public class ExperimentTable {

    public ExperimentTable() {

    }

    /**
     * Intro table EXPERIMENTS from a file 
     * @param conn Connection
     * @param xlsfile
     * @param logidir
     * @param sysdir
     */
    public void IntroExperimentTable(Connection conn, Vector<Bexperiments> experiment, String logidir, String sysdir, float progress) {
        try {
            Vector<Bexperiments> Data = experiment;
            Statement statement = conn.createStatement();
            //I don't have enougth privileges
            /*try {
            System.out.println(logidir + " - "+ sysdir);
            PreparedStatement pstmt = conn.prepareStatement("create or replace directory "+logidir+" as '"+sysdir+"'");
            pstmt.execute(); // Execute the prepared SQL Statement     
            }catch(Exception ee){
            ee.printStackTrace();           
            }*/
            float unit = 100 / Data.size();
            for (Bexperiments exp : Data) {
                progress += unit / 100;
                try {
                    statement.executeUpdate("UPDATE EXPERIMENT SET TYPE = '" + exp.TYPE +
                            "',PROJECT = '" + exp.PROJECT +
                            "',REPLICATE = '" + exp.REPLICATE +
                            "',PERSON = '" + exp.PERSON +
                            "',Amount = '" + exp.Amount +
                            "',Unit = '" + exp.Unit +
                            "',Method = '" + exp.Method +
                            "',Sample = '" + exp.Sample +
                            "',EDATE = '" + exp.EDATE +
                            "' where NAME = '" + exp.Name + "'");
                } catch (SQLException e) {
                }
                try {
                    ResultSet r = statement.executeQuery("SELECT * FROM EXPERIMENT WHERE NAME = '" + exp.Name + "'");
                    if (!r.next()) {
                        statement.executeUpdate("INSERT INTO EXPERIMENT (NAME," +
                                "TYPE,PROJECT,REPLICATE,PERSON,Amount,Unit,Method," +
                                "Sample,EDATE) VALUES ('" + exp.Name +
                                "', '" + exp.TYPE +
                                "', '" + exp.PROJECT +
                                "', '" + exp.REPLICATE +
                                "', '" + exp.PERSON +
                                "', '" + exp.Amount +
                                "', '" + exp.Unit +
                                "', '" + exp.Method +
                                "', '" + exp.Sample +
                                "', '" + exp.EDATE + "')");
                    }
                } catch (SQLException e) {

                }
                //Load CDF Directory
                File pfile = new File(sysdir);
                // List the files under directory
                String[] flist = pfile.list();
                if (flist != null) {
                    for (String slist : flist) {
                        if (slist.matches(exp.Name + ".*")) {
                            //update the CDF data with the matched file name
                            try {
                                statement.executeUpdate("update EXPERIMENT set CDF = bfilename('" + sysdir + "', '" + slist + "') where name = '" + exp.Name + "'");
                            } catch (Exception ee) {
                                ee.printStackTrace();
                            }
                        }
                    }
                }
            }
            statement.close();
        } catch (Exception exception) {

        }

    }

    /**
     * Reads the excel file where all the information is.
     * @param xlsfile
     * @return Vector with objects "Experiments" with all parsed information
     */
    public Vector<Bexperiments> readExcelTableExperiment(String xlsfile) {
        try {
            Vector<Bexperiments> Data = new Vector<Bexperiments>();

            HSSFWorkbook book = openExcel(xlsfile);
            HSSFSheet sheet = book.getSheetAt(0);
            int start = this.getNstart(sheet);

            for (int i = start + 1; i < sheet.getLastRowNum() + 1; i++) {
                HSSFRow row = sheet.getRow(i);
                String[] sData = new String[7];
                for (short e = 0; e < 7; e++) {
                    HSSFCell cell = row.getCell(e);
                    if (cell != null) {
                        sData[e] = cell.toString();
                    } else {
                        sData[e] = " ";
                    }
                }
                Bexperiments exp = new Bexperiments(sData);
                if (exp.state) {
                    Data.addElement(exp);
                }
            }

            return Data;

        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

    }

    /**
     * Open a Excel file
     * @param file_name
     * @return 
     * @throws java.io.IOException
     */
    public HSSFWorkbook openExcel(String file_name)
            throws IOException {
        FileInputStream fileIn = null;
        try {
            HSSFWorkbook wb;
            POIFSFileSystem fs;
            fileIn = new FileInputStream(file_name);
            fs = new POIFSFileSystem(fileIn);
            wb = new HSSFWorkbook(fs);
            return wb;
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }
    }

    /**
     * Reads each row until it find the word "Name" in the firts cell
     * @param sheet
     * @return the number of rows until the data starts.
     */
    public int getNstart(HSSFSheet sheet) {
        Iterator rowIt = sheet.rowIterator();
        int num = -1;

        String Start = "Name";

        while (rowIt.hasNext()) {
            HSSFRow row = (HSSFRow) rowIt.next();
            HSSFCell cell = row.getCell((short) 0);

            if (cell != null) {
                if (Start.matches(cell.toString())) {
                    num = row.getRowNum();
                }
            }
        }
        return num;
    }
}
