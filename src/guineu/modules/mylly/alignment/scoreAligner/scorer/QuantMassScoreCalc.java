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
public class QuantMassScoreCalc implements ScoreCalculator
{	
	/**
	 * @see gcgcaligner.scorer.ScoreCalculator#calculateScore(gcgcaligner.alignment.AlignmentPath, gcgcaligner.datastruct.GCGCDatum, gcgcaligner.alignment.AlignmentParameters)
	 */
	public double calculateScore(
			Peak path, 
			Peak peak,
			ScoreAlignmentParameters params)
	{
		double score = getWorstScore();
		if (peak.hasQuantMass() && 
				peak.getQuantMass() == path.getQuantMass() && 
				path.matchesWithName(peak))
		{
			score = Math.abs(path.getRT1() - peak.getRT1()) * (Double)params.getParameterValue(ScoreAlignmentParameters.rt1Penalty) +
			Math.abs(path.getRT2() - peak.getRT2()) * (Double)params.getParameterValue(ScoreAlignmentParameters.rt2Penalty)+
			Math.abs(path.getRTI() - peak.getRTI()) * (Double)params.getParameterValue(ScoreAlignmentParameters.rtiPenalty);
		}
		return score;
	}

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#getWorstScore()
	 */
	public double getWorstScore()
	{
		return Double.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#matches(gcgcaligner.AlignmentPath, gcgcaligner.GCGCDatum, gcgcaligner.AlignmentParameters)
	 */
	public boolean matches(Peak path, Peak peak,
			ScoreAlignmentParameters params)
	{
		return calculateScore(path, peak, params) < getWorstScore();
	}
	
	public boolean isValid(GCGCDatum peak)
	{
		if (peak.hasQuantMass())
		{
			return true;
		}
		return false;
	}

	public String name()
	{
		return "uses quantified masses";
	}
}
