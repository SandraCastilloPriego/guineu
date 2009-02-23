/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
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
