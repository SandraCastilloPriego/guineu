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
package guineu.modules.mylly.gcgcaligner.process;


import guineu.modules.mylly.gcgcaligner.alignment.Aligner;
import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.alignment.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.filter.FilterFactory;
import guineu.modules.mylly.gcgcaligner.filter.FilterFunction;
import guineu.modules.mylly.gcgcaligner.filter.MapFunction;
import guineu.modules.mylly.gcgcaligner.filter.NameFilter;
import guineu.modules.mylly.gcgcaligner.gui.StatusReporter;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import guineu.modules.mylly.gcgcaligner.scorer.SpectrumDotProd;
import guineu.modules.mylly.helper.Helper;

import java.awt.Frame;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ProcessExecutor implements StatusReporter
{
	private List<StatusReportingProcess<InputSet, InputSet>> _preprocessing;
	private Aligner _aligner;
	private List<StatusReportingProcess<Alignment, Alignment>> _postprocessing;

	private List<StatusReportingProcess<?, ?>> _processes;

	private volatile boolean _cancelled;
	private volatile StatusReportingProcess<?, ?> _curProcess;
	private Collection<PropertyChangeListener> _listeners;

	public ProcessExecutor()
	{
		_preprocessing = new ArrayList<StatusReportingProcess<InputSet, InputSet>>();
		_aligner = null;
		_postprocessing = new ArrayList<StatusReportingProcess<Alignment, Alignment>>();

		_processes = new ArrayList<StatusReportingProcess<?, ?>>();
		_listeners = new HashSet<PropertyChangeListener>();

		_cancelled = false;
	}
	
	public void addStatusChangeListener(StatusChangeListener listener)
	{
		if (listener != null)
		{
			for (StatusReportingProcess<?, ?> p : _processes)
			{
				p.addStatusChangeListener(listener);
			}
//			for(StatusReportingProcess<InputSet, InputSet> preFilter : _preprocessing)
//			{
//				preFilter.addStatusChangeListener(listener);
//			}
//			_aligner.addStatusChangeListener(listener);
//			for (StatusReportingProcess<Alignment, Alignment> postFilter : _postprocessing)
//			{
//				postFilter.addStatusChangeListener(listener);
//			}
		}
	}

	public void removeStatusChangeListener(StatusChangeListener listener)
	{
		if (listener != null)
		{
			for (StatusReportingProcess<?, ?> p : _processes)
			{
				p.removeStatusChangeListener(listener);
			}
//			for(StatusReportingProcess<InputSet, InputSet> preFilter : _preprocessing)
//			{
//				preFilter.removeStatusChangeListener(listener);
//			}
//			_aligner.removeStatusChangeListener(listener);
//			for (StatusReportingProcess<Alignment, Alignment> postFilter : _postprocessing)
//			{
//				postFilter.removeStatusChangeListener(listener);
//			}
		}
	}

	public void addPreProcess(StatusReportingModule<InputSet, InputSet> preprocess)
	{
		_preprocessing.add(preprocess);
		addProcess(preprocess);
	}

	/**
	 * 
	 * @param aligner If null, throws NullPointerException
	 */
	public void addAligner(Aligner aligner)
	{
		if (aligner == null)
		{
			throw new NullPointerException("AbstractAligner argument was null");
		}
		addProcess(aligner);
		this._aligner = aligner;
	}

	public void addPostProcess(StatusReportingModule<Alignment, Alignment> postProcess)
	{
		_postprocessing.add(postProcess);
		addProcess(postProcess);
	}

	public void cancel()
	{
		_cancelled = true;
		StatusReportingProcess<?, ?> currentProcess = _curProcess;
		if (currentProcess != null){currentProcess.cancel(50L);}
	}
	
//	public StatusObject getStatus()
//	{
//		return new StatusObject(_curProcess);
//	}

	@SuppressWarnings("unchecked")
	synchronized public Object process(Object input)
	{
		Object result = input;
		for (StatusReportingProcess m : _processes)
		{
			checkForCancellation();
			_curProcess = m;
			result = m.map(result);
		}
		checkForCancellation();
		return result;
//		InputSet preprocessed = data;
//		for (MapFunction<InputSet, InputSet> filter : _preprocessing)
//		{
//		preprocessed = filter.map(preprocessed);
//		checkForCancellation();
//		}
//		checkForCancellation();
//		Alignment alignmentResult = _aligner.map(preprocessed);
//		Alignment postProcessed = alignmentResult;
//		for (MapFunction<Alignment, Alignment> filter : _postprocessing)
//		{
//		postProcessed = filter.map(postProcessed);
//		checkForCancellation();
//		}
//		checkForCancellation();
//		return postProcessed;
	}

	private void checkForCancellation()
	{
		if (_cancelled)
		{
			_curProcess = null;
			throw new CancellationException("Process was cancelled");
		}
	}

	@SuppressWarnings("unchecked")
	public void addProcess(StatusReportingProcess p)
	{
		_processes.add(p);
//		Method[] methods = p.getClass().getMethods();
//		for (Method m : methods)
//		{
//		if (m.getName() == "map")
//		{
//		Class[] params = m.getParameterTypes();
//		Class retType = m.getReturnType();
//		if (retType == InputSet.class && params.length == 1)
//		{
//		if (params[0] == InputSet.class) //Pre-process
//		{
//		addPreProcess((StatusReportingModule<InputSet, InputSet>) p);
//		return;
//		}
//		}
//		else if (retType == Alignment.class && params.length == 1)
//		{
//		if (params[0] == Alignment.class) //Post-process
//		{
//		addPostProcess((StatusReportingModule<Alignment, Alignment>)p);
//		return;
//		}
//		else if (p instanceof Aligner)
//		{
//		addAligner((Aligner) p);
//		return;
//		}
//		}
//		}
//		}
//		throw new IllegalArgumentException("Input process was of wrong type");
	}

	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Pre-filters:\n");
		int preFilterCount = 0;
		for (MapFunction<InputSet, InputSet> prefilter : _preprocessing)
		{
			if (prefilter != null)
			{
				sb.append(prefilter.getName()).append('\n');
				preFilterCount++;
			}
		}
		if (preFilterCount == 0){sb.append("NONE\n");}

		sb.append("Aligner:\n").append(_aligner.getName()).append('\n');

		sb.append("Post-filters:\n");
		int postFilterCount = 0;
		for (MapFunction<Alignment, Alignment> postfilter : _postprocessing)
		{
			if (postfilter != null)
			{
				sb.append(postfilter.getName()).append('\n');
				postFilterCount++;
			}
		}
		if (postFilterCount == 0){sb.append("NONE\n");}

		return sb.toString();
	}

	public static void main(String[] args)
	{
		test();
	}

	public static StatusReportingModule<InputSet, InputSet> getPrefilter()
	{
		FilterFunction<GCGCDatum> fnFilter = null;

		{
			ArrayList<String> badNames = new ArrayList<String>();
			badNames.add("UNKNOWN");
			badNames.add("keke");
			badNames.add("muu");

			fnFilter = new NameFilter(badNames, "hand-made filter");
		}

		final StatusReportingProcess<InputSet, InputSet> f = FilterFactory.createPreFilter(fnFilter);
		StatusReportingModule<InputSet, InputSet> f2 = new AbstractStatusReportingModule<InputSet, InputSet>()
		{

			@Override
			protected InputSet actualMap(InputSet input)
			throws CancellationException
			{
				return f.map(input);
			}

			protected void doCancellingActions()
			{
				f.cancel(50);
			}

			public void askParameters(Frame parentWindow)
			throws ParameterInputException
			{}

			public boolean isConfigurable()
			{
				return false;
			}

			public String getName()
			{
				return f.getName();
			}
		};

		return f2;
	}

	public static AbstractStatusReportingModule<Alignment, Alignment> getPostFilter(final int minNumberOfPeaks)
	{
		//The ugliest thing I've probably ever done DOWN BELOW
		return new AbstractStatusReportingModule<Alignment, Alignment>()
		{
			@Override
			public String getName()
			{
				if (mapper == null){createMapper();}
				return mapper.getName();
			}
			
			private void createMapper()
			{
				mapper = FilterFactory.
				createPostFilter(new FilterFunction<AlignmentRow>()
				                 {

					public boolean exclude(
							AlignmentRow obj)
					{
						return !include(obj);
					}

					public boolean include(
							AlignmentRow obj)
					{
						return obj.nonNullPeakCount() >= minNumberOfPeaks;
					}

					public String getName()
					{
						return "Filter out if less than " + minNumberOfPeaks + " peaks";
					}

					public AlignmentRow map(
							AlignmentRow obj)
					{
						if (include(obj))
						{
							return obj;
						}
						return null;
					}

				                 });
			}

			public boolean isConfigurable()	{return false;}

			@Override
			public AbstractStatusReportingModule<Alignment, Alignment> clone()
			{
				return super.clone();
			}

			public void askParameters(Frame parentWindow) throws ParameterInputException{}

			@Override
			protected Alignment actualMap(Alignment input) throws CancellationException
			{
				if (mapper == null)
				{
					createMapper();
				}
				return mapper.map(input);
			}

		};
	}
	
	

	public static void test()
	{
		ProcessExecutor pe = new ProcessExecutor();
		try
		{
			StatusReportingModule<InputSet, InputSet> preFilter = getPrefilter();
			Aligner al = new ScoreAligner(new SpectrumDotProd()).getInstance(null, new AlignmentParameters());
			StatusReportingModule<Alignment, Alignment> postFilter = getPostFilter(4);
			File file = new File ("D:\\Data\\Catharanthus\\bench");
			File[] files = file.listFiles();
			InputSet s = new InputSet(GCGCData.loadfiles("\t", true, true, false, files));


			pe.addPreProcess(getPrefilter());
			pe.addAligner(al);
			pe.addPostProcess(getPostFilter(4));


//			pe.addProcess(preFilter);
//			pe.addProcess(al);
//			pe.addProcess(postFilter);


			Alignment output = (Alignment) pe.process(s);
			System.out.printf("%s\n", Helper.statistics(output));
		} catch (IOException e)
		{}
	}
}
