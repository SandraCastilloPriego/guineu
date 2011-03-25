/*
 * Copyright 2007-2011 VTT Biotechnology
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
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleExpressionDataset;
import guineu.data.impl.peaklists.SimplePeakListRowExpression;
import guineu.util.Tables.DataTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

public class DatasetExpressionDataModel extends AbstractTableModel implements DataTableModel {

    private SimpleExpressionDataset dataset;
    private int fixNumberColumns;
    private List<String> columns;

    public DatasetExpressionDataModel(Dataset dataset) {
        this.dataset = (SimpleExpressionDataset) dataset;
        this.columns = new ArrayList<String>();
        this.setParameters();
        this.writeData();
    }

    
    public void setParameters() {
        fixNumberColumns = 0;
        Vector<String> metadata = ((SimpleExpressionDataset) dataset).getMetaDataNames();
        for (String column : metadata) {
            columns.add(column);
            fixNumberColumns++;
        }
    }

    /**
     * Sets the rest of the data from the dataset object, which contains the all the rows.
     *
     */
    private void writeData() {
        PeakListRow peakListRow;
        for (int i = 0; i < dataset.getNumberRows(); i++) {
            peakListRow = this.dataset.getRow(i);
            if (peakListRow.getID() == -1) {
                peakListRow.setID(i);
            }
        }

    }

    /**
     * @see guineu.util.Tables.DataTableModel
     */
    public void removeRows() {
        for (int i = 0; i < this.dataset.getNumberRows(); i++) {
            PeakListRow row = this.dataset.getRow(i);
            if (row.isSelected()) {
                this.dataset.removeRow(row);
                this.fireTableDataChanged();
                this.removeRows();
                break;
            }
        }
    }

    public int getColumnCount() {
        return this.dataset.getNumberCols() + this.fixNumberColumns;
    }

    public int getRowCount() {
        return this.dataset.getNumberRows();
    }

    public Object getValueAt(final int row, final int column) {
        try {
            SimplePeakListRowExpression peakRow = (SimplePeakListRowExpression) this.dataset.getRow(row);

            if (column < this.fixNumberColumns) {
                Object value = peakRow.getMetaData(this.dataset.getMetaDataNames().elementAt(column));
                return value;
            }
            return peakRow.getPeak(this.dataset.getAllColumnNames().elementAt(column - this.fixNumberColumns));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex < this.fixNumberColumns) {
            return (String) this.columns.get(columnIndex).toString();
        } else {
            return this.dataset.getAllColumnNames().elementAt(columnIndex - this.fixNumberColumns);
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
        SimplePeakListRowExpression peakRow = (SimplePeakListRowExpression) this.dataset.getRow(row);
        if (column < this.fixNumberColumns) {
            peakRow.setMetaData(this.columns.get(column), aValue);
        } else {
            peakRow.setPeak(this.dataset.getAllColumnNames().elementAt(column - this.fixNumberColumns), (Double) aValue);
        }
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    /**
     * @see elmar.util.Tables.DataTableModel
     */
    public DatasetType getType() {
        return this.dataset.getType();
    }

    /**
     * @see elmar.util.Tables.DataTableModel
     */
    public int getFixColumns() {
        return this.fixNumberColumns;
    }

    /**
     * @see elmar.util.Tables.DataTableModel
     */
    public void addColumn(String columnName) {
        this.dataset.addColumnName(columnName);
    }
}
