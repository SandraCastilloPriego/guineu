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

import guineu.data.Dataset;
import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

public class ProjectionPlot implements GuineuModule, ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Desktop desktop;

    private ProjectionPlotParameters parameters;

    /**
     * @see net.sf.mzmine.main.MZmineModule#initModule(net.sf.mzmine.main.MZmineCore)
     */
    public void initModule() {

        this.desktop = GuineuCore.getDesktop();

        desktop.addMenuItem(GuineuMenu.STATISTICS,
                "Principal component analysis (PCA)",
                "Principal component analysis", KeyEvent.VK_P, this, "PCA_PLOT", null);

        desktop.addMenuItem(GuineuMenu.STATISTICS,
                "Curvilinear distance analysis (CDA)",
                "Curvilinear distance analysis", KeyEvent.VK_C, this, "CDA_PLOT", null);
        
        desktop.addMenuItem(GuineuMenu.STATISTICS, "Sammon's projection",
                "Sammon's projection", KeyEvent.VK_S,  this, "SAMMON_PLOT", null);

    }

    public String toString() {
        return "Projection plot analyzer";
    }

    public void setParameters(ParameterSet parameters) {
        this.parameters = (ProjectionPlotParameters) parameters;
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

        if ((parameters == null)
                || (selectedAlignedPeakLists[0] != parameters.getSourcePeakList())) {
            parameters = new ProjectionPlotParameters(
                    selectedAlignedPeakLists[0]);
        }

        boolean forceXYComponents = true;
        String command = event.getActionCommand();
        if (command.equals("PCA_PLOT"))
            forceXYComponents = false;

        ProjectionPlotSetupDialog setupDialog = new ProjectionPlotSetupDialog(
                selectedAlignedPeakLists[0], parameters,  forceXYComponents);
        setupDialog.setVisible(true);

        if (setupDialog.getExitCode() == ExitCode.OK) {
            logger.info("Opening new projection plot");

            ProjectionPlotDataset dataset = null;

            if (command.equals("PCA_PLOT"))
                dataset = new PCADataset(parameters);

            if (command.equals("CDA_PLOT"))
                dataset = new CDADataset(parameters);

            if (command.equals("SAMMON_PLOT"))
                dataset = new SammonDataset(parameters);

            GuineuCore.getTaskController().addTask(dataset);
            

        }

    }

}
