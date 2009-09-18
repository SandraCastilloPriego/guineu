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
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.parser.Parser;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class LCMSParserCSV implements Parser {

    private String datasetPath;
    private SimpleLCMSDataset dataset;
    private int rowsNumber;
    private int rowsReaded;
    Lipidclass LipidClassLib;

    public LCMSParserCSV(String datasetPath) {
        this.rowsNumber = 0;
        this.rowsReaded = 0;
        this.datasetPath = datasetPath;
        this.dataset = new SimpleLCMSDataset(this.getDatasetName());
        this.dataset.setType(DatasetType.LCMS);
        this.LipidClassLib = new Lipidclass();
        countNumberRows(); 
    }

    public String getDatasetName() {
        Pattern pat = Pattern.compile("[\\\\/]");
        Matcher matcher = pat.matcher(datasetPath);
        int index = 0;
        while (matcher.find()) {
            index = matcher.start();
        }
        String n = "LCMS - " + datasetPath.substring(index + 1, datasetPath.length() - 4);
        return n;
    }

    public float getProgress() {
        return (float)rowsReaded/rowsNumber;
    }

    public void fillData() {
        try {
            CsvReader reader = new CsvReader(new FileReader(datasetPath));

            reader.readHeaders();
            String[] header = reader.getHeaders();

            while (reader.readRecord()) {
                getData(reader.getValues(), header);
                rowsReaded++;
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
                } else if (header[i].matches(RegExp.ID.getREgExp())) {
                    lipid.setID(Integer.valueOf(sdata[i]));
                } else if (header[i].matches(RegExp.MZ.getREgExp())) {
                    lipid.setMZ(Double.valueOf(sdata[i]));
                } else if (header[i].matches(RegExp.RT.getREgExp())) {
                    double rt = Double.valueOf(sdata[i]);
                    if (rt < 20) {
                        rt = rt * 60;
                    }
                    lipid.setRT(rt);
                } else if (header[i].matches(RegExp.NFOUND.getREgExp())) {
                    lipid.setNumFound(Double.valueOf(sdata[i]).doubleValue());
                } else if (header[i].matches(RegExp.STANDARD.getREgExp())) {
                    lipid.setStandard(Integer.valueOf(sdata[i]));
                } else if (header[i].matches(RegExp.CLASS.getREgExp())) {
                } else if (header[i].matches(RegExp.FA.getREgExp())) {
                    lipid.setFAComposition(sdata[i]);
                } else if (header[i].matches(RegExp.NAME.getREgExp())) {
                    lipid.setName(sdata[i]);
                } else if (header[i].matches(RegExp.ALLNAMES.getREgExp())) {
                    lipid.setAllNames(sdata[i]);
                } else if (header[i].matches(RegExp.ALIGNMENT.getREgExp())) {
                    try {
                        lipid.setNumberAlignment(Integer.valueOf(sdata[i]));
                    } catch (Exception e) {
                        lipid.setNumberAlignment(0);
                    }
                } else {
                    try {
                        lipid.setPeak(header[i], Double.valueOf(sdata[i]));
                    } catch (Exception e) {
                        lipid.setPeak(header[i], Double.valueOf(0.0));
                    }
                }
                if (lipid.getName() == null || lipid.getName().isEmpty()) {
                    lipid.setName("unknown");
                }
               // lipid.setLipidClass(this.LipidClassLib.get_class(lipid.getName()));
            }
            lipid.setSelectionMode(false);
            this.dataset.AddRow(lipid);

        } catch (Exception exception) {
			exception.printStackTrace();
        }
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    private void setExperimentsName(String[] header) {
        try {
           
            String regExpression = "";
            for (RegExp value : RegExp.values()) {
                regExpression += value.getREgExp() + "|";
            }
            for (int i = 0; i < header.length; i++) {
                if (!header[i].matches(regExpression)) {
                    this.dataset.AddNameExperiment(header[i]);
                }
            }
           
        } catch (Exception exception) {
        }
    }

    private void countNumberRows() {
        try {
            CsvReader reader = new CsvReader(new FileReader(datasetPath));
            while (reader.readRecord()) {
                this.rowsNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
