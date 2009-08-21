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

import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.text.InternationalFormatter;

/**
 * @author jmjarkko
 */
public class ConcreteVerifier extends InputVerifier
{
	
	public static InputVerifier getDoubleVerifier()
	{
		if (doubleVerifier == null)
		{
			NumberFormat nf = NumberFormat.getNumberInstance();
			nf.setMaximumFractionDigits(4);
			nf.setMaximumIntegerDigits(6);
			//Between 999999.9999 - -999999.9999
			AbstractFormatter formatter = new InternationalFormatter(nf);
			doubleVerifier = new ConcreteVerifier(formatter);
		}
		return doubleVerifier;
	}
	
	public static InputVerifier getIntegerVerifier(final int min, final int max)
	{
		if (min > max)
		{
			throw new IllegalArgumentException("min > max (" + min + " , " + max + ")");
		}
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(0);
		nf.setMinimumFractionDigits(0);
		AbstractFormatter f = new InternationalFormatter(nf)
		{
			public Object stringToValue(String text) throws ParseException
            {
				Object o = super.stringToValue(text);
				if (o instanceof Integer)
				{
					int val = (Integer) o;
					if (val < min || val > max)
					{
						throw new ParseException("Value too low or high", 0);
					}
					else
					{
						return o;
					}
				}
				throw new ParseException("Value not integer", 0);
            }
		};
		return new ConcreteVerifier(f);
	}
	
	private static InputVerifier doubleVerifier = null;
	private AbstractFormatter verifier;
	
	public ConcreteVerifier(AbstractFormatter formatter)
	{
		if (formatter == null)
		{
			throw new NullPointerException();
		}
		verifier = formatter;
	}
	
	/**
	 * @see javax.swing.InputVerifier#verify(javax.swing.JComponent)
	 */
	@Override
	public boolean verify(JComponent input)
	{
		if (input instanceof JFormattedTextField)
		{
            JFormattedTextField textfield = (JFormattedTextField)input;
            try
            {
            	verifier.stringToValue(textfield.getText());
            	return true;
            }
            catch (ParseException e)
            {
            	return false;
            }
		}
		return true;
	}
}
