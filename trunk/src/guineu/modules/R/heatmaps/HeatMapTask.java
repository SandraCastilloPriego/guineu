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
package guineu.modules.R.heatmaps;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.database.intro.InDataBase;
import guineu.database.intro.impl.InOracle;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.components.FileUtils;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

/**
 *
 * @author scsandra
 */
public class HeatMapTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private InDataBase db;
        private String outputType;

        public HeatMapTask(Dataset dataset, ParameterSet parameters) {
                outputType = parameters.getParameter(HeatMapParameters.fileTypeSelection).getValue();
                String parameterName = parameters.getParameter(HeatMapParameters.selectionData).getValue();
                Dataset newDataset = this.modifyDataset(dataset, parameterName);
                db = new InOracle();
                db.WriteCommaSeparatedFile(newDataset, "temp.csv", null);
        }

        public String getTaskDescription() {
                return "Heat Map... ";
        }

        public double getFinishedPercentage() {

                return 0.0f;

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
                        String[] args = new String[1];
                        args[0] = "--vanilla";
                        System.out.println("Creating Rengine (with arguments)");
                        Rengine re = new Rengine(args, false, new TextConsole());
                        System.out.println("Rengine created, waiting for R");
                        if (!re.waitForR()) {
                                System.out.println("Cannot load R");
                                return;
                        }

                        re.eval("dataset1<-read.csv(file=\"temp.csv\",head=TRUE, sep=\",\")", false);
                        re.eval("dataset<-dataset1[,-c(1:15)]", false);
                        re.eval("rownames(dataset)<-paste(dataset1[,2], dataset1[,5], sep=\"_\")", false);
                        re.eval("library(gplots)", false);
                        re.eval("x<-data.matrix(dataset, rownames.force=NA)", false);
                        if (this.outputType.contains("pdf")) {
                                re.eval("pdf(\"temp.pdf\")", false);
                                re.eval("heatmap.2(x, margins=c(10,10), trace=\"none\", density.info=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                        } else if (this.outputType.contains("fig")) {
                                re.eval("xfig(\"temp.fig\", width = 7, height = 7, horizontal = FALSE, pointsize = 12)", false);
                                re.eval("heatmap.2(x, margins=c(10,10), trace=\"none\", density.info=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                                re.eval("system(\"fig2dev -L emf temp.fig > temp.emf\"", false);
                        }else if (this.outputType.contains("png")) {
                                re.eval("png(\"temp.png\",width=800,height=800)", false);
                                re.eval("heatmap.2(x, margins=c(10,10), trace=\"none\", density.info=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                        }else if (this.outputType.contains("svg")) {
                                re.eval("library(\"RSvgDevice\")", false);
                                re.eval("devSVG(\"temp.svg\")", false);
                                re.eval("heatmap.2(x, margins=c(10,10), trace=\"none\", density.info=\"none\", col=bluered(256))", false);
                                re.eval("dev.off()", false);
                        }
                     //   re.eval("quit(save=\"no\", status=0, runLast= FALSE)", false);
                        re.end();

                        status = TaskStatus.FINISHED;

                } catch (Exception e) {
                        status = TaskStatus.ERROR;

                        errorMessage = e.toString();
                        return;
                }
        }

        private Dataset modifyDataset(Dataset dataset, String parameterName) {
                Vector<String> columnNames = dataset.getAllColumnNames();
                Dataset newDataset = FileUtils.getDataset(dataset, "");
                List<String> controlNames = new ArrayList<String>();
                for (String name : columnNames) {
                        if (!dataset.getParametersValue(name, parameterName).contains("ontrol")) {
                                newDataset.addColumnName(name);
                        } else {
                                controlNames.add(name);
                        }
                }

                double average = 0;
                for (PeakListRow row : dataset.getRows()) {
                        if (row.isSelected()) {
                                PeakListRow newRow = row.clone();
                                newDataset.addRow(newRow);
                                for (String controlColumnName : controlNames) {
                                        average += (Double) newRow.getPeak(controlColumnName);
                                }
                                average /= controlNames.size();

                                for (String columnName : columnNames) {
                                        newRow.setPeak(columnName, ((Double) newRow.getPeak(columnName)) / average);
                                }
                        }
                }
                return newDataset;
        }

        class TextConsole implements RMainLoopCallbacks {

                public void rWriteConsole(Rengine re, String text, int oType) {
                        System.out.print(text);
                }

                public void rBusy(Rengine re, int which) {
                        System.out.println("rBusy(" + which + ")");
                }

                public String rReadConsole(Rengine re, String prompt, int addToHistory) {
                        System.out.print(prompt);
                        try {
                                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                                String s = br.readLine();
                                return (s == null || s.length() == 0) ? s : s + "\n";
                        } catch (Exception e) {
                                System.out.println("jriReadConsole exception: " + e.getMessage());
                        }
                        return null;
                }

                public void rShowMessage(Rengine re, String message) {
                        System.out.println("rShowMessage \"" + message + "\"");
                }

                public String rChooseFile(Rengine re, int newFile) {
                        FileDialog fd = new FileDialog(new Frame(), (newFile == 0) ? "Select a file" : "Select a new file", (newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
                        fd.show();
                        String res = null;
                        if (fd.getDirectory() != null) {
                                res = fd.getDirectory();
                        }
                        if (fd.getFile() != null) {
                                res = (res == null) ? fd.getFile() : (res + fd.getFile());
                        }
                        return res;
                }

                public void rFlushConsole(Rengine re) {
                }

                public void rLoadHistory(Rengine re, String filename) {
                }

                public void rSaveHistory(Rengine re, String filename) {
                }
        }
}
