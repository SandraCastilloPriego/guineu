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
package guineu.modules.filter.report.qualityReport;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.StringParameter;
import guineu.parameters.parametersType.TextAreaParameter;
/**
 *
 * @author scsandra
 */
public class ReportParameters extends SimpleParameterSet {

        final static String[] ionMode = {"ESI+", "ESI-"};
        final static String[] sampleType = {"Serum human", "Serum mouse", "Tissue", "Cells", "Batch"};
        public static final FileNameParameter filename = new FileNameParameter(
                "Input File",
                "File name");
        public static final FileNameParameter outputFilename = new FileNameParameter(
                "HTML Output File",
                "HTML Output File name");
        public static final StringParameter date = new StringParameter(
                "Date",
                "Date");
        public static final StringParameter sampleSet = new StringParameter(
                "Sample set",
                "Sample set");
        public static final StringParameter injection = new StringParameter(
                "Injection volume",
                "Injection volume", "ul");
        public static final ComboParameter<String> ionModeCombo = new ComboParameter<String>(
                "Ion Mode",
                "Ion Mode", ionMode);
        public static final ComboParameter<String> typeCombo = new ComboParameter<String>(
                "Sample type",
                "Sample type", sampleType);
        public static final TextAreaParameter area = new TextAreaParameter(
                "Comments",
                "Comments");

        public ReportParameters() {
                super(new UserParameter[]{filename, outputFilename, date, sampleSet, ionModeCombo, injection, typeCombo, area});
        }
}
