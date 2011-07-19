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

import guineu.main.GuineuCore;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.ComboParameter;

public class HeatMapParameters extends SimpleParameterSet {

        public static final String[] fileTypes = {"pdf", "svg", "png", "fig"};
        public static final ParameterSelection selectionData = new ParameterSelection(GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName());
        public static final ComboParameter<String> fileTypeSelection = new ComboParameter<String>(
                "Output file type", "Output file type",
                fileTypes, fileTypes[0]);

        public HeatMapParameters() {
                super(new UserParameter[]{selectionData, fileTypeSelection});
        }
}
