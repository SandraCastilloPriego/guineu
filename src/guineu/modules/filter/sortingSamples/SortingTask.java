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
package guineu.modules.filter.sortingSamples;

import com.csvreader.CsvReader;
import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SortingTask extends AbstractTask {

        private Dataset peakList;
        private File fileName;
        private double progress = 0.0;

        /**
         * @param parameters
         * @param peakList
         */
        public SortingTask(SortingParameters parameters, Dataset peakList) {

                this.peakList = peakList;
                fileName = parameters.getParameter(SortingParameters.fileName).getValue();


        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public String getTaskDescription() {
                return "Sorting samples in " + peakList;
        }

        /**
         * @see java.lang.Runnable#run()
         */
        public void run() {

                setStatus(TaskStatus.PROCESSING);
                this.peakList.removeSampleNames();
                List<String> sampleList = ReadFile();
                progress = 0.25;
                double step = 0.75 / sampleList.size();
                for (int i = 0; i < sampleList.size(); i++) {
                        this.peakList.addColumnName(sampleList.get(i));
                        progress += step;
                }
                GuineuCore.getDesktop().getInternalFrames()[0].setVisible(false);
                progress = 1.0;
                setStatus(TaskStatus.FINISHED);


        }

        private List<String> ReadFile() {
                List<String> sampleNames = new ArrayList<String>();
                try {
                        CsvReader reader = new CsvReader(new FileReader(this.fileName));
                        while (reader.readRecord()) {
                                sampleNames.add(reader.getValues()[0]);
                        }
                        reader.close();
                } catch (IOException ex) {
                        Logger.getLogger(SortingTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                return sampleNames;
        }
}
