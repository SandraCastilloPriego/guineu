/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.tools.PrepareDeviationFile;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import guineu.data.Dataset;
import java.io.FileOutputStream;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 *
 * @author bicha
 */
public class RTIFileTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Dataset dataset;
    private String fileName;
    private String outputFileName;

    public RTIFileTask(Dataset dataset, RTIFileParameters parameters) {
        this.dataset = dataset;
        fileName = (String) parameters.getParameterValue(RTIFileParameters.fileNames);
        outputFileName = (String) parameters.getParameterValue(RTIFileParameters.outputFileNames);
    }

    public String getTaskDescription() {
        return "Filtering files with Calculate Deviations... ";
    }

    public double getFinishedPercentage() {
        return 1f;
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
            createNewFile();           
            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            Logger.getLogger(RTIFileTask.class.getName()).log(Level.SEVERE, null, ex);
            status = TaskStatus.ERROR;
        }
    }

    private void createNewFile() throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
        List<RIList> names = new ArrayList<RIList>();
        String line;
        RIList list = null;
        while ((line = br.readLine()) != null) {
            if (line.contains("Name:")) {
                if (list != null) {
                    names.add(list);
                }
                list = new RIList();
                line = line.substring(line.indexOf("Name:") + 6);
                list.names = line.split(", ");
            }
            if (line.contains("CAS#:")) {
                if (list != null) {
                    list.CAS = line.substring(line.indexOf("CAS#:") + 6, line.indexOf(" NIST#"));
                }
            }

            if (line.contains("Value:") == true) {
                if (list != null && list.RI == 0) {
                    line = line.substring(line.indexOf("Value:") + 7, line.indexOf("iu"));
                    list.RI = Double.valueOf(line).doubleValue();
                    line = br.readLine();
                    if (line.contains("Column Type")) {
                        line = line.substring(line.indexOf("Type:") + 6);
                        list.columnType = line;
                    }
                    if (line.contains("Column Type: Capillary")) {
                        list.cap = true;
                    }
                } else if (list != null && list.RI != 0) {
                    line = line.substring(line.indexOf("Value:") + 7, line.indexOf("iu"));
                    double value = Double.valueOf(line).doubleValue();
                    line = br.readLine();
                    if ((line.contains("Column Type: Capillary") && !list.cap) || list.columnType.length() == 0) {
                        list.RI = value;
                        line = line.substring(line.indexOf("Type:") + 6);
                        list.columnType = line;
                        list.cap = true;
                    }

                }
            }
        }
        try {
            createXMLFile(names);
        } catch (SAXException ex) {
            Logger.getLogger(RTIFileTask.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void createXMLFile(List<RIList> names) throws FileNotFoundException, IOException, SAXException {
        FileOutputStream fos = new FileOutputStream(this.outputFileName);
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
        hd.startElement("", "", "Data", atts);
        for (RIList list : names) {
            atts.clear();
            hd.startElement("", "", "Metabolite", atts);
            for (String name : list.names) {
                atts.clear();
                hd.startElement("", "", "Name", atts);
                hd.characters(name.toCharArray(), 0, name.length());
                hd.endElement("", "", "Name");
            }
            atts.clear();
            hd.startElement("", "", "CAS", atts);
            hd.characters(list.CAS.toCharArray(), 0, list.CAS.length());
            hd.endElement("", "", "CAS");
            atts.clear();
            hd.startElement("", "", "RI", atts);
            String value = String.valueOf(list.RI);
            hd.characters(value.toCharArray(), 0, value.length());
            hd.endElement("", "", "RI");

            atts.clear();
            atts.addAttribute("", "", "Type", "CDATA", list.columnType);
            hd.startElement("", "", "Column", atts);
            hd.endElement("", "", "ColumnType");

            hd.endElement("", "", "Metabolite");
        }
        hd.endElement("", "", "Data");
        hd.endDocument();
        fos.close();
    }

    class RIList {

        boolean cap = false;
        String columnType = "";
        String[] names;
        String CAS = "";
        double RI = 0;
        String[] synonims;
        String Spectrum;
    }    
}
