/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.statistics.PCA;

import guineu.data.PeakListRow;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.taskcontrol.TaskStatus;
import java.util.logging.Logger;
import jmprojection.CDA;
import jmprojection.Preprocess;
import jmprojection.ProjectionStatus;

import org.jfree.data.xy.AbstractXYDataset;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 * 
 */
public class CDADataset extends AbstractXYDataset implements
        ProjectionPlotDataset {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private double[] component1Coords;
        private double[] component2Coords;
        private ProjectionPlotParameters parameters;
        private String[] selectedRawDataFiles;
        private PeakListRow[] selectedRows;
        private int[] groupsForSelectedRawDataFiles;
        private Object[] parameterValuesForGroups;
        int numberOfGroups;
        private String datasetTitle;
        private int xAxisDimension;
        private int yAxisDimension;
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private ProjectionStatus projectionStatus;

        public CDADataset(ProjectionPlotParameters parameters) {

                this.parameters = parameters;

                this.xAxisDimension = parameters.getParameter(ProjectionPlotParameters.xAxisComponent).getValue();
                this.yAxisDimension = parameters.getParameter(ProjectionPlotParameters.yAxisComponent).getValue();

                selectedRawDataFiles = GuineuCore.getDesktop().getSelectedDataFiles()[0].getAllColumnNames().toArray(new String[0]);
                selectedRows = GuineuCore.getDesktop().getSelectedDataFiles()[0].getRows().toArray(new PeakListRow[0]);

                datasetTitle = "Curvilinear distance analysis";

                // Determine groups for selected raw data files
                groupsForSelectedRawDataFiles = new int[selectedRawDataFiles.length];

                if (parameters.getParameter(ProjectionPlotParameters.coloringType).getValue() == ColoringType.NOCOLORING) {
                        // All files to a single group
                        for (int ind = 0; ind < selectedRawDataFiles.length; ind++) {
                                groupsForSelectedRawDataFiles[ind] = 0;
                        }

                        numberOfGroups = 1;
                }

                if (parameters.getParameter(ProjectionPlotParameters.coloringType).getValue() == ColoringType.COLORBYFILE) {
                        // Each file to own group
                        for (int ind = 0; ind < selectedRawDataFiles.length; ind++) {
                                groupsForSelectedRawDataFiles[ind] = ind;
                        }

                        numberOfGroups = selectedRawDataFiles.length;
                }

                if (parameters.getParameter(ProjectionPlotParameters.coloringType).getValue() == ColoringType.COLORBYPARAMETER) {
                        // Group files with same parameter value to same group
           /* Vector<Object> availableParameterValues = new Vector<Object>();
                        for (String rawDataFile : selectedSamples) {
                        String paramValue = parameters.getParamValue(rawDataFile);
                        if (!availableParameterValues.contains(paramValue)) {
                        availableParameterValues.add(paramValue);
                        }
                        }

                        for (int ind = 0; ind < selectedSamples.size(); ind++) {
                        String paramValue = parameters.getParamValue(selectedSamples.elementAt(ind));
                        groupsForSelectedRawDataFiles[ind] = availableParameterValues.indexOf(paramValue);
                        }
                        parameterValuesForGroups = availableParameterValues.toArray();*/

                        numberOfGroups = parameterValuesForGroups.length;
                }


        }

        public String toString() {
                return datasetTitle;
        }

        public String getXLabel() {
                if (xAxisDimension == 1) {
                        return "1st projected dimension";
                }
                if (xAxisDimension == 2) {
                        return "2nd projected dimension";
                }
                if (xAxisDimension == 3) {
                        return "3rd projected dimension";
                }
                return "" + xAxisDimension + "th projected dimension";
        }

        public String getYLabel() {
                if (yAxisDimension == 1) {
                        return "1st projected dimension";
                }
                if (yAxisDimension == 2) {
                        return "2nd projected dimension";
                }
                if (yAxisDimension == 3) {
                        return "3rd projected dimension";
                }
                return "" + yAxisDimension + "th projected dimension";
        }

        @Override
        public int getSeriesCount() {
                return 1;
        }

        @Override
        public Comparable getSeriesKey(int series) {
                return 1;
        }

        public int getItemCount(int series) {
                return component1Coords.length;
        }

        public Number getX(int series, int item) {
                return component1Coords[item];
        }

        public Number getY(int series, int item) {
                return component2Coords[item];
        }

        public String getRawDataFile(int item) {
                return selectedRawDataFiles[item];
        }

        public int getGroupNumber(int item) {
                return groupsForSelectedRawDataFiles[item];
        }

        public Object getGroupParameterValue(int groupNumber) {
                if (parameterValuesForGroups == null) {
                        return null;
                }
                if ((parameterValuesForGroups.length - 1) < groupNumber) {
                        return null;
                }
                return parameterValuesForGroups[groupNumber];
        }

        public int getNumberOfGroups() {
                return numberOfGroups;
        }

        public void run() {

                status = TaskStatus.PROCESSING;

                logger.info("Computing projection plot");

                // Generate matrix of raw data (input to CDA)

                double[][] rawData = new double[selectedRawDataFiles.length][selectedRows.length];
                for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
                        PeakListRow peakListRow = selectedRows[rowIndex];
                        for (int fileIndex = 0; fileIndex < selectedRawDataFiles.length; fileIndex++) {
                                String rawDataFile = selectedRawDataFiles[fileIndex];
                                try {
                                        double p = (Double) peakListRow.getPeak(rawDataFile);
                                        rawData[fileIndex][rowIndex] = p;
                                } catch (Exception e) {
                                }
                        }
                }

                int numComponents = xAxisDimension;
                if (yAxisDimension > numComponents) {
                        numComponents = yAxisDimension;
                }

                // Scale data and do CDA
                Preprocess.scaleToUnityVariance(rawData);
                CDA cdaProj = new CDA(rawData);
                cdaProj.iterate(100);

                if (status == TaskStatus.CANCELED) {
                        return;
                }

                double[][] result = cdaProj.getState();

                if (status == TaskStatus.CANCELED) {
                        return;
                }

                component1Coords = result[xAxisDimension - 1];
                component2Coords = result[yAxisDimension - 1];

                Desktop desktop = GuineuCore.getDesktop();
                ProjectionPlotWindow newFrame = new ProjectionPlotWindow(desktop, this,
                        parameters, this.datasetTitle);
                desktop.addInternalFrame(newFrame);

                status = TaskStatus.FINISHED;
                logger.info("Finished computing projection plot.");

        }

        public void cancel() {
                if (projectionStatus != null) {
                        projectionStatus.cancel();
                }
                status = TaskStatus.CANCELED;
        }

        public String getErrorMessage() {
                return errorMessage;
        }

        public TaskStatus getStatus() {
                return status;
        }

        public String getTaskDescription() {
                if ((parameters == null)) {
                        return "CDA projection";
                }
                return "CDA projection " + this.datasetTitle;
        }

        public double getFinishedPercentage() {
                if (projectionStatus == null) {
                        return 0;
                }
                return projectionStatus.getFinishedPercentage();
        }

        public Object[] getCreatedObjects() {
                return null;
        }
}
