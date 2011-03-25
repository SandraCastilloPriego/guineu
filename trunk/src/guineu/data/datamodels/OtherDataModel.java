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

import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.DatasetType;
import guineu.util.Tables.DataTableModel;
import javax.swing.table.AbstractTableModel;

public class OtherDataModel extends AbstractTableModel implements DataTableModel {

        private int numColumns;
        private SimpleBasicDataset dataset;

        public OtherDataModel(Dataset dataset) {
                this.dataset = (SimpleBasicDataset) dataset;
                numColumns = this.dataset.getNumberCols() + 1;
        }

        /**
         * @see guineu.util.Tables.DataTableModel
         */
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
                        int index = column - this.getFixColumns();
                        return (String) ((SimplePeakListRowOther) this.dataset.getRow(row)).getPeak(this.dataset.getAllColumnNames().elementAt(index));
                }
        }

        @Override
        public String getColumnName(int columnIndex) {
                if (columnIndex == 0) {
                        return "Selection";
                } else {
                        return this.dataset.getAllColumnNames().elementAt(columnIndex - this.getFixColumns());
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
                SimplePeakListRowOther peakRow = (SimplePeakListRowOther) this.dataset.getRow(row);

                if (column == 0) {
                        peakRow.setSelectionMode((Boolean) aValue);
                } else {
                        peakRow.setPeak(this.dataset.getAllColumnNames().elementAt(column - this.getFixColumns()), aValue.toString());
                }
                fireTableCellUpdated(row, column);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
                return true;
        }

        /**
         * @see guineu.util.Tables.DataTableModel
         */
        public DatasetType getType() {
                return this.dataset.getType();
        }

        /**
         * @see guineu.util.Tables.DataTableModel
         */
        public int getFixColumns() {
                return 1;
        }

        /**
         * @see guineu.util.Tables.DataTableModel
         */
        public void addColumn(String ColumnName) {
                this.dataset.addColumnName(ColumnName);
        }
}
