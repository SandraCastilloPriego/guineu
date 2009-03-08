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
package guineu.util.Tables.impl;

import guineu.data.impl.DatasetType;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author scsandra
 */
public class PushableTable implements DataTable {

    protected DataTableModel model;
    JTable table;

    public PushableTable() {
    }

    public PushableTable(DataTableModel model) {
        this.model = model;
        table = this.tableRowsColor(model);
        setTableProperties();
    }

    public void createTable(DataTableModel model) {
        this.model = model;
        // Color of the alternative rows         
        table = this.tableRowsColor(model);
    }

    public JTable getTable() {
        return table;
    }

    /**
     * Changes the color of the cells depending of determinates conditions.
     * @param tableModel
     * @return table
     */
    protected JTable tableRowsColor(final DataTableModel tableModel) {
        JTable colorTable = new JTable(tableModel) {

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int Index_row, int Index_col) {
                Component comp = super.prepareRenderer(renderer, Index_row, Index_col);
                try {
                    if (getStandard(Index_row)) {
                        comp.setBackground(Color.yellow);
                    } else if (isDataSelected(Index_row) && (Index_col != 0)) {
                        comp.setBackground(new Color(173, 205, 203));
                    } else if (Index_row % 2 == 0 && !isCellSelected(Index_row, Index_col)) {
                        comp.setBackground(new Color(234, 235, 243));
                    } else if (isCellSelected(Index_row, Index_col)) {
                        comp.setBackground(new Color(173, 205, 203));
                    } else {
                        comp.setBackground(Color.white);
                    }
                    table.repaint();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return comp;
            }

            private boolean isDataSelected(int row) {
                try {
                    return ((Boolean) table.getValueAt(row, 0)).booleanValue();
                } catch (Exception e) {
                    return false;
                }
            }
        };


        return colorTable;
    }

    public boolean getStandard(int row) {
        try {
            if ((Integer) this.getTable().getValueAt(row, 8) == 1) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void setTableProperties() {

        // Tooltips      
        this.createTooltips();

        table.setAutoCreateRowSorter(true);
        table.setUpdateSelectionOnSort(true);


        table.setMinimumSize(new Dimension(300, 800));

        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // Columns position and size  
        this.setColumnSize();
    }

    public void setColumnSize() {
        if (this.model.getType() == DatasetType.GCGCTOF) {
            // move the column "Spectrum" to the end of the table             
            table.moveColumn(13, table.getColumnCount() - 1);
            //columns size
            this.setColumnSize(8, 170, table);
            this.setColumnSize(9, 170, table);
        } else if (this.model.getType() == DatasetType.LCMS) {
            //table listener
            // table.getSelectionModel().addListSelectionListener(new RowListener());	    
            //columns size
            this.setColumnSize(4, 150, table);

        }

    }

    private void setColumnSize(int col, int size, JTable table) {
        TableColumn column = null;
        column = table.getColumnModel().getColumn(col);
        column.setPreferredWidth(size);
    }

    public void formatNumbers(DatasetType type) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(7);
        int init = model.getFixColumns();

        for (int i = init; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(new NumberRenderer(format));
        }

    }

    public void formatNumbers(int column) {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumFractionDigits(7);
        table.getColumnModel().getColumn(column).setCellRenderer(new NumberRenderer(format));
    }

    /**
     * Creates the tooltips in the head of the all tables.
     * @param table
     * @param tableModel
     * @param newdata
     */
    private void createTooltips() {
        ToolTipHeader toolheader;
        String[] toolTipStr = new String[model.getColumnCount()];
        for (int i = 0; i <
                model.getColumnCount(); i++) {
            toolTipStr[i] = model.getColumnName(i);
        }

        toolheader = new ToolTipHeader(table.getColumnModel());
        toolheader.setToolTipStrings(toolTipStr);
        table.setTableHeader(toolheader);
    }

    /**
     * Tooltips
     *
     */
    class ToolTipHeader extends JTableHeader {

        private static final long serialVersionUID = 1L;
        String[] toolTips;

        public ToolTipHeader(TableColumnModel model) {
            super(model);
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            int col = columnAtPoint(e.getPoint());
            int modelCol = getTable().convertColumnIndexToModel(col);

            String retStr;
            try {
                retStr = toolTips[modelCol];
            } catch (NullPointerException ex) {
                retStr = "";
                System.out.println("NullPointer Exception tooltips");
            } catch (ArrayIndexOutOfBoundsException ex) {
                retStr = "";
                System.out.println("ArrayIndexOutOfBoundsException tooltips");
            }
            if (retStr.length() < 1) {
                retStr = super.getToolTipText(e);
            }
            return retStr;
        }

        public void setToolTipStrings(String[] toolTips) {
            this.toolTips = toolTips;
        }
    }

    /**
     * Push header
     * 
     */
    class HeaderListener extends MouseAdapter {

        JTableHeader header;
        ButtonHeaderRenderer renderer;

        HeaderListener(JTableHeader header, ButtonHeaderRenderer renderer) {
            this.header = header;
            this.renderer = renderer;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int col = header.columnAtPoint(e.getPoint());
            renderer.setPressedColumn(col);
            header.repaint();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            renderer.setPressedColumn(-1); // clear
            header.repaint();
        }
    }

    /**
     * Button header
     * 
     */
    class ButtonHeaderRenderer extends JButton implements TableCellRenderer {

        int pushedColumn;

        public ButtonHeaderRenderer() {
            pushedColumn = -1;
            setMargin(new Insets(0, 0, 0, 0));
        }

        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            setText((value == null) ? "" : value.toString());
            boolean isPressed = (column == pushedColumn);
            getModel().setPressed(isPressed);
            getModel().setArmed(isPressed);
            return this;
        }

        public void setPressedColumn(int col) {
            pushedColumn = col;
        }
    }

    class NumberRenderer
            extends DefaultTableCellRenderer {

        private NumberFormat formatter;

        public NumberRenderer() {
            this(NumberFormat.getNumberInstance());
        }

        public NumberRenderer(NumberFormat formatter) {
            super();
            this.formatter = formatter;
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public void setValue(Object value) {
            if ((value != null) && (value instanceof Number)) {
                value = formatter.format(value);
            }

            super.setValue(value);
        }
    }
}
