package guineu.data.Tables.experimenttable;


import guineu.data.Dataset;
import guineu.data.impl.Bexperiments;
import guineu.data.impl.DatasetType;
import guineu.data.impl.ExperimentDataset;
import guineu.data.impl.SimpleDataset;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.TableComparator.SortingDirection;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;




public class ExperimentDataModel extends AbstractTableModel implements DataTableModel {
    
    /**
     * All data in the main windows. It can be LCMS or GCGC-Tof data.
     */
    private static final long serialVersionUID = 1L;
    private String columns[];	
    private Object[][] rows; //content all data   
    private int numColumns;
    private int numRows;
    private Vector<String> columns_mol = new Vector<String>();	
    private Vector<Bexperiments> data;
    
    protected boolean isSortAsc = true;
    protected int sortCol = 0;

    public ExperimentDataModel(Dataset data){
        this.data = ((ExperimentDataset)data).getExperiments();
        columns_mol.add("Name");
        columns_mol.add("Type");
        columns_mol.add("Project");
        columns_mol.add("Person");
        columns_mol.add("Replicate");
        columns_mol.add("Amount");
        columns_mol.add("Unit");
        columns_mol.add("Method"); 
        columns_mol.add("Sample");  
        columns_mol.add("Date"); 
        this.set_samples();
    }

   
    /**
     * Makes a new rows[][] with the new dates. First add the columns names with "writeSamplesNames(x)", and then
     * rewrite all data (rows[][]).
     * @param sampleNames vector with the names of the experiment whitch have to be in the table.
     * @param type is true for GCGC-Tof data and false to LCMS data
     */
    public void set_samples(){
        this.writeSamplesName();
        numColumns = columns.length;
        this.writeData();        
        numRows = rows.length; 
    }    

    public Object[][] getRows(){
        return rows;
    }	

    public void setRows(Object[][] rows){
        this.rows = rows;
        numRows = rows.length;
    }   

    /**
     * Adds the name of the experiments in the "columns" variable. There are the title of the columns.
     * @param sampleNames list of all experiments names.
     */
    public void writeSamplesName(){
        columns = new String[columns_mol.size()];
        for(int i = 0; i < columns_mol.size(); i++){
                columns[i] = (String)columns_mol.elementAt(i);
        }
    }
	
	
    /**
     * Takes all necessary information from the database and writes it in rows[][]. 
     * @param data 
     */
    public void writeData(){
        rows = new Object[data.size()][this.columns.length];    
                     
            for(int i = 0; i < data.size(); i++){
                Bexperiments exp = (Bexperiments) data.elementAt(i);
                rows[i][0] = exp.Name;                
                rows[i][1] = exp.TYPE;
                rows[i][2] = exp.PROJECT;
                rows[i][3] = exp.PERSON;              
                rows[i][4] = exp.REPLICATE;
                rows[i][5] = exp.Amount;
                rows[i][6] = exp.Unit;               
                rows[i][7] = exp.Method;
                rows[i][8] = exp.Sample;
                rows[i][9] = exp.EDATE;
            }
            
             		
    }
	
    public void removeRow(int rowIndex){
        Vector<Object[]> bt = new Vector<Object[]>();
        for(int i = 0; i < rows.length; i++){
            bt.addElement(rows[i]);
        }	
        bt.removeElementAt(rowIndex);
        numRows--;
        rows = new Object[bt.size()][numColumns];                
        for(int i = 0; i < rows.length; i++){
            Object[] st = new Object[numColumns];
            st = (Object[])bt.elementAt(i);
            for(int j = 0; j < numColumns; j++){
                rows[i][j] = st[j];
            }
        }		
    }

   
    public int getColumnCount() {
        return numColumns;
    }

    public int getRowCount() {
        return numRows;
    }

    public Object getValueAt (final int row, final int column) {		
        return rows[row][column];		
    }
    
   
    @Override
    public String getColumnName (int columnIndex) {
        String str = columns[columnIndex];
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
        rows[row][column] = aValue;
        fireTableCellUpdated (row, column);
    }
	
	
	
    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }

    void addColumn() {
        String[] oldColumns = this.columns.clone();
        this.columns = new String[oldColumns.length+1];
        for(int i = 0; i < oldColumns.length; i++){
            System.out.println(oldColumns[i]);
            this.columns[i] = oldColumns[i];
        }
        this.columns[oldColumns.length] = "New Column";
        this.numColumns = this.columns.length;
        
        this.addColumnObject(this.rows);       
        this.numRows = this.rows.length;
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

   
	
/*sort the table*/
	
    class ColumnListener extends MouseAdapter {
        protected JTable table;

        public ColumnListener(JTable t) {
            table = t;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getPoint().y > 18){
                TableColumnModel colModel = table.getColumnModel();
                int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
                int modelIndex = -1;	   
                if(columnModelIndex!= -1 && columnModelIndex < table.getColumnCount()){
                    modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
                }
                if (modelIndex < 0)
                    return;
                if (sortCol == modelIndex)
                    isSortAsc = !isSortAsc;
                else
                    sortCol = modelIndex;

                for (int i = 0; i < numColumns; i++) { 
                    TableColumn column = colModel.getColumn(i);
                    column.setHeaderValue(getColumnName(column.getModelIndex()));
                }
                table.getTableHeader().repaint();

                Vector<Object> vt = new Vector<Object>();
                Vector<Object[]> realvt = new Vector<Object[]>();                
                for(int i = 0; i < numRows; i++){
                    vt.addElement(rows[i][sortCol]);
                    Object [] rs = new Object[numColumns];
                    for(int j = 0; j < numColumns; j++){
                        rs[j] = rows[i][j];
                    }	    	  
                    realvt.addElement(rs);
                   

                }
                Collections.sort(vt,new MyComparator(isSortAsc));


                for(int i = 0; i < numRows; i++){	    	  
                    for(int j = 0; j < realvt.size(); j++){
                        if(((Object[])realvt.elementAt(j))[sortCol] != null && (realvt.elementAt(j))[sortCol].equals(vt.elementAt(i))){
                            rows[i] = (Object[])realvt.elementAt(j);
                            realvt.removeElementAt(j);                            
                            break;
                        }
                    }  	    	
                }

                table.tableChanged(new TableModelEvent(ExperimentDataModel.this));
                table.repaint();
            }
        }
    }

    public SortingDirection getSortDirection() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getSortCol() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSortDirection(SortingDirection direction) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setSortCol(int column) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Object[][] getData() {
        return this.rows;
    }

    public void changeData(int column, int row) {
        Bexperiments experiment = this.data.elementAt(row);
        switch(column){
            case 0: 
                experiment.Name = rows[row][column].toString();
                break;
            case 1:
                experiment.TYPE = rows[row][column].toString();
                break;
            case 2:
                experiment.PROJECT = rows[row][column].toString();
                break;
            case 3:
                experiment.PERSON = rows[row][column].toString();
                break;
            case 4:
                experiment.REPLICATE = rows[row][column].toString();
                break;
            case 5:
                experiment.Amount = rows[row][column].toString();
                break;
            case 6:
                experiment.Unit = rows[row][column].toString();
                break;
            case 7:
                experiment.Method = rows[row][column].toString();
                break;
            case 8:
                experiment.Sample = rows[row][column].toString();
                break;
            case 9:
                experiment.EDATE = rows[row][column].toString();
                break;
        }
    }

    public SimpleDataset removeRows() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public DatasetType getType() {
        return DatasetType.EXPERIMENTINFO;
    }
	
}
class MyComparator implements Comparator<Object> {
    protected boolean isSortAsc;

    public MyComparator( boolean sortAsc) {
        isSortAsc = sortAsc;
    }

    public int compare(Object o1, Object o2) {
        String s1 = null;
        String s2 = null;           
        try{          
            
            if(o1.getClass().toString().matches(".*Double.*")){
                int result = Double.compare((Double)o1, (Double)o2);
                if (!isSortAsc)
                    result = -result;  
                return result;
            }else if(o1.getClass().toString().matches(".*Integer.*")){
                int result = 0;
                if((Integer)o1 < (Integer)o2){
                    result = 1;
                }else if((Integer)o1 > (Integer)o2){
                    result = -1;
                }else result = 0;
                if (!isSortAsc)
                    result = -result;  
                return result;
            }else if(o1.getClass().toString().matches(".*String.*")){
                s1 = (String) o1;
                s2 = (String) o2;
                int result = 0;
                if(s1 != null && s2 != null)
                    result = s1.compareTo(s2);

                if (!isSortAsc)
                    result = -result;  

                return result;
            }else if(o1.getClass().toString().matches(".*Boolean.*")){
                int result = 0;
                if((Boolean)o1 && !(Boolean)o2){
                    result = 1;
                }else if(!(Boolean)o1 && (Boolean)o2){
                    result = -1;
                }else result = 0;
                
                 if (!isSortAsc)
                    result = -result;  
                return result;
            }
            return 0;
        }catch(Exception ee){
            return 0;              
        }
    }

    @Override
	  public boolean equals(Object obj) {
	    if (obj instanceof MyComparator) {
	      MyComparator compObj = (MyComparator) obj;
	      return compObj.isSortAsc == isSortAsc;
	    }
	    return false;
	  }
	}

