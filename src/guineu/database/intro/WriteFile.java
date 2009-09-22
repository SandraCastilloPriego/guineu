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
package guineu.database.intro;

import com.csvreader.CsvWriter;
import guineu.data.Dataset;
import guineu.data.Parameter;
import guineu.data.datamodels.GCGCColumnName;
import guineu.data.datamodels.LCMSColumnName;
import guineu.data.impl.SimpleParameterSet;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.data.impl.SimplePeakListRowOther;
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
			Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveLCMSParameters.exportItemMultipleSelection);
			parameters.setParameterValue(SaveLCMSParameters.exportItemMultipleSelection, elementsObjects);

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
			for (String experimentName : dataset.getNameExperiments()) {
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
				for (String experimentName : dataset.getNameExperiments()) {
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

			Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveLCMSParameters.exportItemMultipleSelection);
			parameters.setParameterValue(SaveLCMSParameters.exportItemMultipleSelection, elementsObjects);

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
			for (String experimentName : dataset.getNameExperiments()) {
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
				for (String experimentName : dataset.getNameExperiments()) {
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
			for (String experimentName : dataset.getNameExperiments()) {
				data[c++] = experimentName;
			}
			w.writeRecord(data);
			for (int i = 0; i < dataset.getNumberRows(); i++) {
				SimplePeakListRowOther lipid = (SimplePeakListRowOther) dataset.getRow(i);
				c = 0;
				for (String experimentName : dataset.getNameExperiments()) {
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
			for (String experimentName : dataset.getNameExperiments()) {
				this.setCell(row, cont++, experimentName);

			}
			for (int i = 0; i < dataset.getNumberRows(); i++) {
				SimplePeakListRowOther lipid = (SimplePeakListRowOther) dataset.getRow(i);

				row = sheet.getRow(i + 1);
				if (row == null) {
					row = sheet.createRow(i + 1);
				}
				int c = 0;
				for (String experimentName : dataset.getNameExperiments()) {
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
		}else if (data.getClass().toString().contains("Integer")) {
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
		/*	FileOutputStream fileOut = null;
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

		Object elementsObjects[] = (Object[]) parameters.getParameterValue(SaveGCGCParameters.exportItemMultipleSelection);
		parameters.setParameterValue(SaveGCGCParameters.exportItemMultipleSelection, elementsObjects);

		GCGCColumnName[] elements = CollectionUtils.changeArrayType(elementsObjects,
		GCGCColumnName.class);

		int fieldsNumber = this.getNumFields(elements);
		int cont = 0;
		for (Parameter p : parameters.getParameters()) {
		if ((Boolean) parameters.getParameterValue(p)) {
		this.setCell(row, cont++, p.getName());
		}
		}
		int c = fieldsNumber;
		for (String experimentName : dataset.getNameExperiments()) {
		this.setCell(row, c++, experimentName);
		}
		for (int i = 0; i < dataset.getNumberRows(); i++) {
		SimplePeakListRowGCGC metabolite = (SimplePeakListRowGCGC) dataset.getRow(i);
		row = sheet.getRow(i + 1);
		if (row == null) {
		row = sheet.createRow(i + 1);
		}
		cont = 0;
		for (Parameter p : parameters.getParameters()) {
		if ((Boolean) parameters.getParameterValue(p)) {
		try {
		if (p.getName().matches("^ID.*")) {
		this.setCell(row, cont++, metabolite.getID());
		} else if (p.getName().matches(".*RT1.*")) {
		this.setCell(row, cont++, metabolite.getRT1());
		} else if (p.getName().matches(".*RT2.*")) {
		this.setCell(row, cont++, metabolite.getRT2());
		} else if (p.getName().matches(".*RTI*")) {
		this.setCell(row, cont++, metabolite.getRTI());
		} else if (p.getName().matches(".*Mass.*")) {
		this.setCell(row, cont++, metabolite.getMass());
		} else if (p.getName().matches(".*Difference to ideal peak.*")) {
		this.setCell(row, cont++, metabolite.getDifference());
		} else if (p.getName().matches(".*Num Found.*")) {
		this.setCell(row, cont++, metabolite.getNumFound());
		} else if (p.getName().matches(".*CAS.*")) {
		this.setCell(row, cont++, metabolite.getCAS());
		} else if (p.getName().matches(".*Name.*")) {
		this.setCell(row, cont++, metabolite.getName());
		} else if (p.getName().matches(".*All names.*")) {
		this.setCell(row, cont++, metabolite.getAllNames());
		} else if (p.getName().matches(".*Pubchem ID.*")) {
		this.setCell(row, cont++, metabolite.getPubChemID());
		} else if (p.getName().matches(".*Max Similarity.*")) {
		this.setCell(row, cont++, metabolite.getMaxSimilarity());
		} else if (p.getName().matches(".*Mean Similarity.*")) {
		this.setCell(row, cont++, metabolite.getMeanSimilarity());
		} else if (p.getName().matches(".*Similarity std dev.*")) {
		this.setCell(row, cont++, metabolite.getSimilaritySTDDev());
		} else if (p.getName().matches(".*Spectrum.*")) {
		this.setCell(row, cont++, metabolite.getSpectrumString());
		}
		} catch (Exception ee) {
		}
		}
		}
		c = fieldsNumber;
		for (String experimentName : dataset.getNameExperiments()) {
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
		}*/
	}

	/**
	 * Write Comma Separated file for GCGCTof experiments.
	 * @param dataset
	 * @param path
	 */
	public void WriteCommaSeparatedFileGCGC(Dataset dataset, String path, SimpleParameterSet parameters) {
		try {
			/* CsvWriter w = new CsvWriter(path);
			int fieldsNumber = this.getNumFields(parameters);
			String[] data = new String[dataset.getNumberCols() + fieldsNumber];
			int cont = 0;
			for (Parameter p : parameters.getParameters()) {
			if ((Boolean) parameters.getParameterValue(p)) {
			data[cont++] = p.getName();
			}
			}
			int c = fieldsNumber;

			for (String experimentName : dataset.getNameExperiments()) {
			data[c++] = experimentName;
			}
			w.writeRecord(data);
			for (int i = 0; i < dataset.getNumberRows(); i++) {
			SimplePeakListRowGCGC metabolite = (SimplePeakListRowGCGC) dataset.getRow(i);
			if (metabolite != null && metabolite.getControl()) {
			cont = 0;
			for (Parameter p : parameters.getParameters()) {
			if ((Boolean) parameters.getParameterValue(p)) {
			try {
			if (p.getName().matches("^ID.*")) {
			data[cont++] = String.valueOf(metabolite.getID());
			} else if (p.getName().matches(".*RT1.*")) {
			data[cont++] = String.valueOf(metabolite.getRT1());
			} else if (p.getName().matches(".*RT2.*")) {
			data[cont++] = String.valueOf(metabolite.getRT2());
			} else if (p.getName().matches(".*RTI*")) {
			data[cont++] = String.valueOf(metabolite.getRTI());
			} else if (p.getName().matches(".*Mass.*")) {
			data[cont++] = String.valueOf(metabolite.getMass());
			} else if (p.getName().matches(".*Difference to ideal peak.*")) {
			data[cont++] = String.valueOf(metabolite.getDifference());
			} else if (p.getName().matches(".*CAS.*")) {
			data[cont++] = metabolite.getCAS();
			} else if (p.getName().matches(".*Num Found.*")) {
			data[cont++] = String.valueOf(metabolite.getNumFound());
			} else if (p.getName().matches(".*Metabolite Name.*")) {
			data[cont++] = metabolite.getName();
			} else if (p.getName().matches(".*Metabolite all Names.*")) {
			data[cont++] = metabolite.getAllNames();
			} else if (p.getName().matches(".*Pubchem ID.*")) {
			data[cont++] = metabolite.getPubChemID();
			} else if (p.getName().matches(".*Max Similarity.*")) {
			data[cont++] = String.valueOf(metabolite.getMaxSimilarity());
			} else if (p.getName().matches(".*Mean Similarity.*")) {
			data[cont++] = String.valueOf(metabolite.getMeanSimilarity());
			} else if (p.getName().matches(".*Similarity std dev.*")) {
			data[cont++] = String.valueOf(metabolite.getSimilaritySTDDev());
			} else if (p.getName().matches(".*Spectrum.*")) {
			data[cont++] = metabolite.getSpectrumString();
			}
			} catch (Exception ee) {
			}
			}
			}
			c = fieldsNumber;
			for (String experimentName : dataset.getNameExperiments()) {
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
			w.close();*/
		} catch (Exception exception) {
			System.out.println("InOracle.java---> WriteCommaSeparatedFileGCGC() " + exception);
		}
	}
}
