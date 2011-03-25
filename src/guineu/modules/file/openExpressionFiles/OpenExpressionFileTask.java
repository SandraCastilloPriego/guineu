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
package guineu.modules.file.openExpressionFiles;

import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleExpressionDataset;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.ExpressionParserTSV;
import guineu.main.GuineuCore;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class OpenExpressionFileTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private String assayPath, featurePath, phenoPath, datasetName;
        private Parser parser = null;

        public OpenExpressionFileTask(SimpleParameterSet parameters) {
                this.datasetName = parameters.getParameter(OpenExpressionParameters.datasetName).getValue();
                this.assayPath = parameters.getParameter(OpenExpressionParameters.assayfilename).getValue().getAbsolutePath();
                this.featurePath = parameters.getParameter(OpenExpressionParameters.featurefilename).getValue().getAbsolutePath();
                this.phenoPath = parameters.getParameter(OpenExpressionParameters.phenofilename).getValue().getAbsolutePath();
        }

        public String getTaskDescription() {
                return "Saving Gene Expression Dataset... ";
        }

        public double getFinishedPercentage() {
                if (parser != null) {
                        return parser.getProgress();
                } else {
                        return 0.0f;
                }
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
                try {
                        status = TaskStatus.PROCESSING;
                        parser = new ExpressionParserTSV(assayPath, featurePath, phenoPath, datasetName);
                        parser.fillData();

                        Dataset dataset = (SimpleExpressionDataset) parser.getDataset();
                        System.out.println(dataset.getNumberCols() + " - " + dataset.getNumberRows());
                        GuineuCore.getDesktop().AddNewFile(dataset);

                        status = TaskStatus.FINISHED;
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                }
        }
}
