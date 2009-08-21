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
package guineu.modules.mylly.gcgcaligner.gui;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import guineu.modules.mylly.gcgcaligner.Constants;


import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.alignment.DistValue;
import guineu.modules.mylly.gcgcaligner.alignment.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.datastruct.Pair;
import guineu.modules.mylly.gcgcaligner.filter.AlignmentRowFilter;
import guineu.modules.mylly.gcgcaligner.filter.AlkaneRTICorrectModule;
import guineu.modules.mylly.gcgcaligner.filter.LinearNormalizer;
import guineu.modules.mylly.gcgcaligner.filter.NameFilterModule;
import guineu.modules.mylly.gcgcaligner.filter.NamePostFilter;
import guineu.modules.mylly.gcgcaligner.filter.PeakCountFilterModule;
import guineu.modules.mylly.gcgcaligner.filter.SimilarityFilter;
import guineu.modules.mylly.gcgcaligner.filter.SinglingFilter;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import guineu.modules.mylly.gcgcaligner.gui.tables.ResultWindow;
import guineu.modules.mylly.gcgcaligner.process.ProcessExecutor;
import guineu.modules.mylly.gcgcaligner.process.StatusReportingModule;
import guineu.modules.mylly.helper.Functor;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

public class MainWindow extends JFrame
{
	private static final String	EXIT_PROGRAM		= "Exit program";
	private static final String	OPEN_FILES			= "Open files";
	private static final String	CLOSE_FILES			= "Close files";
	private static final String	SAVE_ALIGN			= "Save alignment";
	private static final String	SHOW_ALIGN			= "Select alignment";
	private static final String	CALC_DEVS			= "Calculate deviations";
	private static final String	SHOW_PEAKS			= "Show peaks in files";
	private static final String	COUNT_FILTER_ALIGN	= "Filter alignment by peak count";
	private static final String	CLOSE_ALIGN			= "Close alignments";
	private static final String	UNIQ_POST			= "Leave only uniques filter";
	private static final String	NORMALIZE			= "Normalize peaks";
	private static final String	SIMILARITY_FILTER	= "Filter by similarity";
	private static final String NAME_FILTER 		= "Filter by peak name";

	private Map<Object, File>									contextToDirectory;
	private Map<String, Action>									actions;

	private List<StatusReportingModule<InputSet, InputSet>>		preProcessors;
	private List<StatusReportingModule<InputSet, Alignment>>	alignerList;
	private List<StatusReportingModule<Alignment, Alignment>>	postProcessors;

	private JSplitPane											horiSplitter;
	private JSplitPane											vertiSplitter;
	private ObjectList<GCGCData>								filesPane;
	private AlignmentPane										alignmentPane;
	private ResultWindow										resultsPane;

	private JMenuBar											menubar;

	// Filemenu
	private JMenu												filemenu;
	private JMenuItem											fileopen;
	private JMenuItem											close;
	private JMenuItem											saveResults;
	private JMenuItem											closeFiles;

	// alignment menu
	private JMenu												alignmentmenu;
	private JMenuItem											batchProcess;
	private JMenuItem											showAlignmentresults;
	private JMenuItem											showPeaksInFiles;
	private JCheckBoxMenuItem									filterClassifiedCheckBox;

	// Filtering menu
	private JMenu												filteringMenu;
	private JMenuItem											filterAlignment;
	private JMenuItem											loadMainPeaks;

	private File												lastSaveDirectory;
	private File												lastOpenDirectory;
	private WeakReference<Alignment>							currentAlignment;

	public MainWindow()
	{
		this(true);
	}

	public MainWindow(boolean useNativeLF)
	{
		super(Constants.name);

		actions = new HashMap<String, Action>();
		createActions();

		contextToDirectory = new WeakHashMap<Object, File>();
		createProcessLists();

		createGUI(useNativeLF);
	}

	public void displayErrorDialog(Exception e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		displayErrorDialog(sw.toString());
	}

	public void displayErrorDialog(String s)
	{
		javax.swing.JOptionPane
		.showMessageDialog(
		                   this,
		                   s,
		                   "Error",
		                   javax.swing.JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * 
	 * @param context
	 * @return Chosen file in case user didn't abort he choosing operation,
	 *         <code>null</code> otherwise.
	 */
	public File launchSingleFileChooser(Object context)
	{
		File chosen = null;
		File lastDir = contextToDirectory.get(context);
		JFileChooser chooser = new JFileChooser(lastDir);
		int retval = chooser.showOpenDialog(this);
		if (retval == JFileChooser.APPROVE_OPTION)
		{
			chosen = chooser.getSelectedFile();
			if (chooser.getCurrentDirectory() != null)
			{
				contextToDirectory.put(context, chooser.getCurrentDirectory());
			}
		}
		return chosen;
	}

	@SuppressWarnings("unchecked")
	private void createProcessLists()
	{
		preProcessors = new ArrayList<StatusReportingModule<InputSet, InputSet>>();
		alignerList = new ArrayList<StatusReportingModule<InputSet, Alignment>>();
		postProcessors = new ArrayList<StatusReportingModule<Alignment, Alignment>>();

		Class[] preProcessorClasses = { AlkaneRTICorrectModule.class,
				NameFilterModule.class };
		Class[] alignerClasses = { ScoreAligner.class };
		Class[] postProcessorClasses = { PeakCountFilterModule.class,
				SinglingFilter.class, SimilarityFilter.class, NamePostFilter.class};
		try
		{
			for (Class c : preProcessorClasses)
			{
				preProcessors.add((StatusReportingModule<InputSet, InputSet>) c.newInstance());
			}
			for (Class c : alignerClasses)
			{
				alignerList.add((StatusReportingModule<InputSet, Alignment>) c.newInstance());
			}
			for (Class c : postProcessorClasses)
			{
				postProcessors.add((StatusReportingModule<Alignment, Alignment>) c.newInstance());
			}
		} catch (InstantiationException e)
		{
			displayErrorDialog(e);
		} catch (IllegalAccessException e)
		{
			displayErrorDialog(e);
		}
	}

	private void createGUI(boolean useNativeLF)
	{
		int wHeight = 600;
		int wWidth = 800;
		// left side occupies only 35% of screen
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setDefaultLookAndFeelDecorated(useNativeLF);
		setSize(wWidth, wHeight);

		// Create left side

		// Upper part, the open files
		ListSelectionModel fileSelector = new DefaultListSelectionModel();
		fileSelector
		.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		filesPane = new ObjectList<GCGCData>();
		filesPane.setVisible(true);

		ArrayList<Action> filePopupActions = new ArrayList<Action>();
		filePopupActions.add(actions.get(SHOW_PEAKS));
		filePopupActions.add(null);
		filePopupActions.add(actions.get(CLOSE_FILES));
		filesPane.createPopupMenu(filePopupActions);

		// Lower part, finished alignments

		alignmentPane = new AlignmentPane();
		alignmentPane.setVisible(true);

		ArrayList<Action> alignmentPopupActions = new ArrayList<Action>();
		alignmentPopupActions.add(actions.get(SHOW_ALIGN));
		alignmentPopupActions.add(actions.get(CALC_DEVS));
		alignmentPopupActions.add(actions.get(SAVE_ALIGN));
		alignmentPopupActions.add(null);
		alignmentPopupActions.add(actions.get(CLOSE_ALIGN));

		alignmentPane.createPopupMenu(alignmentPopupActions);

		// Add upper and lower side to splitpane
		vertiSplitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, false,
		                               filesPane, alignmentPane);

		// Create Result side, the right side
		resultsPane = new ResultWindow();
		resultsPane.setVisible(true);

		horiSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		final double LEFT_SIDE_SIZE = 0.35;
		horiSplitter.setDividerLocation(LEFT_SIDE_SIZE);
		horiSplitter.setLeftComponent(vertiSplitter);
		horiSplitter.setRightComponent(resultsPane);

		add(horiSplitter);

		horiSplitter.setVisible(true);

		vertiSplitter.setDividerLocation((int) (wHeight * 0.5));

		setJMenuBar(createMenuBar());
	}

	private JMenuBar createMenuBar()
	{
		menubar = new JMenuBar();

		// Create filemenu
		filemenu = new JMenu("File");
		filemenu.setMnemonic(KeyEvent.VK_F);

		closeFiles = new JMenuItem(actions.get(CLOSE_FILES));
		fileopen = new JMenuItem(actions.get(OPEN_FILES));
		fileopen.setMnemonic(KeyEvent.VK_O);
		close = new JMenuItem(actions.get(EXIT_PROGRAM));
		close.setMnemonic(KeyEvent.VK_X);
		saveResults = new JMenuItem(actions.get(SAVE_ALIGN));
		fileopen.setMnemonic(KeyEvent.VK_S);
		filemenu.add(fileopen);
		filemenu.add(saveResults);
		filemenu.add(closeFiles);
		filemenu.addSeparator();
		filemenu.add(close);
		// finished creating filemenu

		// Create alignment menu
		alignmentmenu = new JMenu("Alignment");
		alignmentmenu.setMnemonic(KeyEvent.VK_A);

		batchProcess = new JMenuItem("Process files");
		showAlignmentresults = new JMenuItem(actions.get(SHOW_ALIGN));
		showPeaksInFiles = new JMenuItem(actions.get(SHOW_PEAKS));

//		useFilterCheckBox = new JCheckBoxMenuItem("Use Filter : "
//		+ ((currentFilter == null) ? "No filter loaded"
//		: currentFilter.getName()), false);
		filterClassifiedCheckBox = new JCheckBoxMenuItem(
		"Filter peaks with some classification");
		filterClassifiedCheckBox.setSelected(true);

		alignmentmenu.add(batchProcess);
		alignmentmenu.add(showAlignmentresults);
		alignmentmenu.add(showPeaksInFiles);
//		alignmentmenu.add(useFilterCheckBox);
		alignmentmenu.add(filterClassifiedCheckBox);
		// Finished creating alignment menu

		// Create filtering menu
		filteringMenu = new JMenu("Choose postfilters");
		filteringMenu.setMnemonic(KeyEvent.VK_C);

		filterAlignment = new JMenuItem(actions.get(COUNT_FILTER_ALIGN));
//		loadFilter = new JMenuItem(actions.get(LOAD_NAME_FILTER));
		loadMainPeaks = new JMenuItem(actions.get(CALC_DEVS));

		filteringMenu.add(filterAlignment);
//		filteringMenu.add(loadFilter);
		filteringMenu.add(loadMainPeaks);
		filteringMenu.add(new JMenuItem(actions.get(UNIQ_POST)));
		filteringMenu.add(new JMenuItem(actions.get(NORMALIZE)));
		filteringMenu.add(new JMenuItem(actions.get(SIMILARITY_FILTER)));
		filteringMenu.add(new JMenuItem(actions.get(NAME_FILTER)));
		// Finished creating filtering menu

		//About menu

		JMenu aboutMenu = new JMenu("About");
		JMenuItem licenseItem = new JMenuItem("License");
		JMenuItem copyrightItem = new JMenuItem("Copyright");

		licenseItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showLicense();
			}	
		});
		copyrightItem.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				showCopyright();
			}	
		});

		aboutMenu.add(licenseItem);
		aboutMenu.add(copyrightItem);

		// Add submenus to menubar
		menubar.add(filemenu);
		menubar.add(alignmentmenu);
		menubar.add(filteringMenu);

		menubar.add(Box.createHorizontalGlue());
		menubar.add(aboutMenu);

		addActionListeners();
		return menubar;
	}

	private void exitProgram(int errorCode)
	{
		System.exit(errorCode);
	}

	private Alignment getCurrentAlignment()
	{
		Alignment cur;
		if ((currentAlignment != null)
				&& (cur = currentAlignment.get()) != null)
		{
			return cur;
		} else
		{
			throw new IllegalStateException("No alignment selected");
		}
		// return alignmentPane.getSelectedAlignment();
		// if (currentDisplayMode == displayMode.ALIGNMENT)
		// {
		// return alignmentPane.getSelectedAlignment();
		// // return resultsPane.getCurrentAlignment();
		// }

	}

	private void setRightTable(ResultWindow w)
	{
		int loc = horiSplitter.getDividerLocation();
		horiSplitter.setRightComponent(w);
		horiSplitter.setDividerLocation(loc);
	}

	private void showAlignment()
	{
		try
		{
			currentAlignment = new WeakReference<Alignment>(
					alignmentPane.getSelectedAlignment());
			setRightTable(alignmentPane.getResultWindow(getCurrentAlignment()));
		} catch (IllegalStateException e)
		{
			displayErrorDialog(e.getLocalizedMessage());
		}
		// List<Alignment> chosen = alignmentPane.getSelected();
		// if (chosen.size() == 1)
		// {
		// Alignment cur = chosen.get(0);

		// // resultsPane.showResults(cur);
		// currentDisplayMode = displayMode.ALIGNMENT;
		// }
		// else if (chosen.size() == 0)
		// {
		// displayErrorDialog("No alignment selected");
		// }
		// else
		// {
		// displayErrorDialog("More than one alignment selected");
		// }
	}

	private File openOneFile()
	{
		List<File> selectedFiles = launchFileChooser(false, false);
		if (selectedFiles == null || selectedFiles.size() == 0)
		{
			return null;
		} else
		{
			return selectedFiles.get(0);
		}
	}

	private void readInputFiles()
	{
		List<File> files = launchFileChooser(false, true);
		if (files != null && files.size() > 0)
		{
			//DefaultGCGCFileReader reader = new DefaultGCGCFileReader("\t");
			try
			{
			//	reader.askParameters(MainWindow.this);
			} catch (ParameterInputException e)
			{
				return; // User cancelled the dialog
			}
			ProcessExecutor pe = new ProcessExecutor();
			//pe.addProcess(reader);
			ResultsReadyListener<List<GCGCData>, List<GCGCData>> list = 
				new ResultsReadyListener<List<GCGCData>, List<GCGCData>>(
						wrapString("Files read"), wrapString("File reading canceled"),
						new Functor<List<GCGCData>>()
						{
							public void execute(List<GCGCData> input)
							{
								filesPane.addAll(input);
							}
						});
			runProcessExecutor(pe, files, list);
		}
	}

	private List<File> launchFileChooser(boolean save, boolean openMultipleFiles)
	{
		List<File> files = new ArrayList<File>();
		if (save)
		{
			JFileChooser chooser;
			if (lastSaveDirectory != null)
			{
				chooser = new JFileChooser(lastSaveDirectory);
			} else
			{
				chooser = new JFileChooser();
			}
			int returnval = chooser.showSaveDialog(this);
			if (returnval == JFileChooser.APPROVE_OPTION)
			{
				File saveFile = chooser.getSelectedFile();
				if (saveFile != null)
				{
					lastSaveDirectory = saveFile.getParentFile();
				}
				files.add(saveFile);
			}
		} else
		{
			JFileChooser chooser;
			if (lastOpenDirectory != null)
			{
				chooser = new JFileChooser(lastOpenDirectory);
			} else
			{
				chooser = new JFileChooser();
			}
			chooser.setMultiSelectionEnabled(openMultipleFiles);
			int returnval = chooser.showOpenDialog(this);
			if (returnval == JFileChooser.APPROVE_OPTION)
			{
				if (chooser.getCurrentDirectory() != null)
				{
					lastOpenDirectory = chooser.getCurrentDirectory();
				}
				if (openMultipleFiles)
				{
					files
					.addAll(java.util.Arrays
					        .asList(chooser
					                .getSelectedFiles()));
				} else
				{
					files.add(chooser.getSelectedFile());
				}
			}
		}
		return files;
	}

	private void addActionListeners()
	{
		batchProcess.addActionListener(batchProcessListener());
	}

	@SuppressWarnings("serial")
	private void createActions()
	{
		actions.put(EXIT_PROGRAM, new AbstractAction(EXIT_PROGRAM)
		{
			public void actionPerformed(ActionEvent e)
			{
				exitProgram(0);
			}
		});
		actions.put(OPEN_FILES, new AbstractAction(OPEN_FILES)
		{
			public void actionPerformed(ActionEvent e)
			{
				readInputFiles();
			}
		});
		actions.put(SAVE_ALIGN, new AbstractAction(SAVE_ALIGN)
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Alignment cur = getCurrentAlignment();
					List<File> files = launchFileChooser(true, true);
					if (files != null && files.size() > 0)
					{
						File saveFile = files.get(0);
						try
						{
							cur.SaveToFile(saveFile, "\t");
						} catch (IOException exception)
						{
							if (exception instanceof FileNotFoundException)
							{
								displayErrorDialog("Couldn't open file "
								                   + saveFile);
							} else
							{
								displayErrorDialog(exception);
							}
						}
					}
				} catch (IllegalStateException ex)
				{
					displayErrorDialog(ex.getLocalizedMessage());
				}
			}
		});
		actions.put(SHOW_ALIGN, new AbstractAction(SHOW_ALIGN)
		{
			public void actionPerformed(ActionEvent e)
			{
				showAlignment();
			}
		});
		actions.put(CLOSE_FILES, new AbstractAction(CLOSE_FILES)
		{
			public void actionPerformed(ActionEvent e)
			{
				filesPane.removeSelected();
				currentAlignment = null;
				resultsPane.clearUp();
				setRightTable(resultsPane);
			}
		});
		actions.put(CALC_DEVS, new AbstractAction(CALC_DEVS)
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					Alignment cur = getCurrentAlignment();
					List<Pair<List<String>, Double>> l = readRepresentatives();
					if (cur == null){throw new IllegalStateException("No alignment selected");}
					alignmentPane.addObject(findRepresentavies(l, cur));
					// perhapsShowAlignment();
				} catch (IllegalStateException ex)
				{
					displayErrorDialog(ex.getLocalizedMessage());
				}
			}
		});
		actions.put(SHOW_PEAKS, new AbstractAction(SHOW_PEAKS)
		{
			public void actionPerformed(ActionEvent e)
			{
				List<GCGCData> selected = filesPane.getSelected();
				if (selected != null && selected.size() > 0)
				{
					resultsPane.showFiles(selected);
					setRightTable(resultsPane);
				} else
				{
					displayErrorDialog("No files selected!");
				}
			}
		});
		actions.put(COUNT_FILTER_ALIGN, new AbstractAction(COUNT_FILTER_ALIGN)
		{
			public void actionPerformed(ActionEvent e)
			{
				applyMap(PeakCountFilterModule.class);
				// try
				// {
				// alignmentPane.addObject(applyMap(new
				// PeakCountFilterModule()));
				// // Alignment cur = getCurrentAlignment();
				// // PeakCountFilterModule peakFilter = new
				// PeakCountFilterModule();
				// // peakFilter.askParameters(MainWindow.this);
				// // alignmentPane.addObject(peakFilter.map(cur));
				// // perhapsShowAlignment();
				// }
				// catch (IllegalStateException ex)
				// {
				// displayErrorDialog(ex.getLocalizedMessage());
				// }
			}
		});
		actions.put(CLOSE_ALIGN, new AbstractAction(CLOSE_ALIGN)
		{
			public void actionPerformed(ActionEvent e)
			{
				List<Alignment> selected = alignmentPane.getSelected();
				for (Alignment al : selected)
				{
					if (currentAlignment != null
							&& currentAlignment.get() == al)
					{
						resultsPane.clearUp();
						setRightTable(resultsPane);
					}
					alignmentPane.removeObject(al);
				}
			}
		});
		actions.put(UNIQ_POST, new AbstractAction(UNIQ_POST)
		{
			public void actionPerformed(ActionEvent e)
			{
				applyMap(SinglingFilter.class);
				// try
				// {
				// alignmentPane.addObject(applyMap(new SinglingFilter()));
				// // Alignment cur = getCurrentAlignment();
				// // AbstractStatusReportingModule<Alignment, Alignment> filter
				// = new SinglingFilter();
				// // filter.askParameters(MainWindow.this);
				// // alignmentPane.addObject(filter.map(cur));
				// // perhapsShowAlignment();
				// }
				// catch (IllegalStateException ex)
				// {
				// displayErrorDialog(ex.getLocalizedMessage());
				// }
				// catch (ParameterInputException ex2)
				// {
				// //Do nothing as we were cancelled
				// }
			}
		});
		actions.put(NORMALIZE, new AbstractAction(NORMALIZE)
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					List<AlignmentRow> standards = alignmentPane.getResultWindow(getCurrentAlignment())
					.getSelectedAlignments();
					LinearNormalizer norm = new LinearNormalizer(standards);
					alignmentPane.addObject(applyMap(norm, true));
				} catch (IllegalArgumentException ex)
				{
					displayErrorDialog(ex.getLocalizedMessage());
				}
				catch (IllegalStateException ex)
				{
					displayErrorDialog(ex.getLocalizedMessage());
				}
			}
		});
		actions.put(SIMILARITY_FILTER, new AbstractAction(SIMILARITY_FILTER)
		{
			public void actionPerformed(ActionEvent e)
			{
				applyMap(SimilarityFilter.class);
			}
		});
		
		actions.put(NAME_FILTER, new AbstractAction(NAME_FILTER)
		{
			public void actionPerformed(ActionEvent e)
			{
				applyMap(NamePostFilter.class);
			}
		});

		actions = java.util.Collections.unmodifiableMap(actions);
	}

	private void applyMap(
			Class<? extends StatusReportingModule<Alignment, Alignment>> mapping)
	{
		try
		{
			StatusReportingModule<Alignment, Alignment> mapper = mapping.newInstance();
			alignmentPane.addObject(applyMap(mapper));
		} catch (InstantiationException e)
		{
			displayErrorDialog("Could not instantiate "
			                   + mapping.getCanonicalName()
			                   + " because it is abstract/interface.");
		} catch (IllegalAccessException e)
		{
			displayErrorDialog("Could not instantiate"
			                   + mapping.getCanonicalName()
			                   + " because no public zero-arg constructor was found");
		} catch (IllegalStateException ex)
		{
			displayErrorDialog(ex.getLocalizedMessage());
		} catch (ParameterInputException ex2)
		{
			// Do nothing as we were cancelled
		}
	}

	private Alignment applyMap(StatusReportingModule<Alignment, Alignment> f)
	{
		return applyMap(f, false);
	}

	private Alignment applyMap(StatusReportingModule<Alignment, Alignment> f,
			boolean appliesToSelected)
	{
		Alignment cur = getCurrentAlignment();
		if (cur == null){throw new IllegalStateException("No alignment selected!");}
		List<AlignmentRow> selectedOnes = null;		
		if (!appliesToSelected)
		{
			//We take the peaks that are selected in the current table
			//And create a filter that filters those peaks out.
			selectedOnes = alignmentPane.getResultWindow(getCurrentAlignment())
			.getSelectedAlignments();
			AlignmentRowFilter filter = new AlignmentRowFilter(selectedOnes);
			cur = filter.map(cur); //Filter the selected alignments out
		}
		if (f.isConfigurable())
		{
			f.askParameters(this);
		}
		Alignment modified = f.map(cur);
		if (!appliesToSelected) //If we wanted to keep those selected ones, add them to results
		{
			modified.addAll(selectedOnes);			
		}
		return modified;
	}

	private List<Pair<List<String>, Double>> readRepresentatives()
	{
		File f = openOneFile();
		if (f != null)
		{
			try
			{
			/*	List<Pair<List<String>, Double>> l = helper.Helper
				.makeRepresentativeList(
				                        f,
				"\t");
				return l;*/
			} catch (Exception e)
			{
				displayErrorDialog("Opening file " + f.getName() + " failed:\n"
				                   + e.getMessage());
			}
		}
		return new ArrayList<Pair<List<String>, Double>>();
	}

	private Alignment findRepresentavies(
			List<Pair<List<String>, Double>> peaks, Alignment al)
	{
		Map<Integer, Double> representatives = new HashMap<Integer, Double>();

		for (Pair<List<String>, Double> idealPeak : peaks)
		{
			int i = 0;
			Set<String> names = new HashSet<String>(idealPeak.getFirst());
			double rti = idealPeak.getSecond();

			for (AlignmentRow ar : al.getAlignment())
			{
				if (names.contains(ar.getName()))
				{
					double diff = Math.abs(rti - ar.getMeanRTI());
					representatives.put(i, diff);
				}
				i++;
			}
		}

		Alignment newAl = new Alignment(al.getColumnNames(),
		                                al.getParameters(), al.getAligner());
		int i = 0;
		for (AlignmentRow ar : al.getAlignment())
		{
			Double diff = representatives.get(i++);
			if (diff != null)
			{
				newAl.addAlignmentRow(ar.setDistValue(new DistValue(diff)));
			} else
			{
				newAl.addAlignmentRow(ar);
			}
		}
		return newAl;
	}

	private InputSet getSelectedFiles()
	{
		return new InputSet(filesPane.getSelected());
	}

	private <T, Y> List<StatusReportingModule<T, Y>> deepCopyList(List<StatusReportingModule<T, Y>> list)
	{
		List<StatusReportingModule<T, Y>> copy = new ArrayList<StatusReportingModule<T, Y>>();
		for (StatusReportingModule<T, Y> m : list)
		{
			copy.add(m.clone());
		}
		return copy;
	}

	private ActionListener batchProcessListener()
	{
		return new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final String propertyChangedString = "batch process ready";
				/*final FilterChooserDialog processChoosingDialog =
					new FilterChooserDialog(MainWindow.this,
					                        deepCopyList(preProcessors), deepCopyList(alignerList),
					                        deepCopyList(postProcessors), propertyChangedString);*/

				final Functor<Alignment> successAction = new Functor<Alignment>()
				{
					public void execute(
							Alignment input)
					{
						if (input != null)
						{
							alignmentPane.addObject(input);
						}
					}
				};

				PropertyChangeListener listener = new PropertyChangeListener()
				{
					public void propertyChange(
							PropertyChangeEvent evt)
					{
						/*if (evt.getSource() == processChoosingDialog
								&& propertyChangedString.equals(evt.getPropertyName()))
						{
							ProcessExecutor pe = (ProcessExecutor) evt.getNewValue();
							InputSet input = getSelectedFiles();
							if (input.size() > 1)
							{
								ResultsReadyListener<Alignment, Alignment> resultsListener = 
									new ResultsReadyListener<Alignment, Alignment>(
											wrapString("alignment has result"),
											wrapString("alignment got canceled"),
											successAction);
								runProcessExecutor(pe, input, resultsListener);
							} else
							{
								displayErrorDialog("You've chosen only "
								                   + input.size()
								                   + " file(s).\n"
								                   + "Choose at least 2 files to align");
							}
						}*/
					}
				};

				//processChoosingDialog.addPropertyChangeListener(listener);
				//processChoosingDialog.setVisible(true);
			}
		};
	}

	private String wrapString(String str)
	{
		return getClass().getCanonicalName() + ":"
		+ System.identityHashCode(this) + ":" + str;
	}

	private <T, Y> void runProcessExecutor(ProcessExecutor e, Object input,
			ResultsReadyListener<T, Y> listener)
	{
		final ProcessExecutorGui execStatusWindow = new ProcessExecutorGui(this, e);
		execStatusWindow.setVisible(true);
		execStatusWindow.setModal(true);
		execStatusWindow.runProcess(listener, listener.getReadyString(),
		                            listener.getCancelString(), input);
	}

	private void showLicense()
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				JEditorPane textPane = new JEditorPane("text/html", Constants.GPL2_LICENSE);
				textPane.setEditable(false);

				JScrollPane scroller = new JScrollPane(textPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
				textPane.setSize(600, 700);
				scroller.setSize(600, 700);

				JDialog dlg = new JDialog(MainWindow.this, "License", true);
				dlg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dlg.add(scroller);

				dlg.setLocationRelativeTo(MainWindow.this);
				dlg.setSize(600, 700);
				dlg.setVisible(true);
			}
		});
	}

	private void showCopyright()
	{
		JOptionPane.showMessageDialog(this, "MYLLY\n" +
		                              "Copyright 2007-2008 VTT\n" +
		                              "Released under GPL v2\n" +
		                              "Version " + Constants.VERSION,
		                              "About", 
		                              JOptionPane.INFORMATION_MESSAGE);
	}

	private static class ResultsReadyListener<T, Y> implements
	PropertyChangeListener
	{

		private final String		cancelString;
		private final String		readyString;
		private final Functor<T>	resultF;
		private final Functor<Y>	cancelF;

		public ResultsReadyListener(String readyString, String cancelString,
				Functor<T> resultF)
		{
			this(readyString, cancelString, resultF, null);
		}

		public ResultsReadyListener(String readyString, String cancelString,
				Functor<T> resultF, Functor<Y> cancelF)
		{
			this.readyString = readyString;
			this.cancelString = cancelString;
			this.resultF = resultF;
			this.cancelF = cancelF;
		}

		public String getCancelString()
		{
			return cancelString;
		}

		public String getReadyString()
		{
			return readyString;
		}

		@SuppressWarnings("unchecked")
		public void propertyChange(PropertyChangeEvent evt)
		{
			Object result = null;
			if (readyString != null
					&& readyString.equals(evt.getPropertyName()))
			{
				result = evt.getNewValue();
				if (resultF != null)
				{
					resultF.execute((T) result);
				}
			} else if (cancelString != null
					&& cancelString.equals(evt.getPropertyName()))
			{
				result = evt.getOldValue();
				if (cancelF != null)
				{
					cancelF.execute((Y) result);
				}
			}
		}

	}

}
