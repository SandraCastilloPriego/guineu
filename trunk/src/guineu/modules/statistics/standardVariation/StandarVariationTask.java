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
package guineu.modules.statistics.standardVariation;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class StandarVariationTask extends AbstractTask {

        private double progress = 0.0f;
        private String[] group1, group2;
        private Dataset dataset;

        public StandarVariationTask(String[] group1, String[] group2, Dataset dataset) {
                this.group1 = group1;
                this.group2 = group2;
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Standard Variation... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        Dataset newDataset = this.StandardVariation(group1);
                        GUIUtils.showNewTable(newDataset, true);

                        /*for(int i = 0; i < newDataset.getNumberMolecules(); i++){
                        RegressionChart chart = new RegressionChart(newDataset.getConcentrationsID(i), newDataset.getDatasetName(), newDataset.getMolecule(i).getMolName());
                        desktop.addInternalFrame(chart);
                        chart.setVisible(true);
                        }*/

                        progress = 0.5f;
                        newDataset = this.StandardVariation(group2);
                        GUIUtils.showNewTable(newDataset, true);
                        progress = 1f;
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public Dataset StandardVariation(String[] group) {
                Dataset newDataset = FileUtils.getDataset(dataset, "Standard Variation -");
                for (String experimentName : group) {
                        newDataset.addColumnName(experimentName);
                }

                StandardUmol std = new StandardUmol(group);
                for (PeakListRow peakRow : this.dataset.getRows()) {
                        if ((Integer) peakRow.getVar("getStandard") == 1) {
                                std.setStandard(peakRow, (String) peakRow.getVar("getName"));
                        }
                }
                std.run();
                Vector<PeakListRow> mols = std.getMols();
                for (PeakListRow mol : mols) {
                        newDataset.addRow(mol);
                }
                return newDataset;
        }
}
