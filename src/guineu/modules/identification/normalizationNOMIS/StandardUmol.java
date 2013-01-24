/*
 * Copyright 2007-2013 VTT Biotechnology
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

package guineu.modules.identification.normalizationNOMIS;

import flanagan.math.Matrix;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class StandardUmol {
    List<Double> M;
    List<List> standards;
    List<String> standardNames;
    
    
    
    public StandardUmol(){
        this.M = new ArrayList<Double>();
        this.standards = new ArrayList<List>();      
        this.standardNames = new ArrayList<String>();
    }
    
    public int getStandardNumber(){
        return standards.size();
    }
 
    public void calculeM(){
        for(List v : standards){
            M.add(new Double(this.setM(v)));
        }         
    }
    
    
    public double setM(List<Double> standard){
        double sum = 0;
        for(Double Z : standard){
            sum += Math.log10(Z);
        }
        return Math.exp(sum/standard.size());    
    }
    
    public void addStandard(String Name, List standard){
        this.standardNames.add(Name);
        this.standards.add(standard);
    }
    
    public int getIndexOfStandard(String Name){
        try{
            for(int i = 0; i < this.standardNames.size(); i++){
                if(this.standardNames.get(i).matches(".*"+Name+".*")){
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
                List<Double> Tstandard = this.standards.get(t);
                List<Double> Sstandard = this.standards.get(s);
                double sum = 0;
                for(int j = 0; j < Tstandard.size(); j++){
                    double std = (Tstandard.get(j) - this.getMean(Tstandard)) * (Sstandard.get(j) - this.getMean(Sstandard));
                    sum += std;
                }
                matrix[t][s] = sum;
            }
        }
        return matrix;
    }
    
    public double getMean(List<Double> standards){
        double mean = 0;
        for(int j = 0; j < standards.size(); j++){
            mean += standards.get(j);
        }
        return mean/standards.size();                
    }
    
    public double getStandardValue(Matrix beta, int j, int i){        
        double sum = 0;
        for(int s = 0; s < this.standards.size(); s++){
            sum += beta.getElement(i, s) * ((Double)this.standards.get(s).get(j) - this.getMean(this.standards.get(s)));
        }
        return sum * (-1);
    }
    
}
