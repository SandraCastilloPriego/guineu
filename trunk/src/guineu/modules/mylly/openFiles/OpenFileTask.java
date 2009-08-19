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
package guineu.modules.mylly.openFiles;

import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDatasetOther;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.taskcontrol.Task;
import guineu.main.GuineuCore;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class OpenFileTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private String separator;
	private File file;
	private boolean filterClassified;

	OpenFileTask(String fileName, String separator, boolean filterClassified) {		
		file = new File(fileName);
		this.separator = separator;
		this.filterClassified = filterClassified;
	}

	public String getTaskDescription() {
		return "Opening GCGC File... ";
	}

	public double getFinishedPercentage() {
		return 1.0f;
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
		status = TaskStatus.PROCESSING;
		GCGCFileReader reader = new GCGCFileReader(separator, filterClassified);
		try {
			List<GCGCDatum> data = reader.readGCGCDataFile(file);
			SimpleDatasetOther dataset = writeDataset(data);
			GuineuCore.getDesktop().AddNewFile(dataset);
			status = TaskStatus.FINISHED;
		} catch (IOException ex) {
			Logger.getLogger(OpenFileTask.class.getName()).log(Level.SEVERE, null, ex);
			errorMessage = "There has been an error opening the file";
			status = TaskStatus.ERROR;
		}

	}

	private SimpleDatasetOther writeDataset(List<GCGCDatum> data) {
		SimpleDatasetOther datasetOther = new SimpleDatasetOther(file.getName());
		datasetOther.setType(DatasetType.OTHER);
		datasetOther.AddNameExperiment("Id");
		datasetOther.AddNameExperiment("Name");
		datasetOther.AddNameExperiment("RT1");
		datasetOther.AddNameExperiment("RT2");
		datasetOther.AddNameExperiment("RTI");
		datasetOther.AddNameExperiment("Concentration");
		datasetOther.AddNameExperiment("Area");
		datasetOther.AddNameExperiment("CAS");
		datasetOther.AddNameExperiment("Quant Mass");
		datasetOther.AddNameExperiment("Similarity");
		datasetOther.AddNameExperiment("Spectrum");


		for (GCGCDatum mol : data) {
			PeakListRow row = new SimplePeakListRowOther();
			row.setPeak("Id", String.valueOf(mol.getId()));
			row.setPeak("Name", mol.getName());
			row.setPeak("RT1", String.valueOf(mol.getRT1()));
			row.setPeak("RT2", String.valueOf(mol.getRT2()));
			row.setPeak("RTI", String.valueOf(mol.getRTI()));
			row.setPeak("Concentration", String.valueOf(mol.getConcentration()));
			row.setPeak("Area", String.valueOf(mol.getArea()));
			row.setPeak("CAS", String.valueOf(mol.getCAS()));
			row.setPeak("Quant Mass", String.valueOf(mol.getQuantMass()));
			row.setPeak("Similarity", String.valueOf(mol.getSimilarity()));
			row.setPeak("Spectrum", String.valueOf(mol.getSpectrum()));
			datasetOther.AddRow(row);
		}

		return datasetOther;
	}
}













