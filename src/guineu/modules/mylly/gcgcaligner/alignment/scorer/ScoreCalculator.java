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
public interface ScoreCalculator
{
	
	double calculateScore(Peak path, Peak peak, AlignmentParameters params);
	double getWorstScore();
	
	/**
	 * Is score calculated by calculate Score in any way meaningful?
	 * If path and peak don't match in ScoreCalculator's mind, value returned
	 * from calculateScore may still be finite, but matches returns false.
	 * @param path
	 * @param peak
	 * @param params
	 * @return
	 */
	boolean matches(Peak path, Peak peak, AlignmentParameters params);
	boolean isValid(GCGCDatum peak);
	
	String name();
}
