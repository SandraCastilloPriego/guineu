/*
 * Copyright 2007-2008 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package guineu.modules.filter.Alignment;


import guineu.data.Dataset;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.datamodels.DatasetLCMSDataModel;
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
       
        SimpleLCMSDataset dataset = comb.getDataset();
        desktop.AddNewFile(dataset);
                        
        //creates internal frame with the table
        DataTableModel model = new DatasetLCMSDataModel(dataset);
        DataTable table = new PushableTable(model);
        table.formatNumbers(dataset.getType());
        DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800,800));
                        
        desktop.addInternalFrame(frame);
        frame.setVisible(true);        
       
        status = TaskStatus.FINISHED;
    }
   
}
