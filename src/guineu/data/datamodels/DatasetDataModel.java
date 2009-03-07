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
package guineu.data.datamodels;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.table.AbstractTableModel;



import java.util.*;

public class DatasetDataModel extends AbstractTableModel implements DataTableModel {

    /**
     * All data in the main windows. It can be LCMS or GCGC-Tof data.
     */
    private static final long serialVersionUID = 1L;
    private Object[] selection;
    private int numColumns;
    private int numRows;
    private SimpleDataset dataset;
    protected SortingDirection isSortAsc = SortingDirection.Ascending;
    protected int sortCol = 0;

    public DatasetDataModel(Dataset dataset) {
        this.dataset = (SimpleDataset) dataset;
        this.selection = new Object[this.dataset.getNumberRows()];
        numRows = this.dataset.getNumberRows();
        this.writeData();
    }

    public void writeData() {
        PeakListRow peakListRow;
        numColumns = this.dataset.getNumberCols() + this.dataset.getNumberFixColumns();
        for (int i = 0; i < dataset.getNumberRows(); i++) {
            peakListRow = (SimplePeakListRowLCMS) this.dataset.getRow(i);
            selection[i] = new Boolean(false);
            if (peakListRow.getID() == -1) {
                peakListRow.setID(i);
            }
        }

    }

    public SimpleDataset removeRows() {
        SimpleDataset newDataset = new SimpleDataset(this.dataset.getDatasetName());
        /*for (int i = 0; i < rows.length; i++) {
        if (!(Boolean) rows[i][0]) {
        PeakListRow peakListRow = dataset.getRow(i).clone();
        newDataset.AddRow(peakListRow);
        }
        }*/
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
        if (this.dataset.getType() == DatasetType.GCGCTOF) {
            //GCGC-Tof files
            switch (column) {
                case 0:
                    return selection[row];
                case 1:
                    return this.dataset.getRow(row).getID();
                case 2:
                    return this.dataset.getRow(row).getRT1();
                case 3:
                    return this.dataset.getRow(row).getRT2();
                case 4:
                    return this.dataset.getRow(row).getRTI();
                case 5:
                    return this.dataset.getRow(row).getNumFound();
                case 6:
                    return this.dataset.getRow(row).getMaxSimilarity();
                case 7:
                    return this.dataset.getRow(row).getMeanSimilarity();
                case 8:
                    return this.dataset.getRow(row).getSimilaritySTDDev();
                case 9:
                    return this.dataset.getRow(row).getName();
                case 10:
                    return this.dataset.getRow(row).getAllNames();
                case 11:
                    return this.dataset.getRow(row).getPubChemID();
                case 12:
                    return this.dataset.getRow(row).getMass();
                case 13:
                    return this.dataset.getRow(row).getDifference();
                case 14:
                    return this.dataset.getRow(row).getSpectrum();
            }
        } else if (this.dataset.getType() == DatasetType.LCMS) {
            switch (column) {
                case 0:
                    return selection[row];
                case 1:
                    return this.dataset.getRow(row).getID();
                case 2:
                    return this.dataset.getRow(row).getMZ();
                case 3:
                    return this.dataset.getRow(row).getRT();
                case 4:
                    return this.dataset.getRow(row).getName();
                case 5:
                    return this.dataset.getRow(row).getAllNames();
                case 6:
                    return this.dataset.getRow(row).getLipidClass();
                case 7:
                    return this.dataset.getRow(row).getNumFound();
                case 8:
                    return this.dataset.getRow(row).getStandard();
                case 9:
                    return this.dataset.getRow(row).getFAComposition();
                case 10:
                    return " ";
            }

        }
        return this.dataset.getRow(row).getPeak(column - this.dataset.getNumberFixColumns(), this.dataset.getNameExperiments());
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex < this.dataset.getNumberFixColumns()) {
            return LCMSColumnName.values()[columnIndex].getColumnName();
        } else {
            return this.dataset.getNameExperiments().elementAt(columnIndex - this.dataset.getNumberFixColumns());
        }
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
    @SuppressWarnings("fallthrough")
    public void setValueAt(Object aValue, int row, int column) {
        if (this.dataset.getType() == DatasetType.GCGCTOF) {
            //GCGC-Tof files
            switch (column) {
                case 0:
                    selection[row] = aValue;
                    break;
                case 1:
                    this.dataset.getRow(row).setID(((Integer) aValue).intValue());
                    break;
                case 2:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setRT1(((Double) aValue).doubleValue());
                    break;
                case 3:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setRT2(((Double) aValue).doubleValue());
                    break;
                case 4:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setRTI(((Double) aValue).doubleValue());
                    break;
                case 5:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setNumFound(((Integer) aValue).intValue());
                    break;
                case 6:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setMaxSimilarity(((Double) aValue).doubleValue());
                    break;
                case 7:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setMeanSimilarity(((Double) aValue).doubleValue());
                    break;
                case 8:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setSimilaritySTDDev(((Double) aValue).doubleValue());
                    break;
                case 9:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setName((String) aValue);
                    break;
                case 10:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setAllNames((String) aValue);
                    break;
                case 11:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setPubChemID((String) aValue);
                    break;
                case 12:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setMass(((Double) aValue).doubleValue());
                    break;
                case 13:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setDifference(((Double) aValue).doubleValue());
                    break;
                case 14:
                    ((SimplePeakListRowGCGC) this.dataset.getRow(row)).setSpectrum((String) aValue);
                    break;
                default:
                    this.dataset.getRow(row).setPeak(this.dataset.getNameExperiments().elementAt(column - this.dataset.getNumberFixColumns()), ((Double) aValue).doubleValue());

            }
        } else if (this.dataset.getType() == DatasetType.LCMS) {
            switch (column) {
                case 0:
                    selection[row] = aValue;
                    break;
                case 1:
                    this.dataset.getRow(row).setID(((Integer) aValue).intValue());
                    break;
                case 2:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setMZ(((Double) aValue).doubleValue());
                    break;
                case 3:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setRT(((Double) aValue).doubleValue());
                    break;
                case 4:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setName((String) aValue);
                    break;
                case 5:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setAllNames((String) aValue);
                    break;
                case 6:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setLipidClass(((Integer) aValue).intValue());
                    break;
                case 7:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setNumFound(((Double) aValue).doubleValue());
                    break;
                case 8:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setStandard(((Integer) aValue).intValue());
                    break;
                case 9:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setFAComposition((String) aValue);
                    break;
                case 10:
                    ((SimplePeakListRowLCMS) this.dataset.getRow(row)).setNumberAligment(((Integer) aValue).intValue());
                    break;
                default:
                    this.dataset.getRow(row).setPeak(this.dataset.getNameExperiments().elementAt(column - this.dataset.getNumberFixColumns()), ((Double) aValue).doubleValue());


            }

        }

        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    void addColumn() {
        /*    String[] oldColumns = this.columns.clone();
        this.columns = new String[oldColumns.length + 1];
        for (int i = 0; i < oldColumns.length; i++) {
        System.out.println(oldColumns[i]);
        this.columns[i] = oldColumns[i];
        }
        this.columns[oldColumns.length] = "New Column";
        this.numColumns = this.columns.length;*/

        /* this.addColumnObject(this.rows);
        this.numRows = this.rows.length;*/
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

    public void changeData(int column, int row) {
        /*if (dataset.getType() == DatasetType.LCMS) {
        SimplePeakListRowLCMS peakListRow = (SimplePeakListRowLCMS) this.dataset.getRow(row);
        try {
        switch (column) {
        case 1:
        peakListRow.setID((Integer) rows[row][column]);
        break;
        case 2:
        peakListRow.setMZ((Double) rows[row][column]);
        break;
        case 3:
        peakListRow.setRT((Double) rows[row][column]);
        break;
        case 4:
        peakListRow.setName((String) rows[row][column]);
        break;
        case 5:
        peakListRow.setLipidClass((Integer) rows[row][column]);
        break;
        case 6:
        peakListRow.setNumFound((Double) rows[row][column]);
        break;
        case 7:
        if ((Boolean) rows[row][column]) {
        peakListRow.setStandard(1);
        } else {
        peakListRow.setStandard(0);
        }
        break;
        case 8:
        peakListRow.setFAComposition((String) rows[row][column]);
        break;
        case 9:
        peakListRow.setAllNames((String) rows[row][column]);
        break;
        case 10:
        peakListRow.setNumberAlignment((Integer) rows[row][column]);
        break;
        default:
        String experimentName = this.columns[column];
        peakListRow.setPeak(experimentName, (Double) rows[row][column]);
        break;
        }
        } catch (Exception e) {
        }
        }else if(this.dataset.getType() == DatasetType.GCGCTOF){
        //GCGC changes....
        }*/
    }

    public DatasetType getType() {
        return this.dataset.getType();
    }

    public int getFixColumns() {
        return this.dataset.getNumberFixColumns();
    }
    
}
