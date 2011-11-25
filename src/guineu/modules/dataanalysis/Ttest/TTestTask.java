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
package guineu.modules.dataanalysis.Ttest;

import guineu.data.PeakListRow;
import guineu.data.Dataset;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
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
        private String[] group1, group2;
        private Dataset dataset;
        private String parameter;

        public TTestTask(String[] group1, String[] group2, Dataset dataset, String parameter) {
                this.group1 = group1;
                this.group2 = group2;
                this.dataset = dataset;
                this.parameter = parameter;

        }

        public String getTaskDescription() {
                return "T-Test... ";
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
                        List<double[]> t = new ArrayList<double[]>();
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                t.add(this.Ttest(i));
                        }

                        progress = 0.5f;

                        Dataset newDataset = FileUtils.getDataset(dataset, "T_Test - ");
                        newDataset.addColumnName("Ttest");
                        Vector<String> availableParameterValues = dataset.getParameterAvailableValues(parameter);
                        for (String group : availableParameterValues) {
                                newDataset.addColumnName("Mean of " + group);
                        }
                        int cont = 0;

                        for (PeakListRow row : dataset.getRows()) {
                                PeakListRow newRow = row.clone();
                                newRow.removePeaks();
                                newRow.setPeak("Ttest", t.get(cont)[0]);
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
                        e.printStackTrace();
                        return;
                }
        }

        public double[] Ttest(int mol) throws IllegalArgumentException, MathException {
                DescriptiveStatistics stats1 = new DescriptiveStatistics();
                DescriptiveStatistics stats2 = new DescriptiveStatistics();
                double[] values = new double[3];
                String parameter1 = "";

                if (parameter == null) {
                        for (int i = 0; i < group1.length; i++) {
                                try {
                                        stats1.addValue((Double) this.dataset.getRow(mol).getPeak(group1[i]));
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                        for (int i = 0; i < group2.length; i++) {
                                try {
                                        stats2.addValue((Double) this.dataset.getRow(mol).getPeak(group2[i]));
                                } catch (Exception e) {
                                        e.printStackTrace();
                                }
                        }
                } else {
                        try {
                                // Determine groups for selected raw data files
                                Vector<String> availableParameterValues = dataset.getParameterAvailableValues(parameter);

                                int numberOfGroups = availableParameterValues.size();

                                if (numberOfGroups > 1) {
                                        parameter1 = availableParameterValues.firstElement();
                                        String parameter2 = availableParameterValues.elementAt(1);

                                        for (String sampleName : dataset.getAllColumnNames()) {
                                                if (dataset.getParametersValue(sampleName, parameter) != null && dataset.getParametersValue(sampleName, parameter).equals(parameter1)) {
                                                        stats1.addValue((Double) this.dataset.getRow(mol).getPeak(sampleName));
                                                } else if (dataset.getParametersValue(sampleName, parameter) != null && dataset.getParametersValue(sampleName, parameter).equals(parameter2)) {
                                                        stats2.addValue((Double) this.dataset.getRow(mol).getPeak(sampleName));
                                                }
                                        }
                                } else {
                                        return null;
                                }
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
                TTestImpl ttest = new TTestImpl();
                values[0] = ttest.tTest((StatisticalSummary) stats1, (StatisticalSummary) stats2);
                values[1] = stats1.getMean();
                values[2] = stats2.getMean();
                return values;
        }
}
