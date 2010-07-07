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
package guineu.modules.identification.normalizationserum;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.DatasetType;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.taskcontrol.TaskStatus;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author scsandra
 */
public class NormalizeSerum {

    private Dataset dataset;
    private double cont = 0;
    private Vector<StandardUmol> stdMol;

    public NormalizeSerum(Dataset dataset, Vector<StandardUmol> stdMol) {
        this.dataset = dataset;
        this.stdMol = stdMol;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public double getNormalizedValue(double stdRealConcentration, double stdConcentration, double concentration) {
        try {
            if (stdConcentration == 0) {
                return 0;
            }
            return (stdRealConcentration / stdConcentration) * concentration;
        } catch (Exception e) {
            return 0;
        }
    }

    public StandardUmol getUnknownName(PeakListRow row) {
        double RT = (Double) row.getVar("getRT");
        for (StandardUmol std : this.stdMol) {
            if (std.getRange().contains(RT)) {
                return std;
            }
        }
        return null;
    }

    public void normalize(TaskStatus status) {
        for (PeakListRow row : dataset.getRows()) {
            StandardUmol standard = this.getStd(row);            
            if (standard != null) {
               // System.out.println(row.getVar("getName") + " - " + standard.getName());
                for (String experimentName : dataset.getAllColumnNames()) {
                    if (status == TaskStatus.CANCELED || status == TaskStatus.ERROR) {
                        return;
                    }

                    try {
                        Double valueNormalized = (Double) row.getPeak(experimentName);
                        valueNormalized = this.getNormalizedValue(standard.getRealAmount(), standard.getIntensity(experimentName), valueNormalized);
                        row.setPeak(experimentName, new Double(valueNormalized));
                    } catch (Exception exception2) {
                        exception2.printStackTrace();
                    }
                }
            }
            cont++;
        }
    }

    /**
     * Each compound has its own standard. This function return a different standard for each
     * compound.
     * @return
     */
    private StandardUmol getStd(PeakListRow row) {
        String compoundName = (String) row.getVar("getName");
        // In the case the lipid is identified as an adduct, the program search the
        // main peak to get the standard class and change the name to the field "lipid"
        if (dataset.getType() == DatasetType.LCMS) {

            if (compoundName.matches(".*adduct.*")) {
                double num = 0;
                Pattern adduct = Pattern.compile("\\d+.\\d+");
                Matcher matcher = adduct.matcher(compoundName);
                if (matcher.find()) {
                    num = Double.valueOf(compoundName.substring(matcher.start(), matcher.end()));
                }
                double difference = Double.POSITIVE_INFINITY;
                for (PeakListRow parentRow : dataset.getRows()) {
                    double mzRow = ((SimplePeakListRowLCMS) parentRow).getMZ();
                    double dif = Math.abs(num - mzRow);
                    if (dif < difference) {
                        difference = dif;
                        compoundName = ((SimplePeakListRowLCMS) parentRow).getName();
                    }
                }
            }
        }

        // In the case the main lipid is unknown or another adduct..
        if (compoundName.matches(".*unknown.*") || compoundName.matches(".*adduct.*")) {
            return this.getUnknownName(row);
        }

        return getMostSimilar(compoundName);
    }

    private StandardUmol getMostSimilar(String compoundName) {
        StandardUmol minDistanceStd = null;
        int minDistance = Integer.MAX_VALUE;
        for (StandardUmol std : this.stdMol) {
            int dist = this.getLevenshteinDistance(std.getName(), compoundName);
            if (dist < minDistance) {
                minDistance = dist;
                minDistanceStd = std;
            }
        }

        return minDistanceStd;
    }

    public double getProgress() {
        return (cont / (dataset.getNumberRows()));
    }

    public int getLevenshteinDistance(String s, String t) {
        if (s == null || t == null) {
            throw new IllegalArgumentException("Strings must not be null");
        }

        /*
        Levenshtein Distance Algorithm: Java Implementation
        by Chas Emerick
        The difference between this impl. and the previous is that, rather
        than creating and retaining a matrix of size s.length()+1 by t.length()+1,
        we maintain two single-dimensional arrays of length s.length()+1.  The first, d,
        is the 'current working' distance array that maintains the newest distance cost
        counts as we iterate through the characters of String s.  Each time we increment
        the index of String t we are comparing, d is copied to p, the second int[].  Doing so
        allows us to retain the previous cost counts as required by the algorithm (taking
        the minimum of the cost count to the left, up one, and diagonally up and to the left
        of the current cost count being calculated).  (Note that the arrays aren't really
        copied anymore, just switched...this is clearly much better than cloning an array
        or doing a System.arraycopy() each time  through the outer loop.)

        Effectively, the difference between the two implementations is this one does not
        cause an out of memory condition when calculating the LD over two very large strings.
         */

        int n = s.length(); // length of s
        int m = t.length(); // length of t

        if (n == 0) {
            return m;
        } else if (m == 0) {
            return n;
        }

        int p[] = new int[n + 1]; //'previous' cost array, horizontally
        int d[] = new int[n + 1]; // cost array, horizontally
        int _d[]; //placeholder to assist in swapping p and d

        // indexes into strings s and t
        int i; // iterates through s
        int j; // iterates through t

        char t_j; // jth character of t

        int cost; // cost

        for (i = 0; i <= n; i++) {
            p[i] = i;
        }

        for (j = 1; j <= m; j++) {
            t_j = t.charAt(j - 1);
            d[0] = j;

            for (i = 1; i <= n; i++) {
                cost = s.charAt(i - 1) == t_j ? 0 : 1;
                // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
                d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1] + cost);
            }

            // copy current distance counts to 'previous row' distance counts
            _d = p;
            p = d;
            d = _d;
        }

        // our last action in the above loop was to switch d and p, so p now
        // actually has the most recent cost counts
        return p[n];
    }
}
