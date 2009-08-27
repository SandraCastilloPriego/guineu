/*
Copyright 2006-2007 VTT Biotechnology
This file is part of MYLLY.
MYLLY is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.
MYLLY is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with MYLLY; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package guineu.modules.mylly.gcgcaligner.datastruct;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Unmodifiable view to peaks in one file.
 * @author jmjarkko
 */
public class GCGCData implements Iterable<GCGCDatum>, Cloneable {

	private static long nextId = Long.MIN_VALUE;
	private List<GCGCDatum> data; //This one is unmodifiable
	private String name,  CAS;
	private int hashcode;
	private boolean hashcodeCalculated = false;
	private long id;

	private synchronized static long getId() {
		return nextId++;
	}

	public GCGCData(List<GCGCDatum> list, String name) {
		this.data = list;
		this.name = name;
		this.id = getId();
	}

	/**
	 * Does not perform a deep copy as GCGCDatum is supposed to
	 * be immutable.
	 */
	public GCGCData clone() {
		try {
			GCGCData clone = (GCGCData) super.clone();
			clone.data = new ArrayList<GCGCDatum>();
			clone.data.addAll(data);
			return clone;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			throw new InternalError("Clone not supported");
		}
	}

	public int compoundCount() {
		return data.size();
	}

	public GCGCDatum getCompound(int ix) {
		return data.get(ix);
	}

	public boolean equals(Object o) {
		if (o instanceof GCGCData) {
			GCGCData other = (GCGCData) o;
			return other.id == id;
		}
		return false;
	}

	public int hashCode() {
		if (!hashcodeCalculated) {
			hashcode = name.hashCode() + 31 * data.hashCode();
			hashcodeCalculated = true;
		}
		return hashcode;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCAS() {
		return CAS;
	}

	public Iterator<GCGCDatum> iterator() {
		return data.iterator();
	}

	public List<GCGCDatum> toList() {
		ArrayList<GCGCDatum> list = new ArrayList<GCGCDatum>(data);
		return list;
	}

	public GCGCDatum[] toArray() {
		int len = data.size();
		int ix = 0;
		GCGCDatum array[] = new GCGCDatum[len];
		for (GCGCDatum d : data) {
			array[ix++] = d;
		}
		return array;
	}	
	
}
