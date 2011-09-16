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
package guineu.modules.statistics.PCA;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 *
 */
public class ProjectionPlot implements GuineuProcessingModule {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private ProjectionPlotParameters parameters;

        
        public String toString() {
                return "Projection plot analyzer";
        }

        public ProjectionPlotParameters getParameterSet() {
                return parameters;
        }

        public void actionPerformed(ActionEvent event) {

                Dataset selectedAlignedPeakLists[] = desktop.getSelectedDataFiles();
                if (selectedAlignedPeakLists.length != 1) {
                        desktop.displayErrorMessage("Please select a single aligned peaklist");
                        return;
                }

                if (selectedAlignedPeakLists[0].getNumberRows() == 0) {
                        desktop.displayErrorMessage("Selected alignment result is empty");
                        return;
                }

                logger.finest("Showing projection plot setup dialog");
               
                parameters = new ProjectionPlotParameters();  
              
                String command = event.getActionCommand();
              
                ExitCode exitCode = parameters.showSetupDialog();

                if (exitCode == ExitCode.OK) {
                        logger.info("Opening new projection plot");

                        ProjectionPlotDataset dataset = null;

                        if (command.equals("PCA_PLOT")) {
                                dataset = new PCADataset(parameters);
                        }

                        if (command.equals("CDA_PLOT")) {
                                dataset = new CDADataset(parameters);
                        }

                        if (command.equals("SAMMON_PLOT")) {
                                dataset = new SammonDataset(parameters);
                        }

                        GuineuCore.getTaskController().addTask(dataset);

                }

        }

        public Task[] runModule(ParameterSet parameters) {
                throw new UnsupportedOperationException("Not supported yet.");
        }

        public GuineuModuleCategory getModuleCategory() {
                throw new UnsupportedOperationException("Not supported yet.");
        }
}
