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

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.SampleDescription;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Transcriptomics data set implementation.
 *
 * @author SCSANDRA
 */
public class SimpleExpressionDataset implements Dataset {

        private String datasetName;
        private List<PeakListRow> peakList;
        private Vector<String> sampleNames;
        private Vector<String> parameterNames;
        private Vector<String> metaDataNames;
        private Hashtable<String, SampleDescription> parameters;
        private DatasetType type;
        private String infoDataset = "";
        private int ID;
        private int numberRows = 0;
        Hashtable<String, String> sampleType;

        /**
         *
         * @param datasetName Name of the dataset
         */
        public SimpleExpressionDataset(String datasetName) {
                this.datasetName = datasetName;
                this.peakList = new ArrayList<PeakListRow>();
                this.sampleNames = new Vector<String>();
                this.metaDataNames = new Vector<String>();
                this.parameters = new Hashtable<String, SampleDescription>();
                this.parameterNames = new Vector<String>();
                this.sampleType = new Hashtable<String, String>();
                type = DatasetType.EXPRESSION;
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
                for (String experimentName : sampleNames) {
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

        public String getDatasetName() {
                return this.datasetName;
        }

        public void setDatasetName(String datasetName) {
                this.datasetName = datasetName;
        }

        public void addRow(PeakListRow peakListRow) {
                this.peakList.add(peakListRow);
        }

        public void addColumnName(String sampleName) {
                this.sampleNames.addElement(sampleName);
        }

        public void addColumnName(String columnName, int position) {
                this.sampleNames.insertElementAt(columnName, position);
        }

        public Vector<String> getAllColumnNames() {
                return this.sampleNames;
        }

        public PeakListRow getRow(int i) {
                return this.peakList.get(i);
        }

        public List<PeakListRow> getRows() {
                return this.peakList;
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

        public int getNumberCols() {
                return this.sampleNames.size();
        }

        public DatasetType getType() {
                return type;
        }

        public void setType(DatasetType type) {
                this.type = type;
        }

        public void removeRow(PeakListRow row) {
                try {
                        this.peakList.remove(row);
                } catch (Exception e) {
                        System.out.println("No row found");
                }
        }

        public String getInfo() {
                return infoDataset;
        }

        public void setInfo(String info) {
                this.infoDataset = info;
        }

        @Override
        public SimpleExpressionDataset clone() {
                SimpleExpressionDataset newDataset = new SimpleExpressionDataset(this.datasetName);
                for (String experimentName : this.sampleNames) {
                        newDataset.addColumnName(experimentName);
                        for (String parameterName : this.parameterNames) {
                                newDataset.addParameterValue(experimentName, parameterName, this.getParametersValue(experimentName, parameterName));
                        }

                }
                for (PeakListRow peakListRow : this.peakList) {
                        newDataset.addRow(peakListRow.clone());
                }
                newDataset.setType(this.type);

                newDataset.infoDataset = infoDataset;

                return newDataset;
        }

        @Override
        public String toString() {
                return this.getDatasetName();
        }

        public void setSampleType(String sampleName, String type) {
                this.sampleType.put(sampleName, type);
        }

        public String getSampleType(String sampleName) {
                return sampleType.get(sampleName);
        }

        public Vector<String> getMetaDataNames() {
                return this.metaDataNames;
        }

        public void setMetaDataNames(String name) {
                this.metaDataNames.add(name);
        }

        public List<PeakListRow> getSelectedRows() {
                List<PeakListRow> selectedRows = new ArrayList<PeakListRow>();
                for (PeakListRow row : this.getRows()) {
                        if (row.isSelected()) {
                                selectedRows.add(row);
                        }
                }
                return selectedRows;
        }

        public void removeSampleNames() {
                this.sampleNames.clear();
        }
}
