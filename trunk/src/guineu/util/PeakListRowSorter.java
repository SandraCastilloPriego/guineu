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
package guineu.util;

import guineu.data.GCGCColumnName;
import guineu.data.PeakListRow;
import guineu.data.LCMSColumnName;
import java.util.Comparator;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 * 
 * Compare peak list rows either by ID, average m/z or median area of peaks
 * 
 */
public class PeakListRowSorter implements Comparator<PeakListRow> {

        private SortingProperty property;
        private SortingDirection direction;

        public PeakListRowSorter(SortingProperty property,
                SortingDirection direction) {
                this.property = property;
                this.direction = direction;
        }

        public int compare(PeakListRow row1, PeakListRow row2) {

                Double row1Value = getValue(row1);
                Double row2Value = getValue(row2);

                if (direction == SortingDirection.Ascending) {
                        return row1Value.compareTo(row2Value);
                } else {
                        return row2Value.compareTo(row1Value);
                }

        }

        private double getValue(PeakListRow row) {
                switch (property) {
                        case Intensity:
                                Object[] intensityPeaks = row.getPeaks(null);
                                double[] peakIntensities = new double[intensityPeaks.length];
                                for (int i = 0; i < intensityPeaks.length; i++) {
                                        try {
                                                peakIntensities[i] = (Double) intensityPeaks[i];

                                        } catch (Exception e) {
                                        }
                                }
                                double medianIntensity = MathUtils.calcQuantile(peakIntensities,
                                        0.5);
                                return medianIntensity;

                        case Height:
                                Object[] heightPeaks = row.getPeaks(null);
                                double[] peakHeights = new double[heightPeaks.length];
                                for (int i = 0; i < peakHeights.length; i++) {
                                        try {
                                                peakHeights[i] = (Double) heightPeaks[i];

                                        } catch (Exception e) {
                                        }
                                }
                                double medianHeight = MathUtils.calcQuantile(peakHeights, 0.5);
                                return medianHeight;
                        case MZ:
                                if (row.getClass().toString().contains("LCMS")) {
                                        return (Double) row.getVar(LCMSColumnName.MZ.getGetFunctionName());
                                }
                        case RT:
                                if (row.getClass().toString().contains("LCMS")) {
                                        return (Double) row.getVar(LCMSColumnName.RT.getGetFunctionName());
                                } else if (row.getClass().toString().contains("GCGC")) {
                                         return (Double) row.getVar(GCGCColumnName.RT1.getGetFunctionName());
                                }
                        case ID:
                                return row.getID();
                }

                // We should never get here, so throw exception
                throw (new IllegalStateException());
        }
}
