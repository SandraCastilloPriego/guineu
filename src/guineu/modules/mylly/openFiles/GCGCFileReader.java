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
package guineu.modules.mylly.openFiles;

import com.csvreader.CsvReader;
import guineu.modules.mylly.datastruct.ComparablePair;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.modules.mylly.datastruct.GCGCDatumWithConcentration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class GCGCFileReader {

	private Character _separator;
	private boolean _filterClassified;
	private boolean _findSpectrum;
	private boolean _findConcentration;

	public GCGCFileReader(String separator, boolean filterClassified) {		
		if(separator.equals("\\t")){
			_separator = '\t';
		}else{
			_separator = separator.toCharArray()[0];
		}
		_filterClassified = filterClassified;
		_findSpectrum = true;
		_findConcentration = false;


	}

	public List<GCGCDatum> readGCGCDataFile(File file) throws IOException {
		int rtPos;
		int simPos;
		int areaPos;
		int casPos;
		int namePos;
		int riPos;
		int ClassificationsPos;
		int spectrumPos;
		int quantMassPos;
		int concentrationPos;
		List<GCGCDatum> peaks;
		
		FileReader fr = null;

		rtPos = ClassificationsPos = riPos = spectrumPos = concentrationPos = -1;
		areaPos = namePos = casPos = simPos = quantMassPos = -1;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (NullPointerException e2) {
			throw e2;
		}

        CsvReader reader = new CsvReader(fr,_separator);

		//First read the header-row
		{

            reader.readHeaders();			
			String headerRow[] = reader.getHeaders();
			
			for (int i = 0; i < headerRow.length; i++) {
				String str = headerRow[i];
				if (str.equals("R.T. (s)")) {
					rtPos = i;
				} else if ("Sample Concentration".equals(str)) {
					concentrationPos = i;
				} else if ("Area".equals(str)) {
					areaPos = i;
				} else if ("CAS".equals(str)) {
					casPos = i;
				} else if ("Name".equals(str)) {
					namePos = i;
				} else if ("Similarity".equals(str)) {
					simPos = i;
				} else if ("Retention Index".equals(str)) {
					riPos = i;
				} else if (_filterClassified && "Classifications".equals(str)) {
					ClassificationsPos = i;
				} else if ("Spectra".equals(str)) {
					spectrumPos = i;
				} else if ("Quant Masses".equals(str)) {
					quantMassPos = i;
				}
			}
		}
		// Then go through the file row at a time
		{			
			String splitRow[];
			peaks = new ArrayList<GCGCDatum>();
			int cont = 1;
            while(reader.readRecord()){
                splitRow = reader.getValues();
				boolean foundArea = false;
				boolean foundRT1 = false;
				boolean foundRT2 = false;
				boolean foundName = false;
				boolean foundRetentionIndex = false;
				boolean foundClassificationPos = false;
				boolean foundSpectrum = false;
				boolean foundQuantMass = false;
				boolean foundConc = false;

				double rt1;
				double rt2;
				double area;
				double retentionIndex;
				double quantMass;
				double conc = -1;
				boolean useConc = false;
				List<ComparablePair<Integer, Integer>> spectrum = null;

				int similarity = 800;
				String CAS, name;

				rt1 = rt2 = area = retentionIndex = similarity = -1;
				quantMass = GCGCDatum.NO_QUANT_MASS;
				name = "Not Found";
				CAS = "";
				
				for (int i = 0; i < splitRow.length; i++) {
					String curStr = splitRow[i];
					if (!(_filterClassified && i == ClassificationsPos) &&
							"".equals(curStr)) {
						continue;
					}

					if (i == rtPos) {
						String rts[] = curStr.split(",");
						try {
							rt1 = Double.parseDouble(rts[0]);
							foundRT1 = true;
							rt2 = Double.parseDouble(rts[1]);
							foundRT2 = true;
						} catch (NumberFormatException e) {
						}
					} else if (i == riPos) {
						try {
							retentionIndex = "".equals(curStr) ? 0 : Double.parseDouble(curStr);
							foundRetentionIndex = true;
						} catch (NumberFormatException e) {
						}
					} else if (i == areaPos) {
						try {
							area = Double.parseDouble(curStr);
							foundArea = true;
						} catch (NumberFormatException e) {
						}
					} else if (i == concentrationPos) {
						if (!("".equals(curStr) || curStr == null)) {
							try {
								conc = Double.parseDouble(curStr);
							} catch (NumberFormatException e) {
							}
							foundConc = true;
						}
					} else if (i == casPos) {
						CAS = curStr;
					} else if (i == namePos) {
						name = curStr;
						foundName = true;
					} else if (i == simPos) {
						try {
							similarity = "".equals(curStr) ? 0 : Integer.parseInt(curStr);
						} catch (NumberFormatException e) {
						}
					} else if (_filterClassified && i == ClassificationsPos) {
						foundClassificationPos = true;
						if (curStr != null && !"".equals(curStr)) {
							//We have some classification for the peak so drop it
							break;
						}
					} else if (i == spectrumPos && _findSpectrum) {
						try {
							spectrum = parseSpectrum(curStr);
							foundSpectrum = true;
						} catch (NumberFormatException e) {
						}
					} else if (i == quantMassPos) {
						try {
							quantMass = Double.parseDouble(curStr);
						} catch (NumberFormatException e) {
						} finally {
							foundQuantMass = true;
						}
					}

					if (similarity < 0) {
						similarity = 800;
					}
					if (foundArea && foundRT1 &&
							foundRT2 && foundName && foundQuantMass &&
							(!_findSpectrum || foundSpectrum) &&
							(!_findConcentration || foundConc) &&
							(!_filterClassified || foundClassificationPos)) {
						if (!"Not Found".equals(name)) {
							GCGCDatum currentRow;
							if (foundConc) {
								currentRow = new GCGCDatumWithConcentration(cont++, rt1, rt2, retentionIndex,
										quantMass, similarity, area, CAS, name, useConc, file.getName(),
										spectrum, conc);
							} else if (foundRetentionIndex) {
								currentRow = new GCGCDatum(cont++, rt1, rt2, retentionIndex,
										quantMass, similarity, area, conc, useConc,
										CAS, name, file.getName(), spectrum);
							} else {
								currentRow = new GCGCDatum(cont++, rt1, rt2, quantMass,
										area, conc, useConc, similarity, CAS, name, file.getName(), spectrum);
							}
							peaks.add(currentRow);

						}
						break;
					}
				}//End for-loop for (int i = 0; i < splitRow.length; i++)...
			}
		}		
		return peaks;
	}

	private List<ComparablePair<Integer, Integer>> parseSpectrum(String str) {
		String peaks[] = str.split(" ");
		ArrayList<ComparablePair<Integer, Integer>> peakList = new ArrayList<ComparablePair<Integer, Integer>>(peaks.length);
		for (String st : peaks) {
			String tokens[] = st.split(":");
			ComparablePair<Integer, Integer> p = new ComparablePair<Integer, Integer>(
					new Integer(tokens[0]),
					new Integer(tokens[1]));
			peakList.add(p);
		}
		return peakList;
	}
}
