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

package guineu.modules.identification.normalizationNOMIS2;

import java.util.ArrayList;
import java.util.List;




/**
 *
 * @author scsandra
 */
public class StandardUmol {
    List<StandardData> data;
    
    
    public StandardUmol(){
        this.data = new ArrayList<StandardData>();
    }
    
    public int getStandardNumber(){
        return data.size();
    }    
   
    public void addStandard(String Name, List standard){
        data.add(new StandardData(Name, standard));
    }
    
    public int getIndexOfStandard(String Name){
        try{
            for(int i = 0; i < this.data.size(); i++){                
                if(this.data.get(i).Name.matches("^"+Name+".*")){
                    return i;
                }
            }
            return -1;
        }catch(Exception exception){
            return -1;
        }
    }
    
    public double getM(int index){
        return this.data.get(index).M;
    }
    
    public double getValue(int index, int e){
        return this.data.get(index).standard.get(e);
    }
    
    public int getNameType(){
        return this.getIndexOfStandard("TG");
    }
}

class StandardData{
    double M;
    String Name;
    List<Double> standard;
    public StandardData(String Name, List<Double> standard){
        this.Name = Name;
        this.standard = standard;
        this.M = this.setM(standard);
    }
    
    public double setM(List<Double> standard){
        double sum = 0;
        for(Double Z : standard){
            sum += Math.log10(Z);
        }
        return Math.exp(sum/standard.size());    
    }  
    
}
