/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.identification.identification;

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
