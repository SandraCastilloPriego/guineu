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

import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.table.AbstractTableModel;

public class OtherDataModel extends AbstractTableModel implements DataTableModel {

    private int numColumns;
    private SimpleOtherDataset dataset;
    protected SortingDirection isSortAsc = SortingDirection.Ascending;
    protected int sortCol = 0;

    public OtherDataModel(Dataset dataset) {
        this.dataset = (SimpleOtherDataset) dataset;
        numColumns = this.dataset.getNumberCols()+1;
    }

    public void removeRows() {
        for (int i = 0; i < this.dataset.getNumberRows(); i++) {
            PeakListRow row = this.dataset.getRow(i);
            if (row.isSelected()) {
                this.dataset.removeRow(row);
                fireTableStructureChanged();
                this.removeRows();
                break;
            }
        }
    }

    public int getColumnCount() {
        return numColumns;
    }

    public int getRowCount() {
        return this.dataset.getNumberRows();
    }

    public Object getValueAt(final int row, final int column) {
        if (column == 0) {
            return (Boolean) this.dataset.getRow(row).isSelected();
        } else {
            return (String) ((SimplePeakListRowOther) this.dataset.getRow(row)).getPeak(column - this.getFixColumns(), this.dataset.getNameExperiments());
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) {
            return "Selection";
        } else {
            return this.dataset.getNameExperiments().elementAt(columnIndex - this.getFixColumns());
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
    public void setValueAt(Object aValue, int row, int column) {
        if (column == 0) {
            this.dataset.getRow(row).setSelectionMode((Boolean) aValue);
        } else {
            ((SimplePeakListRowOther) this.dataset.getRow(row)).setPeak(this.dataset.getNameExperiments().elementAt(column - this.getFixColumns()), aValue.toString());
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
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

    public DatasetType getType() {
        return this.dataset.getType();
    }

    public int getFixColumns() {
        return 1;
    }

    public void addColumn(String ColumnName) {
        this.dataset.AddNameExperiment(ColumnName);
    }
}
