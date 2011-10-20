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
/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
package guineu.modules.visualization.intensityboxplot;

import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.MultiChoiceParameter;
import guineu.util.PeakListRowSorter;
import guineu.util.SortingDirection;
import guineu.util.SortingProperty;
import guineu.util.dialogs.ExitCode;
import java.util.Arrays;
import java.util.List;

/**
 */
public class IntensityBoxPlotParameters extends SimpleParameterSet {

        public static final String rawDataFilesOption = "Sample";
        public static final MultiChoiceParameter<String> dataFiles = new MultiChoiceParameter<String>(
                "Samples", "Samples to display", new String[0]);
        public static final ComboParameter<Object> xAxisValueSource = new ComboParameter<Object>(
                "X axis value", "X axis value", new Object[]{rawDataFilesOption});
        public static final MultiChoiceParameter<PeakListRow> selectedRows = new MultiChoiceParameter<PeakListRow>(
                "Peak list rows", "Select peaks to display", new PeakListRow[0]);

        public IntensityBoxPlotParameters() {
                super(new Parameter[]{dataFiles, xAxisValueSource,
                                selectedRows});
        }

        @Override
        public ExitCode showSetupDialog() {

                String plDataFiles[] = GuineuCore.getDesktop().getSelectedDataFiles()[0].getAllColumnNames().toArray(new String[0]);
                PeakListRow plRows[] = GuineuCore.getDesktop().getSelectedDataFiles()[0].getRows().toArray(new PeakListRow[0]);
                Arrays.sort(plRows, new PeakListRowSorter(SortingProperty.MZ, SortingDirection.Ascending));
                PeakListRow selRows[] = GuineuCore.getDesktop().getSelectedDataFiles()[0].getSelectedRows().toArray(new PeakListRow[0]);
                getParameter(dataFiles).setChoices(plDataFiles);
                getParameter(dataFiles).setValue(plDataFiles);

                getParameter(selectedRows).setChoices(plRows);
                getParameter(selectedRows).setValue(selRows);

                List<String> sampleParameters = GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName();

                Object xAxisSources[] = new Object[sampleParameters.size() + 1];
                xAxisSources[0] = IntensityBoxPlotParameters.rawDataFilesOption;
                System.arraycopy(sampleParameters.toArray(new String[0]), 0, xAxisSources, 1,
                        sampleParameters.size());
                getParameter(IntensityBoxPlotParameters.xAxisValueSource).setChoices(
                        xAxisSources);

                return super.showSetupDialog();
        }
}
