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
package guineu.modules.identification.identification;

/**
 *
 * @author scsandra
 */
public class MZlipidStore implements Cloneable {

    public double add;
    public String adduct;
    public String lipidClass;
    public String CExpected;
    public String commonName;
    //public String molecularFormula;
    public String completeName;
    public int scoreAbundance;
    // public double molecularWeight = 0;
    // public String SMILES;
    public String[] mass;

    /**
     * Sets the common name of the lipid.
     * @param Name common name of the lipid.
     */
    public void setCommonName(String Name) {
        try {
            if (Name.indexOf("TAG") > -1) {
                if (this.CExpected.matches("DAG")) {
                    Name = Name.replace("TAG", "DAG");
                } else if (this.CExpected.matches("MAG")) {
                    Name = Name.replace("TAG", "MAG");
                }
            }

            if (this.CExpected.matches("LPC/LPE/LPA/LSer") && !Name.matches(".*unknown.*") && !Name.matches(".*Lyso.*")) {
                Name = "Lyso" + Name;
            }

            if (Name.matches(".*GPEth.*")) {
                Name = Name.replace("GPEth", "GPEtn");
            }

            if (Name.matches(".*LysoGPEnt(22:3).*")) {
                Name = "PAF(sodiated)";
            }
            this.commonName = Name;
        } catch (Exception e) {
            System.out.println("MZlipidStore.java --> setCommonName() " + e);
        }
    }

    @Override
    public MZlipidStore clone() {
        MZlipidStore store = new MZlipidStore();
        store.CExpected = this.CExpected;
        store.add = this.add;
        store.adduct = this.adduct;
        return store;
    }
}


