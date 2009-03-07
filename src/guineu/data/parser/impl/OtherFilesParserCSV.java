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
package guineu.data.parser.impl;

import com.csvreader.CsvReader;
import guineu.data.Dataset;
import guineu.data.PeakListRowOther;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDatasetOther;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.data.parser.Parser;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class OtherFilesParserCSV implements Parser {

    private String datasetPath;
    private SimpleDatasetOther dataset;
    private float progress;
    Lipidclass LipidClassLib;

    public OtherFilesParserCSV(String datasetPath) {
        progress = 0.1f;
        this.datasetPath = datasetPath;
        this.dataset = new SimpleDatasetOther(this.getDatasetName());
        this.dataset.setType(DatasetType.OTHER);
        progress = 0.3f;
        this.dataset.setType(null);
        this.LipidClassLib = new Lipidclass();
        progress = 0.5f;
        fillData();
        progress = 1.0f;
    }

    public String getDatasetName() {
        Pattern pat = Pattern.compile("[\\\\/]");
        Matcher matcher = pat.matcher(datasetPath);
        int index = 0;
        while (matcher.find()) {
            index = matcher.start();
        }
        String n = datasetPath.substring(index + 1, datasetPath.length() - 4);
        return n;
    }

    public float getProgress() {
        return progress;
    }

    public void fillData() {
        try {
            CsvReader reader = new CsvReader(new FileReader(datasetPath));
            reader.readHeaders();
            String[] header = reader.getHeaders();
            while (reader.readRecord()) {
                getData(reader.getValues(), header);
            }
            setExperimentsName(header);

        } catch (Exception e) {
        }
    }

    private void getData(String[] sdata, String[] header) {
        try {
            PeakListRowOther lipid = new SimplePeakListRowOther();
            for (int i = 0; i < sdata.length; i++) {
                try {
                    lipid.setPeak(header[i], sdata[i].toString());
                } catch (Exception e) {
                    lipid.setPeak(header[i], " ");
                }

            }

            this.dataset.AddRow(lipid);

        } catch (Exception exception) {
        }
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    private void setExperimentsName(String[] header) {
        try {
            for (int i = 0; i < header.length; i++) {
                this.dataset.AddNameExperiment(header[i]);
            }
        } catch (Exception exception) {
        }
    }
}
