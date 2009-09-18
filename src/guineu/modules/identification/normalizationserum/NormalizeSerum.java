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
package guineu.modules.identification.normalizationserum;

import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.taskcontrol.Task.TaskStatus;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public class NormalizeSerum {

	private SimpleLCMSDataset dataset;
	private double cont = 0;
	private StandardUmol stdMol;

	public NormalizeSerum(SimpleLCMSDataset dataset, StandardUmol stdMol) {
		this.dataset = dataset;
		this.stdMol = stdMol;
	}

	public void getStandars() {
		this.stdMol.vCer.removeAllElements();
		this.fillStd(".*Cer.*", this.stdMol.vCer);
		this.stdMol.vTAG.removeAllElements();
		this.fillStd(".*TAG.*|.*TG.*", this.stdMol.vTAG);
		this.stdMol.vGPEtn.removeAllElements();
		this.fillStd("^GPEtn.*|^PE.*", this.stdMol.vGPEtn);
		this.stdMol.vGPCho.removeAllElements();
		this.fillStd("^GPCho.*|^PC.*", this.stdMol.vGPCho);
		this.stdMol.vLysoGPCho.removeAllElements();
		this.fillStd(".*Lyso.*|.*LP.*|.*lyso.*", this.stdMol.vLysoGPCho);
		this.stdMol.vOtherValue.removeAllElements();
		this.fillStd(".*" + this.stdMol.other + ".*", this.stdMol.vOtherValue);
		this.stdMol.vOtherValue1.removeAllElements();
		this.fillStd(".*" + this.stdMol.other1 + ".*", this.stdMol.vOtherValue1);
	}

	public SimpleLCMSDataset getDataset() {
		return dataset;
	}

	private void fillStd(String lipidName, Vector vlipid) {
		for (int i = 0; i < dataset.getNumberRows(); i++) {
			String olipid = this.getLipidName(i);
			if (olipid.matches(lipidName) && ((SimplePeakListRowLCMS) dataset.getRow(i)).getStandard() == 1) {
				for (String experimentName : dataset.getNameExperiments()) {
					vlipid.addElement(new Double((Double) dataset.getRow(i).getPeak(experimentName)));
				}

			}
		}
	}

	private String getLipidName(int row) {
		if (dataset.getRow(row) != null) {
			String olipid = dataset.getRow(row).getName();
			if (olipid.matches(".*unknown.*")) {
				olipid = this.getUnknownName(row);
			}
			return olipid;
		}
		return null;
	}

	public double getNormalizedValue(double value, double stdConcentration, double concentration) {
		try {
			if (stdConcentration == 0) {
				return 0;
			}
			return (value / stdConcentration) * concentration;
		} catch (Exception e) {
			return 0;
		}
	}

	public String getUnknownName(int row) {
		double RT = (Double) ((SimplePeakListRowLCMS) this.dataset.getRow(row)).getRT();
		if (RT < 300) {
			return "LysoGPCho(18:0)";
		}
		if (RT >= 300 && RT < 410) {
			return "GPA(32:0)";
		}
		if (RT >= 410) {
			return "TAG(52:0)";			
		}
		return null;
	}

	public void normalize(TaskStatus status) {
		this.getStandars();
		for (int i = 0; i < dataset.getNumberRows(); i++) {
			int e = 0;
			for (String experimentName : dataset.getNameExperiments()) {
				if (!experimentName.matches(".*peak status.*")) {
					if (status == TaskStatus.CANCELED || status == TaskStatus.ERROR) {
						return;
					}
					try {
						String lipid = this.getLipidName(i);
						if (lipid != null) {
							lipid = lipid.substring(0, lipid.indexOf("("));


							Double valueNormalized = (Double) dataset.getRow(i).getPeak(experimentName);

							switch (this.getStdIndex(lipid)) {
								case 1:
									if (this.stdMol.vTAG.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vTAG.elementAt(e), this.stdMol.TAG);
									break;
								case 2:
									if (this.stdMol.vGPEtn.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vGPEtn.elementAt(e), this.stdMol.GPEtn);
									break;
								case 3:
									if (this.stdMol.vLysoGPCho.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vLysoGPCho.elementAt(e), this.stdMol.LysoGPCho);
									break;
								case 4:
									if (this.stdMol.vCer.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vCer.elementAt(e), this.stdMol.Cer);
									break;
								case 5:
									if (this.stdMol.vGPCho.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vGPCho.elementAt(e), this.stdMol.GPCho);
									break;
								case 6:
									if (this.stdMol.vOtherValue.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vOtherValue.elementAt(e), this.stdMol.otherValue);
									break;
								case 7:
									if (this.stdMol.vOtherValue1.isEmpty()) {
										status = TaskStatus.ERROR;
										return;
									}
									valueNormalized = this.getNormalizedValue(valueNormalized, this.stdMol.vOtherValue1.elementAt(e), this.stdMol.otherValue1);
									break;
								default:
									break;
							}

							dataset.getRow(i).setPeak(experimentName, new Double(valueNormalized));
						}
						e++;
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
			}
			cont++;
		}
	}

	/**
	 * Each lipid has its own standard. This function return a different number for each
	 * standard.
	 * @param lipid
	 * @return
	 */
	private int getStdIndex(String lipid) {
		if (lipid.matches(stdMol.other)) {
			return 6;
		} else if (lipid.matches(stdMol.other1)) {
			return 7;
		} else if (lipid.matches(".*TAG.*") || lipid.matches(".*ChoE.*") || lipid.matches(".*TG.*")) {
			if (stdMol.TAG == 0.0) {
				return 5;
			}
			return 1;
		} else if (lipid.matches("^GPEtn.*") || lipid.matches("^GPSer.*") || lipid.matches("^PS.*") || lipid.matches("^PE.*")) {
			return 2;
		} else if (lipid.matches(".*Lyso.*") || lipid.matches(".*MAG.*") || lipid.matches("^LP.*") || lipid.matches("^MG.*")) {
			return 3;
		} else if (lipid.matches(".*Cer.*")) {
			return 4;
		} else if (lipid.matches("^GP.*") || lipid.matches(".*SM.*") || lipid.matches("^DAG.*") || lipid.matches("^GPA.*") || lipid.matches("^PA.*") || lipid.matches("^DG.*") || lipid.matches("^P.*")) {
			return 5;
		}
		return 0;

	}

	public double getProgress() {
		return (cont / (dataset.getNumberRows()));
	}
}
