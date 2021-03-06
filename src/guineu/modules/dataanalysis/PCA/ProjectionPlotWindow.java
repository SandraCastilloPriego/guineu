/*
 * Copyright 2007-2013 VTT Biotechnology
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


package guineu.modules.dataanalysis.PCA;

import guineu.parameters.ParameterSet;
import guineu.util.dialogs.AxesSetupDialog;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 * 
 */
public class ProjectionPlotWindow extends JInternalFrame implements
		ActionListener {

	private ProjectionPlotToolbar toolbar;
	private ProjectionPlotPanel plot;

	public ProjectionPlotWindow(String peakList,
			ProjectionPlotDataset dataset, ParameterSet parameters) {
		super(null, true, true, true, true);

		toolbar = new ProjectionPlotToolbar(this);
		add(toolbar, BorderLayout.EAST);

		plot = new ProjectionPlotPanel(this, dataset, parameters);
		add(plot, BorderLayout.CENTER);

		String peakTitle = peakList;
		peakTitle = peakTitle.concat(" : ");
		peakTitle = peakTitle.concat(dataset.toString());
                this.setTitle(peakTitle);

		pack();

	}

	public void actionPerformed(ActionEvent event) {

		String command = event.getActionCommand();

		if (command.equals("SETUP_AXES")) {
			AxesSetupDialog dialog = new AxesSetupDialog(plot.getChart()
					.getXYPlot());
			dialog.setVisible(true);
		}

		if (command.equals("TOGGLE_LABELS")) {
			/*
			 * XYItemRenderer rend = plot.getChart().getXYPlot().getRenderer();
			 * rend.setBaseItemLabelsVisible(!rend.getBaseItemLabelsVisible());
			 */
			plot.cycleItemLabelMode();
		}

	}
	
	

}
