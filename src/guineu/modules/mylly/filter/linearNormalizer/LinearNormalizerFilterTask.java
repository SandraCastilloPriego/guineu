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
package guineu.modules.mylly.filter.linearNormalizer;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class LinearNormalizerFilterTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private SimpleGCGCDataset dataset;

	public LinearNormalizerFilterTask(SimpleGCGCDataset dataset) {
		this.dataset = dataset;
	}

	public String getTaskDescription() {
		return "Filtering file with Linear Normalizer... ";
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
			List<SimplePeakListRowGCGC> standards = new ArrayList<SimplePeakListRowGCGC>();
			Dataset[] gcgcDatasets = GuineuCore.getDesktop().getSelectedDataFiles();
			SimpleGCGCDataset gcgcDataset = (SimpleGCGCDataset) gcgcDatasets[0];
			for (PeakListRow row : gcgcDataset.getRows()) {
				if (row.isSelected()) {
					int id = row.getID();
					for (PeakListRow row2 : gcgcDataset.getRows()) {
						if (row2.getID() == id) {
							standards.add((SimplePeakListRowGCGC) row2);
							break;
						}
					}
				}
			}
			LinearNormalizer filter = new LinearNormalizer(standards);
			SimpleGCGCDataset newAlignment = filter.actualMap(dataset);
			newAlignment.setDatasetName(newAlignment.toString() + "-Normalized");
			newAlignment.setType(DatasetType.GCGCTOF);
			DataTableModel model = new DatasetGCGCDataModel(newAlignment);
			DataTable table = new PushableTable(model);
			table.formatNumbers(newAlignment.getType());
			DataInternalFrame frame = new DataInternalFrame(newAlignment.getDatasetName(), table.getTable(), new Dimension(800, 800));
			GuineuCore.getDesktop().addInternalFrame(frame);
			GuineuCore.getDesktop().AddNewFile(newAlignment);


			status = TaskStatus.FINISHED;
		} catch (Exception ex) {
			Logger.getLogger(LinearNormalizerFilterTask.class.getName()).log(Level.SEVERE, null, ex);
			status = TaskStatus.ERROR;
		}
	}
}
