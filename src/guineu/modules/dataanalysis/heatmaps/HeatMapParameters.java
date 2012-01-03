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
import guineu.parameters.parametersType.DoubleParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.IntegerParameter;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.util.List;
import java.util.Vector;

public class HeatMapParameters extends SimpleParameterSet {

        public static final String[] fileTypes = {"No export", "pdf", "svg", "png", "fig"};
        public static final FileNameParameter fileName = new FileNameParameter(
                "Output name", "Select the path and name of the output file.");
        public static final ComboParameter<String> fileTypeSelection = new ComboParameter<String>(
                "Output file type", "Output file type", fileTypes, fileTypes[0]);
        public static final ComboParameter<String> timePoints = new ComboParameter<String>(
                "Time points",
                "One sample parameter has to be selected to be used in the heat map. They can be defined in \"Project -> Set sample parameters\"",
                new String[0]);
        public static final ComboParameter<String> phenotype = new ComboParameter<String>(
                "Phenotype",
                "Name of the group that will be used to perform the t-test respect the rest of the groups",
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
        public static final DoubleParameter height = new DoubleParameter(
                "Height avobe the heat map", "Height avobe the heat map", 2.0);
        public static final DoubleParameter heighthm = new DoubleParameter(
                "Height of the heat map", "Height of the heat map", 20.0);
        public static final DoubleParameter heightuhm = new DoubleParameter(
                "Height under heat map", "Height under heat map", 3.0);
        public static final DoubleParameter widthdendrogram = new DoubleParameter("Width of the dendrogram",
                "Width of the dendrogram", 1.0);
        public static final DoubleParameter widthhm = new DoubleParameter("Width of the heat map",
                "Width of the heat map", 10.0);
        public static final DoubleParameter columnMargin = new DoubleParameter(
                "Column margin", "Column margin", 10.0);
        public static final DoubleParameter rowMargin = new DoubleParameter(
                "Row margin", "Row margin", 10.0);
        public static final DoubleParameter clabelSize = new DoubleParameter(
                "Size of the column labels", "Size of the column labels", 1.0);
        public static final DoubleParameter rlabelSize = new DoubleParameter(
                "Size of the row labels", "Size of the row labels", 1.0);

        public HeatMapParameters() {
                super(new Parameter[]{fileName, fileTypeSelection, timePoints,
                                phenotype, scale, log,
                                plegend, star, height,heighthm, heightuhm, widthdendrogram, widthhm, columnMargin,
                                rowMargin, clabelSize, rlabelSize});
        }

        @Override
        public ExitCode showSetupDialog() {
                Dataset dataset = GuineuCore.getDesktop().getSelectedDataFiles()[0];
                // Update the parameter choices
                List<String> timePointChoices = dataset.getParametersName();
                String[] tpChoices = new String[timePointChoices.size() +1];
                tpChoices[0] = "No time Points";
                int cont = 1;
                for(String p : timePointChoices){
                        tpChoices[cont++] = p;
                }

                getParameter(HeatMapParameters.timePoints).setChoices(tpChoices);
                List<String> phenotypeChoices = dataset.getParametersName();
                getParameter(HeatMapParameters.phenotype).setChoices(phenotypeChoices.toArray(new String[0]));

                ParameterSetupDialog dialog = new ParameterSetupDialog(this, null);
                dialog.setVisible(true);
                return dialog.getExitCode();
        }
}
