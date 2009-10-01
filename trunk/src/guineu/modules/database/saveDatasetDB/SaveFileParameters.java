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

package guineu.modules.database.saveDatasetDB;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;
import guineu.database.ask.DBask;


public class SaveFileParameters extends SimpleParameterSet{
 	
	private static Object[] studies = DBask.getStudies();

    public static final Parameter author = new SimpleParameter(
            ParameterType.STRING, "Author: ",
            "Author of the processing of the dataset", null, "Author", null);

	public static final Parameter name = new SimpleParameter(
            ParameterType.STRING, "Dataset name: ",
            "Name of the dataset", null, "Name", null);

	public static final Parameter studyId = new SimpleParameter(
            ParameterType.STRING , "Studies: ",
            "Select study", "", studies);

	public static final Parameter units = new SimpleParameter(
            ParameterType.STRING, "Dataset units: ",
            "Units of the dataset", null, "Micromols/litre", null);

    public static final Parameter parameters = new SimpleParameter(
            ParameterType.FILE_NAME, "Dataset parameters: ",
            "Parameter file of the dataset", null, "Parameters", null);


    
    public SaveFileParameters() {
        super(new Parameter[] { name, author, studyId, units, parameters });
    }

}
