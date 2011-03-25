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
package guineu.modules.identification.CustomIdentification;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.MZToleranceParameter;
import guineu.parameters.parametersType.OrderParameter;
import guineu.parameters.parametersType.RTToleranceParameter;
import guineu.parameters.parametersType.StringParameter;

/**
 * 
 */
public class CustomDBSearchParameters extends SimpleParameterSet {

        public static final FileNameParameter dataBaseFile = new FileNameParameter(
                "Database file",
                "Name of file that contains information for peak identification");
        public static final StringParameter fieldSeparator = new StringParameter(
                "Field separator",
                "Character(s) used to separate fields in the exported file");
        public static final OrderParameter<FieldItem> fieldOrder = new OrderParameter<FieldItem>(
                "Field order",
                "Order of items in which they are readed from database file",
                FieldItem.values());
        public static final BooleanParameter ignoreFirstLine = new BooleanParameter(
                "Ignore first line",
                "Ignore the first line of database file", true);
        public static final MZToleranceParameter mzTolerance = new MZToleranceParameter(
                "m/z tolerance",
                "Tolerance mass difference to set an identification to one peak");
        public static final RTToleranceParameter rtTolerance = new RTToleranceParameter(
                "Time tolerance",
                "Maximum allowed difference of time to set an identification to one peak");
        public static final BooleanParameter MS = new BooleanParameter(
                "MSMS Identification",
                "MSMS Identification", new Boolean(true));

        public CustomDBSearchParameters() {
                super(new UserParameter[]{dataBaseFile, fieldSeparator, fieldOrder,
                                ignoreFirstLine, mzTolerance, rtTolerance, MS});
        }
}
