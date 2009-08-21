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
package guineu.modules.mylly.helper;



import guineu.modules.mylly.gcgcaligner.alignment.Alignment;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentRow;
import guineu.modules.mylly.gcgcaligner.alignment.AlignmentSorterFactory;
import guineu.modules.mylly.gcgcaligner.datastruct.Pair;
import guineu.modules.mylly.gcgcaligner.filter.FilterFunction;
import guineu.modules.mylly.gcgcaligner.filter.MapFunction;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

/**
 * Namespace for helper functions.
 * @author jmjarkko
 *
 */
public class Helper
{
	
	public static String statistics(Alignment al)
	{
		StringBuilder sb = new StringBuilder();
		
		List<AlignmentRow> sorted = al.getSortedAlignment(AlignmentSorterFactory.SORT_MODE.peaks);
		java.util.Map<Integer, Integer> peakCount = new HashMap<Integer, Integer>();
		int maxPeaks = 0;
		int totalPeaks = 0;
		int totalAlignments = al.rowCount();
		
		for (AlignmentRow row : sorted)
		{
			int curPeaks = row.nonNullPeakCount();
			if (maxPeaks < curPeaks){maxPeaks = curPeaks;}
			Integer count = peakCount.get(curPeaks);
			if (count == null){count = 0;}
			peakCount.put(curPeaks, count + 1);
			totalPeaks += curPeaks;
		}
		int maxSize = 0;
		for (Integer size : peakCount.values())
		{
			if (size > maxSize){maxSize = size;}
		}
		String printString = "%" + Integer.toString(maxPeaks).length() + "d : %" + Integer.toString(maxSize).length() + "d\n";
		sb.append("Alignment contained ").append(totalAlignments).append(" aligments");
		sb.append(" containing ").append(totalPeaks).append(" peaks.\n");
		Formatter format = new Formatter(sb);
		for (int i = maxPeaks; i > 0; i--)
		{
			Integer curPeakCount = peakCount.get(i) == null ? 0 : peakCount.get(i); 
			format.format(printString, i, curPeakCount);
		}
		return sb.toString();
	}

	public static String stripCAS(String str)
	{
		String parts[] = str.split("\\(CAS\\)");
		String newString;
		if (parts.length > 0)
		{
			newString = parts[0].trim();
		}
		else
		{
			newString = str.trim();
		}
		return newString;
	}

	@SuppressWarnings("unchecked")
	public static <T> Collection<T> filter(Collection<T> c, FilterFunction<T> f)
	{
		Collection<T> filtered;
		try
		{
			filtered = (Collection<T>) c.getClass().newInstance();
			for (T cur : c)
			{
				if (f.include(cur))
				{
					filtered.add(cur);
				}
			}
		} catch (InstantiationException e)
		{
			filtered = c;
		} catch (IllegalAccessException e)
		{
			filtered = c;
		}
		return filtered;
	}

	public static <T> List<T> filter(List<T> l, FilterFunction<T> f)
	{
		return (List<T>) filter((Collection<T>) l, f);
	}

	/**
	 * Throws {@link UnsupportedOperationException} in case remove-method is not
	 * supported by the underlying ListIterator of given <code>list</code>.
	 * @param <T>
	 * @param list
	 * @param f {@link FilterFunction} which map
	 * @return
	 */
	public static <T> List<T> inplaceFilter(List<T> list, FilterFunction<T> f)
	{
		ListIterator<T> iter = list.listIterator();
		while(iter.hasNext())
		{
			T current = iter.next();
			if (!f.include(current))
			{
				iter.remove();
			}
		}
		return list;
	}

	/**
	 * Throws {@link UnsupportedOperationException} in case set-method is not
	 * supported by the underlying ListIterator of given <code>list</code>.
	 * Can also throw {@link IllegalArgumentException} in case <code>MapFunction</code>
	 * maps given element to <code>null</code> and underlying <code>List</code> does not
	 * support <code>null</code> elements.
	 * @param <T>
	 * @param list
	 * @param f {@link MapFunction} which map
	 * @return
	 */
	public static <T> List<T> inplaceMap(List<T> list, MapFunction<T,T> f)
	{
		ListIterator<T> iter = list.listIterator();
		while(iter.hasNext())
		{
			T current = iter.next();
			iter.set(f.map(current));
		}
		return list;
	}

	/**
	 * @param sep TODO
	 * @param f
	 * @throws IOException 
	 */

	public static List<Pair<List<String>, Double>> makeRepresentativeList(File f, String sep) throws IOException
	{
		List<Pair<List<String>, Double>> l = new ArrayList<Pair<List<String>, Double>>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		int[] indices;
		while ((indices = readHeader(br.readLine(), sep)).length == 0);
		String line;
		while ((line = br.readLine()) != null)
		{
			String tokens[] = line.split(sep);
			List<String> names = new ArrayList<String>(indices.length - 1);
			boolean error = false;
			try
			{
				for (int i = 0; i < indices.length - 1; i++)
				{
					String str = tokens[indices[i]];
					if (!"".equals(str))
					{						
						names.add(stripCAS(str));
					}
				}
				Pair<List<String>, Double> pair = new Pair<List<String>, Double>(names, 
						Double.parseDouble(tokens[indices[indices.length - 1]].replace(',', '.')));
				l.add(pair);
			}
			catch (IndexOutOfBoundsException e1)
			{
				error = true;
			}
			catch (NumberFormatException e2)
			{
				error = true;
			}
			if (error)
			{
//				throw new IOException("Line was malformed:\n" + line);
				//just skip the line
			}
		}
		return l;
	}

	/**
	 * 
	 * @param line
	 * @param sep TODO
	 * @return zero-length array in case the line isn't header line.
	 */
	private static int[] readHeader(String line, String sep)
	{
		if (line == null){return new int[0];}
		String tokens[] = line.split(sep);
		List<Integer> indices = new ArrayList<Integer>();
		int riIndex = -1;
		int i = 0;

		for (String token : tokens)
		{
			if(token.contains("Name ")|| i == 0)
			{
				indices.add(i);
			}
			else if ("RI".equals(token))
			{
				riIndex = i;
			}
			i++;
		}
		if (riIndex >= 0)
		{
			indices.add(riIndex);
		}
		else{indices.clear();}


		int indexArray[] = new int[indices.size()];
		for (i = 0; i < indices.size(); i++)
		{
			indexArray[i] = indices.get(i);
		}
		return indexArray;
	}

	//	/**
	//	 * Returns <code>Collection c</code> mapped to a new <code>Collection</code>.
	//	 * If there is no zero-argument constructor which creates an empty collection
	//	 * for input collection, unmodified original collection is returned. Otherwise
	//	 * original <code>Collection c</code> is unmodified and a new <code>Collection
	//	 * </code> with mapped elements is returned. 
	//	 * @param <T>
	//	 * @param c <code>Collection</code> 
	//	 * @param f
	//	 * @return new <code>Collection</code> of the same type with mapped elements or
	//	 * <code>Collection c</code> unmodified is no constructor with zero arguments is
	//	 * visible or available. 
	//	 */
	//	@SuppressWarnings("unchecked")
	//	public static <T> Collection<T> map(Collection<T> c, MapFunction<T,T> f)
	//	{
	//		Collection<T> mapped;
	//		try
	//		{
	//			mapped = (Collection<T>) c.getClass().newInstance();
	//			for (T cur : c)
	//			{
	//				mapped.add(f.map(cur));
	//			}
	//		} catch (InstantiationException e)
	//		{
	//			mapped = c;
	//		} catch (IllegalAccessException e)
	//		{
	//			mapped = c;
	//		}
	//		return mapped;
	//	}
		
		/**
		 * Returns <code>Collection c</code> mapped to a new <code>Collection</code>.
		 * If there is no zero-argument constructor which creates an empty collection
		 * for input collection, unmodified original collection is returned. Otherwise
		 * original <code>Collection c</code> is unmodified and <code>null</code> is 
		 * returned.
		 * @param <T>
		 * @param c <code>Collection</code> 
		 * @param f
		 * @return new <code>Collection</code> of the same type with mapped elements or
		 * <code>Collection c</code> unmodified is no constructor with zero arguments is
		 * visible or available. 
		 */
		@SuppressWarnings("unchecked")
		public static <T,Y> Collection<Y> map(Collection<T> c, MapFunction<T,Y> p)
		{
			Collection<Y> mapped;
			try
			{
				mapped = (Collection<Y>) c.getClass().newInstance();
				for (T cur : c)
				{
					mapped.add(p.map(cur));
				}
			}
			catch (InstantiationException e)
			{
				mapped = null;
			} catch (IllegalAccessException e)
			{
				mapped = null;
			}
			return mapped;
		}

}
