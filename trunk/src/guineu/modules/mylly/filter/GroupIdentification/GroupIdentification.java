/*
Copyright 2006-2007 VTT Biotechnology

This file is part of MYLLY.

MYLLY is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

MYLLY is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MYLLY; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package guineu.modules.mylly.filter.GroupIdentification;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class GroupIdentification {

    public final static String MAX_SIMILARITY = "maximum similarity";
    public final static String MEAN_SIMILARITY = "mean similarity";
    public final static String REMOVE = "Remove";
    public final static String RENAME = "Rename";

    public GroupIdentification(String SOAPUrl, String SOAPAction) {
        // System.setProperty("http.proxyHost", "rohto.vtt.fi");
        //System.setProperty("http.proxyPort", "8000");
    }

    private String PredictManyXMLFile(SimplePeakListRowGCGC newRow, String[] miningModels) throws FileNotFoundException, IOException, SAXException {
        FileOutputStream fos = new FileOutputStream("temporalFile.xml");
// XERCES 1 or 2 additionnal classes.
        OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
        of.setIndent(1);
        of.setIndenting(true);
        of.setDoctype(null, "");
        of.setEncoding("utf-8");
        XMLSerializer serializer = new XMLSerializer(fos, of);
// SAX2.0 ContentHandler.
        ContentHandler hd = serializer.asContentHandler();
        hd.startDocument();
// Processing instruction sample.
//hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
// USER attributes.
        AttributesImpl atts = new AttributesImpl();
// USERS tag.

        atts.clear();
        atts.addAttribute("0", "", "xmlns:xsi", "CDATA", "http://www.w3.org/2001/XMLSchema-instance");
        atts.addAttribute("1", "", "xmlns:xsd", "CDATA", "http://www.w3.org/2001/XMLSchema");
        atts.addAttribute("2", "", "xmlns:soap", "CDATA", "http://www.w3.org/2003/05/soap-envelope");
        hd.startElement("", "", "soap:Envelope", atts);
        atts.clear();
        hd.startElement("", "", "soap:Body", atts);
        atts.clear();
        atts.addAttribute("", "", "xmlns", "CDATA", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/");
        hd.startElement("", "", "PredictMany", atts);
        atts.clear();
        hd.startElement("", "", "ri", atts);
        String RTI = String.valueOf(newRow.getRTI());
        hd.characters(RTI.toCharArray(), 0, RTI.length());
        hd.endElement("", "", "ri");

        atts.clear();
        hd.startElement("", "", "spectrum", atts);

        String spectrum = newRow.getSpectrumString();
        spectrum = spectrum.replace(":", " ");
        spectrum = spectrum.replace(", ", "");
        spectrum = spectrum.replace("[", "");
        spectrum = spectrum.replace("]", "");
        // String spectrum = newRow.getSpectrum().toSpecialString();
        hd.characters(spectrum.toCharArray(), 0, spectrum.length());
        hd.endElement("", "", "spectrum");

        atts.clear();
        hd.startElement("", "", "MiningModelIds", atts);
        for (String model : miningModels) {
            atts.clear();
            hd.startElement("", "", "guid", atts);
            hd.characters(model.toCharArray(), 0, model.length());
            hd.endElement("", "", "guid");
        }
        hd.endElement("", "", "MiningModelIds");
        hd.endElement("", "", "PredictMany");
        hd.endElement("", "", "soap:Body");
        hd.endElement("", "", "soap:Envelope");
        hd.endDocument();
        fos.close();

        return "temporalFile.xml";
    }

    private BufferedReader getAnswer(String xmlFile2Send, HttpURLConnection httpConn) {
        // Open the input file. After we copy it to a byte array, we can see
        // how big it is so that we can set the HTTP Cotent-Length
        // property. (See complete e-mail below for more on this.)

        FileInputStream fin = null;
        try {

            fin = new FileInputStream("temporalFile.xml");
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            // Copy the SOAP file to the open connection.
            copy(fin, bout);
            fin.close();
            byte[] b = bout.toByteArray();
            // Set the appropriate HTTP parameters.
            httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
            httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            httpConn.setRequestProperty("SOAPAction", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/PredictMany");
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
            while ((inputLine = in.readLine()) != null) {
                System.out.println(inputLine);
            }
            in.close();
            return in;

        } catch (Exception ex) {
            Logger.getLogger(GroupIdentification.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fin.close();

            } catch (IOException ex) {
                Logger.getLogger(GroupIdentification.class.getName()).log(Level.SEVERE, null, ex);

            }
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

    protected SimpleGCGCDataset actualMap(SimpleGCGCDataset input) throws Exception {
//PredictMany
        URL url = new URL("http://gmd.mpimp-golm.mpg.de/webservices/wsPrediction.asmx");
        URLConnection connection = url.openConnection();
        HttpURLConnection httpConn = (HttpURLConnection) connection;

        List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();
        // for (PeakListRow row : input.getAlignment()) {
        SimplePeakListRowGCGC newRow = (SimplePeakListRowGCGC) input.getRow(0);// row.clone();
        String[] miningModels = this.getMiningModels();
        //String xmlFile = this.PredictManyXMLFile(newRow, miningModels);
        this.getAnswer("temporalFile.xml", httpConn);

        //  }
        SimpleGCGCDataset filtered = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
        filtered.addAll(als);
        return filtered;
    }

    public String getName() {
        return "Filter by similarity";
    }

    private String[] getMiningModels() throws FileNotFoundException, IOException, SAXException {
        try {
            URL url = new URL("http://gmd.mpimp-golm.mpg.de/webservices/wsPrediction.asmx/GetMiningModels");
            URLConnection connection = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) connection;
            String file = this.createMiningModelsXML();
            BufferedReader reader = this.getMiningModelsAnswer("temporalFile2.xml", httpConn);
            List<String> miningModelsID = new ArrayList<String>();
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                if (inputLine.contains("<MiningModelId>")) {
                    miningModelsID.add(inputLine.substring(inputLine.lastIndexOf("<MiningModelId>") + 15, inputLine.lastIndexOf("</MiningModelId>")));
                }
            }

            return miningModelsID.toArray(new String[0]);
        } catch (MalformedURLException ex) {
            Logger.getLogger(GroupIdentification.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (RemoteException ex) {
            Logger.getLogger(GroupIdentification.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private String createMiningModelsXML() throws FileNotFoundException, IOException, SAXException {
        FileOutputStream fos = new FileOutputStream("temporalFile2.xml");
        // XERCES 1 or 2 additionnal classes.
        OutputFormat of = new OutputFormat("XML", "ISO-8859-1", true);
        of.setIndent(1);
        of.setIndenting(true);
        of.setDoctype(null, "");
        of.setEncoding("utf-8");
        XMLSerializer serializer = new XMLSerializer(fos, of);
// SAX2.0 ContentHandler.
        ContentHandler hd = serializer.asContentHandler();
        hd.startDocument();
// Processing instruction sample.
//hd.processingInstruction("xml-stylesheet","type=\"text/xsl\" href=\"users.xsl\"");
// USER attributes.
        AttributesImpl atts = new AttributesImpl();
// USERS tag.

        atts.clear();
        atts.addAttribute("", "", "xmlns:xsi", "CDATA", "\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"");
        hd.startElement("", "", "soap12:Envelope", atts);
        atts.clear();
        hd.startElement("", "", "soap12:Body", atts);
        atts.clear();
        atts.addAttribute("", "", "xmlns", "CDATA", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/");
        hd.startElement("", "", "GetMiningModels", atts);
        hd.endElement("", "", "GetMiningModels");
        hd.endElement("", "", "soap12:Body");
        hd.endElement("", "", "soap12:Envelope");
        hd.endDocument();
        fos.close();

        return "temporalFile2.xml";

    }

    private BufferedReader getMiningModelsAnswer(String xmlFile2Send, HttpURLConnection httpConn) throws MalformedURLException, RemoteException, FileNotFoundException, IOException {
        // Open the input file. After we copy it to a byte array, we can see
        // how big it is so that we can set the HTTP Cotent-Length
        // property. (See complete e-mail below for more on this.)      

        FileInputStream fin = new FileInputStream(xmlFile2Send);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        // Copy the SOAP file to the open connection.
        copy(fin, bout);
        fin.close();
        byte[] b = bout.toByteArray();
        // Set the appropriate HTTP parameters.
        httpConn.setRequestProperty("Content-Length", String.valueOf(b.length));
        httpConn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        httpConn.setRequestProperty("SOAPAction", "http://gmd.mpimp-golm.mpg.de/FunctionalGroupPrediction/GetMiningModels");
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
        // String inputLine;
        //while ((inputLine = in.readLine()) != null) {
        // System.out.println(inputLine);
        //  }
        return in;


    }
}
