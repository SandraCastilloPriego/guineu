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
package guineu.modules.filter.commonmolecules;

import java.util.logging.Logger;



import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.datamodels.OtherDataModel;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.Vector;

/**
 * 
 */
class CommonMoleculesTask implements Task {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Dataset[] peakLists;
	private TaskStatus status;
	private String errorMessage;
	private String[][] databaseValues;
	private int finishedLines = 0;
	private String dataBaseFile;	
	Vector<lipid> commonNames;

	CommonMoleculesTask(Dataset[] peakList) {
		status = TaskStatus.WAITING;
		this.peakLists = peakList;		
		commonNames = new Vector<lipid>();
	}

	public void cancel() {
		status = TaskStatus.CANCELED;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public double getFinishedPercentage() {
		if (databaseValues == null) {
			return 0;
		}
		return ((double) finishedLines) / databaseValues.length;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public String getTaskDescription() {
		return "Peak identification of " + peakLists + " using database " + dataBaseFile;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		status = TaskStatus.PROCESSING;

		try {

			for (Dataset dataset : peakLists) {
				for (PeakListRow row : dataset.getRows()) {
					SimplePeakListRowLCMS Row = (SimplePeakListRowLCMS)row;
					lipid mol = new lipid(Row.getName(), Row.getMZ(), Row.getRT());
					lipid mol2 = isRepeat(mol);
					if (mol2 != null) {
						mol2.addDatasetName(dataset.getDatasetName());
						mol2.sumApears();
					} else {
						mol.sumApears();
						mol.addDatasetName(dataset.getDatasetName());
						commonNames.addElement(mol);						
					}
				}
			}

			SimpleOtherDataset dataset = new SimpleOtherDataset("Common peaks");
			dataset.AddNameExperiment("m/z");
			dataset.AddNameExperiment("rt");
			dataset.AddNameExperiment("Molecule Name");
			dataset.AddNameExperiment("Number of datasets");
			dataset.AddNameExperiment("Dataset names");
			
			for (lipid mol : commonNames) {
				SimplePeakListRowOther row = new SimplePeakListRowOther();
				row.setPeak("m/z", String.valueOf(mol.mz));
				row.setPeak("rt", String.valueOf(mol.rt));
				row.setPeak("Molecule Name", mol.Name);
				row.setPeak("Number of datasets", String.valueOf(mol.apears));
				row.setPeak("Dataset names", String.valueOf(mol.DatasetNames));

				dataset.AddRow(row);
			}
			DataTableModel model = new OtherDataModel(dataset);
			DataTable table = new PushableTable(model);
			table.formatNumbers(dataset.getType());
			DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

			GuineuCore.getDesktop().addInternalFrame(frame);
			GuineuCore.getDesktop().AddNewFile(dataset);

		} catch (Exception e) {
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}


		status = TaskStatus.FINISHED;

	}

	private lipid isRepeat(lipid mol) {
		for (lipid mol2 : commonNames) {
			double mzdiff = Math.abs(mol2.mz - mol.mz);
			double rtdiff = Math.abs(mol2.rt - mol.rt);
			if (mol2.Name.equals(mol.Name) && mzdiff < 0.05 && rtdiff < 4) {
				return mol2;
			}
		}
		return null;
	}

	class lipid {

		String Name;
		double mz;
		double rt;
		int apears;
		String DatasetNames = "";

		public lipid(String name, double mz, double rt) {
			this.Name = name;
			this.mz = mz;
			this.rt = rt;
			apears = 0;
		}

		public void sumApears() {
			apears++;
		}
		public void addDatasetName(String name){
			DatasetNames += " / " + name;
		}
	}
}
