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
package guineu.modules.mylly.alignment.scoreAligner;

import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDatasetOther;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.main.GuineuCore;




import guineu.modules.mylly.alignment.scoreAligner.functions.Aligner;
import guineu.modules.mylly.alignment.scoreAligner.functions.Alignment;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentRow;
import guineu.modules.mylly.alignment.scoreAligner.functions.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.taskcontrol.Task;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class ScoreAlignmentTask implements Task {

	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;
	private List<GCGCData> datasets;
	private ScoreAlignmentParameters parameters;
	private int ID = 1;
	private Aligner aligner;
	public ScoreAlignmentTask(List<GCGCData> datasets, ScoreAlignmentParameters parameters) {
		this.datasets = datasets;
		this.parameters = parameters;
		aligner = (Aligner) new ScoreAligner(datasets, parameters);
	}

	public String getTaskDescription() {
		return "Aligning files... ";
	}

	public double getFinishedPercentage() {
		return aligner.getProgress();
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
			Alignment alignment = aligner.align();
			SimpleDatasetOther dataset = writeDataset(alignment);
			GuineuCore.getDesktop().AddNewFile(dataset);
			status = TaskStatus.FINISHED;
			//alignment.SaveToFile(new File("c:/alignment"), "\t");
		} catch (Exception ex) {
			Logger.getLogger(ScoreAlignmentTask.class.getName()).log(Level.SEVERE, null, ex);
			errorMessage = "There has been an error doing Score Alignment";
			status = TaskStatus.ERROR;
		}
	}

	private SimpleDatasetOther writeDataset(Alignment alignment) {		
		SimpleDatasetOther datasetOther = new SimpleDatasetOther(alignment.toString());
		datasetOther.setType(DatasetType.OTHER);

		boolean containsMainPeaks = alignment.containsMainPeaks();
		datasetOther.AddNameExperiment("ID");
		datasetOther.AddNameExperiment("RT1");
		datasetOther.AddNameExperiment("RT2");
		datasetOther.AddNameExperiment("RTI");
		datasetOther.AddNameExperiment("Num Found");
		datasetOther.AddNameExperiment("CAS");
		datasetOther.AddNameExperiment("Name");
		datasetOther.AddNameExperiment("All Names");
		datasetOther.AddNameExperiment("Quant Mass");
		if (containsMainPeaks) {
			datasetOther.AddNameExperiment("Difference to ideal peak");
		}
		datasetOther.AddNameExperiment("Max Similarity");
		datasetOther.AddNameExperiment("Mean Similarity");
		datasetOther.AddNameExperiment("Similarity Std Dev");


		String[] columnsNames = alignment.getColumnNames();
		for (String columnName : columnsNames) {
			datasetOther.AddNameExperiment(columnName);
		}

		datasetOther.AddNameExperiment("Spectrum");

		for (AlignmentRow gcgcRow : alignment.getAlignment()) {
			PeakListRow row = new SimplePeakListRowOther();
			writeRow(gcgcRow, row, columnsNames, containsMainPeaks);
			datasetOther.AddRow(row);
		}
		return datasetOther;
	}

	private void writeRow(AlignmentRow gcgcRow, PeakListRow row, String[] columnsNames, boolean containsMainPeaks) {

		NumberFormat formatter = new DecimalFormat("####.####");


		row.setPeak("ID", String.valueOf(ID++));

		String allNames = "";
		for (String name : gcgcRow.getNames()) {
			allNames += name + " || ";
		}

		row.setPeak("All Names", allNames);
		row.setPeak("Name", gcgcRow.getName());
		row.setPeak("RT1", String.valueOf(formatter.format(gcgcRow.getMeanRT1())));
		row.setPeak("RT2", String.valueOf(formatter.format(gcgcRow.getMeanRT2())));
		row.setPeak("RTI", String.valueOf(formatter.format(gcgcRow.getMeanRTI())));
		row.setPeak("Num Found", String.valueOf(gcgcRow.nonNullPeakCount()));
		row.setPeak("CAS", String.valueOf(gcgcRow.getCAS()));
		row.setPeak("Quant Mass", String.valueOf(gcgcRow.getQuantMass()));
		if (containsMainPeaks) {
			if (!gcgcRow.getDistValue().isNull()) {
				row.setPeak("Difference to ideal peak", formatter.format(gcgcRow.getDistValue().distance()));
			} else {
			}
		}
		row.setPeak("Max Similarity", String.valueOf(formatter.format(gcgcRow.getMaxSimilarity())));
		row.setPeak("Mean Similarity", String.valueOf(formatter.format(gcgcRow.getMeanSimilarity())));
		row.setPeak("Similarity Std Dev", String.valueOf(formatter.format(gcgcRow.getSimilarityStdDev())));
		row.setPeak("Spectrum", String.valueOf(gcgcRow.getSpectrum()));
		int cont = 0;
		for (GCGCDatum data : gcgcRow) {
			if (data != null) {
				if (data.getConcentration() > 0 && (Boolean) parameters.getParameterValue(ScoreAlignmentParameters.useConcentration)) {
					row.setPeak(columnsNames[cont++], String.valueOf(formatter.format(data.getConcentration())));

				} else {
					row.setPeak(columnsNames[cont++], String.valueOf(formatter.format(data.getArea())));
				}
			} else {
				row.setPeak(columnsNames[cont++], "NA");
			}
		}
	}
}
