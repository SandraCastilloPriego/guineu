package guineu.data.parser.impl;

import guineu.data.parser.Lipidclass_enum;


public class Lipidclass {

    public Lipidclass(){		

    }

    public int get_class(String lipid_name){
        //System.out.println(lipid_name);
        if(lipid_name.indexOf("(") == -1){
            return Lipidclass_enum.valueOf("unknown").lipid_class();
        }else{			
            String lipid_head = lipid_name.substring(0, lipid_name.indexOf("("));
            int lclass = Lipidclass_enum.valueOf(lipid_head).lipid_class();
            if(lclass == 5 && lipid_name.substring(lipid_name.indexOf("(")).indexOf("e") > -1){
                return 6;
            }else if(lclass == 7 && lipid_name.substring(lipid_name.indexOf("(")).indexOf("e") > -1){
                return 8;
            }else if(lclass == 13 && lipid_name.substring(lipid_name.indexOf("(")).indexOf("e") > -1){
                return 14;
            }else if(lclass == 15 && lipid_name.substring(lipid_name.indexOf("(")).indexOf("e") > -1){
                return 16;
            }else if(lclass == 17 && lipid_name.substring(lipid_name.indexOf("(")).indexOf("e") > -1){
                return 18;
            }else{
                return lclass;
            }	
        }	
    }    
   
}
