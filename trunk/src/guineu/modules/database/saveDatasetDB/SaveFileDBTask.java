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
package guineu.modules.database.saveDatasetDB;

import guineu.data.Dataset;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.database.intro.InDataBase;
import guineu.database.intro.InOracle;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.sql.Connection;

/**
 *
 * @author scsandra
 */
public class SaveFileDBTask implements Task {

	private Dataset dataset;
	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private SaveFileParameters parameters;
	private String author,  datasetName,  parameterFileName,  study;
	private InDataBase db;

	public SaveFileDBTask(Dataset dataset, SaveFileParameters parameters) {
		this.dataset = dataset;
		this.parameters = parameters;
		this.author = (String) parameters.getParameterValue(SaveFileParameters.author);
		this.datasetName = (String) parameters.getParameterValue(SaveFileParameters.name);
		this.parameterFileName = (String) parameters.getParameterValue(SaveFileParameters.parameters);
		this.study = (String) parameters.getParameterValue(SaveFileParameters.studyId);
		db = new InOracle();
	}

	public String getTaskDescription() {
		return "Saving Dataset into the database... ";
	}

	public double getFinishedPercentage() {
		return db.getProgress();
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
			saveFile();
		} catch (Exception e) {
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}
	}

	public synchronized void saveFile() {
		try {
			status = TaskStatus.PROCESSING;
			Connection connection = db.connect();
			String type;
			if (dataset.getType() == DatasetType.LCMS) {
				type = "LC-MS";
				db.lcms(connection, (SimpleLCMSDataset) dataset, type, author, datasetName, parameterFileName, study);
			} else if (dataset.getType() == DatasetType.GCGCTOF) {
				type = "GCxGC-MS";
				db.gcgctof(connection, (SimpleGCGCDataset) dataset, type, author, datasetName, study);
			}
			status = TaskStatus.FINISHED;
		} catch (Exception e) {
			status = TaskStatus.ERROR;
		}
	}
}
