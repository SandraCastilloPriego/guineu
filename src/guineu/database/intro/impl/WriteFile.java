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
package guineu.database.intro.impl;

import com.csvreader.CsvWriter;
import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.GCGCColumnName;
import guineu.data.LCMSColumnName;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleExpressionDataset;
import guineu.data.impl.peaklists.SimplePeakListRowExpression;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.modules.file.saveGCGCFile.SaveGCGCParameters;
import guineu.modules.file.saveLCMSFile.SaveLCMSParameters;
import guineu.parameters.SimpleParameterSet;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Writes the data sets into a file.
 *
 * @author SCSANDRA
 */
public class WriteFile {

        HSSFWorkbook wb;
        List<HSSFCellStyle> styles = new ArrayList<HSSFCellStyle>();

        /**
         * Writes Comma Separated file for LC-MS data set.
         *
         * @param dataset LC-MS data set
         * @param path Path where the new file will be created
         * @param parameters Parameters for saving the file (columns saved in the new file)
         */
        public void WriteCommaSeparatedFileLCMS(Dataset dataset, String path, SimpleParameterSet parameters) {
                try {
                        LCMSColumnName elementsObjects[] = null;
                        if (parameters == null) {
                                elementsObjects = LCMSColumnName.values();
                        } else {
                                elementsObjects = parameters.getParameter(SaveLCMSParameters.exportLCMS).getValue();
                        }

                        CsvWriter w = new CsvWriter(path);

                        //write head
                        int fieldsNumber = elementsObjects.length;
                        String[] data = new String[dataset.getNumberCols() + fieldsNumber];
                        int cont = 0;
                        for (LCMSColumnName p : elementsObjects) {
                                data[cont++] = p.getColumnName();
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
                                for (LCMSColumnName p : elementsObjects) {
                                        try {
                                                data[cont++] = String.valueOf(lipid.getVar(p.getGetFunctionName()));
                                        } catch (Exception ee) {
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

                        LCMSColumnName elementsObjects[] = parameters.getParameter(SaveLCMSParameters.exportLCMS).getValue();


                        // Writes head
                        int fieldsNumber = elementsObjects.length;
                        int cont = 0;
                        for (LCMSColumnName p : elementsObjects) {
                                this.setCell(row, cont++, p.getColumnName(), null);
                        }
                        int c = fieldsNumber;
                        for (String experimentName : dataset.getAllColumnNames()) {
                                this.setCell(row, c++, experimentName, null);
                        }

                        // Writes content
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowLCMS lipid = (SimplePeakListRowLCMS) dataset.getRow(i);
                                row = sheet.getRow(i + 1);
                                if (row == null) {
                                        row = sheet.createRow(i + 1);
                                }

                                cont = 0;
                                for (LCMSColumnName p : elementsObjects) {
                                        try {
                                                this.setCell(row, cont++, lipid.getVar(p.getGetFunctionName()), null);
                                        } catch (Exception ee) {
                                        }

                                }
                                c = fieldsNumber;
                                for (String experimentName : dataset.getAllColumnNames()) {
                                        this.setCell(row, c++, lipid.getPeak(experimentName), null);
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
         * Writes Comma Separated file for expression data set.
         *
         * @param dataset expression data set
         * @param path Path where the new file will be created
         */
        public void WriteCommaSeparatedExpressionSetDataset(Dataset dataset, String path) {
                try {
                        CsvWriter w = new CsvWriter(path);
                        Vector<String> metadata = ((SimpleExpressionDataset) dataset).getMetaDataNames();
                        String[] data = new String[dataset.getNumberCols() + metadata.size()];
                        int c = 0;
                        for (String experimentName : metadata) {
                                data[c++] = experimentName;
                        }
                        for (String experimentName : dataset.getAllColumnNames()) {
                                data[c++] = experimentName;
                        }
                        w.writeRecord(data);


                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowExpression peakRow = (SimplePeakListRowExpression) dataset.getRow(i);
                                c = 0;
                                for (String experimentName : metadata) {
                                        data[c++] = (String) peakRow.getMetaData(experimentName);
                                }

                                for (String experimentName : dataset.getAllColumnNames()) {
                                        if (peakRow.getPeak(experimentName) == null) {
                                                data[c++] = "";
                                        } else {
                                                data[c++] = String.valueOf(peakRow.getPeak(experimentName));
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
                        HSSFSheet sheet;
                        try {
                                FileInputStream fileIn = new FileInputStream(path);
                                POIFSFileSystem fs = new POIFSFileSystem(fileIn);
                                wb = new HSSFWorkbook(fs);
                                int NumberOfSheets = wb.getNumberOfSheets();
                                sheet = wb.createSheet(String.valueOf(NumberOfSheets));
                        } catch (Exception exception) {
                                wb = new HSSFWorkbook();
                                sheet = wb.createSheet("Page 1");
                        }
                        HSSFRow row = sheet.getRow(0);
                        if (row == null) {
                                row = sheet.createRow(0);
                        }
                        int cont = 0;
                        for (String experimentName : dataset.getAllColumnNames()) {
                                this.setCell(row, cont++, experimentName, null);

                        }

                        HSSFPalette palette = wb.getCustomPalette();
                        Color[] colors = dataset.getRowColor();
                        if (colors.length > 0) {
                                for (int i = 0; i < dataset.getNumberRows(); i++) {
                                        palette.setColorAtIndex((short) 0x12, (byte) colors[i].getRed(), (byte) colors[i].getGreen(), (byte) colors[i].getBlue());
                                        HSSFColor mycolor = palette.getColor((short) 0x12);  //unmodified
                                        SimplePeakListRowOther lipid = (SimplePeakListRowOther) dataset.getRow(i);

                                        row = sheet.getRow(i + 1);
                                        if (row == null) {
                                                row = sheet.createRow(i + 1);
                                        }
                                        int c = 0;
                                        for (String experimentName : dataset.getAllColumnNames()) {
                                                if (lipid.getPeak(experimentName) == null) {
                                                        this.setCell(row, c++, "", mycolor);
                                                } else {
                                                        this.setCell(row, c++, lipid.getPeak(experimentName), mycolor);
                                                }
                                        }

                                }
                        } else {

                                Vector<String> names = dataset.getAllColumnNames();
                                for (int i = 0; i < dataset.getNumberRows(); i++) {
                                        SimplePeakListRowOther lipid = (SimplePeakListRowOther) dataset.getRow(i);
                                        row = sheet.getRow(i + 1);
                                        if (row == null) {
                                                row = sheet.createRow(i + 1);
                                        }
                                        for (int j = 0; j < names.size(); j++) {
                                                Color c = dataset.getCellColor(i, j+1);
                                                HSSFColor mycolor = null;
                                                if (c != null) {
                                                        mycolor = palette.findColor((byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue());
                                                }
                                                if (lipid.getPeak(names.elementAt(j)) == null) {
                                                        this.setCell(row, j, "", null);
                                                } else {
                                                        this.setCell(row, j, lipid.getPeak(names.elementAt(j)), mycolor);
                                                }
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
         * Write data in a cell of the excel file.
         *
         * @param row Cell row
         * @param Index Cell column
         * @param data data to be writen into the cell
         */
        private void setCell(HSSFRow row, int Index, Object data, HSSFColor color) {
                HSSFCell cell = row.getCell(Index);
                if (cell == null) {
                        cell = row.createCell(Index);
                }
                if (data.getClass().toString().contains("String")) {
                        cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                        cell.setCellValue((String) data);
                } else if (data.getClass().toString().contains("Double")) {
                        cell.setCellValue((Double) data);
                } else if (data.getClass().toString().contains("Integer")) {
                        cell.setCellValue((Integer) data);
                }
                if (color != null) {
                        HSSFCellStyle style1 = wb.createCellStyle();
                        style1.setFillForegroundColor(color.getIndex());
                        style1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                        for(HSSFCellStyle style : styles){
                                if(style.getFillForegroundColor() == style1.getFillForegroundColor()){
                                        style1 = style;
                                }
                        }
                        if(!styles.contains(style1)){
                                this.styles.add(style1);
                        }
                        cell.setCellStyle(style1);
                } else {                       
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

                        GCGCColumnName elementsObjects[] = parameters.getParameter(SaveGCGCParameters.exportGCGC).getValue();

                        // Write head
                        int fieldsNumber = elementsObjects.length;
                        int cont = 0;
                        for (GCGCColumnName p : elementsObjects) {
                                this.setCell(row, cont++, p.getColumnName(), null);
                        }
                        int c = fieldsNumber;
                        for (String experimentName : dataset.getAllColumnNames()) {
                                this.setCell(row, c++, experimentName, null);
                        }

                        // Write content
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowGCGC metabolite = (SimplePeakListRowGCGC) dataset.getRow(i);
                                row = sheet.getRow(i + 1);
                                if (row == null) {
                                        row = sheet.createRow(i + 1);
                                }
                                cont = 0;

                                for (GCGCColumnName p : elementsObjects) {
                                        try {
                                                this.setCell(row, cont++, metabolite.getVar(p.getGetFunctionName()), null);
                                        } catch (Exception ee) {
                                        }

                                }
                                c = fieldsNumber;
                                for (String experimentName : dataset.getAllColumnNames()) {
                                        try {
                                                this.setCell(row, c++, metabolite.getPeak(experimentName), null);
                                        } catch (Exception e) {
                                                this.setCell(row, c, "NA", null);
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
                        GCGCColumnName elementsObjects[] = parameters.getParameter(SaveGCGCParameters.exportGCGC).getValue();


                        CsvWriter w = new CsvWriter(path);
                        // Write head
                        int fieldsNumber = elementsObjects.length;
                        String[] data = new String[dataset.getNumberCols() + fieldsNumber];
                        int cont = 0;
                        for (GCGCColumnName p : elementsObjects) {
                                data[cont++] = p.getColumnName();
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

                                        for (GCGCColumnName p : elementsObjects) {
                                                try {
                                                        data[cont++] = String.valueOf(metabolite.getVar(p.getGetFunctionName()));
                                                } catch (Exception ee) {
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

        void writeExpressionData(Dataset dataset, String path, SimpleParameterSet parameters) {
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
                        } else if (dataset.getType() == DatasetType.LCMS) {
                                LCMSColumnName elementsObjects[] = parameters.getParameter(SaveLCMSParameters.exportLCMS).getValue();


                                rowString = new String[elementsObjects.length + 1];

                                // Write Feature file header
                                rowString[0] = "feature.id";
                                cont = 1;
                                for (LCMSColumnName data : elementsObjects) {
                                        rowString[cont++] = data.getColumnName();
                                }
                                w.writeRecord(rowString);

                                // Write Feature file body
                                int contId = 0;
                                for (PeakListRow row : dataset.getRows()) {
                                        rowString = new String[elementsObjects.length + 1];
                                        rowString[0] = ids.get(contId++);
                                        cont = 1;
                                        for (LCMSColumnName data : elementsObjects) {
                                                rowString[cont++] = String.valueOf(row.getVar(data.getGetFunctionName()));
                                        }
                                        w.writeRecord(rowString);
                                }

                        } else if (dataset.getType() == DatasetType.GCGCTOF) {
                                GCGCColumnName elementsObjects[] = parameters.getParameter(SaveGCGCParameters.exportGCGC).getValue();

                                rowString = new String[elementsObjects.length + 1];

                                // Write Feature file header
                                rowString[0] = "feature.id";
                                cont = 1;
                                for (GCGCColumnName data : elementsObjects) {
                                        rowString[cont++] = data.getColumnName();
                                }
                                w.writeRecord(rowString);

                                // Write Feature file body
                                int contId = 0;
                                for (PeakListRow row : dataset.getRows()) {
                                        rowString = new String[elementsObjects.length + 1];
                                        rowString[0] = ids.get(contId++);
                                        cont = 1;
                                        for (GCGCColumnName data : elementsObjects) {
                                                rowString[cont++] = String.valueOf(row.getVar(data.getGetFunctionName()));
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

        void WriteExcelExpressionSetDataset(Dataset dataset, String path) {
                throw new UnsupportedOperationException("Not yet implemented");
        }
}
