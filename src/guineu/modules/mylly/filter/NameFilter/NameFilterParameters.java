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
package guineu.modules.mylly.filter.NameFilter;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.StringParameter;

/**
 *
 * @author scsandra
 */
public class NameFilterParameters extends SimpleParameterSet {

        public static final StringParameter suffix = new StringParameter(
                "Suffix: ",
                "Suffix");
        public static final FileNameParameter fileNames = new FileNameParameter(
                "File Name: ",
                "Name of the File");

        public NameFilterParameters() {
                super(new UserParameter[]{suffix, fileNames});
        }
}
