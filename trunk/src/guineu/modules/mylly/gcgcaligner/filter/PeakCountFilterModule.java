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
package guineu.modules.mylly.gcgcaligner.filter;

import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import java.awt.Frame;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.prefs.AbstractPreferences;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class PeakCountFilterModule extends AbstractStatusReportingModule<Alignment, Alignment>
{
	private int peakCount;
	
	public PeakCountFilterModule()
	{
		peakCount = 0;
		updateFilter();
	}
	
	private void updateFilter()
	{
		mapper = FilterFactory.createPostFilter(new PeakCountFilter(peakCount));
	}
	
	public void askParameters(Frame parentWindow)
	{
		String inputMessage = "Filter out if peak count <= ";
		Integer initialSelectionValue = new Integer(0);
		int numOfPeaks = -1;
		String input;
		do
		{
			input = JOptionPane.showInputDialog(parentWindow, inputMessage, initialSelectionValue);
			try
			{
				numOfPeaks = Integer.parseInt(input);
				if (numOfPeaks < 0){numOfPeaks = 0;}
			}
			catch (NumberFormatException e)
			{
			}
		} while(numOfPeaks < 0 && input != null);

		if (numOfPeaks > 0)
		{
			peakCount = numOfPeaks;
			updateFilter();
		}
	}

	public String getName()
	{
		return mapper.getName();
	}
	
	public PeakCountFilterModule clone()
	{
		PeakCountFilterModule m = (PeakCountFilterModule) super.clone();
		return m;
	}

	public boolean isConfigurable()
	{
		return true;
	}
}
