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
package guineu.modules.mylly.gcgcaligner.alignment;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * @author jmjarkko
 */
public class AlignmentParameters implements Cloneable
{
	public final static double DEFAULT_RT1_LAX = 15.0;
	public final static double DEFAULT_RT2_LAX = 0.3;
	public final static double DEFAULT_RTI_LAX = 20;
	public final static double DEFAULT_MIN_SPEC_MATCH = 0.75;
	public final static double DEFAULT_MIN_COMPOUND_CERTAINTY = 700.0;
	public final static double DEFAULT_RT_PENALTY = 5.0;
	public final static double DEFAULT_RT2_PENALTY_COEFF = 7;
	public final static double DEFAULT_RT2_PENALTY = DEFAULT_RT2_PENALTY_COEFF * DEFAULT_RT_PENALTY;
	public final static double DEFAULT_UNIQUE_MATCH_BONUS = -15.0;
	public final static double DEFAULT_NAME_MATCH_BONUS = -100.0;
	public final static double DEFAULT_GAP_PENALTY = 500.0;
	public final static double DEFAULT_CAS_BONUS = -100.0;
	
	public final static double DEFAULT_RTI_PENALTY = 40.0;
	public final static double DEFAULT_MIN_SIMILARITY = 600;
	public final static boolean DEFAULT_MERGE = false;
	public final static boolean DEFAULT_CONCENTRATIOM = true;
	
	private double rt1Lax;
	private double rt2Lax;
	
	private double gapPenalty;
	private double rt1Penalty;
	private double rt2Penalty;
	private double minSpectrumMatch;
	private double nameMatchBonus;
	private double rtiPenalty;
	private double minSimilarity;
	private double rtiLax;
	private boolean useMerge;
	private boolean useConcentration;

	public String toString(String sep, NumberFormat f)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("RT 1 lax").append(sep);
		if (f != null){sb.append(f.format(rt1Lax));}
		sb.append("\nRT 2 lax").append(sep);
		if (f != null){sb.append(f.format(rt2Lax));}
		sb.append("\nRI  lax").append(sep);
		if (f != null){sb.append(f.format(rtiLax));}
		sb.append("\nRT 1 penalty").append(sep);
		if (f != null){sb.append(f.format(rt1Penalty));}
		sb.append("\nRT 2 penalty").append(sep);
		if (f != null){sb.append(f.format(rt2Penalty));}
		sb.append("\nRI  penalty").append(sep);
		if (f != null){sb.append(f.format(rtiPenalty));}
		sb.append("\nminimum spectrum match").append(sep);
		if (f != null){sb.append(f.format(minSpectrumMatch));}
		sb.append("\nbonus for matching names").append(sep);
		if (f != null){sb.append(f.format(nameMatchBonus));}
		sb.append("\nminimum similarity").append(sep);
		if (f != null){sb.append(f.format(minSimilarity));}
		sb.append("\nuse merge").append(sep).append(useMerge);
		return sb.toString();
	}
	
	public String toString()
	{
		return toString(" ",new DecimalFormat("####.####"));
	}
	
	public AlignmentParameters(double rt1lax, double rt2lax, double rtiLax,
			double rtPenalty, double rt2Penalty, double minSpecMatch, 
			double nameMatchBonus, double rtiPenalty, double minSimilarity, boolean useMerge, boolean useConcentration)
	{
		this.rt1Lax = rt1lax;
		this.rt2Lax = rt2lax;
		this.rtiLax = rtiLax;
		this.rt1Penalty = rtPenalty;
		this.rt2Penalty = rt2Penalty;
		this.minSpectrumMatch = minSpecMatch;
		this.nameMatchBonus = nameMatchBonus;
		this.rtiPenalty = rtiPenalty;
		this.gapPenalty = rt1Lax * rtPenalty  + rt2Lax * rt2Penalty + rtiLax * rtiPenalty;
		this.minSimilarity = minSimilarity;
		this.useMerge = useMerge;
		this.useConcentration = useConcentration;
	}
	
	public AlignmentParameters(double rt1lax, double rt2lax, double minNameMatch)
	{
		this(rt1lax, rt2lax, AlignmentParameters.DEFAULT_RTI_LAX, DEFAULT_RT_PENALTY, DEFAULT_RT2_PENALTY, 
				minNameMatch, DEFAULT_NAME_MATCH_BONUS, 
				AlignmentParameters.DEFAULT_RTI_PENALTY, AlignmentParameters.DEFAULT_MIN_SIMILARITY, AlignmentParameters.DEFAULT_MERGE, AlignmentParameters.DEFAULT_CONCENTRATIOM);
	}
	
	public static AlignmentParameters getDefaultParameters()
	{
		return new AlignmentParameters();
	}
	
	public AlignmentParameters()
	{
		this(DEFAULT_RT1_LAX,
				DEFAULT_RT2_LAX,
				AlignmentParameters.DEFAULT_RTI_LAX,
				DEFAULT_RT_PENALTY,
				DEFAULT_RT2_PENALTY,
				DEFAULT_MIN_SPEC_MATCH,
				DEFAULT_NAME_MATCH_BONUS, 
				DEFAULT_RTI_PENALTY, 
				DEFAULT_MIN_SIMILARITY, AlignmentParameters.DEFAULT_MERGE, AlignmentParameters.DEFAULT_CONCENTRATIOM);
	}
	
	public double getLaxRT1(){return rt1Lax;}
	public double getLaxRT2(){return rt2Lax;}
	
	public double getMinSpectrumMatch(){return minSpectrumMatch;}

	public double getRT1Penalty(){return rt1Penalty;}
	public double getRT1Lax(){return rt1Lax;}
	public double getRT2Lax(){return rt2Lax;}
	public double getRTILax(){return rtiLax;}
	public double getGapPenalty(){return gapPenalty;}

	public double getNameMatchBonus(){return nameMatchBonus;}
	public double getRTIPenalty(){return rtiPenalty;}
	public double getMinSimilarity(){return minSimilarity;}
	public double getRT2PenaltyCoeff(){return rt2Penalty/rt1Penalty;}
	public double getRT2Penalty(){return rt2Penalty;}
	
	public boolean useMerge(){return useMerge;}
	public boolean useConcentration(){return useConcentration;}
	
	public AlignmentParameters clone()
	{
		AlignmentParameters clone = new AlignmentParameters(rt1Lax,
				rt2Lax, rtiLax, rt1Penalty, getRT2Penalty(), 
				minSpectrumMatch, nameMatchBonus, rtiPenalty, minSimilarity, useMerge, useConcentration);
		return clone;
	}

	public AlignmentParameters setRTILax(double rtiLax)
	{
		AlignmentParameters clone = clone();
		clone.rtiLax = rtiLax;
		return clone;
	}


	public AlignmentParameters setRTIPenalty(double rtiPenalty)
	{
		AlignmentParameters clone = clone();
		clone.rtiPenalty = rtiPenalty;
		return clone;
	}

	public AlignmentParameters setMinSimilarity(double ignoreRatio)
	{
		AlignmentParameters clone = clone();
		clone.minSimilarity = ignoreRatio;
		return clone;
	}

	public AlignmentParameters setMinSpectrumMatch(double minSpectrumMatch)
	{
		AlignmentParameters clone = clone();
		clone.minSpectrumMatch = minSpectrumMatch;
		return clone;
	}

	public AlignmentParameters setNameMatchBonus(double nameMatchBonus)
	{
		AlignmentParameters clone = clone();
		clone.nameMatchBonus = nameMatchBonus;
		return clone;
	}

	public AlignmentParameters setRT1Lax(double rt1Lax)
	{
		AlignmentParameters clone = clone();
		clone.rt1Lax = rt1Lax;
		return clone;
	}

	public AlignmentParameters setRT1Penalty(double rt1Penalty)
	{
		AlignmentParameters clone = clone();
		clone.rtiPenalty = rt1Penalty;
		return clone;
	}

	public AlignmentParameters setRT2Lax(double rt2Lax)
	{
		AlignmentParameters clone = clone();
		clone.rt2Lax = rt2Lax;
		return clone;
	}
	
	public AlignmentParameters setUseMerge(boolean newVal)
	{
		AlignmentParameters clone = clone();
		clone.useMerge = newVal;
		return clone;
	}
	
	public AlignmentParameters setUseConcentration(boolean newVal)
	{
		AlignmentParameters clone = clone();
		clone.useConcentration = newVal;
		return clone;
	}


}
