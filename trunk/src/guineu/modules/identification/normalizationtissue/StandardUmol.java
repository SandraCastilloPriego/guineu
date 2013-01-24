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
package guineu.modules.identification.normalizationtissue;

import guineu.data.PeakListRow;
import guineu.util.Range;

/**
 *
 * @author scsandra
 */
public class StandardUmol {

    private PeakListRow row;
    private String name;
    private double realAmount = 0.0;
    private Range RTRange;

    public StandardUmol(PeakListRow row) {
        this.row = row;
        this.name = (String) row.getVar("getName");
        this.RTRange = new Range(0.0, 0.0);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRealAmount(double value) {
        this.realAmount = value;
    }

    public void addValue(PeakListRow values) {
        this.row = values;
    }

    public double getRealAmount() {
        return realAmount;
    }

    public Double getIntensity(String experimentName) {
        return (Double) row.getPeak(experimentName);
    }

    public String getName() {
        return name;
    }

    public void setRange(Range RTRange) {
        try {
            this.RTRange = RTRange;
        } catch (Exception e) {
        }
    }

    public Range getRange() {
        return this.RTRange;
    }

    public boolean isSelect() {
        if (row.isSelected() || (Integer) row.getVar("getStandard") == 1) {
            return true;
        }
        return false;
    }
}
