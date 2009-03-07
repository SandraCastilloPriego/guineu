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
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.parser.Parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class LCMSParserCSV implements Parser {

    private String datasetPath;
    private SimpleDataset dataset;
    private float progress;
    Lipidclass LipidClassLib;

    public LCMSParserCSV(String datasetPath) {
        progress = 0.1f;
        this.datasetPath = datasetPath;
        this.dataset = new SimpleDataset(this.getDatasetName());
        progress = 0.3f;
        this.dataset.setType(DatasetType.LCMS);
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
            e.printStackTrace();
        }
    }

    private void getData(String[] sdata, String[] header) {
        try {
            SimplePeakListRowLCMS lipid = new SimplePeakListRowLCMS();
            for (int i = 0; i < sdata.length; i++) {
                if (i >= header.length) {
                } else if (header[i].matches(".*ID.*")) {
                    lipid.setID(Integer.valueOf(sdata[i]));
                } else if (header[i].matches(".*Average M/Z.*|.*Average m/z.*|.*row m/z.*")) {
                    lipid.setMZ(Double.valueOf(sdata[i]));
                } else if (header[i].matches(".*Average RT.*|.*Average retention time.*|.*retention time*")) {
                    double rt = Double.valueOf(sdata[i]);
                    if (rt < 20) {
                        rt = rt * 60;
                    }
                    lipid.setRT(rt);
                } else if (header[i].matches(".*Num Found.*|.*Number of detected peaks.*|.*n_found.*|.*number of detected peaks.*")) {
                    lipid.setNumFound(Double.valueOf(sdata[i]).doubleValue());
                } else if (header[i].matches(".*Standard.*")) {
                    lipid.setStandard(Integer.valueOf(sdata[i]));
                } else if (header[i].matches(".*Class.*")) {
                } else if (header[i].matches(".*FAComposition.*")) {
                    lipid.setFAComposition(sdata[i]);
                } else if (header[i].matches(".*LipidName.*|.*Lipid name.*|.*Lipid Name.*|^Name.*")) {
                    lipid.setName(sdata[i]);
                } else if (header[i].matches(".*Identity.*|.*All Names.*")) {
                    lipid.setAllNames(sdata[i]);
                } else if (header[i].matches(".*Aligment.*|.*Alignment.*")) {
                    try {
                        lipid.setNumberAlignment(Integer.valueOf(sdata[i]));
                    } catch (Exception e) {
                        lipid.setNumberAlignment(0);
                    }
                } else {
                    try {
                        lipid.setPeak(header[i], Double.valueOf(sdata[i]));
                    } catch (Exception e) {
                        if (sdata[i].matches("DETECTED")) {
                            lipid.setPeak(header[i], 1.0);
                        } else {
                            lipid.setPeak(header[i], 0.0);
                        }
                    }
                }
                if (lipid.getName() == null || lipid.getName().isEmpty()) {
                    lipid.setName("unknown");
                }
                lipid.setLipidClass(this.LipidClassLib.get_class(lipid.getName()));
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
            int numFixColumns = 0;
            for (int i = 0; i < header.length; i++) {
                if (!header[i].matches(".*ID.*|.*Average M/Z.*|.*Average m/z.*" +
                        "|.*row m/z.*|.*Alignment.*|.*number of detected peaks.*" +
                        "|.*Aligment.*|.*Average RT.*|.*retention time.*" +
                        "|.*Average retention time.*|.*Num Found.*|.*Number of detected peaks.*" +
                        "|.*n_found.*|.*Standard.*|.*Class.*|.*FAComposition.*" +
                        "|.*LipidName.*|.*Lipid name.*|.*Lipid Name.*" +
                        "|.*Identity.*|.*Name.*|.*All Names.*")) {
                    this.dataset.AddNameExperiment(header[i]);                    
                }else{
                    numFixColumns++;
                }
            }           
            this.dataset.setNumberFixColumns(numFixColumns + 3);

        } catch (Exception exception) {
        }
    }
}
