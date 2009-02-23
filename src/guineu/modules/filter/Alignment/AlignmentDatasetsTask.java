/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */

package guineu.modules.filter.Alignment;


import guineu.data.Dataset;
import guineu.data.impl.SimpleDataset;
import guineu.data.maintable.DatasetDataModel;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;

/**
 *
 * @author SCSANDRA
 */
public class AlignmentDatasetsTask implements Task{
    private Dataset[] datasets;    
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;      
    private Desktop desktop;
    private AlignmentParameters parameters;
    private Alignment comb;
    public AlignmentDatasetsTask (Dataset[]datasets, Desktop desktop, AlignmentParameters parameters){
        this.datasets = datasets;
        this.desktop = desktop;
        this.parameters = parameters;
        comb = new Alignment(datasets, parameters, desktop);
    }
    public String getTaskDescription() {
        return "Doing the alignment... ";
    }

    public double getFinishedPercentage() {
        return comb.getProgress();
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void cancel() {
        status = TaskStatus.CANCELED;
    }

    public void run() {
        try{          
            this.alignment();          
        }catch(Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
    
    public void alignment(){
       status = TaskStatus.PROCESSING;
        comb.run();       
       
        SimpleDataset dataset = comb.getDataset();
        desktop.AddNewFile(dataset);
                        
        //creates internal frame with the table
        DataTableModel model = new DatasetDataModel(dataset);                       
        DataTable table = new PushableTable(model);
        table.formatNumbers(dataset.getType());
        DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800,800));
                        
        desktop.addInternalFrame(frame);
        frame.setVisible(true);        
       
        status = TaskStatus.FINISHED;
    }
   
}
