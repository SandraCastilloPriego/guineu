/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.database.openDatasetDB;

import guineu.database.ask.DBask;
import guineu.database.ask.DataBase;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;


//import src.graphics.DB.MysqlAsk;


/**
 * @author scsandra
 *
 */
public class DataModelDataset extends AbstractTableModel{
	
	String columns[] = {"ID", "Name of dataset", 
            "Type", "Author", "Date"};
	Object rows[][];	
	int numColumns;
	int numRows;
	
	public DataModelDataset(){		
		DataBase db = new DBask();	
		this.rows = db.get_dataset();
		this.numColumns = this.columns.length;
		this.numRows = this.rows.length;
	}
	
	public int getColumnCount() {
		return numColumns;
	}

	public int getRowCount() {
		return numRows;
	}

	public Object getValueAt (final int row, final int column) {
	    return rows[row][column];
	}

    @Override
	public String getColumnName (final int columnIndex) {
	    return columns[columnIndex];
	}

    @Override
	public void setValueAt (final Object aValue, 
	    final int row, final int column) {	    
		rows[row][column] = aValue;
		fireTableCellUpdated (row, column);
	}

    @Override
	public boolean isCellEditable(final int row, final int column) {
		return false;
	}

	public void removeRow(final int rowIndex){           
		final Vector<Object[]> bt = new Vector<Object[]>();
		for(int i = 0; i < rows.length; i++){
			bt.addElement(rows[i]);
		}	
		bt.removeElementAt(rowIndex);	
		numRows--;
		rows = new String[bt.size()][this.columns.length];
		for(int i = 0; i < this.rows.length; i++){
			String[] st = new String[this.columns.length];
			st = (String[])bt.elementAt(i);
			for(int j = 0; j < this.columns.length; j++){
				rows[i][j] = st[j];
			}
		}		
	}	
}
