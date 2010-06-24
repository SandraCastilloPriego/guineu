/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.data.impl;

import guineu.modules.mylly.alignment.scoreAligner.functions.*;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentSorterFactory.SORT_MODE;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Collections;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author jmjarkko
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
    private Hashtable<String, Parameters> parameters;

    public SimpleGCGCDataset(String[] names, ScoreAlignmentParameters parameters, Aligner aligner) {
        this.nameExperiments = new Vector<String>();
        for (String experimentName : names) {
            this.nameExperiments.addElement(experimentName);
        }
        peakList = new ArrayList<PeakListRow>();
        lastSortMode = SORT_MODE.none;
        this.params = parameters;
        this.aligner = aligner;
        datasetName = "Alignment";
        this.parameters = new Hashtable<String, Parameters>();
    }

    public void addParameter(String experimentName, String parameterName, String parameterValue) {
        if (parameters.containsKey(experimentName)) {
            Parameters p = parameters.get(experimentName);
            p.addParameter(parameterName, parameterValue);
        } else {
            Parameters p = new Parameters();
            p.addParameter(parameterName, parameterValue);
            parameters.put(experimentName, p);
        }
    }

    public SimpleGCGCDataset(String datasetName) {
        this.nameExperiments = new Vector<String>();
        peakList = new ArrayList<PeakListRow>();
        lastSortMode = SORT_MODE.none;
        this.datasetName = datasetName;
    }

    public void setParameters(ScoreAlignmentParameters parameters) {
        this.params = parameters;
    }

    public void setAligner(Aligner aligner) {
        this.aligner = aligner;
    }

    public void setGCGCDataConcentration() {
        if ((Boolean) params.getParameterValue(ScoreAlignmentParameters.useConcentration)) {
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

    public void sort(SORT_MODE mode) {
        if (lastSortMode != mode && mode != AlignmentSorterFactory.SORT_MODE.none) {
            Collections.sort(peakList, AlignmentSorterFactory.getComparator(mode));
            lastSortMode = mode;
        }
    }

    /**
     * @return a list containing the AlignmentRows in this Alignment
     */
    public List<PeakListRow> getAlignment() {
        return peakList;
    }

    public GCGCDatum[][] toArray() {
        GCGCDatum tempArray[][] = new GCGCDatum[rowCount()][];
        GCGCDatum returnedArray[][] = new GCGCDatum[colCount()][rowCount()];
        for (int i = 0; i < rowCount(); i++) {
            tempArray[i] = ((List<GCGCDatum>) peakList.get(i).getVar("getDatumArray")).toArray(new GCGCDatum[0]);
        }
        for (int i = 0; i < rowCount(); i++) {
            for (int j = 0; j < colCount(); j++) {
                returnedArray[j][i] = tempArray[i][j];
            }
        }

        return returnedArray;
    }

    public List<PeakListRow> getSortedAlignment(SORT_MODE mode) {
        sort(mode);
        return getAlignment();
    }

    public ScoreAlignmentParameters getParameters() {
        return params;
    }

    public Aligner getAligner() {
        return aligner;
    }

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

    public GCGCDatum getPeak(int rowIx, int colIx) {
        if (rowIx < 0 || rowIx >= rowCount() || colIx < 0 || colIx >= colCount()) {
            throw new IndexOutOfBoundsException("indices out of bounds: rowIx = " +
                    rowIx + " valid range [0," + rowCount() + "]" +
                    " colIx = " + colIx + " valid range [0," + colCount() +
                    "]");
        }
        return ((List<GCGCDatum>) peakList.get(rowIx).getVar("getDatumArray")).get(colIx);
    }

    public int colCount() {
        return nameExperiments.size();
    }

    public int rowCount() {
        return peakList.size();
    }

    public String[] getColumnNames() {
        return (String[]) nameExperiments.toArray(new String[0]).clone();
    }

    public boolean addAlignmentRow(SimplePeakListRowGCGC row) {
        return peakList.add(row);
    }

    public void addAll(Collection<? extends SimplePeakListRowGCGC> rows) {
        for (SimplePeakListRowGCGC r : rows) {
            peakList.add(r);
        }
    }

    public String toString() {
        return datasetName;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public Vector<String> getNameExperiments() {
        return nameExperiments;
    }

    public int getNumberCols() {
        return nameExperiments.size();
    }

    public int getNumberRows() {
        return this.rowCount();
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

    public void AddNameExperiment(String nameExperiment) {
        this.nameExperiments.add(nameExperiment);
    }

    public void AddNameExperiment(String nameExperiment, int position) {
        this.nameExperiments.insertElementAt(nameExperiment, position);
    }

    public List<PeakListRow> getRows() {
        return this.peakList;
    }

    public Dataset clone() {
        SimpleGCGCDataset newDataset = new SimpleGCGCDataset(datasetName);
        for (String experimentName : this.nameExperiments) {
            newDataset.AddNameExperiment(experimentName);
        }
        newDataset.setAligner(aligner);
        newDataset.setParameters(params);
        for (PeakListRow peakListRow : this.peakList) {
            newDataset.AddRow(peakListRow.clone());
        }
        newDataset.setType(this.type);
        return newDataset;

    }

    public void AddRow(PeakListRow peakListRow) {
        this.peakList.add(peakListRow);
    }

    public String getInfo() {
        return infoDataset;
    }

    public void setInfo(String info) {
        this.infoDataset = info;
    }

    class Parameters {

        Hashtable<String, String> parameters;

        public Parameters() {
            parameters = new Hashtable<String, String>();
        }

        public void addParameter(String parameterName, String parameterValue) {
            parameters.put(parameterName, parameterValue);
        }
    }
}
