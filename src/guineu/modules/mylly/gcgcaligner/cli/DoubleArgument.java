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

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

public class DoubleArgument extends NumberArgument
{

	private String _helpMessage;
	
	public DoubleArgument(String switchString, String methodName, String helpMessage, boolean nonNegative, boolean nonZero)
	{
		super(switchString, methodName, helpMessage, null);
		_helpMessage = helpMessage;
	}

	public String usage()
	{
		return super.usage() + " " + _helpMessage;
	}
	
	/*
	@Override
	public Object[] parseString(String commandLine, AlignmentParameters params)
			throws ParseException
	{
		Object[] vals = parseNumber(commandLine);
		if (vals.length == 2)
		{
			String newString = (String) vals[0];
			Number num = (Number) vals[1];
			Object newVals[] = {newString, params}; 
			try
			{
				AlignmentParameters newParams = callProperMethod(num, params);
				newVals[1] = newParams;
			} 
			catch (SecurityException e){} 
			catch (IllegalArgumentException e){} 
			catch (NoSuchMethodException e){} 
			catch (IllegalAccessException e){} 
			catch (InvocationTargetException e){}
			return newVals;
		}
		else
		{
			return new Object[] {commandLine, params};
		}
	}
	*/
}
