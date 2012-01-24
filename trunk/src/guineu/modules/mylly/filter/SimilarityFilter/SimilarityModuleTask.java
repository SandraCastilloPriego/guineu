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
package guineu.modules.mylly.filter.SimilarityFilter;

import guineu.data.DatasetType;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class SimilarityModuleTask extends AbstractTask {

        private SimpleGCGCDataset dataset;
        private SimilarityParameters parameters;

        public SimilarityModuleTask(SimpleGCGCDataset dataset, SimilarityParameters parameters) {
                this.dataset = dataset;
                System.out.println(dataset.toString());
                this.parameters = parameters;
        }

        public String getTaskDescription() {
                return "Filtering files with Similarity Filter... ";
        }

        public double getFinishedPercentage() {
                return 1f;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        double minValue = parameters.getParameter(SimilarityParameters.minSimilarity).getValue();
                        String typeSimilarity = parameters.getParameter(SimilarityParameters.type).getValue();
                        String mode = Similarity.MEAN_SIMILARITY;
                        if (typeSimilarity.matches("maximum similarity")) {
                                mode = Similarity.MAX_SIMILARITY;
                        }

                        String typeAction = parameters.getParameter(SimilarityParameters.action).getValue();
                        String action = Similarity.REMOVE;
                        if (typeAction.matches("Rename")) {
                                action = Similarity.RENAME;
                        }
                        Similarity filter = new Similarity(minValue, action, mode);
                        SimpleGCGCDataset newAlignment = filter.actualMap(dataset);
                        newAlignment.setDatasetName(newAlignment.toString() + parameters.getParameter(SimilarityParameters.suffix).getValue());
                        newAlignment.setType(DatasetType.GCGCTOF);
                        // Shows the new data set
                        GUIUtils.showNewTable(newAlignment, true);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(SimilarityModuleTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }
}
