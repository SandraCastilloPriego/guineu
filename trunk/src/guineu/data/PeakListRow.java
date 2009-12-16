/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.data;

import java.util.Vector;

/**
 *
 * @author SCSANDRA
 */
public interface PeakListRow {

    public int getID();

    public void setID(int i);

    public void setPeak(String name, Double value);

    public Object getPeak(String ExperimentName);

    public Object getPeak(int col, Vector<String> sampleNames);

    public Object[] getPeaks();

    public void removePeaks();

    public void removeNoSamplePeaks(String[] group);

    public int getNumberPeaks();

    public PeakListRow clone();

    public boolean isSelected();

    public void setPeak(String str, String get);

    public Object getVar(String varName);

    public void setVar(String varName, Object value);
	
}
