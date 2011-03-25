/*
    Copyright 2007-2011 VTT Biotechnology

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

package guineu.modules.identification.normalizationNOMIS2;


import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class StandardUmol {
    Vector<StandardData> data;
    
    
    public StandardUmol(){
        this.data = new Vector<StandardData>();
    }
    
    public int getStandardNumber(){
        return data.size();
    }    
   
    public void addStandard(String Name, Vector standard){
        data.add(new StandardData(Name, standard));
    }
    
    public int getIndexOfStandard(String Name){
        try{
            for(int i = 0; i < this.data.size(); i++){                
                if(this.data.elementAt(i).Name.matches("^"+Name+".*")){
                    return i;
                }
            }
            return -1;
        }catch(Exception exception){
            return -1;
        }
    }
    
    public double getM(int index){
        return this.data.elementAt(index).M;
    }
    
    public double getValue(int index, int e){
        return this.data.elementAt(index).standard.elementAt(e);
    }
    
    public int getNameType(){
        return this.getIndexOfStandard("TG");
    }
}

class StandardData{
    double M;
    String Name;
    Vector<Double> standard;
    public StandardData(String Name, Vector<Double> standard){
        this.Name = Name;
        this.standard = standard;
        this.M = this.setM(standard);
    }
    
    public double setM(Vector<Double> standard){
        double sum = 0;
        for(Double Z : standard){
            sum += Math.log10(Z);
        }
        return Math.exp(sum/standard.size());    
    }  
    
}
