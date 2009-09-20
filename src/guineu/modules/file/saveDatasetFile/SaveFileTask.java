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
package guineu.modules.file.saveDatasetFile;

import guineu.data.Dataset;
import guineu.data.impl.SimpleParameterSet;
import guineu.database.intro.InDataBase;
import guineu.database.intro.InOracle;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class SaveFileTask implements Task {

	private Dataset dataset;
	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private String path;
	private InDataBase db;
	private SaveDatasetParameters parameters;
	private SimpleParameterSet OptionParameters;

	public SaveFileTask(Dataset dataset, SaveDatasetParameters parameters, String path) {
		this.dataset = dataset;
		this.path = path;
		this.parameters = parameters;
		db = new InOracle();		
	}

	public String getTaskDescription() {
		return "Saving Dataset... ";
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
			status = TaskStatus.PROCESSING;
			if (parameters.getParameterValue(SaveDatasetParameters.type).toString().matches(".*Excel.*")) {
				db.WriteExcelFile(dataset, path, OptionParameters);
			} else {
				db.WriteCommaSeparatedFile(dataset, path, OptionParameters);
			}
			status = TaskStatus.FINISHED;
		} catch (Exception e) {
			status = TaskStatus.ERROR;
		}
	}

}
