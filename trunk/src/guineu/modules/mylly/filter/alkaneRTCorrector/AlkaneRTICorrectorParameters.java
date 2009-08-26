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
package guineu.modules.mylly.filter.alkaneRTCorrector;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class AlkaneRTICorrectorParameters extends SimpleParameterSet {


	public static final Parameter suffix = new SimpleParameter(
            ParameterType.STRING, "Suffix: ",
            "Suffix", null, "-Alkane RTI Corrector Filter", null);


    public static final Parameter fileNames = new SimpleParameter(
            ParameterType.FILE_NAME, "Filter File: ",
            "Alkane RTI Corrector Filter File", null, "Alkane RTI Corrector Filter File", null);
	
	
	public AlkaneRTICorrectorParameters() {
		super(new Parameter[]{suffix, fileNames});
	}
}
