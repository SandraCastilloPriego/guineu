/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.filter.transpose;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.main.GuineuCore;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class TransposeFilterTask extends AbstractTask {

        private double progress = 0.0f;
        private Dataset dataset;

        public TransposeFilterTask(Dataset dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Transpose Dataset filter... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        SimpleBasicDataset newDataset = new SimpleBasicDataset(dataset.getDatasetName() + "- transposed");
                        newDataset.addColumnName("Name");
                        setStatus(TaskStatus.PROCESSING);

                        NumberFormat formatter = new DecimalFormat("#.####");

                        List<String> newNames = new ArrayList<String>();
                        for (PeakListRow row : dataset.getRows()) {
                                String newName = " ";
                                int l = ((String) row.getVar("getName")).length();
                                try {
                                        switch (dataset.getType()) {
                                                case LCMS:
                                                        newName = ((String) row.getVar("getName")).substring(0, l) + " - " + (formatter.format((Double) row.getVar("getMZ"))).toString() + " - " + (formatter.format((Double) row.getVar("getRT"))).toString() + " - " + ((Double) row.getVar("getNumFound")).toString();
                                                        break;
                                                case GCGCTOF:
                                                        newName = ((String) row.getVar("getName")).substring(0, l) + " - " + (formatter.format((Double) row.getVar("getRT1"))).toString() + " - " + (formatter.format((Double) row.getVar("getRT2"))).toString() + " - " + (formatter.format((Double) row.getVar("getRTI"))).toString();
                                                        break;
                                                case BASIC:
                                                        newName = ((String) row.getVar("getName")).substring(0, l) + " - " + ((Integer) row.getVar("getID")).toString();
                                                        break;
                                        }
                                } catch (Exception e) {
                                        newName = ((String) row.getVar("getName")).substring(0, l) + " - " + ((Integer) row.getVar("getID")).toString();
                                }
                                newDataset.addColumnName(newName);

                                newNames.add(newName);
                        }
                        for (String samples : dataset.getAllColumnNames()) {
                                SimplePeakListRowOther row = new SimplePeakListRowOther();
                                row.setPeak("Name", samples);
                                newDataset.addRow(row);
                        }
                        int cont = 0;
                        for (PeakListRow row2 : dataset.getRows()) {

                                for (PeakListRow row : newDataset.getRows()) {
                                        row.setPeak(newNames.get(cont), String.valueOf(row2.getPeak((String) row.getPeak("Name"))));
                                }
                                cont++;
                        }
                        newDataset.setType(DatasetType.BASIC);
                        GuineuCore.getDesktop().AddNewFile(newDataset);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }
}
