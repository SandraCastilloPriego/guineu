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
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.taskcontrol.Task;
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
	
	private int ID = 1;

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
			SimpleDataset gcgcDataset = (SimpleDataset) gcgcDatasets[0];
			for(PeakListRow row : gcgcDataset.getRows()){
				if(row.isSelected()){
					int id = row.getID();
					id--;
					standards.add(dataset.getAlignment().get(id));
					System.out.println(row.getName() +" - "+  dataset.getAlignment().get(id).getName());
				}
			}
			LinearNormalizer filter = new LinearNormalizer(standards);
			SimpleGCGCDataset newAlignment = filter.actualMap(dataset);
			newAlignment.setName(newAlignment.toString() + "-Normalized");
			SimpleDataset newTableOther = this.writeDataset(newAlignment);
			GuineuCore.getDesktop().AddNewFile(newTableOther);
			GuineuCore.getDesktop().AddNewFile(newAlignment);


			status = TaskStatus.FINISHED;
		} catch (Exception ex) {
			Logger.getLogger(LinearNormalizerFilterTask.class.getName()).log(Level.SEVERE, null, ex);
			status = TaskStatus.ERROR;
		}
	}

	private SimpleDataset writeDataset(SimpleGCGCDataset alignment) {
		SimpleDataset datasetOther = new SimpleDataset(alignment.toString());
		datasetOther.setType(DatasetType.GCGCTOF);
		ScoreAlignmentParameters alignmentParameters = alignment.getParameters();
		boolean concentration = (Boolean) alignmentParameters.getParameterValue(ScoreAlignmentParameters.useConcentration);
		String[] columnsNames = alignment.getColumnNames();
		for (String columnName : columnsNames) {
			datasetOther.AddNameExperiment(columnName);
		}

		for (SimplePeakListRowGCGC gcgcRow : alignment.getAlignment()) {
			datasetOther.AddRow(writeRow(gcgcRow, columnsNames, concentration));
		}
		return datasetOther;
	}

	private PeakListRow writeRow(SimplePeakListRowGCGC gcgcRow, String[] columnsNames, boolean concentration) {
		String allNames = "";
		for (String name : gcgcRow.getNames()) {
			allNames += name + " || ";
		}

		PeakListRow row = new SimplePeakListRowGCGC(ID++, gcgcRow.getRT1(), gcgcRow.getRT2(),
				gcgcRow.getRTI(), gcgcRow.getMaxSimilarity(), gcgcRow.getMeanSimilarity(),
				gcgcRow.getSimilaritySTDDev(), ((double) gcgcRow.nonNullPeakCount()),
				gcgcRow.getMass(), gcgcRow.getDistValue().distance(), gcgcRow.getName(),
				allNames, gcgcRow.getSpectrumString().toString(), null, gcgcRow.getCAS());
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
	
}
