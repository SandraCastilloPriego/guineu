/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.modules.mylly.filter.pubChem.GolmIdentification;

import guineu.data.PeakListRow;
import guineu.data.GCGCColumnName;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;
import guineu.modules.configuration.proxy.ProxyConfigurationParameters;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.xml.writer.AttributeList;
import org.jfree.xml.writer.XMLWriter;

/**
 *
 * @author scsandra
 */
public class GetGolmIDsFilterTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private SimpleGCGCDataset dataset;
    private double progress = 0.0;

    public GetGolmIDsFilterTask(SimpleGCGCDataset dataset) {

        // Proxy configuration
        try {
            ProxyConfigurationParameters proxy = (ProxyConfigurationParameters) ((DesktopParameters) GuineuCore.getDesktop().getParameterSet()).getProxyParameters();
            String proxystr = (String) proxy.getParameterValue(ProxyConfigurationParameters.proxy);
            if (!proxystr.isEmpty()) {
                System.setProperty("http.proxyHost", proxystr);
                System.setProperty("http.proxyPort", (String) proxy.getParameterValue(ProxyConfigurationParameters.port));
            }
        } catch (Exception e) {
            System.out.println("Bad proxy configuration.");
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
            Logger.getLogger(GetGolmIDsFilterTask.class.getName()).log(Level.SEVERE, null, ex);
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
        attributes.setAttribute("xmlns", "http://gmd.mpimp-golm.mpg.de/LibrarySearch/");
        xmlW.writeTag("LibrarySearch", attributes, false);
        xmlW.startBlock();

        xmlW.writeTag("ri", false);
        String RTI = String.valueOf(newRow.getRTI());
        xmlW.writeText(RTI);
        xmlW.writeText("</ri>");
        xmlW.endBlock();


        xmlW.writeTag("riWindow", false);
        RTI = "25.0";
        xmlW.writeText(RTI);
        xmlW.writeText("</riWindow>");
        xmlW.endBlock();

        xmlW.writeTag("AlkaneRetentionIndexGcColumnComposition", false);
        xmlW.writeText("VAR5");
        xmlW.writeText("</AlkaneRetentionIndexGcColumnComposition>");
        xmlW.endBlock();


        xmlW.writeTag("spectrum", false);
        String spectrum = newRow.getSpectrumString();
        spectrum = spectrum.replace(":", " ");
        spectrum = spectrum.replace(", ", "");
        spectrum = spectrum.replace("[", "");
        spectrum = spectrum.replace("]", "");
        xmlW.writeText(spectrum);
        xmlW.writeText("</spectrum>");
        xmlW.endBlock();

        xmlW.endBlock();
        xmlW.writeCloseTag("LibrarySearch");
        xmlW.endBlock();
        xmlW.writeCloseTag("soap:Body");
        xmlW.endBlock();
        xmlW.writeCloseTag("soap:Envelope");


        xmlW.close();

        return w.toString();

    }

    private String getAnswer(String xmlFile2Send, HttpURLConnection httpConn) {
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
            String metaboliteID = "";
            while ((inputLine = in.readLine()) != null) {
                while (inputLine.contains("<analyteName>")) {
                    metaboliteID = inputLine.substring(inputLine.indexOf("<metaboliteID>") + 14, inputLine.indexOf("</metaboliteID>"));

                    inputLine = inputLine.substring(inputLine.indexOf("</AnnotatedMatch>") + 17);
                }
            }
            in.close();
            fin.close();
            httpConn.disconnect();
            return metaboliteID;
        } catch (Exception ex) {
            ex.printStackTrace();
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

            if (row.getName().contains("Unknown")) {
                count++;
                continue;
            }

            try {
                // Using CAS number
                String name = (String) row.getVar(GCGCColumnName.CAS.getGetFunctionName());

                if (!name.contains("0-00-0") && name.length() > 0) {
                    addIDs(row, name);
                }
                // Using names
                String pubChemID = (String) row.getVar(GCGCColumnName.PUBCHEM.getGetFunctionName());
                if (pubChemID.length() == 0) {
                    name = row.getName();
                    name = name.replaceAll(" ", "+");
                    name = name.replaceAll(",", "%2c");
                    if (name.length() > 0) {
                        addIDs(row, name);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            count++;
            progress = (double) count / numRows;

        }

    }

    private void addIDs(PeakListRow row, String name) {
        {
            BufferedReader in = null;
            try {
                String urlName = "http://gmd.mpimp-golm.mpg.de/search.aspx?query=" + name;
                URL url = new URL(urlName);
                in = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String metaboliteID = "";
                    if (inputLine.contains("href=\"Metabolites/")) {
                        metaboliteID = inputLine.substring(inputLine.indexOf("href=\"Metabolites/") + 18, inputLine.indexOf("aspx\">") + 4);
                        urlName = "http://gmd.mpimp-golm.mpg.de/Metabolites/" + metaboliteID;
                        break;
                    } else if (inputLine.contains("href=\"Analytes/")) {
                        metaboliteID = inputLine.substring(inputLine.indexOf("href=\"Analytes/") + 15, inputLine.indexOf("aspx\">") + 4);
                        urlName = "http://gmd.mpimp-golm.mpg.de/Analytes/" + metaboliteID;
                        break;
                    } else if (inputLine.contains("href=\"ReferenceSubstances/")) {
                        metaboliteID = inputLine.substring(inputLine.indexOf("href=\"ReferenceSubstances/") + 26, inputLine.indexOf("aspx\">") + 4);
                        urlName = "http://gmd.mpimp-golm.mpg.de/ReferenceSubstances/" + metaboliteID;
                        break;
                    }
                }
                in.close();
                if (urlName.contains(".aspx")) {
                    url = new URL(urlName);
                    in = new BufferedReader(new InputStreamReader(url.openStream()));
                    while ((inputLine = in.readLine()) != null) {
                        if (inputLine.contains("<meta http-equiv=\"keywords\" content=")) {
                            String line = inputLine.substring(inputLine.indexOf("<meta http-equiv=\"keywords\" content=") + 37, inputLine.indexOf("\" /></head>"));
                            String[] names = line.split(", ");
                            for (String id : names) {
                                if (id.contains("PubChem")) {
                                    id = id.substring(id.indexOf("PubChem") + 8);
                                    String pubChem = (String) row.getVar(GCGCColumnName.PUBCHEM.getGetFunctionName());
                                    if (pubChem.length() == 0) {
                                        pubChem += id;
                                    } else {
                                        pubChem += ", " + id;
                                    }
                                    row.setVar(GCGCColumnName.PUBCHEM.getSetFunctionName(), pubChem);
                                } else if (id.contains("ChEBI")) {
                                    id = id.substring(id.indexOf("ChEBI:") + 6);
                                    row.setVar(GCGCColumnName.ChEBI.getSetFunctionName(), id);
                                } else if (id.contains("KEGG")) {
                                    id = id.substring(id.indexOf("KEGG:") + 6);
                                    row.setVar(GCGCColumnName.KEGG.getSetFunctionName(), id);
                                } else if (id.contains("CAS") || id.contains("ChemSpider") || id.contains("MAPMAN") || id.contains("Beilstein:")) {
                                } else {
                                    String synonym = (String) row.getVar(GCGCColumnName.SYNONYM.getGetFunctionName());
                                    if (synonym.length() == 0) {
                                        synonym += id;
                                    } else {
                                        synonym += " // " + id;
                                    }
                                    row.setVar(GCGCColumnName.SYNONYM.getSetFunctionName(), synonym);
                                }
                            }
                            break;
                        }
                    }
                    in.close();

                }
            } catch (IOException ex) {
                Logger.getLogger(GetGolmIDsFilterTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public String getName() {
        return "Filter IDs Identification";
    }
}
