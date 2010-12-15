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

import com.csvreader.CsvWriter;
import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.GCGCColumnName;
import guineu.data.LCMSColumnName;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleParameterSet;
import guineu.data.impl.datasets.SimpleExpressionDataset;
import guineu.data.impl.peaklists.SimplePeakListRowExpression;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.modules.file.saveGCGCFile.SaveGCGCParameters;
import guineu.modules.file.saveLCMSFile.SaveLCMSParameters;
import guineu.util.CollectionUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Writes the data sets into a file.
 *
 * @author SCSANDRA
 */
public class WriteFile {

    /**
     * Writes Comma Separated file for LC-MS data set.
     *
     * @param dataset LC-MS data set
     * @param path Path where the new file will be created
     * @param parameters Parameters for saving the file (columns saved in the new file)
     */
    public void WriteCommaSeparatedFileLCMS(Dataset dataset, String path, SimpleParameterSet parameters) {
        try {
            Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveLCMSParameters.exportLCMS);
            parameters.setParameterValue(SaveLCMSParameters.exportLCMS, elementsObjects);

            LCMSColumnName[] elements = CollectionUtils.changeArrayType(elementsObjects,
                    LCMSColumnName.class);

            CsvWriter w = new CsvWriter(path);

            //write head
            int fieldsNumber = this.getNumFields(elements);
            String[] data = new String[dataset.getNumberCols() + fieldsNumber];
            int cont = 0;
            for (LCMSColumnName p : elements) {
                if (p.isColumnShown()) {
                    data[cont++] = p.getColumnName();
                }
            }
            int c = fieldsNumber;
            for (String experimentName : dataset.getAllColumnNames()) {
                data[c++] = experimentName;
            }
            w.writeRecord(data);

            //write content
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowLCMS lipid = (SimplePeakListRowLCMS) dataset.getRow(i);

                cont = 0;
                for (LCMSColumnName p : elements) {
                    if (p.isColumnShown()) {
                        try {
                            data[cont++] = String.valueOf(lipid.getVar(p.getGetFunctionName()));
                        } catch (Exception ee) {
                        }
                    }
                }
                c = fieldsNumber;
                for (String experimentName : dataset.getAllColumnNames()) {
                    data[c++] = String.valueOf(lipid.getPeak(experimentName));
                }
                w.writeRecord(data);
            }
            w.endRecord();
            w.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Writes the LC-MS data set into an excel file.
     *
     * @param dataset LC-MS data set
     * @param path Path where the new file will be created
     * @param parameters Parameters for saving the file (columns saved in the new file)
     */
    public void WriteExcelFileLCMS(Dataset dataset, String path, SimpleParameterSet parameters) {
        FileOutputStream fileOut = null;
        try {
            // Prepares sheet
            HSSFWorkbook wb;
            HSSFSheet sheet;
            try {
                FileInputStream fileIn = new FileInputStream(path);
                POIFSFileSystem fs = new POIFSFileSystem(fileIn);
                wb = new HSSFWorkbook(fs);
                int NumberOfSheets = wb.getNumberOfSheets();
                sheet = wb.createSheet(String.valueOf(NumberOfSheets));
            } catch (Exception exception) {
                wb = new HSSFWorkbook();
                sheet = wb.createSheet("Normalized");
            }
            HSSFRow row = sheet.getRow(0);
            if (row == null) {
                row = sheet.createRow(0);
            }

            Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveLCMSParameters.exportLCMS);
            parameters.setParameterValue(SaveLCMSParameters.exportLCMS, elementsObjects);

            LCMSColumnName[] elements = CollectionUtils.changeArrayType(elementsObjects,
                    LCMSColumnName.class);


            // Writes head
            int fieldsNumber = this.getNumFields(elements);
            int cont = 0;
            for (LCMSColumnName p : elements) {
                if (p.isColumnShown()) {
                    this.setCell(row, cont++, p.getColumnName());
                }
            }
            int c = fieldsNumber;
            for (String experimentName : dataset.getAllColumnNames()) {
                this.setCell(row, c++, experimentName);
            }

            // Writes content
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowLCMS lipid = (SimplePeakListRowLCMS) dataset.getRow(i);
                row = sheet.getRow(i + 1);
                if (row == null) {
                    row = sheet.createRow(i + 1);
                }

                cont = 0;
                for (LCMSColumnName p : elements) {
                    if (p.isColumnShown()) {
                        try {
                            this.setCell(row, cont++, lipid.getVar(p.getGetFunctionName()));
                        } catch (Exception ee) {
                        }
                    }
                }
                c = fieldsNumber;
                for (String experimentName : dataset.getAllColumnNames()) {
                    this.setCell(row, c++, lipid.getPeak(experimentName));
                }
            }
            //Writes the output to a file
            fileOut = new FileOutputStream(path);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Writes Comma Separated file for basic data set.
     *
     * @param dataset basic data set
     * @param path Path where the new file will be created
     */
    public void WriteCommaSeparatedBasicDataset(Dataset dataset, String path) {
        try {
            CsvWriter w = new CsvWriter(path);
            String[] data = new String[dataset.getNumberCols()];
            int c = 0;
            for (String experimentName : dataset.getAllColumnNames()) {
                data[c++] = experimentName;
            }
            w.writeRecord(data);
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowOther lipid = (SimplePeakListRowOther) dataset.getRow(i);
                c = 0;
                for (String experimentName : dataset.getAllColumnNames()) {
                    if (lipid.getPeak(experimentName) == null) {
                        data[c++] = "";
                    } else {
                        data[c++] = String.valueOf(lipid.getPeak(experimentName));
                    }
                }
                w.writeRecord(data);
            }
            w.endRecord();
            w.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Writes the basic data set into an excel file.
     *
     * @param dataset basic data set
     * @param path Path where the new file will be created
     */
    public void WriteXLSFileBasicDataset(Dataset dataset, String path) {
        FileOutputStream fileOut = null;
        try {
            HSSFWorkbook wb;
            HSSFSheet sheet;
            try {
                FileInputStream fileIn = new FileInputStream(path);
                POIFSFileSystem fs = new POIFSFileSystem(fileIn);
                wb = new HSSFWorkbook(fs);
                int NumberOfSheets = wb.getNumberOfSheets();
                sheet = wb.createSheet(String.valueOf(NumberOfSheets));
            } catch (Exception exception) {
                wb = new HSSFWorkbook();
                sheet = wb.createSheet("Mass Lynx");
            }
            HSSFRow row = sheet.getRow(0);
            if (row == null) {
                row = sheet.createRow(0);
            }
            int cont = 0;
            for (String experimentName : dataset.getAllColumnNames()) {
                this.setCell(row, cont++, experimentName);

            }
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowOther lipid = (SimplePeakListRowOther) dataset.getRow(i);

                row = sheet.getRow(i + 1);
                if (row == null) {
                    row = sheet.createRow(i + 1);
                }
                int c = 0;
                for (String experimentName : dataset.getAllColumnNames()) {
                    if (lipid.getPeak(experimentName) == null) {
                        this.setCell(row, c++, "");
                    } else {
                        this.setCell(row, c++, lipid.getPeak(experimentName));
                    }
                }
            }
            //Write the output to a file
            fileOut = new FileOutputStream(path);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Returns the number of columns of the new file.
     *
     * @param possibleColumns LC-MS possible columns
     * @return Number of columns of the new file
     */
    public int getNumFields(LCMSColumnName[] possibleColumns) {
        int cont = 0;
        for (LCMSColumnName p : possibleColumns) {
            if (p.isColumnShown()) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Returns the number of columns of the new file.
     *
     * @param possibleColumns GCxGC-MS possible columns
     * @return Number of columns of the new file
     */
    public int getNumFields(GCGCColumnName[] elements) {
        int cont = 0;
        for (GCGCColumnName p : elements) {
            if (p.isColumnShown()) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Write data in a cell of the excel file.
     *
     * @param row Cell row
     * @param Index Cell column
     * @param data data to be writen into the cell
     */
    private void setCell(HSSFRow row, int Index, Object data) {
        if (data.getClass().toString().contains("String")) {
            HSSFCell cell = row.getCell((short) Index);
            if (cell == null) {
                cell = row.createCell((short) Index);
            }
            cell.setCellType(HSSFCell.CELL_TYPE_STRING);
            cell.setCellValue((String) data);
        } else if (data.getClass().toString().contains("Double")) {
            HSSFCell cell = row.getCell((short) Index);
            if (cell == null) {
                cell = row.createCell((short) Index);
            }
            cell.setCellValue((Double) data);
        } else if (data.getClass().toString().contains("Integer")) {
            HSSFCell cell = row.getCell((short) Index);
            if (cell == null) {
                cell = row.createCell((short) Index);
            }
            cell.setCellValue((Integer) data);
        }
    }

    /**
     * Writes the GCxGC-MS data set into an excel file.
     *
     * @param dataset GCxGC-MS data set
     * @param path Path where the new file will be created
     * @param parameters Parameters for saving the file (columns saved in the new file)
     */
    public void WriteExcelFileGCGC(Dataset dataset, String path, SimpleParameterSet parameters) {
        FileOutputStream fileOut = null;
        try {
            HSSFWorkbook wb;
            HSSFSheet sheet;
            try {
                FileInputStream fileIn = new FileInputStream(path);
                POIFSFileSystem fs = new POIFSFileSystem(fileIn);
                wb = new HSSFWorkbook(fs);
                int NumberOfSheets = wb.getNumberOfSheets();
                sheet = wb.createSheet(String.valueOf(NumberOfSheets));
            } catch (Exception exception) {
                wb = new HSSFWorkbook();
                sheet = wb.createSheet("Normalized");
            }
            HSSFRow row = sheet.getRow(0);
            if (row == null) {
                row = sheet.createRow(0);
            }

            Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveGCGCParameters.exportGCGC);
            parameters.setParameterValue(SaveGCGCParameters.exportGCGC, elementsObjects);

            GCGCColumnName[] elements = CollectionUtils.changeArrayType(elementsObjects,
                    GCGCColumnName.class);

            // Write head
            int fieldsNumber = this.getNumFields(elements);
            int cont = 0;
            for (GCGCColumnName p : elements) {
                if (p.isColumnShown()) {
                    this.setCell(row, cont++, p.getColumnName());
                }
            }
            int c = fieldsNumber;
            for (String experimentName : dataset.getAllColumnNames()) {
                this.setCell(row, c++, experimentName);
            }

            // Write content
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowGCGC metabolite = (SimplePeakListRowGCGC) dataset.getRow(i);
                row = sheet.getRow(i + 1);
                if (row == null) {
                    row = sheet.createRow(i + 1);
                }
                cont = 0;

                for (GCGCColumnName p : elements) {
                    if (p.isColumnShown()) {
                        try {
                            this.setCell(row, cont++, metabolite.getVar(p.getGetFunctionName()));
                        } catch (Exception ee) {
                        }
                    }
                }
                c = fieldsNumber;
                for (String experimentName : dataset.getAllColumnNames()) {
                    try {
                        this.setCell(row, c++, metabolite.getPeak(experimentName));
                    } catch (Exception e) {
                        this.setCell(row, c, "NA");
                    }
                }
            }
            //Write the output to a file
            fileOut = new FileOutputStream(path);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            System.out.println("Inoracle2.java --> WriteExcelFileGCGC() " + e);
        }
    }

    /**
     *
     * Writes Comma Separated file for GCxGC-MS data set.
     *
     * @param dataset GCxGC-MS data set
     * @param path Path where the new file will be created
     * @param parameters Parameters for saving the file (columns saved in the new file)
     */
    public void WriteCommaSeparatedFileGCGC(Dataset dataset, String path, SimpleParameterSet parameters) {
        try {
            Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveGCGCParameters.exportGCGC);
            parameters.setParameterValue(SaveGCGCParameters.exportGCGC, elementsObjects);

            GCGCColumnName[] elements = CollectionUtils.changeArrayType(elementsObjects,
                    GCGCColumnName.class);

            CsvWriter w = new CsvWriter(path);
            // Write head
            int fieldsNumber = this.getNumFields(elements);
            String[] data = new String[dataset.getNumberCols() + fieldsNumber];
            int cont = 0;
            for (GCGCColumnName p : elements) {
                if (p.isColumnShown()) {
                    data[cont++] = p.getColumnName();
                }
            }
            int c = fieldsNumber;

            for (String experimentName : dataset.getAllColumnNames()) {
                data[c++] = experimentName;
            }
            w.writeRecord(data);

            // Write content
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowGCGC metabolite = (SimplePeakListRowGCGC) dataset.getRow(i);
                if (metabolite != null) {
                    cont = 0;

                    for (GCGCColumnName p : elements) {
                        if (p.isColumnShown()) {
                            try {
                                data[cont++] = String.valueOf(metabolite.getVar(p.getGetFunctionName()));
                            } catch (Exception ee) {
                            }
                        }
                    }

                    c = fieldsNumber;
                    for (String experimentName : dataset.getAllColumnNames()) {
                        try {
                            data[c++] = String.valueOf(metabolite.getPeak(experimentName));
                        } catch (Exception e) {
                            data[c] = "NA";
                        }
                    }
                    w.writeRecord(data);
                }
            }
            w.endRecord();
            w.close();
        } catch (Exception exception) {
            System.out.println("InOracle.java---> WriteCommaSeparatedFileGCGC() " + exception);
        }
    }

    void writeExpressionData(Dataset dataset, String path) {
        try {
            path = path.replaceAll(".tsv", "");
            String pathAssay = path + "_Assay.tsv";
            String pathFeature = path + "_Feature.tsv";
            String pathPheno = path + "_Pheno.tsv";

            // Write Assay file
            CsvWriter w = new CsvWriter(pathAssay);
            w.setDelimiter('\t');
            List<String> ids = new ArrayList<String>();
            Random r = new Random();
            String[] rowString = new String[dataset.getNumberCols() + 1];

            // Write Assay file header
            rowString[0] = "sample.id";
            int cont = 1;
            for (String sampleName : dataset.getAllColumnNames()) {
                rowString[cont++] = sampleName;
            }
            w.writeRecord(rowString);

            // Write Assay file body
            for (PeakListRow row : dataset.getRows()) {
                rowString = new String[dataset.getNumberCols() + 1];

                String id = "";
                for (int i = 0; i < 11; i++) {
                    id += String.valueOf(r.nextInt(10));
                }
                ids.add(id);
                rowString = new String[dataset.getNumberCols() + 1];
                rowString[0] = id;
                cont = 1;
                for (String sampleName : dataset.getAllColumnNames()) {
                    rowString[cont++] = String.valueOf(row.getPeak(sampleName));
                }
                w.writeRecord(rowString);
            }
            w.close();


            // Write Feature file
            w = new CsvWriter(pathFeature);
            w.setDelimiter('\t');

            if (dataset.getType() == DatasetType.EXPRESSION) {
                rowString = new String[((SimpleExpressionDataset) dataset).getMetaDataNames().size() + 1];

                // Write Feature file header
                rowString[0] = "feature.id";
                cont = 1;
                for (String metaData : ((SimpleExpressionDataset) dataset).getMetaDataNames()) {
                    rowString[cont++] = metaData;
                }
                w.writeRecord(rowString);

                // Write Feature file body
                int contId = 0;
                for (PeakListRow row : dataset.getRows()) {
                    rowString = new String[((SimpleExpressionDataset) dataset).getMetaDataNames().size() + 1];
                    rowString[0] = ids.get(contId++);
                    cont = 1;
                    for (String metaData : ((SimpleExpressionDataset) dataset).getMetaDataNames()) {
                        rowString[cont++] = (String) ((SimplePeakListRowExpression) row).getMetaData(metaData);
                    }
                    w.writeRecord(rowString);
                }
            }
            w.close();


            // Write Pheno file
            w = new CsvWriter(pathPheno);
            w.setDelimiter('\t');
            rowString = new String[dataset.getParametersName().size() + 1];

            // Write Pheno file header
            rowString[0] = "sample.id";
            cont = 1;
            for (String parameter : dataset.getParametersName()) {
                rowString[cont++] = parameter;
            }
            w.writeRecord(rowString);

            // Write Pheno file header
            System.out.println(dataset.getAllColumnNames().size());
            for (String sampleName : dataset.getAllColumnNames()) {
                rowString = new String[dataset.getParametersName().size() + 1];
                rowString[0] = sampleName;
                cont = 1;
                for (String parameter : dataset.getParametersName()) {
                    rowString[cont++] = dataset.getParametersValue(sampleName, parameter);
                }
                w.writeRecord(rowString);
            }
            w.close();

        } catch (IOException ex) {
            Logger.getLogger(WriteFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
