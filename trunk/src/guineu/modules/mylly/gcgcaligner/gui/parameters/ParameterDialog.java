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
package guineu.modules.mylly.gcgcaligner.gui.parameters;

import guineu.modules.mylly.gcgcaligner.cli.NumArgRange;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


/**
 * This class launches a PropertyChangeEvent with a spesific string and Boolean
 * value of <code>true</code> when dialog was closed normally and with value <code>
 * false</code> when it was canceled.
 * @author JMJARKKO
 *
 */
public class ParameterDialog extends JDialog
{

	private Map<String, Object> _nameToResult;
	private List<ParameterOption<?>> _options;
	private String _parametersSetString;
	private JPanel _panel;
	private Map<JTextField, ParameterOption<?>> _tfToOption;
	private Map<JComboBox, MultiChoiceOption<?>> _multipleChoices;
	private JButton _okButton;
	private JButton _cancelButton;
	
	public ParameterDialog(Frame parent, String title, List<ParameterOption<?>> options, String finishedString)
	{
		super(parent, title);
		setModal(true);
		
		_options = new ArrayList<ParameterOption<?>>(options);
		_tfToOption = new HashMap<JTextField, ParameterOption<?>>();
		_multipleChoices = new HashMap<JComboBox, MultiChoiceOption<?>>();
		_parametersSetString = finishedString;
		_nameToResult = new HashMap<String, Object>();
		createGUI();
		setLocationRelativeTo(parent);
	}
	
	private void createGUI()
	{
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		int maxWidth = 0;
		int maxHeight = 0;
		
		_panel = new JPanel(new GridLayout(_options.size() + 1, 1));
		setContentPane(_panel);
		for (ParameterOption<?> p : _options)
		{
			JPanel newPanel = createOption(p);
			maxHeight += newPanel.getPreferredSize().height;
			maxWidth = Math.max(maxWidth, newPanel.getPreferredSize().width);
			_panel.add(newPanel);
		}
		
		
		JPanel okCancelPanel = new JPanel(new GridLayout(1,2));
		_okButton = new JButton("OK");
		_okButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				verifyInput();
			}
		});
		okCancelPanel.add(_okButton);
		
		_cancelButton = new JButton("Cancel");
		_cancelButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				closeDialog(false);
			}
		});
		okCancelPanel.add(_cancelButton);
		_panel.add(okCancelPanel);
		
		maxHeight += okCancelPanel.getPreferredSize().height;
		maxHeight += 20; //TODO fix for correct titlebar height!
		maxWidth = Math.max(240, Math.max(maxWidth,okCancelPanel.getPreferredSize().width));
		
		setSize(maxWidth, maxHeight);
	}
	
	private void showError(String errorText)
	{
		JOptionPane.showMessageDialog(this, errorText, "Error in input", JOptionPane.ERROR_MESSAGE);
	}
	
	private void verifyInput()
	{
		boolean success = true;
		StringBuilder errorString = new StringBuilder("Encountered following errors:\n");
		for (Entry<JTextField, ParameterOption<?>> entry : _tfToOption.entrySet())
		{
			try
			{
				Object entryValue = entry.getValue().parse(entry.getKey().getText());
				_nameToResult.put(entry.getValue().getName(), entryValue);
			}
			catch (ParseException e)
			{
				if (success)
				{
					success = false;
				}
				else
				{
					errorString.append('\n');
				}
				errorString.append(e.getMessage());
			}
		}
		for (Entry<JComboBox, MultiChoiceOption<?>> entry : _multipleChoices.entrySet())
		{
			Object val = entry.getKey().getSelectedItem();
			_nameToResult.put(entry.getValue().getName(), val);
		}
		if (success)
		{
			closeDialog(success);
		}
		else
		{
			showError(errorString.toString());
		}
	}
	
	private void closeDialog(boolean success)
	{
		if (success)
		{
			firePropertyChange(_parametersSetString, false, true);
		}
		else
		{
			firePropertyChange(_parametersSetString, false, false);
		}
		dispose();
	}
	
	private <T> JPanel createOption(ParameterOption<T> option)
	{
		JPanel tempPanel = new JPanel(new GridLayout(1,2));
		JLabel optionNameLabel= new JLabel(option.getName());
		
		tempPanel.add(optionNameLabel);
		
		if(option instanceof MultiChoiceOption)
		{
			MultiChoiceOption<T> muOpt = (MultiChoiceOption<T>) option;
			Vector<?> choices = new Vector(muOpt.getOptions());
			JComboBox box = new JComboBox(choices);
			box.setSelectedItem(muOpt.getInitialValue());
			_multipleChoices.put(box, (MultiChoiceOption<?>) option);
			Dimension prefSize = box.getPreferredSize();
			
			tempPanel.add(box);
		}
		else
		{
			JTextField tf = new JTextField();
			
			tf.setText(option.stringify(option.getInitialValue()));
			
			_tfToOption.put(tf, option);

			tempPanel.add(tf);
		}
		
		return tempPanel;
	}

	public Object getValue(String valueName)
	{
		return _nameToResult.get(valueName);
	}
	
	
	public static void main(String[] noUse) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		final String names[] = {"0 ... 1", "1 ... 10", "bignum test", "2B|!2B?", "Stringtest", "String choice test"};
		
		JFrame parentFrame = new JFrame("Test");
		parentFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		parentFrame.setSize(400, 400);
		ArrayList<ParameterOption<?>> options = new ArrayList<ParameterOption<?>>();
		
		
		NumberParser<Double> np1 = NumberParserGenerator.genParser(Double.class);
		NumArgRange<Double> nar1 = new NumArgRange<Double>(0.0, true, 1.0, true);
		options.add(new NumberParameterOption<Double>(0.3E-1, nar1, names[0], np1));
		
		NumberParser<Integer> np2 = NumberParserGenerator.genParser(Integer.class);
		NumArgRange<Integer> nar2 = new NumArgRange<Integer>(1, true, 10, true);
		options.add(new NumberParameterOption<Integer>(1, nar2, names[1], np2));
		
		NumberParser<BigDecimal> np3 = NumberParserGenerator.genParser(BigDecimal.class);
		NumArgRange<BigDecimal> nar3 = new NumArgRange<BigDecimal>(new BigDecimal(0), true, new BigDecimal("98938492E2000"), true);
		
		
		options.add(new NumberParameterOption<BigDecimal>(new BigDecimal(1), nar3, names[2], np3));
		
		options.add(new BooleanParameterOption(true, names[3]));
		options.add(new StringParameterOption("startVal", names[4]));
		options.add(new MultipleChoiceStrings(Arrays.asList(new String[] {"aki", "make", "pera", "m�"}), "make", names[5]));
		
//		options.add(new NumberParameterOption<Integer>(1, new NumArgRange<Integer>(-5, true, 5, true), val1Name, NumberParserGenerator.genParser(Integer.class));
//		
//		
//		options.add(new NumberParameterOption(1, ArgumentRange.zeroToOne(), val1Name));
//		options.add(new NumberParameterOption(2, new ArgumentRange(1, 10), val2Name));
		final ParameterDialog pd = new ParameterDialog(parentFrame, "Give arguments", options, "finishedSettings");
		pd.addPropertyChangeListener(new PropertyChangeListener()
		{

			public void propertyChange(PropertyChangeEvent evt)
			{
				if (evt.getSource() == pd)
				{
					if (evt.getOldValue() instanceof Boolean)
					{
						if (!((Boolean)evt.getOldValue()).booleanValue() &&
								((Boolean) evt.getNewValue()).booleanValue())
						{
							for (String str : names)
							{
								System.out.printf("%s : %s\n", str, pd.getValue(str));
							}
						}
					}
				}
			}
			
		});
//		pd.setSize(300, 200);
		parentFrame.setVisible(true);
		pd.setVisible(true);
	}
}
