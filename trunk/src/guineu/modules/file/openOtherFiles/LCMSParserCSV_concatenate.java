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
package guineu.modules.file.openOtherFiles;

import guineu.data.parser.impl.*;
import guineu.data.Dataset;
import guineu.data.PeakListRow_concatenate;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDataset_concatenate;
import guineu.data.impl.SimplePeakListRowConcatenate;
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
public class LCMSParserCSV_concatenate implements Parser {

    private String datasetPath;
    private SimpleDataset_concatenate dataset;
    private float progress;
    Lipidclass LipidClassLib;

    public LCMSParserCSV_concatenate(String datasetPath) {
        progress = 0.1f;
        this.datasetPath = datasetPath;
        this.dataset = new SimpleDataset_concatenate(this.getDatasetName());
		this.dataset.setType(DatasetType.OTHER);
        progress = 0.3f;
        this.dataset.setType(null);
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
            header = head.split(",");  
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
            PeakListRow_concatenate lipid = new SimplePeakListRowConcatenate();
            String[] sdata = line.split(",");

            for (int i = 0; i < sdata.length; i++) {
                try {
                    if(sdata[i].toString().matches("null") || sdata[i].isEmpty()){
                        sdata[i] = "NA";						
                    }
					lipid.setPeak(header[i], sdata[i].toString());
                } catch (Exception e) {
                    lipid.setPeak(header[i], "NA");
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
