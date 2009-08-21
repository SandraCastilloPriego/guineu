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

import guineu.modules.mylly.gcgcaligner.datastruct.Pair;
import guineu.modules.mylly.gcgcaligner.datastruct.Spectrum;
import guineu.modules.mylly.gcgcaligner.scorer.SpectrumDotProd;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class SpecProdCalc
{
	public static Spectrum parseSpec(String peakString)
	{
		List<Pair<Integer, Integer>> peaks = new ArrayList<Pair<Integer, Integer>>();
		
		String[] tokens = peakString.substring(2, peakString.length() - 2).split("\\p{Space}*,\\p{Space}*");
		for (String st : tokens)
		{
			String[] parts = st.split(":");
			
			peaks.add(new Pair<Integer, Integer>(
					Integer.parseInt(parts[0].trim()), 
					Integer.parseInt(parts[1].trim())));
		}
		return new Spectrum(peaks);
	}
	
	public static void calcProd(String str1, String str2)
	{
		SpectrumDotProd dp = new SpectrumDotProd();
		Spectrum s1 = parseSpec(str1);
		Spectrum s2 = parseSpec(str2);
		
		System.out.printf("%s\nCompared to:\n%s\n", s1, s2);
		System.out.printf("=> %f\n", dp.compareSpectraVal(s1, s2));
		
		System.out.printf("masses1: %s\n", Arrays.toString(s1.getMasses()));
		System.out.printf("intens1: %s\n", Arrays.toString(s1.getIntensities()));
		System.out.printf("masses2: %s\n", Arrays.toString(s2.getMasses()));
		System.out.printf("intens2: %s\n", Arrays.toString(s2.getIntensities()));
	}
	
	public static void main(String[] args)
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try
		{
			System.out.printf("Enter first spectrum\n");
			String spec1 = in.readLine();
			System.out.printf("Enter second spectrum\n");
			String spec2 = in.readLine();
			calcProd(spec1, spec2);
		} catch (IOException e)
		{}
	}
}
