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



import guineu.modules.mylly.gcgcaligner.alignment.Aligner;
import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.filter.MapFunction;
import guineu.modules.mylly.gcgcaligner.filter.NameFilterModule;
import guineu.modules.mylly.gcgcaligner.filter.PeakCountFilterModule;
import guineu.modules.mylly.gcgcaligner.gui.parameters.ParameterInputException;
import guineu.modules.mylly.gcgcaligner.process.AbstractStatusReportingModule;
import guineu.modules.mylly.gcgcaligner.process.ProcessExecutor;
import guineu.modules.mylly.gcgcaligner.process.StatusReportingModule;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CancellationException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;

public class FilterChooserDialog extends JDialog implements ActionListener
{

	private static enum TYPE {PRE, ALIGN, POST};
	@SuppressWarnings("unchecked")
	private final static StatusReportingModule ADD = generateNullProcess("Add new");
	@SuppressWarnings("unchecked")
	private final static StatusReportingModule REMOVE = generateNullProcess("Remove");
	@SuppressWarnings("unchecked")
	private final static StatusReportingModule NONE = generateNullProcess("None");

	private final static String DEF_TITLE = "Choose methods to use";

	private final String propertyChangeEventName;
	private JPanel	panel;
	private JButton okButton;
	private JButton cancelButton;

	private List<OptionBox> preProcessorComponents;
	private List<OptionBox> postProcessorComponents;
	private OptionBox alignerComponent;

	private List<StatusReportingModule<InputSet, InputSet>> preProcessors;
	private List<StatusReportingModule<InputSet, Alignment>> aligners;
	private List<StatusReportingModule<Alignment, Alignment>> postProcessors;

	private final Frame parentFrame; 

	public FilterChooserDialog(Frame parent,
			List<StatusReportingModule<InputSet, InputSet>> preProcessors,
			List<StatusReportingModule<InputSet, Alignment>> aligners,
			List<StatusReportingModule<Alignment, Alignment>> postProcessors, String propertyChangeEventName)
	{
		super(parent);
		parentFrame = parent;
		this.propertyChangeEventName = propertyChangeEventName;
		this.preProcessors = new ArrayList<StatusReportingModule<InputSet, InputSet>>(preProcessors);
		this.aligners = new ArrayList<StatusReportingModule<InputSet, Alignment>>(aligners);
		this.postProcessors = new ArrayList<StatusReportingModule<Alignment, Alignment>>(postProcessors);

		preProcessorComponents = new ArrayList<OptionBox>();
		postProcessorComponents = new ArrayList<OptionBox>();

		initializeGUI(DEF_TITLE);
		addNewBox(TYPE.PRE);
		addNewBox(TYPE.ALIGN);
		addNewBox(TYPE.POST);
		updateComponents();
	}
	
	private Frame getParentFrame(){return parentFrame;}

	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent e)
	{
		if (e != null && e.getSource() instanceof OptionBox)
		{
			OptionBox source = (OptionBox) e.getSource();
			StatusReportingModule selected = source.getSelectedItem();
			boolean modified = false;

			if (selected == ADD)
			{
				TYPE type = source.getType();
				modified = checkAndAdd(type);
			}
			else if (selected == REMOVE)
			{
				modified = checkAndRemove(source);
			}
			if (modified)
			{
				updateComponents();
			}
		}
		else if (e != null && e.getSource() == okButton)
		{
			sendResults();
			getRidOf();
		}
		else if (e != null && e.getSource() == cancelButton)
		{
			cancel();
		}
	}

	private void cancel()
	{
		getRidOf();
	}

	private void showError(String errorString)
	{
		JOptionPane.showMessageDialog(this, errorString, "Error", JOptionPane.ERROR_MESSAGE);
	}

	private boolean checkAndAdd(TYPE type)
	{
		boolean success = false;
		if (type != TYPE.ALIGN)
		{
			addNewBox(type);
			success = true;
		}
		return success;
	}

	private boolean checkAndRemove(OptionBox box)
	{
		boolean success = false;
		try
		{
			if (preProcessorComponents.size() > 1)
			{
				success = preProcessorComponents.remove(box);
			}
			
			if (!success && postProcessorComponents.size() > 1)
			{
				success = postProcessorComponents.remove(box);
			}
//			if (!success)
//			{
//				success = (alignerComponent == box);
//			}
//			removedPair = findAndRemove(preProcessorComponents, box);
//			if (removedPair == null)
//			{
//				findAndRemove(postProcessorComponents, box);
//			}
//			if (removedPair == null && alignerComponent.getSecond() == box)
//			{
//				removedPair = alignerComponent;
//			}
//			if (removedPair != null)
//			{
//				System.out.printf("Removing %s, %s\n", removedPair.getFirst().getText(),
//				                  ((MapFunction)removedPair.getSecond().getSelectedItem()).getName());
//				removeComponent(removedPair);
//
//				success = true;
//			}
		}
		catch (IllegalArgumentException e)
		{
			showError("Cannot remove last selector");
		}
		return success;
	}

//	/**
//	 * @throws IllegalArgumentException if we try to remove last component
//	 * @param list
//	 * @param box
//	 * @return
//	 */
//	private OptionBox findAndRemove(List<OptionBox> list, OptionBox box)
//	{
//		Pair<JLabel, JComboBox> found = null;
//		for (Pair<JLabel, JComboBox> pair : list)
//		{
//			if (pair.getSecond() == box)
//			{
//				found = pair;
//				break;
//			}
//		}
//		if (found != null && list.size() == 1)
//		{
//			throw new IllegalArgumentException("Cannot remove last component");
//		}
//		return found;
//	}

	private void removeComponent(OptionBox comp)
	{
		TYPE type = comp.getType();
		List<OptionBox> compList;
		if (type == TYPE.PRE)
		{
			compList = preProcessorComponents;
		}
		else if (type == TYPE.POST)
		{
			compList = postProcessorComponents;
		}
		else
		{
			compList = null;
		}
		if (compList != null)
		{
			boolean removed = compList.remove(comp);
			assert(removed);
		}
		else
		{
			aligners = null;
		}
	}


	private void getRidOf()
	{
		preProcessorComponents.clear();
		postProcessorComponents.clear();
		alignerComponent = null;
//		while (preProcessorComponents.size() > 0)
//		{
//			removeComponent(preProcessorComponents.get(0));
//		}
//		while (postProcessorComponents.size() > 0)
//		{
//			removeComponent(postProcessorComponents.get(0));
//		}
//
//		removeComponent(alignerComponent);
		setVisible(false);
		dispose();
	}

	@SuppressWarnings("unchecked")
	private void sendResults()
	{
		ProcessExecutor pe = new ProcessExecutor();
		for (OptionBox pair : preProcessorComponents)
		{
			StatusReportingModule mf = pair.getSelectedItem();
			if (mf != NONE && mf != ADD && mf != REMOVE)
			{
				pe.addPreProcess(mf);
			}
		}
		pe.addAligner((Aligner) alignerComponent.getSelectedItem());
		for (OptionBox pair : postProcessorComponents)
		{
			StatusReportingModule mf = pair.getSelectedItem();
			if (mf != NONE && mf != ADD && mf != REMOVE)
			{
				pe.addPostProcess(mf);
			}
		}
		firePropertyChange(propertyChangeEventName, null, pe);
	}

	private void initializeGUI(String title)
	{
		panel = new JPanel();
		JScrollPane scroller = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroller);
		
		setModal(true);
		panel.setLayout(new GridLayout(2,1));
		setSize(600,300);
		setLocationRelativeTo(getParent());
		setTitle(title);
		okButton = new JButton("OK");
		okButton.addActionListener(this);
		panel.setSize(getWidth() - scroller.getVerticalScrollBar().getWidth(), getHeight());
		cancelButton = new JButton("cancel");
		cancelButton.addActionListener(this);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				cancel();
			}
		});
	}

	private void updateComponents()
	{
		final int currentRows = preProcessorComponents.size() + postProcessorComponents.size() + 2;
		GridLayout layout = (GridLayout) panel.getLayout();
		layout.setRows(currentRows);
		panel.removeAll();

		addBoxes(preProcessorComponents);
		addBox(alignerComponent);
		addBoxes(postProcessorComponents);

		JComponent container = new JPanel();
		container.add(okButton);
		container.add(cancelButton);
		panel.add(container);
//		panel.add(okButton);
//		panel.add(cancelButton);
		validate();
		repaint();
	}

	@SuppressWarnings("unchecked")
	private Vector<StatusReportingModule> createChoices(TYPE type)
	{
		Vector<StatusReportingModule> choices = new Vector<StatusReportingModule>();
		List choiceList = null;

		switch(type)
		{
		case PRE:choiceList = preProcessors;break;
		case POST:choiceList = postProcessors;break;
		case ALIGN:choiceList = aligners;
		}
		for (int i = 0; i < choiceList.size(); i++)
		{
			StatusReportingModule mod = ((StatusReportingModule) choiceList.get(i)).clone();
			choices.add(mod);
			
		}
		if (type != TYPE.ALIGN)
		{
			choices.add(ADD);
			choices.add(REMOVE);
			choices.add(NONE);
		}
		
		return choices;
	}

	private void addNewBox(TYPE type)
	{
		String labelString = null;
		List<OptionBox> listOfComponents = null;

		switch (type)
		{
		case PRE:
			labelString = "Choose preprocessor";
			listOfComponents = preProcessorComponents;
			break;
		case ALIGN:
			labelString = "Choose aligner";
			break;
		case POST:
			labelString = "Choose postprocessor";
			listOfComponents = postProcessorComponents;
			break;
		}

		OptionBox optBox = new OptionBox(labelString, createChoices(type), this, type);
		
		if (listOfComponents != null)
		{
			listOfComponents.add(optBox);
		}
		else
		{
			alignerComponent = optBox;
		}
	}

	private void addBoxes(Collection<OptionBox> boxes)
	{
		for (OptionBox box : boxes)
		{
			addBox(box);
		}
	}

	private void addBox(OptionBox box)
	{
//		panel.add(box.getFirst());
//		panel.add(box.getSecond());
		panel.add(box);
	}


	@SuppressWarnings("unchecked")
	private static StatusReportingModule generateNullProcess(final String name)
	{
		return new AbstractStatusReportingModule()
		{

			@Override
			protected Object actualMap(Object input)
					throws CancellationException
			{
				return input;
			}

			public void askParameters(Frame parentWindow)
					throws ParameterInputException{}

			public boolean isConfigurable()	{return false;}

			@Override
			public String getName()
			{
				return name;
			}			
		};
	}


	public static void main(String[] notRead)
	{
		JFrame parent = new JFrame("Test");
		parent.setSize(800, 600);
		parent.setVisible(true);
		parent.setDefaultCloseOperation(EXIT_ON_CLOSE);

		List<StatusReportingModule<InputSet, InputSet>> preProcessors = new ArrayList<StatusReportingModule<InputSet, InputSet>>();
		List<StatusReportingModule<InputSet, Alignment>> aligners = new ArrayList<StatusReportingModule<InputSet, Alignment>>();
		List<StatusReportingModule<Alignment, Alignment>> postProcessors = new ArrayList<StatusReportingModule<Alignment, Alignment>>();

		preProcessors.add(ProcessExecutor.getPrefilter());
		preProcessors.add(new NameFilterModule());
		aligners.add(new ScoreAligner());
		postProcessors.add(ProcessExecutor.getPostFilter(4));
		postProcessors.add(new PeakCountFilterModule());

		FilterChooserDialog bpWindow = new FilterChooserDialog(parent, preProcessors, aligners, postProcessors, null);
		bpWindow.setVisible(true);
	}

	private static class JComboBoxRenderer extends JLabel implements ListCellRenderer
	{

		/**
		 * 
		 */
		private static final long	serialVersionUID	= -6442992148204460275L;

		public JComboBoxRenderer()
		{
			setOpaque(true);
		}

		@SuppressWarnings("unchecked")
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus)
		{
			MapFunction mf = (MapFunction) value;
			setText(mf.getName());
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}
			return this;
		}

	}

	private static class OptionBox extends JPanel implements ActionListener
	{
		private JLabel _label;
		private JComboBox _choices;
		private JButton _chooseSettingsButton;
		private final TYPE _type;
		private final FilterChooserDialog _target;

		@SuppressWarnings("unchecked")
		public OptionBox (String name, Vector<StatusReportingModule> choices, FilterChooserDialog actListener, TYPE type)
		{
			_choices = new JComboBox(choices);
			_label = new JLabel(name);
			_chooseSettingsButton = new JButton("Choose settings");
			_type = type;
			_target = actListener;
			
			_choices.setRenderer(new JComboBoxRenderer());
			if (choices.contains(NONE))
			{
				_choices.setSelectedItem(NONE);
			}
			else
			{
				_choices.setSelectedIndex(0);
			}
			_choices.addActionListener(this);
			_chooseSettingsButton.addActionListener(this);
			
			add(_choices);
			add(_label);
			add(_chooseSettingsButton);
			updateButton();
		}
		
		public TYPE getType(){return _type;}
		
		public void removeActionListener(ActionListener l)
		{
			_choices.removeActionListener(l);
		}

		@SuppressWarnings("unchecked")
		public StatusReportingModule getSelectedItem()
		{
			return (StatusReportingModule) _choices.getSelectedItem();
		}
		
		private void updateButton()
		{
			_chooseSettingsButton.setEnabled(getSelectedItem().isConfigurable());
		}

		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == _choices)
			{
				updateButton();
				e.setSource(this);
				_target.actionPerformed(e);
			}
			else if (e.getSource() == _chooseSettingsButton)
			{
				StatusReportingModule ps = getSelectedItem();
				if (ps.isConfigurable())
				{
					try
					{
						ps.askParameters(_target.getParentFrame());
					}catch (ParameterInputException ex)
					{
						_target.showError(ex.getLocalizedMessage());
					}
				}
			}
		}
	}
}


