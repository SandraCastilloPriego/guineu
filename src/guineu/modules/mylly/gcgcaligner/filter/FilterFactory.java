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
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingProcess;
import guineu.modules.mylly.gcgcaligner.process.StatusChange;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CancellationException;


public class FilterFactory
{

	public static AbstractStatusReportingProcess<Alignment, Alignment> 
	createPostFilter(final FilterFunction<AlignmentRow> f, final String name)
	{

		return new AbstractStatusReportingProcess<Alignment, Alignment>()
		{

			@Override
			protected Alignment actualMap(Alignment input)
			throws CancellationException
			{
				if (input == null){return null;}

				currentStatus = StatusChange.STATUS.RUNNING;
				final int taskCount = input.rowCount();
				final int reportFrequency = 500;
				int counter = 0;
				lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.NOT_STARTED, 
				                                     StatusChange.STATUS.RUNNING, name,
				                                     0, taskCount));

				Alignment filteredAlignment = new Alignment(input.getColumnNames(), 
				                                            input.getParameters(), 
				                                            input.getAligner());
				
				for (AlignmentRow row : input.getAlignment())
				{
					if (++counter == reportFrequency)
					{
						counter = 0;
						checkForCancellation();
						lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.RUNNING, 
						                                     StatusChange.STATUS.RUNNING, name,
						                                     counter, taskCount));
					}
					if (f.include(row))
					{
						filteredAlignment.addAlignmentRow(row);
					}
				}
				lh.fireStatusChange(new StatusChange(this, StatusChange.STATUS.RUNNING, 
				                                     StatusChange.STATUS.FINISHED, name,
				                                     taskCount, taskCount));
				currentStatus = StatusChange.STATUS.FINISHED;
				return filteredAlignment;
			}

			public AbstractStatusReportingProcess<Alignment, Alignment> clone()
			{
				return createPostFilter(f, name);
			}

			@Override
			public String getName()
			{
				return name;
			}
		};
	}

	public static AbstractStatusReportingProcess<InputSet, InputSet>  
	createPreFilter(final FilterFunction<GCGCDatum> f, final String name)
	{

		return new AbstractStatusReportingProcess<InputSet, InputSet>()
		{

			@Override
			protected InputSet actualMap(InputSet input)
			throws CancellationException
			{
				if (input == null){return null;}
				ArrayList<GCGCData> toFilter = new ArrayList<GCGCData>(input.getData());
				ListIterator<GCGCData> iter = toFilter.listIterator();
				while(iter.hasNext())
				{
					GCGCData cur = iter.next();
					//iter.set(new GCGCData(filter(cur.toList(), f), cur.getName()));
				}
				return new InputSet(toFilter);
			}

			public AbstractStatusReportingProcess<InputSet, InputSet> clone()
			{
				return createPreFilter(f, name);
			}

			@Override
			public String getName()
			{
				return name;
			}			
		};
	}

	public static AbstractStatusReportingProcess<InputSet, InputSet> 
	createPreFilter(FilterFunction<GCGCDatum> f)
	{
		return createPreFilter(f, f.getName());
	}

	public static AbstractStatusReportingProcess<Alignment, Alignment> 
	createPostFilter(final FilterFunction<AlignmentRow> f)
	{
		return createPostFilter(f, f.getName());
	}

}
