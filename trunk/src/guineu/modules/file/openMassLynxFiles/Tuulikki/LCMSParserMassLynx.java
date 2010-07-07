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
package guineu.modules.file.openMassLynxFiles.Tuulikki;

import com.csvreader.CsvReader;
import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.DatasetType;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.data.parser.Parser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class LCMSParserMassLynx implements Parser {

    private String datasetPath;
    private SimpleOtherDataset dataset;
    private float progress;

    public LCMSParserMassLynx(String datasetPath) {
        progress = 0.1f;
        this.datasetPath = datasetPath;
        this.dataset = new SimpleOtherDataset(this.getDatasetName());
        this.dataset.setType(DatasetType.OTHER);
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
            CsvReader reader = new CsvReader(new FileReader(datasetPath));

            reader.readHeaders();
            String[] header = reader.getHeaders();
            for (int i = 0; i < 6; i++) {
                this.dataset.AddColumnName(header[i]);
            }
            boolean open = true;
           
            String[] data;
            String studyID = null;
            String subjID = null;
            String Mseg = null;
            String length = null;
            String visitNum = null;
            String visit = null;
            List<String> values = new ArrayList<String>();
            List<String> names = new ArrayList<String>();

            for (int i = 0; i < 15; i++) {
                reader.readRecord();
                data = reader.getValues();
                if (open) {
                    studyID = data[0];
                    subjID = data[1];
                    Mseg = data[2];
                    length = data[3];
                    visitNum = data[4];
                    visit = data[5];
                    values = new ArrayList<String>();
                    names = new ArrayList<String>();
                    open = false;
                }
                this.dataset.AddColumnName(data[7]);
                names.add(data[7]);
                values.add(data[8]);               
            }

            SimplePeakListRowOther row = new SimplePeakListRowOther();
            row.setPeak("STUDYID", studyID);
            row.setPeak("SUBJID", subjID);
            row.setPeak("MSEG", Mseg);
            row.setPeak("LENGTHSG", length);
            row.setPeak("VISITNUM", visitNum);
            row.setPeak("VISIT", visit);

            for (int i = 0; i < names.size(); i++) {
                row.setPeak(names.get(i), values.get(i));
            }

            this.dataset.AddRow(row);

            open = true;

            String lastValue = null;
            while (reader.readRecord()) {
                data = reader.getValues();
                if (open) {
                    studyID = data[0];
                    subjID = data[1];
                    Mseg = data[2];
                    length = data[3];
                    visitNum = data[4];
                    visit = data[5];
                    values = new ArrayList<String>();
                    if(lastValue != null){
                        values.add(lastValue);
                    }
                    open = false;
                }
                values.add(data[8]);
                if (data[1].compareTo(subjID) != 0) {
                    row = new SimplePeakListRowOther();
                    row.setPeak("STUDYID", studyID);
                    row.setPeak("SUBJID", subjID);
                    row.setPeak("MSEG", Mseg);
                    row.setPeak("LENGTHSG", length);
                    row.setPeak("VISITNUM", visitNum);
                    row.setPeak("VISIT", visit);

                    for (int i = 0; i < names.size(); i++) {
                        row.setPeak(names.get(i), values.get(i));
                    }
                    this.dataset.AddRow(row);
                    lastValue = data[8];
                    open = true;
                }

            }


        /* FileReader fr = new FileReader(new File(datasetPath));
        BufferedReader br = new BufferedReader(fr);
        String line = br.readLine();
        String compound = "";
        boolean oneTime = false;
        this.dataset.AddColumnName("Sample Name");
        SimplePeakListRowOther row = null;
        SimplePeakListRowOther rt = new SimplePeakListRowOther();
        rt.setPeak("Sample Name", "RT");
        // Write Columns
        while ((line = (br.readLine())) != null) {
        if (!line.isEmpty()) {
        if (line.matches("^Compound.*|^Sample Name.*")) {
        compound = line;
        if (oneTime) {
        break;
        }
        oneTime = true;
        br.readLine();
        br.readLine();
        } else if (!compound.isEmpty()) {
        String[] data = line.split("\t");
        dataset.AddColumnName(data[2]);
        rt.setPeak(data[2], data[4]);
        }
        }

        }
        dataset.AddRow(rt);
        br.mark(0);
        br.reset();
        // Write content
        while ((line = (br.readLine())) != null) {
        if (!line.isEmpty()) {
        if (line.matches("^Compound.*|^Sample Name.*")) {
        row = new SimplePeakListRowOther();
        compound = line;
        compound = compound.substring(compound.indexOf(":") + 1);
        br.readLine();
        br.readLine();
        row.setPeak("Sample Name", compound);
        dataset.AddRow(row);
        } else if (row != null) {
        String[] data = line.split("\t");
        row.setPeak(data[2], data[5]);
        }
        }
        }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData(PeakListRow lipid, String line, String[] header, String compound) {
        try {
            //PeakListRow_concatenate lipid = new SimplePeakListRowConcatenate();
            String[] sdata = line.split("\t");

            for (int i = 0; i < sdata.length; i++) {
                try {
                    if (!header[i].isEmpty()) {
                        String name = compound + " - " + header[i];
                        if (header[i].matches("Name")) {
                            name = "Name";
                        } else if (header[i].matches("#")) {
                            name = "#";
                        }
                        lipid.setPeak(name, sdata[i].toString());
                    }
                } catch (Exception e) {
                    //lipid.setPeak(header[i], " ");
                }

            }

        //this.dataset.AddRow(lipid);

        } catch (Exception exception) {
        }
    }

    public Dataset getDataset() {
        return this.dataset;
    }

    private void setExperimentsName(String[] header, String compound, boolean putName) {
        try {

            for (int i = 0; i < header.length; i++) {
                if (putName) {
                    String name = compound + " - " + header[i];
                    if (header[i].matches("Name")) {
                        name = "Name";
                    } else if (header[i].matches("#")) {
                        name = "#";
                    }
                    if (header[i] != null && !header[i].isEmpty()) {
                        this.dataset.AddColumnName(name);
                    }
                } else if (header[i] != null && !header[i].isEmpty() && !header[i].matches("Name") && !header[i].matches("#")) {
                    this.dataset.AddColumnName(compound + " - " + header[i]);
                }
            }


        } catch (Exception exception) {
        }
    }
}
