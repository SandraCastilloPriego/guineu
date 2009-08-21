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
package guineu.modules.mylly.gcgcaligner.filter;



import guineu.modules.mylly.gcgcaligner.datastruct.GCGCData;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import java.util.List;

/**
 * @author jmjarkko
 */
public interface GCGCDatumFilter
{

	/**
	 * @return Name of the filter.
	 */
	public String toString();
	
	public GCGCData filter(GCGCData toFilter);
	
	/**
	 * Filters given <code>Collection</code>
	 * @param listToFilter <code>Collection</code> to filter. Not to be modified.
	 * @return Filtered <code>Collection</code>. New Collection including only the
	 * non-filtered GCGCData.
	 */
	public List<GCGCDatum> filter(List<GCGCDatum> listToFilter);
	
	/**
	 * Filters given <code>array</code>.
	 * @param arrayToFilter <code>array</code> to filter.
	 * @return Filtered <code>array</code>.
	 */
	public GCGCDatum[] filter(GCGCDatum arrayToFilter[]);
}