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

import guineu.data.impl.DatasetType;
import java.util.List;
import java.util.Vector;

public interface Dataset {

	public Dataset clone();

	public String getDatasetName();

	public Vector<String> getNameExperiments();

	public int getNumberCols();

	public int getNumberRows();	

	public void setDatasetName(String name);

	public DatasetType getType();

	public void setType(DatasetType type);

	public PeakListRow getRow(int row);

	public void removeRow(PeakListRow row);

	public void AddNameExperiment(String nameExperiment);

	public void AddNameExperiment(String nameExperiment, int position);

	public List<PeakListRow> getRows();

	public void AddRow(PeakListRow peakListRow);

	public String getInfo();

	public void setInfo(String info);
}
