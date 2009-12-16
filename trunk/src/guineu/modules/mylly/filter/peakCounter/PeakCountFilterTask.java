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
package guineu.modules.mylly.filter.peakCounter;

import guineu.data.PeakListRow;
import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class PeakCountFilterTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private SimpleGCGCDataset dataset;
	private PeakCountParameters parameters;
	private int ID = 1;

	public PeakCountFilterTask(SimpleGCGCDataset dataset, PeakCountParameters parameters) {
		this.dataset = dataset;
		this.parameters = parameters;
	}

	public String getTaskDescription() {
		return "Filtering files with Peak Count Filter... ";
	}

	public double getFinishedPercentage() {
		return 1f;
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
		try {

			int peakCount = (Integer) parameters.getParameterValue(PeakCountParameters.numFound);
			peakCount--;
			PeakCount filter = new PeakCount(peakCount);
			SimpleGCGCDataset newAlignment = this.actualMap(dataset, filter);
			newAlignment.setDatasetName(newAlignment.toString() + (String) parameters.getParameterValue(PeakCountParameters.suffix));
			newAlignment.setType(DatasetType.GCGCTOF);
			DataTableModel model = new DatasetGCGCDataModel(newAlignment);
			DataTable table = new PushableTable(model);
			table.formatNumbers(newAlignment.getType());
			DataInternalFrame frame = new DataInternalFrame(newAlignment.getDatasetName(), table.getTable(), new Dimension(800, 800));
			GuineuCore.getDesktop().addInternalFrame(frame);
			GuineuCore.getDesktop().AddNewFile(newAlignment);


			status = TaskStatus.FINISHED;
		} catch (Exception ex) {
			Logger.getLogger(PeakCountFilterTask.class.getName()).log(Level.SEVERE, null, ex);
			status = TaskStatus.ERROR;
		}
	}

	private SimpleGCGCDataset actualMap(SimpleGCGCDataset input, PeakCount filter) {
		if (input == null) {
			return null;
		}

		SimpleGCGCDataset filteredAlignment = new SimpleGCGCDataset(input.getColumnNames(),
				input.getParameters(),
				input.getAligner());

		for (PeakListRow row : input.getAlignment()) {
			if (filter.include(row)) {
				filteredAlignment.addAlignmentRow((SimplePeakListRowGCGC) row.clone());
			}
		}

		return filteredAlignment;
	}
}
