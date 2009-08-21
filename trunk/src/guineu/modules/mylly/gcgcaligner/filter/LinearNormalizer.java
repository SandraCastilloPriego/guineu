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
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentSorterFactory;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import guineu.modules.mylly.gcgcaligner.process.PollableCallable;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;



public class LinearNormalizer extends AbstractStatusReportingModule<Alignment, Alignment> implements PollableCallable<Alignment>
{
	
	private final static double BASE_LEVEL = 100.0;
	
	private final int REPORTING_FREQUENCY = 500;
	
	private final double baseLevel;

	private final AlignmentRow onlyStandard;
	private final List<AlignmentRow> _standards;
	private final double[] ends;
	private volatile double _done;
	private double _total;
	private Alignment _input;

	
	public LinearNormalizer(Collection<AlignmentRow> standards, Alignment input)
	{
		if (standards.size() == 0)
		{
			throw new IllegalArgumentException("No standards given!");
		}
		ends = new double[standards.size()];
		baseLevel = BASE_LEVEL;
		
		if (standards.size() > 1)
		{
			onlyStandard = null;

			ArrayList<AlignmentRow> tempRows = new ArrayList<AlignmentRow>(standards);
			sort(tempRows);

			_standards = Collections.unmodifiableList(tempRows);

			for (int i = 0; i < tempRows.size(); i++)
			{
				double curPoint = tempRows.get(i).getMeanRT1();
				double nextPoint = (i == tempRows.size() - 1 ? Double.POSITIVE_INFINITY : tempRows.get(i+1).getMeanRT1());
				double end = (curPoint + nextPoint) / 2;
				ends[i] = end;
			}
		}
		else if (standards.size() == 1)
		{
			_standards = null;
			Iterator<AlignmentRow> i = standards.iterator();
			onlyStandard = i.next();
		}
		else{throw new IllegalArgumentException("Empty standard list");}
		
		_input = input;
		_total = input == null ? 0 : input.rowCount(); 
	}
	
	public LinearNormalizer(Collection<AlignmentRow> standards)
	{
		this(standards, null);
	}

	private void sort(List<AlignmentRow> rows)
	{
		Collections.sort(rows, AlignmentSorterFactory.getComparator(AlignmentSorterFactory.SORT_MODE.rt2));
		Collections.sort(rows, AlignmentSorterFactory.getComparator(AlignmentSorterFactory.SORT_MODE.rt1));
	}

	@Override
	protected Alignment actualMap(Alignment input) throws CancellationException
	{
		synchronized(this)
		{
			_input = input;
			_total = input.rowCount();
		}
		try
		{
			return call();
		} catch (Exception e)
		{
			throw new CancellationException(e.getLocalizedMessage());
		}
	}
	
	private int findProperIndex(AlignmentRow r)
	{
		int index = java.util.Arrays.binarySearch(ends, r.getMeanRT1());
		if (index < 0)
		{
			index = -(index + 1);
		}
		return index;
	}

	@Override
	public String getName()
	{
		return "Linear normalizer";
	}

	public Alignment call() throws Exception
	{
		Alignment normalized = new Alignment(_input.getColumnNames(), 
		                                     _input.getParameters(), 
		                                     _input.getAligner());
		if (onlyStandard == null) //Multiple standards
		{
			GCGCDatum[][] stds = new GCGCDatum[_standards.size()][];
			for (int i = 0; i < _standards.size(); i++)
			{
				stds[i] = _standards.get(i).getRow();
			}
			double[][] coeffs = new double[stds.length][];
			int count = 0;
			for (int i = 0; i < stds.length; i++)
			{
				GCGCDatum[] curStd = stds[i];
				double[] curCoeffs = new double[curStd.length];
				for (int j = 0; j < curCoeffs.length; j++)
				{
					curCoeffs[j] = baseLevel / curStd[i].getArea();
				}
				coeffs[i] = curCoeffs;
			}
			ArrayList<AlignmentRow> rows = new ArrayList<AlignmentRow>(_input.getAlignment());
			
                        for (int i = 0; i < rows.size(); i++)
			{
				AlignmentRow cur = rows.get(i);
                                if(cur.getQuantMass() < 0){
                                    int ix = findProperIndex(cur);
                                    rows.set(i, cur.scaleArea(coeffs[ix]));
                                    if (count++ % REPORTING_FREQUENCY == 0)
                                    {
                                            _done = count;
                                    }
                                }
			}
			normalized.addAll(rows);
		}
		else //Only one standard
		{
			int count = 0;
			GCGCDatum[] stds = onlyStandard.getRow();
			double[] coeffs = new double[stds.length];
			for (int i = 0; i < stds.length; i++)
			{
				coeffs[i] = baseLevel / stds[i].getArea();
			}
			ArrayList<AlignmentRow> rows = new ArrayList<AlignmentRow>(_input.getAlignment());
			for (int i = 0; i < rows.size(); i++)
			{				
				AlignmentRow scaled = rows.get(i).scaleArea(coeffs);
				rows.set(i, scaled);
				if (count++ % REPORTING_FREQUENCY == 0)
				{
					_done = count;
				}
			}
			normalized.addAll(rows);
		}
		_done = _total;
		return normalized;
	}

	public double getDone()
	{
		return _done;
	}

	public double getTotal()
	{
		return _total;
	}
}
