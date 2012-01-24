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
package guineu.modules.mylly.datastruct;

import java.util.List;

/**
 * @author jmjarkko
 */
public interface Peak {

    /**
     * Return the retention time of the first column in the chromatogram.
     * @return Retention time 1
     */
    public double getRT1();

    /**
     * Return the retention time of the second column in the chromatogram.
     * @return Retention time 2
     */
    public double getRT2();

    /**
     * Return retention time index.
     * @return Retention time index
     */
    public double getRTI();

    /**
     * Return the area of the peak.
     * @return Peak area.
     */
    public double getArea();

    /**
     * Return CAS identifier.
     * @return CAS Id
     */
    public String getCAS();

    /**
     * Return the concentration of the metabolite.
     * @return Metabolite concentration
     */
    public double getConcentration();

    /**
     * Return the spectrum of the metabolite.
     * @return class "Spectrum"
     */
    public Spectrum getSpectrum();

    /**
     * Return true if the peak "p" has the same name, and false if their names are different.
     * @param p Other peak
     * @return true when their names match
     */
    public boolean matchesWithName(Peak p);

    /**
     * Return a list with all possible identification of the peak.
     * @return
     */
    public List<String> names();

    /**
     * Return true if the peak has the quant mass information.
     * @return true if the peak has the quant mass information
     */
    public boolean hasQuantMass();

    /**
     * Return the quant mass of the peak
     * @return Peak's quant mass
     */
    public double getQuantMass();
}
