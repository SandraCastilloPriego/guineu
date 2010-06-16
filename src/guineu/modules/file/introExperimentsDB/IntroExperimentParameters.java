/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.file.introExperimentsDB;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class IntroExperimentParameters extends SimpleParameterSet {

    public static final Parameter logPath = new SimpleParameter(
            ParameterType.STRING, "logPath",
            "Directori of the logs", null, "logPath", null);
    public static final Parameter CDFPath = new SimpleParameter(
            ParameterType.STRING, "CDFPath",
            "Directori of the CDFs files", null, "CDFPath", null);

    public IntroExperimentParameters() {
        super(new Parameter[]{ /*excelPath*/logPath, CDFPath});
    }
}
