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
import javax.swing.table.AbstractTableModel;
import guineu.data.Dataset;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.modules.configuration.tables.GCGC.GCGCColumnsViewParameters;
import guineu.util.CollectionUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class DatasetGCGCDataModel extends AbstractTableModel implements DataTableModel {

	private SimpleGCGCDataset dataset;
	private int fixNumberColumns = 0;
	private GCGCColumnsViewParameters GCGCViewParameters;
	private List<GCGCColumnName> columns;
	private GCGCColumnName[] elements;

	public DatasetGCGCDataModel(Dataset dataset) {
		this.dataset = (SimpleGCGCDataset) dataset;
		this.setParameters();
		this.writeData();
	}

	public void setParameters() {
		this.columns = new ArrayList<GCGCColumnName>();
		fixNumberColumns = 0;
		this.GCGCViewParameters = (GCGCColumnsViewParameters) ((DesktopParameters) GuineuCore.getDesktop().getParameterSet()).getViewGCGCParameters();
		Object elementsObjects[] = (Object[]) GCGCViewParameters.getParameterValue(GCGCColumnsViewParameters.columnSelection);
		elements = CollectionUtils.changeArrayType(elementsObjects,
				GCGCColumnName.class);
		for (GCGCColumnName column : elements) {
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
			SimplePeakListRowGCGC peakRow = (SimplePeakListRowGCGC) this.dataset.getRow(row);
			if (column < this.fixNumberColumns) {
				Object value = peakRow.getVar(columns.get(column).getGetFunctionName());
				if (columns.get(column) == GCGCColumnName.DIFFERENCE && (Double) value < 0) {
					return 0;
				}
				return value;
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
	public void setValueAt(Object aValue, int row, int column) {
		SimplePeakListRowGCGC peakRow = (SimplePeakListRowGCGC) this.dataset.getRow(row);
		if (column < this.fixNumberColumns) {
			peakRow.setVar(this.columns.get(column).getSetFunctionName(), aValue);
		} else {
			peakRow.setPeak(this.dataset.getNameExperiments().elementAt(column - this.fixNumberColumns), (Double) aValue);
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
