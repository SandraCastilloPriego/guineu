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

import guineu.data.parser.Parser;
import guineu.data.Dataset;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class LCMSParserXLS extends ParserXLS implements Parser {

	private String DatasetName;
	private SimpleDataset dataset;
	private Vector<String> head;
	private Lipidclass LipidClassLib;
	private HSSFWorkbook book;
	private String sheetName;
	private int numberRows,  rowsReaded;

	public LCMSParserXLS(String DatasetName, String sheetName) {
		this.numberRows = 0;
		this.rowsReaded = 0;
		this.DatasetName = DatasetName;
		this.sheetName = sheetName;
		this.dataset = new SimpleDataset(this.getDatasetName());
		this.head = new Vector<String>();
		this.LipidClassLib = new Lipidclass();
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

			int initRow = this.getRowInit(sheet);

			numberRows = this.getNumberRows(initRow, sheet);

			HSSFRow row = sheet.getRow(initRow);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				HSSFCell cell = row.getCell((short) i);
				this.head.addElement(cell.toString());
			}
			this.readLipids(initRow + 1, numberRows, sheet);

			this.setExperimentsName(head);

		} catch (IOException ex) {
			Logger.getLogger(LCMSParserXLS.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Reads lipid information.
	 * @param initRow
	 * @param numberRows
	 * @param numberCols
	 * @param sheet
	 */
	public void readLipids(int initRow, int numberRows, HSSFSheet sheet) {
		for (int i = initRow; i < numberRows + initRow; i++) {
			HSSFRow row = sheet.getRow(i);
			this.readRow(row);
			this.rowsReaded++;
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
		SimplePeakListRowLCMS lipid = new SimplePeakListRowLCMS();
		for (int i = 0; i < row.getLastCellNum(); i++) {
			try {
				String title = head.elementAt(i);
				if (title == null) {
					continue;
				}
				cell = row.getCell((short) i);
				if (title.matches(RegExp.MZ.getREgExp())) {
					if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						lipid.setMZ(cell.getNumericCellValue());
					} else {
						lipid.setMZ(Double.valueOf(cell.toString()));
					}
				} else if (title.matches(RegExp.RT.getREgExp())) {
					if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						lipid.setRT(cell.getNumericCellValue());
					} else {
						lipid.setRT(Double.valueOf(cell.toString()));
					}
				} else if (title.matches(RegExp.NAME.getREgExp())) {
					String str = cell.toString();
					if (str.indexOf("GPEth") > -1) {
						str = this.replace(str, "GPEth", "GPEtn");
					}
					lipid.setName(str);
					lipid.setLipidClass(this.LipidClassLib.get_class(lipid.getName()));
				} else if (title.matches(RegExp.CLASS.getREgExp())) {
				} else if (title.matches(RegExp.NFOUND.getREgExp())) {
					if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						lipid.setNumFound((int) cell.getNumericCellValue());
					} else {
						lipid.setNumFound(Integer.valueOf(cell.toString()));
					}
				} else if (title.matches(RegExp.STANDARD.getREgExp())) {
					if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						lipid.setStandard((int) cell.getNumericCellValue());
					} else {
						lipid.setStandard(Integer.valueOf(cell.toString()));
					}
				} else if (title.matches(RegExp.ALLNAMES.getREgExp())) {
					try {
						lipid.setAllNames(cell.toString());
					} catch (Exception e) {
					}
				} else if (title.matches(RegExp.ALIGNMENT.getREgExp())) {
					try {
						lipid.setAllNames(cell.toString());
					} catch (Exception e) {
					}
				} else {
					try {
						lipid.setPeak(title, cell.getNumericCellValue());
					} catch (Exception e) {
						lipid.setPeak(title, 0.0);
					}
				}

				if (i == 0 && (cell.getCellStyle().getFillForegroundColor() == 13)) {
					lipid.setStandard(1);
				}
				int DataType = this.v_type(book, row, cell);
				if (DataType == 0) {
					lipid.setControl(false);
					lipid.setName("z-non valid");
				} else {
					lipid.setControl(true);
				}

				if (lipid.getName() == null) {
					lipid.setName("unknown");
				}
				lipid.setLipidClass(this.LipidClassLib.get_class(lipid.getName()));
			} catch (Exception exception) {
				//exception.printStackTrace();
			}
		}
		this.dataset.AddRow(lipid);
	}

	/**
	 *
	 * @param row
	 * @return Number of columns with lipid information.
	 */
	public int getLipidNumberCols(HSSFRow row) {
		int N = 3;
		for (short i = 0; i < row.getLastCellNum(); i++) {
			String str = row.getCell(i).toString().replaceAll("'", ",");
			if (row.getCell(i) != null) {
				this.head.addElement(str);
				if (row.getCell(i).toString().matches(".*Class.*|.*Num Found.*|.*Standard.*")) {
					N++;
				}
			}
		}
		return N;
	}

	/**
	 *
	 * @param sheet
	 * @return number of row which it starts to read the excel file.
	 */
	public int getRowInit(HSSFSheet sheet) {

		Iterator rowIt = sheet.rowIterator();
		int num = -1;

		String average = "Average M/Z";

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

	public String getDatasetName() {
		return "LCMS - " + this.getDatasetName(DatasetName) + " - " + sheetName;
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
			for (RegExp value : RegExp.values()) {
				regExpression += value.getREgExp() + "|";
			}

			for (int i = 0; i < header.size(); i++) {
				if (!header.elementAt(i).matches(regExpression)) {
					this.dataset.AddNameExperiment(header.elementAt(i));
				}
			}
			
		} catch (Exception exception) {
		}
	}

	public float getProgress() {
		if (this.numberRows != 0) {
			return (float) this.rowsReaded / this.numberRows;
		} else {
			return 0.0f;
		}
	}

	public Dataset getDataset() {
		return this.dataset;
	}
}
