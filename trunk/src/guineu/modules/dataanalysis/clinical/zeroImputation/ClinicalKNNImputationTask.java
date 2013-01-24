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
package guineu.modules.dataanalysis.clinical.zeroImputation;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class ClinicalKNNImputationTask extends AbstractTask {

        private Dataset dataset;
        private double progress = 0.0;
        private int K;

        public ClinicalKNNImputationTask(Dataset dataset, ClinicalKNNImputationParameters parameters) {
                this.dataset = dataset;
                this.K = parameters.getParameter(ClinicalKNNImputationParameters.K).getValue();
        }

        public String getTaskDescription() {
                return "Zero imputation (KNN)... ";
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

                        // check if there is any zero in the each sample. If there is a zero it imputes it.
                        int count = 0;
                        for (PeakListRow row : dataset.getRows()) {
                                for (String sample : dataset.getAllColumnNames()) {
                                        try {
                                                if ((Double) row.getPeak(sample) == 0) {
                                                        impute(row);
                                                        break;
                                                }
                                        } catch (ClassCastException e) {
                                        }
                                }
                                progress = (double) count / dataset.getNumberRows();
                                count++;
                        }

                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(ClinicalKNNImputationTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }

        private void impute(PeakListRow target) {

                HashMap<PeakListRow, Double> scores = new HashMap<PeakListRow, Double>();
                for (PeakListRow row : dataset.getRows()) {
                        if (!row.equals(target)) {
                                scores.put(row, new Double(getScore(target, row)));
                        }
                }

                scores = sort(scores);
              //  Iterator<Map.Entry<PeakListRow, Double>> entries = scores.entrySet().iterator();
              //  List<PeakListRow> NNSamples = new ArrayList<PeakListRow>();

               /* for (int i = 0; i < this.K; i++) {
                        if (entries.hasNext()) {
                                Map.Entry<PeakListRow, Double> entry = entries.next();
                                NNSamples.add(entry.getKey());
                        }
                }

                if (!NNSamples.isEmpty()) {*/
                        for (String sample : dataset.getAllColumnNames()) {
                                double average = 0;
                                int count = 0;
                                if ((Double) target.getPeak(sample) == 0.0) {
                                        Iterator<Map.Entry<PeakListRow, Double>> entries = scores.entrySet().iterator();
                                        for (int i = 0; i < this.K; i++) {
                                                if (entries.hasNext()) {
                                                        Map.Entry<PeakListRow, Double> entry = entries.next();
                                                        PeakListRow rows = entry.getKey();
                                                        if ((Double) rows.getPeak(sample) == 0) {
                                                                i--;
                                                        } else {
                                                                try {                                                                        
                                                                        average += (Double) rows.getPeak(sample);
                                                                        count++;
                                                                } catch (Exception e) {
                                                                        e.printStackTrace();
                                                                }
                                                        }
                                                }
                                        }

                                        if (average != 0) {
                                                average /= count;
                                        }
                                        target.setPeak(sample, average);
                                }
                        }
               // }
        }

        private double getScore(PeakListRow tvalue, PeakListRow svalue) {
                if (svalue == null) {
                        return Double.POSITIVE_INFINITY;
                }
                double score = 0;
                int count = 0;
                for (String sample : dataset.getAllColumnNames()) {
                        try {
                                double t = (Double) tvalue.getPeak(sample);
                                double s = (Double) svalue.getPeak(sample);

                                if (t != 0) {
                                        score += Math.pow((t - s), 2);
                                        count++;
                                }
                        } catch (ClassCastException e) {
                        }
                }
                return Math.sqrt(score) / count;
        }

        private HashMap<PeakListRow, Double> sort(HashMap<PeakListRow, Double> scores) {
                HashMap map = new LinkedHashMap();
                List mapKeys = new ArrayList(scores.keySet());
                List mapValues = new ArrayList(scores.values());
                scores.clear();
                TreeSet sortedSet = new TreeSet(mapValues);
                Object[] sortedArray = sortedSet.toArray();
                int size = sortedArray.length;
                for (int i = 0; i < size; i++) {
                        map.put(mapKeys.get(mapValues.indexOf(sortedArray[i])), sortedArray[i]);
                }
                return map;
        }
}
