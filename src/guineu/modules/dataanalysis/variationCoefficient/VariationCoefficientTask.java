/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.dataanalysis.variationCoefficient;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.datamodels.VariationCoefficientDataModel;
import guineu.data.impl.VariationCoefficientData;
import guineu.main.GuineuCore;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class VariationCoefficientTask extends AbstractTask {

        private Dataset[] datasets;
        private double progress;

        public VariationCoefficientTask(Dataset[] datasets) {
                this.datasets = datasets;
        }

        public String getTaskDescription() {
                return "Coefficient of variation... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        this.variationCoefficient();
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public void variationCoefficient() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        progress = 0.0f;
                        List<VariationCoefficientData> data = new ArrayList<VariationCoefficientData>();
                        for (Dataset dataset : datasets) {
                                VariationCoefficientData vcdata = new VariationCoefficientData();
                                vcdata.variationCoefficient = getvariationCoefficient(dataset);
                                vcdata.NumberIdentMol = getNumberIdentMol(dataset);
                                vcdata.datasetName = dataset.getDatasetName();
                                vcdata.numberMol = dataset.getNumberRows();
                                vcdata.numberExperiments = dataset.getNumberCols();
                                data.add(vcdata);
                        }

                        DataTableModel model = new VariationCoefficientDataModel(data);
                        DataTable table = new PushableTable(model);
                        table.formatNumbers(1);
                        DataInternalFrame frame = new DataInternalFrame("Coefficient of variation", table.getTable(), new Dimension(450, 450));
                        GuineuCore.getDesktop().addInternalFrame(frame);
                        frame.setVisible(true);

                        progress = 1f;

                } catch (Exception ex) {
                }
                setStatus(TaskStatus.FINISHED);
        }

        private int getNumberIdentMol(Dataset dataset) {
                int cont = 0;
                for (PeakListRow row : dataset.getRows()) {
                        if (!((String) row.getVar("getName")).toLowerCase().matches("unknown")) {
                                cont++;
                        }
                }
                return cont;
        }

        private double getvariationCoefficient(Dataset dataset) {
                DescriptiveStatistics superStats = new DescriptiveStatistics();
                DescriptiveStatistics stats = new DescriptiveStatistics();
                for (PeakListRow row : dataset.getRows()) {
                        stats.clear();
                        for (String experimentName : dataset.getAllColumnNames()) {
                                Object value = row.getPeak(experimentName);
                                if (value != null && value instanceof Double) {
                                        stats.addValue((Double) value);
                                } else {

                                        try {
                                                stats.addValue(Double.valueOf((String) value));
                                        } catch (Exception e) {
                                        }
                                }
                        }
                        if (stats.getMean() > 0) {
                                double value = stats.getStandardDeviation() / stats.getMean();
                                superStats.addValue(value);
                        }
                }
                return superStats.getMean();
        }
}
