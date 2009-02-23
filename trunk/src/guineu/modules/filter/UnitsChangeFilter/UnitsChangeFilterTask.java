/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
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
