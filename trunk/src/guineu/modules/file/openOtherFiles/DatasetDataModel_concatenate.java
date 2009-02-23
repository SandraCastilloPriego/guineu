/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.file.openOtherFiles;

import guineu.data.impl.SimpleDataset_concatenate;
import guineu.data.impl.SimplePeakListRowConcatenate;
import guineu.data.PeakListRow_concatenate;
import guineu.data.maintable.*;
import guineu.data.Dataset;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.table.AbstractTableModel;



import java.util.*;

public class DatasetDataModel_concatenate extends AbstractTableModel implements DataTableModel {

    /**
     * All data in the main windows. It can be LCMS or GCGC-Tof data.
     */
    private static final long serialVersionUID = 1L;
    private String columns[];
    private Object[][] rows; //content all data   
    private int numColumns;
    private int numRows;
    private Vector<String> columns_mol = new Vector<String>();
    private boolean type;
    private SimpleDataset_concatenate dataset;
    protected SortingDirection isSortAsc = SortingDirection.Ascending;
    protected int sortCol = 0;

    public DatasetDataModel_concatenate(Dataset dataset) {
        this.dataset = (SimpleDataset_concatenate) dataset;        
            //LCMS files           
            //columns_mol.add("Lipid Name");           
        
        this.set_samples();
    }

    /**
     * Makes a new rows[][] with the new dates. First add the columns names with "writeSamplesNames(x)", and then
     * rewrite all data (rows[][]).
     * @param sampleNames vector with the names of the experiment whitch have to be in the table.
     * @param type is true for GCGC-Tof data and false to LCMS data
     */
    public void set_samples() {
        this.writeSamplesName();
        this.writeData();
        numColumns = columns.length;
        numRows = rows.length;
    }

    /*public Object[][] getRows(){
    return rows;
    }	
    public void setRows(Object[][] rows){
    this.rows = rows;
    numRows = rows.length;
    } */
    /**
     * Adds the name of the experiments in the "columns" variable. There are the title of the columns.
     * @param sampleNames list of all experiments names.
     */
    public void writeSamplesName() {
        columns = new String[dataset.getNumberCols() + this.columns_mol.size()];
        for (int i = 0; i < columns_mol.size(); i++) {
            columns[i] = (String) columns_mol.elementAt(i);
        }
        int cont = columns_mol.size();
        for (String nameExperiment : this.dataset.getNameExperiments()) {
            try {
                columns[cont++] = nameExperiment;
            } catch (Exception e) {
            }
        }
    }

    /**
     * Takes all necessary information from the database and writes it in rows[][]. 
     * @param data 
     */
    public void writeData() {
        rows = new Object[dataset.getNumberRows()][dataset.getNumberCols() + this.columns_mol.size()];

       
            for (int i = 0; i < dataset.getNumberRows(); i++) {
                SimplePeakListRowConcatenate lipid = (SimplePeakListRowConcatenate) dataset.getRow(i);
                         /*if (lipid.getID() != -1) {
                    rows[i][1] = lipid.getID();
                } else {
                    rows[i][1] = i;
                    lipid.setID(i);
                }
                rows[i][2] = lipid.getMZ();
                rows[i][3] = lipid.getRT();
                rows[i][1] = lipid.getName();
                rows[i][5] = lipid.getLipidClass();
                rows[i][6] = lipid.getNumFound();
                if (lipid.getStandard() == 1) {
                    rows[i][7] = new Boolean(true);
                } else {
                    rows[i][7] = new Boolean(false);
                }
                rows[i][8] = lipid.getFAComposition();
                rows[i][9] = lipid.getAllNames();
                if (lipid.getNumberAlignment() != -1) {
                    rows[i][10] = lipid.getNumberAlignment();
                } else {
                    rows[i][10] = 0;*
                }*/
                int cont = 0;
                for (String nameExperiment : this.dataset.getNameExperiments()) {
                    try {
                        rows[i][cont++] = lipid.getPeak(nameExperiment);
                    } catch (Exception e) {
                    }
                }

            }        
    }

    public Dataset removeRows() {
        SimpleDataset_concatenate newDataset = new SimpleDataset_concatenate(this.dataset.getDatasetName());
        for (int i = 0; i < rows.length; i++) {
            if (!(Boolean) rows[i][0]) {
                PeakListRow_concatenate peakListRow = (PeakListRow_concatenate) dataset.getRow(i).clone();
                newDataset.AddRow(peakListRow);
            }
        }
        newDataset.setNameExperiments(dataset.getNameExperiments());
        newDataset.setType(dataset.getType());
        return newDataset;
    }

    public int getColumnCount() {
        return numColumns;
    }

    public int getRowCount() {
        return numRows;
    }

    public Object getValueAt(final int row, final int column) {
        return rows[row][column];
    }

    @Override
    public String getColumnName(int columnIndex) {
        String str = columns[columnIndex];
        /* if (columnIndex == sortCol && columnIndex != 0)
        str += isSortAsc ? " >>" : " <<";*/
        return str;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (getValueAt(0, c) != null) {
            return getValueAt(0, c).getClass();
        } else {
            return Object.class;
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        rows[row][column] = aValue;
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    void addColumn() {
        String[] oldColumns = this.columns.clone();
        this.columns = new String[oldColumns.length + 1];
        for (int i = 0; i < oldColumns.length; i++) {
            System.out.println(oldColumns[i]);
            this.columns[i] = oldColumns[i];
        }
        this.columns[oldColumns.length] = "New Column";
        this.numColumns = this.columns.length;

        this.addColumnObject(this.rows);
        this.numRows = this.rows.length;
    }

    public void addColumnObject(Object[][] o) {
        Object[][] oldRows = o.clone();
        o = new Object[oldRows.length][oldRows[0].length + 1];
        for (int i = 0; i < oldRows.length; i++) {
            for (int j = 0; j < oldRows[0].length; j++) {
                o[i][j] = oldRows[i][j];
            }
            o[i][oldRows[0].length] = " ";
        }
    }

    public void addColumnObject(int[][] o) {
        int[][] oldRows = o.clone();
        o = new int[oldRows.length][oldRows[0].length + 1];
        for (int i = 0; i < oldRows.length; i++) {
            for (int j = 0; j < oldRows[0].length; j++) {
                o[i][j] = oldRows[i][j];
            }
            o[i][oldRows[0].length] = 0;
        }
    }

    public void setColumnCount(int count) {
        this.numColumns = count;
    }

    public SortingDirection getSortDirection() {
        return isSortAsc;
    }

    public int getSortCol() {
        return sortCol;
    }

    public void setSortDirection(SortingDirection direction) {
        this.isSortAsc = direction;
    }

    public void setSortCol(int column) {
        this.sortCol = column;
    }

    public Object[][] getData() {
        return rows;
    }

    public void changeData(int column, int row) {
        if (dataset.getType() != DatasetType.LCMS || dataset.getType() != DatasetType.GCGCTOF ) {
			SimplePeakListRowConcatenate peakListRow = (SimplePeakListRowConcatenate) this.dataset.getRow(row);
            try {
                String experimentName = this.columns[column];
                peakListRow.setPeak(experimentName, (String)rows[row][column]);
            } catch (Exception e) {
				e.printStackTrace();
            }
        } 
    }

    public DatasetType getType() {
        return this.dataset.getType();
    }
   
}
