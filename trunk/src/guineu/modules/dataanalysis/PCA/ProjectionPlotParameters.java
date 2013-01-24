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
package guineu.modules.dataanalysis.PCA;

import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.IntegerParameter;
import guineu.parameters.parametersType.MultiChoiceParameter;
import guineu.util.dialogs.ExitCode;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 * 
 */
public class ProjectionPlotParameters extends SimpleParameterSet {

        public static final MultiChoiceParameter<String> dataFiles = new MultiChoiceParameter<String>(
                "Data files", "Samples", new String[0]);
        public static final ColoringTypeParameter coloringType = new ColoringTypeParameter();
        public static final Integer[] componentPossibleValues = {1, 2, 3, 4, 5};
        public static final ComboParameter<Integer> xAxisComponent = new ComboParameter<Integer>(
                "X-axis component", "Component on the X-axis",
                componentPossibleValues);
        public static final ComboParameter<Integer> yAxisComponent = new ComboParameter<Integer>(
                "Y-axis component", "Component on the Y-axis",
                componentPossibleValues, componentPossibleValues[1]);
        public static final MultiChoiceParameter<PeakListRow> rows = new MultiChoiceParameter<PeakListRow>(
                "Peak list rows", "Peak list rows to include in calculation", new PeakListRow[0]);
        public static final BooleanParameter showLoadings = new BooleanParameter("Show loadings", "Shows the loadings of each lipid");

        public static final IntegerParameter components = new IntegerParameter("Number of PC's", "Number of PC's to be computed (0 -> computes all components)", 0);

        public ProjectionPlotParameters() {
                super(new UserParameter[]{dataFiles, rows, coloringType, xAxisComponent,
                                yAxisComponent, showLoadings, components});
        }

        @Override
        public ExitCode showSetupDialog() {

                String dataFileChoices[];
                if (GuineuCore.getDesktop().getSelectedDataFiles().length >= 1) {
                        dataFileChoices = GuineuCore.getDesktop().getSelectedDataFiles()[0].getAllColumnNames().toArray(new String[0]);
                } else {
                        dataFileChoices = new String[0];
                }

                PeakListRow rowChoices[];
                if (GuineuCore.getDesktop().getSelectedDataFiles().length >= 1) {
                        rowChoices = GuineuCore.getDesktop().getSelectedDataFiles()[0].getRows().toArray(new PeakListRow[0]);
                } else {
                        rowChoices = new PeakListRow[0];
                }

                getParameter(ProjectionPlotParameters.dataFiles).setChoices(
                        dataFileChoices);
                getParameter(ProjectionPlotParameters.rows).setChoices(rowChoices);

                return super.showSetupDialog();
        }
}
