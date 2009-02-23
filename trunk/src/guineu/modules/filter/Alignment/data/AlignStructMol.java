/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
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

