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
package guineu.modules.mylly.alignment.scoreAligner;

import guineu.data.DatasetType;
import guineu.modules.mylly.alignment.scoreAligner.functions.Aligner;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.mylly.alignment.scoreAligner.functions.ScoreAligner;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class ScoreAlignmentTask extends AbstractTask {

        private Aligner aligner;

        public ScoreAlignmentTask(List<GCGCData> datasets, ScoreAlignmentParameters parameters) {
                aligner = (Aligner) new ScoreAligner(datasets, parameters);
        }

        public String getTaskDescription() {
                return "Aligning files... ";
        }

        public double getFinishedPercentage() {
                return aligner.getProgress();
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        SimpleGCGCDataset alignment = aligner.align();
                        alignment.setType(DatasetType.GCGCTOF);
                        GUIUtils.showNewTable(alignment, true);
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(ScoreAlignmentTask.class.getName()).log(Level.SEVERE, null, ex);
                        errorMessage = "There has been an error doing Score Alignment";
                        setStatus(TaskStatus.ERROR);
                }
        }
}
