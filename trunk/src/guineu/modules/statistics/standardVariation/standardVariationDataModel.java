package guineu.modules.statistics.standardVariation;



import java.util.*;
import javax.swing.table.AbstractTableModel;




public class standardVariationDataModel extends AbstractTableModel {
    
    /**
     * All data in the main windows. It can be LCMS or GCGC-Tof data.
     */
    private static final long serialVersionUID = 1L;
    private String columns;	
    private Vector<String> rows; //content all data   
    private int numColumns;
    private int numRows;
    
    public standardVariationDataModel(String Name){
         rows = new Vector<String>();
         columns = Name;  
         numColumns = 1;            
         numRows = rows.size(); 
    }

    public Vector<String> getRows(){
        return rows;
    }	

    public void addRows(String row){
        this.rows.addElement(row);
        numRows = rows.size();
        this.fireTableDataChanged();
    } 

    public void removeRow(int rowIndex){
        this.rows.setElementAt("1", rowIndex); 
    }

   
    public int getColumnCount() {
        return numColumns;
    }

    public int getRowCount() {
        return numRows;
    }

    public String getValueAt (final int row, final int column) {		
        return rows.elementAt(row);		
    }
    
   
    @Override
    public String getColumnName (int columnIndex) {
        String str = columns;
       /* if (columnIndex == sortCol && columnIndex != 0)
          str += isSortAsc ? " >>" : " <<";*/
        return str;
    }
	
    @Override
    public Class<?> getColumnClass(int c) {
        if(getValueAt(0, c) != null){
            return getValueAt(0, c).getClass();
        }else return Object.class;
    }

    @Override
    public void setValueAt (Object aValue, int row, int column) {
        rows.setElementAt(aValue.toString(), row);
        //fireTableCellUpdated (row, column);
    }
	
	
	
    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

        
    public void addColumnObject(Object[][] o){
        Object[][] oldRows = o.clone();
        o = new Object[oldRows.length][oldRows[0].length+1];
        for(int i = 0; i < oldRows.length; i++){
            for(int j = 0; j < oldRows[0].length; j++){
                o[i][j] = oldRows[i][j];
            }
            o[i][oldRows[0].length]= " ";
        }
    }
     public void addColumnObject(int[][] o){
        int[][] oldRows = o.clone();
        o = new int[oldRows.length][oldRows[0].length+1];
        for(int i = 0; i < oldRows.length; i++){
            for(int j = 0; j < oldRows[0].length; j++){
                o[i][j] = oldRows[i][j];
            }
            o[i][oldRows[0].length]=0;
        }
    }
    
    
    
    public void setColumnCount(int count){
        this.numColumns = count;
    }

    public void reconstruct() {
         for(int i = 0; i < this.rows.size(); i++){
            if(this.rows.elementAt(i).matches("1")){
                this.rows.removeElementAt(i);
                i--;
            }
         }
         this.numRows = this.rows.size();
         this.fireTableDataChanged();
    }

   
}
