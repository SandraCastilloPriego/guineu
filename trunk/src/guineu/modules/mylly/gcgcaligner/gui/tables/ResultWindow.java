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
import guineu.modules.mylly.gcgcaligner.alignment.DistValue;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * 
 */

/**
 * @author jmjarkko
 */
public class ResultWindow extends JPanel
{

	private JScrollPane scroller;
	private Alignment curAlignment;
	private ResultTableModel curTableModel;

	public ResultWindow()
	{
		super(new GridLayout(1,0));
	}

	private void addToPanel(JTable table)
	{
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		if (scroller == null)
		{
			scroller = new JScrollPane(table);
			this.add(scroller);
		}
		else
		{
			scroller.setViewportView(table);
		}
	}

	/**
	 * Returns 
	 * @return
	 */
	public List<AlignmentRow> getSelectedAlignments()
	{
		List<AlignmentRow> selected = new ArrayList<AlignmentRow>();
		if (curTableModel != null)
		{
			selected.addAll(curTableModel.getSelectedAlignmentRows());
		}
		return selected;
	}
	
	public Alignment getCurrentAlignment()
	{
		return curAlignment;
	}

	public void showFiles(List<GCGCData> files)
	{
		JTable table = new JTable(new FilesTableModel(files));
		table.setDefaultRenderer(GCGCDatum.class, new StringCellRenderer());
		table.setDefaultRenderer(String.class, new StringCellRenderer());
		curAlignment = null;curTableModel = null;
		addToPanel(table);
	}
	
	public void clearUp()
	{
		if (scroller != null)
		{
			remove(scroller);
			scroller = null;
		}
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				revalidate();repaint();
			}
		});
	}

	public void showResults(Alignment alignment)
	{
		if (curAlignment == null || alignment != curAlignment)
		{
			curAlignment = alignment;
		}

		curTableModel = new ResultTableModel(curAlignment);
		JTable table = new JTable(curTableModel);
		curTableModel.addTableModelListener(table);

		TableCellRenderer intRender = new MyNumberCellRenderer(new DecimalFormat("####"));
		TableCellRenderer doubleRenderer = new MyNumberCellRenderer(new DecimalFormat("####.####"));
//		TableCellRenderer intRender = new NumberCellRenderer(new DecimalFormat("####"));
//		TableCellRenderer doubleRenderer = new NumberCellRenderer(new DecimalFormat("####.####"));
		TableCellRenderer distValRenderer = new DistValueRenderer(new DecimalFormat("####.####"));

		new DefaultTableCellRenderer();
		table.setDefaultRenderer(DistValue.class, distValRenderer);
		table.setDefaultRenderer(int.class, intRender);
		table.setDefaultRenderer(double.class, doubleRenderer);
		table.setDefaultRenderer(boolean.class, new BooleanCellRenderer());
		table.setDefaultRenderer(Boolean.class, new BooleanCellRenderer());
		table.setDefaultRenderer(JCheckBox.class, new JCheckBoxRenderer());
		table.setDefaultRenderer(String.class, new StringCellRenderer());
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		curTableModel.setHeader(table.getTableHeader());
		table.getTableHeader().setReorderingAllowed(false);

		addToPanel(table);
	}

}
