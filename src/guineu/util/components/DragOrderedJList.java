/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.util.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.DefaultListModel;
import javax.swing.JList;

/**
 * A modified JList that can reorder items in DefaultListModel by dragging with
 * mouse
 * 
 */
public class DragOrderedJList extends JList {

	private int dragFrom;

	public DragOrderedJList(DefaultListModel model) {
		super(model);

		// add mouse button pressed listener
		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent m) {
				dragFrom = getSelectedIndex();
			}
		});

		// add mouse move listener
		addMouseMotionListener(new MouseMotionAdapter() {

			public void mouseDragged(MouseEvent m) {

				// get drag target
				int dragTo = getSelectedIndex();

				// ignore event if order has not changed
				if (dragTo == dragFrom)
					return;

				// reorder the item
				DefaultListModel listModel = (DefaultListModel) DragOrderedJList.this
						.getModel();
				Object item = listModel.elementAt(dragFrom);
				listModel.removeElementAt(dragFrom);
				listModel.add(dragTo, item);

				// update drag source
				dragFrom = dragTo;
			}
		});
	}
}