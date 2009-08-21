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
import guineu.modules.mylly.gcgcaligner.gui.parameters.MultipleChoiceStrings;
import guineu.modules.mylly.gcgcaligner.gui.parameters.NumberParameterOption;
import guineu.modules.mylly.gcgcaligner.gui.parameters.NumberParserGenerator;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterDialog;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterOption;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import guineu.modules.mylly.gcgcaligner.process.BooleanLatch;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;



public class SimilarityFilter extends
		AbstractStatusReportingModule<Alignment, Alignment>
{
	private final int CHECK_FREQUENCY = 10000;
	private final static String MAX_SIMILARITY = "maximum similarity";
	private final static String MEAN_SIMILARITY = "mean similarity";

	private double minValue;
	private String mode;
	
	public SimilarityFilter()
	{
		//mapper = new ActualFilter();
		
		minValue = 0.0;
		mode = MAX_SIMILARITY;
	}
	
	@Override
	public boolean isConfigurable()
	{
		return true;
	}
	
	public void askParameters(Frame parentWindow) throws ParameterInputException
	{
		List<String> choices = Arrays.asList(new String[]{MAX_SIMILARITY, MEAN_SIMILARITY});
		final String finished = "Setting options for " + getClass().getName() + " finished";
		final String simTypeName = "Similarity used";
		final String minSimName = "Minimum required similarity";
		List<ParameterOption<?>> options = new ArrayList<ParameterOption<?>>();
		options.add(new MultipleChoiceStrings(choices, choices.get(0), simTypeName));
		options.add(new NumberParameterOption<Double>(0.0, 
				new NumArgRange<Double>(0.0, 1000.0),
				minSimName,NumberParserGenerator.genParser(double.class)));
		
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
						minValue = (Double) params.getValue(minSimName);
						mode = (String) params.getValue(simTypeName);
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

	@Override
	protected Alignment actualMap(Alignment input) throws CancellationException
	{
		//we don't want to apply this filter in the peaks with Quant Mass
		List<AlignmentRow> QuantMassOnes = input.getQuantMassAlignments();		
		AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);		
		input = filterQuantMass.map(input); //Filter the quant mass alignments out	
		
		List<AlignmentRow> als = new ArrayList<AlignmentRow>();
		int counter = 0;
		int done = 0;
		final int total = input.rowCount();
		for (AlignmentRow row : input.getAlignment())
		{
			if (counter++ == CHECK_FREQUENCY)
			{
				checkForCancellation();
				counter = 0;
				updateStatus(done, total);
			}
			done++;
			double curVal = 0.0;
			if (MAX_SIMILARITY.equals(mode)){curVal = row.getMaxSimilarity();}
			else if (MEAN_SIMILARITY.equals(mode)){curVal = row.getMeanSimilarity();}
			if (curVal >= minValue)
			{
				als.add(row);
			}
		}
		Alignment filtered = new Alignment(input.getColumnNames(), input.getParameters(), input.getAligner());
		filtered.addAll(als);
		filtered.addAll(QuantMassOnes);
		return filtered;
	}

	@Override
	public String getName()
	{
		return "Filter by similarity";
	}
	
/*	private class ActualFilter extends AbstractStatusReportingProcess<Alignment, Alignment>
	{

		public void cancel()
		{
			// TODO Auto-generated method stub
			
		}

		public double done()
		{
			// TODO Auto-generated method stub
			return 0;
		}


		public double total()
		{
			// TODO Auto-generated method stub
			return 0;
		}
		
		@Override
		protected Alignment actualMap(Alignment input)
				throws CancellationException
		{
			//we don't want to apply this filter in the peaks with Quant Mass
			List<AlignmentRow> QuantMassOnes = input.getQuantMassAlignments();		
			AlignmentRowFilter filterQuantMass = new AlignmentRowFilter(QuantMassOnes);
			input = filterQuantMass.map(input); //Filter the quant mass alignments out		
			
			final String mode = SimilarityFilter.this.mode;
			final double minValue = SimilarityFilter.this.minValue;
			
			List<AlignmentRow> als = new ArrayList<AlignmentRow>();
			int counter = 0;
			int done = 0;
			final int total = input.rowCount();
			for (AlignmentRow row : input.getAlignment())
			{
				if (counter++ == CHECK_FREQUENCY)
				{
					checkForCancellation();
					counter = 0;
					updateStatus(done, total);
				}
				done++;
				double curVal = 0.0;
				if (MAX_SIMILARITY.equals(mode)){curVal = row.getMaxSimilarity();}
				else if (MEAN_SIMILARITY.equals(mode)){curVal = row.getMeanSimilarity();}
				System.out.println(minValue);
				if (curVal >= minValue)
				{
					als.add(row);
				}
			}
			Alignment filtered = new Alignment(input.getColumnNames(), input.getParameters(), input.getAligner());
			filtered.addAll(als);
			filtered.addAll(QuantMassOnes);
			return filtered;
		}

		@Override
		public String getName()
		{
			return SimilarityFilter.this.getName();
		}
	}*/
	
}
