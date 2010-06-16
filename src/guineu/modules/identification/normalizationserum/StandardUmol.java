/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.identification.normalizationserum;

import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class StandardUmol {

    double Cer = 0.0;
    double GPCho = 0.0;
    double GPEtn = 0.0;
    double LysoGPCho = 0.0;
    double TAG = 0.0;
    String other = " ";
    double otherValue = 0.0;
    String other1 = " ";
    double otherValue1 = 0.0;
    Vector<Double> vCer;
    Vector<Double> vGPCho;
    Vector<Double> vGPEtn;
    Vector<Double> vLysoGPCho;
    Vector<Double> vTAG;
    Vector<Double> vOtherValue;
    Vector<Double> vOtherValue1;
   // Vector<Double> 
    
    
    public StandardUmol() {
        this.vCer = new Vector<Double>();
        this.vGPCho = new Vector<Double>();
        this.vGPEtn = new Vector<Double>();
        this.vLysoGPCho = new Vector<Double>();
        this.vTAG = new Vector<Double>();
        this.vOtherValue = new Vector<Double>();
        this.vOtherValue1 = new Vector<Double>();

    }
}
