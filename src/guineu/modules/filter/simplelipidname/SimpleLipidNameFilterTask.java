/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.filter.simplelipidname;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;

/**
 *
 * @author scsandra
 */
public class SimpleLipidNameFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private SimpleDataset dataset;

    public SimpleLipidNameFilterTask(Dataset dataset, Desktop desktop) {
        this.dataset = (SimpleDataset) dataset;
        this.desktop = desktop;

    }

    public String getTaskDescription() {
        return "Simplify LipidName filter... ";
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
            for (PeakListRow lipid : dataset.getRows()) {
                String lipidn = (String) lipid.getName();
                lipidn = lipidn.replaceFirst("^GPCho", "PC");
                lipidn = lipidn.replaceFirst("^LysoGPCho", "LysoPC");
                lipidn = lipidn.replaceFirst("^LysoGPEtn", "LysoPE");
                lipidn = lipidn.replaceFirst("^LysoGPA", "LysoPA");
                lipidn = lipidn.replaceFirst("^LysoGPSer", "LysoPS");
                lipidn = lipidn.replaceFirst("^LysoGPIns", "LysoPI");
                lipidn = lipidn.replaceFirst("^LysoGPGro", "LysoPG");
                lipidn = lipidn.replaceFirst("^GPEtn", "PE");
                lipidn = lipidn.replaceFirst("^GPIns", "PI");
                lipidn = lipidn.replaceFirst("^GPGro", "PG");
                lipidn = lipidn.replaceFirst("^GPA", "PA");
                lipidn = lipidn.replaceFirst("^GPSer", "PS");
                lipidn = lipidn.replaceFirst("^MAG", "MG");
                lipidn = lipidn.replaceFirst("^DAG", "DG");
                lipidn = lipidn.replaceFirst("^TAG", "TG");
                lipidn = lipidn.split(" ")[0];
                if (lipidn.indexOf(" - ") > 0) {
                    lipidn = lipidn.substring(0, lipidn.indexOf(" - "));
                }
                lipid.setName(lipidn);
            }
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
