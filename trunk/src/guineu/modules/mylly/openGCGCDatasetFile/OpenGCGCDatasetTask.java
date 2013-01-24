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
package guineu.modules.mylly.openGCGCDatasetFile;

import guineu.data.parser.impl.GCGCParserXLS;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.GCGCParserCSV;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.io.IOException;

/**
 *
 * @author scsandra
 */
public class OpenGCGCDatasetTask extends AbstractTask {

        private String fileDir;
        private Parser parser;

        public OpenGCGCDatasetTask(String fileDir) {
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

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        this.openFile();
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public void openFile() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        if (fileDir.matches(".*xls")) {
                                try {
                                        Parser parserName = new GCGCParserXLS(fileDir, null);
                                        String[] sheetsNames = ((GCGCParserXLS) parserName).getSheetNames(fileDir);
                                        for (String Name : sheetsNames) {
                                                try {
                                                        if (getStatus() != TaskStatus.CANCELED) {
                                                                parser = new GCGCParserXLS(fileDir, Name);
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
                                        if (getStatus() != TaskStatus.CANCELED) {
                                                parser = new GCGCParserCSV(fileDir);
                                                parser.fillData();
                                                this.open(parser);
                                        }
                                } catch (Exception ex) {
                                        ex.printStackTrace();
                                }
                        }
                } catch (Exception ex) {
                        ex.printStackTrace();
                }

                setStatus(TaskStatus.FINISHED);
        }

        public void open(Parser parser) {
                try {
                        if (getStatus() != TaskStatus.CANCELED) {
                                SimpleGCGCDataset dataset = (SimpleGCGCDataset) parser.getDataset();
                                GUIUtils.showNewTable(dataset, true);
                        }
                } catch (Exception exception) {
                        // exception.printStackTrace();
                }
        }
}
