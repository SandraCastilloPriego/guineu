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
import guineu.modules.mylly.gcgcaligner.datastruct.Pair;
import java.text.ParseException;


/**
 * Internationalization is most probably not working with BigInteger and
 * BigDecimal.
 * @author jmjarkko
 *
 */

public class NumberParameterOption<T extends Number & Comparable<T>> implements ParameterOption<T>
{
	private String _parameterName;
	private NumArgRange<T> _parameterRange;
	private Class<T> _numberType;
	private T _initialValue;
	private NumberParser<T> _parser;
	
	public NumberParameterOption(T initialValue, NumArgRange<T> range, String name, NumberParser<T> parser)
	{
		CheckNullity(initialValue, "initialValue");
		CheckNullity(range, "range");
		CheckNullity(name, "name");
		CheckNullity(parser, "parser");
		
		_initialValue = initialValue;
		_parameterRange = range;
		_parameterName = name;
		_parser = parser;
		
	}

	public T parse(String str) throws ParseException
	{
		ParseException ex = null;
		try
		{
			Pair<T, NumArgRange.RANGETEST> parseResult = parseString(str);
			if (parseResult.getSecond() == NumArgRange.RANGETEST.WITHIN)
			{
				return parseResult.getFirst();
			}
			else
			{
				ex = new ParseException(str + " was too " + 
				                        (parseResult.getSecond()  == NumArgRange.RANGETEST.LESS ?
				                        		"small" : "large"), 0);
			}
		}
		catch (NumberFormatException e)
		{
			ex = new ParseException(e.getMessage(), 0);
		}
		throw ex;
	}
	
	/**
	 * @throws NumberFormatException in case of unparseable string
	 * @param toParse String we want to parse
	 * @return Pair containing the parsed number and report of range test
	 * to this number.
	 * @throws ParseException 
	 */
	public Pair<T, NumArgRange.RANGETEST> parseString(String toParse) throws ParseException
	{
		T number = _parser.parseString(toParse);
		return new Pair<T, NumArgRange.RANGETEST>(number, _parameterRange.includes(number));
	}
	
	
	public String getName(){return _parameterName;}
	
	private static void CheckNullity(Object o, String parameterName)
	{
		if (o == null)
		{
			throw new IllegalArgumentException(parameterName + " cannot be null.");
		}
	}

	public Class<T> getOutputType()
	{
		return _numberType;
	}

	public T getInitialValue()
	{
		return _initialValue;
	}

	public String stringify(T val)
	{
		return _parser.stringify(val);
	}

}
