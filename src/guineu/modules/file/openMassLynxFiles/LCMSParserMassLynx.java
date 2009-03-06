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
package guineu.modules.file.openMassLynxFiles;

import guineu.data.parser.impl.*;
import guineu.data.Dataset;
import guineu.data.PeakListRowOther;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleDatasetOther;
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
	private SimpleDatasetOther dataset;
	private float progress;
	Lipidclass LipidClassLib;

	public LCMSParserMassLynx(String datasetPath) {
		progress = 0.1f;
		this.datasetPath = datasetPath;
		this.dataset = new SimpleDatasetOther(this.getDatasetName());
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
			String line = br.readLine();
			String head = null;
			String[] header = null;
			String compound = "";
			PeakListRowOther lipid = null;
			int contRow = 0;
			int contLipids = 0;
			while ((line = (br.readLine())) != null) {
				if (!line.isEmpty()) {
					if (line.matches("^Compound.*")) {
						compound = line;
						compound = compound.substring(compound.indexOf(":") + 1);
						br.readLine();
						head = br.readLine();
						header = head.split("\t");
						contRow = 0;
						contLipids++;

						if (contLipids == 1) {
							setExperimentsName(header, compound, true);
						} else {
							setExperimentsName(header, compound, false);
						}
						continue;
					}
					if (compound != null) {
						if (contLipids == 1) {
							lipid = new SimplePeakListRowOther();
						} else if (contLipids > 1) {
							lipid = (PeakListRowOther) this.dataset.getRow(contRow);
						}
						if (head != null && !head.isEmpty()) {
							getData(lipid, line, header, compound);
						}
						if (contLipids == 1) {
							this.dataset.AddRow(lipid);
						}

					}
					contRow++;
				}

			}



		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void getData(PeakListRowOther lipid, String line, String[] header, String compound) {
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
					}if (header[i] != null && !header[i].isEmpty()){
						this.dataset.AddNameExperiment(name);
					}
				} else if (header[i] != null && !header[i].isEmpty() && !header[i].matches("Name") && !header[i].matches("#") ) {
					this.dataset.AddNameExperiment(compound + " - " + header[i]);
				}
			}


		} catch (Exception exception) {
		}
	}
}
