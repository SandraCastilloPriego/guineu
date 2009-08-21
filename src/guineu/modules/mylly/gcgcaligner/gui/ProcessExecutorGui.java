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


/**
 * Used to run filters in their own thread and to display their progress.
 */
package guineu.modules.mylly.gcgcaligner.gui;

import guineu.modules.mylly.gcgcaligner.process.ListenerHelper;
import guineu.modules.mylly.gcgcaligner.process.ProcessExecutor;
import guineu.modules.mylly.gcgcaligner.process.StatusChange;
import guineu.modules.mylly.gcgcaligner.process.StatusChangeListener;
import guineu.modules.mylly.gcgcaligner.process.StatusReportingProcess;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CancellationException;



import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;



public class ProcessExecutorGui extends JDialog implements StatusChangeListener, StatusReporter
{
	private final static String TITLE = "Progress window";

	private ListenerHelper _lh;
	private MainWindow _parentWindow;
	private ProcessExecutor _processExecutor;
	private Map<StatusReportingProcess<?, ?>, StatusReporter> _guiComponents;
	private List<StatusReporter> _reporters;
	private JPanel _cancelPanel;
	private JPanel _panel;


	public ProcessExecutorGui(MainWindow parentWindow, ProcessExecutor processExec)
	{
		super(parentWindow, TITLE);

		_parentWindow = parentWindow;
		_lh = new ListenerHelper();
		_processExecutor = processExec;

		_guiComponents = new WeakHashMap<StatusReportingProcess<?, ?>, StatusReporter>();
		_reporters = new ArrayList<StatusReporter>();
		createGUI();
	}


	private void createGUI()
	{
		_panel = new JPanel(new GridLayout(2, 1));
		_cancelPanel = new JPanel(new FlowLayout());

		_cancelPanel.add(new JPanel());
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (e != null)
				{
					cancel();
					setVisible(false);
				}
			}
		});
		_cancelPanel.add(cancelButton);
		_cancelPanel.add(new JPanel());
		
		add(_panel);
		

//		JScrollPane scrollpane = new JScrollPane(_panel, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
//		add(scrollpane);
		
		Dimension d = _parentWindow.getSize();
		setSize((int) (d.width * 0.7), 300);
		setLocationRelativeTo(_parentWindow);
		updateLayout();
	}

	private void cancel()
	{
		if (_processExecutor != null)
		{
			_processExecutor.cancel();
		}
	}


	private void updateLayout()
	{
		_panel.removeAll();
		final int rows = _reporters.size() + 2; //one for blank and one for cancel

		_panel.setLayout(new GridLayout(rows, 1));
		for (StatusReporter r : _reporters)
		{
			_panel.add(r);
		}
		_panel.add(new JPanel());_panel.add(_cancelPanel);

		repaint();
	}

	/**
	 * Not re-entrant
	 * @param listener
	 * @param resultName
	 * @param cancelName
	 * @param input
	 */
	public void runProcess(final PropertyChangeListener listener, 
			final String resultName,
			final String cancelName,
			final Object input)
	{
		bringUp();
		Thread th = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					if (Thread.interrupted()){fireCancel(listener, cancelName);}
					_processExecutor.addStatusChangeListener(ProcessExecutorGui.this);
					final Object result = _processExecutor.process(input);
					listener.propertyChange(new PropertyChangeEvent(ProcessExecutorGui.this, resultName, null, result));
					_processExecutor.removeStatusChangeListener(ProcessExecutorGui.this);
				}
				catch (CancellationException e)
				{
					fireCancel(listener, cancelName);
				}
				tearDown();
			}
		});
		th.start();
	}
	
	private void fireCancel(PropertyChangeListener listener, String cancelName)
	{
		listener.propertyChange(new PropertyChangeEvent(ProcessExecutorGui.this, cancelName, null, null));
	}

	private void bringUp()
	{
		setVisible(true);
		setModal(true);
	}

	private void tearDown()
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				_reporters.clear();
				_guiComponents.clear();
				dispose();
			}
		});
	}

	public StatusReporter createStatusBar(StatusReportingProcess<?, ?> m)
	{
		StatusReporter r = new StatusReporter(m.getName());
		return r;
	}

	public void statusChanged(final StatusChange change)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				if (change != null && change.getSource() instanceof StatusReportingProcess)
				{
					StatusReportingProcess<? , ?> source = 
						(StatusReportingProcess<?, ?>) change.getSource();
					StatusReporter reporter = _guiComponents.get(source);
					if (reporter == null)
					{
						reporter = createStatusBar(source);
						_guiComponents.put(source, reporter);
						_reporters.add(reporter);
						updateLayout();
					}
					reporter.updateStatus(change);
					repaint();
				}
			}
		});
	}

	public void addStatusChangeListener(StatusChangeListener listener)
	{
		_lh.addListener(listener);
	}

	public void removeStatusChangeListener(StatusChangeListener listener)
	{
		_lh.removeListener(listener);
	}

	private static class StatusReporter extends JPanel
	{
		private JLabel label;
		private JProgressBar progressBar;
		private final String name;

		public StatusReporter(String name)
		{
			this.name = name;
			label = new JLabel(name);

			progressBar = new JProgressBar();
			progressBar.setIndeterminate(true);

			
			setLayout(new GridLayout(2,1));
			add(label);
			add(progressBar);
			getPreferredSize();
		}
		
		public Dimension getPreferredSize()
		{
			Dimension d1 = label.getPreferredSize();
			Dimension d2 = progressBar.getPreferredSize();
			return new Dimension(d1.width + d2.width, d1.height + d2.height);
		}

		public void updateStatus(final StatusChange c)
		{
			javax.swing.SwingUtilities.invokeLater(new Runnable()
			{
				public void run(){
					if (c != null && c.getDoneTaskCount() > 0)
					{
						label.setText(name + " : " + c.getNewStatus());
						progressBar.setMaximum(c.getTotalTaskCount());
						progressBar.setValue(c.getDoneTaskCount());
						progressBar.setIndeterminate(false);
						repaint();
					}
				}
			});
		}
	}

}
