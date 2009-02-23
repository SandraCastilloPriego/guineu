/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.file.introExperimentsDB;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class IntroExperimentParameters extends SimpleParameterSet {

    public static final Parameter logPath = new SimpleParameter(
            ParameterType.STRING, "logPath",
            "Directori of the logs", null, "logPath", null);
    public static final Parameter CDFPath = new SimpleParameter(
            ParameterType.STRING, "CDFPath",
            "Directori of the CDFs files", null, "CDFPath", null);

    public IntroExperimentParameters() {
        super(new Parameter[]{ /*excelPath*/logPath, CDFPath});
    }
}
