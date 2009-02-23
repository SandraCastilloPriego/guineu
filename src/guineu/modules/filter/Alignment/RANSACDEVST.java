/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
*/
package guineu.modules.filter.Alignment;




import guineu.modules.filter.Alignment.data.AlignStructMol;
import java.util.Random;
import java.util.Vector;
import org.apache.commons.math.stat.regression.SimpleRegression;





public class RANSACDEVST {
	
    /**
     * input:
     * data - a set of observed data points
     * model - a model that can be fitted to data points
     * n - the minimum number of data values required to fit the model
     * k - the maximum number of iterations allowed in the algorithm
     * t - a threshold value for determining when a data point fits a model
     * d - the number of close data values required to assert that a model fits well to data
     * 
     * output:
     * bestfit - model parameters which best fit the data (or nil if no good model is found)
     */  

    int n;	
    double d;
    int k;	
    Random rnd;
    int AlsoNumber;
    AlignmentParameters AParameters;
    public RANSACDEVST(AlignmentParameters AParameters){
        this.AParameters = AParameters;
    }

    /**
     * Set all parameters and start ransac.
     * @param v vector with the points which represent all possible alignments.
     */
    public void alignment(Vector<AlignStructMol> v){
        this.rnd = new Random(); 
        n = 2;
        if(v.size() < 10){
            d = 3;
        }else{
            d = v.size() * (Double)this.AParameters.getParameterValue(AlignmentParameters.NMinPoints);
        }
        k = (int)this.getK();                  
        this.ransac(v);            
    }


    /**
     * Calculate k (number of trials)
     * @return number of trials "k" required to select a subset of n good data points.
     */
    public double getK(){
        double w = 0.05;
        double b = Math.pow(w, 2);            
        return 3*(1 / b);
    }

    /**
     * RANSAC algorithm
     * @param v vector with the points which represent all possible alignments.
     */
    public void ransac(Vector<AlignStructMol> v){           
       double besterr = 9.9E99;        

        for(int iterations = 0; iterations < this.k; iterations++){	
            this.AlsoNumber = this.n; 
            boolean initN = this.getInitN(v);
            if(!initN){
                continue;
            }

            SimpleRegression regression = this.getAllModelPoints(v);

            if(this.AlsoNumber >= this.d){                    
                double error = this.newError(v, regression);                    
                if(error < besterr){
                    besterr = error;
                    for(int i = 0; i < v.size(); i++){
                        AlignStructMol alignStruct = v.elementAt(i);
                        if(alignStruct.ransacAlsoInLiers || alignStruct.ransacMaybeInLiers){
                            alignStruct.Aligned = true;                            
                        }else{
                            alignStruct.Aligned = false;
                        }                            

                        alignStruct.ransacAlsoInLiers = false;
                        alignStruct.ransacMaybeInLiers = false;
                    }  
                    this.deleteRepeatsAlignments(v);
                }
            }

            for(int i = 0; i < v.size(); i++){
                AlignStructMol alignStruct = v.elementAt(i);
                alignStruct.ransacAlsoInLiers = false;
                alignStruct.ransacMaybeInLiers = false;
            }  
        }
    }
    
    
    /**
     * Take the initial 2 points ramdoly (but the points have to be separated)
     * @param v vector with the points which represent all possible alignments.
     * @return false if there are any problem.
     */
    private boolean getInitN(Vector<AlignStructMol> v){ 
        int quarter = v.size()/4;                   
        quarter--;
        if(quarter > 8){
            int index = rnd.nextInt(quarter); 
            v.elementAt(index).ransacMaybeInLiers = true; 

            index = rnd.nextInt(quarter);
            index += (quarter*3);                        
            v.elementAt(index).ransacMaybeInLiers = true;
            return true;
        }else if(v.size() > 1){
            for(int i = 0; i < this.n; i++){
                int index = rnd.nextInt(v.size());
                if(v.elementAt(index).ransacMaybeInLiers){
                    i--;
                }else{
                    v.elementAt(index).ransacMaybeInLiers = true;
                }
            }
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Take the model
     * @param v vector with the points which represent all possible alignments.
     * @return regression of the points inside the model
     */
    public SimpleRegression getAllModelPoints(Vector<AlignStructMol> v){
        SimpleRegression regression = new SimpleRegression();       
     
        /*for(int i = 0; i < v.size(); i++){
            AlignStructMol point = v.elementAt(i);
            if(point.ransacMaybeInLiers){ 
                regression.addData(point.lipid1.getAverageRT(), point.lipid2.getAverageRT());
            }
        }


       for(int i = 0; i < v.size(); i++){
            AlignStructMol point = v.elementAt(i);         

            double intercept = regression.getIntercept();
            double slope = regression.getSlope();

            double y = point.lipid2.getAverageRT();
            double bestY = intercept + (point.lipid1.getAverageRT()* slope);

            if(Math.abs(y-bestY) < (Float)this.AParameters.getParameterValue(AlignmentParameters.Margin)){
                point.ransacAlsoInLiers = true; 
                this.AlsoNumber++;
            }else{
                point.ransacAlsoInLiers = false;
            } 
        }   */             
        
        return regression;
    }
    

    /**
     * calculate the error in the model
     * @param v vector with the points which represent all possible alignments.
     * @param regression regression of the alignment points
     * @return the error in the model
     */
    public double newError(Vector<AlignStructMol> v, SimpleRegression regression){
        int PointNumber = 0;
        //DescriptiveStatistics stats = DescriptiveStatistics.newInstance(); 	
        boolean STD = false;
        double intercept = regression.getIntercept();
        double slope = regression.getSlope();
        double error = 0;
        //double leastSquare = 0;
       /* for(int i = 0; i < v.size(); i++){
            if(v.elementAt(i).ransacAlsoInLiers || v.elementAt(i).ransacMaybeInLiers){                  

               if(v.elementAt(i).picksScore > 1.2){
                    error++;
                }
             //  double y = v.elementAt(i).RT2;
               double newy =(v.elementAt(i).lipid1.getAverageRT() * slope) + intercept; 
               v.elementAt(i).NewY = newy;
             //  leastSquare += Math.pow(Math.abs(newy - y), 2);
                //stats.addValue(Math.abs(newy - y));
                PointNumber++;                    
                if(v.elementAt(i).STD){
                    STD = true;
                }
            }                
        }


       /*dispersion/PointNumber;*/
       // error += leastSquare;
        if(!STD) error = error + 10; /*/=2;*/           
        //if(regression.getRSquare() < this.t) error++;
        //error += Math.pow(1/slope, 2);

        //if(error > 0) error /= PointNumber;  
        if(Double.isNaN(regression.getR()) || regression.getR() < 0){
            error += 10;
        }      
        error /= PointNumber; 

        return error;	
    }        

    /**
     * If the same lipid is aligned with two differents lipids delete the alignment farest to the ransac regression line of all aligned points.
     * @param v vector with the points which represent all possible alignments.
     */
    private void deleteRepeatsAlignments(Vector<AlignStructMol> v){       
        /*for(int i = 0; i < v.size(); i++){ 
            AlignStructMol lipid1 = v.elementAt(i);
            if(lipid1.Aligned){
                for(int j = 0; j < v.size(); j++){
                    if(i != j){
                        AlignStructMol lipid2 = v.elementAt(j);
                        if(lipid2.Aligned){
                            if(lipid1.lipid1.getAverageRT() == lipid2.lipid1.getAverageRT() || lipid1.lipid2.getAverageRT() == lipid2.lipid2.getAverageRT()){
                                //System.out.println(lipid1.RT1 +" - "+ lipid1.RT2 + ": " + lipid1.Aligned + " / " + lipid2.RT1 +" - "+ lipid2.RT2 + ": " + lipid2.Aligned);
                              //  double Score1 = Math.abs(lipid1.NewY - lipid1.lipid2.getAverageRT());
                              //  double Score2 = Math.abs(lipid2.NewY - lipid2.lipid2.getAverageRT());
                                if(lipid1.picksScore < lipid2.picksScore){
                                    lipid2.Aligned = false;                                    
                                }else{
                                    lipid1.Aligned = false;
                                }
                                
                            }
                        }
                    }
                }
            }
        }*/
    }
        
     
    
}