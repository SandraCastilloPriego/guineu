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
package guineu.modules.configuration.tables.LCMS;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.datamodels.LCMSColumnName;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class LCMSColumnsViewParameters extends SimpleParameterSet {

    public static final Parameter columnSelection = new SimpleParameter(
            ParameterType.MULTIPLE_SELECTION, "View Columns",
            "View Columns", null, LCMSColumnName.values());

    public LCMSColumnsViewParameters() {
        super(new Parameter[]{columnSelection});
    }
}
