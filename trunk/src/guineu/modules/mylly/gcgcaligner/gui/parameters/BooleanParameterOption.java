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

import java.util.List;

public class BooleanParameterOption implements MultiChoiceOption<Boolean>
{
	
	private final boolean _initialValue;
	private boolean _currentValue;
	private String _name;
	
	public BooleanParameterOption()
	{
		this(false);
	}
	
	public BooleanParameterOption(boolean initialValue)
	{
		this(false, "");
	}
	
	public BooleanParameterOption(String name)
	{
		this(false, name);
	}
	
	public BooleanParameterOption(boolean initialValue, String name)
	{
		_initialValue = _currentValue = initialValue;
		_name = name;
	}

	public Boolean getInitialValue()
	{
		return _initialValue;
	}

	public String getName()
	{
		return _name;
	}

	public Boolean parse(String input)
	{
		return Boolean.parseBoolean(input);
	}

	public String stringify(Boolean val)
	{
		return String.valueOf(_currentValue);
	}

	public List<Boolean> getOptions()
	{
		return java.util.Arrays.asList(new Boolean[] {false, true});
	}

}
