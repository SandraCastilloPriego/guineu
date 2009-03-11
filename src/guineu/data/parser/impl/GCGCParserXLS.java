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
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class GCGCParserXLS extends ParserXLS implements Parser {

	private String DatasetPath;
	private HSSFWorkbook book;
	private Vector<String> head;
	SimpleDataset dataset;
	private int numberRows,  rowsReaded;

	public GCGCParserXLS(String DatasetPath) {
		this.numberRows = 0;
		this.rowsReaded = 0;
		this.DatasetPath = DatasetPath;
		this.dataset = new SimpleDataset(this.getDatasetName());
		this.dataset.setType(DatasetType.GCGCTOF);
		head = new Vector<String>();
	}

	public String getDatasetName() {
		return "GCGC - " + this.getDatasetName(DatasetPath);
	}

	public float getProgress() {
		if (this.numberRows != 0) {
			return (float) this.rowsReaded / this.numberRows;
		} else {
			return 0.0f;
		}
	}

	public void fillData() {
		try {

			book = openExcel(DatasetPath);
			HSSFSheet sheet;
			sheet = book.getSheetAt(0);

			HSSFRow row = sheet.getRow(0);
			for (int i = 0; i < row.getLastCellNum(); i++) {
				HSSFCell cell = row.getCell((short) i);
				this.head.addElement(cell.toString());
			}
			numberRows = this.getNumberRows(0, sheet);

			for (int i = 1; i < numberRows + 1; i++) {
				this.read_data(sheet, i);
				this.rowsReaded++;
			}
			this.setExperimentsName(head);

		} catch (IOException ex) {
			Logger.getLogger(GCGCParserXLS.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	/**
	 * Read one line of data in the dataset (=excel file) and introduce it in the StructGCGC gcgctof
	 */
	private void read_data(HSSFSheet sheet, int Index) {
		try {
			HSSFRow row = sheet.getRow(Index);
			SimplePeakListRowGCGC metabolite = new SimplePeakListRowGCGC();
			for (int e = 0; e < head.size(); e++) {
				if (head.elementAt(e).matches(RegExp.MASS.getREgExp())) {
					metabolite.setMass(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.RT1.getREgExp())) {
					metabolite.setRT1(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.RT2.getREgExp())) {
					metabolite.setRT2(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.RTI.getREgExp())) {
					metabolite.setRTI(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.NFOUND.getREgExp())) {
					metabolite.setNumFound(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.DIFFERENCE.getREgExp())) {
					metabolite.setDifference(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.ALLNAMES.getREgExp())) {
					metabolite.setAllNames(this.setStringDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.NAME.getREgExp())) {
					metabolite.setName(this.setStringDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.PUBCHEM.getREgExp())) {
					metabolite.setPubChemID(this.setStringDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.MAXSIM.getREgExp())) {
					metabolite.setMaxSimilarity(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.MEANSIM.getREgExp())) {
					metabolite.setMeanSimilarity(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.SIMSTD.getREgExp())) {
					metabolite.setSimilaritySTDDev(this.setDateInToStruct(row, e));
				} else if (head.elementAt(e).matches(RegExp.SPECTRUM.getREgExp())) {
					metabolite.setSpectrum(this.setStringDateInToStruct(row, e));
				} else {
					try {
						Double concentration = new Double(this.setDateInToStruct(row, e));
						metabolite.setPeak(head.elementAt(e), concentration);
					} catch (Exception ee) {
						metabolite.setPeak(head.elementAt(e), 0.0);
					}
				}
			}
			this.dataset.AddRow(metabolite);

		} catch (Exception exception) {
			System.out.println("ParserMetGCGC.java ---> read_data() " + exception);

		}
	}

	private double setDateInToStruct(HSSFRow row, int Index) {
		try {
			HSSFCell cell = row.getCell((short) Index);
			if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
				return cell.getNumericCellValue();
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				return Double.valueOf(cell.toString());
			}
			return 0;
		} catch (Exception exception) {
			System.out.println("ParserMetGCGC.java ---> setDataInToStruct() " + exception);
			return 0;
		}
	}

	private String setStringDateInToStruct(HSSFRow row, int Index) {
		try {
			HSSFCell cell = row.getCell((short) Index);
			if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				return cell.toString();
			}
			return "";
		} catch (Exception e) {
			System.out.println("ParserMetGCGC.java ---> setStringDataInToStruct() " + e);
			return "";
		}
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

	public Dataset getDataset() {
		return this.dataset;
	}
}
