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

package guineu.modules.identification.CustomIdentification;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;



import com.Ostermiller.util.CSVParser;
import guineu.data.Dataset;
import guineu.data.Parameter;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimpleParameter;
import guineu.taskcontrol.Task;

/**
 * 
 */
class CustomDBSearchTask implements Task {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private SimpleDataset peakList;

	private TaskStatus status;
	private String errorMessage;
	private String[][] databaseValues;
	private int finishedLines = 0;
	
	private String dataBaseFile;
	private String fieldSeparator;
	private Object[] fieldOrder;
	private boolean ignoreFirstLine;
	private double mzTolerance;
	private double rtTolerance;
	private CustomDBSearchParameters parameters;


	CustomDBSearchTask(Dataset peakList, CustomDBSearchParameters parameters) {
		status = TaskStatus.WAITING;
		this.peakList = (SimpleDataset) peakList;
		
		dataBaseFile = (String) parameters
		.getParameterValue(CustomDBSearchParameters.dataBaseFile);
		fieldSeparator = (String) parameters
		.getParameterValue(CustomDBSearchParameters.fieldSeparator);

		Parameter p = parameters.getParameter("Field order");
		fieldOrder = ((SimpleParameter) p)
				.getPossibleValues();

		ignoreFirstLine = (Boolean) parameters
		.getParameterValue(CustomDBSearchParameters.ignoreFirstLine);
		mzTolerance = (Double) parameters
		.getParameterValue(CustomDBSearchParameters.mzTolerance);
		rtTolerance = (Double) parameters
		.getParameterValue(CustomDBSearchParameters.rtTolerance);
		
	}

	
	public void cancel() {
		status = TaskStatus.CANCELED;
	}

	
	public String getErrorMessage() {
		return errorMessage;
	}

	
	public double getFinishedPercentage() {
		if (databaseValues == null)
			return 0;
		return ((double) finishedLines) / databaseValues.length;
	}

	
	public TaskStatus getStatus() {
		return status;
	}

	
	public String getTaskDescription() {
		return "Peak identification of " + peakList + " using database "
				+ dataBaseFile;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {

		status = TaskStatus.PROCESSING;

		File dbFile = new File(dataBaseFile);

		try {
			// read database contents in memory
			FileReader dbFileReader = new FileReader(dbFile);
			databaseValues = CSVParser.parse(dbFileReader, fieldSeparator.charAt(0));
			if (ignoreFirstLine)
				finishedLines++;
			for (; finishedLines < databaseValues.length; finishedLines++) {
				try {
					processOneLine(databaseValues[finishedLines]);
				} catch (Exception e) {
					// ingore incorrect lines
				}
			}
			dbFileReader.close();

		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not read file " + dbFile, e);
			status = TaskStatus.ERROR;
			errorMessage = e.toString();
			return;
		}
		
       
		status = TaskStatus.FINISHED;

	}

	private void processOneLine(String values[]) {

		int numOfColumns = Math.min(fieldOrder.length, values.length);

		String lineID = null, lineName = null, lineFormula = null;
		double lineMZ = 0, lineRT = 0;

		for (int i = 0; i < numOfColumns; i++) {                   
			if (fieldOrder[i].toString().matches(FieldItem.FIELD_ID.getName()))
				lineID = values[i];
			if (fieldOrder[i].toString().matches(FieldItem.FIELD_NAME.getName()))
				lineName = values[i];
			if (fieldOrder[i].toString().matches(FieldItem.FIELD_FORMULA.getName()))
				lineFormula = values[i];
			if (fieldOrder[i].toString().matches(FieldItem.FIELD_MZ.getName()))
				lineMZ = Double.parseDouble(values[i]);
			if (fieldOrder[i].toString().matches(FieldItem.FIELD_RT.getName())){
				lineRT = Double.parseDouble(values[i])*60;
                        }
		}	
                
                for (PeakListRow peakRow : peakList.getRows()) {

			boolean mzOK = (Math.abs(peakRow.getMZ() - lineMZ) < mzTolerance);
			boolean rtOK = (Math.abs(peakRow.getRT() - lineRT) < rtTolerance);
			
			if (mzOK && rtOK) {
                            String name = peakRow.getName();
                            if(name.matches(".*nknown.*")){
				peakRow.setName(lineName);
                            }else{
                                String allNames = peakRow.getAllNames();
                                if(allNames != null){
                                    peakRow.setAllNames(allNames+ " // " + lineName);
                                }else{
                                    peakRow.setAllNames(lineName);
                                }
                            }

			}
		}

	}

}
