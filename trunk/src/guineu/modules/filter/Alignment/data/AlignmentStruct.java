/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
 */

package guineu.modules.filter.Alignment.data;

import java.util.Comparator;
import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public class AlignmentStruct {
    public double Score;
    public int ID1;
    public int ID2;
    public Vector<AlignStructMol> v;
    public AlignmentStruct(){};
    public AlignmentStruct(Vector<AlignStructMol> v){
        this.v = v;
    }
    public void addLipids(AlignStructMol struct){
        this.v.addElement(struct);
    }
}
class chromComparator implements Comparator<AlignmentStruct> {
    public chromComparator(){}
    public int compare(AlignmentStruct o1, AlignmentStruct o2) {        
        return Double.compare(o1.Score, o2.Score);
    }
}