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
package guineu.modules.file.saveOtherFile;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

public class SaveOtherParameters extends SimpleParameterSet {

	static Object[] objects = {"Excel", "csv"};
    public static final Parameter Otherfilename = new SimpleParameter(
            ParameterType.FILE_NAME,
            "Filename",
            "Name of exported peak list file name. If the file exists, it won't be overwritten.");
    public static final Parameter fieldSeparator = new SimpleParameter(
            ParameterType.STRING, "Field separator",
            "Character(s) used to separate fields in the exported file",
            (Object) ",");
       public static final Parameter type = new SimpleParameter(
            ParameterType.STRING, "type",
            "Type of file", null, null, objects, null);

    public SaveOtherParameters() {
        super(new Parameter[]{Otherfilename, type});
    }
}
