/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.util.components;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.datamodels.DatasetLCMSDataModel;
import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.data.datamodels.ExperimentDataModel;
import guineu.data.datamodels.OtherDataModel;
import guineu.data.DatasetType;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimplePeakListRowGCGC;
import guineu.data.impl.SimplePeakListRowLCMS;
import guineu.data.impl.SimplePeakListRowOther;
import guineu.util.Tables.DataTableModel;

/**
 *
 * @author scsandra
 */
public class FileUtils {

	public static PeakListRow getPeakListRow(DatasetType type) {
		if (type == DatasetType.LCMS) {
			return new SimplePeakListRowLCMS();
		} else if (type == DatasetType.GCGCTOF) {
			return new SimplePeakListRowGCGC();
		} else if (type == DatasetType.OTHER) {
			return new SimplePeakListRowOther();
		} else {
			return null;
		}
	}

	public static Dataset getDataset(Dataset dataset, String Name) {
		Dataset newDataset = null;
		if (dataset.getType() == DatasetType.LCMS) {
			newDataset = new SimpleLCMSDataset(Name + dataset.getDatasetName());
		} else if (dataset.getType() == DatasetType.GCGCTOF) {
			newDataset = new SimpleGCGCDataset(Name + dataset.getDatasetName());
			((SimpleGCGCDataset) newDataset).setParameters(((SimpleGCGCDataset) dataset).getParameters());
			((SimpleGCGCDataset) newDataset).setAligner(((SimpleGCGCDataset) dataset).getAligner());
		} else if (dataset.getType() == DatasetType.OTHER) {
			newDataset = new SimpleOtherDataset(Name + dataset.getDatasetName());
		}
		newDataset.setType(dataset.getType());
		return newDataset;
	}

	public static DataTableModel getTableModel(Dataset dataset) {
		DataTableModel model = null;
		if (dataset.getType() == DatasetType.LCMS) {
			model = new DatasetLCMSDataModel(dataset);
		} else if (dataset.getType() == DatasetType.GCGCTOF) {
			model = new DatasetGCGCDataModel(dataset);
		} else if (dataset.getType() == DatasetType.OTHER) {
			model = new OtherDataModel(dataset);
		} else if (dataset.getType() == DatasetType.EXPERIMENTINFO) {
			model = new ExperimentDataModel(dataset);
		}
		return model;
	}

	public static Dataset cloneDataset(Dataset dataset, String name) {
		Dataset newDataset = null;

		if (dataset.getType() == DatasetType.LCMS) {
			newDataset = ((SimpleLCMSDataset) dataset).clone();
		} else if (dataset.getType() == DatasetType.GCGCTOF) {
			newDataset = new SimpleGCGCDataset(name + dataset.getDatasetName());
			newDataset.setType(DatasetType.GCGCTOF);
			((SimpleGCGCDataset) newDataset).setParameters(((SimpleGCGCDataset) dataset).getParameters());
			((SimpleGCGCDataset) newDataset).setAligner(((SimpleGCGCDataset) dataset).getAligner());
			for (String ColumnName : dataset.getAllColumnNames()) {
				newDataset.AddColumnName(ColumnName);
			}
			for (PeakListRow row : dataset.getRows()) {
				((SimpleGCGCDataset) newDataset).addAlignmentRow((SimplePeakListRowGCGC) row);
			}
		}
		return newDataset;
	}
}
