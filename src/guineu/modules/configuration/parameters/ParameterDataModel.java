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
package guineu.modules.configuration.parameters;

import guineu.data.Dataset;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ParameterDataModel extends AbstractTableModel {

        /**
         * All data in the main windows. It can be LCMS or GCGC-Tof data.
         */
        private static final long serialVersionUID = 1L;
        private List<String> columns;
        private List<String> parameters;
        private List<String[]> rows; //content all data
        private int numColumns;
        private int numRows;
        private JTable table;

        public ParameterDataModel(Dataset dataset, JTable table) {
                this.table = table;

                // Column names
                columns = new ArrayList<String>();
                columns.add("Samples");
                parameters = dataset.getParametersName();
                for (String parameter : parameters) {
                        columns.add(parameter);
                }
                numColumns = columns.size();


                // First column with the name of the samples
                String[] col = dataset.getAllColumnNames().toArray(new String[0]);
                rows = new ArrayList<String[]>();
                rows.add(col);
                numRows = dataset.getAllColumnNames().size();

                // Parameter columns
                for (int i = 0; i < dataset.getParametersName().size(); i++) {
                        String parameterName = dataset.getParametersName().get(i);
                        col = new String[dataset.getAllColumnNames().size()];

                        for (int e = 0; e < dataset.getAllColumnNames().size(); e++) {
                                String experimentName = dataset.getAllColumnNames().get(e);
                                col[e] = dataset.getParametersValue(experimentName, parameterName);
                        }
                        rows.add(col);
                }
        }

        public void addColumn(String column) {
                this.columns.add(column);
                this.parameters.add(column);
                String[] newCol = new String[numRows];
                rows.add(newCol);
                numColumns++;
        }

        public int getColumnCount() {
                return numColumns;
        }

        public int getRowCount() {
                return numRows;
        }

        public String getValueAt(final int row, final int column) {
                try {
                        return rows.get(column)[row];
                } catch (Exception e) {
                        return "";
                }
        }

        @Override
        public String getColumnName(int columnIndex) {
                try {
                        return columns.get(columnIndex);
                } catch (Exception e) {
                        return null;
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
                try {
                        for (int trow : table.getSelectedRows()) {
                                for (int tcolumn : table.getSelectedColumns()) {
                                        rows.get(tcolumn)[trow] = aValue.toString();
                                }
                        }
                        rows.get(column)[row] = aValue.toString();
                        fireTableCellUpdated(row, column);
                } catch (Exception e) {
                }
        }

        public void setValueAt(Object aValue, int row, int column, boolean fillAllCells) {
                try {
                        if (fillAllCells) {
                                this.setValueAt(aValue, row, column);
                        } else {
                                rows.get(column)[row] = aValue.toString();
                                fireTableCellUpdated(row, column);
                        }
                } catch (Exception e) {
                }
        }

        @Override
        public boolean isCellEditable(int row, int column) {
                return true;
        }

        public void addColumnObject(Object[][] o) {
                Object[][] oldRows = o.clone();
                o = new Object[oldRows.length][oldRows[0].length + 1];
                for (int i = 0; i < oldRows.length; i++) {
                        System.arraycopy(oldRows[i], 0, o[i], 0, oldRows[0].length);
                        o[i][oldRows[0].length] = " ";
                }
        }

        public void addColumnObject(int[][] o) {
                int[][] oldRows = o.clone();
                o = new int[oldRows.length][oldRows[0].length + 1];
                for (int i = 0; i < oldRows.length; i++) {
                        System.arraycopy(oldRows[i], 0, o[i], 0, oldRows[0].length);
                        o[i][oldRows[0].length] = 0;
                }
        }

        public void setColumnCount(int count) {
                this.numColumns = count;
        }

        public void addParameters(Dataset dataset) {
                for (int i = 1; i < this.getColumnCount(); i++) {
                        String parameterName = this.parameters.get(i - 1);
                        for (int e = 0; e < this.rows.get(i).length; e++) {
                                String experimentName = this.rows.get(0)[e];
                                String parameterValue = this.rows.get(i)[e];
                                dataset.addParameterValue(experimentName, parameterName, parameterValue);
                        }
                }
        }
        
}
