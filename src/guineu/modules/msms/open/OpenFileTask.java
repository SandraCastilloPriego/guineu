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
package guineu.modules.msms.open;

import guineu.data.PeakListRow;
import guineu.data.DatasetType;
import guineu.data.parser.impl.LCMSParserCSV;
import guineu.data.parser.impl.LCMSParserXLS;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.data.parser.Parser;
import guineu.desktop.Desktop;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author scsandra
 */
public class OpenFileTask extends AbstractTask {

        private OpenMSMSFileParameters parameters;
        private Desktop desktop;
        private Parser parser;

        public OpenFileTask(Desktop desktop, OpenMSMSFileParameters parameters) {
                this.parameters = parameters;
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

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        this.openFile();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        public void openFile() {
                String fileDir = parameters.getParameter(OpenMSMSFileParameters.parameters).getValue().getAbsolutePath();

                if (fileDir.matches(".*xls")) {
                        try {
                                Parser parserName = new LCMSParserXLS(fileDir, null);
                                String[] sheetsNames = ((LCMSParserXLS) parserName).getSheetNames(fileDir);
                                for (String Name : sheetsNames) {
                                        try {
                                                if (getStatus() != TaskStatus.CANCELED) {
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
                                if (getStatus() != TaskStatus.CANCELED) {
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
                        if (getStatus() != TaskStatus.CANCELED) {
                                SimpleLCMSDataset dataset = (SimpleLCMSDataset) parser.getDataset();

                                SimpleBasicDataset otherDataset = modifyDataset(dataset);

                                desktop.AddNewFile(otherDataset);

                        }
                } catch (Exception exception) {
                        exception.printStackTrace();
                }
        }

        private SimpleBasicDataset modifyDataset(SimpleLCMSDataset dataset) {
                SimpleBasicDataset datasetOther = new SimpleBasicDataset(dataset.getDatasetName());
                datasetOther.setType(DatasetType.BASIC);
                datasetOther.addColumnName("m/z");
                datasetOther.addColumnName("rt");
                double margin = parameters.getParameter(OpenMSMSFileParameters.rtTolerance).getValue().getTolerance();
                int i = 1;
                int maxim = 1;
                double rtAverage = 0;
                try {
                        for (PeakListRow peakRow : dataset.getRows()) {
                                SimplePeakListRowLCMS row = (SimplePeakListRowLCMS) peakRow;
                                if (row.getID() != -2) {
                                        PeakListRow newRow = new SimplePeakListRowOther();
                                        newRow.setPeak("fragment" + i, String.valueOf(row.getMZ()));
                                        i++;
                                        row.setID(-2);
                                        rtAverage = row.getRT();
                                        for (PeakListRow peakRow2 : dataset.getRows()) {
                                                SimplePeakListRowLCMS row2 = (SimplePeakListRowLCMS) peakRow2;
                                                if (row2.getID() != -2) {
                                                        if (row.getRT() < row2.getRT() + margin && row.getRT() > row2.getRT() - margin) {
                                                                newRow.setPeak("fragment" + i, String.valueOf(row2.getMZ()));
                                                                i++;
                                                                row2.setID(-2);
                                                                rtAverage += row2.getRT();
                                                                rtAverage /= 2;
                                                        }
                                                }
                                        }
                                        rtAverage /= 60;
                                        newRow = orderRow(newRow);
                                        newRow.setPeak("rt", String.valueOf(rtAverage));
                                        datasetOther.addRow(newRow);
                                        if (i > maxim) {
                                                maxim = i;
                                        }
                                        i = 1;
                                }
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                for (int e = 1; e <= maxim; e++) {
                        datasetOther.addColumnName("fragment" + e);
                }

                return datasetOther;
        }

        private PeakListRow orderRow(PeakListRow newRow) {
                PeakListRow newOrderRow = new SimplePeakListRowOther();
                Object[] peaks = newRow.getPeaks(null);
                Double[] newPeaks = new Double[peaks.length];
                for (int i = 0; i < peaks.length; i++) {
                        newPeaks[i] = Double.valueOf(peaks[i].toString());
                }
                Arrays.sort(newPeaks, Collections.reverseOrder());
                for (int i = 1; i <= newPeaks.length; i++) {
                        newOrderRow.setPeak("fragment" + i, String.valueOf(newPeaks[i - 1]));
                }
                return newOrderRow;
        }
}
