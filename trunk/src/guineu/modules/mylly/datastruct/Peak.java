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
package guineu.modules.mylly.datastruct;

import java.util.List;

/**
 * @author jmjarkko
 */
public interface Peak {
	//TODO WRITE THE DESCRIPTIONS!

	public double getRT1();

	public double getRT2();

	public double getRTI();

	public double getArea();

	public String getCAS();

	public double getConcentration();

	public Spectrum getSpectrum();

	public boolean matchesWithName(Peak p);

	public List<String> names();

	public boolean hasQuantMass();

	public double getQuantMass();
}
