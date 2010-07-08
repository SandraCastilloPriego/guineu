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
package guineu.modules.statistics.anova;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.inference.TestUtils;

/**
 *
 * @author scsandra
 */
public class AnovaTestTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Dataset dataset;
    private String parameter;
    private int progress = 0;

    public AnovaTestTask(Dataset dataset, AnovaParameters parameters) {
        this.dataset = dataset;
        parameter = (String) parameters.getParameterValue(AnovaParameters.groups);
    }

    public String getTaskDescription() {
        return "Performing Anova test... ";
    }

    public double getFinishedPercentage() {
        return (float) progress / dataset.getNumberRows();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void cancel() {
        status = TaskStatus.CANCELED;
    }

    public void run() {
        status = TaskStatus.PROCESSING;
        try {
            Vector<String> groups = dataset.getParameterAvailableValues(parameter);
            Dataset newDataset = FileUtils.getDataset(dataset, "Anova Test - ");
            newDataset.AddColumnName("Anova test");

            for (PeakListRow row : dataset.getRows()) {
                PeakListRow newRow = row.clone();
                newRow.removePeaks();
                newRow.setPeak("Anova test", anova(groups, row));
                newDataset.AddRow(newRow);
                progress++;
            }
            GUIUtils.showNewTable(newDataset);
            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            Logger.getLogger(AnovaTestTask.class.getName()).log(Level.SEVERE, null, ex);
            status = TaskStatus.ERROR;
        }
    }

    private double anova(Vector<String> groups, PeakListRow row) {
        try {
            List classes = new ArrayList();
            for (String group : groups) {
                Vector<Double> values = new Vector<Double>();
                for (String name : dataset.getAllColumnNames()) {
                    if (dataset.getParametersValue(name, parameter) != null && dataset.getParametersValue(name, parameter).equals(group)) {
                        values.addElement((Double) row.getPeak(name));
                    }
                }
                double[] valuesArray = new double[values.size()];
                for (int i = 0; i < values.size(); i++) {
                    valuesArray[i] = values.elementAt(i).doubleValue();
                }
                classes.add(valuesArray);
            }
            return TestUtils.oneWayAnovaPValue(classes);
        } catch (IllegalArgumentException ex) {
            return -1;
        } catch (MathException ex) {
            return -1;
        }
    }
}
