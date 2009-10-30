/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.mylly.filter.calculateDeviations;

import guineu.data.impl.SimpleGCGCDataset;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.Task.TaskStatus;
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
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.modules.mylly.datastruct.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
                if (names.contains(ar.getVar("getName"))) {
                    double diff = Math.abs(rti - (Double) ar.getVar("getRTI"));
                    representatives.put(i, diff);
                }
                i++;
            }
        }

        int i = 0;
        for (PeakListRow ar : al.getAlignment()) {
            Double diff = representatives.get(i++);
            if (diff != null) {
                ar.setVar("setDifference", diff);				
            }
        }

    }

    @SuppressWarnings("empty-statement")
    public List<Pair<List<String>, Double>> makeRepresentativeList(File f, String sep) throws IOException {
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
//				throw new IOException("Line was malformed:\n" + line);
                //just skip the line
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
