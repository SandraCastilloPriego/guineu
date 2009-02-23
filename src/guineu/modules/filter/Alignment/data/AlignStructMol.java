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
package guineu.modules.filter.Alignment.data;


import guineu.data.PeakListRow;
import java.util.Comparator;


/**
 *
 * @author SCSANDRA
 */
public class AlignStructMol implements Cloneable {
    public int IDDataset1;
    public int IDDataset2;
    public double picksScore;   
    public PeakListRow lipid1, lipid2;    
    public boolean STD;   
    public boolean Aligned;    
    public boolean ransacMaybeInLiers;
    public boolean ransacAlsoInLiers;
    public double NewY;
    public double score;
    
    public AlignStructMol(){}
    
    public AlignStructMol(int IDDataset1, int IDDataset2, PeakListRow lipid1, PeakListRow lipid2, boolean STD, boolean isAligned,double picks1, double picks2){
        this.lipid1 = lipid1;
        this.lipid2 = lipid2;
        this.IDDataset1 = IDDataset1;
        this.IDDataset2 = IDDataset2;
        this.Aligned = isAligned;
        this.STD = STD;    
        this.picksScore = Math.abs(Math.log10(picks1) - Math.log10(picks2));       
    }
    
    public AlignStructMol(int IDDataset1, int IDDataset2, PeakListRow lipid1, PeakListRow lipid2, boolean STD, boolean isAligned, double picks){
        this.IDDataset1 = IDDataset1;
        this.IDDataset2 = IDDataset2;
        this.lipid1 = lipid1;
        this.lipid2 = lipid2;
        this.Aligned = isAligned;
        this.STD = STD;    
        this.picksScore = picks;       
    }
    
    public void setAligned(boolean Aligned){
        this.Aligned = Aligned;
    }
    
    public void reset(){
        this.Aligned = false;
        this.ransacAlsoInLiers = false;
        this.ransacMaybeInLiers = false;
    }
    
    @Override
    public AlignStructMol clone(){        
        return new AlignStructMol(this.IDDataset1, this.IDDataset2, lipid1, lipid2, this.STD, this.Aligned, this.picksScore);
    }
}


class AlignComparator implements Comparator<AlignStructMol> {
    public AlignComparator(){}
    public int compare(AlignStructMol o1, AlignStructMol o2) {        
        return Double.compare(o1.score, o2.score);
    }
}

