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
package guineu.modules.mylly.filter.NameGolmIdentification;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import guineu.data.PeakListRow;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.modules.configuration.proxy.ProxyConfigurationParameters;
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
public class NameGolmIdentificationFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private SimpleGCGCDataset dataset;
    private double progress = 0.0;

    public NameGolmIdentificationFilterTask(SimpleGCGCDataset dataset) {

        // Proxy configuration
        ProxyConfigurationParameters proxy = (ProxyConfigurationParameters) ((DesktopParameters) GuineuCore.getDesktop().getParameterSet()).getProxyParameters();
        String proxystr = (String) proxy.getParameterValue(ProxyConfigurationParameters.proxy);
        if (!proxystr.isEmpty()) {
            System.setProperty("http.proxyHost", proxystr);
            System.setProperty("http.proxyPort", (String) proxy.getParameterValue(ProxyConfigurationParameters.port));
        }
        this.dataset = dataset;
    }

    public String getTaskDescription() {
        return "Filtering files with Name Identifiacion Filter... ";
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
            Logger.getLogger(NameGolmIdentificationFilterTask.class.getName()).log(Level.SEVERE, null, ex);
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
        atts.addAttribute("", "", "xmlns:xsi", "CDATA", "http://www.w3.org/2001/XMLSchema-instance");
        atts.addAttribute("", "", "xmlns:xsd", "CDATA", "http://www.w3.org/2001/XMLSchema");
        atts.addAttribute("", "", "xmlns:soap", "CDATA", "http://schemas.xmlsoap.org/soap/envelope/");
        hd.startElement("", "", "soap:Envelope", atts);
        atts.clear();
        hd.startElement("", "", "soap:Body", atts);
        atts.clear();
        atts.addAttribute("", "", "xmlns", "CDATA", "http://gmd.mpimp-golm.mpg.de/LibrarySearch/");
        hd.startElement("", "", "LibrarySearch", atts);
        atts.clear();
        hd.startElement("", "", "ri", atts);
        String RTI = String.valueOf(newRow.getRTI());

        hd.characters(RTI.toCharArray(), 0, RTI.length());
        hd.endElement("", "", "ri");

        atts.clear();
        hd.startElement("", "", "riWindow", atts);
        RTI = "10.0";
        hd.characters(RTI.toCharArray(), 0, RTI.length());
        hd.endElement("", "", "riWindow");

        atts.clear();
        hd.startElement("", "", "AlkaneRetentionIndexGcColumnComposition", atts);
        hd.characters("MDN35".toCharArray(), 0, 5);
        hd.endElement("", "", "AlkaneRetentionIndexGcColumnComposition");

        atts.clear();
        hd.startElement("", "", "spectrum", atts);

        String spectrum = newRow.getSpectrumString();
        spectrum = spectrum.replace(":", " ");
        spectrum = spectrum.replace(", ", "");
        spectrum = spectrum.replace("[", "");
        spectrum = spectrum.replace("]", "");
        hd.characters(spectrum.toCharArray(), 0, spectrum.length());
        hd.endElement("", "", "spectrum");

        hd.endElement("", "", "LibrarySearch");
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
            httpConn.setRequestProperty("SOAPAction", "http://gmd.mpimp-golm.mpg.de/LibrarySearch/LibrarySearch");
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
                while (inputLine.contains("<analyteName>")) {
                    name = inputLine.substring(inputLine.indexOf("<analyteName>") + 13, inputLine.indexOf("</analyteName>"));
                    if (!group.contains(name)) {
                        group.add(name);
                    }
                    name = "";
                    inputLine = inputLine.substring(inputLine.indexOf("</AnnotatedMatch>") + 17);
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

            if (((SimplePeakListRowGCGC) row).getMolClass() != null &&
                    ((SimplePeakListRowGCGC) row).getMolClass().length() != 0 &&
                    !((SimplePeakListRowGCGC) row).getMolClass().contains("NA") &&
                    !((SimplePeakListRowGCGC) row).getMolClass().contains("null")) {
                count++;
                continue;
            }
            URL url = new URL("http://gmd.mpimp-golm.mpg.de/webservices/wsLibrarySearch.asmx");
            URLConnection connection = url.openConnection();


            if(!connection.getAllowUserInteraction()){
                 GuineuCore.getDesktop().displayErrorMessage("Please check the proxy configuration in this program.");
                 status = TaskStatus.ERROR;
                 break;
            }

            HttpURLConnection httpConn = (HttpURLConnection) connection;
            String xmlFile = this.PredictManyXMLFile((SimplePeakListRowGCGC) row);
            List<String> group = this.getAnswer(xmlFile, httpConn);
            if (group != null) {
                String finalGroup = "";
                for (String name : group) {
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
        return "Filter Name Identification";
    }
}
