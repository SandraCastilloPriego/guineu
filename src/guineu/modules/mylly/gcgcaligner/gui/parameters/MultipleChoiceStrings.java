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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class MultipleChoiceStrings implements MultiChoiceOption<String>
{
	
	private final static String DEF_NAME = "Choices";
	
	private final List<String> _options;
	private final String _initialValue;
	private final String _name;
	
	public MultipleChoiceStrings(List<String> choices, String initialValue, String name)
	{
		_initialValue = initialValue;
		_options = new ArrayList<String>(choices);
		_name = name;
	}
	public MultipleChoiceStrings(List<String> choices)
	{
		this(choices, (choices.size() == 0 ? null : choices.get(0)), DEF_NAME);
	}
	

	public List<String> getOptions()
	{
		return new ArrayList<String>(_options);
	}

	public String getInitialValue()
	{
		return _initialValue;
	}

	public String getName()
	{
		return _name;
	}

	public String parse(String input) throws ParseException
	{
		return input;
	}

	public String stringify(String val)
	{
		return val;
	}

}