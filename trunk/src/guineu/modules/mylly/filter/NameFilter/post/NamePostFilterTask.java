/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.NameFilter.post;

import guineu.main.GuineuCore;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.modules.mylly.filter.NameFilter.NameFilterParameters;
import guineu.modules.mylly.filter.NameFilter.NamePostFilter;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.Task.TaskStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;

/**
 *
 * @author bicha
 */
public class NamePostFilterTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private Dataset[] datasets;
	private NameFilterParameters parameters;
	private int ID = 1;

	public NamePostFilterTask(Dataset[] datasets, NameFilterParameters parameters) {
		this.datasets = datasets;
		this.parameters = parameters;
	}

	public String getTaskDescription() {
		return "Filtering files with Name Postprocessor Filter... ";
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
			NamePostFilter filter = new NamePostFilter();
			String names = (String) parameters.getParameterValue(NameFilterParameters.fileNames);
			filter.generateNewFilter(this.askParameters(names));
			List<SimpleGCGCDataset> newDatasets = new ArrayList<SimpleGCGCDataset>();
			for (Dataset alignDataset : datasets) {
				newDatasets.add(filter.actualMap((SimpleGCGCDataset) alignDataset));
			}

			for (SimpleGCGCDataset alignment : newDatasets) {
				alignment.setName(alignment.toString() + (String) parameters.getParameterValue(NameFilterParameters.suffix));
				DataTableModel model = new DatasetGCGCDataModel(alignment);
				DataTable table = new PushableTable(model);
				table.formatNumbers(alignment.getType());
				DataInternalFrame frame = new DataInternalFrame(alignment.getDatasetName(), table.getTable(), new Dimension(800, 800));
				GuineuCore.getDesktop().addInternalFrame(frame);
				GuineuCore.getDesktop().AddNewFile(alignment);
			}

			status = TaskStatus.FINISHED;
		} catch (Exception ex) {
			Logger.getLogger(NamePostFilterTask.class.getName()).log(Level.SEVERE, null, ex);
			status = TaskStatus.ERROR;
		}
	}

	public List<String> askParameters(String names) throws FileNotFoundException {
		File f = new File(names);
		if (f != null) {
			try {
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				List<String> namesList = new ArrayList<String>();
				while ((line = (br.readLine())) != null) {
					namesList.add(line);
				}
				return namesList;
			} catch (FileNotFoundException e) {
				return null;
			//	GCGCAlign.getMainWindow().displayErrorDialog("File " + f + " was not found");
			} catch (IOException e) {
				return null;
			//	GCGCAlign.getMainWindow().displayErrorDialog(e);
			}
		}
		return null;
	}
}
