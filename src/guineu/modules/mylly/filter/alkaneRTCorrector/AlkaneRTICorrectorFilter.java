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
package guineu.modules.mylly.filter.alkaneRTCorrector;

import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.taskcontrol.TaskListener;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.parameters.ParameterSet;
import java.util.ArrayList;

/**
 *
 * @author scsandra
 */
public class AlkaneRTICorrectorFilter implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private AlkaneRTICorrectorParameters parameters;

        public AlkaneRTICorrectorFilter() {
                parameters = new AlkaneRTICorrectorParameters();
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuSeparator(GuineuMenu.MYLLY);
                desktop.addMenuItem(GuineuMenu.MYLLY, "Alkane RTI Corrector Filter..",
                        "Alkane RTI Corrector Filter", KeyEvent.VK_A, this, null, "icons/help.png");

        }

        public void taskStarted(Task task) {
                logger.info("Running Alkane RTI Corrector Filter");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished Alkane RTI Corrector Filter ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while Alkane RTI Corrector Filtering .. ";
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {
                try {
                        ExitCode exitcode = parameters.showSetupDialog();
                        if (exitcode == ExitCode.OK) {
                                runModule();
                        }
                } catch (Exception exception) {
                }
        }

        public ParameterSet getParameterSet() {
                return this.parameters;
        }

        public String toString() {
                return "Alkane RTI Corrector Filter";
        }

        public Task[] runModule() {

                Dataset[] datasets = desktop.getSelectedDataFiles();
                List<GCGCData> newDatasets = new ArrayList<GCGCData>();

                for (int i = 0; i < datasets.length; i++) {
                        GCGCDatum[][] datum = ((SimpleGCGCDataset) datasets[i]).toArray();
                        List<GCGCDatum> datumList = new ArrayList<GCGCDatum>();
                        for (GCGCDatum data : datum[0]) {
                                datumList.add(data.clone());
                        }
                        newDatasets.add(new GCGCData(datumList, datasets[i].getDatasetName()));
                }

                // prepare a new group of tasks
                Task tasks[] = new AlkaneRTICorrectorFilterTask[1];

                tasks[0] = new AlkaneRTICorrectorFilterTask(newDatasets, parameters);

                GuineuCore.getTaskController().addTasks(tasks);

                return tasks;



        }
}
