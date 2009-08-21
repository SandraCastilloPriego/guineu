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



import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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

public class ObjectList<T> extends JScrollPane
{
	private Set<T> contained;
	private JList listOfObjects;
	private DefaultListModel lm;
	private JPopupMenu	popup;

	public ObjectList()
	{
		lm = new DefaultListModel();
		listOfObjects = new JList(lm);
		contained = new HashSet<T>();

		ListSelectionModel lsm = new DefaultListSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); 
		listOfObjects.setSelectionModel(lsm);
		setViewportView(listOfObjects);
		setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
	}

	public void setDebugGraphicsOptions(int flag)
	{
		super.setDebugGraphicsOptions(flag);
		listOfObjects.setDebugGraphicsOptions(flag);
	}

	public void createPopupMenu(List<Action> actions)
	{
		popup = new JPopupMenu();

		for (Action a : actions)
		{
			if (a == null)
			{
				popup.addSeparator();
			}
			else
			{
				popup.add(a);
			}
		}

		MouseListener ml = new MouseAdapter()
		{
			public void mousePressed(MouseEvent e){perhapsShowPopup(e);}
			public void mouseReleased(MouseEvent e){perhapsShowPopup(e);}

			private void perhapsShowPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		};
		addMouseListener(ml);
		listOfObjects.addMouseListener(ml);
	}

	public void addAll(final Collection<T> objs)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{		
				for (T cur : objs)
				{
					if (! contained.contains(cur)) //No duplicates
					{
						add(cur, false);
					}
				}
				repaint();
			}
		});
	}

	public void setVisible(boolean flag)
	{
		listOfObjects.setVisible(flag);
		super.setVisible(flag);
	}

	private void add(final T obj, boolean useEQ)
	{
		Runnable r = new Runnable()
		{
			public void run()
			{
				lm.addElement(obj);
				contained.add(obj);
			}
		};
		if (useEQ)
		{
			SwingUtilities.invokeLater(r);
		}
		else
		{
			r.run();
		}
	}

	public void addObject(T al)
	{
		add(al, true);
		addRepaintTOEQ();
	}

	private void addRepaintTOEQ()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				repaint();
			}
		});
	}

	private void remove(final T obj, boolean useEventQueue)
	{
		Runnable task = new Runnable()
		{
			public void run()
			{
				boolean firstOk = contained.remove(obj);
				boolean secondOk = lm.removeElement(obj);
			}
		};
		if (useEventQueue)
		{
			SwingUtilities.invokeLater(task);
		}
		else
		{
			task.run();
		}
	}

	public void removeObject(T al)
	{
		remove(al, true);
		addRepaintTOEQ();
	}

	public void removeSelected()
	{
		final List<T> selected = getSelected();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{

				for (T cur : selected)
				{
					remove(cur, false);
				}
				repaint();
			}	
		});
	}

	@SuppressWarnings("unchecked")
	public List<T> getSelected()
	{
		int[] selectedIx = listOfObjects.getSelectedIndices();
		List<T> selected = new ArrayList<T>(selectedIx.length);
		for (int ix : selectedIx)
		{
			selected.add((T) lm.get(ix));
		}
		return selected;
	}
}
