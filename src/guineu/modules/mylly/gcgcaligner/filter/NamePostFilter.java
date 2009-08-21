/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of MULLU.

    MULLU is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation.

    MULLU is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MULLU; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package guineu.modules.mylly.gcgcaligner.filter;




import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;

/**
 * @author scsandra
 *
 */
public class NamePostFilter extends AbstractStatusReportingModule<Alignment, Alignment> {
	
	
	private NameFilter curNameFilter;

	public NamePostFilter()
	{
		curNameFilter = new NameFilter(new ArrayList<String>());
	}
	
	private void generateNewFilter(Collection<String> names)
	{
		Set<String> filteredNames = curNameFilter.filteredNames();
		filteredNames.addAll(names);
		curNameFilter = new NameFilter(filteredNames);
	}
	
	public void askParameters(Frame parentWindow)
	{
		File f = null; /*GCGCAlign.getMainWindow().launchSingleFileChooser(getClass());*/
		if (f != null)
		{
			try
			{
				FileReader fr = new FileReader(f);
				BufferedReader br = new BufferedReader(fr);
				String line = null;
				List<String> names = new ArrayList<String>();
				while ((line = (br.readLine())) != null)
				{
					names.add(line);
				}
				generateNewFilter(names);
			}
			catch (FileNotFoundException e)
			{
			//	GCGCAlign.getMainWindow().displayErrorDialog("File " + f + " was not found");
			} 
			catch (IOException e)
			{
				//GCGCAlign.getMainWindow().displayErrorDialog(e);
			}
		}
	}
	
	public String getName()
	{
		return "Filter by peak name";
	}
	
	@Override
	protected Alignment actualMap(Alignment input) throws CancellationException
	{
		//we don't want to apply this filter in the peaks with Quant Mass
		List<AlignmentRow> QuantMassOnes = input.getQuantMassAlignments();		
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);		
		input = filterQuantMass.map(input); //Filter the quant mass alignments out	
		
		
		List<AlignmentRow> als = new ArrayList<AlignmentRow>();
		
		for (AlignmentRow row : input.getAlignment())
		{
			if(curNameFilter.include(row.getName())){
				als.add(row);
			}
			
		}
		Alignment filtered = new Alignment(input.getColumnNames(), input.getParameters(), input.getAligner());
		filtered.addAll(als);
		filtered.addAll(QuantMassOnes);
		return filtered;		
	}	
	

	public boolean isConfigurable()
	{
		return true;
	}
}
