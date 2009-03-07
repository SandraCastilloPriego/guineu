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
package guineu.data.impl;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class SimpleDataset implements Dataset {

    String datasetName;
    Vector<PeakListRow> PeakList;
    Vector<String> nameExperiments;
    DatasetType type;
    int numFixColumns;

    public SimpleDataset(String datasetName) {
        this.datasetName = datasetName;
        this.PeakList = new Vector<PeakListRow>();
        this.nameExperiments = new Vector<String>();
        type = DatasetType.LCMS;
    }

    public String getDatasetName() {
        return this.datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

    public void AddRow(PeakListRow peakListRow) {
        this.PeakList.addElement(peakListRow);
    }

    public void AddNameExperiment(String nameExperiment) {
        this.nameExperiments.addElement(nameExperiment);
    }

    public PeakListRow getRow(int i) {
        return this.PeakList.elementAt(i);
    }

    public Vector<PeakListRow> getRows() {
        return this.PeakList;
    }

    public int getNumberRows() {
        return this.PeakList.size();
    }

    public int getNumberCols() {
        return this.nameExperiments.size();
    }

    public Vector<String> getNameExperiments() {
        return this.nameExperiments;
    }

    public void setNameExperiments(Vector<String> experimentNames) {
        this.nameExperiments = experimentNames;
    }

    public DatasetType getType() {
        return type;
    }

    public void setType(DatasetType type) {
        this.type = type;
    }

    @Override
    public SimpleDataset clone() {
        SimpleDataset newDataset = new SimpleDataset(this.datasetName);
        for (String experimentName : this.nameExperiments) {
            newDataset.AddNameExperiment(experimentName);
        }
        for (PeakListRow peakListRow : this.PeakList) {
            newDataset.AddRow(peakListRow.clone());
        }
        newDataset.setType(this.type);
        return newDataset;
    }

    public int getNumberFixColumns() {
        return this.numFixColumns;
    }

    public void setNumberFixColumns(int columnNum) {
        this.numFixColumns = columnNum;
    }
}
