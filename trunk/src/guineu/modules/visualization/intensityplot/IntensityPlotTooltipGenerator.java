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
/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */

package guineu.modules.visualization.intensityplot;

import java.text.DecimalFormat;
import java.text.Format;

import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.xy.XYDataset;

/**
 * 
 */
class IntensityPlotTooltipGenerator implements CategoryToolTipGenerator,
        XYToolTipGenerator {

    /**
     * @see org.jfree.chart.labels.CategoryToolTipGenerator#generateToolTip(org.jfree.data.category.CategoryDataset,
     *      int, int)
     */
    public String generateToolTip(CategoryDataset dataset, int row, int column) {
        Format intensityFormat = new DecimalFormat("0.0E0");
        Double peaks[] = ((IntensityPlotDataset) dataset).getPeaks(row, column);
        String files[] = ((IntensityPlotDataset) dataset).getFiles(column);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < files.length; i++) {
            sb.append(files[i].toString());
            sb.append(": ");
            if (peaks[i] != null) {              
                sb.append("height: ");
                sb.append(intensityFormat.format(peaks[i]));
            } else {
                sb.append("N/A");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public String generateToolTip(XYDataset dataset, int series, int item) {
        return generateToolTip((CategoryDataset) dataset, series, item);
    }

}
