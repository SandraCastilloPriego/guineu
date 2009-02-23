/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.util.Tables;

import guineu.data.impl.DatasetType;
import javax.swing.JTable;


/**
 *
 * @author scsandra
 */
public interface DataTable {
    public void createTable(DataTableModel model);
    public void setTableProperties();
    public JTable getTable();
    public void formatNumbers(DatasetType type);
    public void formatNumbers(int column);
    public void setColumnSize();
}
