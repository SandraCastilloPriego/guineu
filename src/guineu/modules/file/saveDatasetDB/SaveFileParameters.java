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
package guineu.modules.file.saveDatasetDB;

import guineu.database.retrieve.impl.OracleRetrievement;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.StringParameter;

public class SaveFileParameters extends SimpleParameterSet {

        private static Object[] studies = OracleRetrievement.getStudies();
        public static final StringParameter author = new StringParameter(
                "Author: ",
                "Author of the processing of the dataset");
        public static final StringParameter name = new StringParameter(
                "Dataset name: ",
                "Name of the dataset");
        public static final ComboParameter studyId = new ComboParameter(
                "Studies: ",
                "Select study", studies);
        public static final StringParameter units = new StringParameter(
                "Dataset units: ",
                "Units of the dataset");
        public static final FileNameParameter parameters = new FileNameParameter(
                "Dataset parameters: ",
                "Parameter file of the dataset");

        public SaveFileParameters() {
                super(new Parameter[]{name, author, studyId, units, parameters});
        }
}
