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

public class NumArgRange<T extends Number & Comparable<T>>
{
	
	public static enum RANGETEST {LESS, MORE, WITHIN};

	private T _lowerLimit;
	private T _upperLimit;
	private boolean _incLower;
	private boolean _incUpper;
	
	
	public static NumArgRange<Double> nonNegativeRealRange()
	{
		return new NumArgRange<Double>(0.0, true, Double.POSITIVE_INFINITY, false);
	}
	
	public static NumArgRange<Integer> intRange(int inclusiveLowerLimit, int exclusiveUpperLimit)
	{
		return new NumArgRange<Integer>(inclusiveLowerLimit, true, exclusiveUpperLimit, false);
	}
	
	public static NumArgRange<Double> wholeRealRange()
	{
		return new NumArgRange<Double>(Double.NEGATIVE_INFINITY, false, Double.POSITIVE_INFINITY, false);
	}
	
	public static NumArgRange<Double> positiveRealRange()
	{
		return new NumArgRange<Double>(0.0, false, Double.POSITIVE_INFINITY, false);
	}
	
	public NumArgRange<Double> zeroToOneRange()
	{
		return new NumArgRange<Double>(0.0, true, 1.0, true);
	}
	
	public NumArgRange(T lowerLimit, T upperLimit)
	{
		this(lowerLimit, true, upperLimit, true);
	}
	
	public NumArgRange(T lowerLimit, boolean lowerInclude, T upperLimit, boolean upperInclude)
	{
		_lowerLimit = lowerLimit;
		_upperLimit = upperLimit;
		_incLower = lowerInclude;
		_incUpper = upperInclude;
		int comparison = _lowerLimit.compareTo(_upperLimit);
		if (comparison > 0)
		{
			throw new IllegalArgumentException("Lower limit (" + _lowerLimit +  ") was " +
					"larger than upper limit (" + _upperLimit + ").");
		}
		else if (comparison == 0 && ! (_incLower && _incUpper))
		{
			throw new IllegalArgumentException("Lower limit (" + _lowerLimit + ") and upper limit (" 
			                                   + _upperLimit + ") cannot be same when both limits" +
			                                   		" are not inclusive.");
		}
	}

	public Class<? extends Number> getNumberClass()
	{
		return _lowerLimit.getClass();
	}

	public RANGETEST includes(T testee)
	{
		RANGETEST inclusion = RANGETEST.WITHIN;
		int comparison = testee.compareTo(_lowerLimit);
		if (comparison > 0 || (comparison == 0 && _incLower))
		{
			comparison = testee.compareTo(_upperLimit);
			if (comparison < 0 || (comparison == 0 && _incUpper))
			{
				inclusion = RANGETEST.WITHIN;
			}
			else
			{
				inclusion = RANGETEST.MORE;
			}
		}
		else
		{
			inclusion = RANGETEST.LESS;
		}
		
		return inclusion;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append((_incLower ? '[' : ']'));
		sb.append(_lowerLimit).append(", ").append(_upperLimit);
		sb.append((_incUpper ? ']' : '['));
		
		return sb.toString();
	}
	
}
