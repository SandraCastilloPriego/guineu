/*
    Copyright 2007-2012 VTT Biotechnology

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



import guineu.data.impl.datasets.SimpleLCMSDataset;
import java.util.Vector;


/**
 *
 * @author scsandra
 */
public class NormalizeNOMIS {
    private SimpleLCMSDataset dataset;
    private double cont;
    private StandardUmol stdMol;
    
    public NormalizeNOMIS(SimpleLCMSDataset dataset, StandardUmol stdMol){
        this.dataset = dataset;
        this.stdMol = stdMol;
    }
    
  
    public void run(){
        try{
            this.normalize();
        }catch(Exception e){
            System.out.println("NormalizeFilter.java --> run() : " + e);
        }
    }
    
    public void getStandars(){         
        
       /* for(int i = 0; i < dataset.getNumberMolecules(); i++){
           SimpleLipid lipid = (SimpleLipid)dataset.getMolecule(i);
           if(lipid.getStandard() == 1){  
                Vector<Double> standard = new Vector<Double>(); 
                String Name;
                for(int e = 0; e < dataset.getNumberExperiment(); e++){                    
                    standard.addElement(new Double((Double)dataset.getExperiment(e).getConcentration(i)));
                }
                Name = lipid.getMolName();
                 try{
                    String[] names = Name.split(";");
                    Name = names[0];
                }catch(Exception exception){}

                if(Name != null && Name.indexOf("(") > 0){                
                    Name = Name.substring(0, Name.indexOf("("));
                }
                stdMol.addStandard(Name, standard);
           }           
        }    */    
    }

    public SimpleLCMSDataset getDataset() {
        return dataset;
    }
    
    
    
    private String getLipidName(int row){
        /* if(dataset.getMolecule(row) != null){
            SimpleLipid lipid = (SimpleLipid) dataset.getMolecule(row);  
            String olipid = lipid.getMolName(); 
            try{
                String[] names = olipid.split(";");
                olipid = names[0];
            }catch(Exception exception){}
            
            if(olipid != null && olipid.indexOf("(") > 0){                
                olipid = olipid.substring(0, olipid.indexOf("("));
            }
            
            if(this.stdMol.getIndexOfStandard(olipid) == -1){
                olipid = this.getUnknownName(lipid.getAverageRT());
            }
            return olipid;
         }*/
         return null;
    }
    
    public double getNormalizedValue(double value, double stdConcentration, double M){
        return (value/stdConcentration)*M;
    }
    
    public String getUnknownName(double RT){ 
        if(this.stdMol.getNameType() == -1){
            if(RT < 300){
                return "LysoGPCho";
            }
            if(RT >= 300 && RT < 410){
                return "GPA";
            }
            if(RT >= 410){
                return "TAG";
            }
        }else{
             if(RT < 300){
                return "LPC";
            }
            if(RT >= 300 && RT < 410){
                return "GPA";
            }
            if(RT >= 410){
                return "TG";
            }
        }
        return null;
    }
    
    public void normalize(){
        this.getStandars();       
       
     
      /*  for(int i = 0; i < dataset.getNumberMolecules(); i++){
            String lipid = this.getLipidName(i);  
            int index = this.stdMol.getIndexOfStandard(lipid);              
            if(index != -1){
                for(int e = 0; e < dataset.getNumberExperiment(); e++){
                    try{ 
                        double valueNormalized = dataset.getExperiment(e).getConcentration(i);   

                        valueNormalized = this.getNormalizedValue(valueNormalized,(Double)this.stdMol.getValue(index, e), this.stdMol.getM(index));                           

                        dataset.getExperiment(e).updateConcentration(i, new Double(valueNormalized));   

                    }catch(Exception exception){
                        System.out.println(this.getLipidName(i));
                        exception.printStackTrace();
                    }
                }
            }
            
            cont++;
        }*/
    }  
    
    
    public double getProgress() {
        return /*(cont/(dataset.getNumberMolecules()))*/0.0f;
    }
   
}
