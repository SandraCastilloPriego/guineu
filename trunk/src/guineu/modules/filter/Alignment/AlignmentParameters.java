/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
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
 