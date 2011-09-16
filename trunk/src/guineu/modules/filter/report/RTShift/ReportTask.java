/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.filter.report.RTShift;

import com.csvreader.CsvReader;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.LCMSColumnName;
import guineu.parameters.SimpleParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author scsandra
 */
public class ReportTask extends AbstractTask {

        private Dataset dataset;
        private String fileName;
        private String reportFileName;
        private List<String> sampleNames;
        private double totalRows, processedRows;

        public ReportTask(Dataset simpleDataset, SimpleParameterSet parameters) {
                this.dataset = simpleDataset;
                this.fileName = parameters.getParameter(ReportParameters.filename).getValue().getAbsolutePath();
                this.reportFileName = parameters.getParameter(ReportParameters.reportFilename).getValue().getAbsolutePath();
                this.sampleNames = new ArrayList<String>();
                this.totalRows = dataset.getNumberRows();
                this.processedRows = 0;
        }

        public String getTaskDescription() {
                return "Report RT deviation... ";
        }

        public double getFinishedPercentage() {
                return processedRows / totalRows;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);
                        readFile();
                        saveRTCharts();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

        /**
         * Read the file with the name of the samples in order
         * @throws java.lang.Exception
         */
        private void readFile() throws Exception {
                FileReader fr = null;
                try {
                        fr = new FileReader(new File(fileName));
                } catch (Exception e) {
                        throw e;
                }
                CsvReader reader = new CsvReader(fr);
                String splitRow[];
                while (reader.readRecord()) {
                        splitRow = reader.getValues();
                        this.sampleNames.add(splitRow[0]);
                }
                reader.close();
                fr.close();
        }

        /**
         * For each selected row a chart is created and saved.
         */
        private void saveRTCharts() {
                for (PeakListRow row : dataset.getRows()) {
                        if (row.isSelected() && getStatus() == TaskStatus.PROCESSING) {
                                CategoryDataset data = createSampleDataset(row);
                                String lipidName = "MZ: " + String.valueOf(row.getVar(LCMSColumnName.MZ.getGetFunctionName()))
                                        + "RT: " + String.valueOf(row.getVar(LCMSColumnName.RT.getGetFunctionName()));
                                createChart(data, lipidName);
                        }
                        this.processedRows++;
                }
        }

        /**
         * Create the chart and save it into a png file.
         * @param dataset
         * @param lipidName
         */
        private void createChart(CategoryDataset dataset, String lipidName) {
                try {

                        JFreeChart chart = ChartFactory.createLineChart("RT shift", "Samples", "RT", dataset, PlotOrientation.VERTICAL, false, false, false);

                        // Chart characteristics
                        CategoryPlot plot = (CategoryPlot) chart.getPlot();
                        final NumberAxis axis = (NumberAxis) plot.getRangeAxis();
                        axis.setAutoRangeIncludesZero(false);
                        axis.setAutoRangeMinimumSize(1.0);
                        LineAndShapeRenderer categoryRenderer = new LineAndShapeRenderer();
                        categoryRenderer.setSeriesLinesVisible(0, false);
                        categoryRenderer.setSeriesShapesVisible(0, true);
                        plot.setRenderer(categoryRenderer);

                        // Save all the charts in the folder choosen by the user
                        ChartUtilities.saveChartAsPNG(new File(this.reportFileName + "/RT Shift:" + lipidName + ".png"), chart, 1000, (500));
                } catch (IOException ex) {
                        Logger.getLogger(ReportTask.class.getName()).log(Level.SEVERE, null, ex);
                }

        }

        /**
         * Create the dataset for the chart. Each sample represents
         * one category.
         * @param row The intensities and general data of one metabolite
         * @return CategoryDataset
         */
        private CategoryDataset createSampleDataset(PeakListRow row) {

                DefaultCategoryDataset data = new DefaultCategoryDataset();
                int cont = 1;
                for (String sampleName : sampleNames) {
                        try {
                                sampleName += "01.CDF peak retention time";
                                double value = (Double) row.getPeak(sampleName);
                                data.addValue(value, cont + " => " + sampleName, String.valueOf(cont));
                                cont++;
                        } catch (Exception e) {
                        }
                }


                return data;
        }
}
