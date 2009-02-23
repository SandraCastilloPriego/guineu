/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.modules.file.saveDatasetDB;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;



/**
 *
 * @author scsandra
 */
public class SaveFileParameters extends SimpleParameterSet{
   
    public static final Parameter author = new SimpleParameter(
            ParameterType.STRING, "Author",
            "Author of the dataset", null, "Author", null);
    
    
    public SaveFileParameters() {
        super(new Parameter[] { author });
    }

}
