/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.identification.identificationNeg;

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


