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
package guineu.modules.configuration.tables.LCMS;

import guineu.data.datamodels.DatasetLCMSDataModel;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.desktop.preferences.ColumnsLCMSParameters;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.dialogs.ExitCode;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 *
 * @author scsandra
 */
public class LCMSColumnsView implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private SimpleParameterSet parameters;

        public LCMSColumnsView() {
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.CONFIGURATION, "LCMS Table View..",
                        "Configuration of view of the LC-MS table columns", KeyEvent.VK_L, this, null, "icons/conf1.png");
                parameters = GuineuCore.getLCMSColumnsParameters();
        }

        public void taskStarted(Task task) {
                logger.info("Running LCMS Table View");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished LCMS Table View ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while LCMS Table View  .. ";
                        logger.severe(msg);
                        desktop.displayErrorMessage(msg);

                }
        }

        public void actionPerformed(ActionEvent e) {

                ExitCode exitCode = parameters.showSetupDialog();
                if (exitCode != ExitCode.OK) {
                        return;
                }
                runModule();
        }

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public String toString() {
                return "LCMS Table View";
        }

        public Task[] runModule() {
                JInternalFrame[] frames = desktop.getInternalFrames();
                for (int i = 0; i < frames.length; i++) {
                        try {
                                JTable table = ((DataInternalFrame) frames[i]).getTable();
                                TableModel model = table.getModel();
                                if (model.getClass().toString().contains("DatasetLCMSDataModel")) {
                                        ((DatasetLCMSDataModel) model).setParameters();
                                }
                                table.setModel(model);
                                table.createDefaultColumnsFromModel();
                                table.revalidate();
                        } catch (Exception e) {
                        }
                }
                return null;
        }
}
