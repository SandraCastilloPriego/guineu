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
package guineu.modules.filter.concatenation;

import guineu.data.impl.SimpleDataset_concatenate;
import guineu.data.PeakListRow_concatenate;
import guineu.data.Dataset;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimplePeakListRowConcatenate;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.Task.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.Vector;

public class filterConcatenateTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private Desktop desktop;
	private double progress;

	public filterConcatenateTask(Desktop desktop) {
		this.desktop = desktop;
	}

	public String getTaskDescription() {
		return "concatenate File... ";
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
			Dataset[] datasets = this.desktop.getSelectedDataFiles();
			Dataset[] otherDatasets = new SimpleDataset_concatenate[datasets.length - 1];
			SimpleDataset_concatenate newDataset = null;
			int cont = 0;
			for (Dataset dataset : datasets) {
				// newDataset = createDataset(datasets);
				if (dataset.getDatasetName().matches(".*concatenated.*")) {
					newDataset = ((SimpleDataset_concatenate) dataset).clone();
				} else {
					otherDatasets[cont++] = dataset;
				}
			}
			this.fillDataset(newDataset, otherDatasets);
			// this.refillDataset(newDataset);
			newDataset.setType(DatasetType.OTHER);
			desktop.AddNewFile(newDataset);
			//creates internal frame with the table
            /*DataTableModel model = new DatasetDataModel_concatenate(newDataset);
			DataTable table = new PushableTable(model);
			DataInternalFrame frame = new DataInternalFrame(newDataset.getDatasetName(), table.getTable(), new Dimension(800, 800));
			desktop.addInternalFrame(frame);
			frame.setVisible(true);*/
			//desktop.removeData(datasets[0]);
			//desktop.removeData(datasets[1]);
			status = TaskStatus.FINISHED;
		} catch (Exception e) {
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}
	}

	private SimpleDataset_concatenate createDataset(Dataset[] datasets) {
		SimpleDataset_concatenate dataset = new SimpleDataset_concatenate("concatenated");
		for (int i = 0; i < datasets.length; i++) {
			for (PeakListRow_concatenate row : ((SimpleDataset_concatenate) datasets[i]).getRows()) {
				if (!dataset.containRowName((String) row.getPeak("Name"))) {
					SimplePeakListRowConcatenate newRow = new SimplePeakListRowConcatenate();
					newRow.setPeak("Name", (String) row.getPeak("Name"));
					dataset.AddRow(newRow);
				}
			}
		}

		return dataset;
	}

	private void fillDataset(SimpleDataset_concatenate newDataset, Dataset[] otherDatasets) {

		for (Dataset data : otherDatasets) {
			Vector<String> experimentsNames = newDataset.getNameExperiments();
			for (String Name : ((SimpleDataset_concatenate) data).getNameExperiments()) {
				if (!Name.matches(".*DIPP.*")) {
					newDataset.AddNameExperiment(Name);
				}
			}
		}
		for (PeakListRow_concatenate row2 : newDataset.getRows()) {
			for (Dataset data : otherDatasets) {
				for (PeakListRow_concatenate row : ((SimpleDataset_concatenate) data).getRows()) {

					try {
//                       /* if(data.getDatasetName().matches(".*concatenated.*")){
//                        if (row2.getPeak("Name").toString().contains(row.getPeak(/*"Tubecode"*/"Name").toString())||
//                                row.getPeak("Name").toString().contains(row2.getPeak(/*"Tubecode"*/"Name").toString())) {
//                            for (String peak : ((SimpleDataset_concatenate) newDataset).getNameExperiments()) {
//                                if (!peak.matches(/*".*Tubecode.*"*/"Name")) {
//                                    try {
//                                        row2.setPeak(peak, row.getPeak(peak));
//                                    } catch (Exception ee) {
//
//                                    }
//                                }
//                            }
//                            break;
//                        }
//                        }else{*/
						if (row2.getPeak("DIPPCode").toString().matches(".*" + row.getPeak("DIPP").toString() + ".*")){
								/*for (String peak : ((SimpleDataset_concatenate) newDataset).getNameExperiments()) {

								if (peak.matches("Birthweight")) {*/
									try {
										row2.setPeak("Birthweight", (String) row.getPeak("Birthweight"));
										row2.setPeak("DIPP", (String) row.getPeak("DIPP"));
									} catch (Exception ee) {
									}
								//}
							//}
							//break;
						}//else if(row2.getPeak("DIPPCode").toString().matches(".*" + row.getPeak("DIPP").toString() + ".*")){)
					// }
					} catch (Exception e) {
					}
				}
			}
		}

	}

	private void refillDataset(SimpleDataset_concatenate newDataset) {
		for (PeakListRow_concatenate row : newDataset.getRows()) {
			/* try {
			if (!row.getPeak("is_ICA_Positive").matches("1")) {
			Double value = Double.valueOf(row.getPeak("ICA"));
			if (value > 0) {
			row.setPeak("is_ICA_Positive", "1");
			} else {
			row.setPeak("is_ICA_Positive", "0");
			}
			}
			} catch (Exception e) {
			row.setPeak("is_ICA_Positive", "0");
			}
			try {
			Double value = Double.valueOf(row.getPeak("IKA"));
			if (value > 0) {
			row.setPeak("is_IKA_Positive", "1");
			} else {
			row.setPeak("is_IKA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-Endo-AbA_Positive", "0");
			}
			try {
			Double value = Double.valueOf(row.getPeak("GADA"));
			if (value > 6.4) {
			row.setPeak("is_GADA_Positive", "1");
			} else {
			row.setPeak("is_GADA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_GADA_Positive", "0");
			}
			try {
			Double value = Double.valueOf(row.getPeak("IA2A"));
			if (value > 0.5) {
			row.setPeak("is_IA2A_Positive", "1");
			} else {
			row.setPeak("is_IA2A_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_IA2A_Positive", "0");
			}
			try {
			if (!row.getPeak("is_TGA_Positive").matches("1")) {
			Double value = Double.valueOf(row.getPeak("TGA"));
			if (value >= 5) {
			row.setPeak("is_TGA_Positive", "1");
			} else {
			row.setPeak("is_TGA_Positive", "0");
			}
			}
			} catch (Exception e) {
			row.setPeak("is_TGA_Positive", "0");
			}
			try {
			Double value = Double.valueOf(row.getPeak("S-Endo-AbA"));
			if (value >= 5) {
			row.setPeak("is_S-Endo-AbA_Positive", "1");
			} else {
			row.setPeak("is_S-Endo-AbA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-Endo-AbA_Positive", "0");
			}
			try {
			Double value = Double.valueOf(row.getPeak("S-ARA-AbA"));
			if (value >= 5) {
			row.setPeak("is_S-ARA-AbA_Positive", "1");
			} else {
			row.setPeak("is_S-ARA-AbA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-ARA-AbA_Positive", "0");
			}
			/* try {
			Double value = Double.valueOf(row.getPeak("S-AGA-AbG"));
			if (value >= 5) {
			row.setPeak("is_S-AGA-AbG_Positive", "1");
			} else {
			row.setPeak("is_S-AGA-AbG_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-AGA-AbG_Positive", "0");
			}
			try {
			Double value = Double.valueOf(row.getPeak("S-AGA-AbA"));
			if (value >= 5) {
			row.setPeak("is_S-AGA-AbA_Positivee", "1");
			} else {
			row.setPeak("is_S-AGA-AbA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-AGA-AbA_Positive", "0");
			}   */
			/*  try {
			if (!row.getPeak("is_ICA_Positive").matches("\\d")) {
			row.setPeak("is_ICA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_ICA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_IKA_Positive").matches("\\d")) {
			row.setPeak("is_IKA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_IKA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_GADA_Positive").matches("\\d")) {
			row.setPeak("is_GADA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_GADA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_IA2A_Positive").matches("\\d")) {
			row.setPeak("is_IA2A_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_IA2A_Positive", "0");
			}
			try {
			if (!row.getPeak("is_TGA_Positive").matches("\\d")) {
			row.setPeak("is_TGA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_TGA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_S-Endo-AbA_Positive").matches("\\d")) {
			row.setPeak("is_S-Endo-AbA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-Endo-AbA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_S-ARA-AbA_Positive").matches("\\d")) {
			row.setPeak("is_S-ARA-AbA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-ARA-AbA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_S-AGA-AbG_Positive").matches("\\d")) {
			row.setPeak("is_S-AGA-AbG_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-AGA-AbG_Positive", "0");
			}
			try {
			if (!row.getPeak("is_S-AGA-AbA_Positive").matches("\\d")) {
			row.setPeak("is_S-AGA-AbA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-AGA-AbA_Positive", "0");
			}
			try {
			if (!row.getPeak("is_S-IgA_Positive").matches("\\d")) {
			row.setPeak("is_S-IgA_Positive", "0");
			}
			} catch (Exception e) {
			row.setPeak("is_S-IgA_Positive", "0");
			}*/
		}
	}
}
