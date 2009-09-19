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
import guineu.util.Tables.DataTableModel;
import javax.swing.table.AbstractTableModel;

public class DatasetLCMSDataModel extends AbstractTableModel implements DataTableModel {

    private SimpleLCMSDataset dataset;   
    private int fixNumberColumns = 0;

    public DatasetLCMSDataModel(Dataset dataset) {
        this.dataset = (SimpleLCMSDataset) dataset;
        fixNumberColumns = LCMSColumnName.values().length;
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
            SimplePeakListRowLCMS peakRow = (SimplePeakListRowLCMS) this.dataset.getRow(row);

            //GCGC-Tof files
            switch (column) {
                case 0:
                    return peakRow.isSelected();
                case 1:
                    return peakRow.getID();
                case 2:
                    return peakRow.getMZ();
                case 3:
                    return peakRow.getRT();
                case 4:
                    return peakRow.getName();
                case 5:
                    return peakRow.getAllNames();
                case 6:
                    return peakRow.getPubChemID();
                case 7:
                    return peakRow.getVTTID();
                case 8:
                    return peakRow.getAllVTTID();
                case 9:
                    return peakRow.getMolClass();
                case 10:
                    return peakRow.getNumFound();
                case 11:
                    if (peakRow.getStandard() == 1) {
                        return new Boolean(true);
                    } else {
                        return new Boolean(false);
                    }
                case 12:
                    return peakRow.getFAComposition();
                case 13:
                    return peakRow.getNumberAlignment();
            }
            return peakRow.getPeak(column - this.fixNumberColumns, this.dataset.getNameExperiments());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex < this.fixNumberColumns) {
            if (this.dataset.getType() == DatasetType.LCMS) {
                return LCMSColumnName.values()[columnIndex].getColumnName();
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

        switch (column) {
            case 0:
                peakRow.setSelectionMode((Boolean) aValue);
                break;
            case 1:
                peakRow.setID(intValue);
                break;
            case 2:
                peakRow.setMZ(doubleValue);
                break;
            case 3:
                peakRow.setRT(doubleValue);
                break;
            case 4:
                peakRow.setName((String) aValue);
                break;
            case 5:
                peakRow.setAllNames((String) aValue);
                break;
            case 6:
                peakRow.setPubChemID((String) aValue);
                break;
            case 7:
                peakRow.setVTTD((String) aValue);
                break;
            case 8:
                peakRow.setAllVTTD((String) aValue);
                break;
            case 9:
                peakRow.setLipidClass((String) aValue);
                break;
            case 10:
                peakRow.setNumFound(doubleValue);
                break;
            case 11:
                if ((Boolean) aValue) {
                    peakRow.setStandard(1);
                } else {
                    peakRow.setStandard(0);
                }
                break;
            case 12:
                peakRow.setFAComposition((String) aValue);
                break;
            case 13:
                peakRow.setNumberAligment(intValue);
                break;
            default:
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
