/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.data.datamodels;


public enum LCMSColumnName {

	SELECTION("Selection"),
	ID("Id"),
	MZ("m/z"),
	RT("Retention time"),
	NAME("Name"),
	ALLNAMES("All names"),
	PUBCHEM("PubChem ID"),
	VTT("VTT ID"),
	ALLVTT("All VTT IDs"),
	Class("Lipid class"),
	NFOUND("Num found"),
	STANDARD("Standard"),
	FA("FA Composition"),
	ALIGNMENT("Alignment");
	private final String columnName;

	LCMSColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return this.columnName;
	}
}
