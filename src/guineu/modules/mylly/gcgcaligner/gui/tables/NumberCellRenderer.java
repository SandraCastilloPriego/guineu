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

import java.text.NumberFormat;

import javax.swing.table.DefaultTableCellRenderer;

public class NumberCellRenderer extends DefaultTableCellRenderer
{
	
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3229807180514868202L;
	
	private NumberFormat f;
	
	public NumberCellRenderer(NumberFormat f)
	{
		this.f = f;
	}
	
	/**
	 * Displayes NaNs as "".
	 */
	protected void setValue(Object value)
	{
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
		setText(formatted);
	}

}
