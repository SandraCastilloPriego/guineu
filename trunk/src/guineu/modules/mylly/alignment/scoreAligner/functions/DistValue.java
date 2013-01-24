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
package guineu.modules.mylly.alignment.scoreAligner.functions;

/**
 *
 * @author Jarkko Miettinen
 */
public class DistValue implements Comparable<DistValue>, Cloneable
{
	public final static DistValue nullObject;
	public final static double NO_VALUE = -Double.MAX_VALUE;
	
	static
	{
		nullObject = new DistValue(NO_VALUE);
		nullObject._isNull = true;
	}
	
	private boolean _isNull;
	private double _dist;
	
	public DistValue(double dist)
	{
		_dist = dist;
		_isNull = false;
	}


	public DistValue(DistValue val)
	{
		_dist = val._dist;
		_isNull = val._isNull;
	}

	public void setDistance (double dist){
		this._dist = dist;
		_isNull = false;		
	}

	public double distance()
	{		
		return _dist;
	}
	
	public boolean isNull()
	{
		return _isNull;
	}
	
	public static DistValue getNull(){return nullObject;}

	public int compareTo(DistValue o)
	{
		int comparison;
		if (isNull())
		{
			comparison = o.isNull() ? 0 : 1; 
		}
		else if (o.isNull())
		{
			comparison = -1;
		}
		else
		{
			comparison = 
				distance() < o.distance() ? -1 :
				distance() > o.distance() ?  1 :
					0;
		}
		return comparison;
	}
	
        @Override
	public boolean equals(Object o)
	{
		return (o instanceof DistValue &&
				((DistValue) o)._dist == _dist);
	}
	
        @Override
	public DistValue clone()
	{
		DistValue val;
		try
		{
			val = (DistValue) super.clone();
		} catch (CloneNotSupportedException e)
		{
			throw new Error("Clone was not supported even though it should've been");
		}
		return val;
	}
	
        @Override
	public String toString()
	{
		return isNull() ? "" : Double.toString(_dist);
	}	

}
