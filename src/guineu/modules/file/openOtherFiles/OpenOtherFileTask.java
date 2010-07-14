/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.file.openOtherFiles;

import guineu.data.parser.impl.OtherFilesParserCSV;
import guineu.data.Dataset;
import guineu.data.impl.SimpleBasicDataset;
import guineu.data.parser.Parser;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class OpenOtherFileTask implements Task {

        private String fileDir;
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private Desktop desktop;
        private Parser parser;

        public OpenOtherFileTask(String fileDir, Desktop desktop) {
                if (fileDir != null) {
                        this.fileDir = fileDir;
                }
                this.desktop = desktop;
        }

        public String getTaskDescription() {
                return "Opening File... ";
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
                        this.openFile();
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        errorMessage = e.toString();
                        return;
                }
        }

        public void openFile() {
                status = TaskStatus.PROCESSING;
                try {
                        if (status == TaskStatus.PROCESSING) {
                                parser = new OtherFilesParserCSV(fileDir);
                                parser.fillData();
                                Dataset dataset = (SimpleBasicDataset) parser.getDataset();
                                desktop.AddNewFile(dataset);
                        }
                } catch (Exception ex) {
                }

                status = TaskStatus.FINISHED;
        }
}
