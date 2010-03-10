/*
 * Copyright 2007-2008 VTT Biotechnology
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

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class ReportParameters extends SimpleParameterSet {
 

    final static String[] ionMode = {"ESI+", "ESI-"};

    final static String[] sampleType = {"Serum human", "Serum mouse", "Tissue", "Cells", "Batch"};

    public static final Parameter filename = new SimpleParameter(
            ParameterType.FILE_NAME,
            "Input File",
            "File name");
     public static final Parameter outputFilename = new SimpleParameter(
            ParameterType.FILE_NAME,
            "HTML Output File",
            "HTML Output File name");
    public static final Parameter date = new SimpleParameter(
            ParameterType.STRING, "Date",
            "Date", null,null,null, null);
    public static final Parameter sampleSet = new SimpleParameter(
            ParameterType.STRING, "Sample set",
            "Sample set", null,null,null, null);
    public static final Parameter injection = new SimpleParameter(
            ParameterType.STRING, "Injection volume",
            "Injection volume", "ul",null,null,null);
    public static final Parameter ionModeCombo = new SimpleParameter(
            ParameterType.STRING, "Ion Mode",
            "Ion Mode", null, null, ionMode, null);
    public static final Parameter typeCombo = new SimpleParameter(
            ParameterType.STRING, "Sample type",
            "Sample type", null, null, sampleType, null);

    public static final Parameter area = new SimpleParameter(
            ParameterType.TEXTAREA, "Comments",
            "Comments", null, null, null, null);


    public ReportParameters() {
        super(new Parameter[]{filename, outputFilename, date, sampleSet, ionModeCombo,injection, typeCombo, area});
    }

    
}
