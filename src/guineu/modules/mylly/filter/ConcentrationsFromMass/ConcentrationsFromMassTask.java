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
package guineu.modules.mylly.filter.ConcentrationsFromMass;

import com.csvreader.CsvReader;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.modules.mylly.datastruct.Spectrum;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;
import guineu.util.GUIUtils;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class ConcentrationsFromMassTask extends AbstractTask {

        private SimpleGCGCDataset dataset;
        private double progress = 0.0;
        private String fileName;
        private Hashtable<String, List<Data>> table;
        private boolean direction;
        private double spectrumPeak;

        public ConcentrationsFromMassTask(SimpleGCGCDataset dataset, ConcentrationsFromMassParameters parameters) {
                this.dataset = dataset;
                this.fileName = parameters.getParameter(ConcentrationsFromMassParameters.fileNames).getValue().getAbsolutePath();
                this.direction = parameters.getParameter(ConcentrationsFromMassParameters.direction).getValue();
                this.spectrumPeak = parameters.getParameter(ConcentrationsFromMassParameters.spectrumPeak).getValue();
                this.table = new Hashtable<String, List<Data>>();
        }

        public String getTaskDescription() {
                return "Getting Concentrations filter... ";
        }

        public double getFinishedPercentage() {
                return progress;
        }

        public void cancel() {
                setStatus(TaskStatus.CANCELED);
        }

        public void run() {
                setStatus(TaskStatus.PROCESSING);
                SimpleGCGCDataset newDataset = null;
                if (!direction) {
                        newDataset = this.concentrationFromMass();

                } else {
                        newDataset = this.massFromContectrations();
                }

                if (newDataset != null) {
                        GUIUtils.showNewTable(newDataset, true);
                }
                setStatus(TaskStatus.FINISHED);
        }

        private SimpleGCGCDataset concentrationFromMass() {
                try {
                        // Reads the file with 4 columns: Name, Mass, Intensity and SumIntensities.
                        // Fills the Hashtable "table" with the content of the file.
                        this.readFile();

                        SimpleGCGCDataset newDataset = (SimpleGCGCDataset) dataset.clone();
                        newDataset.setDatasetName(newDataset.getDatasetName() + "- Filtered");
                        for (PeakListRow row : newDataset.getAlignment()) {
                                if (((SimplePeakListRowGCGC) row).getMass() >= 0) {

                                        String compoundName = (String) row.getVar("getName");

                                        // If there is no file and the Hashtable is empty or it doesn't contain the compound
                                        // the module uses the enum (StandardCompoundsEnum).
                                        if (!table.containsKey(compoundName)) {
                                                List<StandardCompoundsEnum> values = new ArrayList<StandardCompoundsEnum>();
                                                for (StandardCompoundsEnum s : StandardCompoundsEnum.values()) {
                                                        if (compoundName.compareTo(s.getName()) == 0) {
                                                                values.add(s);
                                                        }
                                                }
                                                StandardCompoundsEnum val = null;
                                                if (values.size() > 0) {
                                                        val = values.get(0);
                                                        if (values.size() > 1) {
                                                                double mass = (Double) row.getVar("getMass");
                                                                for (StandardCompoundsEnum s : values) {
                                                                        if (mass == s.getMass()) {
                                                                                val = s;
                                                                        }
                                                                }
                                                        }
                                                }
                                                if (val != null) {
                                                        for (String name : newDataset.getAllColumnNames()) {
                                                                double concentration = ((SimplePeakListRowGCGC) row).getPeak(name);
                                                                double newConcentration = val.getSumIntensity() * (concentration / val.getIntensity());
                                                                if (newConcentration != Double.POSITIVE_INFINITY) {
                                                                        ((SimplePeakListRowGCGC) row).setPeak(name, newConcentration);
                                                                } else {
                                                                        ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                                                }
                                                        }
                                                } else {
                                                        ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                                }
                                        } else {

                                                List<Data> data = this.table.get(compoundName);
                                                Data val = null;
                                                if (data.size() > 0) {
                                                        val = data.get(0);
                                                        if (data.size() > 1) {
                                                                double mass = (Double) row.getVar("getMass");
                                                                for (Data s : data) {
                                                                        if (mass == s.mass) {
                                                                                val = s;
                                                                        }
                                                                }
                                                        }
                                                }
                                                if (val != null) {
                                                        for (String name : newDataset.getAllColumnNames()) {
                                                                double concentration = ((SimplePeakListRowGCGC) row).getPeak(name);
                                                                double newConcentration = val.sumIntensities * (concentration / val.intensity);
                                                                if (newConcentration != Double.POSITIVE_INFINITY) {
                                                                        ((SimplePeakListRowGCGC) row).setPeak(name, newConcentration);
                                                                } else {
                                                                        ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                                                }
                                                        }
                                                } else {
                                                        ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                                }
                                        }
                                }
                        }

                        return newDataset;
                } catch (Exception ex) {
                        Logger.getLogger(ConcentrationsFromMassTask.class.getName()).log(Level.SEVERE, null, ex);
                        setStatus(TaskStatus.ERROR);
                        return null;
                }

        }

        private SimpleGCGCDataset massFromContectrations() {
                SimpleGCGCDataset newDataset = (SimpleGCGCDataset) dataset.clone();
                newDataset.setDatasetName(newDataset.getDatasetName() + "- Filtered");
                for (PeakListRow row : newDataset.getAlignment()) {
                        if (row.isSelected()) {
                                int intensity = 0, sumIntensities = 0;

                                if (this.spectrumPeak != 0) {
                                        Spectrum spectrum = ((SimplePeakListRowGCGC) row).getSpectrum();
                                        int[] masses = spectrum.getMasses();
                                        for (int index = 0; index < masses.length; index++) {
                                                if (masses[index] == this.spectrumPeak) {
                                                        intensity = spectrum.getIntensities()[index];
                                                }
                                                sumIntensities += spectrum.getIntensities()[index];
                                        }
                                }
                                String compoundName = (String) row.getVar("getName");

                                // If there is no file and the Hashtable is empty or it doesn't contain the compound
                                // the module uses the enum (StandardCompoundsEnum).
                                if (!table.containsKey(compoundName)) {
                                        List<StandardCompoundsEnum> values = new ArrayList<StandardCompoundsEnum>();
                                        for (StandardCompoundsEnum s : StandardCompoundsEnum.values()) {
                                                if (compoundName.compareTo(s.getName()) == 0) {
                                                        values.add(s);
                                                }
                                        }
                                        StandardCompoundsEnum val = null;
                                        if (values.size() > 0) {
                                                val = values.get(0);
                                                if (values.size() > 1) {
                                                        double mass = (Double) row.getVar("getMass");
                                                        for (StandardCompoundsEnum s : values) {
                                                                if (mass == s.getMass()) {
                                                                        val = s;
                                                                }
                                                        }
                                                }
                                        }

                                        if (intensity == 0 || this.spectrumPeak == 0) {
                                                intensity = val.getIntensity();
                                        }

                                        if (sumIntensities == 0 || this.spectrumPeak == 0) {
                                                sumIntensities = val.getSumIntensity();
                                        }

                                        if (val != null) {
                                                for (String name : newDataset.getAllColumnNames()) {
                                                        double concentration = ((SimplePeakListRowGCGC) row).getPeak(name);
                                                        double newConcentration = intensity * (concentration / sumIntensities);
                                                        if (newConcentration != Double.POSITIVE_INFINITY) {
                                                                ((SimplePeakListRowGCGC) row).setPeak(name, newConcentration);
                                                        } else {
                                                                ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                                        }
                                                }
                                        } else {
                                                ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                        }
                                } else {

                                        List<Data> data = this.table.get(compoundName);
                                        Data val = null;
                                        if (data.size() > 0) {
                                                val = data.get(0);
                                                if (data.size() > 1) {
                                                        double mass = (Double) row.getVar("getMass");
                                                        for (Data s : data) {
                                                                if (mass == s.mass) {
                                                                        val = s;
                                                                }
                                                        }
                                                }
                                        }
                                        if (intensity == 0) {
                                                intensity = val.intensity;
                                        }
                                        if (val != null) {
                                                for (String name : newDataset.getAllColumnNames()) {
                                                        double concentration = ((SimplePeakListRowGCGC) row).getPeak(name);
                                                        double newConcentration = intensity * (concentration / val.sumIntensities);
                                                        if (newConcentration != Double.POSITIVE_INFINITY) {
                                                                ((SimplePeakListRowGCGC) row).setPeak(name, newConcentration);
                                                        } else {
                                                                ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                                        }
                                                }
                                        } else {
                                                ((SimplePeakListRowGCGC) row).setMolClass("Excluded");
                                        }
                                }
                        }
                }

                return newDataset;

        }

        public String getName() {
                return "Filter Getting Concentrations";
        }

        /**
         * Reads the file with 4 columns: Name, Mass, Intensity and SumIntensities.
         */
        public void readFile() {
                try {
                        CsvReader reader;
                        reader = new CsvReader(new FileReader(this.fileName));
                        reader.readHeaders();
                        //String[] header = reader.getHeaders();
                        while (reader.readRecord()) {
                                String[] row = reader.getValues();
                                Data data = new Data(row);
                                if (this.table.containsKey(row[0])) {
                                        this.table.get(row[0]).add(data);
                                } else {
                                        List<Data> dataSet = new ArrayList<Data>();
                                        dataSet.add(data);
                                        this.table.put(row[0], dataSet);
                                }
                        }
                        reader.close();
                } catch (IOException ex) {
                }
        }

        private class Data {

                String name;
                int mass;
                int intensity;
                int sumIntensities;

                public Data(String[] row) {
                        name = row[0];
                        try {
                                mass = Integer.parseInt(row[1]);
                                intensity = Integer.parseInt(row[2]);
                                sumIntensities = Integer.parseInt(row[3]);
                        } catch (Exception exception) {
                        }
                }
        }
}
