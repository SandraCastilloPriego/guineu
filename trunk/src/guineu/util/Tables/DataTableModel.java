/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.util.Tables;

import guineu.data.Dataset;
import guineu.data.impl.DatasetType;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.table.TableModel;

/**
 *
 * @author scsandra
 */
public interface DataTableModel extends TableModel {

    public SortingDirection getSortDirection();

    public int getSortCol();

    public void setSortDirection(SortingDirection direction);

    public void setSortCol(int column);

    public Object[][] getData();

    public void changeData(int column, int row);

    public Dataset removeRows();
    
    public DatasetType getType();
}
