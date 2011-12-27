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
package guineu.modules.dataanalysis.heatmaps;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.IntegerParameter;
import guineu.util.dialogs.ExitCode;
import java.util.List;
import java.util.Vector;

public class HeatMapParameters extends SimpleParameterSet {

        public static final String[] fileTypes = {"pdf", "svg", "png", "fig"};
        public static final FileNameParameter fileName = new FileNameParameter(
                "Output name", "Select the path and name of the output file.");
        public static final ComboParameter<String> fileTypeSelection = new ComboParameter<String>(
                "Output file type", "Output file type", fileTypes, fileTypes[0]);
        public static final ComboParameter<String> grouping = new ComboParameter<String>(
                "Groups",
                "One sample parameter has to be selected to be used in the heat map. They can be defined in \"Project -> Set sample parameters\"",
                new String[0]);
        public static final ComboParameter<String> referenceGroup = new ComboParameter<String>(
                "Group for p-value calculation",
                "Name of the group that will be used to perform the t-test respect the rest of the groups",
                new String[0]);
        public static final ComboParameter<String> referencePheno = new ComboParameter<String>(
                "Phenotype of reference",
                "Name of the group that will be used as a reference from the sample parameters",
                new String[0]);
        public static final BooleanParameter scale = new BooleanParameter(
                "Scaling",
                "Scaling the data with the standard deviation of each column.", true);
        public static final BooleanParameter log = new BooleanParameter("Log",
                "Log scaling of the data", true);
        public static final BooleanParameter plegend = new BooleanParameter(
                "P-value legend", "Adds the p-value legend", true);
        public static final IntegerParameter star = new IntegerParameter(
                "Size p-value legend", "Size of the p-value legend", 5);
        public static final BooleanParameter showControlSamples = new BooleanParameter(
                "Show control samples",
                "Shows control samples if this option is selected", true);
        public static final IntegerParameter height = new IntegerParameter(
                "Height", "Height", 10);
        public static final IntegerParameter width = new IntegerParameter("Width",
                "Width", 10);
        public static final IntegerParameter columnMargin = new IntegerParameter(
                "Column margin", "Column margin", 10);
        public static final IntegerParameter rowMargin = new IntegerParameter(
                "Row margin", "Row margin", 10);

        public HeatMapParameters() {
                super(new Parameter[]{fileName, fileTypeSelection, grouping,
                                referenceGroup, referencePheno, scale, log,
                                showControlSamples, plegend, star, height, width, columnMargin,
                                rowMargin});
        }

        @Override
        public ExitCode showSetupDialog() {               
                Dataset dataset = GuineuCore.getDesktop().getSelectedDataFiles()[0];
                // Update the parameter choices
                List<String> choices = dataset.getParametersName();
                getParameter(HeatMapParameters.grouping).setChoices(choices.toArray(new String[0]));
                getParameter(HeatMapParameters.referenceGroup).setChoices(choices.toArray(new String[0]));

                Vector<String> paramValues = dataset.getParameterAvailableValues(choices.get(0));
                getParameter(HeatMapParameters.referencePheno).setChoices(paramValues.toArray(new String[0]));
                
                HeatmapSetupDialog dialog = new HeatmapSetupDialog(this, dataset);
                dialog.setVisible(true);
                return dialog.getExitCode();
        }
}
