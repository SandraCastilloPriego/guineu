/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
 */

package guineu.modules.filter.Alignment;

/**
 *
 * @author SCSANDRA
 */
public class RegressionStruct {
    String type;
    int ID1;
    int ID2;
    public double slope;
    public double intercept;
    boolean control = true;
    
    public boolean isAligned(double RT1, double RT2){
        double y = RT2 * slope + intercept;
        double y2 = RT1 * slope + intercept;
        //if(y > RT1 - 0.5 && y < RT1 + 0.5){return true;}
       /* else*/ if(y2 > RT2 - 2.0 && y2 < RT2 + 2.0){return true;}
        else return false;
    }
}
