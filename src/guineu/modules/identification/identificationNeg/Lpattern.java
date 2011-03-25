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
package guineu.modules.identification.identificationNeg;

import java.util.Comparator;

public class Lpattern implements Comparable {

    public String str;
    public int start;
    public int end;

    public int compareTo(Object anotherLipid) throws ClassCastException {
        if (!(anotherLipid instanceof Lpattern)) {
            throw new ClassCastException("A Lpattern object expected.");
        }
        int i = ((Lpattern) anotherLipid).start;
        return i;
    }
    public static Comparator StringComparator = new Comparator() {

        public int compare(Object Structl, Object anotherStructl) {
            String lipid_name = ((Lpattern) Structl).str.toUpperCase();
            String lipid_name2 = ((Lpattern) anotherStructl).str.toUpperCase();
            return lipid_name.compareTo(lipid_name2);
        }
    };
}
