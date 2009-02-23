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

package guineu.modules.statistics.standardVariation;



import guineu.data.PeakListRow;
import guineu.data.maintable.DatasetDataModel;
import guineu.data.impl.SimpleDataset;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.util.Vector;


/**
 *
 * @author scsandra
 */
public class standarVariationTask implements Task {
    
   
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;      
    private Desktop desktop;
    private double progress = 0.0f;
    private String[] group1, group2;
    private SimpleDataset dataset;
   
    
    public standarVariationTask(String[] group1, String[] group2, SimpleDataset dataset, Desktop desktop){
        this.group1 = group1;
        this.group2 = group2;
        this.dataset = dataset;
        this.desktop = desktop;
       
    }
    public String getTaskDescription() {
        return "Standard Variation... ";
    }

    public double getFinishedPercentage() {
        return progress;
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
            status = TaskStatus.PROCESSING; 
            SimpleDataset newDataset = this.StandardVariation(group1);
            DatasetDataModel model = new DatasetDataModel(newDataset); 
            progress = 0.25f;            
            DataTable table = new PushableTable(model);
            table.formatNumbers(9);
            DataInternalFrame frame = new DataInternalFrame("Standard Variation 1", table.getTable(), new Dimension(450,450));
            desktop.addInternalFrame(frame);
            desktop.AddNewFile(newDataset);
            
            /*for(int i = 0; i < newDataset.getNumberMolecules(); i++){
                RegressionChart chart = new RegressionChart(newDataset.getConcentrationsID(i), newDataset.getDatasetName(), newDataset.getMolecule(i).getMolName());
                desktop.addInternalFrame(chart);
                chart.setVisible(true);
            }*/
            
            frame.setVisible(true);
            progress = 0.5f;   
            newDataset = this.StandardVariation(group2);
            model = new DatasetDataModel(newDataset); 
            progress = 0.75f;            
            table = new PushableTable(model);
            table.formatNumbers(9);
            frame = new DataInternalFrame("Standard Variation 2", table.getTable(), new Dimension(450,450));
            desktop.addInternalFrame(frame);
            desktop.AddNewFile(newDataset);
            frame.setVisible(true);
            progress = 1f;
            status = TaskStatus.FINISHED;
        }catch(Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }
    
    public SimpleDataset StandardVariation(String[] group){ 
        SimpleDataset newDataset = new SimpleDataset(this.dataset.getDatasetName() + " - Standard Variation");
        for (String experimentName : group) {
            newDataset.AddNameExperiment(experimentName);
        }        
        StandardUmol std = new StandardUmol(group);         
        for(PeakListRow mol : this.dataset.getRows()){
            if(mol.getStandard() == 1){
                std.setStandard(mol, mol.getName());                
            }
        }
        std.run();
        Vector<PeakListRow> mols = std.getMols();
        for(PeakListRow mol : mols){
            newDataset.AddRow(mol);
        }
        return newDataset;    
    }
   
}
