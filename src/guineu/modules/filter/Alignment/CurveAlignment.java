/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
*/

package guineu.modules.filter.Alignment;


import guineu.modules.filter.Alignment.data.AlignStructMol;
import flanagan.math.Matrix;
import guineu.desktop.Desktop;
import java.util.Vector;


/**
 *
 * @author scsandra
 */
public class CurveAlignment {
    
    AlignmentChart chart;         
    public CurveAlignment(Desktop desktop){  
        try{ 
            chart = new AlignmentChart("Solution");
            desktop.addInternalFrame(chart);
            chart.printAlignmentChart();
            chart.setVisible(true);
            chart.toFront();               
        }catch(Exception exception){                                  
        }  
    }

    public Vector[] CurveRansac(Vector[] AllLipidTypes, AlignmentParameters AParameters){
        try{  
            double score;
            double bestscore = 1000;
            Vector[] Solution = new Vector[AllLipidTypes.length];
            RANSACDEVST ransac = new RANSACDEVST(AParameters);
            for(int it = 0; it < (Integer)AParameters.getParameterValue(AlignmentParameters.OptimizationIterations); it++){                 
                //RANSAC
                for(int i = 0; i < AllLipidTypes.length; i++){   
                    for(AlignStructMol mol : (Vector<AlignStructMol>)AllLipidTypes[i]){
                  //      mol.reset();
                    }
                    ransac.alignment(AllLipidTypes[i]);     
                }                
                //Optimization conditions
               /* if((Boolean)AParameters.getParameterValue(AlignmentParameters.curve)){
                    Vector<double[]> threePoints = this.getThreePoints(AllLipidTypes);    
                    if(threePoints == null){ continue;}               
                    double[] abc = this.getCurveEquation(threePoints);
                    score = this.getScore(AllLipidTypes, abc);
                }else{   
                    score = this.getScore(AllLipidTypes);
                }

                if(score < bestscore){                      
                    bestscore = score;  
                    int cont = 0;
                    for(Vector v : AllLipidTypes){
                        Vector<AlignStructMol> newV = new Vector<AlignStructMol>();
                        for(AlignStructMol mol: (Vector<AlignStructMol>)v){
                            newV.addElement(mol.clone());
                        }
                        Solution[cont++] = newV;
                    }                    
                    this.printGlobalChart(Solution);
                }*/

            }            
            return Solution;
        }catch(Exception e){
            System.out.println("CurveAlignment.java --> CurveRansac() " + e);
            return null;
        }
    }
   
    
  /*  private double getScore(Vector[] AllLipidTypes){
        SimpleRegression regression = new SimpleRegression();
        double Aligned = 0;
        double Score = 0;
        for(int i = 0; i < AllLipidTypes.length; i++){
            for(int j = 0; j < AllLipidTypes[i].size(); j++){     
                AlignStructMol point = (AlignStructMol) AllLipidTypes[i].elementAt(j);
                if(point.Aligned){
                    regression.addData(point.lipid1.getAverageRT(), point.lipid2.getAverageRT());
                    Aligned++;
                }
            }
        }
        double intercept = regression.getIntercept();
        double slope = regression.getSlope();
        for(int i = 0; i < AllLipidTypes.length; i++){
            for(int j = 0; j < AllLipidTypes[i].size(); j++){    
                AlignStructMol point = (AlignStructMol) AllLipidTypes[i].elementAt(j);
                double bestY = intercept + (point.lipid1.getAverageRT() * slope);
                Score += Math.abs(bestY - point.lipid2.getAverageRT());
            }
        }
        return  Score/Aligned;   
    }*/
    
    
    /**
     * 
     * @param threePoints Three XY coordinates
     * @return the three variables of the curve equation
     */
    private double[] getCurveEquation(Vector<double[]> threePoints) {
        double[][] m = new double[3][3];
        double[] y = new double[3];
        for(int i = 0; i < 3; i++){            
            double[] point = threePoints.elementAt(i);
            y[i] = point[1];
            m[i][0] = Math.pow(point[0], 2);
            m[i][1] = point[0];
            m[i][2] = 1;
        }
        
        Matrix matrix = new Matrix(m);
        
        Matrix iMatrix = matrix.inverse();
        double[][] im = iMatrix.getArrayReference();
        double[] values = new double[3];
        for(int i = 0; i < 3; i++){            
            values[i] = im[i][0]*y[0] + im[i][1]*y[1] + im[i][2]*y[2]; 
        }
        
        return values;
    }

   /* private double getScore(Vector[] AllLipidTypes, double[] abc) {
        double Score = 0;
        double Aligned = 0;
        for(int i = 0; i < AllLipidTypes.length; i++){
            for(int j = 0; j < AllLipidTypes[i].size(); j++){                
                AlignStructMol point = (AlignStructMol) AllLipidTypes[i].elementAt(j);
                if(point.Aligned){
                    double bestY = abc[0]*Math.pow(point.lipid1.getAverageRT(),2) + (abc[1] * point.lipid1.getAverageRT()) + abc[2];
                    Score += Math.abs(bestY - point.lipid2.getAverageRT());
                    Aligned++;
                }   
            }
        }
        return Score/Aligned;
    }


    private Vector<double[]> getThreePoints(Vector[] AllLipidTypes) {
        Vector<double[]> threePoints = new Vector<double[]>();
        double[] max = new double[2];       
        double[] media = new double[2];
        double[] min = new double[2];
       
        
        Vector<AlignStructMol> AlignLipids = new Vector<AlignStructMol>();
        for(int i = 0; i < AllLipidTypes.length; i++){
            for(int j = 0; j < AllLipidTypes[i].size(); j++){
                AlignStructMol Alipid = (AlignStructMol) AllLipidTypes[i].elementAt(j);
                if(Alipid.Aligned){
                    AlignLipids.add(Alipid);
                }
            }
        }
       
        Collections.sort(AlignLipids, new RTComparator());
        min[0] = AlignLipids.elementAt(0).lipid1.getAverageRT();
        min[1] = AlignLipids.elementAt(0).NewY;
        threePoints.add(min);
        max[0] = AlignLipids.elementAt(AlignLipids.size()-1).lipid1.getAverageRT();
        max[1] = AlignLipids.elementAt(AlignLipids.size()-1).NewY;
        threePoints.add(max);
        media[0] = AlignLipids.elementAt(AlignLipids.size()/2).lipid1.getAverageRT();
        media[1] = AlignLipids.elementAt(AlignLipids.size()/2).NewY;
        threePoints.add(media);
              
        return threePoints;
        
    }
    
    private void printGlobalChart(Vector[] Solution){
        try{
            chart.removeSeries();
            for(int e = 0; e < Solution.length; e++){  
                try{
                    String lipidName = " ";
                    chart.addSeries(Solution[e], lipidName);                         
                }catch(Exception exception){

                }
            }
            chart.printAlignmentChart(); 
        }catch(Exception exception){
            System.out.println("Alignment.java --> printGlobalChart() "+ exception);
        }
    }
  
    
    class RTComparator implements Comparator<AlignStructMol> {
        public RTComparator(){}
       
        public int compare(AlignStructMol o1, AlignStructMol o2) {
            return Double.compare(o1.lipid1.getAverageRT(), o2.lipid1.getAverageRT());
        }
    }*/
}
