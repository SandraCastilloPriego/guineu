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

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JTable;

public class MyNumberCellRenderer extends MyTableCellRenderer
{
	private NumberFormat	f;

	public MyNumberCellRenderer(NumberFormat format)
	{
		f = format;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		MyTableCellRenderer comp = (MyTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		
		String formatted;
		if (value instanceof Long || value instanceof Integer)
		{
			long val = ((Number) value).longValue();
			formatted = f.format(val);
		}
		else if (value instanceof Double || value instanceof Float)
		{
			double val = ((Number) value).doubleValue();
			if (Double.isNaN(val)){formatted = "";}
			else{formatted = f.format(val);}
		}
		else
		{
			formatted = "ERROR";
		}
		comp.setText(formatted);
		return comp;
	}
	
}
