/*
 * Copyright 2006-2009 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.filter.Alignment.RANSAC;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Range;
import guineu.util.components.FileUtils;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

class RansacAlignerTask implements Task {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private Dataset peakLists[],  alignedPeakList;
	private TaskStatus status = TaskStatus.WAITING;
	private String errorMessage;

	// Processed rows counter
	private int processedRows,  totalRows;
	// Parameters
	private String peakListName;
	private double mzTolerance;
	private double rtTolerance;
	private RansacAlignerParameters parameters;

	public RansacAlignerTask(Dataset[] peakLists, RansacAlignerParameters parameters) {

		this.peakLists = peakLists;
		this.parameters = parameters;

		// Get parameter values for easier use
		peakListName = (String) parameters.getParameterValue(RansacAlignerParameters.peakListName);

		mzTolerance = (Double) parameters.getParameterValue(RansacAlignerParameters.MZTolerance);

		rtTolerance = (Double) parameters.getParameterValue(RansacAlignerParameters.RTTolerance);
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getTaskDescription()
	 */
	public String getTaskDescription() {
		return "Ransac aligner, " + peakListName + " (" + peakLists.length + " peak lists)";
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getFinishedPercentage()
	 */
	public double getFinishedPercentage() {
		if (totalRows == 0) {
			return 0f;
		}
		return (double) processedRows / (double) totalRows;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getStatus()
	 */
	public TaskStatus getStatus() {
		return status;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#getErrorMessage()
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @see net.sf.mzmine.taskcontrol.Task#cancel()
	 */
	public void cancel() {
		status = TaskStatus.CANCELED;
	}

	/**
	 * @see Runnable#run()
	 */
	public void run() {
		status = TaskStatus.PROCESSING;
		logger.info("Running RANSAC aligner");

		// Remember how many rows we need to process.
		for (int i = 0; i < peakLists.length; i++) {
			for (int e = i + 1; e < peakLists.length; e++) {
				totalRows += peakLists[i].getNumberRows();
			}
			totalRows += peakLists[i].getNumberRows();
		}
		totalRows += ((peakLists.length) * (peakLists.length - 1)) / 2;


		// Create a new aligned peak list
		this.alignedPeakList = FileUtils.getDataset(peakLists[0], "Aligned Dataset");

		for (Dataset dataset : this.peakLists) {
			for (String experimentName : dataset.getAllColumnNames()) {
				this.alignedPeakList.addColumnName(experimentName);
			}
		}

		// Ransac alignmnent
		ransacPeakLists(peakLists, alignedPeakList);
		removeDuplicateRows(alignedPeakList);

		// Write non-aligned peaks

		for (Dataset dataset : this.peakLists) {
			for (PeakListRow row : dataset.getRows()) {
				if (getPeakRow(alignedPeakList, dataset, row) == null) {
					PeakListRow newRow = row.clone();
					newRow.setVar("setNumberAlignment", 1);
					alignedPeakList.addRow(newRow);
				}
			}
		}
		

		// Add new aligned peak list to the project
		GuineuCore.getDesktop().AddNewFile(alignedPeakList);

		// Add task description to peakList

		logger.info(
				"Finished RANSAC aligner");
		status = TaskStatus.FINISHED;

	}

	private String getMostCommon(String name) {
		String[] names = name.split(" ; ");
		int[] score = new int[names.length];
		int cont = 0;
		for (String name1 : names) {
			score[cont] = 0;
			for (String name2 : names) {
				if (name1.compareTo(name2) == 0) {
					score[cont]++;
				}
			}
			cont++;
		}

		String bestName = names[0];
		int bestScore = score[0];
		for (int i = 0; i < score.length; i++) {
			if (bestScore < score[i]) {
				bestScore = score[i];
				bestName = names[i];
			}
		}

		return bestName;
	}

	private PeakListRow getPeakRow(Dataset newDataset, Dataset oldDataset, PeakListRow molRow) {
		int NPeaks = molRow.getNumberPeaks();
		for (PeakListRow row : newDataset.getRows()) {
			int cont = 0;
			for (String experimentNames : oldDataset.getAllColumnNames()) {
				try {
					Double peak1 = (Double) row.getPeak(experimentNames);
					Double peak2 = (Double) molRow.getPeak(experimentNames);
					if (peak1 == peak2 && peak1 != 0) {
						cont++;
					}
				} catch (Exception e) {
					break;
				}
			}
			if (cont >= NPeaks) {
				return row;
			}
		}

		return null;
	}

	private PeakListRow getPeakRow(Dataset newDataset, PeakListRow molRow) {
		int NPeaks = molRow.getNumberPeaks();
		for (PeakListRow row : newDataset.getRows()) {
			int cont = 0;
			for (String experimentNames : newDataset.getAllColumnNames()) {
				try {
					Double peak1 = (Double) row.getPeak(experimentNames);
					Double peak2 = (Double) molRow.getPeak(experimentNames);
					if (peak1 == peak2) {
						cont++;
					}
				} catch (Exception e) {
				}
			}
			if (cont >= NPeaks) {
				return row;
			}
		}

		return null;
	}

	private List<PeakListRow> getRowsInsideScanAndMZRange(Dataset peakListY, Range rangeRT, Range rangeMZ) {
		List<PeakListRow> rangeRows = new ArrayList<PeakListRow>();
		for (PeakListRow row : peakListY.getRows()) {
			if (rangeMZ.contains((Double) row.getVar("getMZ")) && rangeRT.contains((Double) row.getVar("getRT"))) {
				rangeRows.add(row);
			}
		}
		return rangeRows;
	}

	/**
	 *
	 * @param peakLists
	 * @param finalPeakList
	 */
	private void ransacPeakLists(Dataset[] peakLists, Dataset AlignedPeakList) {

		// Do the alignment combining all the samples
		for (int i = 0; i < peakLists.length; i++) {
			for (int e = i + 1; e < peakLists.length; e++) {
				if (peakLists[i] != null && peakLists[e] != null) {

					// Get the list of all possible alignments
					Vector<AlignStructMol> list = this.getVectorAlignment(peakLists[i], peakLists[e]);
					RANSAC ransac = new RANSAC(parameters);
					ransac.alignment(list);
					this.getNewPeakList(list, AlignedPeakList);

				}
				processedRows++;
			}
		}
	}

	/**
	 * Create the vector which contains all the possible aligned peaks.
	 * @param peakListX
	 * @param peakListY
	 * @return vector which contains all the possible aligned peaks.
	 */
	private Vector<AlignStructMol> getVectorAlignment(Dataset peakListX, Dataset peakListY) {

		Vector<AlignStructMol> alignMol = new Vector<AlignStructMol>();

		for (PeakListRow row : peakListX.getRows()) {

			// Calculate limits for a row with which the row can be aligned
			double mzMin = ((Double) row.getVar("getMZ")) - mzTolerance;
			double mzMax = ((Double) row.getVar("getMZ")) + mzTolerance;
			double rtMin, rtMax;
			double rtToleranceValue = rtTolerance;
			rtMin = ((Double) row.getVar("getRT")) - rtToleranceValue;
			rtMax = ((Double) row.getVar("getRT")) + rtToleranceValue;

			// Get all rows of the aligned peaklist within parameter limits
			List<PeakListRow> candidateRows = this.getRowsInsideScanAndMZRange(peakListY,
					new Range(rtMin, rtMax), new Range(mzMin, mzMax));

			for (PeakListRow candidateRow : candidateRows) {
				alignMol.addElement(new AlignStructMol(row, candidateRow));
			}
			processedRows++;
		}
		return alignMol;
	}

	private void addPeaks(PeakListRow row, PeakListRow row2) {
		double mz = ((Double) row.getVar("getMZ") + (Double) row2.getVar("getMZ")) / 2;
		double rt = ((Double) row.getVar("getRT") + (Double) row2.getVar("getRT")) / 2;
		String allNames = null;
		if ((String) row.getVar("getName") == null) {
			allNames = (String) row2.getVar("getName");
		} else {
			allNames = (String) row.getVar("getName") + " ; " + row2.getVar("getName");
		}

		Hashtable<String, Double> peaks = (Hashtable<String, Double>) row2.getVar("getPeaksTable");
		Set<String> set = peaks.keySet();

		Iterator<String> itr = set.iterator();
		while (itr.hasNext()) {
			String str = itr.next();
			row.setPeak(str, (Double) peaks.get(str));
		}
		row.setVar("setName", allNames);
		row.setVar("setMZ", mz);
		row.setVar("setRT", rt);
		row.setVar("setIdentificationType", row2.getVar("getIdentificationType"));
	}

	/**
	 * Write the result of all the alignments
	 * @param lists vector which contains the result of all the alignments between all the samples
	 * @return peak list
	 */
	private void getNewPeakList(Vector<AlignStructMol> list, Dataset newDataset) {

		try {
			// for each possible aligned pair of peaks in all the samples			
			for (AlignStructMol mol : list) {

				// check if the rows are already in the new peak list
				PeakListRow row1 = this.getPeakRow(newDataset, mol.row1);
				PeakListRow row2 = this.getPeakRow(newDataset, mol.row2);

				if (mol.Aligned) {
					// if they are not add a new row
					if (row1 == null && row2 == null) {
						PeakListRow row3 = FileUtils.getPeakListRow(newDataset.getType());
						this.addPeaks(row3, mol.row1);
						this.addPeaks(row3, mol.row2);
						newDataset.addRow(row3);

					} else {
						// if one of them is already in the new peak list, add the other aligned peak in the same row
						if (row1 != null) {
							this.addPeaks(row1, mol.row2);
						}
						if (row2 != null) {
							this.addPeaks(row2, mol.row1);
						}
					}
				} else {
					if (row1 == null) {
						PeakListRow row3 = FileUtils.getPeakListRow(newDataset.getType());
						this.addPeaks(row3, mol.row1);
						newDataset.addRow(row3);
					}
					if (row2 == null) {
						PeakListRow row3 = FileUtils.getPeakListRow(newDataset.getType());
						this.addPeaks(row3, mol.row2);
						newDataset.addRow(row3);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Find the rows where are repeats peaks and call the function "removeRepeatedRows()"
	 * @param peakList
	 */
	private void removeDuplicateRows(Dataset peakList) {
		List<PeakListRow> remove = new ArrayList<PeakListRow>();
		for (PeakListRow row : peakList.getRows()) {
			List<PeakListRow> removeRows = this.removeDuplicatePeakRow(peakList, row);
			for (PeakListRow removeRow : removeRows) {
				remove.add(removeRow);
			}
		}

		for (PeakListRow row : remove) {
			peakList.removeRow(row);
		}

		for (PeakListRow row : peakList.getRows()) {
			row.setVar("setNumberAlignment", 0);
			for (Dataset data : this.peakLists) {
				if (row.getPeak(data.getAllColumnNames().elementAt(0)) != null) {
					row.setVar("setNumberAlignment", ((Integer) row.getVar("getNumberAlignment") + 1));
				}
			}

			row.setVar("setName", this.getMostCommon((String) row.getVar("getName")));
		}

	}

	private List<PeakListRow> removeDuplicatePeakRow(Dataset newDataset, PeakListRow molRow) {
		int NPeaks = molRow.getNumberPeaks() - 1;
		List<PeakListRow> removeRows = new ArrayList<PeakListRow>();
		for (PeakListRow row : newDataset.getRows()) {
			if (molRow != row) {
				if (row.getNumberPeaks() < NPeaks) {
					NPeaks = row.getNumberPeaks() - 1;
				}
				int cont = 0;
				for (String experimentNames : newDataset.getAllColumnNames()) {
					try {
						Double peak1 = (Double) row.getPeak(experimentNames);
						Double peak2 = (Double) molRow.getPeak(experimentNames);
						if (peak1 == peak2) {
							cont++;
						}
					} catch (Exception e) {
					}
				}
				if (cont >= NPeaks) {
					if (row.getNumberPeaks() <= molRow.getNumberPeaks()) {
						removeRows.add(row);
					}
				}
			}
		}
		return removeRows;
	}
}
