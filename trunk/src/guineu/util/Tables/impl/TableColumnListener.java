/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.util.Tables.impl;

import guineu.util.components.*;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


/**
 *
 * @author scsandra
 */
public class TableColumnListener extends MouseAdapter {
        protected JTable table;
        protected DataTableModel model;
        protected Object[][] rows;
        public TableColumnListener(JTable table, DataTableModel model) {
            this.table = table;
            this.model = model;
            rows = model.getData();
        }

        @Override
        public void mouseClicked(MouseEvent e) {          
            TableColumnModel colModel = table.getColumnModel();
            int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
            int modelIndex = -1;	   
            if(columnModelIndex!= -1 && columnModelIndex < table.getColumnCount()){
                modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
            }
            if (modelIndex < 0)
                return;
            
            if (model.getSortCol() == modelIndex){
                if(model.getSortDirection() == SortingDirection.Ascending){ 
                    model.setSortDirection(SortingDirection.Descending);
                }else{
                    model.setSortDirection(SortingDirection.Ascending);
                }
            }else{
                model.setSortCol(modelIndex);
            }
            for (int i = 0; i < table.getColumnCount(); i++) { 
                TableColumn column = colModel.getColumn(i);
                column.setHeaderValue(model.getColumnName(column.getModelIndex()));
            }
            table.getTableHeader().repaint();

            Vector<Object> vt = new Vector<Object>();
            Vector<Object[]> realvt = new Vector<Object[]>();                
            for(int i = 0; i < table.getRowCount(); i++){
                vt.addElement(rows[i][model.getSortCol()]);
                Object [] rs = new Object[table.getColumnCount()];
                for(int j = 0; j < table.getColumnCount(); j++){
                    rs[j] = rows[i][j];
                }	    	  
                realvt.addElement(rs);


            }
            
            Collections.sort(vt,new TableComparator(model.getSortDirection()));


            for(int i = 0; i < table.getRowCount(); i++){	    	  
                for(int j = 0; j < realvt.size(); j++){
                    if(((Object[])realvt.elementAt(j))[model.getSortCol()] != null && (realvt.elementAt(j))[model.getSortCol()].equals(vt.elementAt(i))){
                        rows[i] = (Object[])realvt.elementAt(j);
                        realvt.removeElementAt(j);                            
                        break;
                    }
                }  	    	
            }

            table.tableChanged(new TableModelEvent(model));
            table.repaint();
           
        }
    }
	

