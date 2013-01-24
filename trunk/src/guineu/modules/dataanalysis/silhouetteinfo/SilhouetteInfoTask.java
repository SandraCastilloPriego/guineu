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
package guineu.modules.dataanalysis.silhouetteinfo;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.main.GuineuCore;
import guineu.modules.R.RUtilities;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JInternalFrame;
import org.rosuda.JRI.Rengine;
import org.rosuda.javaGD.GDCanvas;

/**
 *
 * @author scsandra
 */
public class SilhouetteInfoTask extends AbstractTask {

        private Dataset dataset;
        private int progress = 0;
        private int k;
        public static GDCanvas _gdc;
        public int numberOfRows;

        public SilhouetteInfoTask(Dataset dataset, ParameterSet parameters) {
                this.dataset = dataset;
                this.k = parameters.getParameter(SilhouetteInfoParameters.k).getValue();
                this.numberOfRows = this.dataset.getNumberRows();
        }

        public String getTaskDescription() {
                return "Silhouette information... ";
        }

        public double getFinishedPercentage() {
                return (float) progress / numberOfRows;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                try {
                        final Rengine rEngine;
                        try {
                                rEngine = RUtilities.getREngine();
                        } catch (Throwable t) {

                                throw new IllegalStateException(
                                        "Silhouette information requires R but it couldn't be loaded (" + t.getMessage() + ')');
                        }
                        synchronized (RUtilities.R_SEMAPHORE) {
                                if (rEngine.eval("require(cluster)").asBool().isFALSE()) {
                                        setStatus(TaskStatus.ERROR);
                                        errorMessage = "The \"gplots\" R package couldn't be loaded - is it installed in R?";
                                }

                                rEngine.eval("library(cluster)");

                                int numberOfRows = this.isAnySelected(dataset);
                                boolean isAnySelected = true;
                                if (numberOfRows == 0) {
                                        numberOfRows = dataset.getNumberRows();
                                        isAnySelected = false;
                                }

                                rEngine.eval("dataset<- matrix(\"\",nrow =" + numberOfRows + ",ncol=" + dataset.getNumberCols() + ")");

                                List<String> columnNames = dataset.getAllColumnNames();

                                // assing the values to the matrix
                                int realRowIndex = 0;
                                for (int indexRow = 0; indexRow < dataset.getNumberRows(); indexRow++) {
                                        PeakListRow row = dataset.getRow(indexRow);
                                        if ((isAnySelected && row.isSelected()) || !isAnySelected) {
                                                for (int indexColumn = 0; indexColumn < dataset.getNumberCols(); indexColumn++) {

                                                        int r = realRowIndex + 1;
                                                        int c = indexColumn + 1;

                                                        double value = (Double) row.getPeak(columnNames.get(indexColumn));

                                                        if (!Double.isInfinite(value) && !Double.isNaN(value)) {

                                                                rEngine.eval("dataset[" + r + "," + c + "] = " + value);
                                                        } else {

                                                                rEngine.eval("dataset[" + r + "," + c + "] = NA");
                                                        }
                                                }
                                                realRowIndex++;
                                        }
                                        progress++;
                                }

                                // Window
                                _gdc = new GDCanvas(800, 800);
                                JInternalFrame f = new JInternalFrame("Silhouette information", true, true, true, true);
                                f.getContentPane().setLayout(new BorderLayout());
                                f.getContentPane().add(_gdc, BorderLayout.PAGE_END);
                                f.pack();
                                GuineuCore.getDesktop().addInternalFrame(f);

                                rEngine.eval("library(JavaGD)");
                                rEngine.eval("Sys.putenv('JAVAGD_CLASS_NAME'='guineu/modules/dataanalysis/silhouetteinfo/SIFrame')");
                                rEngine.eval("JavaGD(width=800, height=800)");


                                rEngine.eval("dataset <- apply(dataset, 2, as.numeric)");
                                rEngine.eval("pr4 <- pam(dataset, " + this.k + ")");
                                rEngine.eval("str(si <- silhouette(pr4))");
                                rEngine.eval("(ssi <- summary(si))");
                                rEngine.eval("plot(si, nmax = 80, cex.names = 0.5)");
                        }
                        rEngine.end();
                        setStatus(TaskStatus.FINISHED);
                } catch (Exception ex) {
                        Logger.getLogger(SilhouetteInfoTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                }
        }

        private int isAnySelected(Dataset dataset) {
                int cont = 0;
                for (PeakListRow row : dataset.getRows()) {
                        if (row.isSelected()) {
                                cont++;
                        }
                }
                return cont;
        }
}
