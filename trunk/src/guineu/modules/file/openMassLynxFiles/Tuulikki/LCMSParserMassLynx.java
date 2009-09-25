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
package guineu.modules.file.openMassLynxFiles.Tuulikki;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimplePeakListRowOther;
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
			FileReader fr = new FileReader(new File(datasetPath));
			BufferedReader br = new BufferedReader(fr);
			String line = br.readLine();
			String compound = "";
			boolean oneTime = false;
			this.dataset.AddNameExperiment("Sample Name");
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
					} else if(!compound.isEmpty()){						
						String[] data = line.split("\t");						
						dataset.AddNameExperiment(data[2]);
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
			}
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
						this.dataset.AddNameExperiment(name);
					}
				} else if (header[i] != null && !header[i].isEmpty() && !header[i].matches("Name") && !header[i].matches("#")) {
					this.dataset.AddNameExperiment(compound + " - " + header[i]);
				}
			}


		} catch (Exception exception) {
		}
	}
}
