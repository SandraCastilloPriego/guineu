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
package guineu.modules.mylly.gcgcaligner.test;


import guineu.modules.mylly.gcgcaligner.alignment.Aligner;
import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentPath;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.alignment.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatumWithConcentration;
import guineu.modules.mylly.gcgcaligner.datastruct.InputSet;
import guineu.modules.mylly.gcgcaligner.filter.GCGCDatumFilter;
import guineu.modules.mylly.gcgcaligner.filter.LinearNormalizer;
import guineu.modules.mylly.gcgcaligner.scorer.ScoreCalculator;
import guineu.modules.mylly.gcgcaligner.scorer.SpectrumDotProd;
import guineu.modules.mylly.helper.Helper;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class Tester
{

	private final static String FILTERS[] = null;
	private static File files[];

	private static List<GCGCData> data;
	private final static String SEP = "\t";

	@SuppressWarnings("unchecked")
	private static Alignment testAligner(Class AlignerClass, GCGCDatumFilter filter)
	{
		Alignment alignment = null;
		AlignmentParameters defaults = new AlignmentParameters();
		try
		{
//			ScoreCalculator calc = new RequireNameRTIScoreCalculator();
			ScoreCalculator calc = new SpectrumDotProd();
			Constructor c = AlignerClass.getConstructor(InputSet.class, defaults.getClass());
			Aligner a = (Aligner) c.newInstance(new Object[] {new InputSet(data), defaults});
			a.setScoreCalculator(calc);
			long time = System.currentTimeMillis();
			alignment = a.align();
			System.out.printf("Generating alignment took %d ms\n", System.currentTimeMillis() - time);
			boolean usedStd = false;

			time = System.currentTimeMillis();

			AlignmentRow std = findStandard(alignment);

			if (std != null)
			{
				List<AlignmentRow> stds = new ArrayList<AlignmentRow>();
				stds.add(std);
				LinearNormalizer ln = new LinearNormalizer(stds);
				alignment = ln.map(alignment);
				usedStd = true;
			}
			time = System.currentTimeMillis() - time;
			if (usedStd)
			{
				System.out.printf("Finding std and normalizing took %d ms\n", time);
			}
			else
			{
				System.out.printf("Trying to find std took %d ms\n", time);
			}

		}
		catch (Exception e){e.printStackTrace();}
		return alignment;

	}

	private static <T> void printStatistics(Class<T> AlignerClass, String dir)
	{
		File FilterFiles[] = null; 
		try
		{
			files = (new File(dir)).listFiles();
			data = GCGCData.loadfiles(SEP, true, true, false, files);
			if (FILTERS != null)
			{
				FilterFiles = new File[FILTERS.length];
				for (int i = 0; i < FILTERS.length; i++)
				{
					FilterFiles[i] = new File(FILTERS[i]);
				}
			}
		}
		catch (IOException e){}
		GCGCDatumFilter f = null;

		Alignment al = testAligner(AlignerClass, f);
		System.out.printf("%s\n", Helper.statistics(al));
	}

	private static void printToSB(StringBuilder sb, double d)
	{
		NumberFormat formatter = new DecimalFormat("####.####");
		sb.append(formatter.format(d)).append('\t');
	}

	private static AlignmentRow findStandard(Alignment al)
	{
		final double stdMass = 316.0;
		AlignmentRow std = null;
		for (AlignmentRow row : al.getAlignment())
		{
			if (std != null){break;}
			for (GCGCDatum d : row)
			{
				if (d.hasQuantMass() && d.getQuantMass() == stdMass)
				{
					std = row;
					break;
				}
			}
		}
		return std;
	}

	private static void writeAPs(File f, boolean writeConc, List<AlignmentPath> aps, String names[])
	{
		StringBuilder headerSB = new StringBuilder();
		String headerInfos[] = {"Mass", "RT1", "RT2", "Num Found", "Name",
				"Max similarity", "Mean similarity", "Similarity std dev"};
		for (int i = 0; i < headerInfos.length; i++)
		{
			headerSB.append(headerInfos[i]).append('\t');
		}
		for (int i = 0; i < names.length; i++)
		{
			headerSB.append(names[i]);
			if (i != names.length - 1)
			{
				headerSB.append('\t');
			}
		}
		headerSB.append('\n');
		BufferedWriter bw;
		try
		{
			bw = new BufferedWriter(new FileWriter(f));
			bw.write(headerSB.toString());
			for (AlignmentPath p : aps)
			{
				StringBuilder sb = new StringBuilder();
				printToSB(sb, p.getRT1());
				printToSB(sb, p.getRT2());
				printToSB(sb, p.nonEmptyPeaks());
				sb.append(p.names().get(0)).append('\t');
				printToSB(sb, p.getMaxSimilarity());
				printToSB(sb, p.getMeanSimilarity());
				printToSB(sb, p.getSimilarityStdDev());
				for (int i = 0; i < p.length(); i++)
				{
					GCGCDatum d = p.getPeak(i);
					if (d != null)
					{
						String str;
						if (d instanceof GCGCDatumWithConcentration && writeConc)
						{
							GCGCDatumWithConcentration temp = (GCGCDatumWithConcentration) d;
							str = String.valueOf(temp.getConc());
						}
						else
						{
							str = String.valueOf(d.getArea());
						}
						char ar[] = str.toCharArray();
						int ix = str.lastIndexOf('.');
						if (ix >= 0){ar[ix] = ',';}
						sb.append(new String(ar));
					}
					sb.append('\t');
				}
				bw.write(sb.toString() + "\n");
			}
			bw.close();
		}
		catch (IOException e){}
	}

//	private static void readAndAlignFiles()
//	{
//	String path = "D:\\Data\\flavoout\\quantified";
//	files = (new File(path)).listFiles();
//	try
//	{
//	data = GCGCData.loadfiles(SEP, true, false, true, files);
//	String names[] = new String[data.size()];
//	ScoreAligner al = new ScoreAligner(new InputSet(data), new AlignmentParameters());
//	List<AlignmentPath> aps = al.getAlignmentPaths();
//	File outputFile = new File(path + "\\results_conc.txt");
//	writeAPs(outputFile, true, aps, names);
//	outputFile = new File(path + "\\results_area.txt");
//	writeAPs(outputFile, false, aps, names);
//	}
//	catch (IOException e)
//	{
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//	}

//	}

	public static void main(String args[]) throws Exception
	{
//		readAndAlignFiles();
		printStatistics(ScoreAligner.class, args[0]);
	}
}
