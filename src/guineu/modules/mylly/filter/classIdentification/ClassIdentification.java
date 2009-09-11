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
package guineu.modules.mylly.filter.classIdentification;

import com.csvreader.CsvReader;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.modules.mylly.datastruct.ComparablePair;
import guineu.modules.mylly.datastruct.Spectrum;
import guineu.modules.mylly.datastruct.Spectrum.SortingMode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Corrects RTIs for all peaks based on give list on alkanes.
 * @author jmjarkko
 *
 */
public class ClassIdentification {

	private List<String[]> rules;
	private double numberRowProcessed = 0;

	private SimpleGCGCDataset input;


	public ClassIdentification() {
		this.rules = new ArrayList<String[]>();
	}

	public String getName() {
		return "Class Identification";
	}

	public double getProgress(){		
		return (double)(numberRowProcessed / (double) input.getAlignment().size());
	}

	public void createCorrector(File rulesFile) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(rulesFile));
		CsvReader reader = new CsvReader(br);

		while (reader.readRecord()) {
			String data[] = reader.getValues();
			try {			
				this.rules.add(data);
			} catch (Exception e) {
			}
		}

	}

	public SimpleGCGCDataset actualMap(SimpleGCGCDataset input) {
		//we don't want to apply this filter in the peaks with Quant Mass		
		this.input = input;
		List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();

		for (SimplePeakListRowGCGC row : input.getAlignment()) {
				SimplePeakListRowGCGC clonedRow = (SimplePeakListRowGCGC)row.clone();
				this.setRules(clonedRow);				
				als.add(clonedRow);
				this.numberRowProcessed++;
		}
		SimpleGCGCDataset filtered = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
		filtered.addAll(als);
		return filtered;
	}

	private void setRules(SimplePeakListRowGCGC clonedRow) {
		Spectrum spectra = clonedRow.getSpectrum();
        spectra.sort(SortingMode.INTENSITY);
		List<ComparablePair<Integer, Integer>> spectrumRow = spectra.getPeakList();
		for(String[] rule: this.rules){
			Rules r = new Rules( clonedRow, spectrumRow,  rule[1]);
			if(r.getResult()){
				String rowClass = clonedRow.getMolClass();
				if(rowClass != null && !rowClass.isEmpty()){
					rowClass += " \\\\ ";
					rowClass += rule[0];
				}else{
					rowClass = rule[0];
				}
				clonedRow.setClass(rowClass);
			}
		}
	}


}
