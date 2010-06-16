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
package guineu.modules.mylly.filter.SimilarityFilter;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class SimilarityParameters extends SimpleParameterSet {

	private static Object[] similarity = {"maximum similarity", "mean similarity"};

	private static Object[] actionDone = {"Remove", "Rename"};

	public static final Parameter suffix = new SimpleParameter(
            ParameterType.STRING, "Suffix: ",
            "Suffix", null, "-Similarity Filter", null);

	public static final Parameter type = new SimpleParameter(
            ParameterType.STRING , "Similarity used: ",
            "Similarity used", "", similarity);

	public static final Parameter action = new SimpleParameter(
            ParameterType.STRING , "Action: ",
            "Action", "", actionDone);

   	public static final Parameter minSimilarity = new SimpleParameter(
			ParameterType.DOUBLE, "Minimun similarity required:",
			"Minimun similarity required", "", new Double(0.0),
			new Double(0.0), null);
	
	
	public SimilarityParameters() {
		super(new Parameter[]{suffix, type, action, minSimilarity});
	}
}
