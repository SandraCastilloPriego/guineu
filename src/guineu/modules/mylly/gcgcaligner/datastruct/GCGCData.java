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
package guineu.modules.mylly.gcgcaligner.datastruct;

import guineu.modules.mylly.helper.Helper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Unmodifiable view to peaks in one file.
 * @author jmjarkko
 */
public class GCGCData implements Iterable<GCGCDatum>, Cloneable {

	private static long nextId = Long.MIN_VALUE;
	private List<GCGCDatum> data; //This one is unmodifiable
	private String name,  CAS;
	private int hashcode;
	private boolean hashcodeCalculated = false;
	private long id;

	private synchronized static long getId() {
		return nextId++;
	}

	public GCGCData(List<GCGCDatum> list, String name) {
		this.data = list;
		this.name = name;
		this.id = getId();
	}

	/**
	 * Does not perform a deep copy as GCGCDatum is supposed to
	 * be immutable.
	 */
	public GCGCData clone() {
		try {
			GCGCData clone = (GCGCData) super.clone();
			clone.data = new ArrayList<GCGCDatum>();
			clone.data.addAll(data);
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new InternalError("Clone not supported");
		}
	}

	public int compoundCount() {
		return data.size();
	}

	public GCGCDatum getCompound(int ix) {
		return data.get(ix);
	}

	public boolean equals(Object o) {
		if (o instanceof GCGCData) {
			GCGCData other = (GCGCData) o;
			return other.id == id;
		}
		return false;
	}

	public int hashCode() {
		if (!hashcodeCalculated) {
			hashcode = name.hashCode() + 31 * data.hashCode();
			hashcodeCalculated = true;
		}
		return hashcode;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCAS() {
		return CAS;
	}

	public Iterator<GCGCDatum> iterator() {
		return data.iterator();
	}

	public List<GCGCDatum> toList() {
		ArrayList<GCGCDatum> list = new ArrayList<GCGCDatum>(data);
		return list;
	}

	public GCGCDatum[] toArray() {
		int len = data.size();
		int ix = 0;
		GCGCDatum array[] = new GCGCDatum[len];
		for (GCGCDatum d : data) {
			array[ix++] = d;
		}
		return array;
	}

	public static List<GCGCData> loadfiles(String separator, boolean filterClassified, boolean findSpectrum, boolean findConcentration, File... files) throws IOException {
		List<GCGCData> datalist = new ArrayList<GCGCData>();
		for (File f : files) {
			GCGCData gcgcdata = new GCGCData(readGCGCDataFile(f, separator, filterClassified, findSpectrum, findConcentration), f.getName());
			datalist.add(gcgcdata);
		}
		return datalist;
	}

	private static List<? extends Pair<Integer, Integer>> parseSpectrum(String str) {
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

	/**
	 *
	 * @param file
	 * @param filterClassified TODO
	 * @param findSpectrum TODO
	 * @param findConcentration TODO
	 * @return List<GCGCData> containing peaks
	 * @throws IOException
	 */
	public static List<GCGCDatum> readGCGCDataFile(File file, String separator, boolean filterClassified, boolean findSpectrum, boolean findConcentration) throws IOException {
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

		BufferedReader br = null;
		FileReader fr = null;

		rtPos = ClassificationsPos = riPos = spectrumPos = concentrationPos = -1;
		areaPos = casPos = namePos = simPos = quantMassPos = -1;

		try {
			fr = new FileReader(file);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (NullPointerException e2) {
			throw e2;
		}

		//First read the header-row
		{
			String headerStr;
			String headerRow[];
			do {
				headerStr = br.readLine();
			} while ("".equals(headerStr.trim()));//skip blank lines

			headerRow = Pattern.compile("\t+").split(headerStr, 0);
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
				} else if (filterClassified && "Classifications".equals(str)) {
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
			String line;
			String splitRow[];
			peaks = new ArrayList<GCGCDatum>();

			while ((line = br.readLine()) != null) {
				boolean foundArea = false;
				boolean foundRT1 = false;
				boolean foundRT2 = false;
				boolean foundCAS = false;
				boolean foundName = false;
				boolean foundSimilarity = false;
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
				List<? extends Pair<Integer, Integer>> spectrum = null;

				int similarity = 800;
				String name, CAS;

				rt1 = rt2 = area = retentionIndex = similarity = -1;
				quantMass = GCGCDatum.NO_QUANT_MASS;
				name = "Not Found";
				CAS = "0-00-0";
				splitRow = Pattern.compile(separator).split(line, 0);
				for (int i = 0; i < splitRow.length; i++) {
					String curStr = splitRow[i];
					if (!(filterClassified && i == ClassificationsPos) &&
							"".equals(curStr)) {
						continue;
					}

					if (i == rtPos) {
						String rts[] = curStr.split(" , ");
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
								conc = Double.valueOf(curStr);
							} catch (NumberFormatException e) {
							}
							foundConc = true;
						}
					} else if (i == casPos) {
						CAS = curStr;
						name = curStr;
						foundCAS = true;
					} else if (i == namePos) {
						name = Helper.stripCAS(curStr);
						foundName = true;
					} else if (i == simPos) {
						try {
							similarity = "".equals(curStr) ? 0 : Integer.parseInt(curStr);
							if (similarity == 0) {
								similarity = 800;
							}
							foundSimilarity = true;
						} catch (NumberFormatException e) {

							foundSimilarity = true;

						}
					} else if (filterClassified && i == ClassificationsPos) {
						foundClassificationPos = true;
						if (curStr != null && !"".equals(curStr)) {
							//We have some classification for the peak so drop it
							break;
						}
					} else if (i == spectrumPos && findSpectrum) {
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

					if (foundArea && foundRT1 &&
							foundRT2 && foundName && foundCAS &&
							foundSimilarity && foundQuantMass &&
							(!findSpectrum || foundSpectrum) &&
							(!findConcentration || foundConc) &&
							(!filterClassified || foundClassificationPos)) {
						if (!"Not Found".equals(name)) {
							GCGCDatum currentRow;
							if (foundConc) {
								currentRow = new GCGCDatumWithConcentration(rt1, rt2, retentionIndex,
										quantMass, similarity, area, CAS, name, useConc,
										spectrum, conc);
							} else if (foundRetentionIndex) {
								currentRow = new GCGCDatum(rt1, rt2, retentionIndex,
										quantMass, similarity, area, conc, useConc,
										CAS, name, spectrum);
							} else {
								currentRow = new GCGCDatum(rt1, rt2, quantMass,
										area, conc, useConc, similarity, CAS, name, spectrum);
							}
							peaks.add(currentRow);
						System.out.printf("Created new peak %s\n", currentRow);
						}
						break;
					}
				}//End for-loop for (int i = 0; i < splitRow.length; i++)...
			}
		}
		if (br != null) {
			br.close();
		}
		return peaks;
	}
}
