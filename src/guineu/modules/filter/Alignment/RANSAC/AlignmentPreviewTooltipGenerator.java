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



package guineu.modules.filter.Alignment.RANSAC;

import guineu.main.GuineuCore;
import java.text.NumberFormat;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;


/**
 * Tooltip generator
 */
class AlignmentPreviewTooltipGenerator implements XYToolTipGenerator {

    private NumberFormat rtFormat = GuineuCore.getRTFormat();
	String axis_X, axis_Y;

	public AlignmentPreviewTooltipGenerator(String axis_X, String axis_Y){
		this.axis_X = axis_X;
		this.axis_Y = axis_Y;
	}

    /**
     * @see org.jfree.chart.labels.XYToolTipGenerator#generateToolTip(org.jfree.data.xy.XYDataset,
     *      int, int)
     */
    public String generateToolTip(XYDataset dataset, int series, int item) {

        double rtValue = dataset.getYValue(series, item);
        double rtValue2 = dataset.getXValue(series, item);

        String tooltip = axis_X+ ": "
                + rtFormat.format(rtValue) + "\n" + axis_Y + ": "
                + rtFormat.format(rtValue2);

        return tooltip;

    }

}
