/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.prefilter.NameFilter;

import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDatasetOther;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.modules.mylly.alignment.scoreAligner.functions.Aligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.taskcontrol.Task.TaskStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author bicha
 */
public class NamePostFilterTask {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private List<GCGCData> datasets;
    private NameFilterParameters parameters;
    private int ID = 1;
    private Aligner aligner;

    public NamePostFilterTask(List<GCGCData> datasets, NameFilterParameters parameters) {
        this.datasets = datasets;
        this.parameters = parameters;
    }

    public String getTaskDescription() {
        return "Filtering files with Name Filter... ";
    }

    public double getFinishedPercentage() {
        return aligner.getProgress();
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
        status = TaskStatus.PROCESSING;
        try {
            NameFilterModule filter = new NameFilterModule();
            String names = (String) parameters.getParameterValue(NameFilterParameters.fileNames);
            filter.generateNewFilter(this.askParameters(names));
            List<GCGCData> newDatasets = filter.actualMap(datasets);

            for (GCGCData dates : newDatasets) {
                String name = dates.getName() + (String) parameters.getParameterValue(NameFilterParameters.suffix);
                SimpleDatasetOther newTableOther = this.writeDataset(dates.toList(), name);
            }

            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            Logger.getLogger(NameFilterTask.class.getName()).log(Level.SEVERE, null, ex);
            status = TaskStatus.ERROR;
        }
    }

    private SimpleDatasetOther writeDataset(List<GCGCDatum> data, String name) {
        SimpleDatasetOther datasetOther = new SimpleDatasetOther(name);
        datasetOther.setType(DatasetType.OTHER);
        datasetOther.AddNameExperiment("Id");
        datasetOther.AddNameExperiment("Name");
        datasetOther.AddNameExperiment("RT1");
        datasetOther.AddNameExperiment("RT2");
        datasetOther.AddNameExperiment("RTI");
        datasetOther.AddNameExperiment("Concentration");
        datasetOther.AddNameExperiment("Area");
        datasetOther.AddNameExperiment("CAS");
        datasetOther.AddNameExperiment("Quant Mass");
        datasetOther.AddNameExperiment("Similarity");
        datasetOther.AddNameExperiment("Spectrum");


        for (GCGCDatum mol : data) {
            PeakListRow row = new SimplePeakListRowOther();
            row.setPeak("Id", String.valueOf(mol.getId()));
            row.setPeak("Name", mol.getName());
            row.setPeak("RT1", String.valueOf(mol.getRT1()));
            row.setPeak("RT2", String.valueOf(mol.getRT2()));
            row.setPeak("RTI", String.valueOf(mol.getRTI()));
            row.setPeak("Concentration", String.valueOf(mol.getConcentration()));
            row.setPeak("Area", String.valueOf(mol.getArea()));
            row.setPeak("CAS", String.valueOf(mol.getCAS()));
            row.setPeak("Quant Mass", String.valueOf(mol.getQuantMass()));
            row.setPeak("Similarity", String.valueOf(mol.getSimilarity()));
            row.setPeak("Spectrum", String.valueOf(mol.getSpectrum()));
            datasetOther.AddRow(row);
        }

        return datasetOther;
    }

    public List<String> askParameters(String names) throws FileNotFoundException {
        File f = new File(names);
        if (f != null) {
            try {
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                String line = null;
                List<String> namesList = new ArrayList<String>();
                while ((line = (br.readLine())) != null) {
                    namesList.add(line);
                }
                return namesList;
            } catch (FileNotFoundException e) {
                return null;
            //	GCGCAlign.getMainWindow().displayErrorDialog("File " + f + " was not found");
            } catch (IOException e) {
                return null;
            //	GCGCAlign.getMainWindow().displayErrorDialog(e);
            }
        }
        return null;
    }
}
