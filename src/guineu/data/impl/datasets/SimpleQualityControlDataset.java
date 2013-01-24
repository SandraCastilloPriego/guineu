/*
 * Copyright 2007-2013 VTT Biotechnology
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

import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class SimpleQualityControlDataset extends SimpleBasicDataset {

    private String date, sampleSet, ionMode, sampleType, comments = "", injection;
    
    List<PeakListRow> PeakListDB;
    List<String> nameExperimentsDB;

    public SimpleQualityControlDataset(String datasetName) {
        super(datasetName);
        this.PeakListDB = new ArrayList<PeakListRow>();
        this.nameExperimentsDB = new ArrayList<String>();
        type = DatasetType.QUALITYCONTROL;
    }

    public void setParameters(String date, String sampleSet, String ionMode, String injection, String sampleType, String comments) {
        this.date = date;
        this.sampleSet = sampleSet;
        this.ionMode = ionMode;
        this.injection = injection;
        this.sampleType = sampleType;
        this.comments = comments;
    }

    public void setRow(PeakListRow row){
        this.PeakListDB.add(row);
    }

    public void setAdditionalRow(PeakListRow newRow){
        for(PeakListRow row : PeakListDB){
            if(row.getPeak("1").toString().compareTo(newRow.getPeak("1").toString()) == 0){
                int cont = 1;
                for(Object peak : newRow.getPeaks(null)){
                    row.setPeak("*" + cont, peak.toString());
                    cont++;
                }
                break;
            }
        }
    }
    
    public List<PeakListRow> getRowsDB(){
        return this.PeakListDB;
    }

    public String getDate() {
        return date;
    }

    public String getSampleSet() {
        return sampleSet;
    }

    public String getIonMode() {
        return ionMode;
    }

    public String getInjection() {
        return injection;
    }

    public String getSampleType() {
        return sampleType;
    }

    public String getComments() {
        return comments;
    }
}
