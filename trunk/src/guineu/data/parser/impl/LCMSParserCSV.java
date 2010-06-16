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
package guineu.data.parser.impl;

import com.csvreader.CsvReader;
import guineu.data.Dataset;
import guineu.data.ParameterType;
import guineu.data.datamodels.LCMSColumnName;
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
        return (float) rowsReaded / rowsNumber;
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

    private Object getType(String data, ParameterType type) {
        switch (type) {
            case BOOLEAN:
                return new Boolean(data);
            case INTEGER:               
                return Integer.valueOf(data);
            case DOUBLE:
                return Double.valueOf(data);
            case STRING:
                return data;
        }

        return null;
    }

    private void getData(String[] sdata, String[] header) {
        try {
            SimplePeakListRowLCMS lipid = new SimplePeakListRowLCMS();
            for (int i = 0; i < sdata.length; i++) {
                if (i >= header.length) {
                }
                boolean isfound = false;
                for (LCMSColumnName field : LCMSColumnName.values()) {
                    if (header[i].matches(field.getRegularExpression())) {
                        isfound = true;
                        if (field == LCMSColumnName.RT) {
                            double rt = Double.parseDouble(sdata[i]);
                            if (rt < 20) {
                                rt *= 60;
                                sdata[i] = String.valueOf(rt);
                            }
                        }
                        lipid.setVar(field.getSetFunctionName(), this.getType(sdata[i], field.getType()));
                        break;
                    }
                }
                if (!isfound) {
                    try {
                        lipid.setPeak(header[i], Double.valueOf(sdata[i]));
                    } catch (Exception e) {
                        if (sdata[i].matches(".*null.*|.*NA.*|.*N/A.*")) {
                            lipid.setPeak(header[i], 0.0);
                        } else if (sdata[i] != null) {
                            lipid.setPeak(header[i], sdata[i].toString());
                        }
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
            for (LCMSColumnName value : LCMSColumnName.values()) {
                regExpression += value.getRegularExpression() + "|";
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
