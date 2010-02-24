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
package guineu.modules.filter.report.qualityReport;

import com.csvreader.CsvReader;
import guineu.data.datamodels.OtherDataModel;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimpleParameterSet;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class ReportTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private String fileName,  date,  sampleSet,  ionMode,  sampleType,  area ="";
    private double totalRows,  processedRows;

    public ReportTask(Desktop desktop, SimpleParameterSet parameters) {
        this.desktop = desktop;
        this.fileName = (String) parameters.getParameterValue(ReportParameters.filename);
        this.date = (String) parameters.getParameterValue(ReportParameters.date);
        this.sampleSet = (String) parameters.getParameterValue(ReportParameters.sampleSet);
        this.ionMode = (String) parameters.getParameterValue(ReportParameters.ionModeCombo);
        this.sampleType = (String) parameters.getParameterValue(ReportParameters.typeCombo);
        this.area = (String) parameters.getParameterValue(ReportParameters.area);
        this.processedRows = 0;
    }

    public String getTaskDescription() {
        return "Report Intensities... ";
    }

    public double getFinishedPercentage() {
        return processedRows / totalRows;
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
        try {
            status = TaskStatus.PROCESSING;
            List<sample> samples = readFile();
            writeDataset(samples);
            status = TaskStatus.FINISHED;
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    /**
     * Read the file with the name of the samples in order
     * @throws java.lang.Exception
     */
    private List<sample> readFile() throws Exception {

        List<sample> Samples = new ArrayList<sample>();
        FileReader fr = null;
        try {
            fr = new FileReader(new File(fileName));
        } catch (Exception e) {
            throw e;
        }
        CsvReader reader = new CsvReader(fr);
        String splitRow[];
        sample newSample = null;
        while (reader.readRecord()) {
            splitRow = reader.getValues();
            try {


                if (splitRow[0].matches(".*Sample Name.*")) {
                    newSample = new sample();
                    newSample.setSampleName(splitRow[0]);
                }

                if (splitRow[2].matches(".*lysoPC_50.*")) {
                    newSample.setLysoPC(splitRow);
                } else if (splitRow[2].matches(".*PC_50.*")) {
                    newSample.setPC(splitRow);
                } else if (splitRow[2].matches(".*TG_50.*")) {
                    newSample.setTG(splitRow);
                } else if (splitRow[2].matches(".*LPC.*")) {
                    newSample.setLPC(splitRow);
                } else if (splitRow[2].matches(".*PCD.*")) {
                    newSample.setPCD(splitRow);
                } else if (splitRow[2].matches(".*TGC.*")) {
                    newSample.setTGC(splitRow);
                    Samples.add(newSample);
                }
            } catch (Exception e) {
            }
        }
        reader.close();
        fr.close();
        return Samples;
    }

    private void writeDataset(List<sample> samples) {
        SimpleOtherDataset dataset = new SimpleOtherDataset("Summary Report");
        for (int i = 1; i <= 10; i++) {
            dataset.AddNameExperiment(String.valueOf(i));
        }
        // row1
        SimplePeakListRowOther row1 = new SimplePeakListRowOther();
        row1.setPeak("1", "Date:");
        row1.setPeak("2", date);
        dataset.AddRow(row1);
        // row2
        SimplePeakListRowOther row2 = new SimplePeakListRowOther();
        row2.setPeak("1", "SampleSet:");
        row2.setPeak("2", sampleSet);
        dataset.AddRow(row2);
        // row3
        SimplePeakListRowOther row3 = new SimplePeakListRowOther();
        row3.setPeak("1", "Ion Mode:");
        row3.setPeak("2", ionMode);
        dataset.AddRow(row3);
        // row4
        SimplePeakListRowOther row4 = new SimplePeakListRowOther();
        row4.setPeak("1", "Sample type:");
        row4.setPeak("2", sampleType);
        dataset.AddRow(row4);
        SimplePeakListRowOther rowblank = new SimplePeakListRowOther();
        dataset.AddRow(rowblank);
        rowblank = new SimplePeakListRowOther();
        dataset.AddRow(rowblank);
        // row5
        SimplePeakListRowOther row5 = new SimplePeakListRowOther();
        row5.setPeak("1", "Basic parameters for seronorm control samples & batch standard:");
        dataset.AddRow(row5);
        // row6
        SimplePeakListRowOther row6 = new SimplePeakListRowOther();
        row6.setPeak("1", "Sample name");
        row6.setPeak("2", "LysoPC height ratio");
        row6.setPeak("3", "LysoPC RT");
        row6.setPeak("4", "PC height ratio");
        row6.setPeak("5", "PC RT");
        row6.setPeak("6", "TG height ratio");
        row6.setPeak("7", "TG RT");
        dataset.AddRow(row6);

        DescriptiveStatistics Stats[] = new DescriptiveStatistics[6];
        for (int i = 0; i < 6; i++) {
            Stats[i] = new DescriptiveStatistics();
        }
        for (sample s : samples) {
            int cont = 1;
            SimplePeakListRowOther row = new SimplePeakListRowOther();
            row.setPeak(String.valueOf(cont++), s.sampleName);
            row.setPeak(String.valueOf(cont++), s.getLysoPCratio());
            Stats[0].addValue(Double.valueOf(s.getLysoPCratio()));
            row.setPeak(String.valueOf(cont++), s.getLysoPCRT());
            Stats[1].addValue(Double.valueOf(s.getLysoPCRT()));
            row.setPeak(String.valueOf(cont++), s.getPCratio());
            Stats[2].addValue(Double.valueOf(s.getPCratio()));
            row.setPeak(String.valueOf(cont++), s.getPC_50RT());
            Stats[3].addValue(Double.valueOf(s.getPC_50RT()));
            row.setPeak(String.valueOf(cont++), s.getTGratio());
            Stats[4].addValue(Double.valueOf(s.getTGratio()));
            row.setPeak(String.valueOf(cont++), s.getTGRT());
            Stats[5].addValue(Double.valueOf(s.getTGRT()));
            dataset.AddRow(row);
        }

        SimplePeakListRowOther row = new SimplePeakListRowOther();
        row.setPeak("1", "MEAN");
        for (int i = 0; i < 6; i++) {
            row.setPeak(String.valueOf(i + 2), String.valueOf(Stats[i].getMean()));
        }
        dataset.AddRow(row);

        row = new SimplePeakListRowOther();
        row.setPeak("1", "RSD");
        for (int i = 0; i < 6; i++) {
            row.setPeak(String.valueOf(i + 2), String.valueOf((Stats[i].getStandardDeviation() * 100) / Stats[i].getMean()));
        }
        dataset.AddRow(row);

        rowblank = new SimplePeakListRowOther();
        dataset.AddRow(rowblank);
        rowblank = new SimplePeakListRowOther();
        dataset.AddRow(rowblank);
        // row8
        SimplePeakListRowOther row8 = new SimplePeakListRowOther();
        row8.setPeak("1", "Additional parameters for seronorm control samples & batch standard:");
        dataset.AddRow(row8);
        // row ..
        SimplePeakListRowOther row7 = new SimplePeakListRowOther();
        row7.setPeak("1", "Sample name");
        row7.setPeak("2", "LysoPC height");
        row7.setPeak("3", "PC height");
        row7.setPeak("4", "TG height");
        row7.setPeak("5", "*LysoPC height");
        row7.setPeak("6", "*LysoPC RT");
        row7.setPeak("7", "*PC height");
        row7.setPeak("8", "*PC RT");
        row7.setPeak("9", "*TG height");
        row7.setPeak("10", "*TG RT");
        dataset.AddRow(row7);

        DescriptiveStatistics superStats[] = new DescriptiveStatistics[9];
        for (int i = 0; i < 9; i++) {
            superStats[i] = new DescriptiveStatistics();
        }
        for (sample s : samples) {
            int cont = 1;
            row = new SimplePeakListRowOther();
            row.setPeak(String.valueOf(cont++), s.sampleName);
            row.setPeak(String.valueOf(cont++), s.getlysoPCHeight());
            superStats[0].addValue(Double.valueOf(s.getlysoPCHeight()));
            row.setPeak(String.valueOf(cont++), s.getPC_50Height());
            superStats[1].addValue(Double.valueOf(s.getPC_50Height()));
            row.setPeak(String.valueOf(cont++), s.getTG_50Height());
            superStats[2].addValue(Double.valueOf(s.getTG_50Height()));
            row.setPeak(String.valueOf(cont++), s.getLPCHeight());
            superStats[3].addValue(Double.valueOf(s.getLPCHeight()));
            row.setPeak(String.valueOf(cont++), s.getLPCRT());
            superStats[4].addValue(Double.valueOf(s.getLPCRT()));
            row.setPeak(String.valueOf(cont++), s.getPCHeight());
            superStats[5].addValue(Double.valueOf(s.getPCHeight()));
            row.setPeak(String.valueOf(cont++), s.getPCRT());
            superStats[6].addValue(Double.valueOf(s.getPCRT()));
            row.setPeak(String.valueOf(cont++), s.getTGCHeight());
            superStats[7].addValue(Double.valueOf(s.getTGCHeight()));
            row.setPeak(String.valueOf(cont++), s.getTGCRT());
            superStats[8].addValue(Double.valueOf(s.getTGCRT()));
            dataset.AddRow(row);
        }

        row = new SimplePeakListRowOther();
        row.setPeak("1", "MEAN");
        for (int i = 0; i < 9; i++) {
            row.setPeak(String.valueOf(i + 2), String.valueOf(superStats[i].getMean()));
        }
        dataset.AddRow(row);

        row = new SimplePeakListRowOther();
        row.setPeak("1", "RSD");
        for (int i = 0; i < 9; i++) {
            row.setPeak(String.valueOf(i + 2), String.valueOf((superStats[i].getStandardDeviation() * 100) / superStats[i].getMean()));
        }
        dataset.AddRow(row);

        rowblank = new SimplePeakListRowOther();
        dataset.AddRow(rowblank);
        rowblank = new SimplePeakListRowOther();
        dataset.AddRow(rowblank);
        // row9
        SimplePeakListRowOther row9 = new SimplePeakListRowOther();
        row9.setPeak("1", "Coments:");
        row9.setPeak("2", area);
        dataset.AddRow(row9);

        desktop.AddNewFile(dataset);

        //creates internal frame with the table
        DataTableModel model = new OtherDataModel(dataset);
        DataTable table = new PushableTable(model);
        DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

        desktop.addInternalFrame(frame);
        frame.setVisible(true);
    }

    class sample {

        String sampleName;
        Data LysoPC, PC_50, TG_50, LPC, PC, TGC;

        public void setSampleName(String name) {
            String sname = name.substring(name.indexOf("Name:") + 6, name.indexOf("Sample ID"));
            this.sampleName = sname;
        }

        public void setLysoPC(String[] data) {
            LysoPC = new Data(data);
        }

        public void setPC(String[] data) {
            PC_50 = new Data(data);
        }

        public void setTG(String[] data) {
            TG_50 = new Data(data);
        }

        public void setLPC(String[] data) {
            LPC = new Data(data);
        }

        public void setPCD(String[] data) {
            PC = new Data(data);
        }

        public void setTGC(String[] data) {
            TGC = new Data(data);
        }

        public String getLysoPCratio() {
            return String.valueOf(LysoPC.height / LPC.height);
        }

        public String getLysoPCRT() {
            return String.valueOf(LysoPC.RT);
        }

        public String getPCratio() {
            return String.valueOf(PC_50.height / PC.height);
        }

        public String getPC_50RT() {
            return String.valueOf(PC_50.RT);
        }

        public String getTGratio() {
            return String.valueOf(TG_50.height / TGC.height);
        }

        public String getTGRT() {
            return String.valueOf(TG_50.RT);
        }

        public String getTGCRT() {
            return String.valueOf(TGC.RT);
        }

        public String getPCRT() {
            return String.valueOf(PC.RT);
        }

        public String getLPCRT() {
            return String.valueOf(LPC.RT);
        }

        public String getPC_50Height() {
            return String.valueOf(PC_50.height);
        }

        public String getlysoPCHeight() {
            return String.valueOf(LysoPC.height);
        }

        public String getTG_50Height() {
            return String.valueOf(TG_50.height);
        }

        public String getLPCHeight() {
            return String.valueOf(LPC.height);
        }

        public String getPCHeight() {
            return String.valueOf(PC.height);
        }

        public String getTGCHeight() {
            return String.valueOf(TGC.height);
        }
    }

    class Data {

        String Name;
        double trace;
        double RT;
        double height;
        double area;
        double Height_area;
        double signalToNoise;

        public Data(String[] fields) {
            this.Name = fields[2];
            this.trace = Double.valueOf(fields[3]);
            this.RT = Double.valueOf(fields[4]);
            this.height = Double.valueOf(fields[5]);
            this.area = Double.valueOf(fields[6]);
        }
    }
}
