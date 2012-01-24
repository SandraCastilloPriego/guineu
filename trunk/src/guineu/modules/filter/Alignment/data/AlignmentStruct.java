/*
 * Copyright 2007-2012 VTT Biotechnology
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