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
package guineu.modules.dataanalysis.Ttest;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 *
 * @author scsandra
 */
public class TTestTask extends AbstractTask {

        private double progress = 0.0f;
        private Dataset dataset;
        private String parameter;

        public TTestTask(Dataset dataset, TtestParameters parameters) {
                this.dataset = dataset;
                this.parameter = parameters.getParameter(TtestParameters.groups).getValue();
        }

        @Override
        public String getTaskDescription() {
                return "T-Test... ";
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
                        List<double[]> t = new ArrayList<double[]>();
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                t.add(this.Ttest(i));
                        }

                        progress = 0.5f;

                        Dataset newDataset = FileUtils.getDataset(dataset, "T_Test - ");
                        List<String> availableParameterValues = dataset.getParameterAvailableValues(parameter);
                        for (String group : availableParameterValues) {
                                newDataset.addColumnName("Mean of " + group);
                        }
                        int cont = 0;

                        for (PeakListRow row : dataset.getRows()) {
                                row.setVar("setPValue", t.get(cont)[0]);
                                PeakListRow newRow = row.clone();
                                newRow.removePeaks();
                                newRow.setVar("setPValue", t.get(cont)[0]);
                                if (parameter != null) {
                                        for (int i = 0; i < 2; i++) {
                                                newRow.setPeak("Mean of " + availableParameterValues.get(i), t.get(cont)[i + 1]);
                                        }
                                }
                                cont++;
                                newDataset.addRow(newRow);
                        }
                        GUIUtils.showNewTable(newDataset, true);
                        progress = 1f;
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                }
        }

        public double[] Ttest(int mol) throws IllegalArgumentException, MathException {
                DescriptiveStatistics stats1 = new DescriptiveStatistics();
                DescriptiveStatistics stats2 = new DescriptiveStatistics();
                double[] values = new double[3];
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
                                                try {                                                        
                                                        stats1.addValue((Double) this.dataset.getRow(mol).getPeak(sampleName));
                                                } catch (Exception e) {
                                                       
                                                }
                                        } else if (dataset.getParametersValue(sampleName, parameter) != null && dataset.getParametersValue(sampleName, parameter).equals(parameter2)) {
                                                try {
                                                        stats2.addValue((Double) this.dataset.getRow(mol).getPeak(sampleName));
                                                } catch (Exception e) {
                                                       
                                                }
                                        }
                                }
                        } else {
                                return null;
                        }
                } catch (Exception e) {
                }

                TTestImpl ttest = new TTestImpl();
                values[0] = ttest.tTest((StatisticalSummary) stats1, (StatisticalSummary) stats2);
                values[1] = stats1.getMean();
                values[2] = stats2.getMean();
                return values;
        }
}
