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
package guineu.modules.file.openOtherFiles;

import guineu.data.impl.SimpleDataset_concatenate;
import guineu.data.impl.SimplePeakListRowConcatenate;
import guineu.data.Dataset;
import guineu.data.impl.DatasetType;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.table.AbstractTableModel;




public class OtherDataModel extends AbstractTableModel implements DataTableModel {

	private int numColumns;
	private int numRows;	
	private SimpleDataset_concatenate dataset;
	protected SortingDirection isSortAsc = SortingDirection.Ascending;
	protected int sortCol = 0;

	public OtherDataModel(Dataset dataset) {
		this.dataset = (SimpleDataset_concatenate) dataset;
		numColumns = this.dataset.getNumberCols();
		numRows = this.dataset.getNumberRows();
	}
	
	
	
	public Dataset removeRows() {
		SimpleDataset_concatenate newDataset = new SimpleDataset_concatenate(this.dataset.getDatasetName());
		/* for (int i = 0; i < rows.length; i++) {
		if (!(Boolean) rows[i][0]) {
		PeakListRow_concatenate peakListRow = (PeakListRow_concatenate) dataset.getRow(i).clone();
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
		String peak = (String) ((SimplePeakListRowConcatenate)this.dataset.getRow(row)).getPeak(column, this.dataset.getNameExperiments());
		if(peak != null){
			return peak;
		}else{
			return "NA";
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		return this.dataset.getNameExperiments().elementAt(columnIndex);
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
		((SimplePeakListRowConcatenate) this.dataset.getRow(row)).setPeak(this.dataset.getNameExperiments().elementAt(column), aValue.toString());
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

	public void changeData(int column, int row) {
		/* if (dataset.getType() != DatasetType.LCMS || dataset.getType() != DatasetType.GCGCTOF ) {
		SimplePeakListRowConcatenate peakListRow = (SimplePeakListRowConcatenate) this.dataset.getRow(row);
		try {
		String experimentName = this.columns[column];
		peakListRow.setPeak(experimentName, (String)rows[row][column]);
		} catch (Exception e) {
		e.printStackTrace();
		}
		} */
	}

	public DatasetType getType() {
		return this.dataset.getType();
	}
}
