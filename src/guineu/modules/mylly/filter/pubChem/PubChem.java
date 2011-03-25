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
package guineu.modules.mylly.filter.pubChem;

import com.csvreader.CsvReader;
import guineu.data.PeakListRow;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class PubChem {

    Hashtable<String, String[]> pubchemNames;

    public PubChem() {
        this.pubchemNames = new Hashtable<String, String[]>();
    }

    public String getName() {
        return "PubChem ID Filter";
    }

    public void createCorrector(File pubChemFile) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(pubChemFile));
        CsvReader reader = new CsvReader(br);

        while (reader.readRecord()) {
            String data[] = reader.getValues();
            try {
                this.pubchemNames.put(data[3], data);
            } catch (Exception e) {
            }
        }

    }

    public SimpleGCGCDataset actualMap(SimpleGCGCDataset input) {
        List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();

        for (PeakListRow row : input.getAlignment()) {
            if (this.pubchemNames.containsKey((String) row.getVar("getName"))) {
                SimplePeakListRowGCGC clonedRow = (SimplePeakListRowGCGC) row.clone();
                String[] data = this.pubchemNames.get((String) row.getVar("getName"));
                clonedRow.setPubChemID(data[1]);
                clonedRow.setCAS(data[0]);
                clonedRow.setName(data[2]);
                als.add(clonedRow);
            } else {
                als.add((SimplePeakListRowGCGC) row.clone());
            }
        }
        
        SimpleGCGCDataset filtered = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
        filtered.addAll(als);
        return filtered;
    }
}
