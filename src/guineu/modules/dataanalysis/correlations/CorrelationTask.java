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
package guineu.modules.dataanalysis.correlations;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.stat.correlation.Covariance;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;

/**
 *
 * @author scsandra
 */
public class CorrelationTask extends AbstractTask {

        private Dataset[] dataset;
        private int progress = 0;
        private double cutoff = 0.05;
        private String correlationType;
        private boolean showPvalue;

        public CorrelationTask(Dataset[] dataset, CorrelationParameters parameters) {
                this.dataset = dataset;
                cutoff = parameters.getParameter(CorrelationParameters.cutoff).getValue();
                correlationType = parameters.getParameter(CorrelationParameters.correlationTypeSelection).getValue();
                showPvalue = parameters.getParameter(CorrelationParameters.show).getValue();
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
                                if (this.showPvalue) {
                                        newDataset.addColumnName(name + " - pValue");
                                }
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
                                String pValueName = name + " - pValue";
                                for (int i = 0; i < data2.getNumberRows(); i++) {
                                        RealMatrix matrix = getCorrelation(row, data2.getRow(i), data1.getAllColumnNames());
                                        PearsonsCorrelation correlation = null;
                                        if (this.correlationType.contains("Pearsons")) {
                                                correlation = new PearsonsCorrelation(matrix);
                                        } else if (this.correlationType.contains("Spearmans")) {
                                                SpearmansCorrelation scorrelation = new SpearmansCorrelation(matrix);
                                                correlation = scorrelation.getRankCorrelation();
                                        }
                                        if (correlation != null) {
                                                double c = correlation.getCorrelationMatrix().getEntry(1, 0);
                                                double p = correlation.getCorrelationPValues().getEntry(1, 0);
                                                String cp = String.valueOf(c);

                                                Color cell = Color.WHITE;
                                                if (p < cutoff) {
                                                        if (c < 0) {
                                                                cell = Color.green;
                                                        } else {
                                                                cell = Color.red;
                                                        }

                                                }
                                                int pColumnIndex = 0;
                                                if (this.showPvalue) {
                                                        newDataset.getRow(i).setPeak(pValueName, String.valueOf(p));
                                                        pColumnIndex = newDataset.getAllColumnNames().indexOf(pValueName) + 2;
                                                        newDataset.setCellColor(cell, i, pColumnIndex);
                                                }

                                                pColumnIndex = newDataset.getAllColumnNames().indexOf(name) + 2;
                                                newDataset.getRow(i).setPeak(name, cp);

                                                newDataset.setCellColor(cell, i, pColumnIndex);

                                        }

                                        if (this.correlationType.contains("Covariance")) {
                                                Covariance covariance = new Covariance(matrix);
                                                double c = covariance.getCovarianceMatrix().getEntry(1, 0);
                                                newDataset.getRow(i).setPeak(name, String.valueOf(c));
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

                List<Double[]> data = new ArrayList<Double[]>();
                for (int i = 0; i < sampleNames.size(); i++) {
                        try {
                                if ((Double) row.getPeak(sampleNames.elementAt(i)) != Double.NaN && (Double) row2.getPeak(sampleNames.elementAt(i)) != Double.NaN
                                        && (Double) row.getPeak(sampleNames.elementAt(i)) != 0.0 && (Double) row2.getPeak(sampleNames.elementAt(i)) != 0.0) {
                                        Double[] dat = new Double[2];
                                        dat[0] = (Double) row.getPeak(sampleNames.elementAt(i));
                                        dat[1] = (Double) row2.getPeak(sampleNames.elementAt(i));
                                        data.add(dat);
                                       // System.out.println(sampleNames.elementAt(i) + " - " + row.getPeak(sampleNames.elementAt(i)) + " - " + row2.getPeak(sampleNames.elementAt(i)));

                                }
                        } catch (Exception e) {
                                //System.out.println(row.getPeak(sampleNames.elementAt(i)) + " - " + row2.getPeak(sampleNames.elementAt(i)));
                                //e.printStackTrace();
                        }
                }

                double[][] dataMatrix = new double[data.size()][2];
                int count = 0;
                for (Double[] dat : data) {
                        dataMatrix[count][0] = dat[0];
                        dataMatrix[count++][1] = dat[1];
                }
                RealMatrix matrix = new BlockRealMatrix(dataMatrix);
                return matrix;

        }
}
