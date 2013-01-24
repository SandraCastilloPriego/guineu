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
package guineu.modules.filter.dataselection;

import guineu.main.GuineuCore;
import guineu.modules.GuineuModuleCategory;
import guineu.modules.GuineuProcessingModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *
 * @author scsandra
 */
public class DataSelectionModule implements GuineuProcessingModule {

        public static final String MODULE_NAME = "Data selection";

        public ParameterSet getParameterSet() {
                return null;
        }

        @Override
        public String toString() {
                return MODULE_NAME;
        }

        public Task[] runModule(ParameterSet parameters) {
                SelectionBar bar = new SelectionBar(GuineuCore.getDesktop());
                JFrame frame = GuineuCore.getDesktop().getMainFrame();
                frame.add(bar, BorderLayout.EAST);
                frame.validate();
                return null;
        }

        public GuineuModuleCategory getModuleCategory() {
                return GuineuModuleCategory.FILTERING;
        }

        public String getIcon() {
                return "icons/selection.png";
        }

        public boolean setSeparator() {
                return false;
        }
}
