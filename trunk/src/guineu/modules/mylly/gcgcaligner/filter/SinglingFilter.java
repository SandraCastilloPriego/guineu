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
import guineu.modules.mylly.gcgcaligner.cli.NumArgRange;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.Pair;
import guineu.modules.mylly.gcgcaligner.gui.parameters.BooleanParameterOption;
import guineu.modules.mylly.gcgcaligner.gui.parameters.NumberParameterOption;
import guineu.modules.mylly.gcgcaligner.gui.parameters.NumberParserGenerator;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterDialog;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterOption;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import guineu.modules.mylly.gcgcaligner.process.BooleanLatch;
import guineu.modules.mylly.gcgcaligner.process.PollableCallable;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;



public class SinglingFilter extends AbstractStatusReportingModule<Alignment, Alignment> implements PollableCallable<Alignment>
{
	private boolean filterUnknowns;
	private double minSimilarity;
	private Alignment input;
	private volatile double total;
	private volatile double done;

	@Override
	protected Alignment actualMap(final Alignment input) throws CancellationException
	{
		this.input = input;
		try
		{
			return call();
		} catch (Exception e)
		{
			return null;
		}

	}

	public SinglingFilter()
	{
		filterUnknowns = true;
	}

	public void askParameters(Frame parentWindow)
	throws ParameterInputException
	{
		final String unknownOptionName = "Filter unknown peaks";
		final String minSimOptionName = "Minimum required max similarity";
		final String finished = "Setting options for " + getClass().getName() + " finished";
		List<ParameterOption<?>> options = new ArrayList<ParameterOption<?>>();
		options.add(new BooleanParameterOption(false, unknownOptionName));
		options.add(new NumberParameterOption<Double>(0.0, 
				new NumArgRange<Double>(0.0, 1000.0), 
				minSimOptionName, 
				NumberParserGenerator.genParser(double.class)));

		final ParameterDialog params = new ParameterDialog(parentWindow, "Set options", options, finished);
		final BooleanLatch successFlag = new BooleanLatch(false);

		params.addPropertyChangeListener(new PropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt != null && evt.getSource() == params && 
						finished.equals(evt.getPropertyName()))
				{
					Boolean success = (Boolean) evt.getNewValue();
					if (success)
					{
						filterUnknowns = (Boolean) params.getValue(unknownOptionName);
						minSimilarity = (Double) params.getValue(minSimOptionName);
						successFlag.set(true);
					}
				}
			}
		});
		params.setVisible(true);
		if (!successFlag.isSet())
		{
			throw new ParameterInputException("Parameter input was cancelled");
		}
	}

	public boolean isConfigurable(){return true;}

	@Override
	public String getName()
	{
		return "Leave only uniques filter";
	}

	private static class PeakReducer
	{
		private final boolean _containsMainPeaks;
		private final boolean _filterUnknowns;

		//First one will contain the largest peak, second one the peak nearest
		//to ideal. Those can be the same peak
		private Map<String, Pair<AlignmentRow, AlignmentRow>> _peaks;
		private List<AlignmentRow> _unknownsList;
		private final double _minSimilarity;

		public PeakReducer(boolean containsMainPeaks, boolean filterUnknowns)
		{
			this(containsMainPeaks, filterUnknowns, 0);
		}
		
		public PeakReducer(boolean containsMainPeaks, boolean filterUnknowns, double minSimilarity)
		{
			_containsMainPeaks = containsMainPeaks;
			_filterUnknowns = filterUnknowns;
			_minSimilarity = minSimilarity;
			_peaks = new HashMap<String, Pair<AlignmentRow, AlignmentRow>>();
			_unknownsList = new ArrayList<AlignmentRow>();
		}

		public void addAlignment(AlignmentRow peak)
		{			
			if (peak.getMaxSimilarity() < _minSimilarity){return;}
			
			if (peak.getName().contains(GCGCDatum.UNKOWN_NAME))
			{
				if (! _filterUnknowns)
				{
					_unknownsList.add(peak);
				}
				return;
			}

			Pair<AlignmentRow, AlignmentRow> pair = _peaks.get(peak.getName());
			if (pair == null)
			{
				pair = new Pair<AlignmentRow, AlignmentRow>(null, null);
				_peaks.put(peak.getName(), pair);
			}

			//First of pair, the one with most peaks
			boolean setFirst = false;
			if (pair.getFirst() != null)
			{
				int peakCountDiff = peak.nonNullPeakCount() - pair.getFirst().nonNullPeakCount();
				if (peakCountDiff > 0 || (peakCountDiff == 0 && peak.getMaxSimilarity() >
				pair.getFirst().getMaxSimilarity()))
				{
					setFirst = true;
				}
			}
			else{setFirst = true;}
			if (setFirst){pair.setFirst(peak);}
			
			//Second of pair, the closest to ideal by RTI alignment.
			boolean distanceLess = (pair.getSecond() == null) 
			|| peak.getDistValue().compareTo(pair.getSecond().getDistValue())  < 0;
			
			boolean similarityMore = (pair.getSecond() == null) 
			|| peak.getDistValue().compareTo(pair.getSecond().getDistValue()) == 0 &&
			peak.getMaxSimilarity() > pair.getSecond().getMaxSimilarity();
			
			boolean setSecond = _containsMainPeaks && (distanceLess || similarityMore);
			if (setSecond){pair.setSecond(peak);}	
			
		}

		public List<AlignmentRow> getAlignmentRows()
		{	
			ArrayList<AlignmentRow> peaks = _containsMainPeaks ? new ArrayList<AlignmentRow>(2*_peaks.size()) : new ArrayList<AlignmentRow>(_peaks.size());

			for(Map.Entry<String, Pair<AlignmentRow, AlignmentRow>> peak : _peaks.entrySet())
			{
				AlignmentRow first = peak.getValue().getFirst();
				peaks.add(first);
				AlignmentRow second = peak.getValue().getSecond();
				
				if (_containsMainPeaks && !(first.equals(second)))
				{
					peaks.add(second);
				}
			}
			for (AlignmentRow row : _unknownsList)
			{
				peaks.add(row);
			}
			return peaks;
		}
	}

	public Alignment call() throws Exception
	{
		//we don't want to apply this filter in the peaks with Quant Mass
		List<AlignmentRow> QuantMassOnes = input.getQuantMassAlignments();		
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);
		input = filterQuantMass.map(input); //Filter the quant mass alignments out		
		
		PeakReducer reducer = new PeakReducer(input.containsMainPeaks(), filterUnknowns, minSimilarity);
		List<AlignmentRow> rows = input.getAlignment();
		total = rows.size();
		int count = 0;
		for (AlignmentRow row : rows)
		{
			reducer.addAlignment(row);
			if (count++ % 1000 == 0){done = count;}
		}
		Alignment modified = new Alignment(input.getColumnNames(), input.getParameters(), input.getAligner());
		modified.addAll(reducer.getAlignmentRows());
		modified.addAll(QuantMassOnes);
		done = total;
		return modified;
	}

	public double getDone()
	{
		return done;
	}

	public double getTotal()
	{
		return total;
	}
}
