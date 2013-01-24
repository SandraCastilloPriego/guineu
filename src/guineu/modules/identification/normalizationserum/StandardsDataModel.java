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
package guineu.modules.identification.normalizationserum;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author scsandra
 */
public class StandardsDataModel extends AbstractTableModel {

        private String[] columns;
        private List<String> name;
        private List<Double> value;//content all data
        private int numColumns;
        private int numRows;
        List<StandardUmol> standards;

        public StandardsDataModel(List<StandardUmol> standards) {

                this.standards = standards;

                columns = new String[2];
                columns[0] = "Standard Name";
                columns[1] = "Amount";
                name = new ArrayList<String>();
                value = new ArrayList<Double>();
                for (StandardUmol std : standards) {
                        name.add(std.getName());
                        value.add(std.getRealAmount());
                }
                numRows = name.size();
                numColumns = 2;
        }

        public int getRowCount() {
                return numRows;
        }

        public int getColumnCount() {
                return numColumns;
        }

        @Override
        public String getColumnName(int columnIndex) {
                return columns[columnIndex];
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
                if (column == 0) {
                        name.add(row, (String) aValue);
                } else {
                        value.add(row, (Double) aValue);
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
        public boolean isCellEditable(int row, int column) {
                return true;
        }

        public Object getValueAt(int row, int column) {
                if (column == 0) {
                        return name.get(row);
                } else {
                        return value.get(row);
                }
        }

        public void fillStandards() {
                for (int i = 0; i < this.name.size(); i++) {
                        this.standards.get(i).setRealAmount(value.get(i));
                }
        }

        public void resetStandards() {
                for (int i = 0; i < this.value.size(); i++) {
                        this.value.set(i, 0.0);
                }
                this.fireTableDataChanged();
        }
}
