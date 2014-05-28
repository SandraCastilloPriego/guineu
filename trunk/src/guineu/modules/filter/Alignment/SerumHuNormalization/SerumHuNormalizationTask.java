/*
 * Copyright 2007-2013 VTT Biotechnology
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.interpolation.SplineInterpolator;
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
        private String order, id, batchesName;
        HashMap<Integer, List<PolynomialSplineFunction>> functions;
        HashMap<Integer, Integer> correlated;
        private String message = "Human serum normalization";
        
        private PeakListRow ids, runOrder, batches;

        public SerumHuNormalizationTask(Dataset[] peakLists, ParameterSet parameters) {

                this.peakLists = peakLists;
                this.loessBandwidth = parameters.getParameter(SerumHuNormalizationParameters.loessBand).getValue();
                this.iterations = parameters.getParameter(SerumHuNormalizationParameters.iterations).getValue();
                this.fileName = parameters.getParameter(SerumHuNormalizationParameters.filename).getValue();
                this.informationFile = parameters.getParameter(SerumHuNormalizationParameters.infoFilename).getValue();
                this.extrapolation = parameters.getParameter(SerumHuNormalizationParameters.extrapolation).getValue();

                this.order = parameters.getParameter(SerumHuNormalizationParameters.order).getValue();
                this.id = parameters.getParameter(SerumHuNormalizationParameters.identifier).getValue();
                this.batchesName = parameters.getParameter(SerumHuNormalizationParameters.batches).getValue();

                this.functions = new HashMap<Integer, List<PolynomialSplineFunction>>();
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
                        //writeInfo();
                       // plotInterpolations(data);
                }
                setStatus(TaskStatus.FINISHED);
        }

        private void normalize(Dataset data, Dataset newData) {
                // First row => ids of the samples ( 0 == standard serum, 1 == normal sample)
                this.ids = data.getRow(0).clone();     

                // Second row => run order
                this.runOrder = data.getRow(0).clone();

                // Third row => different data sets
                this.batches = data.getRow(0).clone();
                
                for (String name : data.getAllColumnNames()) {
                        ids.setPeak(name, data.getParametersValue(name, this.id));
                        runOrder.setPeak(name, data.getParametersValue(name, this.order));
                        batches.setPeak(name, data.getParametersValue(name, this.batchesName));
                }


                int numBatches = 1;
                double n = (Double) batches.getPeak(data.getAllColumnNames().get(0));

                for (String name : data.getAllColumnNames()) {
                        if ((Double) batches.getPeak(name) > n) {
                                numBatches++;
                                n = (Double) batches.getPeak(name);
                        }
                }

                this.createCurves(data, numBatches);
                for (int batch = 0; batch < numBatches; batch++) {
                        message = "Normalizing";
                        this.totalRows = data.getNumberRows();
                        this.processedRows = 0;
                        List<String> names = data.getAllColumnNames();
                        for (int i = 0; i < data.getNumberRows(); i++) {
                                this.processedRows++;
                                PeakListRow row = data.getRow(i);
                                PeakListRow newrow = newData.getRow(i);
                                try {
                                        // Get the interpolation of all the human serum points using Loess 
                                        PolynomialSplineFunction function = functions.get(row.getID()).get(batch);

                                        if (function != null) {
                                                // Prepare the points for the extrapolation
                                                PolynomialFunction extrapolationFunction = null;
                                                if (this.extrapolation) {
                                                        List<Double> points = new ArrayList<Double>();
                                                        for (int e = 0; e < row.getNumberPeaks(); e++) {
                                                                if ((Double) batches.getPeak(names.get(e)) == batch) {
                                                                        try {
                                                                                points.add(function.value((Double) runOrder.getPeak(names.get(e))));
                                                                        } catch (ArgumentOutsideDomainException ex) {
                                                                                Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE,
                                                                                        null, ex);
                                                                        }
                                                                }
                                                        }

                                                        // Extrapolation function
                                                        extrapolationFunction = this.fittPolinomialFunction(batches, runOrder, names, batch, points);
                                                }
                                                double lastPoint = 0;
                                                for (int e = 0; e < row.getNumberPeaks(); e++) {
                                                        String sampleName = names.get(e);
                                                        if ((Double) ids.getPeak(sampleName) > 0.0) {
                                                                if ((Double) batches.getPeak(sampleName) == batch) {
                                                                        try {

                                                                                if ((Double) ids.getPeak(sampleName) == 0) {
                                                                                        lastPoint = function.value((Double) runOrder.getPeak(sampleName));
                                                                                }
                                                                                double value = 0;
                                                                                try {
                                                                                        Double controlMol = function.value((Double) runOrder.getPeak(names.get(e)));
                                                                                        if (controlMol < 0.0 || controlMol == Double.NaN || controlMol == Double.POSITIVE_INFINITY || controlMol == Double.NEGATIVE_INFINITY) {
                                                                                                controlMol = getAverage(ids, row, e, names);
                                                                                        }


                                                                                        value = (Double) row.getPeak(sampleName) / controlMol;

                                                                                        if (value < 0.0 || value == Double.NaN || value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY) {
                                                                                                controlMol = getAverage(ids, row, e, names);
                                                                                        }

                                                                                        value = (Double) row.getPeak(sampleName) / controlMol;
                                                                                } catch (ClassCastException exception) {
                                                                                        value = -100;
                                                                                }
                                                                                newrow.setPeak(sampleName, value);
                                                                        } catch (ArgumentOutsideDomainException ex) {
                                                                                // ex.printStackTrace();
                                                                                //if the value has to be extrapolated
                                                                                if (extrapolation && extrapolationFunction != null) {
                                                                                        double value = 0;
                                                                                        try {

                                                                                                Double controlMol = extrapolationFunction.value((Double) runOrder.getPeak(names.get(e)));
                                                                                                if (controlMol < 0.0 || controlMol == Double.NaN || controlMol == Double.POSITIVE_INFINITY || controlMol == Double.NEGATIVE_INFINITY) {
                                                                                                        controlMol = getAverage(ids, row, e, names);
                                                                                                }
                                                                                                value = (Double) row.getPeak(sampleName) / controlMol;

                                                                                                if (value < 0.0 || value == Double.NaN || value == Double.POSITIVE_INFINITY || value == Double.NEGATIVE_INFINITY) {
                                                                                                        controlMol = getAverage(ids, row, e, names);
                                                                                                }

                                                                                                value = (Double) row.getPeak(sampleName) / controlMol;
                                                                                        } catch (ClassCastException exception) {
                                                                                                value = -100;
                                                                                        }
                                                                                        newrow.setPeak(sampleName, value);
                                                                                } else {
                                                                                        double value = 0;
                                                                                        try {
                                                                                                value = (Double) row.getPeak(sampleName) / lastPoint;//extrapolationFunction.value((Double) runOrder.getPeak(names.elementAt(e)));
                                                                                        } catch (ClassCastException exception) {
                                                                                                value = -100;
                                                                                        }
                                                                                        newrow.setPeak(sampleName, value);
                                                                                }

                                                                        }
                                                                }
                                                        }
                                                }
                                        } else {
                                                System.out.println("Function is null" + row.getID());
                                        }
                                } catch (Exception exception) {
                                        exception.printStackTrace();
                                        System.out.println(row.getID());

                                }
                        }
                }

        }

        private void fillData(PeakListRow batches, PeakListRow ids, PeakListRow runOrder, PeakListRow row, int batch, List<String> sampleNames) throws ClassCastException {
                List<Double> yvalList = new ArrayList<Double>();
                List<Double> xvalList = new ArrayList<Double>();
                for (int i = 0; i < row.getNumberPeaks(); i++) {
                        String name = sampleNames.get(i);

                        if ((Double) batches.getPeak(name) == batch) {
                                try {
                                        if ((Double) ids.getPeak(name) == 0 && (Double) row.getPeak(name) > 0) {
                                                xvalList.add((Double) runOrder.getPeak(name));
                                                yvalList.add((Double) row.getPeak(name));
                                        }
                                } catch (Exception e) {
                                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, e);
                                        System.out.println(name);
                                }
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
                        if (this.loessBandwidth == 0 && this.iterations == 0) {
                                loess = new LoessInterpolator();
                        } else {
                                loess = new LoessInterpolator(this.loessBandwidth, this.iterations);
                        }
                        double[] newy = loess.smooth(xval, yval);
                        PolynomialSplineFunction function = loess.interpolate(xval, newy);
                        return function;

                } catch (MathException ex) {
                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);

                        SplineInterpolator loess = new SplineInterpolator();
                        PolynomialSplineFunction function = loess.interpolate(xval, yval);
                        return function;

                }
        }

        private void createCurves(Dataset data, int numBatches) {
                this.totalRows = data.getNumberRows();
                this.processedRows = 0;
                this.message = "Fitting the standard curves";

                List<String> names = data.getAllColumnNames();
                for (int i = 0; i < data.getNumberRows(); i++) {
                        this.processedRows++;
                        PeakListRow row = data.getRow(i);
                        for (int batch = 0; batch < numBatches; batch++) {
                                PolynomialSplineFunction function = null;
                                try {
                                        fillData(batches, ids, runOrder, row, batch, names);
                                        if (yval.length > 2) {
                                                function = createCurve();


                                                //Checkin loess curve
                                                checkFunction(function, row, batch, batches, runOrder, names);



                                                if (function == null) {
                                                        System.out.println("nulllll");
                                                }
                                                if (this.functions.containsKey(row.getID())) {
                                                        List<PolynomialSplineFunction> functionList = this.functions.get(row.getID());
                                                        functionList.add(batch, function);
                                                        this.functions.put(row.getID(), functionList);
                                                } else {
                                                        List<PolynomialSplineFunction> functionList = new ArrayList<PolynomialSplineFunction>();
                                                        functionList.add(function);
                                                        this.functions.put(new Integer(row.getID()), functionList);
                                                }
                                        } else {
                                                this.searchBestFunction(data, row, numBatches);
                                        }
                                } catch (ClassCastException ex) {
                                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                }
        }

        private PolynomialFunction fittPolinomialFunction(PeakListRow batches, PeakListRow runOrder, List<String> names, int batch, List<Double> data) {
                // Add the maximun number of iterations in GaussNewtonOptimizer
                GaussNewtonOptimizer optimizer = new GaussNewtonOptimizer(false);
                PolynomialFitter fitter = new PolynomialFitter(5, optimizer);
                for (int i = 0; i < data.size(); i++) {
                        if ((Double) batches.getPeak(names.get(i)) == batch) {
                                Double point = data.get(i);
                                fitter.addObservedPoint(1, (Double) runOrder.getPeak(names.get(i)), point);
                        }
                }
                try {
                        PolynomialFunction function = fitter.fit();
                        return function;
                } catch (Exception ex) {
                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);
                        return null;
                }
        }

        private void searchBestFunction(Dataset data, PeakListRow comparedRow, int numBatches) {
                double bestValue = 0;
                int bestID = -1;
                List<String> names = data.getAllColumnNames();
                for (int i = 0; i < data.getNumberRows(); i++) {
                        PeakListRow row = data.getRow(i);

                        RealMatrix m = this.getMatrix(comparedRow, row, names);
                        if (m != null && m.getData().length > 3) {
                                SpearmansCorrelation scorrelation = new SpearmansCorrelation(m);
                                PearsonsCorrelation correlation = scorrelation.getRankCorrelation();
                                double value = correlation.getCorrelationMatrix().getEntry(1, 0);
                                if (value > bestValue && functions.get(row.getID()) != null && row.getID() != comparedRow.getID()) {
                                        List<PolynomialSplineFunction> functionList = functions.get(row.getID());
                                        if (functionList.size() == numBatches) {
                                                bestValue = value;
                                                bestID = row.getID();
                                        }
                                }
                        }

                }
                if (bestID > -1) {
                        List<PolynomialSplineFunction> functionList = functions.get(bestID);
                        functions.put(comparedRow.getID(), functionList);

                }

                this.correlated.put(comparedRow.getID(), bestID);
        }

        private RealMatrix getMatrix(PeakListRow row, PeakListRow row2, List<String> sampleNames) {

                List<Double[]> data = new ArrayList<Double[]>();
                for (int i = 0; i < sampleNames.size(); i++) {
                        try {
                                if ((Double) row.getPeak(sampleNames.get(i)) != Double.NaN && (Double) row2.getPeak(sampleNames.get(i)) != Double.NaN
                                        && (Double) row.getPeak(sampleNames.get(i)) != 0.0 && (Double) row2.getPeak(sampleNames.get(i)) != 0.0) {
                                        Double[] dat = new Double[2];
                                        dat[0] = (Double) row.getPeak(sampleNames.get(i));
                                        dat[1] = (Double) row2.getPeak(sampleNames.get(i));
                                        data.add(dat);
                                }
                        } catch (Exception e) {
                        }
                }

                if (data.size() > 0) {
                        double[][] dataMatrix = new double[data.size()][2];
                        int count = 0;
                        for (Double[] dat : data) {
                                dataMatrix[count][0] = dat[0];
                                dataMatrix[count++][1] = dat[1];
                        }
                        RealMatrix matrix = new BlockRealMatrix(dataMatrix);
                        return matrix;
                } else {
                        return null;
                }

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
                final Rengine rEngine;
                try {
                        rEngine = RUtilities.getREngine();
                } catch (Throwable t) {

                        throw new IllegalStateException(
                                "Plotting requires R but it couldn't be loaded (" + t.getMessage() + ')');
                }
                synchronized (RUtilities.R_SEMAPHORE) {
                        this.totalRows = data.getNumberRows();
                        this.processedRows = 0;
                        message = "Plotting curves";
                        List<String> names = data.getAllColumnNames();
                        // assing the values to the matrix
                        String path = this.fileName.getAbsolutePath();


                        path = path.replaceAll("\\\\", "/");
                        rEngine.eval("pdf(\"" + path + "\")");
                        rEngine.eval("p <- vector(mode=\"numeric\",length=" + data.getNumberCols() + ")");
                        rEngine.eval("loess <- vector(mode=\"numeric\",length=" + data.getNumberCols() + ")");
                        rEngine.eval("time <- vector(mode=\"numeric\",length=" + data.getNumberCols() + ")");
                        rEngine.eval("color <- vector(mode=\"numeric\",length=" + data.getNumberCols() * 2 + ")");

                        for (int row = 0; row < data.getNumberRows(); row++) {

                                List<PolynomialSplineFunction> functions = this.functions.get(data.getRow(row).getID());
                                double lastPoint = 0;
                                for (int col = 0; col < data.getNumberCols(); col++) {
                                        int r = col + 1;
                                        rEngine.eval("p[" + r + "] <- " + data.getRow(row).getPeak(names.get(col)));


                                        if (functions != null) {
                                                PolynomialSplineFunction function = functions.get(Double.valueOf((Double) batches.getPeak(names.get(col))).intValue());
                                                if (function != null) {
                                                        try {
                                                                rEngine.eval("loess[" + r + "] <- "
                                                                        + function.value((Double) runOrder.getPeak(names.get(col))));
                                                                lastPoint = function.value((Double) runOrder.getPeak(names.get(col)));
                                                        } catch (ArgumentOutsideDomainException ex) {
                                                                rEngine.eval("loess[" + r + "] <- "
                                                                        + lastPoint);
                                                        }
                                                }
                                        }

                                        rEngine.eval("time[" + r + "] <- " + col);

                                }

                                rEngine.eval("plot(p~time, xlab=\"Running time\", ylab=\"Intensity\")");
                                rEngine.eval("lines(loess~time)");
                                String name = data.getRow(row).getID() + "-" + data.getRow(row).getName();
                                rEngine.eval("title(main = \"" + name + "\")");
                                this.processedRows++;
                        }
                        rEngine.eval("dev.off()");

                }
        }

        private void checkFunction(PolynomialSplineFunction function, PeakListRow row, int batch, PeakListRow batches, PeakListRow runOrder, List<String> names) {
                boolean ok = true;

                for (String sampleNames : names) {
                        if (batch == (Integer)batches.getPeak(sampleNames)) {
                                try {
                                        double value = function.value((Double) runOrder.getPeak(sampleNames));
                                        if (value <= 0.0 || value == Double.NaN) {
                                                ok = false;
                                        }
                                } catch (ArgumentOutsideDomainException ex) {
                                        Logger.getLogger(SerumHuNormalizationTask.class.getName()).log(Level.SEVERE, null, ex);
                                }
                        }
                }

                if (!ok) {
                        System.out.println("values are zero, negative or nan " + row.getID());
                }
        }

        private Double getAverage(PeakListRow ids, PeakListRow row, int e, List<String> names) {
                String actualName = names.get(e);
                boolean passedName = false;
                double initValue = 1;
                double lastValue = 1;
                for (String name : names) {
                        if ((Double) ids.getPeak(name) == 0.0) {
                                if (!passedName) {
                                        initValue = (Double) row.getPeak(name);
                                } else {
                                        lastValue = (Double) row.getPeak(name);
                                        break;
                                }

                        }
                        if (name.equals(actualName)) {
                                passedName = true;
                        }
                }

                return (initValue + lastValue) / 2;
        }
}
