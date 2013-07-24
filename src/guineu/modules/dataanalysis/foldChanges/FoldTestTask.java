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
package guineu.modules.dataanalysis.foldChanges;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.List;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class FoldTestTask extends AbstractTask {

        private double progress = 0.0f;
        private Dataset dataset;
        private String parameter;

        public FoldTestTask(Dataset dataset, FoldTestParameters parameters) {
                this.dataset = dataset;
                this.parameter = parameters.getParameter(FoldTestParameters.groups).getValue();
        }

        @Override
        public String getTaskDescription() {
                return "Fold Changes... ";
        }

        @Override
        public double getFinishedPercentage() {
                return progress;
        }

        @Override
        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        @Override
        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        double[] t = new double[dataset.getNumberRows()];
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                t[i] = this.Foldtest(i);
                        }

                        Dataset newDataset = FileUtils.getDataset(dataset, "Fold changes - ");
                        progress = 0.3f;
                        newDataset.addColumnName("Fold changes");
                        int cont = 0;
                        for (PeakListRow row : dataset.getRows()) {
                                PeakListRow newRow = row.clone();
                                newRow.setPeak("Fold changes", t[cont++]);
                                newDataset.addRow(newRow);
                        }
                        progress = 0.5f;

                        GUIUtils.showNewTable(newDataset, true);

                        progress = 1f;
                        setStatus(TaskStatus.FINISHED);

                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                }
        }

        public double Foldtest(int mol) throws IllegalArgumentException, MathException {
                DescriptiveStatistics stats1 = new DescriptiveStatistics();
                DescriptiveStatistics stats2 = new DescriptiveStatistics();

                String parameter1 = "";


                try {
                        // Determine groups for selected raw data files
                        List<String> availableParameterValues = dataset.getParameterAvailableValues(parameter);

                        int numberOfGroups = availableParameterValues.size();

                        if (numberOfGroups > 1) {
                                parameter1 = availableParameterValues.get(0);
                                String parameter2 = availableParameterValues.get(1);

                                for (String sampleName : dataset.getAllColumnNames()) {
                                        if (dataset.getParametersValue(sampleName, parameter) != null && dataset.getParametersValue(sampleName, parameter).equals(parameter1)) {
                                                stats1.addValue((Double) this.dataset.getRow(mol).getPeak(sampleName));
                                        } else if (dataset.getParametersValue(sampleName, parameter) != null && dataset.getParametersValue(sampleName, parameter).equals(parameter2)) {
                                                stats2.addValue((Double) this.dataset.getRow(mol).getPeak(sampleName));
                                        }
                                }
                        } else {
                                return -1;
                        }

                } catch (Exception e) {
                        e.printStackTrace();
                }


                if (stats1.getN() > 0 && stats2.getN() > 0) {
                        /*double[] sortValues1 = stats1.getSortedValues();
                         double[] sortValues2 = stats2.getSortedValues();

                         return sortValues1[((int) stats1.getN() / 2)] / sortValues2[((int) stats2.getN() / 2)];*/
                        return stats1.getMean() / stats2.getMean();
                } else {
                        return 0;
                }
        }
}
