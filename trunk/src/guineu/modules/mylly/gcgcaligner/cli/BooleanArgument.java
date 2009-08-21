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

public abstract class BooleanArgument extends AbstractArgument
{

	private String	_switchString;
	private String	_helpMessage;

	public BooleanArgument(){}
	
	public BooleanArgument(String switchString, String helpMessage)
	{
		_switchString = switchString;
		_helpMessage = helpMessage;
	}

	public boolean matches(String switchString)
	{
		return _switchString.equalsIgnoreCase(switchString);
	}

	public int tokensUsed(){return 0;}

	public String usage()
	{
		return _switchString + " " + _helpMessage;
	}

}
