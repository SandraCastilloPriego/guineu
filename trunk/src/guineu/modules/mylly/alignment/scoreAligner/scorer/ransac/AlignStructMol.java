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
package guineu.modules.mylly.alignment.scoreAligner.scorer.ransac;

import guineu.data.PeakListRow;
import java.util.Comparator;

public class AlignStructMol implements Comparator {

    public PeakListRow row1,  row2;
    public double RT,  RT2;
    public boolean Aligned = false;
    public boolean ransacMaybeInLiers;
    public boolean ransacAlsoInLiers;

    public AlignStructMol(PeakListRow row1, PeakListRow row2) {
        this.row1 = row1;
        this.row2 = row2;
     /*   RT = row1.getAverageRT();
        RT2 = row2.getAverageRT();*/
    }

   /* public AlignStructMol(PeakListRow row1, PeakListRow row2, RawDataFile file, RawDataFile file2) {
        this.row1 = row1;
        this.row2 = row2;
        if (row1.getPeak(file) != null) {
            RT = row1.getPeak(file).getRT();
        } else {
            RT = row1.getAverageRT();
        }

        if (row2.getPeak(file2) != null) {
            RT2 = row2.getPeak(file2).getRT();
        } else {
            RT = row1.getAverageRT();
        }
    }*/

    AlignStructMol() {

    }

    public int compare(Object arg0, Object arg1) {
        if (((AlignStructMol) arg0).RT < ((AlignStructMol) arg1).RT) {
            return -1;
        } else {
            return 1;
        }
    }
}



