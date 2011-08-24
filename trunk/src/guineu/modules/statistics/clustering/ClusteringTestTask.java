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
package guineu.modules.statistics.clustering;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import guineu.util.WekaUtils;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.clusterers.Clusterer;
import weka.clusterers.Cobweb;
import weka.clusterers.EM;
import weka.clusterers.FarthestFirst;
import weka.clusterers.SimpleKMeans;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author scsandra
 */
public class ClusteringTestTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Dataset dataset;
    private String parameter, dataType;
    private int numberOfClusters;
    private ClusteringAlgorithmsEnum algorithm;
    private double progress = 0;

    public ClusteringTestTask(Dataset dataset, ClusteringParameters parameters) {
        this.dataset = dataset;
        parameter = parameters.getParameter(ClusteringParameters.clusteringAlgorithm).getValue();
        dataType =  parameters.getParameter(ClusteringParameters.clusteringData).getValue();
        numberOfClusters =  parameters.getParameter(ClusteringParameters.N).getValue();

        // Get the type algorithm from the enum.
        for (ClusteringAlgorithmsEnum e : ClusteringAlgorithmsEnum.values()) {
            System.out.println(e.toString() + " - " + parameter);
            if (e.toString().equals(parameter.toUpperCase())) {
                algorithm = e;
                break;
            }
        }
    }

    public String getTaskDescription() {
        return "Performing Clustering data... ";
    }

    public double getFinishedPercentage() {
        return (float) progress;
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
        try {
            status = TaskStatus.PROCESSING;
            if (dataType.equals("Samples")) {
                Instances wekaData = WekaUtils.getWekaDataset(dataset, true);

                this.progress = 0.2;
                List<Integer> clusters = this.getClusterer(algorithm, wekaData);
                this.progress = 0.7;

                for (int i = 0; i < dataset.getNumberCols(); i++) {
                    String sampleName = dataset.getAllColumnNames().elementAt(i);
                    dataset.addParameterValue(sampleName, "Clustering", String.valueOf(clusters.get(i)));
                }

            } else if (dataType.equals("Variables")) {
                Instances wekaData = WekaUtils.getWekaDataset(dataset, false);

                this.progress = 0.2;
                List<Integer> clusters = this.getClusterer(algorithm, wekaData);
                this.progress = 0.7;
                if (clusters != null && clusters.size() > 0) {
                    if (!dataset.getAllColumnNames().contains("Clustering")) {
                        dataset.addColumnName("Clustering");
                    }
                    int i = 0;
                    for (PeakListRow row : dataset.getRows()) {
                        row.setPeak("Clustering", (double) clusters.get(i++));
                    }
                }
            }

            this.progress = 1;
            status = TaskStatus.FINISHED;
        } catch (Exception ex) {
            ex.printStackTrace();
            status = TaskStatus.ERROR;
        }
    }

    private List<Integer> getClusterer(ClusteringAlgorithmsEnum name, Instances wekaData) {
        List<Integer> clusters = new ArrayList<Integer>();
        String[] options = new String[2];
        Clusterer clusterer = null;
        switch (name) {
            case COBWEB:
                clusterer = new Cobweb();
                break;
            case EM:
                clusterer = new EM();
                options[0] = "-I";
                options[1] = "100";
                try {
                    ((EM) clusterer).setOptions(options);
                } catch (Exception ex) {
                    Logger.getLogger(ClusteringTestTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case FARTHESTFIRST:
                clusterer = new FarthestFirst();
                options[0] = "-N";
                options[1] = String.valueOf(this.numberOfClusters);
                try {
                    ((FarthestFirst) clusterer).setOptions(options);
                } catch (Exception ex) {
                    Logger.getLogger(ClusteringTestTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case SIMPLEKMEANS:
                clusterer = new SimpleKMeans();
                options[0] = "-N";
                options[1] = String.valueOf(this.numberOfClusters);
                try {
                    ((SimpleKMeans) clusterer).setOptions(options);
                } catch (Exception ex) {
                    Logger.getLogger(ClusteringTestTask.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
        }

        try {
            clusterer.buildClusterer(wekaData);
            Enumeration e = wekaData.enumerateInstances();
            while (e.hasMoreElements()) {
                clusters.add(clusterer.clusterInstance((Instance) e.nextElement()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return clusters;


    }
}
