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

import com.csvreader.CsvWriter;
import guineu.data.Dataset;
import guineu.data.GCGCColumnName;
import guineu.data.LCMSColumnName;
import guineu.data.impl.SimpleParameterSet;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.modules.file.saveGCGCFile.SaveGCGCParameters;
import guineu.modules.file.saveLCMSFile.SaveLCMSParameters;
import guineu.util.CollectionUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 *
 * @author SCSANDRA
 */
public class WriteFile {

	/**
	 * Write Comma Separated file for LCMS experiments.
	 * @param dataset
	 * @param path
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
				if (p.isCommon()) {
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
					if (p.isCommon()) {
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
	 * Write the data into an excel file.
	 * @param dataset
	 * @param path
	 */
	public void WriteExcelFileLCMS(Dataset dataset, String path, SimpleParameterSet parameters) {
		FileOutputStream fileOut = null;
		try {
			// Prepare sheet
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


			// Write head
			int fieldsNumber = this.getNumFields(elements);
			int cont = 0;
			for (LCMSColumnName p : elements) {
				if (p.isCommon()) {
					this.setCell(row, cont++, p.getColumnName());
				}
			}
			int c = fieldsNumber;
			for (String experimentName : dataset.getAllColumnNames()) {
				this.setCell(row, c++, experimentName);
			}

			// Write content
			for (int i = 0; i < dataset.getNumberRows(); i++) {
				SimplePeakListRowLCMS lipid = (SimplePeakListRowLCMS) dataset.getRow(i);
				row = sheet.getRow(i + 1);
				if (row == null) {
					row = sheet.createRow(i + 1);
				}

				cont = 0;
				for (LCMSColumnName p : elements) {
					if (p.isCommon()) {
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
			//Write the output to a file
			fileOut = new FileOutputStream(path);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * Write Comma Separated file for LCMS experiments.
	 * @param dataset
	 * @param path
	 */
	public void WriteCommaSeparatedFileconcatenate(Dataset dataset, String path) {
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
	 * Write Comma Separated file for LCMS experiments.
	 * @param dataset
	 * @param path
	 */
	public void WriteXLSFileconcatenate(Dataset dataset, String path) {
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

	public int getNumFields(LCMSColumnName[] elements) {
		int cont = 0;
		for (LCMSColumnName p : elements) {
			if (p.isCommon()) {
				cont++;
			}
		}
		return cont;
	}

	public int getNumFields(GCGCColumnName[] elements) {
		int cont = 0;
		for (GCGCColumnName p : elements) {
			if (p.isCommon()) {
				cont++;
			}
		}
		return cont;
	}

	/**
	 * Write data in a cell of a Excel file.
	 * @param row
	 * @param Index
	 * @param data
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
	 * Write the data into an excel file.
	 * @param dataset
	 * @param path
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
				if (p.isCommon()) {
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
					if (p.isCommon()) {
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
	 * Write Comma Separated file for GCGCTof experiments.
	 * @param dataset
	 * @param path
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
				if (p.isCommon()) {
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
				if (metabolite != null && metabolite.getControl()) {
					cont = 0;

					for (GCGCColumnName p : elements) {
						if (p.isCommon()) {
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
}
