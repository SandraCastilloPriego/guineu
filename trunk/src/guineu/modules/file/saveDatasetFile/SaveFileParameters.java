/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
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
public class SaveFileParameters extends SimpleParameterSet{
   
    public static final Parameter path = new SimpleParameter(
            ParameterType.STRING, "path",
            "Path of the dataset", null, "Path", null);
    
    public static final Parameter type = new SimpleParameter(
            ParameterType.STRING, "type",
            "Type of file", null, "Type", null);
    
    public SaveFileParameters() {
        super(new Parameter[] { path, type });
    }

}
