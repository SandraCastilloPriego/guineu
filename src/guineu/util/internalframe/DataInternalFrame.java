/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.util.internalframe;

import java.awt.Dimension;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author scsandra
 */
public class DataInternalFrame extends JInternalFrame{    
    
    JTable table;
    public DataInternalFrame(String name, JTable table, Dimension size){
        super(name, true, true, true, true);
        this.table = table;
        setSize(size);       
        setTable(table);
    }
    
    public JTable getTable(){
        return table;
    }
    
    public void setTable(JTable table){        
        try{
            JScrollPane scrollPanel = new JScrollPane (table);
            scrollPanel.setPreferredSize(new Dimension(this.getWidth()-330,this.getHeight()-90));	    
            this.add(scrollPanel); 
        }catch(Exception e){
            e.printStackTrace();
        } 
    }
}
