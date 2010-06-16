/*
 * Copyright 2007-2010 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package guineu.modules.statistics.PCA;

import guineu.util.GUIUtils;
import java.awt.Color;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JToolBar;


public class ProjectionPlotToolbar extends JToolBar {

	static final Icon axesIcon = new ImageIcon("icons/axesicon.png");

	static final Icon labelsIcon = new ImageIcon("icons/annotationsicon.png");

	public ProjectionPlotToolbar(ProjectionPlotWindow masterFrame) {
		super(JToolBar.VERTICAL);

		setFloatable(false);
		setFocusable(false);
		setMargin(new Insets(5, 5, 5, 5));
		setBackground(Color.white);

		GUIUtils.addButton(this, null, axesIcon, masterFrame, "SETUP_AXES",
				"Setup ranges for axes");

		addSeparator();

		GUIUtils.addButton(this, null, labelsIcon, masterFrame,
				"TOGGLE_LABELS", "Toggle sample names");

	}

}
