/*
 * Copyright 2007-2013 VTT Biotechnology
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
import guineu.data.GCGCColumnName;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.desktop.preferences.ColumnsGCGCParameters;
import guineu.main.GuineuCore;
import guineu.util.Tables.DataTableModel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Data model for GCxGC-MS tables
 * 
 * @author scsandra
 */
public class DatasetGCGCDataModel extends AbstractTableModel implements DataTableModel {

        private SimpleGCGCDataset dataset;
        private int fixNumberColumns = 0;
        private List<GCGCColumnName> columns;
        private GCGCColumnName[] elements;
        private Color[] rowColor;

        public DatasetGCGCDataModel(Dataset dataset) {
                this.dataset = (SimpleGCGCDataset) dataset;
                rowColor = new Color[dataset.getNumberRows()];
                this.setParameters();
                this.writeData();
        }

        public Color getRowColor(int row) {
                if (row < rowColor.length) {
                        return rowColor[row];
                } else {
                        return null;
                }
        }
        
        public void addRowColor(Color[] color) {
                this.rowColor = color;
        }

        /**
         * @see guineu.data.GCGCColumnName
         * The function isColumnShown() in the enum class says whether each column has to be shown in the table or not.
         *
         */
        public void setParameters() {
                this.columns = new ArrayList<GCGCColumnName>();
                fixNumberColumns = 0;
                elements = GuineuCore.getGCGCColumnsParameters().getParameter(ColumnsGCGCParameters.GCGCdata).getValue();
                for (GCGCColumnName column : elements) {
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
                                fireTableStructureChanged();
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
                        SimplePeakListRowGCGC peakRow = (SimplePeakListRowGCGC) this.dataset.getRow(row);
                        if (column < this.fixNumberColumns) {
                                Object value = peakRow.getVar(columns.get(column).getGetFunctionName());
                                if (columns.get(column) == GCGCColumnName.DIFFERENCE && (Double) value < 0) {
                                        return 0;
                                }
                                return value;
                        }
                        return peakRow.getPeak(this.dataset.getAllColumnNames().get(column - this.fixNumberColumns));
                } catch (Exception e) {
                        return null;
                }
        }

        @Override
        public String getColumnName(int columnIndex) {
                if (columnIndex < this.fixNumberColumns) {
                        return (String) this.columns.get(columnIndex).toString();
                } else {
                        return this.dataset.getAllColumnNames().get(columnIndex - this.fixNumberColumns);
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
                SimplePeakListRowGCGC peakRow = (SimplePeakListRowGCGC) this.dataset.getRow(row);
                if (column < this.fixNumberColumns) {
                        peakRow.setVar(this.columns.get(column).getSetFunctionName(), aValue);
                } else {
                        peakRow.setPeak(this.dataset.getAllColumnNames().get(column - this.fixNumberColumns), (Double) aValue);
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
                return this.fixNumberColumns;
        }

        /**
         * @see guineu.util.Tables.DataTableModel
         */
        public void addColumn(String columnName) {
                this.dataset.addColumnName(columnName);
        }

        @Override
        public Color getCellColor(int row, int column) {
                return this.dataset.getCellColor(row, column);
        }
}
