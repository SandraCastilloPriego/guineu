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
package guineu.modules.filter.splitdatasets;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.components.FileUtils;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class SplitTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private String[] group1, group2;
    private Dataset dataset;
    private String parameter;

    public SplitTask(String[] group1, String[] group2, Dataset dataset, Desktop desktop, String parameter) {
        this.group1 = group1;
        this.group2 = group2;
        this.dataset = dataset;
        this.desktop = desktop;
        this.parameter = parameter;
    }

    public String getTaskDescription() {
        return "Split dataset... ";
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
            progress = 0.5f;

            Split(group1, "1");
            Split(group2, "2");

            if (parameter != null) {
                SplitFromParameter();
            }
            progress = 1f;
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    private void Split(String[] group, String groupName) {
        // If the group is empty
        if (group.length == 0) {
            return;
        }

        Dataset DatasetSplit = FileUtils.getDataset(dataset, "Split dataset " + groupName + " - ");

        for (String name : group) {
            DatasetSplit.AddNameExperiment(name);
        }
        for (PeakListRow row : dataset.getRows()) {
            PeakListRow newRow = row.clone();
            newRow.removeNoSamplePeaks(group);
            DatasetSplit.AddRow(newRow);
        }
        this.createNewDataset(DatasetSplit);
    }

    private void SplitFromParameter() {
        Vector<String> availableParameterValues = new Vector<String>();
        for (String rawDataFile : dataset.getNameExperiments()) {
            String paramValue = dataset.getParametersValue(rawDataFile, parameter);
            if (!availableParameterValues.contains(paramValue)) {
                availableParameterValues.add(paramValue);
            }
        }

        for (String parameterVal : availableParameterValues) {
            Vector<String> group = new Vector<String>();
            for (String rawDataFile : dataset.getNameExperiments()) {
                if (dataset.getParametersValue(rawDataFile, parameter).equals(parameterVal)) {
                    group.addElement(rawDataFile);
                }
            }
            Split(group.toArray(new String[0]), parameterVal);
        }
    }

    private void createNewDataset(Dataset newDataset) {
        DataTableModel model = FileUtils.getTableModel(newDataset);
        DataTable table = new PushableTable(model);
        table.formatNumbers(newDataset.getType());
        DataInternalFrame frame = new DataInternalFrame(newDataset.getDatasetName(), table.getTable(), new Dimension(450, 450));
        desktop.addInternalFrame(frame);
        desktop.AddNewFile(newDataset);
        frame.setVisible(true);
    }
}
