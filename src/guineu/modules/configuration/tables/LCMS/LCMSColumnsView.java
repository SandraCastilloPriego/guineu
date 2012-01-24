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
package guineu.modules.configuration.tables.LCMS;

import guineu.data.datamodels.DatasetLCMSDataModel;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.internalframe.DataInternalFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.TableModel;

/**
 *
 * @author scsandra
 */
public class LCMSColumnsView implements GuineuProcessingModule {

        public static final String MODULE_NAME = "LCMS Table View";
        private SimpleParameterSet parameters = GuineuCore.getLCMSColumnsParameters();

        public ParameterSet getParameterSet() {
                return parameters;
        }

        public String toString() {
                return "LCMS Table View";
        }

        public Task[] runModule(ParameterSet parameters) {
                JInternalFrame[] frames = GuineuCore.getDesktop().getInternalFrames();
                for (int i = 0; i < frames.length; i++) {
                        try {
                                JTable table = ((DataInternalFrame) frames[i]).getTable();
                                TableModel model = table.getModel();
                                if (model.getClass().toString().contains("DatasetLCMSDataModel")) {
                                        ((DatasetLCMSDataModel) model).setParameters();
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

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.CONFIGURATION;
        }

        public String getIcon() {
                return "icons/tablesconflcms.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
