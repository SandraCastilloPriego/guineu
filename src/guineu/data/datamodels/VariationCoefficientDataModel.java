package guineu.data.datamodels;

import guineu.data.impl.DatasetType;
import guineu.data.impl.VariationCoefficientData;
import guineu.util.Tables.DataTableModel;
import javax.swing.table.AbstractTableModel;
import java.util.*;

public class VariationCoefficientDataModel extends AbstractTableModel implements DataTableModel {

    /**
     * All data in the main windows. It can be LCMS or GCGC-Tof data.
     */
    private static final long serialVersionUID = 1L;
    private String columns[];
    private Object[][] rows; //content all data   
    private int numColumns;
    private int numRows;
    private Vector<String> columns_mol = new Vector<String>();
    

    public VariationCoefficientDataModel(Vector<VariationCoefficientData> data) {
        columns_mol.add("DatasetName");
        columns_mol.add("Coefficient variation");
        columns_mol.add("N Molecules");
        columns_mol.add("N Molecules known");
        columns_mol.add("N Experiments");
        set_samples(data);
    }

    /**
     * Makes a new rows[][] with the new dates. First add the columns names with "writeSamplesNames(x)", and then
     * rewrite all data (rows[][]).
     * @param sampleNames vector with the names of the experiment whitch have to be in the table.
     * @param type is true for GCGC-Tof data and false to LCMS data
     */
    public void set_samples(Vector<VariationCoefficientData> data) {
        this.writeSamplesName();
        numColumns = columns.length;
        this.writeData(data);
        numRows = rows.length;
    }

    public Object[][] getRows() {
        return rows;
    }

    public void setRows(Object[][] rows) {
        this.rows = rows;
        numRows = rows.length;
    }

    /**
     * Adds the name of the experiments in the "columns" variable. There are the title of the columns.
     * @param sampleNames list of all experiments names.
     */
    public void writeSamplesName() {
        columns = new String[columns_mol.size()];
        for (int i = 0; i < columns_mol.size(); i++) {
            columns[i] = (String) columns_mol.elementAt(i);
        }
    }

    /**
     * Takes all necessary information from the database and writes it in rows[][]. 
     * @param data 
     */
    public void writeData(Vector<VariationCoefficientData> data) {
        rows = new Object[data.size()][this.columns.length];

        for (int i = 0; i < data.size(); i++) {
            VariationCoefficientData vcdata = data.elementAt(i);
            rows[i][0] = vcdata.datasetName;
            rows[i][1] = vcdata.variationCoefficient;
            rows[i][2] = vcdata.numberMol;
            rows[i][3] = vcdata.NumberIdentMol;
            rows[i][4] = vcdata.numberExperiments;
        }


    }

    public int getColumnCount() {
        return numColumns;
    }

    public int getRowCount() {
        return numRows;
    }

    public Object getValueAt(final int row, final int column) {
        return rows[row][column];
    }

    @Override
    public String getColumnName(int columnIndex) {
        String str = columns[columnIndex];
        /* if (columnIndex == sortCol && columnIndex != 0)
        str += isSortAsc ? " >>" : " <<";*/
        return str;
    }

    @Override
    public Class<?> getColumnClass(int c) {
        if (getValueAt(0, c) != null) {
            return getValueAt(0, c).getClass();
        } else {
            return Object.class;
        }
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        rows[row][column] = aValue;
        fireTableCellUpdated(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
      
    public void setColumnCount(int count) {
        this.numColumns = count;
    }

   
    public Object[][] getData() {
        return rows;
    }

    public DatasetType getType() {
        return null;
    }

    public int getFixColumns() {
        return 0;
    }

    public void removeRows() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void addColumn(String ColumnName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
