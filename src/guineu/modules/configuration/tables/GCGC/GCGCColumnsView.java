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
package guineu.modules.configuration.tables.GCGC;

import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTableModel;
import guineu.util.internalframe.DataInternalFrame;
import javax.swing.JInternalFrame;
import javax.swing.JTable;
import javax.swing.ToolTipManager;

/**
 *
 * @author scsandra
 */
public class GCGCColumnsView implements GuineuProcessingModule {

        public static final String MODULE_NAME = "GCGC Table View";
        private SimpleParameterSet parameters = GuineuCore.getGCGCColumnsParameters();

        public ParameterSet getParameterSet() {
                return parameters;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                JInternalFrame[] frames = GuineuCore.getDesktop().getInternalFrames();
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

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.CONFIGURATION;
        }

        public String getIcon() {
                return "icons/tablesconfgcgc.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
