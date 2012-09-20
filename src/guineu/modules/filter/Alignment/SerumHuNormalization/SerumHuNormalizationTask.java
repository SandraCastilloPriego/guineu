/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.filter.Alignment.SerumHuNormalization;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.R.RUtilities;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.linear.BlockRealMatrix;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.optimization.fitting.PolynomialFitter;
import org.apache.commons.math.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math.stat.correlation.SpearmansCorrelation;
import org.rosuda.JRI.Rengine;

class SerumHuNormalizationTask extends AbstractTask {

        private Dataset peakLists[];
        // Processed rows counter
        private int processedRows, totalRows;
        private double[] xval, yval;
        private double loessBandwidth;
        private int iterations;
        private File fileName, informationFile;
        private boolean extrapolation;
        HashMap<Integer, PolynomialSplineFunction> functions;
        HashMap<Integer, Integer> correlated;
        private String message = "Human serum normalization";

        public SerumHuNormalizationTask(Dataset[] peakLists, ParameterSet parameters) {

                this.peakLists = peakLists;
                this.loessBandwidth = parameters.getParameter(SerumHuNormalizationParameters.loessBand).getValue();
                this.iterations = parameters.getParameter(SerumHuNormalizationParameters.iterations).getValue();
                this.fileName = parameters.getParameter(SerumHuNormalizationParameters.filename).getValue();
                this.informationFile = parameters.getParameter(SerumHuNormalizationParameters.infoFilename).getValue();
                this.extrapolation = parameters.getParameter(SerumHuNormalizationParameters.extrapolation).getValue();
                this.functions = new HashMap<Integer, PolynomialSplineFunction>();
                this.correlated = new HashMap<Integer, Integer>();

        }

        public String getTaskDescription() {
                return message;
        }

        public double getFinishedPercentage() {
                if (totalRows == 0) {
                        return 0f;
                }
                return (double) processedRows / (double) totalRows;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        /**
         * @see Runnable#run()
         */
        public void run() {
                setStatus(TaskStatus.PROCESSING);

                for (Dataset data : this.peakLists) {
                        Dataset newData = data.clone();
                        GUIUtils.showNewTable(newData, true);
                        normalize(data, newData);
                        writeInfo();
                }
                setStatus(TaskStatus.FINISHED);
        }

        private void normalize(Dataset data, Dataset newData) {
                PeakListRow ids = data.getRow(0);
                PeakListRow runOrder = data.getRow(1);
                this.createCurves(data, runOrder, ids);
                plotInterpolations(data);
                message = "Normalizing";
                this.totalRows = data.getNumberRows() - 2;
                this.processedRows = 0;
                Vector<String> names = data.getAllColumnNames();
                for (int i = 2; i < data.getNumberRows(); i++) {
                        this.processedRows++;
                        PeakListRow row = data.getRow(i);
                        PeakListRow newrow = newData.getRow(i);

                        // Get the interpolation of all the human serum points using Loess 
                        PolynomialSplineFunction function = null;
                        try {
                                fillData(ids, runOrder, row, names);
                                if (yval.length > 2) {
                                        function = createCurve();
                                } else {
                                        // If the molecule doesn't have the same molecule in the standard serum tries to find the best match from the others
                                        function = searchBestFunction(data, ids, runOrder, names, row);
                                }
                        } catch (ClassCastException ex) {
                                System.out.println(row.getID());
                        }

                        if (function != null) {
                                // Prepare the points for the extrapolation
                                PolynomialFunction extrapolationFunction = null;
                                if (this.extrapolation) {
                                        List<Double> points = new ArrayList<Double>();
                                        for (int e = 0; e
                                                < row.getNumberPeaks(); e++) {
                                                try {
                                                        points.add(function.value((Double) runOrder.getPeak(names.elementAt(e))));
                                                } catch (ArgumentOutsideDomainException ex) {
                                                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE,
                                                                null, ex);
                                                }
                                        }

                                        // Extrapolation function
                                        extrapolationFunction = this.fittPolinomialFunction(runOrder, names, points);
                                }
                                double lastPoint = 0;
                                for (int e = 0; e < row.getNumberPeaks(); e++) {
                                        String sampleName = names.elementAt(e);
                                        try {

                                                if ((Double) ids.getPeak(sampleName) == 0) {
                                                        lastPoint = function.value((Double) runOrder.getPeak(sampleName));
                                                }
                                                double value = (Double) row.getPeak(sampleName) / function.value((Double) runOrder.getPeak(names.elementAt(e)));
                                                newrow.setPeak(names.elementAt(e), value);
                                        } catch (ArgumentOutsideDomainException ex) {
                                                //if the value has to be extrapolated
                                                if (extrapolation && extrapolationFunction != null) {
                                                        double value = (Double) row.getPeak(sampleName) / extrapolationFunction.value((Double) runOrder.getPeak(names.elementAt(e)));
                                                        newrow.setPeak(sampleName, value);
                                                } else {
                                                        double value = (Double) row.getPeak(sampleName) / lastPoint;//extrapolationFunction.value((Double) runOrder.getPeak(names.elementAt(e)));
                                                        newrow.setPeak(sampleName, value);
                                                }

                                        }
                                }
                        }
                }

        }

        private void fillData(PeakListRow ids, PeakListRow runOrder, PeakListRow row, Vector<String> sampleNames) throws ClassCastException {
                List<Double> yvalList = new ArrayList<Double>();
                List<Double> xvalList = new ArrayList<Double>();
                for (int i = 0; i < row.getNumberPeaks(); i++) {
                        String name = sampleNames.elementAt(i);
                        if ((Double) ids.getPeak(name) == 0 && (Double) row.getPeak(name) > 0) {
                                xvalList.add((Double) runOrder.getPeak(name));
                                yvalList.add((Double) row.getPeak(name));
                        }
                }

                xval = new double[xvalList.size()];
                yval = new double[yvalList.size()];
                for (int i = 0; i < xvalList.size(); i++) {
                        xval[i] = xvalList.get(i);
                        if (yvalList.get(i) != Double.NaN) {
                                yval[i] = yvalList.get(i);
                        }
                }
        }

        private PolynomialSplineFunction createCurve() {
                try {
                        // if there are more than 2 points in the model
                        LoessInterpolator loess = null;
                        if (this.loessBandwidth == 0) {
                                loess = new LoessInterpolator();
                        } else {
                                loess = new LoessInterpolator(this.loessBandwidth, this.iterations);
                        }
                        PolynomialSplineFunction function = loess.interpolate(xval, yval);
                        return function;

                } catch (MathException ex) {
                        try {
                                LoessInterpolator loess = new LoessInterpolator(0.7, 4);
                                PolynomialSplineFunction function = loess.interpolate(xval, yval);
                                return function;
                        } catch (MathException ex1) {
                                Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex1);
                                return null;
                        }
                }
        }

        private void createCurves(Dataset data, PeakListRow runOrder, PeakListRow ids) {
                Vector<String> names = data.getAllColumnNames();
                for (int i = 2; i < data.getNumberRows(); i++) {
                        PolynomialSplineFunction function = null;
                        PeakListRow row = data.getRow(i);
                        try {
                                fillData(ids, runOrder, row, names);
                                if (yval.length > 2) {
                                        function = createCurve();
                                        this.functions.put(new Integer(row.getID()), function);
                                }
                        } catch (ClassCastException ex) {
                                System.out.println(row.getID());
                        }
                }
        }

        private PolynomialFunction fittPolinomialFunction(PeakListRow runOrder, Vector<String> names, List<Double> data) {
                // Add the maximun number of iterations in GaussNewtonOptimizer
                GaussNewtonOptimizer optimizer = new GaussNewtonOptimizer(false);
                PolynomialFitter fitter = new PolynomialFitter(5, optimizer);
                for (int i = 0; i < data.size(); i++) {
                        Double point = data.get(i);
                        fitter.addObservedPoint(1, (Double) runOrder.getPeak(names.elementAt(i)), point);
                }
                try {
                        PolynomialFunction function = fitter.fit();
                        return function;
                } catch (Exception ex) {
                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                }
        }

        private PolynomialSplineFunction searchBestFunction(Dataset data, PeakListRow ids, PeakListRow runOrder, Vector<String> names, PeakListRow comparedRow) {
                PolynomialSplineFunction bestFunction = null;
                double bestValue = 0;
                int bestID = 0;
                for (int i = 2; i < data.getNumberRows(); i++) {
                        PeakListRow row = data.getRow(i);

                        RealMatrix m = this.getMatrix(comparedRow, row, names);
                        if (m.getData().length > 3) {
                                SpearmansCorrelation scorrelation = new SpearmansCorrelation(m);
                                PearsonsCorrelation correlation = scorrelation.getRankCorrelation();
                                double value = correlation.getCorrelationMatrix().getEntry(1, 0);
                                if (value > bestValue && functions.get(row.getID()) != null) {
                                        bestValue = value;
                                        bestID = row.getID();
                                        bestFunction = functions.get(row.getID());
                                }
                        } else {
                                return null;
                        }

                }
                this.correlated.put(comparedRow.getID(), bestID);
                return bestFunction;
        }

        private RealMatrix getMatrix(PeakListRow row, PeakListRow row2, Vector<String> sampleNames) {

                List<Double[]> data = new ArrayList<Double[]>();
                for (int i = 0; i < sampleNames.size(); i++) {
                        try {
                                if ((Double) row.getPeak(sampleNames.elementAt(i)) != Double.NaN && (Double) row2.getPeak(sampleNames.elementAt(i)) != Double.NaN
                                        && (Double) row.getPeak(sampleNames.elementAt(i)) != 0.0 && (Double) row2.getPeak(sampleNames.elementAt(i)) != 0.0) {
                                        Double[] dat = new Double[2];
                                        dat[0] = (Double) row.getPeak(sampleNames.elementAt(i));
                                        dat[1] = (Double) row2.getPeak(sampleNames.elementAt(i));
                                        data.add(dat);
                                }
                        } catch (Exception e) {
                        }
                }

                double[][] dataMatrix = new double[data.size()][2];
                int count = 0;
                for (Double[] dat : data) {
                        dataMatrix[count][0] = dat[0];
                        dataMatrix[count++][1] = dat[1];
                }
                RealMatrix matrix = new BlockRealMatrix(dataMatrix);
                return matrix;

        }

        private void writeInfo() {
                message = "Writing information";
                FileWriter fstream = null;
                try {
                        fstream = new FileWriter(this.informationFile.getAbsolutePath());
                        PrintWriter out = new PrintWriter(fstream);

                        for (Map.Entry<Integer, Integer> entry : correlated.entrySet()) {
                                Integer key = entry.getKey();
                                Integer value = entry.getValue();   
                                out.println(String.valueOf(key) + " - " + String.valueOf(value));
                        }
                } catch (IOException ex) {
                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                        try {
                                fstream.close();
                        } catch (IOException ex) {
                                Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
        }

        private void plotInterpolations(Dataset data) {
                this.message = "Creating Plots";
                PeakListRow runOrder = data.getRow(1);
                final Rengine rEngine;
                try {
                        rEngine = RUtilities.getREngine();
                } catch (Throwable t) {

                        throw new IllegalStateException(
                                "q-value test requires R but it couldn't be loaded (" + t.getMessage() + ')');
                }
                synchronized (RUtilities.R_SEMAPHORE) {
                        this.totalRows = data.getNumberRows() - 2;
                        Vector<String> names = data.getAllColumnNames();
                        // assing the values to the matrix
                        String path = this.fileName.getAbsolutePath();
                        path = path.replaceAll("\\\\", "/");
                        rEngine.eval("pdf(\"" + path + "\")");
                        for (int row = 2; row < data.getNumberRows(); row++) {
                                rEngine.eval("p <- vector(mode=\"numeric\",length=" + data.getNumberCols() + ")");
                                rEngine.eval("loess <- vector(mode=\"numeric\",length=" + data.getNumberCols() + ")");
                                rEngine.eval("time <- vector(mode=\"numeric\",length=" + data.getNumberCols() + ")");
                                rEngine.eval("color <- vector(mode=\"numeric\",length=" + data.getNumberCols() * 2 + ")");
                                PolynomialSplineFunction function = this.functions.get(data.getRow(row).getID());
                                double lastPoint = 0;
                                for (int col = 0; col < data.getNumberCols(); col++) {
                                        int r = col + 1;
                                        rEngine.eval("p[" + r + "] <- " + data.getRow(row).getPeak(names.elementAt(col)));

                                        if (function != null) {
                                                try {
                                                        rEngine.eval("loess[" + r + "] <- " + function.value((Double) runOrder.getPeak(names.elementAt(col))));
                                                        lastPoint = function.value((Double) runOrder.getPeak(names.elementAt(col)));
                                                } catch (ArgumentOutsideDomainException ex) {
                                                        rEngine.eval("loess[" + r + "] <- " + lastPoint);
                                                }
                                        }
                                        rEngine.eval("time[" + r + "] <- " + runOrder.getPeak(names.elementAt(col)));

                                }

                                rEngine.eval("plot(p~time, xlab=\"Running time\", ylab=\"Intensity\")");
                                rEngine.eval("lines(loess~time)");
                                rEngine.eval("title(main = " + data.getRow(row).getID() + ")");
                                this.processedRows++;
                        }
                        rEngine.eval("dev.off()");

                }
        }
}
