/*
 * Copyright 2007-2013 VTT Biotechnology
 * This file is part of Guineu.
 *
 * Guineu is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Guineu is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.mylly.filter.NameFilter;

public interface MapFunction<T,Y>
{
	/**
	 * Maps obj to a new object. If this map is used as a
	 * filter, it may return <code>null</code> to signify
	 * that this object should not be included in the result.
	 * Therefore check for nulls is needed when using such
	 * maps if result will not copy with nulls. 
	 * @param obj
	 * @return
	 */
	public Y map(T obj);
	
	/**
	 * Returns the name of this map.
	 * @return name.
	 */
	public String getName();
}
