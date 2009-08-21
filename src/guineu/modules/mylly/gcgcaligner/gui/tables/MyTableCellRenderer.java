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

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class MyTableCellRenderer extends JLabel implements TableCellRenderer
{

	protected final static Border NOT_IN_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	private final static Color LIGHTEST_GRAY = new Color(245, 245, 245);

	protected JComponent _rendComp;
	private Color evenBGColor;
	private Color oddBGColor;
	private Color evenFGColor;
	private Color oddFGColor;


	public void setEvenFGColor(Color c){evenFGColor = c;}
	public void setOddFGColor(Color c){oddFGColor = c;}
	public void setEvenBGColor(Color c){evenBGColor = c;}
	public void setOddBGColor(Color c){oddBGColor = c;}
	
	public MyTableCellRenderer()
	{
		this(null);
	}
	
	public MyTableCellRenderer(JComponent rendered)
	{
		super();
		if (rendered != null)
		{
			_rendComp = rendered;
			_rendComp.setOpaque(true);
			_rendComp.setBorder(NOT_IN_FOCUS_BORDER);
		}
		else
		{
			setOpaque(true);
			setBorder(NOT_IN_FOCUS_BORDER);
		}
		evenFGColor = oddFGColor = Color.BLACK;
		oddBGColor = LIGHTEST_GRAY;
		evenBGColor = Color.WHITE;
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column)
	{
		JComponent rendered;
		boolean evenRow = row % 2 == 0;

		if (_rendComp == null)
		{
			rendered = this;
		}
		else
		{
			rendered = _rendComp;
		}
		Color bgColor = (isSelected ? table.getSelectionBackground() : 
			(evenRow ? (evenBGColor == null ? table.getBackground() : evenBGColor) : 
				(oddBGColor == null ? table.getBackground() : oddBGColor)));
		Color fgColor = (isSelected ? table.getSelectionForeground() : 
			(evenRow ? (evenFGColor == null ? table.getSelectionForeground() : evenFGColor) : 
				(oddFGColor == null ? table.getSelectionForeground() : oddFGColor)));


		rendered.setBackground(bgColor);
		rendered.setForeground(fgColor);
		rendered.setFont(table.getFont());

		if (hasFocus)
		{
			Border border = null;
			if (isSelected) 
			{
				border = UIManager.getBorder("Table.focusSelectedCellHighlightBorder");
			}
			if (border == null)
			{
				border = UIManager.getBorder("Table.focusCellHighlightBorder");
			}
			rendered.setBorder(border);
			if (table.isCellEditable(row, column) && !isSelected)
			{
				Color bgColor2;
				Color fgColor2;
				if ((fgColor2 = UIManager.getColor("Table.focusCellForeground")) != null)
				{
					rendered.setForeground(fgColor2);
				}
				if ((bgColor2 = UIManager.getColor("Table.focusCellBackground")) != null)
				{
					rendered.setBackground(bgColor2);
				}
			}
		}
		else
		{
			setBorder(NOT_IN_FOCUS_BORDER);
		}
		return rendered;
	}

	public void invalidate(){}
	public void validate(){}
	public void revalidate(){}
	public void repaint(long tm, int x, int y, int width, int height){}
	public void repaint(Rectangle r){}
	public void repaint(){}

}
