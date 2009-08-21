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



import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.InternationalFormatter;

public class AlignmentParameterDialog extends JDialog
{
	private WINDOW_STATUS status;
	private AlignmentParameters currentParameters;
	private String parameterChangeEventName;
	private final static Dimension DEFAULT_SIZE = new Dimension(400, 260);
	
	private final JFormattedTextField rt1laxTF;
	private final JFormattedTextField rt2laxTF;
	private final JFormattedTextField rtPenaltyTF;
	private final JFormattedTextField rt2PenaltyTF;
//	private final JFormattedTextField gapPenaltyTF;
	private final JFormattedTextField minSpecMatchTF;
	private final JFormattedTextField nameMatchBonusTF;
	private final JFormattedTextField rtiMismatchPenaltyTF;
	private final JFormattedTextField minSimilarityTF;
	private final JComboBox useMergeSelectionBox;
	private final JComboBox useConcentrationSelectionBox;
	
	private JFormattedTextField rtiLaxTF;
	private AbstractFormatter formatter;
	
	public enum WINDOW_STATUS {OK, CANCELED, NONE};

	public AlignmentParameters getParameters()
	{
		return currentParameters;
	}

	public AlignmentParameterDialog(Frame parent, AlignmentParameters startParams, String paramChangeEventName)
	{
		super(parent,"Please insert values for alignment");
		if (startParams == null)
		{
			throw new IllegalArgumentException("AlignmentParameters cannot be null");
		}
		
		currentParameters = startParams;
		parameterChangeEventName = paramChangeEventName;
		
		NumberFormat nf = new DecimalFormat();
		formatter = new InternationalFormatter(nf);
		rt1laxTF = new JFormattedTextField(formatter);
		rt2laxTF = new JFormattedTextField(formatter);
		rtiLaxTF = new JFormattedTextField(formatter);
		rtPenaltyTF = new JFormattedTextField(formatter);
		rt2PenaltyTF = new JFormattedTextField(formatter);
		minSpecMatchTF = new JFormattedTextField(formatter);
		nameMatchBonusTF = new JFormattedTextField(formatter);
		rtiMismatchPenaltyTF = new JFormattedTextField(formatter);
		minSimilarityTF = new JFormattedTextField(formatter);
		useMergeSelectionBox = new JComboBox(new String[] {"true", "false"});
		useMergeSelectionBox.setSelectedIndex(1); //false
		useConcentrationSelectionBox = new JComboBox(new String[] {"true", "false"});
		useConcentrationSelectionBox.setSelectedIndex(1); //false
		
		clearWindowSettings();
		
		createLayout();
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				updateStatus(WINDOW_STATUS.CANCELED);
			}
		});
	}
	
	private void clearWindowSettings()
	{
		setSize(DEFAULT_SIZE);
		setResizable(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		status = WINDOW_STATUS.NONE;
	}

	private synchronized void updateStatus(WINDOW_STATUS newStatus)
	{
		status = newStatus;
		if (status == WINDOW_STATUS.OK)
		{
			//TODO check input values
			AlignmentParameters oldParams = currentParameters;
			currentParameters = createParameters();
			firePropertyChange(parameterChangeEventName, oldParams, currentParameters);
		}
		setVisible(false);
		dispose();
	}
	
	private void popUpErrorDialog(String string)
	{
		javax.swing.JOptionPane.showMessageDialog(this, string);
	}
	
	private void setValues(AlignmentParameters p)
	{
		rt1laxTF.setValue(p.getRT1Lax());
		rt2laxTF.setValue(p.getRT2Lax());
		rtiLaxTF.setValue(p.getRTILax());
		rtPenaltyTF.setValue(p.getRT1Penalty());
		rt2PenaltyTF.setValue(p.getRT2Penalty());
		minSpecMatchTF.setValue(p.getMinSpectrumMatch());
		double nameMatchBonus = -p.getNameMatchBonus();
		nameMatchBonusTF.setValue(nameMatchBonus);
		rtiMismatchPenaltyTF.setValue(p.getRTIPenalty());
		minSimilarityTF.setValue(p.getMinSimilarity());
		useMergeSelectionBox.setSelectedItem(p.useMerge() ? "true" : "false");
		useConcentrationSelectionBox.setSelectedItem(p.useConcentration() ? "true" : "false");
	}
	
	private void createLayout()
	{
		setModal(true);
		setLayout(new GridLayout(10,1));
		
		JLabel rt1laxLabel = new JLabel("RT 1 lax");
		JLabel rt2laxLabel = new JLabel("RT 2 lax");
		JLabel rtiLaxLabel = new JLabel("RI lax");
		JLabel rtPenaltyLabel = new JLabel("RT penalty");
		JLabel rt2PenaltyLabel = new JLabel("RT 2 penalty");
		JLabel minSpecMatchLabel   = new JLabel("Minimum spectrum match");
		JLabel nameMatchBonusLabel = new JLabel("Bonus for matching names");
		JLabel rtiMismatchPenaltyLabel = new JLabel("RI penalty");
		JLabel minSimilarityLabel = new JLabel("Drop peaks with similarity < ");
//		JLabel mergeLabel = new JLabel("Merge similiar peaks");
		JLabel concentrationLabel = new JLabel("Use concentrations");
		setValues(currentParameters);
		
		JLabel[] labels = {
				rt1laxLabel, 
				rt2laxLabel, 
				rtiLaxLabel, 
				rtPenaltyLabel, 
				rt2PenaltyLabel, 
				rtiMismatchPenaltyLabel, 
				minSpecMatchLabel, 
				nameMatchBonusLabel,  
				minSimilarityLabel,
				concentrationLabel
				};
		JComponent[] others  = {
				rt1laxTF, 
				rt2laxTF, 
				rtiLaxTF, 
				rtPenaltyTF,
				rt2PenaltyTF,
				rtiMismatchPenaltyTF,
				minSpecMatchTF, 
				nameMatchBonusTF,
				minSimilarityTF,
				useConcentrationSelectionBox
				};
		
		for (int i = 0; i < labels.length; i++)
		{
			JPanel temp = new JPanel(new GridLayout(1, 2));
			temp.add(labels[i]);
			temp.add(others[i]);
			add(temp);
		}
		
		JButton ok = new JButton("Ok");
		ok.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateStatus(WINDOW_STATUS.OK);
			}
		}
		);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateStatus(WINDOW_STATUS.CANCELED);
			}
		});
		
		JButton defaults = new JButton("Defaults");
		defaults.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				currentParameters = AlignmentParameters.getDefaultParameters();
				setValues(currentParameters);
				repaint();
			}
		});
		
		JPanel temp = new JPanel(new GridLayout(1, 3));
		temp.add(ok);temp.add(defaults);temp.add(cancel);
		add(temp);
	}
	
	private double extractDoubleFromTF(JFormattedTextField tf)
	{
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(4);
		nf.setMaximumIntegerDigits(6);
		//Between 9999.999999 - -9999.999999
		AbstractFormatter formatter = new InternationalFormatter(nf);
		double value = -1.0;
		try
		{
			Object o = formatter.stringToValue(tf.getText());
			if (o instanceof Number)
			{
				value = ((Number) o).doubleValue();
			}
//			if (o instanceof Long){value = ((Long) o).doubleValue();}
//			else if (o instanceof Double){value = ((Double) o).doubleValue();}
//			else if (o instanceof Integer){value = ((Integer) o).doubleValue();}
//			else if (o instanceof Float){value = ((Float) o).doubleValue();}
		}
		catch (ParseException e)
		{
			popUpErrorDialog(tf.getText() + " is not a valid number");
			//The ParseException shouldn't be thrown for this function is
			//only to be called after the input is verified.
		}
		return value;
	}
	
	private AlignmentParameters createParameters()
	{
		double rt1lax = extractDoubleFromTF(rt1laxTF);
		double rt2lax = extractDoubleFromTF(rt2laxTF);
		double rtilax = extractDoubleFromTF(rtiLaxTF);
		double rtpen = extractDoubleFromTF(rtPenaltyTF);
		double rt2pen = extractDoubleFromTF(rt2PenaltyTF);
		double minSpecMatch = extractDoubleFromTF(minSpecMatchTF);
		double nameMatchBonus = -extractDoubleFromTF(nameMatchBonusTF);
		double rtiMismatchPenalty = extractDoubleFromTF(rtiMismatchPenaltyTF);
		double ignoreRatio = extractDoubleFromTF(minSimilarityTF);
		boolean useMerge = "true".equals(useMergeSelectionBox.getSelectedItem()) ? true : false;
		boolean useConcentration = "true".equals(useConcentrationSelectionBox.getSelectedItem()) ? true : false;
		
		AlignmentParameters params = new AlignmentParameters(rt1lax, rt2lax, rtilax, rtpen,
				rt2pen, minSpecMatch, nameMatchBonus, rtiMismatchPenalty, ignoreRatio, useMerge, useConcentration);
		return params;
	}
}
