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
package guineu.modules.visualization.Rintensityboxplot;

import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.IntegerParameter;
import guineu.parameters.parametersType.MultiChoiceParameter;
import guineu.parameters.parametersType.StringParameter;
import guineu.util.PeakListRowSorter;
import guineu.util.SortingDirection;
import guineu.util.SortingProperty;
import guineu.util.dialogs.ExitCode;
import java.util.Arrays;
import java.util.List;

public class BoxPlotParameters extends SimpleParameterSet {

        public static final String rawDataFilesOption = "Sample";
        public static final String colorFilesOption = "No color";
        public static final String[] fileTypes = {"No export", "pdf", "png"};
        public static final FileNameParameter fileName = new FileNameParameter(
                "Output name", "Select the path and name of the output file.");
        public static final ComboParameter<String> fileTypeSelection = new ComboParameter<String>(
                "Output file type", "Output file type", fileTypes, fileTypes[0]);
        public static final MultiChoiceParameter<String> samples = new MultiChoiceParameter<String>(
                "Samples", "Samples to display", new String[0]);
        public static final ComboParameter<Object> xAxisValueSource = new ComboParameter<Object>(
                "X axis value", "X axis value", new Object[]{rawDataFilesOption});
        public static final ComboParameter<Object> colorValueSource = new ComboParameter<Object>(
                "Color factor", "Color factor", new Object[]{colorFilesOption});
        public static final MultiChoiceParameter<PeakListRow> selectedRows = new MultiChoiceParameter<PeakListRow>(
                "Peak list rows", "Select peaks to display", new PeakListRow[0]);
        public static final StringParameter name = new StringParameter("Title", "Title of the plot");
        public static final IntegerParameter height = new IntegerParameter("Height", "height of the box plot", new Integer(600));
        public static final IntegerParameter width = new IntegerParameter("Width", "width of the box plot", new Integer(800));


        public BoxPlotParameters() {
                super(new Parameter[]{fileName, fileTypeSelection, name, height, width, samples, xAxisValueSource, colorValueSource,
                                selectedRows});
        }

        @Override
        public ExitCode showSetupDialog() {
                try {
                        String selectedPeakLists[] = getParameter(samples).getValue();
                        if (selectedPeakLists != null && selectedPeakLists.length > 0) {
                                getParameter(samples).setValue(selectedPeakLists);
                        } else {
                                String plDataFiles[] = GuineuCore.getDesktop().getSelectedDataFiles()[0].getAllColumnNames().toArray(new String[0]);
                                getParameter(samples).setChoices(plDataFiles);
                                getParameter(samples).setValue(plDataFiles);
                        }


                        PeakListRow plRows[] = GuineuCore.getDesktop().getSelectedDataFiles()[0].getRows().toArray(new PeakListRow[0]);
                        Arrays.sort(plRows, new PeakListRowSorter(SortingProperty.MZ, SortingDirection.Ascending));
                        PeakListRow selRows[] = GuineuCore.getDesktop().getSelectedDataFiles()[0].getSelectedRows().toArray(new PeakListRow[0]);
                        getParameter(selectedRows).setChoices(plRows);
                        if (selRows != null && selRows.length > 0) {
                                getParameter(selectedRows).setValue(selRows);
                                getParameter(name).setValue(selRows[0].getName());
                        } else {
                                getParameter(name).setValue("Box and whisker plot of concentrations in each group");
                        }

                        List<String> sampleParameters = GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName();

                        Object xAxisSources[] = new Object[sampleParameters.size() + 1];
                        xAxisSources[0] = BoxPlotParameters.rawDataFilesOption;
                        System.arraycopy(sampleParameters.toArray(new String[0]), 0, xAxisSources, 1,
                                sampleParameters.size());
                        getParameter(BoxPlotParameters.xAxisValueSource).setChoices(
                                xAxisSources);

                        Object colorSources[] = new Object[sampleParameters.size() + 1];
                        colorSources[0] = BoxPlotParameters.colorFilesOption;
                        System.arraycopy(sampleParameters.toArray(new String[0]), 0, colorSources, 1,
                                sampleParameters.size());
                        getParameter(BoxPlotParameters.colorValueSource).setChoices(
                                colorSources);
                } catch (Exception e) {
                }

                return super.showSetupDialog();
        }
}
