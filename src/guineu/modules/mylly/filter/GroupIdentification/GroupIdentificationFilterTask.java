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
package guineu.modules.mylly.filter.GroupIdentification;

import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.xml.writer.AttributeList;
import org.jfree.xml.writer.XMLWriter;

/**
 *
 * @author scsandra
 */
public class GroupIdentificationFilterTask implements Task {

        private TaskStatus status = TaskStatus.WAITING;
        private String errorMessage;
        private SimpleGCGCDataset dataset;
        private double progress = 0.0;

        public GroupIdentificationFilterTask(SimpleGCGCDataset dataset) {
               this.dataset = dataset;
        }

        public String getTaskDescription() {
                return "Filtering files with Group Identifiacion Filter... ";
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
                status = TaskStatus.PROCESSING;
                try {
                        actualMap(dataset);
                        status = TaskStatus.FINISHED;
                } catch (Exception ex) {
                        Logger.getLogger(GroupIdentificationFilterTask.class.getName()).log(Level.SEVERE, null, ex);
                        status = TaskStatus.ERROR;
                }
        }

        private String PredictManyXMLFile(SimplePeakListRowGCGC newRow) throws FileNotFoundException, IOException {

                Writer w = new StringWriter();
                XMLWriter xmlW = new XMLWriter(w);
                xmlW.writeXmlDeclaration();
                xmlW.allowLineBreak();

                AttributeList attributes = new AttributeList();
                attributes.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                attributes.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
                attributes.setAttribute("xmlns:soap", "http://schemas.xmlsoap.org/soap/envelope/");
                xmlW.writeTag("soap:Envelope", attributes, false);

                xmlW.startBlock();
                xmlW.writeTag("soap:Body", false);

                xmlW.startBlock();
                attributes = new AttributeList();
                attributes.setAttribute("xmlns", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/");
                xmlW.writeTag("PredictAll4AlkaneRiColumnTypeComposition", attributes, false);
                xmlW.startBlock();

                xmlW.writeTag("Ri", false);
                String RTI = String.valueOf(newRow.getRTI());
                xmlW.writeText(RTI);
                xmlW.writeText("</Ri>");
                xmlW.endBlock();

                xmlW.writeTag("Spectrum", false);
                String spectrum = newRow.getSpectrumString();
                spectrum = spectrum.replace(":", " ");
                spectrum = spectrum.replace(", ", "");
                spectrum = spectrum.replace("[", "");
                spectrum = spectrum.replace("]", "");
                xmlW.writeText(spectrum);
                xmlW.writeText("</Spectrum>");
                xmlW.endBlock();

                xmlW.writeTag("ColumnType", false);
                xmlW.writeText("MDN35");
                xmlW.writeText("</ColumnType>");
                xmlW.endBlock();

                xmlW.endBlock();
                xmlW.writeCloseTag("PredictAll4AlkaneRiColumnTypeComposition");
                xmlW.endBlock();
                xmlW.writeCloseTag("soap:Body");
                xmlW.endBlock();
                xmlW.writeCloseTag("soap:Envelope");

                xmlW.close();

                return w.toString();
        }

        private List<String> getAnswer(String xmlFile2Send, HttpURLConnection httpConn) {
                // Open the input file. After we copy it to a byte array, we can see
                // how big it is so that we can set the HTTP Cotent-Length
                // property. (See complete e-mail below for more on this.)


                try {

                        InputStream fin = new ByteArrayInputStream(xmlFile2Send.getBytes("UTF-8"));
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        // Copy the SOAP file to the open connection.
                        copy(fin, bout);
                        fin.close();
                        byte[] b = bout.toByteArray();
                        // Set the appropriate HTTP parameters.
                        httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
                        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
                        httpConn.setRequestProperty("SOAPAction", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/PredictAll4AlkaneRiColumnTypeComposition");
                        httpConn.setRequestMethod("POST");
                        httpConn.setDoOutput(true);
                        httpConn.setDoInput(true);
                        // Everything's set up; send the XML that was read in to b.
                        OutputStream out = httpConn.getOutputStream();
                        out.write(b);
                        out.close();
                        // Read the response and write it to standard out.
                        InputStreamReader isr = new InputStreamReader(httpConn.getInputStream());
                        BufferedReader in = new BufferedReader(isr);
                        String inputLine;
                        List<String> group = new ArrayList<String>();
                        String name = "";
                        while ((inputLine = in.readLine()) != null) {
                                while (inputLine.contains("<FunctionalGroup>")) {
                                        name = inputLine.substring(inputLine.indexOf("<FunctionalGroup>") + 17, inputLine.indexOf("</FunctionalGroup>"));
                                        if (inputLine.contains("<PredictionGroupIsPresent>true</PredictionGroupIsPresent>")) {
                                                if (!group.contains(name)) {
                                                        group.add(name);
                                                }
                                        }
                                        name = "";
                                        inputLine = inputLine.substring(inputLine.indexOf("</Prediction>") + 14);
                                }
                        }
                        in.close();
                        fin.close();
                        httpConn.disconnect();
                        return group;

                } catch (Exception ex) {
                        return null;
                }

        }

        // copy method from From E.R. Harold's book "Java I/O"
        public static void copy(InputStream in, OutputStream out)
                throws IOException {

                // do not allow other threads to read from the
                // input or write to the output while copying is
                // taking place

                synchronized (in) {
                        synchronized (out) {

                                byte[] buffer = new byte[256];
                                while (true) {
                                        int bytesRead = in.read(buffer);
                                        if (bytesRead == -1) {
                                                break;
                                        }
                                        out.write(buffer, 0, bytesRead);
                                }
                        }
                }
        }

        protected void actualMap(SimpleGCGCDataset input) throws Exception {
                int numRows = input.getNumberRows();
                int count = 0;
                for (PeakListRow row : input.getAlignment()) {
                        if (status == TaskStatus.CANCELED) {
                                break;
                        }

                        /* if (((SimplePeakListRowGCGC) row).getMolClass() != null &&
                        ((SimplePeakListRowGCGC) row).getMolClass().length() != 0 &&
                        !((SimplePeakListRowGCGC) row).getMolClass().contains("NA")) {
                        count++;
                        continue;
                        }*/
                        URL url = new URL("http://gmd.mpimp-golm.mpg.de/webservices/wsPrediction.asmx");
                        URLConnection connection = url.openConnection();
                        HttpURLConnection httpConn = (HttpURLConnection) connection;
                        String xmlFile = this.PredictManyXMLFile((SimplePeakListRowGCGC) row);
                        List<String> group = this.getAnswer(xmlFile, httpConn);
                        if (group != null) {
                                String finalGroup = "";
                                for (String name : group) {
                                        finalGroup += name + ",";
                                }

                                ((SimplePeakListRowGCGC) row).setGolmGroup(finalGroup);
                                count++;
                                progress = (double) count / numRows;
                        }
                }
                File file = new File("temporalFile.xml");
                file.delete();
        }

        public String getName() {
                return "Filter Group Identification";
        }
}
