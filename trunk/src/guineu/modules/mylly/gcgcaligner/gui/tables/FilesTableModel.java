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



import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class FilesTableModel extends AbstractTableModel
{
	private int columnCount;
	private int rowCount;
	private List<GCGCData> files;
	
	public FilesTableModel(List<GCGCData> files)
	{
		this.files = files;
		columnCount = files.size();
		rowCount = 0;
		for (GCGCData d : files)
		{
			if (d.compoundCount() > rowCount)
			{
				rowCount = d.compoundCount();
			}
		}
	}

	public int getColumnCount()
	{
		return columnCount;
	}

	public int getRowCount()
	{
		return rowCount;
	}

	public int findColumn(String name)
	{
		int location = -1;
		for (int i = 0; i < columnCount; i++)
		{
			if (files.get(i).getName().equals(name))
			{
				location = i;
				break;
			}
		}
		
		return location;
	}
	
	public String getColumnName(int ix)
	{
		return files.get(ix).getName();
	}
	
	public Object getValueAt(int rowIndex, int columnIndex)
	{
		Object val = "";
		if (columnIndex < columnCount)
		{
			GCGCData curColumn = files.get(columnIndex);
			if (rowIndex < curColumn.compoundCount())
			{
				val = curColumn.getCompound(rowIndex).toString();
			}
		}
		return val;
	}

}
