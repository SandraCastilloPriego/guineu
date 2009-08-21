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
package guineu.modules.mylly.gcgcaligner.gui;


import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.gui.tables.ResultWindow;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

public class AlignmentPane extends ObjectList<Alignment>
{
	private Map<Alignment, ResultWindow> alignmentToResult;
	
	public AlignmentPane()
	{
		super();
		alignmentToResult = new HashMap<Alignment, ResultWindow>();
	}
	
	public void removeObject(Alignment al)
	{
		super.removeObject(al);
		alignmentToResult.remove(al);
	}
	
	public Alignment getSelectedAlignment()
	{
		List<Alignment> selected = getSelected();
		if (selected.size() == 0)
		{
			throw new IllegalStateException("No alignments selected");
		}
		else if (selected.size() > 1)
		{
			throw new IllegalStateException("Multiple alignments selected");
		}
		Alignment al = selected.get(0);
		return al;
	}
	
	public ResultWindow getResultWindow(Alignment al)
	{
		ResultWindow window = alignmentToResult.get(al);
		if (window == null)
		{
			window = new ResultWindow();
			window.showResults(al);
			alignmentToResult.put(al, window);
		}
		return window;
	}
}
