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
package guineu.modules.identification.simplelipidname;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class SimpleLipidNameFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private double progress = 0.0f;
    private SimpleLCMSDataset dataset;

    public SimpleLipidNameFilterTask(Dataset dataset, Desktop desktop) {
        this.dataset = (SimpleLCMSDataset) dataset;
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
                String lipidn = (String) lipid.getVar("getName");
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
                lipid.setVar("setName", lipidn);
            }
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
