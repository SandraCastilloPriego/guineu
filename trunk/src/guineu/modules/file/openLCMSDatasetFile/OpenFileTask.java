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

package guineu.modules.file.openExperimentsData;


import guineu.data.datamodels.ExperimentDataModel;
import guineu.data.parser.impl.ExperimentParserXLS;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;

/**
 *
 * @author scsandra
 */
public class OpenFileTask implements Task {
    
    private String fileDir;    
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;      
    private Desktop desktop;  
    private ExperimentParserXLS parser;
    
    public OpenFileTask(String fileDir, Desktop desktop){
        if(fileDir != null)
            this.fileDir = fileDir;
        this.desktop = desktop;
        parser = new ExperimentParserXLS(fileDir); 
    }
    public String getTaskDescription() {
        return "Opening Experiment File... ";
    }

    public double getFinishedPercentage() {
        return parser.getProgress();
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
            this.openFile();          
        }catch(Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
    
    public void openFile(){
        status = TaskStatus.PROCESSING;        
        try {
            parser.fillData();
            desktop.AddNewFile(parser.getData());
           
            //creates internal frame with the table
            DataTableModel model = new ExperimentDataModel(parser.getData());                       
            DataTable table = new PushableTable(model);            
            DataInternalFrame frame = new DataInternalFrame(parser.getDatasetName(), table.getTable(), new Dimension(800,800));

            desktop.addInternalFrame(frame);
            frame.setVisible(true);      
        } catch (Exception ex) {                
        }               
        status = TaskStatus.FINISHED;
    }
}
