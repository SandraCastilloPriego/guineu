/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.NameFilter;

import guineu.main.GuineuCore;
import guineu.data.impl.SimpleGCGCDataset;
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
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;

/**
 *
 * @author bicha
 */
public class NameFilterTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private Dataset[] datasets;
	private NameFilterParameters parameters;

	public NameFilterTask(Dataset[] datasets, NameFilterParameters parameters) {
		this.datasets = datasets;
		this.parameters = parameters;
	}

	public String getTaskDescription() {
		return "Filtering files with Name Filter... ";
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
			for (Dataset alignment : datasets) {

				SimpleGCGCDataset dataset = null;
				if (alignment.getNumberCols() == 1) {
					NameFilterModule filter = new NameFilterModule();
					String name = (String) parameters.getParameterValue(NameFilterParameters.fileNames);
					filter.generateNewFilter(this.askParameters(name));
					GCGCDatum[][] datum = ((SimpleGCGCDataset) alignment).toArray();
					List<GCGCDatum> datumList = new ArrayList<GCGCDatum>();
					for (GCGCDatum data : datum[0]) {
						datumList.add(data.clone());
					}
					GCGCData newData = filter.actualMap(new GCGCData(datumList, alignment.getDatasetName()));
					newData.setName(newData.getName() + (String) parameters.getParameterValue(NameFilterParameters.suffix));
					SimpleGCGCDataset newTableOther = this.writeDataset(newData.toList(), newData.getName());
					GuineuCore.getDesktop().AddNewFile(newTableOther);
				} else {
					NamePostFilter filter = new NamePostFilter();
					String names = (String) parameters.getParameterValue(NameFilterParameters.fileNames);
					filter.generateNewFilter(this.askParameters(names));


					dataset = filter.actualMap((SimpleGCGCDataset) alignment);
					dataset.setDatasetName(dataset.toString() + (String) parameters.getParameterValue(NameFilterParameters.suffix));
					dataset.setType(DatasetType.GCGCTOF);
					DataTableModel model = new DatasetGCGCDataModel(dataset);
					DataTable table = new PushableTable(model);
					table.formatNumbers(dataset.getType());
					DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));
					GuineuCore.getDesktop().addInternalFrame(frame);
					GuineuCore.getDesktop().AddNewFile(dataset);
				}
			}

			status = TaskStatus.FINISHED;
		} catch (Exception ex) {
			Logger.getLogger(NameFilterTask.class.getName()).log(Level.SEVERE, null, ex);
			status = TaskStatus.ERROR;
		}
	}

	private SimpleGCGCDataset writeDataset(List<GCGCDatum> data, String name) {

		SimpleGCGCDataset dataset = new SimpleGCGCDataset(name);
		dataset.AddNameExperiment(name);
		dataset.setType(DatasetType.GCGCTOF);

		for (GCGCDatum mol : data) {
			SimplePeakListRowGCGC row = new SimplePeakListRowGCGC((int) mol.getId(), mol.getRT1(), mol.getRT2(), mol.getRTI(),
					mol.getSimilarity(), 0, 0, 0, mol.getQuantMass(), null, mol.getName(),
					null, mol.getSpectrum().toString(), null, mol.getCAS());


			GCGCDatum[] peaks = new GCGCDatum[1];
			mol.setColumnName(name);
			peaks[0] = mol;
			row.setDatum(peaks);
			dataset.addAlignmentRow(row);
		}

		return dataset;
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
