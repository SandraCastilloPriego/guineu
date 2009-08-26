/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.NameFilter;

import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.alignment.scoreAligner.functions.Alignment;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
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

/**
 *
 * @author bicha
 */
public class NamePostFilterTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private List<Alignment> datasets;
	private NameFilterParameters parameters;
	private int ID = 1;

	public NamePostFilterTask(List<Alignment> datasets, NameFilterParameters parameters) {
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
			NamePostFilter filter = new NamePostFilter();
			String names = (String) parameters.getParameterValue(NameFilterParameters.fileNames);
			filter.generateNewFilter(this.askParameters(names));
			List<Alignment> newDatasets = new ArrayList<Alignment>();
			for (Alignment alignDataset : datasets) {				
				newDatasets.add(filter.actualMap(alignDataset));
			}

			for (Alignment dates : newDatasets) {
				dates.setName(dates.toString() + (String) parameters.getParameterValue(NameFilterParameters.suffix));
				SimpleDataset dataset = writeDataset(dates);
				GuineuCore.getDesktop().AddNewFile(dataset);
			}

			status = TaskStatus.FINISHED;
		} catch (Exception ex) {
			Logger.getLogger(NamePreFilterTask.class.getName()).log(Level.SEVERE, null, ex);
			status = TaskStatus.ERROR;
		}
	}

	private SimpleDataset writeDataset(Alignment alignment) {
		SimpleDataset datasetOther = new SimpleDataset(alignment.toString());
		datasetOther.setType(DatasetType.GCGCTOF);
		ScoreAlignmentParameters alignmentParameters = alignment.getParameters();
		boolean concentration = (Boolean) alignmentParameters.getParameterValue(ScoreAlignmentParameters.useConcentration);

		String[] columnsNames = alignment.getColumnNames();
		for (String columnName : columnsNames) {
			datasetOther.AddNameExperiment(columnName);
		}

		for (AlignmentRow gcgcRow : alignment.getAlignment()) {
			datasetOther.AddRow(writeRow(gcgcRow, columnsNames, concentration));
		}
		return datasetOther;
	}

	private PeakListRow writeRow(AlignmentRow gcgcRow, String[] columnsNames, boolean concentration) {
		String allNames = "";
		for (String name : gcgcRow.getNames()) {
			allNames += name + " || ";
		}

		PeakListRow row = new SimplePeakListRowGCGC(ID++, gcgcRow.getMeanRT1(), gcgcRow.getMeanRT2(),
				gcgcRow.getMeanRTI(), gcgcRow.getMaxSimilarity(), gcgcRow.getMeanSimilarity(),
				gcgcRow.getSimilarityStdDev(), ((double) gcgcRow.nonNullPeakCount()),
				gcgcRow.getQuantMass(), gcgcRow.getDistValue().distance(), gcgcRow.getName(),
				allNames, gcgcRow.getSpectrum().toString(), null);
		int cont = 0;
		for (GCGCDatum data : gcgcRow) {
			if (data != null) {
				if (data.getConcentration() > 0 && concentration) {
					row.setPeak(columnsNames[cont++], data.getConcentration());

				} else {
					row.setPeak(columnsNames[cont++], data.getArea());
				}
			} else {
				row.setPeak(columnsNames[cont++], "NA");
			}
		}
		return row;
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
