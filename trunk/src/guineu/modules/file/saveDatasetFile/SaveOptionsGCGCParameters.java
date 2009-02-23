/*
 * Copyright 2007-2008 VTT Biotechnology
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

package guineu.modules.file.saveDatasetFile;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;

/**
 *
 * @author scsandra
 */
public class SaveOptionsGCGCParameters extends SimpleParameterSet {
    public static final Parameter id = new SimpleParameter(
            ParameterType.BOOLEAN, "ID",
            "ID", new Boolean(false));
    public static final Parameter rt1 = new SimpleParameter(
            ParameterType.BOOLEAN, "RT1",
            "RT1", new Boolean(true));
    public static final Parameter rt2 = new SimpleParameter(
            ParameterType.BOOLEAN, "RT2",
            "RT2", new Boolean(true));
    public static final Parameter rti = new SimpleParameter(
            ParameterType.BOOLEAN, "RTI",
            "RTI", new Boolean(false));
    public static final Parameter numFound = new SimpleParameter(
            ParameterType.BOOLEAN, "Num Found",
            "Num Found", new Boolean(true));
     public static final Parameter difference = new SimpleParameter(
            ParameterType.BOOLEAN, "Difference to ideal peak",
            "Difference to ideal peak", new Boolean(false));    
    public static final Parameter maxSimilarity = new SimpleParameter(
            ParameterType.BOOLEAN, "Max Similarity",
            "Max Similarity", new Boolean(true));
    public static final Parameter meanSimilarity = new SimpleParameter(
            ParameterType.BOOLEAN, "Mean Similarity",
            "Mean Similarity", new Boolean(true));
    public static final Parameter SimilaritystdDev = new SimpleParameter(
            ParameterType.BOOLEAN, "Similarity std dev",
            "Similarity std dev", new Boolean(true));
     public static final Parameter name = new SimpleParameter(
            ParameterType.BOOLEAN, "Metabolite Name",
            "Metabolite Name", new Boolean(true));
    public static final Parameter allNames = new SimpleParameter(
            ParameterType.BOOLEAN, "Metabolite all Names",
            "Metabolite all Names", new Boolean(true));
    public static final Parameter pubchemID = new SimpleParameter(
            ParameterType.BOOLEAN, "Pubchem ID",
            "Pubchem ID", new Boolean(false));
    public static final Parameter Mass = new SimpleParameter(
            ParameterType.BOOLEAN, "Mass",
            "Mass", new Boolean(true));    
    public static final Parameter spectrum = new SimpleParameter(
            ParameterType.BOOLEAN, "Spectrum",
            "Spectrum", new Boolean(true));
    
    public SaveOptionsGCGCParameters() {
        super(new Parameter[]{id, Mass, rt1, rt2, rti, numFound, difference, name, allNames, pubchemID, maxSimilarity, meanSimilarity, SimilaritystdDev, spectrum});
    }
}