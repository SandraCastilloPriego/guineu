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
package guineu.modules.file.openLCMSDatasetFile;

import guineu.data.parser.impl.LCMSParserCSV;
import guineu.data.parser.impl.LCMSParserXLS;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.parser.Parser;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.io.IOException;

/**
 *
 * @author scsandra
 */
public class OpenFileTask implements Task {

        private String fileDir;
        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private Parser parser;

        public OpenFileTask(String fileDir) {
                if (fileDir != null) {
                        this.fileDir = fileDir;
                }
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
                        status = TaskStatus.PROCESSING;
                        this.openFile();
                        status = TaskStatus.FINISHED;
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                        errorMessage = e.toString();
                        return;
                }
        }

        public void openFile() {
                if (fileDir.matches(".*xls")) {
                        try {
                                Parser parserName = new LCMSParserXLS(fileDir, null);
                                String[] sheetsNames = ((LCMSParserXLS) parserName).getSheetNames(fileDir);
                                for (String Name : sheetsNames) {
                                        try {
                                                if (status != TaskStatus.CANCELED) {
                                                        parser = new LCMSParserXLS(fileDir, Name);
                                                        parser.fillData();
                                                        this.open(parser);
                                                }
                                        } catch (Exception exception) {
                                                exception.printStackTrace();
                                        }
                                }
                        } catch (IOException ex) {
                                ex.printStackTrace();
                        }
                } else if (fileDir.matches(".*csv")) {
                        try {
                                if (status != TaskStatus.CANCELED) {
                                        parser = new LCMSParserCSV(fileDir);
                                        parser.fillData();
                                        this.open(parser);
                                }
                        } catch (Exception ex) {
                                ex.printStackTrace();
                        }
                }

        }

        public void open(Parser parser) {
                try {
                        if (status != TaskStatus.CANCELED) {
                                SimpleLCMSDataset dataset = (SimpleLCMSDataset) parser.getDataset();

                                //creates internal frame with the table
                                GUIUtils.showNewTable(dataset,true);
                        }
                } catch (Exception exception) {
                        // exception.printStackTrace();
                }
        }
}
