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

package guineu.modules.identification.normalizationNOMIS;

import flanagan.math.Matrix;
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
                stdMol.addStandard(Name, standard);
           }           
        } */
        this.stdMol.calculeM();
    }

    public SimpleLCMSDataset getDataset() {
        return dataset;
    }
    
    
    
    private String getLipidName(int row){
       /*  if(dataset.getMolecule(row) != null){
            SimpleLipid lipid = (SimpleLipid) dataset.getMolecule(row);  
            String olipid = lipid.getMolName();          
            if(olipid.matches(".*unknown.*")){
                olipid = this.getUnknownName(lipid.getAverageRT());
            }
            return olipid;
         }*/
         return null;
    }
    
    public double getNormalizedValue(double value, double stdConcentration, double concentration){
        return (value/stdConcentration)*concentration;
    }
    
    public String getUnknownName(double RT){      
        if(RT < 300){
            return "LysoGPCho(18:0)";
        }
        if(RT >= 300 && RT < 410){
            return "GPA(32:0)";
        }
        if(RT >= 410){
            return "TAG(52:0)";
        }
        return null;
    }
    
    public void normalize(){
        this.getStandars();       
        Matrix CovarianceMatrix = new Matrix(this.stdMol.covariaceMatrix());
       // Matrix ProductMatrix = new Matrix(this.CreateMatrixProduct(this.stdMol.getStandardNumber()));
          
      //  Matrix beta = Matrix.times(ProductMatrix, CovarianceMatrix.inverse());        
        
      /*  for(int i = 0; i < beta.getNumberOfRows(); i++){
            for(int j = 0; j < beta.getNumberOfColumns(); j++){
                System.out.print(beta.getElement(i, j)+ " ");
            }
            System.out.println("--");
        }
        
       /* for(int i = 0; i < dataset.getNumberMolecules(); i++){
            String lipid = this.getLipidName(i);  
            if(lipid != null && lipid.indexOf("(") > 0){                
                lipid = lipid.substring(0, lipid.indexOf("("));
            
                for(int e = 0; e < dataset.getNumberExperiment(); e++){
                    try{ 
                        double valueNormalized = dataset.getExperiment(e).getConcentration(dataset.getMolecule(i, false).getID());   
                        int index = this.stdMol.getIndexOfStandard(lipid);     
                        if(index != -1){
                            valueNormalized = valueNormalized * Math.exp(this.stdMol.getStandardValue(beta, e, i));
                            dataset.getExperiment(e).updateConcentration(dataset.getMolecule(i, false).getID(), new Double(valueNormalized));   
                        }

                    }catch(Exception exception){
                        System.out.println(this.getLipidName(i));
                        exception.printStackTrace();
                    }
                }
            }
            cont++;
        }*/
    }  
    
    
   /* public double[][] CreateMatrixProduct(int StandardNumber){
        double[][] matrix = new double[dataset.getNumberMolecules()][StandardNumber];
        for(int i = 0; i < dataset.getNumberMolecules(); i++){
            double mean = this.getMean(i);
            for(int s = 0; s < StandardNumber; s++){
                double sum = 0;
                double standardMean = this.getStandardMean(s);
                for(int j = 0; j < dataset.getNumberExperiment(); j++){
                    if(dataset.getValue(i, j) == 0){
                        dataset.setValue(i, j, 0.1);
                    }                  
                    double mult = (Math.log10(dataset.getValue(i, j)) - mean)*(Math.log10((Double)stdMol.standards.elementAt(s).elementAt(j)) - standardMean);
                    sum+=mult;
                }
                matrix[i][s] = sum;
            }
        }
        
        return matrix;
    }
    
    
    
    
    
    
    public double getProgress() {
        return (cont/(dataset.getNumberMolecules()));
    }

    private double getMean(int i) {
        double mean = 0;
        for(int j = 0; j < dataset.getNumberExperiment(); j++){
            if(dataset.getValue(i, j) == 0){
                dataset.setValue(i, j, 0.1);
            } 
            mean += Math.log10(dataset.getValue(i, j));
        }
        return mean/dataset.getNumberExperiment();
    }

    private double getStandardMean(int s) {
        double mean = 0;
        for(int j = 0; j < stdMol.standards.elementAt(s).size(); j++){
            mean += Math.log10((Double)stdMol.standards.elementAt(s).elementAt(j));
        }
        return mean/stdMol.standards.elementAt(s).size();
    }*/
}
