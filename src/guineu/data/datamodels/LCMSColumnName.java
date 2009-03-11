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

/**
 *
 * @author bicha
 */
public enum LCMSColumnName {

	SELECTION("Selection", 0),
	ID("Id", 1),
	MZ("m/z", 2),
	RT("Retention time", 3),
	NAME("Name", 4),
	ALLNAMES("All names", 5),
	Class("Lipid class", 6),
	NFOUND("Num found", 7),
	STANDARD("Standard", 8),
	FA("FA Composition", 9),
	ALIGNMENT("Alignment", 10);

	private final String columnName;
	private final int index;
	LCMSColumnName(String columnName, int index) {
		this.columnName = columnName;
		this.index = index;
	}

	public String getColumnName() {
		return this.columnName;
	}

	public int getIndex(){
		return this.index;
	}


}
