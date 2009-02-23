/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */
package guineu.data;

import java.text.NumberFormat;

/**
 * Parameter interface, represents parameters or variables used in the project
 */
public interface Parameter {

    /**
     * Returns the parameter type
     * 
     * @return Parameter type
     */
    public ParameterType getType();
    
    /**
     * Returns this parameter's name. The name must be unique within one ParameterSet.
     * 
     * @return Parameter name
     */
    public String getName();

    /**
     * 
     * @return Detailed description of the parameter
     */
    public String getDescription();
    
    /**
     * 
     * @return Symbol for units of the parameter or null
     */
    public String getUnits();
  
    /**
     * 
     * @return Default value of this parameter or null
     */
    public Object getDefaultValue();
    
    /**
     * 
     * @return Array of possible values of this parameter or null
     */
    public Object[] getPossibleValues();

    /**
     * 
     * @return Minimum possible value of this parameter or null
     */
    public Object getMinimumValue();

    /**
     * 
     * @return Maximum possible value of this parameter or null
     */
    public Object getMaximumValue();
    
    /**
     * 
     * @return Parameter for the NumberFormat suitable for this parameter or null
     */
    public NumberFormat getNumberFormat();
    
}
