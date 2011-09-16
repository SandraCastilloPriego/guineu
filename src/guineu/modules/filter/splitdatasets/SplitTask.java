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
package guineu.modules.filter.splitdatasets;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import guineu.util.components.FileUtils;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class SplitTask extends AbstractTask {

        private double progress = 0.0f;
        private String[] group1, group2;
        private Dataset dataset;
        private String parameter;

        public SplitTask(String[] group1, String[] group2, Dataset dataset, String parameter) {
                this.group1 = group1;
                this.group2 = group2;
                this.dataset = dataset;
                this.parameter = parameter;
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
                        progress = 0.5f;

                        Split(group1, "1");
                        Split(group2, "2");

                        if (parameter != null) {
                                SplitFromParameter();
                        }
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
                GUIUtils.showNewTable(datasetSplit, true);
        }

        private void SplitFromParameter() {
                Vector<String> availableParameterValues = dataset.getParameterAvailableValues(parameter);

                for (String parameterVal : availableParameterValues) {
                        Vector<String> group = new Vector<String>();
                        for (String rawDataFile : dataset.getAllColumnNames()) {
                                if (dataset.getParametersValue(rawDataFile, parameter).equals(parameterVal)) {
                                        group.addElement(rawDataFile);
                                }
                        }
                        Split(group.toArray(new String[0]), parameterVal);
                }
        }
}
