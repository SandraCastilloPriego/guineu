/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.data.parser.impl;

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
        Pattern pat = Pattern.compile("\\\\");
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
            FileReader fr = new FileReader(new File(datasetPath));
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            String head = br.readLine();
            String[] header;
            if (head.contains("\t")) {
                header = head.split("\t");
            } else {
                header = head.split(",");
            }

            while ((line = (br.readLine())) != null) {
                if (!line.isEmpty()) {
                    getData(line, header);
                }
            }

            setExperimentsName(header);

        } catch (Exception e) {

        }
    }

    private void getData(String line, String[] header) {
        try {
            SimplePeakListRowLCMS lipid = new SimplePeakListRowLCMS();
            String[] sdata = line.split(",");

            for (int i = 0; i < sdata.length; i++) {
                if (i >= header.length) {
                } else if (header[i].matches(".*ID.*")) {
                    lipid.setID(Integer.valueOf(sdata[i]));
                } else if (header[i].matches(".*Average M/Z.*") || header[i].matches(".*Average m/z.*")  || header[i].matches(".*row m/z.*")) {
                    lipid.setMZ(Double.valueOf(sdata[i]));
                } else if (header[i].matches(".*Average RT.*") || header[i].matches(".*Average retention time.*") || header[i].matches(".*retention time*")) {
                    double rt = Double.valueOf(sdata[i]);
                    if (rt < 20) {
                        rt = rt * 60;
                    }
                    lipid.setRT(rt);
                } else if (header[i].matches(".*Num Found.*") || header[i].matches(".*Number of detected peaks.*") || header[i].matches(".*n_found.*")  || header[i].matches(".*number of detected peaks.*")) {
                    lipid.setNumFound(Double.valueOf(sdata[i]).doubleValue());
                } else if (header[i].matches(".*Standard.*")) {
                    lipid.setStandard(Integer.valueOf(sdata[i]));
                } else if (header[i].matches(".*Class.*")) {
                } else if (header[i].matches(".*FAComposition.*")) {
                    lipid.setFAComposition(sdata[i]);
                } else if (header[i].matches(".*LipidName.*") || header[i].matches(".*Lipid name.*") || header[i].matches(".*Lipid Name.*") || header[i].matches("^Name.*") ) {
                    lipid.setName(sdata[i]);
                } else if (header[i].matches(".*Identity.*") ||  header[i].matches(".*All Names.*")) {
                    lipid.setAllNames(sdata[i]);
                } else if (header[i].matches(".*Aligment.*") || header[i].matches(".*Alignment.*")) {
                    try {
                        lipid.setNumberAlignment(Integer.valueOf(sdata[i]));
                    } catch (Exception e) {
                        lipid.setNumberAlignment(0);
                    }
                } else {
                    try {
                        lipid.setPeak(header[i], Double.valueOf(sdata[i]));
                    } catch (Exception e) {
                        lipid.setPeak(header[i], 0.0);
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

            for (int i = 0; i < header.length; i++) {
                if (!header[i].matches(".*ID.*") && !header[i].matches(".*Average M/Z.*") && !header[i].matches(".*Average m/z.*") && !header[i].matches(".*row m/z.*") && !header[i].matches(".*Alignment.*") && !header[i].matches(".*number of detected peaks.*") && !header[i].matches(".*Aligment.*") && (!header[i].matches(".*Average RT.*") && !header[i].matches(".*retention time.*") && !header[i].matches(".*Average retention time.*")) && (!header[i].matches(".*Num Found.*") && !header[i].matches(".*Number of detected peaks.*") && !header[i].matches(".*n_found.*")) && !header[i].matches(".*Standard.*") && !header[i].matches(".*Class.*") && !header[i].matches(".*FAComposition.*") && (!header[i].matches(".*LipidName.*") && !header[i].matches(".*Lipid name.*") && !header[i].matches(".*Lipid Name.*")) && (!header[i].matches(".*Identity.*") && !header[i].matches(".*Name.*") && !header[i].matches(".*All Names.*"))) {
                    this.dataset.AddNameExperiment(header[i]);
                }
            }

        } catch (Exception exception) {
        }
    }
}
