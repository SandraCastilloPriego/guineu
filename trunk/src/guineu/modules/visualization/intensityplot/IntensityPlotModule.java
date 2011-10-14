/*
 * Copyright 2007-2011 VTT Biotechnology
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
/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */

package guineu.modules.visualization.intensityplot;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;



/**
 * Peak intensity plot module
 */
public class IntensityPlotModule implements GuineuProcessingModule {

	private IntensityPlotParameters parameters = new IntensityPlotParameters();
        public static final String MODULE_NAME = "Peak intensity plot";

	private static IntensityPlotModule myInstance;

	public IntensityPlotModule() {
		myInstance = this;
	}

	
	public String toString() {
		return MODULE_NAME;
	}

	/**
	 * @see net.sf.mzmine.modules.MZmineModule#getParameterSet()
	 */
	public ParameterSet getParameterSet() {
		return parameters;
	}

	public static void showIntensityPlot(Dataset peakList, PeakListRow rows[]) {

		myInstance.parameters.getParameter(IntensityPlotParameters.dataFiles)
				.setChoices(peakList.getAllColumnNames().toArray(new String[0]));

		myInstance.parameters.getParameter(IntensityPlotParameters.dataFiles)
				.setValue(peakList.getAllColumnNames().toArray(new String[0]));

		myInstance.parameters
				.getParameter(IntensityPlotParameters.selectedRows).setChoices(
						rows);
		myInstance.parameters
				.getParameter(IntensityPlotParameters.selectedRows).setValue(
						rows);


                
		ExitCode exitCode = myInstance.parameters.showSetupDialog();

		if (exitCode == ExitCode.OK)
			myInstance.runModule(myInstance.parameters.clone());

	}

	@Override
	public Task[] runModule(ParameterSet parameters) {

		IntensityPlotFrame newFrame = new IntensityPlotFrame(parameters);
		GuineuCore.getDesktop().addInternalFrame(newFrame);
		return null;
	}

	@Override
	public GuineuModuleCategory getModuleCategory() {
		return GuineuModuleCategory.VISUALIZATION;
	}

        public String getIcon() {
                return null;
        }

        public boolean setSeparator() {
                return false;
        }

}