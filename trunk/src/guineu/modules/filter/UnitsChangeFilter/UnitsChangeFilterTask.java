/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.modules.filter.UnitsChangeFilter;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimpleParameterSet;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class UnitsChangeFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private SimpleDataset dataset;
    private UnitsChangeFilterParameters parameters;

    public UnitsChangeFilterTask(Dataset simpleDataset, Desktop desktop, SimpleParameterSet parameters) {
        this.dataset = (SimpleDataset) simpleDataset;
        this.desktop = desktop;
        this.parameters = (UnitsChangeFilterParameters) parameters;
    }

    public String getTaskDescription() {
        return "Change Units Filter... ";
    }

    public double getFinishedPercentage() {
        return progress;
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
        try {
            status = TaskStatus.PROCESSING;
            for (PeakListRow row : dataset.getRows()) {
                for (String experimentName : dataset.getNameExperiments()) {
                    Double peak = (Double) row.getPeak(experimentName);
                    double divide = (Double) parameters.getParameterValue(UnitsChangeFilterParameters.divide);
                    double multiply = (Double) parameters.getParameterValue(UnitsChangeFilterParameters.multiply);
                    if (divide != 0) {                       
                        row.setPeak(experimentName, peak / divide);
                    }
                    if (multiply != 0) {
                        row.setPeak(experimentName, peak * multiply);                     
                    }
                }
            }
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
