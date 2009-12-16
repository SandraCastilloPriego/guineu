/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.filter.Alignment.normalizationSTD;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.util.logging.Logger;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

class STDNormalizationTask implements Task {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Dataset peakLists[];
	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	// Processed rows counter
	private int processedRows,  totalRows;

	public STDNormalizationTask(Dataset[] peakLists) {

		this.peakLists = peakLists;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
	 */
	public String getTaskDescription() {
		return "Normalization";
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
	 */
	public double getFinishedPercentage() {
		if (totalRows == 0) {
			return 0f;
		}
		return (double) processedRows / (double) totalRows;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getStatus()
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getErrorMessage()
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#cancel()
	 */
	public void cancel() {
		status = TaskStatus.CANCELED;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		status = TaskStatus.PROCESSING;
		logger.info("Running Normalization");

		for (Dataset data : this.peakLists) {
			normalize(data);
		}
		logger.info(
				"Finished Normalization");
		status = TaskStatus.FINISHED;

	}

	private void normalize(Dataset data) {
		DescriptiveStatistics stats = new DescriptiveStatistics();
		for (String nameExperiment : data.getNameExperiments()) {
			for (PeakListRow row : data.getRows()) {
				Object value = row.getPeak(nameExperiment);
				if (value != null && value instanceof Double) {
					stats.addValue((Double) value);
				}
			}
			for (PeakListRow row : data.getRows()) {
				Object value = row.getPeak(nameExperiment);
				if (value != null && value instanceof Double) {
					row.setPeak(nameExperiment, (Double) value / stats.getStandardDeviation());
				}
			}
			stats.clear();
		}
	}
}
