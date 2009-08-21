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
package guineu.modules.mylly.gcgcaligner.alignment.scorer;




import guineu.modules.mylly.gcgcaligner.alignment.AlignmentParameters;
import guineu.modules.mylly.gcgcaligner.datastruct.GCGCDatum;
import guineu.modules.mylly.gcgcaligner.datastruct.Peak;



/**
 * @author jmjarkko
 */
public class RequireNameRTIScoreCalculator implements ScoreCalculator
{

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#calculateScore(gcgcaligner.AlignmentPath, gcgcaligner.GCGCDatum, gcgcaligner.AlignmentParameters)
	 */
	public double calculateScore(Peak path, Peak peak,
			AlignmentParameters params)
	{
		double score;
		double rtiDiff = Math.abs(path.getRTI() - peak.getRTI());
		if (rtiDiff > params.getRTILax()){return getWorstScore();}
		double rt2Diff = Math.abs(path.getRT2() - peak.getRT2());
		if (rt2Diff > params.getRT2Lax()){return getWorstScore();}
		double rt1Diff = Math.abs(path.getRT1() - peak.getRT1());
		if (rt1Diff > params.getRT1Lax()){return getWorstScore();}
		if (path.matchesWithName(peak))
		{
			score = rtiDiff * params.getRTIPenalty() +
			rt1Diff * params.getRT1Penalty() +
			rt2Diff * params.getRT2Penalty();
			if (path.matchesWithName(peak))
			{
				score += params.getNameMatchBonus();
			}
		}
		else
		{
			score = getWorstScore();
		}
		return score;
	}

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#matches(gcgcaligner.AlignmentPath, gcgcaligner.GCGCDatum, gcgcaligner.AlignmentParameters)
	 */
	public boolean matches(Peak path, Peak peak, AlignmentParameters params)
	{
		return path.matchesWithName(peak);
	}
	
	public double getWorstScore()
	{
		return Double.MAX_VALUE;
	}

	/* (non-Javadoc)
	 * @see gcgcaligner.ScoreCalculator#isValid(gcgcaligner.GCGCDatum)
	 */
	public boolean isValid(GCGCDatum peak)
	{
		return true;
	}
	
	public String name()
	{
		return "Uses name and retention times";
	}


	

}
