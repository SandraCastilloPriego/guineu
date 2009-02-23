/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.filter.relatedpeaks;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class RelatedPeaksTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private SimpleDataset dataset;

    public RelatedPeaksTask(Dataset dataset, Desktop desktop) {
        this.dataset = (SimpleDataset) dataset;
        this.desktop = desktop;

    }

    public String getTaskDescription() {
        return "Related Peaks filter... ";
    }

    public double getFinishedPercentage() {
        return progress;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void cancel() {
        status = TaskStatus.CANCELED;
    }

    public void run() {
        try {
            status = TaskStatus.PROCESSING;
            if (dataset.getType() == DatasetType.LCMS) {
                for (PeakListRow row : dataset.getRows()) {
                    if (!this.isGoodCandidate(row.getName()) || !row.getAllNames().matches(".*Deuterium.*") || !this.isRepeated(row)) {
                        ((SimplePeakListRowLCMS) row).setAllNames("");
                    }
                }
            }
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    private boolean isGoodCandidate(String name) {
        if (name.matches("unknown") || name.matches(".*TG.*") || name.matches(".*TAG.*") || name.matches(".*ChoE.*")) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isRepeated(PeakListRow row) {
        for (PeakListRow rowCompared : dataset.getRows()) {
            int rowComparedGroup = this.getGroup(rowCompared.getAllNames());
            int rowGroup = this.getGroup(row.getAllNames());
            if (row != rowCompared && (rowComparedGroup != -1) && (rowGroup != -1) && rowComparedGroup == rowGroup) {
                if(row.getMZ() > rowCompared.getMZ()){
                    if(this.getMean(row) < this.getMean(rowCompared)){
                        return true;
                    }
                }else{
                    if(this.getMean(row) > this.getMean(rowCompared)){
                        return true;
                    }
                }                
            }
        }
        return false;
    }

    private int getGroup(String identity) {       
         try {
            String str = identity.substring(identity.indexOf("Group")+5, identity.indexOf(" [(Deuterium"));       
            return Integer.valueOf(str);
        } catch (Exception e) {
            return -1;
        }
    }
    
    private double getMean(PeakListRow row){
        double mean = 0;
        Double[] peaks = (Double[]) row.getPeaks();
        for(Double concentration: peaks){
            mean += concentration;
        }
        return mean / peaks.length;
    }
}

