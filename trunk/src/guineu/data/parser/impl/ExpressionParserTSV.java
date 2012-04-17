/*
 * Copyright 2007-2012 VTT Biotechnology
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
import guineu.data.DatasetType;
import guineu.data.ParameterType;
import guineu.data.impl.datasets.SimpleExpressionDataset;
import guineu.data.impl.peaklists.SimplePeakListRowExpression;
import guineu.data.parser.Parser;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class ExpressionParserTSV implements Parser {

    private String assayPath, featurePath, phenoPath;
    private SimpleExpressionDataset dataset;
    private int rowsNumber;
    private int rowsReaded;
    private int ID = 0;

    public ExpressionParserTSV(String assayPath, String featurePath, String phenoPath, String datasetName) {
        this.rowsNumber = 0;
        this.rowsReaded = 0;
        this.assayPath = assayPath;
        this.featurePath = featurePath;
        this.phenoPath = phenoPath;

        this.dataset = new SimpleExpressionDataset(getDatasetName() + datasetName);
        this.dataset.setType(DatasetType.EXPRESSION);

    }

    public String getDatasetName() {
        String n = "GENE EXPRESSION - ";
        return n;
    }

    public float getProgress() {
        return (float) rowsReaded / rowsNumber;
    }

    public void fillData() {
        try {
            this.countNumberRows();
            
            CsvReader reader = new CsvReader(new FileReader(assayPath));
            reader.setDelimiter('\t');

            reader.readHeaders();
            String[] header = reader.getHeaders();
            setExperimentsName(header);
            Hashtable<String, List<String>> features = readFeature();

            while (reader.readRecord()) {
                getData(reader.getValues(), header, features);
                rowsReaded++;
            }

            if (this.phenoPath != null) {
                fillPhenoData();
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void countNumberRows() {
        try {
            CsvReader reader = new CsvReader(new FileReader(assayPath));
            while (reader.readRecord()) {
                this.rowsNumber++;
            }
            reader.close();
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

    private void getData(String[] sdata, String[] header, Hashtable<String, List<String>> features) {
        try {
            SimplePeakListRowExpression row = new SimplePeakListRowExpression(ID++,features.get(sdata[0]).get(0), 0.0, 0.0);
            for (int i = 0; i < sdata.length; i++) {
                if (i == 0 && features != null) {
                    List<String> data = features.get(sdata[i]);
                    Vector<String> names = this.dataset.getMetaDataNames();
                    for (int e = 0; e < names.size(); e++) {
                        row.setMetaData(names.elementAt(e), data.get(e + 1));
                    }
                } else {
                    try {
                        row.setPeak(header[i], Double.valueOf(sdata[i]));
                    } catch (Exception e) {
                        if (sdata[i].matches(".*null.*|.*NA.*|.*N/A.*")) {
                            row.setPeak(header[i], 0.0);
                        } else if (sdata[i] != null) {
                            row.setPeak(header[i], sdata[i].toString());
                        }
                    }
                }
            }
            row.setSelectionMode(false);
            this.dataset.addRow(row);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    private void setExperimentsName(String[] header) {
        try {
            for (int i = 1; i < header.length; i++) {
                this.dataset.addColumnName(header[i]);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Hashtable readFeature() {
        try {
            Hashtable<String, List<String>> features = new Hashtable<String, List<String>>();
            CsvReader reader = new CsvReader(new FileReader(featurePath));
            reader.setDelimiter('\t');
            reader.readHeaders();
            String[] header = reader.getHeaders();
            for (int i = 1; i < header.length; i++) {
                this.dataset.setMetaDataNames(header[i]);
            }
            while (reader.readRecord()) {
                List<String> data = new ArrayList<String>();
                String[] sdata = reader.getValues();
                String label = sdata[0];
                for (String d : sdata) {
                    data.add(d);
                }
                features.put(label, data);
            }

            return features;

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void fillPhenoData() {
        try {

            CsvReader reader = new CsvReader(new FileReader(phenoPath));
            reader.setDelimiter('\t');
            reader.readHeaders();
            String[] header = reader.getHeaders();

            while (reader.readRecord()) {
                String[] data = reader.getValues();
                String sampleName = data[0];
                for (int i = 1; i < header.length; i++) {
                    dataset.addParameterValue(sampleName, header[i], data[i]);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
