/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.filter.report.qualityReport;

import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleOtherDataset;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class SimpleQualityControlDataset extends SimpleOtherDataset {

    private String date, sampleSet, ionMode, sampleType, comments = "", injection;
    
    Vector<PeakListRow> PeakListDB;
    Vector<String> nameExperimentsDB;

    public SimpleQualityControlDataset(String datasetName) {
        super(datasetName);
        this.PeakListDB = new Vector<PeakListRow>();
        this.nameExperimentsDB = new Vector<String>();
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
                for(Object peak : newRow.getPeaks()){
                    row.setPeak("*" + cont, peak.toString());
                    cont++;
                }
                break;
            }
        }
    }
    
    public Vector<PeakListRow> getRowsDB(){
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
