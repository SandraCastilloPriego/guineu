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
package guineu.modules.mylly.filter.ConcentrationsFromMass;

import guineu.data.PeakListRow;
import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.main.GuineuCore;
import guineu.modules.mylly.datastruct.ComparablePair;
import guineu.modules.mylly.datastruct.Spectrum;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
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
            for (PeakListRow row : newDataset.getAlignment()) {
                if (((SimplePeakListRowGCGC) row).getMass() >= 0) {

                    int intensity = 0;

                    Spectrum sp = ((SimplePeakListRowGCGC) row).getSpectrum();

                    for (ComparablePair<Integer, Integer> list : sp.getPeakList()) {
                        System.out.println("first: " + list.getFirst());
                        if (list.getFirst() == ((SimplePeakListRowGCGC) row).getMass()) {
                            intensity = list.getSecond();
                            System.out.println("second: " + list.getSecond());
                            break;
                        }
                    }
                     System.out.println("---");

                    for (String name : newDataset.getNameExperiments()) {
                        double newConcentration = 0;
                        double concentration = ((SimplePeakListRowGCGC) row).getPeak(name);
                        double constant = concentration / intensity;
                        for (ComparablePair<Integer, Integer> list : sp.getPeakList()) {
                            newConcentration += (list.getSecond() * constant);
                        }
                        if (newConcentration != Double.POSITIVE_INFINITY) {
                            ((SimplePeakListRowGCGC) row).setPeak(name, newConcentration);
                        }else{
                             ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                        }
                    }
                }
            }

            DataTableModel model = new DatasetGCGCDataModel(newDataset);
            DataTable table = new PushableTable(model);
            table.formatNumbers(newDataset.getType());
            DataInternalFrame frame = new DataInternalFrame(newDataset.getDatasetName(), table.getTable(), new Dimension(800, 800));
            GuineuCore.getDesktop().addInternalFrame(frame);
            GuineuCore.getDesktop().AddNewFile(newDataset);
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
