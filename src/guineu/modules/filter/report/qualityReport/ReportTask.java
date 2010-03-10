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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;

/**
 *
 * @author scsandra
 */
public class ReportTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private String fileName,  date,  sampleSet,  ionMode,  sampleType,  comments = "",  injection,  outputFile;
    private double totalRows,  processedRows;
    DescriptiveStatistics Stats[], superStats[];

    public ReportTask(Desktop desktop, SimpleParameterSet parameters) {
        this.desktop = desktop;
        this.fileName = (String) parameters.getParameterValue(ReportParameters.filename);
        this.date = (String) parameters.getParameterValue(ReportParameters.date);
        this.sampleSet = (String) parameters.getParameterValue(ReportParameters.sampleSet);
        this.ionMode = (String) parameters.getParameterValue(ReportParameters.ionModeCombo);
        this.injection = (String) parameters.getParameterValue(ReportParameters.injection) + " ul";
        this.sampleType = (String) parameters.getParameterValue(ReportParameters.typeCombo);
        this.outputFile = (String) parameters.getParameterValue(ReportParameters.outputFilename);
        this.comments = (String) parameters.getParameterValue(ReportParameters.area);
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
            if (this.outputFile != null) {
                writeHTML(samples);
            }
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

    private void writeHTML(List<sample> samples) {
        try {
            DecimalFormat formatter = new DecimalFormat("####.##");
            FileWriter fstream = new FileWriter(outputFile);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write("<html><title>Quality control form for global lipidomics platform.doc</title>");
            out.write("<style type=\"text/css\">");
            out.write("table, td {   border-color: #000;    border-style: solid;}");
            out.write("table {    border-width: 0 0 1px 1px;    border-spacing: 0;    border-collapse: collapse;}");
            out.write("td {    margin: 0;    padding: 4px;    border-width: 1px 1px 0 0;   }");
            out.write(".td1 {    margin: 0;    padding: 4px;    border-top-width: 2px ;  }");
            out.write("</style>");


            out.write("</head><body>");
            out.write("<br><h1><font color=\"#365F91\" size=\"5\" face=\"Cambria\"><b>Quality control form for global lipidomics platform (UPLC-QTOFMS)</b></font></h1> <br>");

            out.write("<p><font size=\"4\" face=\"Calibri\"><b>Sample set:</b> " + sampleSet + "</font></p>");
            out.write("<p><font size=\"4\" face=\"Calibri\"><b>Ion mode:</b> " + ionMode + "</font></p>");
            out.write("<p><font size=\"4\" face=\"Calibri\"><b>Injection volume:</b> " + injection + "</font></p>");
            out.write("<p><font size=\"4\" face=\"Calibri\"><b>Sample type:</b> " + sampleType + "</font></p><br>");

            out.write("<p><font size=\"4\" face=\"Calibri\"><b>Basic parameters for seronorm control samples &amp; batch standard:</b></font></p>");


            out.write("<a name=\"0.1_table01\"></a><div align=\"left\"> <table width=\"899\" border=\"1\" cellspacing=\"0\">");
            out.write("<tr valign=\"top\" align=\"center\"><td rowspan=\"2\" height=\"15\">");
            out.write("<font size=\"2\" face=\"Calibri\"><b>Run name</b></font></td>");
            out.write("<td colspan=\"3\" align=\"center\"><font size=\"2\" face=\"Calibri\"><b>LysoPC</b></font></td>");
            out.write("<td colspan=\"3\"align=\"center\"><font size=\"2\" face=\"Calibri\"><b>PC</b></font></td>");
            out.write("<td colspan=\"3\"align=\"center\"><font size=\"2\" face=\"Calibri\"><b>TG</b></font></td>");
            out.write("<td colspan=\"2\"align=\"center\"><font size=\"2\" face=\"Calibri\"><b></b></font></td></tr>");

            out.write("<tr valign=\"top\" align=\"center\"><td height=\"15\"><font size=\"2\" face=\"Calibri\"><b>RT</b></font></td>");
            out.write("<td align=\"center\" width=\"200\"><font size=\"2\" face=\"Calibri\"><b>height/area</b></font></td>");
            out.write("<td align=\"center\" width=\"200\"><font size=\"2\" face=\"Calibri\"><b>LysoPC/LPCD3</b></font></td>");

            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>RT</b></font></td>");
            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>height/area</b></font></td>");
            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>PC/PCD6</b></font></td>");

            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>RT</b></font></td>");
            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>height/area</b></font></td>");
            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>TG/TGC13</b></font></td>");
            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>Date</b></font></td>");
            out.write("<td align=\"center\"><font size=\"2\" face=\"Calibri\"><b>Time</b></font></td></tr>");

            for (sample s : samples) {
                out.write("<tr valign=\"top\" align=\"center\"><td height=\"15\"><font size=\"2\" face=\"Calibri\">" + s.sampleName + "</font></td>");
                out.write(s.getFormat());
            }
            out.write("<tr valign=\"top\" align=\"center\"><td1><td class=\"td1\" height=\"15\"><font size=\"2\" face=\"Calibri\"> <b>MEAN</b></font></td></td1>");
            String format = "<td class=\"td1\"> " + String.valueOf(formatter.format(Stats[0].getMean()).toString()) + " </td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[1].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[2].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[3].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[4].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[5].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[6].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[7].getMean()).toString() + "</td>" +
                    "<td class=\"td1\">" + formatter.format(Stats[8].getMean()).toString() + "</td>" +
                    "<td class=\"td1\"></td><td class=\"td1\"></td></tr>";
            out.write(format);
            out.write("<tr valign=\"top\" align=\"center\"><td1><td height=\"15\"><font size=\"2\" face=\"Calibri\"> <b>RSD</b></font></td></td1>");
            format = "<td>" + formatter.format((Stats[0].getStandardDeviation() * 100) / Stats[0].getMean()).toString() + " </td>" +
                    "<td>" + formatter.format((Stats[1].getStandardDeviation() * 100) / Stats[1].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[2].getStandardDeviation() * 100) / Stats[2].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[3].getStandardDeviation() * 100) / Stats[3].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[4].getStandardDeviation() * 100) / Stats[4].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[5].getStandardDeviation() * 100) / Stats[5].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[6].getStandardDeviation() * 100) / Stats[6].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[7].getStandardDeviation() * 100) / Stats[7].getMean()).toString() + "</td>" +
                    "<td>" + formatter.format((Stats[8].getStandardDeviation() * 100) / Stats[8].getMean()).toString() + "</td>" +
                    "<td></td><td></td></tr>";
            out.write(format);

            out.write("</table></td></tr></table></div>");
            out.write("<p><font size=\"4\" face=\"Calibri\"><b>Comments: </b>" + comments + "</font></p>");
            out.write("</body></html>");



            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }


    }

    private void writeDataset(List<sample> samples) {
        DecimalFormat formatter = new DecimalFormat("####.##");

        SimpleOtherDataset dataset = new SimpleOtherDataset("Summary Report");
        for (int i = 1; i <= 12; i++) {
            dataset.AddNameExperiment(String.valueOf(i));
        }

        dataset.AddRow(getRow("Date:", date));

        dataset.AddRow(getRow("SampleSet:", sampleSet));

        dataset.AddRow(getRow("Ion Mode:", ionMode));

        dataset.AddRow(getRow("Injection volume:", injection));

        dataset.AddRow(getRow("Sample type:", sampleType));

        dataset.AddRow(getRow("", ""));
        dataset.AddRow(getRow("", ""));

        dataset.AddRow(getRow("Basic parameters for seronorm control samples & batch standard:", ""));

        dataset.AddRow(this.getTitle());

        Stats = new DescriptiveStatistics[9];
        for (int i = 0; i < 9; i++) {
            Stats[i] = new DescriptiveStatistics();
        }
        for (sample s : samples) {
            dataset.AddRow(s.getRow(Stats));
        }

        SimplePeakListRowOther row = new SimplePeakListRowOther();
        row.setPeak("1", "MEAN");
        for (int i = 0; i < 9; i++) {
            row.setPeak(String.valueOf(i + 2), formatter.format(Stats[i].getMean()).toString());
        }
        dataset.AddRow(row);

        row = new SimplePeakListRowOther();
        row.setPeak("1", "RSD");
        for (int i = 0; i < 9; i++) {
            row.setPeak(String.valueOf(i + 2), formatter.format((Stats[i].getStandardDeviation() * 100) / Stats[i].getMean()).toString());
        }
        dataset.AddRow(row);

        dataset.AddRow(getRow("", ""));
        dataset.AddRow(getRow("", ""));
        // row8
        SimplePeakListRowOther row8 = new SimplePeakListRowOther();
        row8.setPeak("1", "Additional parameters for seronorm control samples & batch standard:");
        dataset.AddRow(row8);

        dataset.AddRow(this.getTitle2());

        superStats = new DescriptiveStatistics[9];
        for (int i = 0; i < 9; i++) {
            superStats[i] = new DescriptiveStatistics();
        }
        for (sample s : samples) {
            dataset.AddRow(s.getRow2(superStats));
        }

        row = new SimplePeakListRowOther();
        row.setPeak("1", "MEAN");
        for (int i = 0; i < 9; i++) {
            row.setPeak(String.valueOf(i + 2), formatter.format(superStats[i].getMean()).toString());
        }
        dataset.AddRow(row);

        row = new SimplePeakListRowOther();
        row.setPeak("1", "RSD");
        for (int i = 0; i < 9; i++) {
            row.setPeak(String.valueOf(i + 2), formatter.format((superStats[i].getStandardDeviation() * 100) / superStats[i].getMean()).toString());
        }
        dataset.AddRow(row);

        dataset.AddRow(getRow("", ""));
        dataset.AddRow(getRow("", ""));
        dataset.AddRow(getRow("Comments:", comments));

        desktop.AddNewFile(dataset);

        //creates internal frame with the table
        DataTableModel model = new OtherDataModel(dataset);
        DataTable table = new PushableTable(model);
        DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

        desktop.addInternalFrame(frame);
        frame.setVisible(true);
    }

    private SimplePeakListRowOther getRow(String title, String content) {
        SimplePeakListRowOther row = new SimplePeakListRowOther();
        row.setPeak("1", title);
        row.setPeak("2", content);
        return row;
    }

    private SimplePeakListRowOther getTitle() {
        SimplePeakListRowOther row = new SimplePeakListRowOther();
        row.setPeak("1", "Sample name");
        row.setPeak("2", "LysoPC RT");
        row.setPeak("3", "LysoPC height/area");
        row.setPeak("4", "LysoPC height ratio");
        row.setPeak("5", "PC RT");
        row.setPeak("6", "PC height/area");
        row.setPeak("7", "PC height ratio");
        row.setPeak("8", "TG RT");
        row.setPeak("9", "TG height/area");
        row.setPeak("10", "TG height ratio");
        row.setPeak("11", "Date");
        row.setPeak("12", "Time");
        return row;
    }

    private SimplePeakListRowOther getTitle2() {
        SimplePeakListRowOther row = new SimplePeakListRowOther();
        row.setPeak("1", "Sample name");
        row.setPeak("2", "LysoPC height");
        row.setPeak("3", "PC height");
        row.setPeak("4", "TG height");
        row.setPeak("5", "*LysoPC height");
        row.setPeak("6", "*LysoPC RT");
        row.setPeak("7", "*PC height");
        row.setPeak("8", "*PC RT");
        row.setPeak("9", "*TG height");
        row.setPeak("10", "*TG RT");
        return row;
    }

    class sample {

        String sampleName;
        String date;
        Data LysoPC, PC_50, TG_50, LPC, PC, TGC;
        DecimalFormat formatter;

        public void setSampleName(String name) {
            String sname = name.substring(name.indexOf("Name:") + 6, name.indexOf("Sample ID"));
            this.sampleName = sname;
            this.date = sname.substring(sname.lastIndexOf("_") + 1);
            formatter = new DecimalFormat("####.##");
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
        /* row.setPeak("1", "Sample name");
        row.setPeak("2", "LysoPC RT");
        row.setPeak("3", "LysoPC height/area");
        row.setPeak("4", "LysoPC height ratio");
        row.setPeak("5", "PC RT");
        row.setPeak("6", "PC height/area");
        row.setPeak("7", "PC height ratio");
        row.setPeak("8", "TG RT");
        row.setPeak("9", "TG height/area");
        row.setPeak("10", "TG height ratio");
        row.setPeak("11", "Date");
        row.setPeak("12", "Time");*/

        private SimplePeakListRowOther getRow(DescriptiveStatistics Stats[]) {
            SimplePeakListRowOther row = new SimplePeakListRowOther();
            int cont = 1;
            row.setPeak(String.valueOf(cont++), sampleName);
            //Lyso
            row.setPeak(String.valueOf(cont++), String.valueOf(LysoPC.RT));
            Stats[0].addValue(LysoPC.RT);

            row.setPeak(String.valueOf(cont++), String.valueOf(LysoPC.heightArea));
            Stats[1].addValue(LysoPC.heightArea);

            row.setPeak(String.valueOf(cont++), getLysoPCratio());
            Stats[2].addValue(Double.valueOf(getLysoPCratio()));

            //PC
            row.setPeak(String.valueOf(cont++), String.valueOf(PC_50.RT));
            Stats[3].addValue(PC_50.RT);

            row.setPeak(String.valueOf(cont++), String.valueOf(PC_50.heightArea));
            Stats[4].addValue(PC_50.heightArea);

            row.setPeak(String.valueOf(cont++), getPCratio());
            Stats[5].addValue(Double.valueOf(getPCratio()));

            //TG
            row.setPeak(String.valueOf(cont++), String.valueOf(TG_50.RT));
            Stats[6].addValue(TG_50.RT);

            row.setPeak(String.valueOf(cont++), String.valueOf(TG_50.heightArea));
            Stats[7].addValue(TG_50.heightArea);

            row.setPeak(String.valueOf(cont++), getTGratio());
            Stats[8].addValue(Double.valueOf(getTGratio()));

            row.setPeak(String.valueOf(cont++), date);
            row.setPeak(String.valueOf(cont++), LysoPC.time);
            return row;
        }

        private SimplePeakListRowOther getRow2(DescriptiveStatistics superStats[]) {
            SimplePeakListRowOther row = new SimplePeakListRowOther();
            int cont = 1;
            row.setPeak(String.valueOf(cont++), sampleName);
            row.setPeak(String.valueOf(cont++), getlysoPCHeight());
            superStats[0].addValue(Double.valueOf(getlysoPCHeight()));
            row.setPeak(String.valueOf(cont++), getPC_50Height());
            superStats[1].addValue(Double.valueOf(getPC_50Height()));
            row.setPeak(String.valueOf(cont++), getTG_50Height());
            superStats[2].addValue(Double.valueOf(getTG_50Height()));
            row.setPeak(String.valueOf(cont++), getLPCHeight());
            superStats[3].addValue(Double.valueOf(getLPCHeight()));
            row.setPeak(String.valueOf(cont++), getLPCRT());
            superStats[4].addValue(Double.valueOf(getLPCRT()));
            row.setPeak(String.valueOf(cont++), getPCHeight());
            superStats[5].addValue(Double.valueOf(getPCHeight()));
            row.setPeak(String.valueOf(cont++), getPCRT());
            superStats[6].addValue(Double.valueOf(getPCRT()));
            row.setPeak(String.valueOf(cont++), getTGCHeight());
            superStats[7].addValue(Double.valueOf(getTGCHeight()));
            row.setPeak(String.valueOf(cont++), getTGCRT());
            superStats[8].addValue(Double.valueOf(getTGCRT()));
            return row;
        }

        public String getLysoPCratio() {
            return formatter.format(LysoPC.height / LPC.height).toString();
        }

        public String getLysoPCRT() {
            return String.valueOf(LysoPC.RT);
        }

        public String getPCratio() {
            return formatter.format(PC_50.height / PC.height).toString();
        }

        public String getPC_50RT() {
            return String.valueOf(PC_50.RT);
        }

        public String getTGratio() {
            return formatter.format(TG_50.height / TGC.height).toString();
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

        public String getFormat() {
            String format = "<td>" + String.valueOf(LysoPC.RT) + " </td>" +
                    "<td>" + String.valueOf(LysoPC.heightArea) + "</td>" +
                    "<td>" + getLysoPCratio() + "</td>" +
                    "<td>" + String.valueOf(PC_50.RT) + "</td>" +
                    "<td>" + String.valueOf(PC_50.heightArea) + "</td>" +
                    "<td>" + getPCratio() + "</td>" +
                    "<td>" + String.valueOf(TG_50.RT) + "</td>" +
                    "<td>" + String.valueOf(TG_50.heightArea) + "</td>" +
                    "<td>" + getTGratio() + "</td>" +
                    "<td>" + date + "</td>" +
                    "<td>" + LysoPC.time + "</td></tr>";

            return format;
        }
    }

    class Data {

        String Name;
        double trace;
        double RT;
        double height;
        double area;
        double heightArea;
        double signalToNoise;
        String time;

        public Data(String[] fields) {
            try {
                DecimalFormat formatter = new DecimalFormat("####.##");
                this.Name = fields[2];
                this.trace = formatter.parse(fields[3]).doubleValue();
                this.RT = formatter.parse(fields[4]).doubleValue();
                this.height = formatter.parse(fields[5]).doubleValue();
                this.area = formatter.parse(fields[6]).doubleValue();
                this.heightArea = formatter.parse(fields[7]).doubleValue();
                this.time = fields[10];
            } catch (ParseException ex) {
                Logger.getLogger(ReportTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
