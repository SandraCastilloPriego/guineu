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
package guineu.modules.statistics.Ttest;

import guineu.data.PeakListRow;
import guineu.data.impl.SimpleDataset;
import guineu.data.Dataset;
import guineu.data.datamodels.DatasetDataModel;
import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import org.apache.commons.math.MathException;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.StatisticalSummary;
import org.apache.commons.math.stat.inference.TTestImpl;

/**
 *
 * @author scsandra
 */
public class TTestTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private Desktop desktop;
	private double progress = 0.0f;
	private String[] group1,  group2;
	private Dataset dataset;

	public TTestTask(String[] group1, String[] group2, Dataset dataset, Desktop desktop) {
		this.group1 = group1;
		this.group2 = group2;
		this.dataset = dataset;
		this.desktop = desktop;

	}

	public String getTaskDescription() {
		return "T-Test... ";
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
			double[] t = new double[dataset.getNumberRows()];
			for (int i = 0; i < dataset.getNumberRows(); i++) {
				t[i] = this.Ttest(i);
			}

			progress = 0.5f;
			DataTableModel model = null;
			Dataset newDataset = null;
			if (dataset.getType() == DatasetType.LCMS) {
				newDataset = new SimpleDataset("T_Test - " + dataset.getDatasetName());
				newDataset.setType(this.dataset.getType());
				newDataset.AddNameExperiment("Ttest");
				int cont = 0;

				for (PeakListRow row : dataset.getRows()) {
					PeakListRow newRow = row.clone();
					newRow.removePeaks();
					newRow.setPeak("Ttest", t[cont++]);
					((SimpleDataset)newDataset).AddRow(newRow);
				}
				model = new DatasetDataModel(newDataset);
			} else if (dataset.getType() == DatasetType.GCGCTOF) {
				newDataset = new SimpleGCGCDataset("T_Test - " + dataset.getDatasetName());
				newDataset.setType(this.dataset.getType());
				newDataset.AddNameExperiment("Ttest");
				int cont = 0;

				for (PeakListRow row : dataset.getRows()) {
					PeakListRow newRow = row.clone();
					((SimplePeakListRowGCGC) newRow).removePeaks();
					newRow.setPeak("Ttest", t[cont++]);
					((SimpleGCGCDataset)newDataset).addAlignmentRow((SimplePeakListRowGCGC) newRow);
				}
				model = new DatasetGCGCDataModel(newDataset);
			}

			DataTable table = new PushableTable(model);
			table.formatNumbers(dataset.getType());
			DataInternalFrame frame = new DataInternalFrame("T-Test" + dataset.getDatasetName(), table.getTable(), new Dimension(450, 450));
			desktop.addInternalFrame(frame);
			desktop.AddNewFile(newDataset);
			frame.setVisible(true);
			progress = 1f;
			status = TaskStatus.FINISHED;
		} catch (Exception e) {
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}
	}

	public double Ttest(int mol) throws IllegalArgumentException, MathException {
		DescriptiveStatistics stats1 = new DescriptiveStatistics();
		DescriptiveStatistics stats2 = new DescriptiveStatistics();
		for (int i = 0; i < group1.length; i++) {
			stats1.addValue((Double) this.dataset.getRow(mol).getPeak(group1[i]));
		}
		for (int i = 0; i < group2.length; i++) {
			stats2.addValue((Double) this.dataset.getRow(mol).getPeak(group2[i]));
		}
		TTestImpl ttest = new TTestImpl();
		return ttest.tTest((StatisticalSummary) stats1, (StatisticalSummary) stats2);
	}
}
