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
package guineu.modules.mylly.filter.NameFilter;

import guineu.modules.mylly.datastruct.GCGCData;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class NameFilter {

	private NameFilterTool curNameFilter;

	public NameFilter() {
		curNameFilter = new NameFilterTool(new ArrayList<String>());
	}

	public void generateNewFilter(Collection<String> names) {
		Set<String> filteredNames = curNameFilter.filteredNames();
		filteredNames.addAll(names);
		curNameFilter = new NameFilterTool(filteredNames);
	}

	public String getName() {
		return curNameFilter.getName();
	}

	public GCGCData actualMap(GCGCData obj) {

		int done = 0;

		GCGCData gcgcdata = obj;

		GCGCData filtered = null;

		List<GCGCDatum> filteredFile = new ArrayList<GCGCDatum>();
		for (GCGCDatum d : gcgcdata) {

			if (curNameFilter.include(d)) {
				filteredFile.add(d);
			}
			done++;
		}
		filtered = new GCGCData(filteredFile, gcgcdata.getName());

		return filtered;
	}
}
