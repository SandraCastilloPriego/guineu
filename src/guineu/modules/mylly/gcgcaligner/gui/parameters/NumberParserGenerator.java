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
package guineu.modules.mylly.gcgcaligner.gui.parameters;



import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberParserGenerator
{
	/**
	 * Generates parsers for normal Number-classes found in java.lang.
	 * @param <T>
	 * @param clazz
	 * @return
	 */
	public static <T extends Number> NumberParser<T> genParser(final Class<T> clazz)
	{
		return new NumberParser<T>()
		{
			
			//private NumberFormat nf = isLimitedInteger(clazz) ?
				//	NumberFormat.getIntegerInstance(GCGCAlign.getCurrentLocale()) :
			//	isLimitedFloat(clazz) ?
				//		NumberFormat.getNumberInstance(GCGCAlign.getCurrentLocale()) :
				//			null;

				@SuppressWarnings("unchecked")
				public T parseString(String str) throws ParseException
				{
					if (isLimitedInteger(clazz) || isLimitedFloat(clazz))
					{
						Number n = null;/*nf.parse(str);*/
						boolean tooSmall = false;
						boolean tooLarge = false;

						if (isLimitedInteger(clazz))
						{
							long smallestValue = 0L;
							long largestValue = 0L;
							if (clazz == Integer.class || clazz == int.class)
							{
								smallestValue = Integer.MIN_VALUE;
								largestValue = Integer.MAX_VALUE;
								n = new Integer(n.intValue());
							}
							else if (clazz == Long.class || clazz == long.class)
							{
								smallestValue = Long.MIN_VALUE;
								largestValue = Long.MAX_VALUE;
								n = new Long(n.longValue());
							}
							else if (clazz == Short.class || clazz == short.class)
							{
								smallestValue = Short.MIN_VALUE;
								largestValue = Short.MAX_VALUE;
								n = new Short(n.shortValue());
							}
							else if (clazz == Byte.class || clazz == byte.class)
							{
								smallestValue = Byte.MIN_VALUE;
								largestValue = Byte.MAX_VALUE;
								n = new Byte(n.byteValue());
							}
							tooSmall = n.longValue() < smallestValue;
							tooLarge = n.longValue() > largestValue;
						}
						else if (clazz == Float.class || clazz == float.class)
						{
							tooSmall = n.floatValue() < -Float.MAX_VALUE;
							tooLarge = n.floatValue() > Float.MAX_VALUE;
							n = new Float(n.floatValue());
						}
						else if (clazz == Double.class || clazz == double.class)
						{
							tooSmall = n.doubleValue() < -Double.MAX_VALUE;
							tooLarge = n.doubleValue() > Double.MAX_VALUE;
							n = new Double(n.doubleValue());
						}
						if (tooSmall)
						{
							throw new ParseException(str + " was too large for " + clazz.getSimpleName(), 0);
						}
						else if (tooLarge)
						{
							throw new ParseException(str + " was too small for " + clazz.getSimpleName(), 0);
						}
						//N has already been created as a proper wrapper, we only need to
						//cast it to get correct output signature.
						return (T) n; 
 					}
					else
					{
						try
						{
							//BigThings
							if (clazz == BigDecimal.class)
							{
								return (T) new BigDecimal(str);
							}
							else if (clazz == BigInteger.class)
							{
								return (T) new BigInteger(str);
							}
						}
						catch (NumberFormatException e)
						{
							throw new ParseException(str + " was not parseable as a " + clazz.getSimpleName(), 0);
						}
					}
					assert(false) : "Should be never reached. Input string is " + str;
					return null;
				}

				public String stringify(T val)
				{
					String formatted = null;
				/*	if (nf != null)
					{
						if (isLimitedFloat(val.getClass()))
						{
							formatted = nf.format(val.doubleValue());
						}
						else
						{
							formatted = nf.format(val.longValue());
						}
					}
					else
					{
						formatted = val.toString();
					}*/
					return formatted;
				}
		};
	}

	private static boolean isLimitedInteger(Class<? extends Number> clazz)
	{
		return (clazz == Byte.class || clazz == Short.class || clazz == Integer.class || clazz == Long.class ||
				clazz == byte.class || clazz == Short.class || clazz == int.class     || clazz == long.class);
	}

	private static boolean isLimitedFloat(Class<? extends Number> clazz)
	{
		return (clazz == Float.class || clazz == Double.class ||
				clazz == float.class || clazz == double.class);
	}
}
