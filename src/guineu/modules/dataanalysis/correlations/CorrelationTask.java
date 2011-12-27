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
package guineu.modules.dataanalysis.correlations;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.awt.Color;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;

/**
 *
 * @author scsandra
 */
public class CorrelationTask extends AbstractTask {

        private Dataset[] dataset;
        private int progress = 0;

        public CorrelationTask(Dataset[] dataset) {
                this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Performing correlation matrix... ";
        }

        public double getFinishedPercentage() {
                return (float) progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        Dataset data1 = dataset[0];
                        Dataset data2 = null;
                        if (dataset.length == 1) {
                                data2 = dataset[0];
                        } else if (dataset.length > 1) {
                                data2 = dataset[1];
                        }
                        Dataset newDataset = new SimpleBasicDataset("Correlation matrix");
                        newDataset.addColumnName("ID");

                        // Column names from the first dataset
                        for (PeakListRow row : data1.getRows()) {
                                String name = row.getID() + " - " + row.getName();
                                newDataset.addColumnName(name);
                        }

                        // Row names from the second dataset
                        for (int i = 0; i < data2.getNumberRows(); i++) {
                                PeakListRow row = data2.getRow(i);
                                PeakListRow newRow = new SimplePeakListRowOther();
                                newRow.setPeak("ID", row.getID() + " - " + row.getName());
                                newDataset.addRow(newRow);
                        }

                        for (PeakListRow row : data1.getRows()) {
                                String name = row.getID() + " - " + row.getName();
                                for (int i = 0; i < data2.getNumberRows(); i++) {
                                        RealMatrix matrix = getCorrelation(row, data2.getRow(i), data1.getAllColumnNames());
                                        PearsonsCorrelation correlation = new PearsonsCorrelation(matrix);
                                        double c = correlation.getCorrelationMatrix().getEntry(1, 0);
                                        double p = correlation.getCorrelationPValues().getEntry(1, 0);
                                        newDataset.getRow(i).setPeak(name, String.valueOf(c + "(" + p + ")"));
                                        if (p < 0.05) {
                                                if(c < 0){
                                                        newDataset.setCellColor(Color.green, i, (newDataset.getAllColumnNames().indexOf(name) + 2));
                                                }else{
                                                        newDataset.setCellColor(Color.red, i, (newDataset.getAllColumnNames().indexOf(name) + 2));
                                                }
                                        } else {
                                                newDataset.setCellColor(null, i, (newDataset.getAllColumnNames().indexOf(name) + 2));
                                        }
                                }
                        }
                        GUIUtils.showNewTable(newDataset, true);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(CorrelationTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }

        private RealMatrix getCorrelation(PeakListRow row, PeakListRow row2, Vector<String> sampleNames) {

                double[][] dataMatrix = new double[sampleNames.size()][2];
                for (int i = 0; i < sampleNames.size(); i++) {
                        try {
                                dataMatrix[i][0] = (Double) row.getPeak(sampleNames.elementAt(i));
                                dataMatrix[i][1] = (Double) row2.getPeak(sampleNames.elementAt(i));
                        } catch (Exception e) {
                                // System.out.println(row.getPeak(sampleNames.elementAt(i)) + " - " + row2.getPeak(sampleNames.elementAt(i)));
                                //  e.printStackTrace();
                        }
                }
                RealMatrix matrix = new BlockRealMatrix(dataMatrix);
                return matrix;

        }
}
