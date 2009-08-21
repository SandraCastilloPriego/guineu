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
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * FIXME JCheckBox is initially a bit more to right than after it has been
 * selected. Check constructor and superclass.
 * @author jmjarkko
 *
 */
public class BooleanCellRenderer extends MyTableCellRenderer
{
	
	public BooleanCellRenderer(boolean set)
	{
		super(new JCheckBox());
		((JCheckBox) _rendComp).setSelected(set);
		setLayout(new FlowLayout());
	}
	
	public BooleanCellRenderer()
	{
		this(false);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		((JCheckBox) _rendComp).setSelected(((Boolean) value).booleanValue());
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
