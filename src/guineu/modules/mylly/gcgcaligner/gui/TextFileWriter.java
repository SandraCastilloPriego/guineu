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
package guineu.modules.mylly.gcgcaligner.gui;



import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class TextFileWriter
{
	public void writeToFile(File file, Alignment alignment, String separator) throws IOException
	{
		boolean containsMainPeaks = alignment.containsMainPeaks();
		
		NumberFormat formatter = new DecimalFormat("####.####");
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		out.write("Aligner" + separator + alignment.getAligner().getName() + "\n");
		out.write("Parameters:\n" + alignment.getParameters().toString(separator, formatter));
		out.write("\n\n");
		out.write("RT1");
		out.write(separator);
		out.write("RT2");
		out.write(separator);
		out.write("RI");
		out.write(separator);
		out.write("Quant Mass");
		out.write(separator);
		out.write("Num Found");
		out.write(separator);
		if (containsMainPeaks)
		{
			out.write("Difference to ideal peak");
			out.write(separator);
		}
                out.write("CAS");
		out.write(separator);
		out.write("Name");
		out.write(separator);
		out.write("All names");
		out.write(separator);
		out.write("Max similarity");
		out.write(separator);
		out.write("Mean similarity");
		out.write(separator);
		out.write("Similarity std dev");
		for(String st : alignment.getColumnNames()){out.write(separator);out.write(st + " Area");}
		out.write(separator);out.write("Spectrum");
		out.write('\n');
		for (AlignmentRow row : alignment.getAlignment())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(formatter.format(row.getMeanRT1())).append(separator);
			sb.append(formatter.format(row.getMeanRT2())).append(separator);
			sb.append(formatter.format(row.getMeanRTI())).append(separator);
			sb.append(formatter.format(row.getQuantMass())).append(separator);
			sb.append(row.nonNullPeakCount()).append(separator);
			if (containsMainPeaks)
			{
				if (!row.getDistValue().isNull())
				{
					sb.append(formatter.format(row.getDistValue().distance()));
				}
				sb.append(separator);
			}
                        
			String CAS = row.getCAS();
			sb.append(CAS).append(separator);
                        //Now write the names
			String name = row.getName();
                        sb.append(name).append(separator);
                        
			if (row.getNames().length > 0)
			{
				sb.append('"');
				for (int i = 0; i < row.getNames().length; i++)
				{
					sb.append(row.getNames()[i]);
					if (i != row.getNames().length - 1){sb.append(" || ");}
				}
				sb.append('"');
			}
			else
			{
				sb.append(row.getName());
			}
			sb.append(separator);

			sb.append(formatter.format(row.getMaxSimilarity())).append(separator);
			sb.append(formatter.format(row.getMeanSimilarity())).append(separator);
			sb.append(formatter.format(row.getSimilarityStdDev())).append(separator);
			for (GCGCDatum d : row)
			{
				if (d != null)
				{
					if(d.getConcentration() != -1){
						sb.append(formatter.format(d.getConcentration()));						
					}else{
						sb.append(formatter.format(d.getArea()));
					}
				}
				else
				{
					sb.append("NA");
				}
				sb.append(separator);
			}
			if (row.getSpectrum() != null)
			{
				sb.append(row.getSpectrum().toString());
			}
			out.write(sb.toString());
			out.write('\n');
		}
		out.close();
	}
}
