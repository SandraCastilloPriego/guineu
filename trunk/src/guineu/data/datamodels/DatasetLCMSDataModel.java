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

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.modules.configuration.tables.LCMS.LCMSColumnsViewParameters;
import guineu.util.CollectionUtils;
import guineu.util.Tables.DataTableModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class DatasetLCMSDataModel extends AbstractTableModel implements DataTableModel {

	private SimpleLCMSDataset dataset;
	private int fixNumberColumns;
	private LCMSColumnsViewParameters LCMSViewParameters;
	private List<LCMSColumnName> columns;
	private LCMSColumnName[] elements;

	public DatasetLCMSDataModel(Dataset dataset) {
		this.dataset = (SimpleLCMSDataset) dataset;		
		this.setParameters();
		this.writeData();
	}

	public void setParameters() {
		this.columns = new ArrayList<LCMSColumnName>();
		fixNumberColumns = 0;
		this.LCMSViewParameters = (LCMSColumnsViewParameters) ((DesktopParameters) GuineuCore.getDesktop().getParameterSet()).getViewLCMSParameters();
		Object elementsObjects[] = (Object[]) LCMSViewParameters.getParameterValue(LCMSColumnsViewParameters.columnSelection);
		elements = CollectionUtils.changeArrayType(elementsObjects,
				LCMSColumnName.class);
		for (LCMSColumnName column : elements) {
			if (column.isCommon()) {
				columns.add(column);
				fixNumberColumns++;
			}
		}
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
			SimplePeakListRowLCMS peakRow = (SimplePeakListRowLCMS) this.dataset.getRow(row);

			if (column < this.fixNumberColumns) {
				switch (columns.get(column)) {
					case SELECTION:
						return peakRow.isSelected();
					case ID:
						return peakRow.getID();
					case MZ:
						return peakRow.getMZ();
					case RT:
						return peakRow.getRT();
					case NAME:
						return peakRow.getName();
					case ALLNAMES:
						return peakRow.getAllNames();
					case PUBCHEM:
						return peakRow.getPubChemID();
					case VTT:
						return peakRow.getVTTID();
					case ALLVTT:
						return peakRow.getAllVTTID();
					case LIPIDCLASS:
						return peakRow.getMolClass();
					case NFOUND:
						return peakRow.getNumFound();
					case STANDARD:
						if (peakRow.getStandard() == 1) {
							return new Boolean(true);
						} else {
							return new Boolean(false);
						}
					case FA:
						return peakRow.getFAComposition();
					case ALIGNMENT:
						return peakRow.getNumberAlignment();
				}
			}
			return peakRow.getPeak(this.dataset.getNameExperiments().elementAt(column - this.fixNumberColumns));
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex < this.fixNumberColumns) {
			return (String) this.columns.get(columnIndex).toString();
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
	@SuppressWarnings("fallthrough")
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

		SimplePeakListRowLCMS peakRow = (SimplePeakListRowLCMS) this.dataset.getRow(row);

		if (column < columns.size()) {
			switch (columns.get(column)) {
				case SELECTION:
					peakRow.setSelectionMode((Boolean) aValue);
					break;
				case ID:
					peakRow.setID(intValue);
					break;
				case MZ:
					peakRow.setMZ(doubleValue);
					break;
				case RT:
					peakRow.setRT(doubleValue);
					break;
				case NAME:
					peakRow.setName((String) aValue);
					break;
				case ALLNAMES:
					peakRow.setAllNames((String) aValue);
					break;
				case PUBCHEM:
					peakRow.setPubChemID((String) aValue);
					break;
				case VTT:
					peakRow.setVTTD((String) aValue);
					break;
				case ALLVTT:
					peakRow.setAllVTTD((String) aValue);
					break;
				case LIPIDCLASS:
					peakRow.setLipidClass((String) aValue);
					break;
				case NFOUND:
					peakRow.setNumFound(doubleValue);
					break;
				case STANDARD:
					if ((Boolean) aValue) {
						peakRow.setStandard(1);
					} else {
						peakRow.setStandard(0);
					}
					break;
				case FA:
					peakRow.setFAComposition((String) aValue);
					break;
				case ALIGNMENT:
					peakRow.setNumberAligment(intValue);
					break;
			}

		} else {
			peakRow.setPeak(this.dataset.getNameExperiments().elementAt(column - this.fixNumberColumns), doubleValue);
		}


		fireTableCellUpdated(row, column);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return true;
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
