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
package guineu.modules.filter.splitdatasets;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class SplitTask extends AbstractTask {

        private double progress = 0.0f;
        private Dataset dataset;
        private String parameter;

        public SplitTask(Dataset dataset, SplitParameters parameters) {
                this.dataset = dataset;
                parameter = (String) parameters.getParameter(SplitParameters.ValueSource).getValue();
                System.out.println(parameter);
        }

        public String getTaskDescription() {
                return "Split dataset... ";
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
                        System.out.println("1");
                        progress = 0.5f;
                        SplitFromParameter();
                        System.out.println("2");
                        progress = 1f;
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        private void Split(String[] group, String groupName) {
                // If the group is empty
                if (group.length == 0) {
                        return;
                }

                Dataset datasetSplit = FileUtils.getDataset(dataset, "Split dataset " + groupName + " - ");

                for (String name : group) {
                        datasetSplit.addColumnName(name);
                }
                for (PeakListRow row : dataset.getRows()) {
                        PeakListRow newRow = row.clone();
                        newRow.removeNoSamplePeaks(group);
                        datasetSplit.addRow(newRow);
                }
                List<String> parameters = dataset.getParametersName();
                for (String p : parameters) {
                        for (String sample : datasetSplit.getAllColumnNames()) {
                                datasetSplit.addParameterValue(sample, p, dataset.getParametersValue(sample, p));
                        }
                }

                GUIUtils.showNewTable(datasetSplit, true);
        }

        private void SplitFromParameter() {
                System.out.println("1.5" + dataset.getType());
                List<String> availableParameterValues = dataset.getParameterAvailableValues(parameter);
                System.out.println(availableParameterValues.size());
                for (String parameterVal : availableParameterValues) {
                        System.out.println(parameterVal);
                        List<String> group = new ArrayList<String>();
                        for (String rawDataFile : dataset.getAllColumnNames()) {
                                if (dataset.getParametersValue(rawDataFile, parameter) != null && dataset.getParametersValue(rawDataFile, parameter).equals(parameterVal)) {
                                        group.add(rawDataFile);
                                }
                        }
                        Split(group.toArray(new String[0]), parameterVal);
                }
        }
}
