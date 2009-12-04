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
package guineu.modules.statistics.variationCoefficient;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.datamodels.VariationCoefficientDataModel;
import guineu.data.impl.VariationCoefficientData;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.Vector;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class VariationCoefficientTask implements Task {

	private Dataset[] datasets;
	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private Desktop desktop;
	private double progress;

	public VariationCoefficientTask(Dataset[] datasets, Desktop desktop) {
		this.datasets = datasets;
		this.desktop = desktop;

	}

	public String getTaskDescription() {
		return "Coefficient of variation... ";
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
			this.variationCoefficient();
		} catch (Exception e) {
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}
	}

	public void variationCoefficient() {
		status = TaskStatus.PROCESSING;
		try {
			progress = 0.0f;
			Vector<VariationCoefficientData> data = new Vector<VariationCoefficientData>();
			for (Dataset dataset : datasets) {
				VariationCoefficientData vcdata = new VariationCoefficientData();
				vcdata.variationCoefficient = getvariationCoefficient(dataset);
				vcdata.NumberIdentMol = getNumberIdentMol(dataset);
				vcdata.datasetName = dataset.getDatasetName();
				vcdata.numberMol = dataset.getNumberRows();
				vcdata.numberExperiments = dataset.getNumberCols();
				data.addElement(vcdata);
			}

			DataTableModel model = new VariationCoefficientDataModel(data);
			DataTable table = new PushableTable(model);
			table.formatNumbers(1);
			DataInternalFrame frame = new DataInternalFrame("Coefficient of variation", table.getTable(), new Dimension(450, 450));
			desktop.addInternalFrame(frame);
			frame.setVisible(true);

			progress = 1f;

		} catch (Exception ex) {
		}
		status = TaskStatus.FINISHED;
	}

	private int getNumberIdentMol(Dataset dataset) {
		int cont = 0;
		for (PeakListRow row : dataset.getRows()) {
			if (!((String) row.getVar("getName")).toLowerCase().matches("unknown")) {
				cont++;
			}
		}
		return cont;
	}

	private double getvariationCoefficient(Dataset dataset) {
		DescriptiveStatistics superStats = DescriptiveStatistics.newInstance();
		DescriptiveStatistics stats = DescriptiveStatistics.newInstance();
		for (PeakListRow row : dataset.getRows()) {
			stats.clear();
			for (String experimentName : dataset.getNameExperiments()) {
				Object value = row.getPeak(experimentName);
				if (value != null && value instanceof Double) {
					stats.addValue((Double) value);
				}
			}
			if (stats.getMean() > 0) {
				double value = stats.getStandardDeviation() / stats.getMean();
				superStats.addValue(value);
			}
		}
		return superStats.getMean();
	}
}
