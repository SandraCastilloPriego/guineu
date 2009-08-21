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
import java.text.ParseException;


public interface CommandLineArgument extends Comparable<CommandLineArgument>
{
	/**
	 * Parse command line arguments, cutting out the used part of the string.
	 * If string contains invalid arguments for this option, throws ParseException where
	 * message should tell what is wrong in the value of the parameter (is it missing, not
	 * a proper number, negative, etc.).
	 * @param commandLine current command line argument string
	 * @param params Current parsed alignment parameters
	 * @throws ParseException Throws parse exception if 
	 * @return array of <code>{modifiedString, newParameters}</code>
	 */
//	Object[] parseString(String commandLine, AlignmentParameters params) throws ParseException;
	
	/**
	 * Prints usage of following form:
	 * <p>
	 * --switchName {validValues}
	 * </p>
	 * @return
	 */
	String usage();
	
	/**
	 * 
	 * @return number of tokens consumed by this command line switch
	 * after the actual switch token. 
	 * Negative amount means that it uses all the rest of the tokens.
	 */
	int tokensUsed();
	
	/**
	 * 
	 * @param switchString
	 * @return <b>true</b> if <code>switchString</code> matches this command line switch.
	 * Otherwise returns <b>false</b>.
	 */
	boolean matches(String switchString);
	
	/**
	 * Parses tokens and returns new 
	 * @param old Old AlignmentParameters based on which new updated AlignmentParameters object
	 * is created.
	 * @param tokens input tokens
	 * @throws ParseException Throws <code>ParseException</code> if token is not parseable.
	 * @return new AlignmentParameters object with updated parameters.
	 */
	AlignmentParameters consume(AlignmentParameters old, String ... tokens) throws ParseException;
}
