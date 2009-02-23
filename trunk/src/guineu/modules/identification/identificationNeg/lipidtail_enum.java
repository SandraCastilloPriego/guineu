/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.identification.identificationNeg;

public enum lipidtail_enum {

    TAG("sn-glycerol"),
    MAG("sn-glycerol"),
    DAG("sn-glycerol"),
    ChoE("cholest-5-en-3beta-yl"),
    GPA("sn-glycero-3-phosphate"),
    LysoGPA("sn-glycero-3-phosphate"),
    GPCho("sn-glycero-3-phosphocholine"),
    LysoGPCho("sn-glycero-3-phosphocholine"),
    GPEtn("sn-glycero-3-phosphoethanolamine"),
    LysoGPEtn("sn-glycero-3-phosphoethanolamine"),
    GPGro("sn-glycero-3-phospho-(1'-sn-glycerol)"),
    LysoGPGro("sn-glycero-3-phospho-(1'-sn-glycerol)"),
    GPIns("sn-glycero-3-phospho-(1'-myo-inositol)"),
    LysoGPIns("sn-glycero-3-phospho-(1'-myo-inositol)"),
    GPSer("sn-glycero-3-phosphoserine"),
    LysoGPSer("sn-glycero-3-phosphoserine"),
    Cer("hexadecasphinganine"),
    Cer1("octadecasphing-4-enine"),
    Cer2("octadecasphinganine"),
    SM("octadecasphinganine-1-phosphocholine"),
    SM1("octadecasphing-4-enine-1-phosphocholine");
    private final String lipid_tail;

    lipidtail_enum(String lipid_tail) {
        this.lipid_tail = lipid_tail;
    }

    public String lipid_tail() {
        return lipid_tail;
    }
}
