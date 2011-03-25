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
package guineu.modules.identification.identificationNeg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class MZmineData {

    public double MZ;
    public double RT;
    public int N_Found;
    public int std;
    public String name;
    public Vector<Double> peaks;
    public List<MZlipidStore> possibleLipids;
    public double ppm = 40;

    public MZmineData() {
        this.possibleLipids = new ArrayList<MZlipidStore>();
        this.peaks = new Vector<Double>();
    }

    public double getTolerance() {
        return (this.MZ * this.ppm) / 1000000;
    }

    public double getmzMin(int index) {
        return this.MZ - this.getTolerance() - this.possibleLipids.get(index).add;
    }

    public double getmzMax(int index) {
        return this.MZ + this.getTolerance() - this.possibleLipids.get(index).add;
    }

    public void sortAbundaneScore() {
        Collections.sort(possibleLipids, new MyLipidComparator());
    }

    public void resetLipid() {
        this.possibleLipids = new ArrayList<MZlipidStore>();
        this.addPossibilities();
    }

    /**
     * Removes repeats lipids.
     */
    public void removeRepeats() {
        for (int i = 0; i < this.possibleLipids.size(); i++) {
            MZlipidStore store1 = this.possibleLipids.get(i);
            for (int e = 0; e < this.possibleLipids.size(); e++) {
                MZlipidStore store2 = this.possibleLipids.get(e);
                if (i != e && store1.commonName.compareTo(store2.commonName) == 0) {
                    if (store1.scoreAbundance >= store2.scoreAbundance) {
                        this.possibleLipids.remove(store1);
                        i--;
                        break;
                    } else {
                        this.possibleLipids.remove(store2);
                        e--;
                    }
                }
            }
        }
    }

    /**
     * 
     */
    public void addPossibilities() {
        try {
            if (this.RT <= 300 && this.MZ <= 550) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Fatty Acyls";
                dates.CExpected = "FA";
                dates.add = -1.007825;
                dates.adduct = "[M+H]";
                this.possibleLipids.add(dates);
            }
            if (this.RT <= 300 && this.MZ <= 650) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Glycerophospholipi*";
                dates.CExpected = "LPC/LPE/LPA/LSer";
                dates.add = -1.007825;
                dates.adduct = "[M+H]";
                this.possibleLipids.add(dates);
            }
            if (this.RT <= 300 && this.MZ <= 500) {
            /*MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "Glycerolipids";
            dates.CExpected = "MAG";
            dates.add = -17.0027;                 
            dates.adduct = "[(M+H)-18]"; 
            this.possibleLipids.add(dates);*/
            }
            if (this.RT <= 421 && this.RT >= 300 && this.MZ >= 550) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Glycerophospholipi*";
                dates.CExpected = "GPCho/GPEtn/GPIns/GPSer";
                dates.add = -1.007825;
                dates.adduct = "[M+H]";
                this.possibleLipids.add(dates);
            }
            if (this.RT <= 430 && this.MZ >= 340) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Sphingolipids";
                dates.CExpected = "Cer";
                dates.add = -19.01839;
                dates.adduct = "[(M+H)-18]";
                this.possibleLipids.add(dates);
            }
            if (this.RT <= 420 && this.RT >= 330) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Sphingolipids";
                dates.CExpected = "SM";
                dates.add = -1.007825;
                dates.adduct = "[M+H]";
                this.possibleLipids.add(dates);
            }
            if (this.RT <= 410 && this.MZ >= 550) {
            /*MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "Glycerophospholipi*";
            dates.CExpected = "GPA";
            dates.add = -96.9691;                 
            dates.adduct = "[M-97]";
            this.possibleLipids.add(dates);*/
            }
            if (this.RT <= 410 && this.MZ >= 550) {
            /*MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "Glycerophospholipi*";
            dates.CExpected = "GPGro";
            dates.add = -171.0059;                
            dates.adduct = "[M-171]";
            this.possibleLipids.add(dates);*/
            }
            if (this.RT <= 410 && this.MZ >= 350) {
            /*MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "Glycerolipids";
            dates.CExpected = "DAG";
            dates.add = 18.0344;               
            dates.adduct = "[M+18]";
            this.possibleLipids.add(dates);*/
            }
            if (this.RT >= 410) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Glycerolipids";
                dates.CExpected = "TAG";
                dates.add = 34.969402;
                dates.adduct = "[M+18]";
                this.possibleLipids.add(dates);
                dates = new MZlipidStore();
                dates.lipidClass = "Glycerolipids";
                dates.CExpected = "TAG";
                dates.add = -1.007825;
                dates.adduct = "[M+18]";
                this.possibleLipids.add(dates);
                dates = new MZlipidStore();
                dates.lipidClass = "Glycerolipids";
                dates.CExpected = "TAG";
                dates.add = 36.948606;
                dates.adduct = "[M+18]";
                this.possibleLipids.add(dates);
            }
            if (this.RT >= 350 && this.MZ >= 550) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Sterollipids";
                dates.CExpected = "ChoE";
                dates.add = 34.969402;
                dates.adduct = "[M+18]";
                this.possibleLipids.add(dates);
                dates = new MZlipidStore();
                dates.lipidClass = "Sterollipids";
                dates.CExpected = "ChoE";
                dates.add = -1.007825;
                dates.adduct = "[M+18]";
                this.possibleLipids.add(dates);
                dates = new MZlipidStore();
                dates.lipidClass = "Sterollipids";
                dates.CExpected = "ChoE";
                dates.add = 36.948606;
                dates.adduct = "[M+18]";
                this.possibleLipids.add(dates);
            }
            if (this.RT >= 410 && this.MZ >= 1000) {
            /*MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "Glycerophospholipi*";
            dates.CExpected = "CL";
            dates.add = 3*22.98977-2*1.007825;           
            dates.adduct = "[M-2H+3Na]+";
            this.possibleLipids.add(dates);*/
            }
            if (this.RT >= 410 && this.MZ >= 1000) {
            /*MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "Glycerophospholipi*";
            dates.CExpected = "CL";
            dates.add = 2*22.98977-1.007825;            
            dates.adduct = "[M-H+2Na]+";
            this.possibleLipids.add(dates);*/
            }
            if (this.RT >= 410 && this.MZ >= 1000) {
                MZlipidStore dates = new MZlipidStore();
                dates.lipidClass = "Glycerophospholipi*";
                dates.CExpected = "CL";
                dates.add = 20.974666;
                dates.adduct = "[M+Na]+";
                this.possibleLipids.add(dates);
            }
            MZlipidStore dates = new MZlipidStore();
            dates.lipidClass = "(.*)";
            dates.CExpected = "";
            dates.add = -1.007825;
            dates.adduct = "";
            this.possibleLipids.add(dates);


        } catch (Exception exception) {
            System.out.println("Identification.java-> setAllData() " + exception);
        }
    }

    public void setCommonName(MZlipidStore lipid, String Name) {
        lipid.commonName = "unknown";
        try {
            if (lipid.CExpected.compareTo("FA") == 0) {
                lipid.setCommonName(Name);
            } else if (lipid.CExpected.compareTo("LPC/LPE/LPA/LSer") == 0) {
                if (Name.matches(".*GP.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("GPCho/GPEtn/GPIns/GPSer") == 0) {
                if (Name.matches(".*GP.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("TAG") == 0) {
                if (Name.matches(".*TAG.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("DAG") == 0) {
                if (Name.matches(".*DAG.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("MAG") == 0) {
                if (Name.matches(".*MAG.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("ChoE") == 0) {
                if (Name.matches(".*ChoE.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("CL") == 0) {
                if (Name.matches(".*CL.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("Cer") == 0) {
                if (Name.matches(".*Cer.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("SM") == 0) {
                if (Name.matches(".*SM.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("GPA") == 0) {
                if (Name.matches(".*GPA.*")) {
                    lipid.setCommonName(Name);
                }
            } else if (lipid.CExpected.compareTo("GPGro") == 0) {
                if (Name.matches(".*GPGro.*")) {
                    lipid.setCommonName(Name);
                }
            }
        } catch (Exception e) {
            System.out.println("MZmineData.java--> setCompleteName()" + e);
        }
    }

    /**
     * Sets the complete name of the lipid 
     * @param lipid
     * @param Name
     */
    public void setCompleteName(MZlipidStore lipid, String Name) {
        lipid.completeName = "unknown";

        try {

            if (lipid.CExpected.compareTo("FA") == 0) {
                lipid.completeName = Name;
            } else if (lipid.CExpected.compareTo("LPC/LPE/LPA/LSer") == 0) {
                if (Name.matches(".*phosphocholine.*") &&
                        !Name.matches("^1-.*-2-.*phosphocholine.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches(".*phosphoserine.*") &&
                        !Name.matches("^1-.*-2-.*phosphoserine.*") &&
                        !Name.matches(".*-O-.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches(".*phospho-.*-myo-inositol.*") &&
                        !Name.matches("^1-.*-2-.*phospho-.*-myo-inositol.*") &&
                        !Name.matches(".*-O-.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches(".*phosphoethanolamine.*") &&
                        !Name.matches("^1-.*-2-.*phosphoethanolamine.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches(".*phosphate.*") &&
                        !Name.matches("^1-.*-2-.*phosphate.*") &&
                        !Name.matches(".*-O-.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches(".*phospho-.*-myo-inositol.*") &&
                        !Name.matches("^1-.*-2-.*phospho-.*-myo-inositol.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("GPCho/GPEtn/GPIns/GPSer") == 0) {
                if (Name.matches("^1-.*-2-.*phosphocholine.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches("^1-.*-2-.*phosphoserine.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches("^1-.*-2-.*phospho-.*-myo-inositol.*")) {
                    lipid.completeName = Name;
                }
                if (Name.matches("^1-.*-2-.*phosphoethanolamine.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("TAG") == 0) {
                if (Name.matches("^1-.*-2-.*-3-.*-sn.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("DAG") == 0) {
                if (Name.matches("^1-.*-2.*-sn-glycerol.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("MAG") == 0) {
                if (Name.matches("^1-.*-sn-glycerol.*") || lipid.completeName.matches("^2-.*-sn-glycerol.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("ChoE") == 0) {
                if (Name.matches(".*cholest-5-en-3beta-yl-.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("CL") == 0) {
                if (Name.matches(".*1(.*)2(.*)-sn-glycero-3-phospho.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("Cer") == 0) {
                if (Name.matches("^N-.*-octadecasphing.*") &&
                        !Name.matches("^N-.*phosp.*") &&
                        !Name.matches("^N-.*glucosyl.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("SM") == 0) {
                if ((!Name.matches(".*hydroxy.*")) && (Name.matches("^N-.*octadecasphing-4-enine-1-phosphocholine.*") || Name.matches("^N-.*octadecasphinganine-1-phosphocholine.*"))) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("GPA") == 0) {
                if (Name.matches(".*phosphate.*")) {
                    lipid.completeName = Name;
                }
            } else if (lipid.CExpected.compareTo("GPGro") == 0) {
                if (Name.matches(".*phospho-.*-sn-glycerol.*")) {
                    lipid.completeName = Name;
                }
            }
        } catch (Exception e) {
            System.out.println("MZmineData.java--> setCompleteName()" + e);
        }

    }

    class MyLipidComparator implements Comparator<Object> {

        public MyLipidComparator() {

        }

        public int compare(Object o1, Object o2) {
            MZlipidStore s1 = null;
            MZlipidStore s2 = null;
            int d1 = 0;
            int d2 = 0;

            try {
                s1 = (MZlipidStore) o1;
                s2 = (MZlipidStore) o2;
                d1 = s1.scoreAbundance;
                d2 = s2.scoreAbundance;
            } catch (Exception ee) {
                return 1000;
            }
            try {
                //sort by abundance score
                if (s1.scoreAbundance == 0) {
                    return -1;
                } else if (s2.scoreAbundance == 0) {
                    return 1;
                }
                double result = Double.compare((double) d1, (double) d2) * 2;

                //sort by MZ
                double massDiference1 = Math.abs(MZ - (Double.valueOf(s1.mass[0]) + s1.add));
                double massDiference2 = Math.abs(MZ - (Double.valueOf(s2.mass[0]) + s2.add));
                result += Double.compare(massDiference1, massDiference2);
                if (result == 0) {
                    Pattern pat = Pattern.compile("\\d\\d?");
                    Matcher match1 = pat.matcher(s1.commonName);
                    Matcher match2 = pat.matcher(s2.commonName);
                    if (match1.find() && match2.find()) {
                        double boundsDiference1 = Double.valueOf(s1.commonName.substring(match1.start(), match1.end())) * -1;
                        double boundsDiference2 = Double.valueOf(s2.commonName.substring(match2.start(), match2.end())) * -1;
                        result = Double.compare(boundsDiference1, boundsDiference2);
                    }
                }

                return (int) result;

            } catch (Exception ee) {
                return 1000;
            }
        }
    }
}


