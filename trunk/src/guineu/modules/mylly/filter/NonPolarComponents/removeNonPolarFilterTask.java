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
package guineu.modules.mylly.filter.NonPolarComponents;

import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.main.GuineuCore;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class removeNonPolarFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private double progress = 0.0f;
    private SimpleGCGCDataset dataset;

    public removeNonPolarFilterTask(SimpleGCGCDataset dataset) {
        this.dataset = dataset;
    }

    public String getTaskDescription() {
        return "Remove Non-Polar Compounds... ";
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
            SimpleGCGCDataset newAlignment = (SimpleGCGCDataset) dataset.clone();

            List<PeakListRow> removedRows = new ArrayList<PeakListRow>();
            for(PeakListRow row : newAlignment.getRows()){
                String spectrum = (String) row.getVar("getSpectrumString");
                if(spectrum.contains(" 73:")){
                    String value = spectrum.substring(spectrum.indexOf(" 73:")+4);
                    try{
                        value = value.substring(0, value.indexOf(" , "));
                    }catch(Exception e){
                        value = value.substring(0, value.length() - 1);
                    }

                    double doubleValue = Double.parseDouble(value);
                    if(doubleValue < 250){
                        removedRows.add(row);
                    }
                }else{
                    removedRows.add(row);
                }

            }

            for(PeakListRow row : removedRows){
                newAlignment.removeRow(row);
            }
            GuineuCore.getDesktop().AddNewFile(newAlignment);

            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
}
