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
package guineu.data.impl.datasets;

import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.*;
import guineu.data.DatasetType;
import guineu.modules.mylly.alignment.scoreAligner.functions.*;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentSorterFactory.SORT_MODE;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import guineu.data.Dataset;
import guineu.data.GCGCColumnName;
import guineu.data.PeakListRow;
import guineu.util.Range;
import java.util.Hashtable;
import java.util.Vector;

/**
 * GCxGC-MS data set implementation.
 *
 * @author scsandra
 */
public class SimpleGCGCDataset implements Dataset {

        private List<PeakListRow> peakList;
        private Vector<String> nameExperiments;
        private AlignmentSorterFactory.SORT_MODE lastSortMode;
        private ScoreAlignmentParameters params;
        private Aligner aligner;
        private String datasetName;
        private DatasetType type;
        private String infoDataset = "";
        private Hashtable<String, SampleDescription> parameters;
        private Vector<String> parameterNames;
        private int ID;
        private int numberRows = 0;

        /**
         *     
         * @param names Sample Names
         * @param parameters Alignment parameters
         * @param aligner Class which performed the alignment of the sample files to create this data set
         */
        public SimpleGCGCDataset(String[] names, ScoreAlignmentParameters parameters, Aligner aligner) {

                this.nameExperiments = new Vector<String>();
                for (String experimentName : names) {
                        this.nameExperiments.addElement(experimentName);
                }

                this.params = parameters;

                this.aligner = aligner;

                // Peak list
                peakList = new ArrayList<PeakListRow>();
                lastSortMode = SORT_MODE.none;

                // The data set name is "Alignment" when it is create as a result of the
                // alignment of different sample files
                datasetName = "Alignment";


                // SampleDescription to describe the samples from guineu.modules.configuration.parameters
                this.parameters = new Hashtable<String, SampleDescription>();
                this.parameterNames = new Vector<String>();
        }

        /**
         * 
         * @param datasetName Name of data set
         */
        public SimpleGCGCDataset(String datasetName) {
                this.nameExperiments = new Vector<String>();
                peakList = new ArrayList<PeakListRow>();
                lastSortMode = SORT_MODE.none;
                this.datasetName = datasetName;
                this.type = DatasetType.GCGCTOF;
                // SampleDescription to describe the samples from guineu.modules.configuration.parameters
                this.parameters = new Hashtable<String, SampleDescription>();
                this.parameterNames = new Vector<String>();
        }

        /**
         * Sets the aligment parameters.
         *
         * @param parameters Alignemnt parameters
         */
        public void setParameters(ScoreAlignmentParameters parameters) {
                this.params = parameters;
        }

        /**
         * Sets the class which performed the alignment to create this data set.
         *
         * @param aligner Aligner
         */
        public void setAligner(Aligner aligner) {
                this.aligner = aligner;
        }

        /**
         * Returns the class which performed the alignment to create this data set.
         * 
         * @return Aligner
         */
        public Aligner getAligner() {
                return aligner;
        }

        /**
         * It sets for each class GCGCDatum inside the data set the option "setUseConcentration()" as true or
         * false depending on the alignemnt parameters and the amount of concentration.
         *
         * When the check box int he alignment parameters is checked the data set will show
         * concentrations instead areas. It only will use concentrations when the amount is
         * larger than 0.
         *
         * @see guineu.modules.mylly.datastruct.GCGCDatum
         *
         */
        public void setGCGCDataConcentration() {
                if ((Boolean) params.getParameter(ScoreAlignmentParameters.useConcentration).getValue()) {
                        for (PeakListRow row : peakList) {
                                for (GCGCDatum data : (List<GCGCDatum>) row.getVar("getDatumArray")) {
                                        if (data.getConcentration() > 0) {
                                                data.setUseConcentration(true);
                                        } else {
                                                data.setUseConcentration(false);
                                        }
                                }
                        }
                } else {
                        for (PeakListRow row : peakList) {
                                for (GCGCDatum data : (List<GCGCDatum>) row.getVar("getDatumArray")) {
                                        data.setUseConcentration(false);
                                }
                        }
                }
        }

        /**
         * Defines the order of the rows in the data set.
         * @see guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentSorterFactory
         *
         * @param mode Sort mode
         */
        public void sort(SORT_MODE mode) {
                if (lastSortMode != mode && mode != AlignmentSorterFactory.SORT_MODE.none) {
                        Collections.sort(peakList, AlignmentSorterFactory.getComparator(mode));
                        lastSortMode = mode;
                }
        }

        /**
         * Returns a list of every row in the data set.
         * 
         * @return List containing the AlignmentRows in this Alignment
         */
        public List<PeakListRow> getAlignment() {
                return peakList;
        }

        /**
         * The rows are lists of GCGCDatum. One GCGCDatum represents a "peak". Returns and array
         * bidimensional of peaks where the first dimension represents each row and the second
         * each peak.
         *
         * @see guineu.modules.mylly.datastruct.GCGCDatum
         *
         * @return Array bidimensional of peaks (GCGCDatum)
         */
        public GCGCDatum[][] toArray() {
                GCGCDatum tempArray[][] = new GCGCDatum[getNumberRows()][];
                GCGCDatum returnedArray[][] = new GCGCDatum[getNumberCols()][getNumberRows()];
                for (int i = 0; i < getNumberRows(); i++) {
                        tempArray[i] = ((List<GCGCDatum>) peakList.get(i).getVar("getDatumArray")).toArray(new GCGCDatum[0]);
                }
                for (int i = 0; i < getNumberRows(); i++) {
                        for (int j = 0; j < getNumberCols(); j++) {
                                returnedArray[j][i] = tempArray[i][j];
                        }
                }

                return returnedArray;
        }

        /**
         * Returns a list of rows sorted depending on the chosen sort mode.
         *
         * @param mode Sort mode
         * @return Sorted list of rows
         */
        public List<PeakListRow> getSortedAlignment(SORT_MODE mode) {
                sort(mode);
                return getAlignment();
        }

        /**
         * Returns the alignment parameters.
         *
         * @return alignment parameters
         */
        public ScoreAlignmentParameters getParameters() {
                return params;
        }

        /**
         * True or False depending on the distance value.
         * Returns true when the distance value of the compound in any row is not null.
         *
         * @return Returns true when the distance value of the compound in any row is not null
         */
        public boolean containsMainPeaks() {
                boolean contains = false;
                for (PeakListRow row : peakList) {
                        if (!((DistValue) row.getVar("getDistValue")).isNull()) {
                                contains = true;
                                break;
                        }
                }
                return contains;
        }

        /**
         * Returns a list of rows which contain mass value.
         *
         * @return List of rows
         */
        public List<SimplePeakListRowGCGC> getQuantMassAlignments() {
                List<SimplePeakListRowGCGC> QuantMassList = new ArrayList<SimplePeakListRowGCGC>();
                for (int i = 0; i < peakList.size(); i++) {
                        PeakListRow alignmentRow = peakList.get(i);
                        if ((Double) alignmentRow.getVar("getMass") > -1) {
                                QuantMassList.add((SimplePeakListRowGCGC) alignmentRow);
                        }
                }
                return QuantMassList;
        }

        /**
         * Returns the peak in the described position with the indexes rowIx and colIx.
         *
         * @param rowIx row index
         * @param colIx column index
         * @return peak in the described position
         */
        public GCGCDatum getPeak(int rowIx, int colIx) {
                if (rowIx < 0 || rowIx >= getNumberRows() || colIx < 0 || colIx >= getNumberCols()) {
                        throw new IndexOutOfBoundsException("indices out of bounds: rowIx = "
                                + rowIx + " valid range [0," + getNumberRows() + "]"
                                + " colIx = " + colIx + " valid range [0," + getNumberCols()
                                + "]");
                }
                return ((List<GCGCDatum>) peakList.get(rowIx).getVar("getDatumArray")).get(colIx);
        }

        /**
         * Returns an array of a copy of sample names.
         *
         * @return Array with the name of the samples
         */
        public String[] getColumnNames() {
                return (String[]) nameExperiments.toArray(new String[0]).clone();
        }

        /**
         * Adds a new row into the row list inside the class
         *
         * @param row Row
         * @return true when the row was added without problems
         */
        public boolean addAlignmentRow(SimplePeakListRowGCGC row) {
                return peakList.add(row);
        }

        /**
         * Add new rows into the data set. The rows can be in any kind of Collection class.
         *
         * @param rows Rows to be added.
         */
        public void addAll(Collection<? extends SimplePeakListRowGCGC> rows) {
                for (SimplePeakListRowGCGC r : rows) {
                        peakList.add(r);
                }
        }

        public void setID(int ID) {
                this.ID = ID;
        }

        public int getID() {
                return ID;
        }

        public void addParameterValue(String experimentName, String parameterName, String parameterValue) {
                if (parameters.containsKey(experimentName)) {
                        SampleDescription p = parameters.get(experimentName);
                        p.addParameter(parameterName, parameterValue);
                } else {
                        SampleDescription p = new SampleDescription();
                        p.addParameter(parameterName, parameterValue);
                        parameters.put(experimentName, p);
                }
                if (!this.parameterNames.contains(parameterName)) {
                        parameterNames.addElement(parameterName);
                }
        }

        public void deleteParameter(String parameterName) {
                for (String experimentName : nameExperiments) {
                        if (parameters.containsKey(experimentName)) {
                                SampleDescription p = parameters.get(experimentName);
                                p.deleteParameter(parameterName);
                        }
                }
                this.parameterNames.remove(parameterName);
        }

        public String getParametersValue(String experimentName, String parameterName) {
                if (parameters.containsKey(experimentName)) {
                        SampleDescription p = parameters.get(experimentName);
                        return p.getParameter(parameterName);
                } else {
                        return null;
                }
        }

        public Vector<String> getParameterAvailableValues(String parameter) {
                Vector<String> availableParameterValues = new Vector<String>();
                for (String rawDataFile : this.getAllColumnNames()) {
                        String paramValue = this.getParametersValue(rawDataFile, parameter);
                        if (!availableParameterValues.contains(paramValue) && !paramValue.isEmpty()) {
                                availableParameterValues.add(paramValue);
                        }
                }
                return availableParameterValues;
        }

        public Vector<String> getParametersName() {
                return parameterNames;
        }

        @Override
        public String toString() {
                return datasetName;
        }

        public String getDatasetName() {
                return datasetName;
        }

        public Vector<String> getAllColumnNames() {
                return nameExperiments;
        }

        public int getNumberCols() {
                return nameExperiments.size();
        }

        public int getNumberRows() {
                return this.peakList.size();
        }

        public int getNumberRowsdb() {
                return this.numberRows;
        }

        public void setNumberRows(int numberRows) {
                this.numberRows = numberRows;
        }

        public void setDatasetName(String name) {
                this.datasetName = name;
        }

        public DatasetType getType() {
                return this.type;
        }

        public void setType(DatasetType type) {
                this.type = type;
        }

        public PeakListRow getRow(int row) {
                return this.peakList.get(row);
        }

        public void removeRow(PeakListRow row) {
                this.peakList.remove(row);
        }

        public void addColumnName(String nameExperiment) {
                this.nameExperiments.add(nameExperiment);
        }

        public void addColumnName(String nameExperiment, int position) {
                this.nameExperiments.insertElementAt(nameExperiment, position);
        }

        public List<PeakListRow> getRows() {
                return this.peakList;
        }

        @Override
        public Dataset clone() {
                SimpleGCGCDataset newDataset = new SimpleGCGCDataset(datasetName);
                for (String experimentName : this.nameExperiments) {
                        newDataset.addColumnName(experimentName);
                        for (String parameterName : this.parameterNames) {
                                newDataset.addParameterValue(experimentName, parameterName, this.getParametersValue(experimentName, parameterName));
                        }
                }
                newDataset.setAligner(aligner);
                newDataset.setParameters(params);
                for (PeakListRow peakListRow : this.peakList) {
                        newDataset.addRow(peakListRow.clone());
                }
                newDataset.setType(this.type);

                newDataset.infoDataset = infoDataset;

                return newDataset;

        }

        public void addRow(PeakListRow peakListRow) {
                this.peakList.add(peakListRow);
        }

        public String getInfo() {
                return infoDataset;
        }

        public void setInfo(String info) {
                this.infoDataset = info;
        }

        public PeakListRow[] getRowsInsideRT1AndRT2Range(Range RT1Range, Range RT2Range) {
                List<PeakListRow> rows = new ArrayList<PeakListRow>();
                for (PeakListRow row : this.peakList) {
                        if (RT1Range.contains((Double) row.getVar(GCGCColumnName.RT1.getGetFunctionName()))
                                && RT2Range.contains((Double) row.getVar(GCGCColumnName.RT2.getGetFunctionName()))) {
                                rows.add(row);
                        }
                }
                return rows.toArray(new PeakListRow[0]);
        }

        public PeakListRow[] getRowsInsideRTRange(Range RTRange, int RT) {
                List<PeakListRow> rows = new ArrayList<PeakListRow>();
                for (PeakListRow row : this.peakList) {
                        if (RT == 1) {
                                if (RTRange.contains((Double) row.getVar(GCGCColumnName.RT1.getGetFunctionName()))) {
                                        rows.add(row);
                                }
                        } else {
                                if (RTRange.contains((Double) row.getVar(GCGCColumnName.RT2.getGetFunctionName()))) {
                                        rows.add(row);
                                }
                        }
                }
                return rows.toArray(new PeakListRow[0]);
        }

        public Range getRowsRTRange(int RT) {
                double min = Double.MAX_VALUE;
                double max = 0;
                for (PeakListRow row : this.peakList) {
                        if (RT == 1) {
                                double RTvalue = (Double) row.getVar(GCGCColumnName.RT1.getGetFunctionName());
                                if (RTvalue < min) {
                                        min = RTvalue;
                                }
                                if (RTvalue > max) {
                                        max = RTvalue;
                                }
                        } else {
                                double RTvalue = (Double) row.getVar(GCGCColumnName.RT2.getGetFunctionName());
                                if (RTvalue < min) {
                                        min = RTvalue;
                                }
                                if (RTvalue > max) {
                                        max = RTvalue;
                                }
                        }
                }
                return new Range(min, max);
        }
}
