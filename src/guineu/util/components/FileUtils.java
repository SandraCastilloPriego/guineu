/*
 * Copyright 2007-2013 VTT Biotechnology
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
import guineu.data.datamodels.OtherDataModel;
import guineu.data.DatasetType;
import guineu.data.datamodels.DatasetExpressionDataModel;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.datasets.SimpleBasicDataset;
import guineu.data.impl.datasets.SimpleExpressionDataset;
import guineu.data.impl.peaklists.SimplePeakListRowExpression;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.data.impl.peaklists.SimplePeakListRowOther;
import guineu.util.Tables.DataTableModel;

/**
 *
 * @author scsandra
 */
public class FileUtils {

    public static PeakListRow getPeakListRow(DatasetType type) {
        switch (type) {
            case LCMS:
                return new SimplePeakListRowLCMS();
            case GCGCTOF:
                return new SimplePeakListRowGCGC();
            case QUALITYCONTROL:
            case BASIC:
                return new SimplePeakListRowOther();
            case EXPRESSION:
                return new SimplePeakListRowExpression();
        }
        return null;
    }

    public static Dataset getDataset(Dataset dataset, String Name) {
        Dataset newDataset = null;
        switch (dataset.getType()) {
            case LCMS:
                newDataset = new SimpleLCMSDataset(Name + dataset.getDatasetName());
                break;
            case GCGCTOF:
                newDataset = new SimpleGCGCDataset(Name + dataset.getDatasetName());
                ((SimpleGCGCDataset) newDataset).setParameters(((SimpleGCGCDataset) dataset).getParameters());
                ((SimpleGCGCDataset) newDataset).setAligner(((SimpleGCGCDataset) dataset).getAligner());
                break;
            case QUALITYCONTROL:
            case BASIC:
                newDataset = new SimpleBasicDataset(Name + dataset.getDatasetName());
                break;
            case EXPRESSION:
                newDataset = new SimpleExpressionDataset(Name + dataset.getDatasetName());
                break;
        }
        newDataset.setType(dataset.getType());
        return newDataset;
    }

    public static DataTableModel getTableModel(Dataset dataset) {
        DataTableModel model = null;
        switch (dataset.getType()) {
            case LCMS:
                model = new DatasetLCMSDataModel(dataset);
                break;
            case GCGCTOF:
                model = new DatasetGCGCDataModel(dataset);
                break;
            case QUALITYCONTROL:
            case BASIC:
                model = new OtherDataModel(dataset);
                if(dataset.getRowColor() != null){
                        model.addRowColor(dataset.getRowColor());
                }
                break;
            case EXPRESSION:
                model = new DatasetExpressionDataModel(dataset);
                break;
        }
        return model;
    }
}
