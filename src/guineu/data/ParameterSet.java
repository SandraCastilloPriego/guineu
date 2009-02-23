/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.data;


/**
 * 
 */
public interface ParameterSet {

    /**
     * Create a copy of this parameter set, all referenced objects must be cloned. 
     */
    public ParameterSet clone();
    
    /**
     * Represent method's parameters and their values in human-readable format
     */
    public String toString();

}
