/*
Copyright 2006-2007 VTT Biotechnology

This file is part of MYLLY.

MYLLY is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

MYLLY is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with MYLLY; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package guineu.modules.mylly.filter.pubChem;

import com.csvreader.CsvReader;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Corrects RTIs for all peaks based on give list on alkanes.
 * @author jmjarkko
 *
 */
public class PubChem {

	Hashtable<String, String[]> pubchemNames;

	public PubChem() {
		this.pubchemNames = new Hashtable<String, String[]>();
	}

	public String getName() {
		return "Filter by peak name";
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
		//we don't want to apply this filter in the peaks with Quant Mass		

		List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();

		for (SimplePeakListRowGCGC row : input.getAlignment()) {
		
			if (this.pubchemNames.containsKey(row.getName())) {				
				SimplePeakListRowGCGC clonedRow = (SimplePeakListRowGCGC) row.clone();
				String[] data = this.pubchemNames.get(row.getName());
				clonedRow.setPubChemID(data[1]);
				clonedRow.setCAS(data[0]);
				clonedRow.setName(data[2]);
				als.add(clonedRow);
			}else{
				als.add((SimplePeakListRowGCGC) row.clone());
			}

		}
		SimpleGCGCDataset filtered = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
		filtered.addAll(als);
		return filtered;
	}
}
