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
package guineu.modules.mylly.filter.GroupIdentification;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

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
        System.setProperty("http.proxyHost", "rohto.vtt.fi");
        System.setProperty("http.proxyPort", "8000");
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

    private String PredictManyXMLFile(SimplePeakListRowGCGC newRow) throws FileNotFoundException, IOException, SAXException {
        FileOutputStream fos = new FileOutputStream("temporalFile.xml");
// XERCES 1 or 2 additionnal classes.
        OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
        of.setIndent(1);
        of.setIndenting(true);
        of.setEncoding("utf-8");
        XMLSerializer serializer = new XMLSerializer(fos, of);
// SAX2.0 ContentHandler.
        ContentHandler hd = serializer.asContentHandler();
        hd.startDocument();

// USER attributes.
        AttributesImpl atts = new AttributesImpl();
// USERS tag.

        atts.clear();
        atts.addAttribute("0", "", "xmlns:xsi", "CDATA", "http://www.w3.org/2001/XMLSchema-instance");
        atts.addAttribute("1", "", "xmlns:xsd", "CDATA", "http://www.w3.org/2001/XMLSchema");
        atts.addAttribute("2", "", "xmlns:soap", "CDATA", "http://schemas.xmlsoap.org/soap/envelope/");
        hd.startElement("", "", "soap:Envelope", atts);
        atts.clear();
        hd.startElement("", "", "soap:Body", atts);
        atts.clear();
        atts.addAttribute("", "", "xmlns", "CDATA", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/");
        hd.startElement("", "", "PredictAll4AlkaneRiColumnTypeComposition", atts);
        atts.clear();
        hd.startElement("", "", "Ri", atts);
        String RTI = String.valueOf(newRow.getRTI());
        hd.characters(RTI.toCharArray(), 0, RTI.length());
        hd.endElement("", "", "Ri");

        atts.clear();
        hd.startElement("", "", "Spectrum", atts);

        String spectrum = newRow.getSpectrumString();
        spectrum = spectrum.replace(":", " ");
        spectrum = spectrum.replace(", ", "");
        spectrum = spectrum.replace("[", "");
        spectrum = spectrum.replace("]", "");
        hd.characters(spectrum.toCharArray(), 0, spectrum.length());
        hd.endElement("", "", "Spectrum");

        atts.clear();
        hd.startElement("", "", "ColumnType", atts);
        hd.characters("MDN35".toCharArray(), 0, 5);
        hd.endElement("", "", "ColumnType");
        hd.endElement("", "", "PredictAll4AlkaneRiColumnTypeComposition");
        hd.endElement("", "", "soap:Body");
        hd.endElement("", "", "soap:Envelope");
        hd.endDocument();
        fos.close();

        return "temporalFile.xml";
    }

    private List<String> getAnswer(String xmlFile2Send, HttpURLConnection httpConn) {
        // Open the input file. After we copy it to a byte array, we can see
        // how big it is so that we can set the HTTP Cotent-Length
        // property. (See complete e-mail below for more on this.)

        FileInputStream fin = null;
        try {

            fin = new FileInputStream(xmlFile2Send);
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
                        group.add(name);
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
            URL url = new URL("http://gmd.mpimp-golm.mpg.de/webservices/wsPrediction.asmx");
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            String xmlFile = this.PredictManyXMLFile((SimplePeakListRowGCGC) row);
            List<String> group = this.getAnswer(xmlFile, httpConn);
            if (group != null) {
                List<String> newGroup = new ArrayList<String>();
                for (String name : group) {
                    if (!newGroup.contains(name)) {
                        newGroup.add(name);
                    }
                }
                String finalGroup = "";
                for (String name : newGroup) {
                    finalGroup += name + ",";
                }

                ((SimplePeakListRowGCGC) row).setMolClass(finalGroup);
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
