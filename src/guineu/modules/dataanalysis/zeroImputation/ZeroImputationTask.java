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
package guineu.modules.dataanalysis.zeroImputation;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.Vector;
import org.apache.commons.math.random.RandomDataImpl;

/**
 *
 * @author scsandra
 */
public class ZeroImputationTask extends AbstractTask {

        private Dataset dataset;
        private double progress;
        private RandomDataImpl data;

        public ZeroImputationTask(Dataset dataset) {
                this.dataset = dataset;
                data = new RandomDataImpl();
        }

        public String getTaskDescription() {
                return "Zero imputation... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        for (PeakListRow row : dataset.getRows()) {
                                double min = getMinimun(row, dataset.getAllColumnNames());
                                double zeroVal = min * 0.5;
                                for (String name : dataset.getAllColumnNames()) {
                                        double randomVal = data.nextGaussian(zeroVal, 0.5);
                                        Double peak = (Double) row.getPeak(name);
                                        if (peak == 0) {
                                                row.setPeak(name, randomVal);
                                        }
                                }
                        }

                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        private double getMinimun(PeakListRow row, Vector<String> columnName) {
                double min = Double.MAX_VALUE;
                for (String name : columnName) {
                        double value = (Double) row.getPeak(name);
                        if (value > 0 && value < min) {
                                min = value;
                        }
                }
                return min;
        }
}
