/*
    Copyright 2007-2010 VTT Biotechnology

    This file is part of MULLU.

    MULLU is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation.

    MULLU is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MULLU; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package guineu.modules.identification.normalizationNOMIS;

import flanagan.math.Matrix;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class StandardUmol {
    Vector<Double> M;
    Vector<Vector> standards;
    Vector<String> standardNames;
    
    
    
    public StandardUmol(){
        this.M = new Vector<Double>();
        this.standards = new Vector<Vector>();      
        this.standardNames = new Vector<String>();
    }
    
    public int getStandardNumber(){
        return standards.size();
    }
 
    public void calculeM(){
        for(Vector v : standards){
            M.addElement(new Double(this.setM(v)));
        }         
    }
    
    
    public double setM(Vector<Double> standard){
        double sum = 0;
        for(Double Z : standard){
            sum += Math.log10(Z);
        }
        return Math.exp(sum/standard.size());    
    }
    
    public void addStandard(String Name, Vector standard){
        this.standardNames.addElement(Name);
        this.standards.addElement(standard);
    }
    
    public int getIndexOfStandard(String Name){
        try{
            for(int i = 0; i < this.standardNames.size(); i++){
                if(this.standardNames.elementAt(i).matches(".*"+Name+".*")){
                    return i;
                }
            }
            return -1;
        }catch(Exception exception){
            return -1;
        }
    }
    
    public double[][] covariaceMatrix(){
        double[][] matrix = new double[this.standards.size()][this.standards.size()];
        for(int t = 0; t < this.standards.size(); t++){            
            for(int s = 0; s < this.standards.size(); s++){
                Vector<Double> Tstandard = this.standards.elementAt(t);
                Vector<Double> Sstandard = this.standards.elementAt(s);
                double sum = 0;
                for(int j = 0; j < Tstandard.size(); j++){
                    double std = (Tstandard.elementAt(j) - this.getMean(Tstandard)) * (Sstandard.elementAt(j) - this.getMean(Sstandard));
                    sum += std;
                }
                matrix[t][s] = sum;
            }
        }
        return matrix;
    }
    
    public double getMean(Vector<Double> standards){
        double mean = 0;
        for(int j = 0; j < standards.size(); j++){
            mean += standards.elementAt(j);
        }
        return mean/standards.size();                
    }
    
    public double getStandardValue(Matrix beta, int j, int i){        
        double sum = 0;
        for(int s = 0; s < this.standards.size(); s++){
            sum += beta.getElement(i, s) * ((Double)this.standards.elementAt(s).elementAt(j) - this.getMean(this.standards.elementAt(s)));
        }
        return sum * (-1);
    }
    
}
