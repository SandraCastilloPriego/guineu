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
package guineu.modules.dataanalysis.wilcoxontest;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class WilcoxonTestTask extends AbstractTask {

        private String[] group1, group2;
        private Dataset dataset;
        private String parameter;
        private double progress;

        public WilcoxonTestTask(String[] group1, String[] group2, Dataset dataset, String parameter) {
                this.group1 = group1;
                this.group2 = group2;
                this.dataset = dataset;
                this.parameter = parameter;
        }

        public String getTaskDescription() {
                return "Wilcoxon signed rank test... ";
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

                        Dataset newDataset = FileUtils.getDataset(dataset, "Wilcoxon_test - ");                      
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
                        return;
                }
        }

        public double[] Ttest(int mol) throws IllegalArgumentException {
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
                                e.printStackTrace();
                        }
                }
                try {
                        final Rengine rEngine;
                        try {
                                rEngine = RUtilities.getREngine();
                        } catch (Throwable t) {

                                throw new IllegalStateException(
                                        "Wilcoxon test requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }
                        synchronized (RUtilities.R_SEMAPHORE) {
                                rEngine.eval("x <- 0");
                                rEngine.eval("y <- 0");
                                long group1 = rEngine.rniPutDoubleArray(stats1.getValues());
                                rEngine.rniAssign("x", group1, 0);

                                long group2 = rEngine.rniPutDoubleArray(stats2.getValues());
                                rEngine.rniAssign("y", group2, 0);
                               /* if(mol == 1){
                                rEngine.eval("write.csv(x, \"x.csv\")");
                                rEngine.eval("write.csv(y, \"y.csv\")");
                                }*/
                                rEngine.eval("result <- 0");

                                rEngine.eval("result <- wilcox.test(as.numeric(t(x)),as.numeric(t(y)))");
                                long e = rEngine.rniParse("result$p.value", 1);
                                long r = rEngine.rniEval(e, 0);
                                REXP x = new REXP(rEngine, r);

                                values[0] = x.asDouble();
                        }

                        rEngine.end();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(WilcoxonTestTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }



                values[1] = stats1.getMean();
                values[2] = stats2.getMean();
                return values;
        }
}
