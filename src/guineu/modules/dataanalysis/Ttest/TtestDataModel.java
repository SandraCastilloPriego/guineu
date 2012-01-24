/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.dataanalysis.Ttest;

import java.util.*;
import javax.swing.table.AbstractTableModel;

public class TtestDataModel extends AbstractTableModel {

    /**
     * All data in the main windows. It can be LCMS or GCGC-Tof data.
     */
    private static final long serialVersionUID = 1L;
    private String columns;
    private Vector<String> rows; //content all data   
    private int numColumns;
    private int numRows;

    public TtestDataModel(String Name) {
        rows = new Vector<String>();
        columns = Name;
        numColumns = 1;
        numRows = rows.size();
    }

    public Vector<String> getRows() {
        return rows;
    }

    public void addRows(String row) {
        this.rows.addElement(row);
        numRows = rows.size();
        this.fireTableDataChanged();
    }

    public void removeRow(int rowIndex) {
        this.rows.setElementAt("1", rowIndex);
    }

    public int getColumnCount() {
        return numColumns;
    }

    public int getRowCount() {
        return numRows;
    }

    public String getValueAt(final int row, final int column) {
        return rows.elementAt(row);
    }

    @Override
    public String getColumnName(int columnIndex) {
        String str = columns;
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
        rows.setElementAt(aValue.toString(), row);
    //fireTableCellUpdated (row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
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

    public void reconstruct() {
        for (int i = 0; i < this.rows.size(); i++) {
            if (this.rows.elementAt(i).matches("1")) {
                this.rows.removeElementAt(i);
                i--;
            }
        }
        this.numRows = this.rows.size();
        this.fireTableDataChanged();
    }
}
