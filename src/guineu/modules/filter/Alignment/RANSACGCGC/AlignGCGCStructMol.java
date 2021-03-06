/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.filter.Alignment.RANSACGCGC;

import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import java.util.Comparator;

public class AlignGCGCStructMol implements Comparator<AlignGCGCStructMol> {

        public PeakListRow row1, row2;
        public double RT, RT2;
        public boolean Aligned = false;
        public boolean ransacMaybeInLiers;
        public boolean ransacAlsoInLiers;

        public AlignGCGCStructMol(SimplePeakListRowGCGC row1, SimplePeakListRowGCGC row2, boolean useRTI) {
                this.row1 = row1;
                this.row2 = row2;
                if (useRTI) {
                        RT = row1.getRTI();
                        RT2 = row2.getRTI();
                } else {
                        RT = row1.getRT1();
                        RT2 = row2.getRT1();
                }
        }

        AlignGCGCStructMol() {
        }

        public int compare(AlignGCGCStructMol arg0, AlignGCGCStructMol arg1) {
                if (arg0.RT2 <= arg1.RT2) {
                        return -1;
                } else {
                        return 1;
                }
        }
}
