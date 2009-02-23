/*
 * Copyright 2007-2008 VTT Biotechnology
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

import guineu.data.Dataset;
import guineu.data.impl.Bexperiments;
import guineu.data.impl.ExperimentDataset;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author scsandra
 */
public class ExperimentParserXLS extends ParserXLS {

    private String DatasetPath;
    private HSSFWorkbook book;
    private float progress;
    ExperimentDataset dataset;

    public ExperimentParserXLS(String DatasetPath) {
        this.DatasetPath = DatasetPath;
        this.dataset = new ExperimentDataset(this.getDatasetName());
    }

    public String getDatasetName() {
        return this.getDatasetName(DatasetPath);
    }

    public float getProgress() {
        return progress;
    }

    public Dataset getData() {
        return dataset;
    }

    public void fillData() {

        progress = 0.1f;

        readExcelTableExperiment();

        progress = 0.9f;

    }

    /**
     * Reads the excel file where all the information is.
     * @param xlsfile
     * @return Vector with objects "Experiments" with all parsed information
     */
    public void readExcelTableExperiment() {
        try {
            book = this.openExcel(DatasetPath);
            HSSFSheet sheet = book.getSheetAt(0);
            int start = this.getRowInit(sheet);

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
                    dataset.addExperiment(exp);
                }
            }


        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    /**
     * 
     * @param sheet
     * @return number of row which it starts to read the excel file.
     */
    public int getRowInit(HSSFSheet sheet) {

        Iterator rowIt = sheet.rowIterator();
        int num = -1;

        String average = "Name";

        while (rowIt.hasNext()) {
            HSSFRow row = (HSSFRow) rowIt.next();
            HSSFCell cell = row.getCell((short) 0);

            if (cell != null) {
                if (average.compareTo(cell.toString()) == 0) {
                    num = row.getRowNum();
                }
            }
        }

        return num;
    }
}
