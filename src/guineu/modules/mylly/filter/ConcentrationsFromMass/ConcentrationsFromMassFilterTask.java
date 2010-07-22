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
package guineu.modules.mylly.filter.ConcentrationsFromMass;

import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class ConcentrationsFromMassFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private SimpleGCGCDataset dataset;
    private double progress = 0.0;

    public ConcentrationsFromMassFilterTask(SimpleGCGCDataset dataset) {
        this.dataset = dataset;
    }

    public String getTaskDescription() {
        return "Getting Concentrations filter... ";
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
        status = TaskStatus.PROCESSING;
        try {

            SimpleGCGCDataset newDataset = (SimpleGCGCDataset) dataset.clone();
            newDataset.setDatasetName(newDataset.getDatasetName() + "- Filtered");
            for (PeakListRow row : newDataset.getAlignment()) {
                if (((SimplePeakListRowGCGC) row).getMass() >= 0) {

                    String compoundName = (String) row.getVar("getName");
                    List<StandardCompoundsEnum> values = new ArrayList<StandardCompoundsEnum>();
                    for (StandardCompoundsEnum s : StandardCompoundsEnum.values()) {
                        if (compoundName.compareTo(s.getName()) == 0) {
                            values.add(s);
                        }
                    }
                    StandardCompoundsEnum val = null;
                    if (values.size() > 0) {
                        val = values.get(0);
                        if (values.size() > 1) {
                            double mass = (Double) row.getVar("getMass");
                            for (StandardCompoundsEnum s : values) {
                                if (mass == s.getMass()) {
                                    val = s;
                                }
                            }
                        }
                    }
                    if (val != null) {
                        for (String name : newDataset.getAllColumnNames()) {
                            double concentration = ((SimplePeakListRowGCGC) row).getPeak(name);
                            double newConcentration = val.getSumIntensity() * (concentration / val.getIntensity());
                            if (newConcentration != Double.POSITIVE_INFINITY) {
                                ((SimplePeakListRowGCGC) row).setPeak(name, newConcentration);
                            } else {
                                ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                            }
                        }
                    } else {
                        ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                    }
                }
            }

            GUIUtils.showNewTable(newDataset, true);
           
            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            Logger.getLogger(ConcentrationsFromMassFilterTask.class.getName()).log(Level.SEVERE, null, ex);
            status = TaskStatus.ERROR;
        }
    }

    public String getName() {
        return "Filter Getting Concentrations";
    }
}
