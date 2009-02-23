/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.filter.UnitsChangeFilter;

import guineu.modules.file.introExperimentsDB.*;
import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class UnitsChangeFilterParameters extends SimpleParameterSet {

    public static final Parameter multiply = new SimpleParameter(
            ParameterType.DOUBLE, "Multiply by",
            "Multiply by","", new Double(0.0),
            new Double(0.0), null);
    public static final Parameter divide = new SimpleParameter(
            ParameterType.DOUBLE, "Divide by",
            "Divide by","", new Double(0.0),
            new Double(0.0), null);

    public UnitsChangeFilterParameters() {
        super(new Parameter[]{multiply, divide});
    }
}
