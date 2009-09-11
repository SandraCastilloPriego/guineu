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

import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.modules.mylly.datastruct.GCGCDatum;
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
			SimpleGCGCDataset dataset = writeDataset(data);
			GuineuCore.getDesktop().AddNewFile(dataset);
			status = TaskStatus.FINISHED;
		} catch (IOException ex) {
			Logger.getLogger(OpenFileTask.class.getName()).log(Level.SEVERE, null, ex);
			errorMessage = "There has been an error opening the file";
			status = TaskStatus.ERROR;
		}

	}

	private SimpleGCGCDataset writeDataset(List<GCGCDatum> data) {

		SimpleGCGCDataset dataset = new SimpleGCGCDataset(file.getName());
		dataset.AddNameExperiment(file.getName());
		dataset.setType(DatasetType.GCGCTOF);

		for (GCGCDatum mol : data) {
			SimplePeakListRowGCGC row = new SimplePeakListRowGCGC((int) mol.getId(), mol.getRT1(), mol.getRT2(), mol.getRTI(),
					mol.getSimilarity(), 0, 0, 0, mol.getQuantMass(), 0, mol.getName(),
					null, mol.getSpectrum().toString(), null, mol.getCAS());
			
			GCGCDatum[] peaks = new GCGCDatum[1];
			peaks[0] = mol;
			row.setDatum(peaks);
			dataset.addAlignmentRow(row);
		}

		return dataset;
	}
}













