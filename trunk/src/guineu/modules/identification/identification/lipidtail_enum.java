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
package guineu.modules.identification.identification;

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
