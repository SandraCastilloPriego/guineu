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
package guineu.data.parser.impl;

import guineu.data.parser.Parser;
import guineu.data.Dataset;
import guineu.data.ParameterType;
import guineu.data.GCGCColumnName;
import guineu.data.DatasetType;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class GCGCParserXLS extends ParserXLS implements Parser {

    private String DatasetName;
    private SimpleGCGCDataset dataset;
    private Vector<String> head;
    private HSSFWorkbook book;
    private String sheetName;
    private int numberRows,  rowsReaded, numColumns;

    public GCGCParserXLS(String DatasetName, String sheetName, int numColumns) {
        this.numberRows = 0;
        this.rowsReaded = 0;
        this.numColumns = numColumns;
        this.DatasetName = DatasetName;
        this.sheetName = sheetName;
        this.dataset = new SimpleGCGCDataset(this.getDatasetName());
		this.dataset.setType(DatasetType.GCGCTOF);
        this.head = new Vector<String>();
    }

    public void fillData() {
        try {
            book = this.openExcel(DatasetName);
            HSSFSheet sheet;
            try {
                sheet = book.getSheet(sheetName);
            } catch (Exception exception) {
                sheet = book.getSheetAt(0);
            }

          //  int initRow = this.getRowInit(sheet);

            int initRow = numColumns;
            
            if (initRow > -1) {
                numberRows = this.getNumberRows(initRow, sheet);
                HSSFRow row = sheet.getRow(initRow);

                for (int i = 0; i < row.getLastCellNum(); i++) {
                    HSSFCell cell = row.getCell((short) i);
                    this.head.addElement(cell.toString());
                }
                this.readMetabolites(initRow + 1, numberRows, sheet);

                this.setExperimentsName(head);
            } else {
                this.dataset = null;
            }
        } catch (IOException ex) {
            Logger.getLogger(GCGCParserXLS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Reads lipid information.
     * @param initRow
     * @param numberRows
     * @param numberCols
     * @param sheet
     */
    public void readMetabolites(int initRow, int numberRows, HSSFSheet sheet) {
        for (int i = initRow; i < numberRows + initRow; i++) {
            HSSFRow row = sheet.getRow(i);
            this.readRow(row);
            this.rowsReaded++;
        }
    }

    private Object getType(String data, ParameterType type) {
        try {
            switch (type) {
                case BOOLEAN:
                    return new Boolean(data);
                case INTEGER:
                    return Integer.valueOf(data);
                case DOUBLE:
                    return Double.valueOf(data);
                case STRING:
                    return data;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Reads lipid information of one row.
     * @param row
     * @param numberCols
     * @return
     */
    public void readRow(HSSFRow row) {
        HSSFCell cell;
        SimplePeakListRowGCGC metabolite = new SimplePeakListRowGCGC();
        for (int i = 0; i < row.getLastCellNum(); i++) {
            try {
                String title = head.elementAt(i);
                if (title == null) {
                    continue;
                }
                cell = row.getCell((short) i);
                boolean isfound = false;

                for (GCGCColumnName field : GCGCColumnName.values()) {
                    if (title.matches(field.getRegularExpression())) {
                        metabolite.setVar(field.getSetFunctionName(), this.getType(cell.toString(), field.getType()));
                        isfound = true;
                        break;
                    }
                }

                if (!isfound) {
                    try {
                        metabolite.setPeak(title, cell.getNumericCellValue());
                    } catch (Exception e) {
						e.printStackTrace();
                        metabolite.setPeak(title, 0.0);
                    }
                }

                if (metabolite.getName() == null) {
                    metabolite.setName("unknown");
                }
            } catch (Exception exception) {
                //exception.printStackTrace();
            }
        }
        this.dataset.addRow(metabolite);
    }

    /**
     *
     * @param sheet
     * @return number of row which it starts to read the excel file.
     */
    public int getRowInit(HSSFSheet sheet) {

        Iterator rowIt = sheet.rowIterator();
        int num = -1;

        while (rowIt.hasNext()) {
            HSSFRow row = (HSSFRow) rowIt.next();
            HSSFCell cell = row.getCell((short) 0);

            if (cell != null) {
                for (GCGCColumnName field : GCGCColumnName.values()) {
                    if (cell.toString().matches(field.getRegularExpression())) {
                        num = row.getRowNum();
                        break;
                    }
                }
            }
        }

        return num;
    }

    public String getDatasetName() {
        return "GCGC - " + this.getDatasetName(DatasetName) + " - " + sheetName;
    }

    public String[] getSheetNames(String fileName) throws IOException {
        HSSFWorkbook wb = this.openExcel(fileName);
        String[] sheetsNames = new String[wb.getNumberOfSheets()];
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            sheetsNames[i] = wb.getSheetName(i);
        }
        return sheetsNames;
    }

    private void setExperimentsName(Vector<String> header) {
        try {

            String regExpression = "";
            for (GCGCColumnName value : GCGCColumnName.values()) {
                regExpression += value.getRegularExpression() + "|";
            }

            for (int i = numColumns; i < header.size(); i++) {
                if (!header.elementAt(i).matches(regExpression)) {
                    this.dataset.addColumnName(header.elementAt(i));
                }
            }

        } catch (Exception exception) {
        }
    }

    public float getProgress() {
        try {
            if (this.numberRows != 0) {
                return (float) this.rowsReaded / this.numberRows;
            } else {
                return 0.0f;
            }
        } catch (Exception exception) {
            return 0.0f;
        }
    }

    public Dataset getDataset() {
        return this.dataset;
    }
}
