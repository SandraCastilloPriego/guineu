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





import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author scsandra
 *
 */
public class NamePostFilter {
	
	
	private NameFilterTool curNameFilter;

	public NamePostFilter()
	{
		curNameFilter = new NameFilterTool(new ArrayList<String>());
	}
	
	public void generateNewFilter(Collection<String> names)
	{
		Set<String> filteredNames = curNameFilter.filteredNames();
		filteredNames.addAll(names);
		curNameFilter = new NameFilterTool(filteredNames);
	}	
	
	public String getName()
	{
		return "Filter by peak name";
	}
	

	public SimpleGCGCDataset actualMap(SimpleGCGCDataset input)
	{
		//we don't want to apply this filter in the peaks with Quant Mass
		List<SimplePeakListRowGCGC> QuantMassOnes = input.getQuantMassAlignments();
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);		
		input = filterQuantMass.actualMap(input); //Filter the quant mass alignments out
		
		
		List<SimplePeakListRowGCGC> als = new ArrayList<SimplePeakListRowGCGC>();
		
		for (PeakListRow row : input.getAlignment())
		{
			if(curNameFilter.include((String)row.getVar("getName"))){
				als.add((SimplePeakListRowGCGC)row.clone());
			}
			
		}
		SimpleGCGCDataset filtered = new SimpleGCGCDataset(input.getColumnNames(), input.getParameters(), input.getAligner());
		filtered.addAll(als);
		for(SimplePeakListRowGCGC row : QuantMassOnes){
			filtered.addAlignmentRow((SimplePeakListRowGCGC)row.clone());
		}
		return filtered;		
	}	
		
}
