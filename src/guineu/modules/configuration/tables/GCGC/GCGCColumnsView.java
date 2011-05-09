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
package guineu.modules.configuration.tables.GCGC;

import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.dialogs.ExitCode;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author scsandra
 */
public class GCGCColumnsView implements GuineuModule, TaskListener, ActionListener {

        private Logger logger = Logger.getLogger(this.getClass().getName());
        private Desktop desktop;
        private SimpleParameterSet parameters;

        public GCGCColumnsView() {
                this.desktop = GuineuCore.getDesktop();
                desktop.addMenuItem(GuineuMenu.CONFIGURATION, "GCGC Table View..",
                        "Configuration of view of the GCxGC-MS table columns", KeyEvent.VK_G, this, null, "icons/conf2.png");
                parameters = GuineuCore.getGCGCColumnsParameters();
        }

        public void taskStarted(Task task) {
                logger.info("Running GCGC Table View");
        }

        public void taskFinished(Task task) {
                if (task.getStatus() == TaskStatus.FINISHED) {
                        logger.info("Finished GCGC Table View ");
                }

                if (task.getStatus() == TaskStatus.ERROR) {

                        String msg = "Error while GCGC Table View  .. ";
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

        @Override
        public String toString() {
                return "GCGC Table View";
        }

        public Task[] runModule() {
                JInternalFrame[] frames = desktop.getInternalFrames();
                for (int i = 0; i < frames.length; i++) {
                        try {

                                JTable table = ((DataInternalFrame) frames[i]).getTable();
                                DataTableModel model = (DataTableModel) table.getModel();

                                if (model.getClass().toString().contains("DatasetGCGCDataModel")) {
                                        ((DatasetGCGCDataModel) model).setParameters();
                                }
                                table.setModel(model);
                                table.createDefaultColumnsFromModel();

                                ToolTipManager.sharedInstance().unregisterComponent(table);
                                ToolTipManager.sharedInstance().unregisterComponent(table.getTableHeader());

                                table.revalidate();
                        } catch (Exception e) {
                        }
                }
                return null;
        }
}
