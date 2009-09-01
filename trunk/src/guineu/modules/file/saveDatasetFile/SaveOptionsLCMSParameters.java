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
package guineu.modules.file.saveDatasetFile;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class SaveOptionsLCMSParameters extends SimpleParameterSet {
    public static final Parameter id = new SimpleParameter(
            ParameterType.BOOLEAN, "ID",
            "ID", new Boolean(false));
    public static final Parameter mz = new SimpleParameter(
            ParameterType.BOOLEAN, "Average M/Z",
            "Average M/Z", new Boolean(true));
    public static final Parameter rt = new SimpleParameter(
            ParameterType.BOOLEAN, "Average RT",
            "Average RT", new Boolean(true));	
    public static final Parameter name = new SimpleParameter(
            ParameterType.BOOLEAN, "Lipid Name",
            "Lipid Name", new Boolean(true));
    public static final Parameter allNames = new SimpleParameter(
            ParameterType.BOOLEAN, "All Names",
            "All Names", new Boolean(false));
    public static final Parameter lipidClass = new SimpleParameter(
            ParameterType.BOOLEAN, "Lipid Class",
            "Lipid Class", new Boolean(true));
    public static final Parameter numFound = new SimpleParameter(
            ParameterType.BOOLEAN, "Num Found",
            "Num Found", new Boolean(true));
    public static final Parameter standard = new SimpleParameter(
            ParameterType.BOOLEAN, "Standard",
            "Standard", new Boolean(true));
    public static final Parameter alignment = new SimpleParameter(
            ParameterType.BOOLEAN, "Alignment",
            "Alignment", new Boolean(false));
    public static final Parameter FAComposition = new SimpleParameter(
            ParameterType.BOOLEAN, "FA Composition",
            "FA Composition", new Boolean(false));

    public SaveOptionsLCMSParameters() {
        super(new Parameter[]{id, mz, rt, name, allNames, lipidClass, numFound, standard, alignment, FAComposition});
    }
}
