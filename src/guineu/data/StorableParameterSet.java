/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.data;

import org.dom4j.Element;

/**
 * 
 */
public interface StorableParameterSet extends ParameterSet {

    /**
     * Export parameter values to XML representation
     * 
     */
    public void exportValuesToXML(Element element);

    /**
     * Import parameter values from XML representation
     */
    public void importValuesFromXML(Element element);

}
