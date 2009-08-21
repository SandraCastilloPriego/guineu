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




import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.alignment.ScoreAligner;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.InternationalFormatter;
import javax.swing.text.NumberFormatter;

/**
 * @author jmjarkko
 */
public class PermutationTester
{
	private final static String peakFileNames[] = {"D:\\data\\Catharanthus\\10_9.txt"};
	
	private static GCGCData[] createCloneArray(int n, GCGCData ...data)
	{
		GCGCData dataArray[] = new GCGCData[n * data.length];
		int ix = 0;
		for (GCGCData d : data)
		{
			for (int i = 0; i < n; i++)
			{
				dataArray[ix++] = d.clone();
			}
		}
		return dataArray;
	}
	
	private static Alignment permuteTest(ScoreAligner al, AlignmentParameters params,
			GCGCData data[])
	{
		List<GCGCData> dataFiles = new ArrayList<GCGCData>(data.length);
		for (GCGCData d : data)
		{
			dataFiles.add(d);
		}
		java.util.Collections.shuffle(dataFiles);
		return al.getInstance(null, params).align();
	}
	
	private static int[] unrank(int index, int rank, int array[])
	{
		for (int i = index; i > 0; i--)
		{
			int temp = array[i - 1];
			array[i - 1] = array[rank % i];
			array[rank % i] = temp;
			rank = rank / i;
		}
		return array;
	}
	
	private static int[] permArray(int len, int rank)
	{
		if (rank >= factorial(len))
		{
			rank = (int) factorial(len) - 1;
		}
		if (rank < 0)
		{
			rank = 0;
		}
		int array[] = new int[len];
		for (int i = 0; i < array.length; i++)
		{
			array[i] = i;
		}
		array = unrank(array.length, rank, array);
		return array;
	}
	
	private static GCGCData[] unrank(int rank, GCGCData array[])
	{
		GCGCData copy[] = new GCGCData[array.length];
		int indexArray[] = permArray(copy.length, rank);
		for (int i = 0; i < copy.length; i++)
		{
			copy[i] = array[indexArray[i]];
		}
		return copy;
	}
	
	private final static Alignment normalTest(int n, ScoreAligner al, 
			AlignmentParameters params,
			GCGCData ...data)
	{
		GCGCData dataArray[] = createCloneArray(n, data);
		return al.getInstance(null, params).align();
	}
	
	private static long factorial(int i)
	{
		if (i < 2){return 1;}
		else{return i * factorial(i-1);}
	}

	private static void doTest(String args[])
	{
		File files[] = new File("D:\\data\\Catharanthus\\bench").listFiles();
//		for (int i = 0; i < files.length; i++)
//		{
//			files[i] = new File(peakFileNames[i]);
//		}
		GCGCData data[] = new GCGCData[files.length];
		int i = 0;
		try
		{
			for (GCGCData d : GCGCData.loadfiles("\t", true, true, false, files))
			{
				data[i++] = d;
			}
			AlignmentParameters defaults = new AlignmentParameters();
			int n = 10;
			try
			{
				n = Integer.parseInt(args[0]);
				if (n < 1){n = 10;}
			}
			catch (NumberFormatException e){}
			catch (ArrayIndexOutOfBoundsException e2){}
			ScoreAligner alignerTemplate = new ScoreAligner(null, new AlignmentParameters(), null);
			StatisticsCalculator c = new StatisticsCalculator();
			if (n >= factorial(data.length))
			{
				System.out.printf("Going through all permutations (%d)\n", factorial(data.length));
				int k = (int) factorial(data.length);
				for (int j = 0; j < k; j++)
				{
					System.out.printf("%d\n",j+1);
					c.addAlignment(alignerTemplate.getInstance(null, defaults).align());
				}
			}
			else
			{
				for (int j = 0; j < n; j++)
				{
					System.out.printf("%d\n",j+1);
					c.addAlignment(permuteTest(alignerTemplate, defaults, data));
				}
			}
			System.out.printf("%s", c.toString());
			
//			generateStatistics("Non-shuffle", normalTest(n, alignerTemplate, defaults, data));
//			generateStatistics("Shuffle" , permuteTest(n, alignerTemplate, defaults, data));
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
//		testPermutationGeneration(5);
		doTest(args);
	}
	
	private static class StatisticsCalculator
	{
		private Map<Integer, List<Integer>> peakCountToCount;
		private int max;
		
		public StatisticsCalculator()
		{
			peakCountToCount = new HashMap<Integer, List<Integer>>();
			max = 0;
		}
		
		public void addAlignment(Alignment al)
		{
			List<AlignmentRow> p = al.getAlignment();
			HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
			for (AlignmentRow r : p)
			{
				int peaks = r.nonNullPeakCount();
				Integer i = m.get(peaks);
				if (i == null){i = 0;}
				if (peaks > max){max = peaks;}
				m.put(peaks, ++i);
			}
			for (int i = max; i >= 1; i--)
			{
				List<Integer> counts = peakCountToCount.get(i);
				if (counts == null){counts = new ArrayList<Integer>();}
				Integer countInThisAlignment = m.get(i);
				if (countInThisAlignment == null){countInThisAlignment = 0;}
				counts.add(countInThisAlignment);
				peakCountToCount.put(i, counts);
			}
		}
		
		public double[][] getMeanAndStdDev()
		{
			double[][] meansAndStdDevs = new double[max][];
			for (int i = 1; i <= max; i++)
			{
				meansAndStdDevs[i-1] = getMeanAndStdDev(i);
			}
			return meansAndStdDevs;
		}
		
		public double[] getMeanAndStdDev(int count)
		{
			List<Integer> counts = peakCountToCount.get(count);
			if (counts != null)
			{
				int maxVal = Integer.MIN_VALUE;
				int minVal = Integer.MAX_VALUE;
				int n = 0;
				double mean = 0;
				double S = 0;
				for (int curPeakCount :  counts)
				{
					double delta = curPeakCount - mean;
					if (curPeakCount > maxVal){maxVal = curPeakCount;}
					if (curPeakCount < minVal){minVal = curPeakCount;}
					mean += delta/++n;
					S += delta*(curPeakCount-mean);
				}
				S = (counts.size() > 1) ? Math.sqrt(S / (n-1)) : 0;
				return new double[] {mean, S, minVal, maxVal};
			}
			else
			{
				return new double[] {0,0,0,0};
			}
		}
		
		public String toString()
		{
			double[][] meansAndStdDevs = getMeanAndStdDev();
			StringBuilder sb = new StringBuilder();
			NumberFormat formatter = new DecimalFormat("####.##");
			for (int i = meansAndStdDevs.length - 1; i >= 0; i--)
			{
				sb.append(i+1).append(": mean: ");
				sb.append(formatter.format(meansAndStdDevs[i][0]));
				sb.append(" std dev: ");
				sb.append(formatter.format(meansAndStdDevs[i][1]));
				sb.append(" min: ");
				sb.append((long)meansAndStdDevs[i][2]);
				sb.append(" max: ");
				sb.append((long)meansAndStdDevs[i][3]);
				sb.append('\n');
			}
			return sb.toString();
		}
	}
	
}
