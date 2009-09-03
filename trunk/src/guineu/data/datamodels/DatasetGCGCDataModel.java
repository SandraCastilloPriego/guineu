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

import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.table.AbstractTableModel;
import guineu.data.Dataset;

/**
 *
 * @author scsandra
 */
public class DatasetGCGCDataModel extends AbstractTableModel implements DataTableModel {

	private SimpleGCGCDataset dataset;
	protected SortingDirection isSortAsc = SortingDirection.Ascending;
	protected int sortCol = 0;
	private int fixNumberColumns = 0;

	public DatasetGCGCDataModel(Dataset dataset) {
		this.dataset = (SimpleGCGCDataset) dataset;
		fixNumberColumns = GCGCColumnName.values().length;
		this.writeData();
	}

	public void writeData() {
		PeakListRow peakListRow;
		for (int i = 0; i < dataset.getNumberRows(); i++) {
			peakListRow = this.dataset.getRow(i);
			if (peakListRow.getID() == -1) {
				peakListRow.setID(i);
			}
		}
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
		return this.dataset.getNumberCols() + this.fixNumberColumns;
	}

	public int getRowCount() {
		return this.dataset.getNumberRows();
	}

	public Object getValueAt(final int row, final int column) {
		try {
			//GCGC-Tof files
		
			switch (column) {
				case 0:
					return this.dataset.getRow(row).isSelected();
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
					return this.dataset.getRow(row).getCAS();
				case 7:
					return this.dataset.getRow(row).getMaxSimilarity();
				case 8:
					return this.dataset.getRow(row).getMeanSimilarity();
				case 9:
					return this.dataset.getRow(row).getSimilaritySTDDev();
				case 10:
					return this.dataset.getRow(row).getName();
				case 11:
					return this.dataset.getRow(row).getAllNames();
				case 12:
					return this.dataset.getRow(row).getPubChemID();
				case 13:
					return this.dataset.getRow(row).getMass();
				case 14:
					return this.dataset.getRow(row).getDifference();
				case 15:
					return this.dataset.getRow(row).getSpectrumString();
			}
			return this.dataset.getRow(row).getPeak(this.dataset.getNameExperiments().get(column - this.fixNumberColumns));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < this.fixNumberColumns) {
			if (this.dataset.getType() == DatasetType.LCMS) {
				return LCMSColumnName.values()[columnIndex].getColumnName();
			} else if (this.dataset.getType() == DatasetType.GCGCTOF) {
				return GCGCColumnName.values()[columnIndex].getColumnName();
			} else {
				return this.dataset.getNameExperiments().elementAt(columnIndex - this.fixNumberColumns);
			}
		} else {
			return this.dataset.getNameExperiments().elementAt(columnIndex - this.fixNumberColumns);
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

		double doubleValue = 0;
		int intValue = 0;
		if (aValue == null) {
			aValue = "0";
		}
		if (aValue.getClass().toString().matches(".*String.*")) {
			if (aValue.toString().equals("NA")) {
				doubleValue = 0;
				intValue = 0;
				aValue = "";
			} else {
				try {
					doubleValue = Double.parseDouble((String) aValue);
				} catch (Exception e) {
					try {
						doubleValue = (Double) this.getValueAt(row, column);
					} catch (Exception ee) {
					}
				}
				try {
					doubleValue = Double.parseDouble((String) aValue);
					intValue = (int) doubleValue;
				} catch (Exception e) {
					try {
						intValue = (Integer) this.getValueAt(row, column);
					} catch (Exception ee) {
					}
				}
			}
		} else if (aValue.getClass().toString().matches(".*Double.*")) {
			doubleValue = ((Double) aValue).doubleValue();
			intValue = (int) doubleValue;
		} else if (aValue.getClass().toString().matches(".*Integer.*")) {
			intValue = ((Integer) aValue).intValue();
			doubleValue = intValue;
		}
		//GCGC-Tof files
		switch (column) {
			case 0:
				this.dataset.getRow(row).setSelectionMode((Boolean) aValue);
				break;
			case 1:
				this.dataset.getRow(row).setID(intValue);
				break;
			case 2:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setRT1(doubleValue);
				break;
			case 3:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setRT2(doubleValue);
				break;
			case 4:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setRTI(doubleValue);
				break;
			case 5:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setNumFound(intValue);
				break;
			case 6:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setCAS((String) aValue);
				break;
			case 7:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setMaxSimilarity(doubleValue);
				break;
			case 8:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setMeanSimilarity(doubleValue);
				break;
			case 9:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setSimilaritySTDDev(doubleValue);
				break;
			case 10:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setName((String) aValue);
				break;
			case 11:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setAllNames((String) aValue);
				break;
			case 12:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setPubChemID((String) aValue);
				break;
			case 13:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setMass(doubleValue);
				break;
			case 14:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setDifference(doubleValue);
				break;
			case 15:
				((SimplePeakListRowGCGC) this.dataset.getRow(row)).setSpectrum((String) aValue);
				break;
			default:
				this.dataset.getRow(row).setPeak(this.dataset.getNameExperiments().elementAt(column - this.fixNumberColumns), doubleValue);

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
		return this.fixNumberColumns;
	}

	public void addColumn(String columnName) {
		this.dataset.AddNameExperiment(columnName);
	}
}
