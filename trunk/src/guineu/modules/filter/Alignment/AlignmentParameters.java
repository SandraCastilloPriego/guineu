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

package guineu.modules.filter.Alignment;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author SCSANDRA
 */
public class AlignmentParameters extends SimpleParameterSet{
   
    public static final Parameter OptimizationIterations = new SimpleParameter(
            ParameterType.INTEGER, "OptimizationIterations",
            "Optimization Iterations", new Integer(100));
    public static final Parameter NMinPoints = new SimpleParameter(
            ParameterType.DOUBLE, "NMinPoints",
            "Number Minimun of Points", new Float(0.2));
    public static final Parameter Margin = new SimpleParameter(
            ParameterType.DOUBLE, "Margin",
            "Margin", new Float(2.0));
    public static final Parameter curve = new SimpleParameter(
            ParameterType.BOOLEAN, "Curve",
            "Curve", new Boolean(false));
    
    public AlignmentParameters() {
        super(new Parameter[] { OptimizationIterations, NMinPoints, Margin, curve });
    }
}
 