/*
 * Copyright 2007-2010 VTT Biotechnology
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class lipidomicIdentification {

    private Hashtable<String, String> commonNames_conversion;

    public lipidomicIdentification() {
        this.commonNames_conversion = new Hashtable<String, String>();
    }

    /**
     * The hash table "commonNames_conversion" gets the name for every configuration (for example: "20a:0")
     * from "name-conv.txt" file.
     * Example:
     * 		names_conversion.get("-(11-eicosenoyl)") = 20a:1
     */
    public void Introhash_ToCommonNamesConversion() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("conf\\names-conv.txt"));
            String line = reader.readLine();
            while (line != null) {
                this.commonNames_conversion.put(line.substring(line.indexOf("::") + 2), line.substring(0, line.indexOf("::")));
                line = reader.readLine();
            }
            reader.close();

        } catch (Exception se) {
            System.out.println(se);
        }
    }

    /**
     * Transforms the real name of the lipid to common name.
     * @param longName it is the complete name of the lipid
     * @return common name
     */
    public String getCommonName(String longName, MZlipidStore lipidStore) {
        if (longName.compareTo("unknown") == 0) {
            return longName;
        }

        //Create ChoE common name.
        String ChoE = this.ChoE(longName);
        if (ChoE != null) {
            return ChoE;
        }

        return this.OtherLipids(longName, lipidStore);
    }

    /**
     * Creates common name for all lipids.
     * @param longName It is the complete name of the lipid
     * @param lipidStore 
     * @return common name of the lipids.
     */
    public String OtherLipids(String longName, MZlipidStore lipidStore) {

        Vector<Lpattern> subvt = this.FindPattern(longName, "-?O?-?\\(?[\\d?\\d,?]*?-?[\\w]{4,}\\)?-");

        //head        
        String head = longName.substring(subvt.lastElement().end);
        head = this.getHeadName(head) + "(";
        head = this.Cer_SM(head, longName);
        if (head == null || head.matches(".*pyrophosphate.*")) {
            return "unknown";
        }

        //carbons
        Vector<String> numbers = new Vector<String>();
        for (int i = 0; i < subvt.size(); i++) {
            String str = this.fixString(subvt.elementAt(i).str);
            if (this.commonNames_conversion.containsKey(str)) {
                numbers.addElement(this.commonNames_conversion.get(str));
            }
        }
        String number = this.getNumberCommonName(numbers, lipidStore);
        //System.out.println("numbers " +number);
        return this.discartInvalidLipids(head, number, lipidStore);
    }

    /**
     * Creates common name for ChoE lipids.
     * @param longName It is the complete name of the lipid
     * @return common name of ChoE lipids.
     */
    public String ChoE(String longName) {
        if (longName.matches("^cholest-5-en-3beta-yl.*")) {
            String number = this.fixString(longName.substring(longName.indexOf("-yl") + 3));
            if (number.indexOf("ate") > -1) {
                number = number.replace("ate", "yl");
            }
            if (this.commonNames_conversion.containsKey(number)) {
                number = this.commonNames_conversion.get(number).replace("a", "");
                return "ChoE(" + number + ")";
            }
        }
        return null;
    }

    /**
     * Creates the head for Cer and SM
     * @param head 
     * @param longName
     * @return
     */
    public String Cer_SM(String head, String longName) {
        if (head.matches("Cer.*") || head.matches("SM.*")) {
            if (longName.matches("N.*-octadecasphing-4-enine.*")) {
                head = head + "d18:1/";
            } else if (longName.matches("N.*-octadecasphinganine.*")) {
                head = head + "d18:0/";
            } else {
                return null;
            }
        }
        return head;
    }

    /**
     * Discarts invalid lipids like GPCho(12:0) or TAG(15:0)
     * @param head
     * @param number
     * @param lipidStore
     * @return the lipid name or "unknown"
     */
    public String discartInvalidLipids(String head, String number, MZlipidStore lipidStore) {
        if (number.matches("^0:0.*")) {
            return "unknown";
        }

        if (lipidStore.CExpected.matches("GPCho/GPEtn/GPIns/GPSer")) {
            Pattern carbons = Pattern.compile("\\d\\d?");
            Matcher matcher = carbons.matcher(number);
            if (matcher.find()) {
                double num = Double.valueOf(number.substring(matcher.start(), matcher.end()));
                if (num < 24) {
                    return "unknown";
                }
            }
        }

        if (lipidStore.CExpected.matches("TAG")) {
            Pattern carbons = Pattern.compile("\\d\\d?");
            Matcher matcher = carbons.matcher(number);
            if (matcher.find()) {
                double num = Double.valueOf(number.substring(matcher.start(), matcher.end()));
                if (num < 36) {
                    return "unknown";
                }
            }
        }
        return head + number;
    }

    /**
     * 
     * @param str
     * @return
     */
    public String fixString(String str) {
        if (!str.matches("^-.*")) {
            str = "-" + str;
        }
        if (str.matches("^-1.*") || str.matches("^-2.*") || str.matches("^-3.*")) {
            str = str.substring(2);
        }
        if (str.lastIndexOf("-") == str.length() - 1) {
            str = str.substring(0, str.lastIndexOf("-"));
        }
        str = str.trim();
        if (str.matches("^O-")) {
            str = "-" + str;
        }
        return str;
    }

    /**
     * To get the number of carbons and double bounds of the lipid chain.
     * @param numbers
     * @return
     */
    public String getNumberCommonName(Vector<String> numbers, MZlipidStore lipidStore) {

        int n1 = 0;
        int n2 = 0;
        String mark = "";
        for (int i = 0; i < numbers.size(); i++) {
            String strNumbers = numbers.elementAt(i);
            Pattern carbons = Pattern.compile("\\d\\d?");
            Matcher matcher = carbons.matcher(strNumbers);
            if (matcher.find()) {
                n1 = n1 + Integer.valueOf(strNumbers.substring(matcher.start(), matcher.end()));
            }
            if (matcher.find()) {
                n2 = n2 + Integer.valueOf(strNumbers.substring(matcher.start(), matcher.end()));
            }
            if (strNumbers.indexOf("e") > -1) {
                mark = "e";
            }
        }
        return String.valueOf(n1) + ":" + String.valueOf(n2) + mark + ")";
    }

    /**
     * Finds the head name for the common name of the lipid
     * @param head 
     * @return the head name.
     */
    public String getHeadName(String head) {
        if (head.indexOf("(1&apos;-sn-glycerol)") > -1) {
            return "GPGro";
        } else if (head.indexOf("(1&apos;-myo-inositol)") > -1) {
            return "GPIns";
        }
        for (lipidtail_enum p : lipidtail_enum.values()) {
            if (p.lipid_tail().indexOf(head) > -1) {
                head = p.toString();
                if (p.toString().compareTo("Cer1") == 0 || p.toString().compareTo("Cer2") == 0) {
                    head = "Cer";
                } else if (p.toString().compareTo("SM1") == 0) {
                    head = "SM";
                }
            }
        }
        return head;
    }

    /**
     * Find the "pattern" in the "str" and return a vector with all matchs
     */
    public Vector<Lpattern> FindPattern(String str, String pattern) {
        Pattern pat = Pattern.compile(pattern);
        Matcher match = pat.matcher(str);

        Vector<Lpattern> vector = new Vector<Lpattern>();
        while (match.find()) {
            Lpattern inpat = new Lpattern();
            inpat.str = str.substring(match.start(), match.end());
            inpat.start = match.start();
            inpat.end = match.end();
            vector.add(inpat);
        }
        return vector;
    }
    /*public String setSodiated(String commonName, MZlipidStore lipidStore){
    System.out.println(commonName + " - " + lipidStore.adduct);
    //sodiated
    if(lipidStore.adduct.indexOf("Na") > -1){
    int n1 = 0;
    int n2 = 0;
    Pattern carbons = Pattern.compile("\\d\\d?");
    Matcher matcher = carbons.matcher(commonName);  
    String head ="";
    String tail = "";
    if(matcher.find())
    {		  
    n1 = n1 + Integer.valueOf(commonName.substring(matcher.start(), matcher.end()));
    head = commonName.substring(0,matcher.start());
    } 
    if(matcher.find())
    {		  
    n2 = n2 + Integer.valueOf(commonName.substring(matcher.start(), matcher.end()));  
    tail = commonName.substring(matcher.end());
    } 
    n1 -=2;
    n2 -=3;
    return head + String.valueOf(n1) + ":" + String.valueOf(n2)+tail+")(sodiated)" ;
    }else{
    return commonName;
    }
    }*/
}
