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
package guineu.modules.mylly.gcgcaligner.cli;




import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.cli.NumArgRange.RANGETEST;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberArgument extends AbstractArgument
{

	protected String _cliSwitchString;
	protected String _methodName;
	protected String _helpMessage;
	protected NumArgRange _range;
	
	protected NumberArgument(String switchString, String methodName, 
			String helpMessage, NumArgRange range)
	{
		_cliSwitchString = switchString;
		_methodName = methodName;
		_helpMessage = helpMessage;
		_range = range;
	}
	
	public static NumberArgument getDoubleArgument(String switchString, String methodName,
			String helpMessage)
	{
		NumArgRange<Double> range = NumArgRange.nonNegativeRealRange();
		return new NumberArgument(switchString, methodName, helpMessage, range);
	}
	
	public static NumberArgument getDoubleArgument(String switchString, String methodName,
			String helpMessage,	double minVal, double maxVal)
	{
		NumArgRange<Double> range = new NumArgRange<Double>(minVal, maxVal);
		return new NumberArgument(switchString, methodName, helpMessage, range);
	}
	
	public static NumberArgument getIntegerArgument(String switchString, String methodName,
			String helpMessage, int minVal, int maxVal)
	{
		NumArgRange<Integer> range = new NumArgRange<Integer>(minVal, maxVal);
		return new NumberArgument(switchString, methodName, helpMessage, range);
	}

	public int tokensUsed(){return 1;}
	public boolean matches(String switchString){return _cliSwitchString.equalsIgnoreCase(switchString);}
	public AlignmentParameters consume(AlignmentParameters old, String ... tokens) throws ParseException
	{
		assert(tokens.length == 1);
		Number num;
		String token = tokens[0];
		//TODO international parsing of numbers
		try
		{
			if (_range.getNumberClass() == Integer.class)
			{
				num = new Integer(token);
			}
			else
			{
				num = new Double(token);
			}
			validate(num, token);
		}
		catch (NumberFormatException e)
		{
			if (_range.getNumberClass() == Integer.class)
			{
				throw new ParseException("Token " + token + " was not a valid integer", 0);
			}
			else
			{
				throw new ParseException("Token " + token + " was not a valid number", 0);
			}
		}
		try
		{
			return callProperSetterMethod(num, old);
		}
		//FIXME Fails silently, supposes that BatchMode.java is correctly written
		catch (SecurityException e){} 
		catch (IllegalArgumentException e){} 
		catch (NoSuchMethodException e){} 
		catch (IllegalAccessException e){} 
		catch (InvocationTargetException e){}
		return old;
	}
	
	protected void validate(Number n, String token) throws ParseException
	{
		double num = n.doubleValue();
		ParseException ex = null;
		RANGETEST result = _range.includes(n);
		if (result == RANGETEST.LESS)
		{
			ex = new ParseException("Number " + token + " was too low", 0);
		}
		else if (result == RANGETEST.MORE)
		{
			ex = new ParseException("Number " + token + " was too high", 0);
		}
		if (ex != null){throw ex;}
	}

	protected AlignmentParameters callProperSetterMethod(Number num, AlignmentParameters params) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		AlignmentParameters newParams = params;
		if (num != null)
		{
			Object paramArray[] = new Object[1];
			Method method;
			method = params.getClass().getDeclaredMethod("set" + _methodName, new Class[] {_range.getNumberClass()});
			paramArray[0] = _range.getNumberClass() == Integer.class ? num.intValue() : num.doubleValue();
			newParams = (AlignmentParameters) method.invoke(params, paramArray);
		}
		return newParams;
	}
	
	protected Number callProperGetterMethod(AlignmentParameters params) 
	throws IllegalArgumentException, IllegalAccessException, 
	InvocationTargetException, SecurityException, NoSuchMethodException
	{
		Method method = params.getClass().getDeclaredMethod("get" + _methodName, new Class[0]);
		Number number = (Number) method.invoke(params, new Object[0]);
		return number;
	}
	
	protected Number getDefaultValue()
	{
		AlignmentParameters p = new AlignmentParameters();
		Number val = 0;
		try
		{
			val = callProperGetterMethod(p);
		} 
		catch (IllegalArgumentException e){}
		catch (SecurityException e){} 
		catch (IllegalAccessException e){}
		catch (InvocationTargetException e){}
		catch (NoSuchMethodException e){}
		return val;
	}

	/*
	public abstract Object[] parseString(String commandLine, AlignmentParameters params)
	throws ParseException;
	*/
	
	public String usage()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(_cliSwitchString).append(" ");
		sb.append("{x").append(" E ").append(_range);
		if (_range.getNumberClass() == Integer.class)
		{
			sb.append(", x is an integer");
		}
		sb.append(", def : ").append(getDefaultValue()).append("}");
		sb.append(_helpMessage);
		return sb.toString();
	}

}
