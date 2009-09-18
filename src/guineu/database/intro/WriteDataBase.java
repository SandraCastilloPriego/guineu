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
package guineu.database.intro;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.DatasetType;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.database.ask.DBask;
import guineu.modules.mylly.datastruct.Spectrum;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SCSANDRA
 */
public class WriteDataBase {

	/**
	 *  Intro table EXPERIMENTS
	 * @param conn
	 * @param lmcs_known
	 * @param excel_id
	 */
	public void tableEXPERIMENT(Connection conn, Dataset dataset, int DatasetId) {
		try {
			Statement statement = conn.createStatement();
			ResultSet r = null;
			for (String sampleName : dataset.getNameExperiments()) {
				String sampleNameExp;
				try {
					sampleNameExp = sampleName.substring(0, sampleName.indexOf(" "));
				} catch (Exception e) {
					sampleNameExp = sampleName;
				}
				if (sampleName != null) {
					r = statement.executeQuery("SELECT * FROM EXPERIMENT " + "WHERE NAME = '" + sampleNameExp + "'");
					if (r.next()) {
						statement.executeUpdate("INSERT INTO DATASET_COLUMNS (NAME, EXPERIMENT_ID ,DATASET_ID) VALUES ('" + sampleName + "', '" + r.getInt(1) + "', '" + DatasetId + "')");
					} else {
						statement.executeUpdate("INSERT INTO DATASET_COLUMNS (NAME,DATASET_ID) VALUES ('" + sampleName + "', '" + DatasetId + "')");
					}
				}
			}
			r.close();
			statement.close();

		} catch (SQLException ex) {
			Logger.getLogger(InOracle.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Fills the Table DATASET
	 * @param conn Connection
	 * @param excel_name The name of the excel file where the experiments are.
	 * @param type (LSMS or GCGCTof)
	 * @param author (Author of the data)
	 * @return the ID of the data in the database.
	 */
	public int tableDATASET(Connection conn, String excel_name, String type, String author, String parameters, String study) {
		{
			try {
				int exp_id = 0;
				if (excel_name != null) {
					Statement statement = conn.createStatement();
					try {
						String dir = "";
						String file = "";
						try {
							int line = parameters.lastIndexOf("\\");
							dir = parameters;
							file = parameters;
							if (line > 0) {
								dir = parameters.substring(0, line);
								file = parameters.substring(line + 1);
							}
						} catch (Exception exception) {
						}
						statement.executeUpdate("INSERT INTO DATASET (EXCEL_NAME,D_TYPE,AUTHOR,D_DATE,UNITS,PARAMETERS, STUDY) VALUES ('" + excel_name + "', '" + type + "', '" + author + "', to_date(sysdate,'dd/MM/yyyy'),'µl', bfilename('" + dir + "', '" + file + "'), '" + DBask.getStudyID(study, conn) + "')");
					} catch (SQLException sqlexception) {
						sqlexception.printStackTrace();
					}
					ResultSet r = statement.executeQuery("SELECT * FROM DATASET WHERE EXCEL_NAME = '" + excel_name + "' ORDER BY DATASETID desc");
					if (r.next()) {
						exp_id = r.getInt(8);
					}
					statement.close();

					return exp_id;
				}
				return -1;
			} catch (Exception exception) {
				System.out.println("ERROR : " + exception);
				exception.printStackTrace();
				return -1;
			}
		}
	}

	/**
	 * Intro dates into the table MOL_LCMS
	 * @param conn
	 * @param lmcs_known
	 * @param stmt
	 * @param st
	 * @param excel_id
	 * @return
	 */
	public int[] tableMOL_LCMS(Connection conn, SimpleLCMSDataset lcms_known, int excel_id) {

		try {
			int[] mol_ID = new int[lcms_known.getNumberRows()];
			Statement statement = conn.createStatement();
			for (int i = 0; i < lcms_known.getNumberRows(); i++) {
				PeakListRow lipid = lcms_known.getRow(i);
				try {
					statement.executeUpdate("INSERT INTO MOL_LCMS (AVERAGE_MZ," +
							"AVERAGE_RT,LIPID_NAME,LIPID_CLASS,N_FOUND,STD,EPID, " +
							"FA_COMPOSITION,PUBCHEM_ID, VTTID, VTTALLIDS,ALL_NAMES)" +
							" VALUES ( '" + Double.valueOf(lipid.getMZ()).floatValue() +
							"', '" + Double.valueOf(lipid.getRT()).floatValue() +
							"', '" + lipid.getName() +
							"', '" + Integer.valueOf(lipid.getMolClass()) +
							"', '" + (int) lipid.getNumFound() +
							"', '" + lipid.getStandard() +
							"', '" + excel_id +
							"', '" + lipid.getFAComposition() +
							"', '" + lipid.getPubChemID() +
							"', '" + lipid.getVTTID() +
							"', '" + lipid.getAllVTTID() +
							"', '" + lipid.getAllNames() + "') ");
					ResultSet r = statement.executeQuery("SELECT * FROM MOL_LCMS ORDER BY ID desc");

					if (r.next()) {
						mol_ID[i] = r.getInt(1);
					}
				} catch (SQLException se) {
					System.out.println("We got an exception while preparing a statement:" + "Probably bad SQL.");
					se.printStackTrace();
				}

			}
			statement.close();
			return mol_ID;
		} catch (SQLException ex) {

			ex.printStackTrace();
			Logger.getLogger(InOracle.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	/**
	 * Intro dates into the table MEASUREMENT
	 * @param conn
	 * @param lmcs_known
	 * @param stmt
	 * @param st
	 * @param mol_ID
	 */
	public void tableMEASUREMENT(Connection conn, Dataset Molecules, int[] mol_ID, int excel_id) {
		Statement statement = null;
		try {
			statement = conn.createStatement();
			for (int i = 0; i < Molecules.getNumberRows(); i++) {
				PeakListRow row = Molecules.getRow(i);
				for (String experimentName : Molecules.getNameExperiments()) {
					Double peak = (Double) row.getPeak(experimentName);
					//ID_sample
					ResultSet r = statement.executeQuery("SELECT COLUMN_ID FROM DATASET_COLUMNS WHERE NAME = '" + experimentName + "'");
					int ID_sample = 0;
					if (r.next()) {
						ID_sample = r.getInt(1);
						statement.close();
					} else {
						statement.close();
						break;
					}
					statement = conn.createStatement();
					if (Molecules.getType() == DatasetType.LCMS) {
						statement.executeUpdate("INSERT INTO MEASUREMENT (DATASET_CID," +
								"MOL_LCMS_ID,CONCENTRATION, DATASETID) VALUES ('" + ID_sample +
								"', '" + mol_ID[i] +
								"', '" + (float) ((Double) peak).floatValue() +
								"', '" + excel_id + "') ");
					} else if (Molecules.getType() == DatasetType.GCGCTOF) {
						statement.executeUpdate("INSERT INTO MEASUREMENT (DATASET_CID,MOL_GCGCTOF_ID,CONCENTRATION, DATASETID) VALUES " +
								"('" + ID_sample + "', '" + mol_ID[i] +
								"', '" + (float) ((Double) peak).floatValue() +
								"', '" + excel_id + "') ");
					}
				}
			}
			statement.close();
		} catch (SQLException ex) {
			try {
				statement.close();
				Logger.getLogger(InOracle.class.getName()).log(Level.SEVERE, null, ex);
			} catch (SQLException ex1) {
				Logger.getLogger(InOracle.class.getName()).log(Level.SEVERE, null, ex1);
			}
		}
	}

	public int[] tableMOL_GCGCTof(Connection conn, SimpleGCGCDataset dataset, int exp_id) {
		//intro table MOL_GCGCTOF
		Statement st = null;
		int[] mol_ID = new int[dataset.getNumberRows() + 1];
		for (int i = 0; i < dataset.getNumberRows(); i++) {
			PeakListRow metabolite = dataset.getRow(i);
			try {
				int result = 0;
				st = conn.createStatement();
				st.executeUpdate("INSERT INTO MOL_GCGCTOF (RT1, RT2, RTI, " +
						"N_FOUND, MAX_SIMILARITY, MEAN_SIMILARITY, SIMILARITY_STD_DEV, " +
						"METABOLITE_NAME, PUBCHEM_ID, METABOLITE_ALLNAMES, " +
						"EPID, MASS, DIFFERENCE, SPECTRUM, CAS, CLASS) VALUES ('" + (float) metabolite.getRT1() +
						"', '" + (float) metabolite.getRT2() +
						"', '" + (float) metabolite.getRTI() +
						"', '" + metabolite.getNumFound() +
						"', '" + metabolite.getMaxSimilarity() +
						"', '" + (float) metabolite.getMeanSimilarity() +
						"', '" + (float) metabolite.getSimilaritySTDDev() +
						"', '" + metabolite.getName() +
						"', '" + (int) result +
						"', '" + metabolite.getAllNames() +
						"', '" + (int) exp_id +
						"', '" + (float) metabolite.getMass() +
						"', '" + (float) metabolite.getDifference() +
						"', '" + metabolite.getSpectrumString() +
						"', '" + metabolite.getCAS() +
						"', '" + metabolite.getMolClass() + "') ");
				ResultSet r = st.executeQuery("SELECT * FROM MOL_GCGCTOF ORDER BY ID desc");
				r.next();
				mol_ID[i] = r.getInt(1);
				r.close();
				st.close();
			} catch (SQLException se) {
				System.out.println("We got an exception while preparing a statement:" +
						"Probably bad SQL.");
				se.printStackTrace();
			}
		}
		return mol_ID;
	}

	public void tableSPECTRUM(Connection conn, SimpleGCGCDataset mol, Statement st, int[] mol_ID) {
		try {
			st = conn.createStatement();
			for (int i = 0; i < mol.getNumberRows(); i++) {
				PeakListRow peak = mol.getRow(i);
				Spectrum spectrum = peak.getSpectrum();

				if (spectrum == null) {
					break;
				}
				for (int e = 0; e < spectrum.length(); e++) {
					try {
						if (e == 0) {
							ResultSet r = st.executeQuery("SELECT * FROM SPECTRUMS WHERE MOL_ID = '" + mol_ID[i] + "'");
							if (r.next()) {
								break;
							}
							r.close();
						}
						st.executeUpdate("INSERT INTO SPECTRUMS (MOL_ID, MASS, INTENSITY) VALUES ( '" + (float) mol_ID[i] + "', '" + (float) spectrum.getPeakList().get(e).getFirst() + "', '" + (float) spectrum.getPeakList().get(e).getSecond() + "') ");
					} catch (SQLException se) {
						System.out.println("We got an exception while preparing a statement:" + "Probably bad SQL.");
						se.printStackTrace();
					}
				}
			}
		} catch (SQLException ex) {
			Logger.getLogger(InOracle.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Connect with Pubchem database
	 * Argument search is the name of the metabolite
	 * Return int = 2334333 (Pubchem ID)
	 */
	public static int pubchem_connect(String search) {
		//System.out.println(search);
		try {
			System.setProperty("http.proxyHost", "rohto.vtt.fi");
			System.setProperty("http.proxyPort", "8000");

			String page = "http://www.ncbi.nlm.nih.gov/sites/entrez?db=pcsubstance&term=" + URLEncoder.encode("\"" + search + "\"", "UTF-8");

			// Connect
			URL urlObject = new URL(page);
			URLConnection con = urlObject.openConnection();


			// Get the response
			BufferedReader webData = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line;
			int mark = 0;
			int result = 0;


			while ((line = webData.readLine()) != null) {
				if (line.indexOf("- - - - - - - - begin Results - - - - - -") > -1) {
					mark = 1;
				}
				if (mark == 1) {
					if (line.indexOf("var Menu") > -1) {
						int i = line.indexOf("var Menu") + 8;
						if (i != -1) {
							int j = line.indexOf("_");
							line = line.substring(i, j);
							result = Integer.parseInt(line.trim());
							webData.close();
							webData.close();
							return result;
						} else {
							webData.close();
							webData.close();
							return 0;
						}
					}
				}
			}

			return 0;

		} catch (Exception e) {
			System.out.println("Error: " + e.getMessage());
			return 0;
		}


	}
}
