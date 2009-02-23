/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.util.Tables.impl;

import java.util.Comparator;

/**
 *
 * @author scsandra
 */
public class TableComparator implements Comparator<Object> {    
    public enum SortingDirection {Ascending, Descending};
    private SortingDirection isSortAsc;
    
    public TableComparator(SortingDirection sortDirection) {
        isSortAsc = sortDirection;
    }

    public int compare(Object o1, Object o2) {
        String s1 = null;
        String s2 = null;           
        try{
            int result = 0;
            if(o1.getClass().toString().matches(".*Double.*")){
                result = Double.compare((Double)o1, (Double)o2);                
            }else if(o1.getClass().toString().matches(".*Integer.*")){               
                if((Integer)o1 < (Integer)o2){
                    result = 1;
                }else if((Integer)o1 > (Integer)o2){
                    result = -1;
                }         
            }else if(o1.getClass().toString().matches(".*String.*")){
                s1 = (String) o1;
                s2 = (String) o2;               
                if(s1 != null && s2 != null)
                    result = s1.compareTo(s2);
               
            }else if(o1.getClass().toString().matches(".*Boolean.*")){
                if((Boolean)o1 && !(Boolean)o2){
                    result = 1;
                }else if(!(Boolean)o1 && (Boolean)o2){
                    result = -1;
                }                
            }
            if (isSortAsc == SortingDirection.Ascending) return -result;               
            return result;
           
        }catch(Exception ee){
            return 0;              
        }
    }
   
}
