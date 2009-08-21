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
package guineu.modules.mylly.alignment.scoreAligner.scorer;


import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.Peak;



/**
 * @author jmjarkko
 */
public class RTIScoreCalculator implements ScoreCalculator
{

	private double rt1Penalty;
	private double rt2Penalty;
	private double nameMatchBonus;
	private ScoreAlignmentParameters lastParams;
	private double rtiPenalty;

	public double calculateScore(Peak path, Peak peak, ScoreAlignmentParameters params)
	{
		if (params != null && params != lastParams)
		{
			rtiPenalty = (Double)params.getParameterValue(ScoreAlignmentParameters.rtiPenalty);
			rt1Penalty = (Double)params.getParameterValue(ScoreAlignmentParameters.rt1Penalty);
			rt2Penalty = (Double)params.getParameterValue(ScoreAlignmentParameters.rt2Penalty);
			nameMatchBonus = (Double)params.getParameterValue(ScoreAlignmentParameters.nameMatchBonus);
		}
		double rt1Diff = Math.abs(path.getRT1() - peak.getRT1());
		double score = rt1Diff * rt1Penalty;
		double rt2Diff = Math.abs(path.getRT2() - peak.getRT2());
		score += rt2Penalty * rt2Diff;
		if (path.matchesWithName(peak)){score += nameMatchBonus;}
		score += rtiPenalty * Math.abs(path.getRTI() - peak.getRTI());
		return score;
	}

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#matches(gcgcaligner.AlignmentPath, gcgcaligner.GCGCDatum, gcgcaligner.AlignmentParameters)
	 */
	public boolean matches(Peak path, Peak peak, ScoreAlignmentParameters params)
	{
		double gapPenalty = (Double)params.getParameterValue(ScoreAlignmentParameters.rt1Lax)*(Double)params.getParameterValue(ScoreAlignmentParameters.rt1Penalty)
				+ (Double)params.getParameterValue(ScoreAlignmentParameters.rt2Lax)*(Double)params.getParameterValue(ScoreAlignmentParameters.rt2Penalty)
				+ (Double)params.getParameterValue(ScoreAlignmentParameters.rtiLax)*(Double)params.getParameterValue(ScoreAlignmentParameters.rtiPenalty);

		return calculateScore(path, peak, params) < gapPenalty;
	}

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#getWorstScore()
	 */
	public double getWorstScore()
	{
		return Double.MAX_VALUE;
	}
	
	public boolean isValid(GCGCDatum peak)
	{
		return true;
	}

	public String name()
	{
		return "Use retention times";
	}
}
