/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
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

/**
 *
 * @author scsandra
 */
public class GCGCParserXLS extends ParserXLS implements Parser {

    private String DatasetPath;
    private HSSFWorkbook book;
    private float progress;
    private String[] mol_dates;
    private Vector<String> head;
    private Vector<String> experimentName;
    SimpleDataset dataset;

    public GCGCParserXLS(String DatasetPath) {
        this.DatasetPath = DatasetPath;
        this.dataset = new SimpleDataset(this.getDatasetName());
        this.dataset.setType(DatasetType.GCGCTOF);
        experimentName = new Vector<String>();
        head = new Vector<String>();

        mol_dates = new String[13];
        mol_dates[0] = ".*Mass.*";
        mol_dates[1] = ".*RT1.*";
        mol_dates[2] = ".*RT2.*";
        mol_dates[3] = ".*RTI.*";
        mol_dates[4] = ".*Num Found.*";
        mol_dates[5] = ".*Difference.*";
        mol_dates[6] = ".*All names.*";
        mol_dates[7] = ".*Name.*";
        mol_dates[8] = ".*Pubchem.*";
        mol_dates[9] = ".*Max.*";
        mol_dates[10] = ".*Mean.*";
        mol_dates[11] = ".*Similarity std dev.*";
        mol_dates[12] = ".*Spectrum.*";
    }

    public String getDatasetName() {
        return this.getDatasetName(DatasetPath);
    }

    public float getProgress() {
        return progress;
    }

    public void fillData() {
        try {

            progress = 0.1f;
            book = openExcel(DatasetPath);
            HSSFSheet sheet;
            sheet = book.getSheetAt(0);

            HSSFRow row = sheet.getRow(0);
            readHead(row);
            int numberRows = this.getNumberRows(0, sheet);

            progress = 0.4f;
            for (int i = 1; i < numberRows + 1; i++) {
                this.read_data(sheet, i);
            }
            this.dataset.setNameExperiments(experimentName);
            progress = 1.0f;
        } catch (IOException ex) {
            Logger.getLogger(GCGCParserXLS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void readHead(HSSFRow row) {
        int N = 0;

        int index = this.getLastIndex(row);

        for (short i = 0; i < index; i++) {
            String str = row.getCell(i).toString().replaceAll("'", "");
            this.head.addElement(str);
            for (int j = 0; j < mol_dates.length - 1; j++) {
                if (str.matches(this.mol_dates[j])) {
                    N++;
                }
            }
        }

        for (short i = (short) N; i < index; i++) {
            String str = row.getCell(i).toString().replaceAll("'", "");
            if (!str.matches(mol_dates[12]) && !str.isEmpty()) {
                experimentName.addElement(str);
            }
        }
        String str = row.getCell((short) index).toString();
        //Spectrum
        if (str.matches(mol_dates[12])) {
            this.head.addElement(str);
        }

    }

    private int getLastIndex(HSSFRow row) {
        int index = row.getLastCellNum();
        try {
            HSSFCell cell = row.getCell((short) index);
            while (cell == null) {
                index--;
                cell = row.getCell((short) index);
            }
            return index;
        } catch (Exception exception) {
            exception.printStackTrace();
            return index;
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
                if (head.elementAt(e).matches(this.mol_dates[0])) {
                    metabolite.setMass(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[1])) {
                    metabolite.setRT1(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[2])) {
                    metabolite.setRT2(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[3])) {
                    metabolite.setRTI(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[4])) {
                    metabolite.setNumFound(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[5])) {
                    metabolite.setDifference(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[6])) {
                    metabolite.setAllNames(this.setStringDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[7])) {
                    metabolite.setName(this.setStringDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[8])) {
                    metabolite.setPubChemID(this.setStringDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[9])) {
                    metabolite.setMaxSimilarity(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[10])) {
                    metabolite.setMeanSimilarity(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[11])) {
                    metabolite.setSimilaritySTDDev(this.setDateInToStruct(row, e));
                } else if (head.elementAt(e).matches(this.mol_dates[12])) {
                    metabolite.setSpectrum(this.setStringDateInToStruct(row, e));
                } else {
                    try{
                        Double concentration = new Double(this.setDateInToStruct(row, e));                   
                        metabolite.setPeak(head.elementAt(e), concentration);
                    }catch(Exception ee){                                         
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

    public Dataset getDataset() {
        return this.dataset;
    }
}
