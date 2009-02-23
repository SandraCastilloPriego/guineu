/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.main;

import guineu.data.ParameterSet;


/**
 * This interface represents Guineu module.
 */
public interface GuineuModule {

    /**
     * Initialize this module.
     * 
     */
    public void initModule();

    /**
     * Returns module name 
     * 
     * @return Module name
     */
    public String toString();
    
    /**
     * Returns module's current parameters and their values
     * @return Parameter values as ParameterSet or null if module has no parameters
     */
    public ParameterSet getParameterSet();
    
    
    /**
     * Sets current parameters and their values
     * @param parameterValues New parameter values
     */
    public void setParameters(ParameterSet parameterValues);

}
