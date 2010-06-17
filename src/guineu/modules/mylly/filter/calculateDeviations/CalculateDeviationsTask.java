/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.calculateDeviations;

import guineu.data.impl.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.modules.mylly.datastruct.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author bicha
 */
public class CalculateDeviationsTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Dataset dataset;
    private String fileName;

    public CalculateDeviationsTask(Dataset dataset, CalculateDeviationsParameters parameters) {
        this.dataset = dataset;
        fileName = (String) parameters.getParameterValue(CalculateDeviationsParameters.fileNames);
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
            List<Pair<List<String>, Double>> representativies = this.makeRepresentativeList(new File(fileName), "\\t");
            findRepresentavies(representativies, (SimpleGCGCDataset) dataset);
            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            Logger.getLogger(CalculateDeviationsTask.class.getName()).log(Level.SEVERE, null, ex);
            status = TaskStatus.ERROR;
        }
    }

    private void findRepresentavies(
            List<Pair<List<String>, Double>> peaks, SimpleGCGCDataset al) {
        Map<Integer, Double> representatives = new HashMap<Integer, Double>();

        for (Pair<List<String>, Double> idealPeak : peaks) {
            int i = 0;
            Set<String> names = new HashSet<String>(idealPeak.getFirst());
            double rti = idealPeak.getSecond();

            for (PeakListRow ar : al.getAlignment()) {
                if (names.contains((String)ar.getVar("getName")) || names.contains((String)ar.getVar("getCAS"))) {
                    double diff = Math.abs(rti - (Double) ar.getVar("getRTI"));
                    representatives.put(i, diff);
                }
                i++;
            }
        }

        int i = 0;
        for (PeakListRow ar : al.getAlignment()) {
            try {
                Double diff = representatives.get(i++);
                if (diff != null) {
                    if (ar.getVar("getDifference") == null || (Double) ar.getVar("getDifference") == 0.0) {
                        ar.setVar("setDifference", diff);
                    }
                }
            } catch (Exception e) {
            }
        }

    }

    @SuppressWarnings("empty-statement")
    public List<Pair<List<String>, Double>> makeRepresentativeList(File f, String sep) throws IOException {
        if (f.getName().contains("xml")) {
            try {
                return makeXMLRepresentativeList(f);
            } catch (Exception ex) {
                Logger.getLogger(CalculateDeviationsTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        List<Pair<List<String>, Double>> l = new ArrayList<Pair<List<String>, Double>>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        int[] indices;
        while ((indices = readHeader(br.readLine(), sep)).length == 0);
        String line;
        while ((line = br.readLine()) != null) {
            String tokens[] = line.split(sep);
            List<String> names = new ArrayList<String>(indices.length - 1);
            boolean error = false;
            try {
                for (int i = 0; i < indices.length - 1; i++) {
                    String str = tokens[indices[i]];
                    if (!"".equals(str)) {
                        names.add(stripCAS(str));
                    }
                }
                Pair<List<String>, Double> pair = new Pair<List<String>, Double>(names,
                        Double.parseDouble(tokens[indices[indices.length - 1]].replace(',', '.')));
                l.add(pair);
            } catch (IndexOutOfBoundsException e1) {
                error = true;
            } catch (NumberFormatException e2) {
                error = true;
            }
            if (error) {
            }
        }
        return l;
    }

    private List<Pair<List<String>, Double>> makeXMLRepresentativeList(File f) throws ParserConfigurationException, ParserConfigurationException, SAXException, IOException {
        List<Pair<List<String>, Double>> l = new ArrayList<Pair<List<String>, Double>>();
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(f);
        doc.getDocumentElement().normalize();
        NodeList nodeLst = doc.getElementsByTagName("Metabolite");
        for (int s = 0; s < nodeLst.getLength(); s++) {
            Node MetaboliteNode = nodeLst.item(s);
            List<String> names = new ArrayList<String>();
            if (MetaboliteNode.getNodeType() == Node.ELEMENT_NODE) {
                Element MetaboliteElement = (Element) MetaboliteNode;
                NodeList firstNameList = MetaboliteElement.getElementsByTagName("Name");
                for (int i = 0; i < firstNameList.getLength(); i++) {
                    Element firstNameElement = (Element) firstNameList.item(i);
                    NodeList textFNList = firstNameElement.getChildNodes();
                    names.add(((Node) textFNList.item(0)).getNodeValue().trim());
                }

                NodeList firstCASList = MetaboliteElement.getElementsByTagName("CAS");
                Element CASElement = (Element) firstCASList.item(0);
                NodeList textCASLNList = CASElement.getChildNodes();
                names.add(((Node) textCASLNList.item(0)).getNodeValue().trim());

                NodeList firstRIList = MetaboliteElement.getElementsByTagName("RI");
                Element RIElement = (Element) firstRIList.item(0);

                NodeList textLNList = RIElement.getChildNodes();

                Pair<List<String>, Double> pair = new Pair<List<String>, Double>(names,
                        Double.parseDouble(((Node) textLNList.item(0)).getNodeValue().trim()));
                l.add(pair);
            }
        }
        return l;
    }

    public static String stripCAS(String str) {
        String parts[] = str.split("\\(CAS\\)");
        String newString;
        if (parts.length > 0) {
            newString = parts[0].trim();
        } else {
            newString = str.trim();
        }
        return newString;
    }

    /**
     *
     * @param line
     * @param sep TODO
     * @return zero-length array in case the line isn't header line.
     */
    private int[] readHeader(String line, String sep) {
        if (line == null) {
            return new int[0];
        }
        String tokens[] = line.split(sep);
        List<Integer> indices = new ArrayList<Integer>();
        int riIndex = -1;
        int i = 0;

        for (String token : tokens) {
            if (token.contains("Name ") || i == 0) {
                indices.add(i);
            } else if ("RI".equals(token)) {
                riIndex = i;
            }
            i++;
        }
        if (riIndex >= 0) {
            indices.add(riIndex);
        } else {
            indices.clear();
        }


        int indexArray[] = new int[indices.size()];
        for (i = 0; i < indices.size(); i++) {
            indexArray[i] = indices.get(i);
        }
        return indexArray;
    }
}
