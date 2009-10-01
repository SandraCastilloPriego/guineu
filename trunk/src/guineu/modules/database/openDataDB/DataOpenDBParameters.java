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

package guineu.modules.database.openDataDB;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;
import guineu.database.ask.DBask;

/**
 *
 * @author scsandra
 */
public class DataOpenDBParameters extends SimpleParameterSet{

	final static String[] projectName = DBask.getProjectList();
	final static String[] info = {"Sample Information", "Project Information", "Study Information", "Dataset Information"};
	final static String[] typeExperiment = {"LC-MS", "GCGC-Tof"};

	public static final Parameter project = new SimpleParameter(
            ParameterType.STRING, "Project",
            "Projects", null, null, projectName, null);

	public static final Parameter studies = new SimpleParameter(
            ParameterType.STRING, "Study",
            "Studies", null, null, projectName, null);

	public static final Parameter type = new SimpleParameter(
            ParameterType.STRING, "Type of Experiment",
            "Type of Experiment", null, null, typeExperiment, null);

	public static final Parameter information = new SimpleParameter(
            ParameterType.MULTIPLE_SELECTION, "Open",
            "Type of information that you want to get from the database", null, info);

	public DataOpenDBParameters() {
        super(new Parameter[]{project, studies, type, information});
    }
}
