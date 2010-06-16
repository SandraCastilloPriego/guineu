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
package guineu.data.impl;

/**
 *
 * @author scsandra
 */
public class Bexperiments {

    public String Name,  TYPE,  PROJECT,  REPLICATE,  PERSON,  Amount,  Unit,  Method,  Sample,  EDATE;
    public boolean state = false;

    public Bexperiments(String[] data) {
        this.Name = data[0];
        this.TYPE = data[1].toUpperCase();
        this.PROJECT = data[2].toUpperCase();
        this.REPLICATE = data[3].toUpperCase();
        this.PERSON = data[4].toUpperCase();
        this.Amount = data[5];
        this.Unit = data[6];

        String[] moreData = this.Name.split("_");
        if (moreData.length == 5) {
            this.Method = moreData[0].toUpperCase();
            this.Sample = moreData[1].toUpperCase();
            this.EDATE = moreData[3];
            state = true;
        }

    }

	public Bexperiments clone(){
		String[] data = new String[7];
		data[0] = this.Name;
		data[1] = this.TYPE;
		data[2] = this.PROJECT;
		data[3] = this.REPLICATE;
		data[4] = this.PERSON;
		data[5] = this.Amount;
		data[6] = this.Unit;
		return new Bexperiments(data);
	}
}
