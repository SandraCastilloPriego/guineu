/*
Copyright 2006-2007 VTT Biotechnology
This file is part of MYLLY.
MYLLY is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
MYLLY is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with MYLLY; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package guineu.modules.mylly.gcgcaligner.gui.tables;

import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentSorterFactory;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentSorterFactory.SORT_MODE;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.Pair;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

public class ResultTableModel extends AbstractTableModel {

    private static enum SORT_STATE {

        DESCENDING {

            public SORT_STATE next() {
                return SORT_STATE.ASCENDING;
            }
        },
        ASCENDING {

            public SORT_STATE next() {
                return SORT_STATE.DESCENDING;
            }
        },
        NONE {

            public SORT_STATE next() {
                return SORT_STATE.ASCENDING;
            }
        };

        public abstract SORT_STATE next();
    }
    final static private String ROWID_COL_NAME = "#";
    final static private String RT1_COL_NAME = "RT 1";
    final static private String RT2_COL_NAME = "RT 2";
    final static private String RTI_COL_NAME = "RI";
    final static private String QUANT_MASS = "Quant Mass";
    final static private String COUNT_COL_NAME = "Num Found";
    final static private String DIFF_COL_NAME = "Diff to ideal peak";
    final static private String CAS_COL_NAME = "CAS";
    final static private String NAMES_COL_NAME = "Names";
    final static private String MAXSIM_COL_NAME = "Max similarity";
    final static private String MEANSIM_COL_NAME = "Mean similarity";
    final static private String STDSIM_COL_NAME = "Similarity std dev";
    private static final String SELECTED_COL_NAME = "Selected?";
    private List<AlignmentRow> alignmentRows;
    private boolean useRTI;
    private boolean hasDifferences;
    private MouseListener ml;
    private JTableHeader currentHeader;
    private TableCellRenderer lastRenderer;
    private Map<Integer, Method> colIxToAccessor;
    private Map<Integer, String> colIxToColName;
    /* model to view is here in case I get enough time and willpower
    to move this sorter in a class of its own so that it lives a 
    life between actual TableModel and JTable. Now the middle layer
    and JTable layer both reside in this class.
     */
    private Map<Integer, Integer> modelToView;
    private Map<Integer, Integer> viewToModel;
    private Map<Integer, SORT_STATE> columnSorted;
    private Map<Integer, SORT_MODE> colIxToSorter;
    private int columnCount;
    private int areaColCount;
    private int infoColCount;
    private int rowIdIx;
    private int casIx;
    private int nameIx;
    private int lastSortIndex;
    private int selectedIx;
    private int diffIx = -1;
    private ArrayList<Boolean> selectedList;

    public ResultTableModel(Alignment alignment) {
        alignmentRows = alignment.getAlignment();
        /* AlignmentParameters params = alignment.getParameters();
        if(params.useConcentration()){
        setGCGCDataConcentration();
        }*/
        for (AlignmentRow row : alignmentRows) {
            if (row.getMeanRTI() != 0.0) {
                useRTI = true;
                break;
            }
        }
        hasDifferences = alignment.containsMainPeaks();
        generateMappings(alignment);
    }

    public void setGCGCDataConcentration() {
        for (AlignmentRow row : alignmentRows) {
            for (GCGCDatum data : row) {
                if (data.getConcentration() > 0) {
                    data.setUseConcentration(true);
                } else {
                    data.setUseConcentration(false);
                }
            }
        }

    }

    private void generateMappings(Alignment alignment) {
        int colIx = 0;

        selectedList = new ArrayList<Boolean>(alignment.rowCount());
        for (int i = 0; i < alignment.rowCount(); i++) {
            selectedList.add(Boolean.FALSE);
        }
        colIxToAccessor = new HashMap<Integer, Method>();
        colIxToColName = new HashMap<Integer, String>();
        colIxToSorter = new HashMap<Integer, SORT_MODE>();
        try {
//			colIxToAccessor.put(colIx, this.getClass().getMethod("getRowId", int.class));
            colIxToColName.put(colIx, ROWID_COL_NAME);
            rowIdIx = colIx;
            colIx++;

            colIxToColName.put(colIx, SELECTED_COL_NAME);
            selectedIx = colIx++;

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getMeanRT1", (Class[]) null));
            colIxToColName.put(colIx, RT1_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.rt1);
            colIx++;

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getMeanRT2", (Class[]) null));
            colIxToColName.put(colIx, RT2_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.rt2);
            colIx++;

            if (useRTI) {
                colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getMeanRTI", (Class[]) null));
                colIxToColName.put(colIx, RTI_COL_NAME);
                colIxToSorter.put(colIx, SORT_MODE.rti);
                colIx++;
            }

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getQuantMass", (Class[]) null));
            colIxToColName.put(colIx, QUANT_MASS);
            colIxToSorter.put(colIx, SORT_MODE.quantMass);
            colIx++;

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("nonNullPeakCount", (Class[]) null));
            colIxToColName.put(colIx, COUNT_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.peaks);
            colIx++;

            if (hasDifferences) {
                colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getDistValue", (Class[]) null));
                colIxToColName.put(colIx, DIFF_COL_NAME);
                colIxToSorter.put(colIx, SORT_MODE.diffToIdeal);
                diffIx = colIx++;
            }

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getCAS", (Class[]) null));
            colIxToColName.put(colIx, CAS_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.name);
            casIx = colIx;
            colIx++;

//			colIxToAccessor.put(colIx, this.getClass().getMethod("getNames", AlignmentRow.class));
            colIxToColName.put(colIx, NAMES_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.name);
            nameIx = colIx;
            colIx++;

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getMaxSimilarity", (Class[]) null));
            colIxToColName.put(colIx, MAXSIM_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.maxSimilarity);
            colIx++;

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getMeanSimilarity", (Class[]) null));
            colIxToColName.put(colIx, MEANSIM_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.meanSimilarity);
            colIx++;

            colIxToAccessor.put(colIx, AlignmentRow.class.getMethod("getSimilarityStdDev", (Class[]) null));
            colIxToColName.put(colIx, STDSIM_COL_NAME);
            colIxToSorter.put(colIx, SORT_MODE.stdSimilarity);
            colIx++;

            if (getRowCount() > 0) {
                areaColCount = alignmentRows.get(0).length();
            } else {
                areaColCount = 0;
            }
            infoColCount = colIxToAccessor.size() + 3; //One for row number, one for names, one for selected
            columnCount = infoColCount + areaColCount;
            String[] areaColNames = alignment.getColumnNames();
            for (int i = 0; i < areaColCount; i++) {
                colIxToColName.put(i + infoColCount, areaColNames[i]);
            }

        } catch (SecurityException e) {
            //Should not happen in this use
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            //We've mistyped the method names
            System.err.printf("No method found\n");
            e.printStackTrace();
        }

        //Initialize the sorting properties
        modelToView = new HashMap<Integer, Integer>();
        viewToModel = new HashMap<Integer, Integer>();
        columnSorted = new HashMap<Integer, SORT_STATE>();
        for (int i = 0; i < getRowCount(); i++) {
            modelToView.put(i, i);
            viewToModel.put(i, i);
        }
        for (int i = 0; i < getColumnCount(); i++) {
            columnSorted.put(i, SORT_STATE.NONE);
        }
        ml = new HeaderListener();
    }

    public int getRowId(int row) {
        return row;
    }

    public String getNames(AlignmentRow row) {
        StringBuilder sb = new StringBuilder();
        String[] names = row.getNames();
        if (names.length > 0) {
            for (int i = 0; i < names.length; i++) {
                sb.append(names[i]);
                if (i != names.length - 1) {
                    sb.append(" || ");
                }
            }
        } else {
            sb.append(AlignmentRow.DEFAULT_NAME);
        }
        return sb.toString();
    }

    public int getColumnCount() {
        return columnCount;
    }

    public Class<?> getColumnClass(int ix) {
        if (ix == rowIdIx) {
            return int.class;
        } else if (ix == casIx || ix == nameIx) {
            return String.class;
        } else if (ix == selectedIx) {
            return Boolean.class;
        } else {
            Method m = colIxToAccessor.get(ix);
            if (m != null) {
                return m.getReturnType();
            } else {
                return double.class; //We are talking about areas here
            }
        }
    }

    public String getColumnName(int ix) {
        return colIxToColName.get(ix);
    }

    public int getRowCount() {
        return alignmentRows.size();
    }

    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == selectedIx) {
            int ix = viewToModel.get(rowIndex);
            selectedList.set(ix, (Boolean) aValue);
//    		fireTableCellUpdated(rowIndex, columnIndex);
        //FIXME wrong graphics when updating boolean values
        }
    }

    public boolean isCellEditable(int row, int col) {
        return col == selectedIx;
    }

    public List<AlignmentRow> getSelectedAlignmentRows() {
        List<AlignmentRow> rows = new ArrayList<AlignmentRow>();
        for (int i = 0; i < alignmentRows.size(); i++) {
            if (selectedList.get(i)) {
                rows.add(alignmentRows.get(i));
            }
        }
        return rows;
    }

    public List<AlignmentRow> getQuantMassAlignmentRows() {
        List<AlignmentRow> rows = new ArrayList<AlignmentRow>();
        for (int i = 0; i < alignmentRows.size(); i++) {
            if (((Double) getValueAt(i, 5)).doubleValue() > -1) {
                rows.add(alignmentRows.get(i));
            }
        }
        return rows;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        //omien metodit akksessorit kusevat, arealle ei aksessoreja

        Object result = "FAILURE";
        int modelRowIx = viewToModel.get(rowIndex);

        Method m = colIxToAccessor.get(columnIndex);
        if (m != null) {
            try {
                result = m.invoke(alignmentRows.get(modelRowIx), new Object[0]);
            } catch (InvocationTargetException e) {

            } catch (IllegalArgumentException e) {
            //Should not happen, if it happens, mappings are not up-to-date
            } catch (IllegalAccessException e) {
            //Should not happen, if it happens, mappings are not up-to-date
            }
        } else {
            if (columnIndex == rowIdIx) {
                result = rowIndex;
            } else if (columnIndex == nameIx) {
                result = getNames(alignmentRows.get(modelRowIx));
            } else if (columnIndex == diffIx) {
                assert (false) : "Should never be reached!";
            } else if (columnIndex == selectedIx) {
                result = selectedList.get(modelRowIx);
            } else {
                //We want to get the area or concentration                              
                GCGCDatum selected = alignmentRows.get(modelRowIx).getDatum(columnIndex - infoColCount);
                if (selected.useConcentration()) {
                    result = selected == null ? 0.0 : selected.getConcentration();
                } else {
                    result = selected == null ? 0.0 : selected.getArea();
                }
            }
        }
        return result;
    }

    private class HeaderListener extends MouseAdapter {

        public void mouseClicked(MouseEvent e) {

            if (e.getSource() == currentHeader) {
                JTableHeader header = (JTableHeader) e.getSource();
                int columnIndex = header.getColumnModel().getColumnIndexAtX(e.getX());
                sort(columnIndex);
            }

        }
    }

    public synchronized void setHeader(JTableHeader header) {
        if (currentHeader != null) {
            currentHeader.removeMouseListener(ml);
            currentHeader.setDefaultRenderer(lastRenderer);
        }
        currentHeader = header;
        if (header != null) {
            lastRenderer = header.getDefaultRenderer();
            header.setDefaultRenderer(getHeaderRenderer(lastRenderer));
            header.addMouseListener(ml);
        }
    }

    private List<Pair<AlignmentRow, Integer>> encapsulateRows() {
        List<Pair<AlignmentRow, Integer>> rowsEncapsulated = new ArrayList<Pair<AlignmentRow, Integer>>(alignmentRows.size());
        for (int i = 0; i < alignmentRows.size(); i++) {
            Integer modelIx = viewToModel.get(i);
            Pair<AlignmentRow, Integer> curRow = new Pair<AlignmentRow, Integer>(alignmentRows.get(modelIx), i);
            rowsEncapsulated.add(curRow);
        }
        return rowsEncapsulated;
    }

    private Comparator<Pair<AlignmentRow, Integer>> encapsulateComparator(final Comparator<AlignmentRow> comp) {
        return new Comparator<Pair<AlignmentRow, Integer>>() {

            public int compare(Pair<AlignmentRow, Integer> o1,
                    Pair<AlignmentRow, Integer> o2) {
                return comp.compare(o1.getFirst(), o2.getFirst());
            }
        };
    }

    /**
     * Sort view to model based on given column index. If column has no
     * sorting mode associated, does not do anything.
     * @param columnIndex index of the column based on which to sort.
     */
    private synchronized void sort(int columnIndex) {
        SORT_STATE sortingDirection = columnSorted.get(columnIndex).next();
        SORT_MODE sortMode = colIxToSorter.get(columnIndex);
        if (sortMode != null) {
            HashMap<Integer, Integer> updatedViewToModel = new HashMap<Integer, Integer>(viewToModel.size());
            HashMap<Integer, Integer> updatedModelToView = new HashMap<Integer, Integer>(modelToView.size());

            if (lastSortIndex == columnIndex) {
                reverseMaps(updatedViewToModel, updatedModelToView);
            } else {
                lastSortIndex = columnIndex;
                Comparator<AlignmentRow> sorterForColumn = AlignmentSorterFactory.getComparator(sortMode, sortingDirection == SORT_STATE.ASCENDING);
                List<Pair<AlignmentRow, Integer>> rows = encapsulateRows();
                Comparator<Pair<AlignmentRow, Integer>> encapsulatedComp = encapsulateComparator(sorterForColumn);
                java.util.Collections.sort(rows, encapsulatedComp);

                for (int i = 0; i < rows.size(); i++) {
                    int originalIndex = viewToModel.get(rows.get(i).getSecond());
                    updatedViewToModel.put(i, originalIndex);
                    updatedModelToView.put(originalIndex, i);
                }
            }
            viewToModel = updatedViewToModel;
            modelToView = updatedModelToView;


            SORT_STATE currentSortedState = sortingDirection;
            columnSorted.put(columnIndex, currentSortedState);
            fireTableDataChanged();
            if (currentHeader != null) {
                currentHeader.repaint();
            }
        }
    }

    private void reverseMaps(Map<Integer, Integer> updatedViewToModel, Map<Integer, Integer> updatedModelToView) {
        final int substractee = getRowCount() - 1;
        for (Entry<Integer, Integer> entry : viewToModel.entrySet()) {
            int newIndex = substractee - entry.getKey();
            updatedViewToModel.put(newIndex, entry.getValue());
        }
        for (Entry<Integer, Integer> entry : modelToView.entrySet()) {
            int newIndex = substractee - entry.getValue();
            updatedModelToView.put(entry.getKey(), newIndex);
        }
    }

    /**
     * Encapsulate TableCellRenderer and print sorting status info
     * at the end of it. Only works when TableCellRenderer works like
     * DefaultTableCellRenderer which creates a JLabel. Otherwise won't
     * print any extra info.
     * @param rend
     * @return
     */
    private TableCellRenderer getHeaderRenderer(final TableCellRenderer rend) {
        return new TableCellRenderer() {

            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = rend.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (comp instanceof JLabel) {
                    JLabel labelComp = (JLabel) comp;
                    SORT_STATE columnState = columnSorted.get(column);
                    String text = colIxToColName.get(column);
                    if (columnState == SORT_STATE.ASCENDING) {
                        text += " (A)";
                    } else if (columnState == SORT_STATE.DESCENDING) {
                        text += " (D)";
                    }
                    labelComp.setText(text);
                }
                return comp;
            }
        };
    }
}
