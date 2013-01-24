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
package guineu.modules.dataanalysis.anova;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.inference.TestUtils;

/**
 *
 * @author scsandra
 */
public class AnovaTestTask extends AbstractTask {

        private Dataset dataset;
        private String parameter;
        private int progress = 0;

        public AnovaTestTask(Dataset dataset, AnovaParameters parameters) {
                this.dataset = dataset;
                parameter = parameters.getParameter(AnovaParameters.groups).getValue();
        }

        public String getTaskDescription() {
                return "Performing Anova test... ";
        }

        public double getFinishedPercentage() {
                return (float) progress / dataset.getNumberRows();
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        List<String> groups = dataset.getParameterAvailableValues(parameter);
                        for (PeakListRow row : dataset.getRows()) {
                                row.setVar("setPValue", anova(groups, row));
                                progress++;
                        }                        
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(AnovaTestTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }

        private double anova(List<String> groups, PeakListRow row) {
                try {
                        DescriptiveStatistics stats1 = new DescriptiveStatistics();
                        List<double[]> classes = new ArrayList<double[]>();
                        for (String group : groups) {
                                List<Double> values = new ArrayList<Double>();
                                for (String name : dataset.getAllColumnNames()) {
                                        if (dataset.getParametersValue(name, parameter) != null && dataset.getParametersValue(name, parameter).equals(group)) {
                                                try {
                                                        values.add((Double) row.getPeak(name));
                                                        stats1.addValue((Double) row.getPeak(name));
                                                } catch (Exception e) {
                                                        System.out.println(row.getPeak(name));
                                                }

                                        }
                                }
                                double[] valuesArray = new double[values.size()];
                                for (int i = 0; i < values.size(); i++) {
                                        valuesArray[i] = values.get(i).doubleValue();
                                }
                               
                                classes.add(valuesArray);
                        }
                       
                        if(stats1.getVariance() == 0) return -1;
                        return TestUtils.oneWayAnovaPValue(classes);
                } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                        return -1;
                } catch (MathException ex) {
                        ex.printStackTrace();
                        return -1;
                }
        }
}
