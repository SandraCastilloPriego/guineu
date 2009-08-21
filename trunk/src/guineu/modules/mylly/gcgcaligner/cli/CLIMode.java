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


import guineu.modules.mylly.gcgcaligner.alignment.Aligner;
import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.alignment.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CLIMode implements Runnable
{
	private List<CommandLineArgument> cliArgs;
	private String errorMessage;
	private RunningParameters params;
	private String cliTokens[];
	
	private String combineToString(String stringArray[])
	{
		StringBuilder sb = new StringBuilder();
		int i = 0;
		for (String st : stringArray)
		{
			sb.append(st);
			if (++i < stringArray.length)
			{
				sb.append(' ');
			}
		}
		return sb.toString();
	}
	
	private int readInputFileNames(int startIx)
	{
		ArrayList<String> filenameList = new ArrayList<String>();
		for (; startIx < cliTokens.length - 1; startIx++)
		{
			filenameList.add(cliTokens[startIx]);
		}
		if (filenameList.size() == 0)
		{
			errorMessage = "No input files specified";
			return startIx;
		}
		else
		{
			
			ArrayList<String> nonExistingFiles = new ArrayList<String>();
			ArrayList<String> newFileNames = new ArrayList<String>(filenameList.size());
			for (int i = 0; i < filenameList.size(); i++)
			{
				//Go through all filenames and is any of them is non-existant
				//mark that up. Also add all non-directory files from directory
				//files in the filenamelist
				String curFN = filenameList.get(i);
				File curFile = new File(curFN);
				if (!curFile.exists()){nonExistingFiles.add(curFN);}
				else if (curFile.isDirectory())
				{
					for (File f : curFile.listFiles())
					{
						if (!f.isDirectory())
						{
							newFileNames.add(f.getAbsolutePath());
						}
					}
				}
				else
				{
					newFileNames.add(curFN);
				}
			}
			String inputFileNameArray[] = new String[newFileNames.size()];
			for (int i = 0; i < newFileNames.size(); i++)
			{
				inputFileNameArray[i] = newFileNames.get(i);
			}
			if (nonExistingFiles.size() > 0)
			{
				errorMessage = "Following file" + (nonExistingFiles.size() > 1 ? "s " : " ") +
				"could not be found: ";
				for (int i = 0; i < nonExistingFiles.size(); i++)
				{
					errorMessage += nonExistingFiles.get(i);
					if (i != nonExistingFiles.size() - 1)
					{
						errorMessage += ", ";
					}
				}
			}
			params = params.setInputFileNames(inputFileNameArray);
			return startIx;
		}
	}
	
	private void readOutputFileName(int startIx)
	{
		if (startIx < cliTokens.length - 1)
		{
			StringBuilder em = new StringBuilder();
			em.append("Superfluous arguments detected after ").append(startIx);
			if (startIx == 1){em.append("st");}
			else if (startIx == 2){em.append("nd");}
			else{em.append("th");}
			em.append(" token and ignored");
			errorMessage = em.toString();
		}
		else if (startIx >= cliTokens.length)
		{
			errorMessage = "No output file specified";
		}
		else
		{
			String fn = cliTokens[cliTokens.length - 1];
			if (!(new File(fn)).exists())
			{
				params = params.setOutputFileName(fn);				
			}
			else
			{
				errorMessage = fn + " exists already, will not overwrite.";
			}
		}
	}
	
	public String getErrorMessage(){return errorMessage;}
	public boolean errorEncountered(){return errorMessage != null;}
	
	public CLIMode(String args[])
	{
		params = new RunningParameters();
		if (args.length > 0) //Otherwise we just use the GUI
		{
			cliTokens = new String[args.length];
			System.arraycopy(args, 0, cliTokens, 0, args.length);
			createArguments();
			parseArguments(cliTokens);
		}
	}
	
	public CLIMode(RunningParameters params)
	{
		this.params = params;
	}
	
	private void createArguments()
	{
		cliArgs = new ArrayList<CommandLineArgument>();
		cliArgs.add(NumberArgument.getDoubleArgument("--rt1", "RT1Lax", "sets maximum allowed difference in RT1"));
		cliArgs.add(NumberArgument.getDoubleArgument("--rt2", "RT2Lax", "sets maximum allowed difference in RT2"));
		cliArgs.add(NumberArgument.getDoubleArgument("--rti", "RTILax", "sets maximum allowed difference in RTI"));
		cliArgs.add(NumberArgument.getDoubleArgument("--rt1Pen", "RT1Penalty", "sets penalty for RT1 difference"));
		cliArgs.add(NumberArgument.getDoubleArgument("--rt2Pen", "RT2PenaltyCoeff", "sets RT2 penalty to be x*RT1penalty"));
		cliArgs.add(NumberArgument.getDoubleArgument("--rtiPen", "RTIPenalty", "sets RTI penalty for RTI difference"));
		cliArgs.add(NumberArgument.getDoubleArgument("--minSpec", "SpectrumMatch", "sets minimum match for spectrums", 0, 1));
		cliArgs.add(NumberArgument.getIntegerArgument("--sim", "MinSimilarity", "sets minimum accepted similarity value", 0, 999));
		cliArgs.add(new BooleanArgument("--batch", "sets batch mode on")
		{
			public AlignmentParameters consume(AlignmentParameters old,
					String... tokens) throws ParseException
			{
				params.setUseBatch(true);
				return old;
			}
		});
		cliArgs.add(new BooleanArgument("--merge", "merges peaks after alignment")
		{
			public AlignmentParameters consume(AlignmentParameters old, String... tokens) throws ParseException
			{
				return old.setUseMerge(true);
			}
		});
	}
	
	private void parseArguments(String args[])
	{
		int tokenIx = 0;
		for (; tokenIx < args.length && !errorEncountered(); tokenIx++)
		{
			String curToken = args[tokenIx];
			CommandLineArgument matchingArgument = null;
			for (CommandLineArgument ca : cliArgs)
			{
				if (ca.matches(curToken))
				{
					matchingArgument = ca;
					break;
				}
			}
			if (matchingArgument == null){break;}
			else
			{
				String tokens[] = new String[matchingArgument.tokensUsed()];
				System.arraycopy(args, tokenIx + 1, tokens, 0, matchingArgument.tokensUsed());
				tokenIx += matchingArgument.tokensUsed();
				try
				{
					params.setAlignParams(matchingArgument.consume(params.getAlignParams(), tokens));
				}
				catch (ParseException e)
				{
					errorMessage = e.getMessage();
				}
			}
		}
		if (!errorEncountered())
		{
			tokenIx = readInputFileNames(tokenIx);
		}
		if (!errorEncountered())
		{
			readOutputFileName(tokenIx);
		}
	}
	
	private String printUsage()
	{
		StringBuilder sb = new StringBuilder("USAGE: GCGCAlign {switches} {inputfiles} outputfile\n");
		Collections.sort(cliArgs);
		sb.append("Where switches are:\n");
		for (CommandLineArgument arg : cliArgs)
		{
			sb.append(arg.usage()).append('\n');
		}
		return sb.toString();
	}
	
	public RunningParameters getParameters()
	{
		return params;
	}
	
	public static void test(String args[])
	{
		CLIMode m = new CLIMode(args);
		if (m.errorEncountered())
		{
			System.out.println(m.printUsage());
			System.err.println(m.getErrorMessage());
		}
		else
		{
			System.out.printf("%s\n", m.params);
		}
	}

	public void run()
	{
		File files[] = new File[params.getInputFileNames().length];
		for (int i = 0; i < params.getInputFileNames().length; i++)
		{
			files[i] = new File(params.getInputFileNames()[i]);
		}
		List<GCGCData> GCGCRuns = null;
		try
		{
			GCGCRuns = GCGCData.loadfiles("\t", true, true, false, files);

		} catch (IOException e)
		{
			System.err.println("Encountered error reading files:\n" + e.getMessage() + 
					"\nBatch process quitting.");
			return;
		}
		
		Aligner al = new ScoreAligner(new InputSet(GCGCRuns), params.getAlignParams());
		Alignment result = al.align();
		try
		{
			File outputFile = new File(params.getOutputFileName());
			System.out.println("Writing finished alignment to " + params.getOutputFileName());
			result.SaveToFile(outputFile, "\t");
		} catch (IOException e)
		{
			System.err.println("Saving to " + params.getOutputFileName() + " failed:\n" +
					e.getMessage() + "\nBatch process quitting.");
			return;
		}
	}
}
