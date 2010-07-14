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
package guineu.modules.database.openDataDB;

import guineu.data.parser.Parser;
import guineu.data.parser.impl.database.LCMSParserDataBase;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.parser.impl.database.GCGCParserDataBase;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;

/**
 *
 * @author scsandra
 */
public class OpenFileDBTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private Parser parser;

        public OpenFileDBTask(int datasetID, String type) {
                if (type.contains("GCxGC")) {
                        parser = new GCGCParserDataBase(datasetID);
                } else {
                        parser = new LCMSParserDataBase(datasetID);
                }

        }

        public String getTaskDescription() {
                return "Opening Dataset... ";
        }

        public double getFinishedPercentage() {
                return parser.getProgress();
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
                try {
                        status = TaskStatus.PROCESSING;
                        parser.fillData();
                        SimpleLCMSDataset dataset = (SimpleLCMSDataset) parser.getDataset();

                        //creates internal frame with the table
                        GUIUtils.showNewTable(dataset);
                        status = TaskStatus.FINISHED;
                } catch (Exception e) {
                        status = TaskStatus.ERROR;
                }
        }
}
