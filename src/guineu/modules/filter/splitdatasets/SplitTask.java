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

/**
 *
 * @author scsandra
 */
public class SplitTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private Desktop desktop;
	private double progress = 0.0f;
	private String[] group1,  group2;
	private Dataset dataset;

	public SplitTask(String[] group1, String[] group2, Dataset dataset, Desktop desktop) {
		this.group1 = group1;
		this.group2 = group2;
		this.dataset = dataset;
		this.desktop = desktop;

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
			Dataset DatasetSplit1 = FileUtils.getDataset(dataset, "Split dataset 1 - ");
			Dataset DatasetSplit2 = FileUtils.getDataset(dataset, "Split dataset 2 - ");		

			for (String name : group1) {
				DatasetSplit1.AddNameExperiment(name);
			}
			for (PeakListRow row : dataset.getRows()) {
				PeakListRow newRow = row.clone();
				newRow.removeNoSamplePeaks(group2);
				DatasetSplit1.AddRow(newRow);
			}
			for (String name : group2) {
				DatasetSplit2.AddNameExperiment(name);
			}
			for (PeakListRow row : dataset.getRows()) {
				PeakListRow newRow = row.clone();
				newRow.removeNoSamplePeaks(group1);
				DatasetSplit2.AddRow(newRow);
			}

			this.createNewDataset(DatasetSplit1);
			this.createNewDataset(DatasetSplit2);
			progress = 1f;
			status = TaskStatus.FINISHED;
		} catch (Exception e) {
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}
	}

	public void createNewDataset(Dataset newDataset) {
		DataTableModel model = FileUtils.getTableModel(newDataset);
		DataTable table = new PushableTable(model);
		table.formatNumbers(newDataset.getType());
		DataInternalFrame frame = new DataInternalFrame(newDataset.getDatasetName(), table.getTable(), new Dimension(450, 450));
		desktop.addInternalFrame(frame);
		desktop.AddNewFile(newDataset);
		frame.setVisible(true);
	}
}
