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
package guineu.modules.mylly.gcgcaligner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class ErrorHandler implements Thread.UncaughtExceptionHandler
{
	
	public final static ErrorHandler handler = new ErrorHandler();
	
	static final public int OUT_OF_MEMORY = 1;
	static final public int UNSPECIFIED_ERROR = Integer.MAX_VALUE;

	static final public String outOfMemoryString = "Run out of memory of " +  Runtime.getRuntime().maxMemory() + 
	" bytes.\nExiting.\n\n" + 
	"In order to avoid this problem, try running java virtual machine with larger heap size.\n" +
	"This is done in the Sun implementation using -XmxSm switch where S is the number of megabytes\n" +
	"that virtual machine is allowed to use.";

	
	

	public static void handle(Throwable t)
	{
		if (t instanceof OutOfMemoryError)
		{
			System.err.print(outOfMemoryString);
			System.exit(OUT_OF_MEMORY);
		}
		else
		{
			String errorFileName = "crashreport";
			int reportNumber = 1;
			File f;
			while ((f = new File(errorFileName + reportNumber + ".txt")).exists())
			{
				reportNumber++;
				if (reportNumber == 1)
				{
					bailOut(t);
				}
			}
			try
			{
				PrintWriter pw = new PrintWriter(f);
				t.printStackTrace(pw);
				System.err.println("Error report was written to " + f.getName());
				System.err.println("Exiting");
			} catch (FileNotFoundException e)
			{
				//Can't do much then
				bailOut(t);
			}
			System.exit(UNSPECIFIED_ERROR);
		}
	}

	private static void bailOut(Throwable t)
	{
		//FIXME is there some way to actually ensure that this gets written
		//when we have very low memory and encounter an error?
		System.err.println("Could not write error report to file.");
		System.err.println("So I'll print it here:");
		t.printStackTrace(System.err);
		System.exit(UNSPECIFIED_ERROR);
	}

	public void uncaughtException(Thread t, Throwable e)
	{
		handle(e);
	}
}
