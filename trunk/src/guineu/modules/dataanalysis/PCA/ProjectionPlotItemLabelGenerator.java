/*
 * Copyright 2007-2012 VTT Biotechnology
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
package guineu.modules.dataanalysis.PCA;

import guineu.parameters.ParameterSet;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYZDataset;

/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 * 
 */
public class ProjectionPlotItemLabelGenerator extends StandardXYItemLabelGenerator {

        private enum LabelMode {

                None, FileName, ParameterValue
        };
        private LabelMode[] labelModes;
        private int labelModeIndex = 0;

        ProjectionPlotItemLabelGenerator(ParameterSet parameters) {

                labelModes = new LabelMode[]{LabelMode.None};
                try {
                        if (parameters.getParameter(ProjectionPlotParameters.coloringType).getValue() == ColoringType.NOCOLORING) {
                                labelModes = new LabelMode[]{LabelMode.None, LabelMode.FileName};
                        }

                        if (parameters.getParameter(ProjectionPlotParameters.coloringType).getValue() == ColoringType.COLORBYFILE) {
                                labelModes = new LabelMode[]{LabelMode.None, LabelMode.FileName};
                        }

                        if (parameters.getParameter(ProjectionPlotParameters.coloringType).getValue() == ColoringType.COLORBYPARAMETER) {
                                labelModes = new LabelMode[]{LabelMode.None, LabelMode.FileName, LabelMode.ParameterValue};
                        }
                } catch (Exception e) {
                        labelModes = new LabelMode[]{LabelMode.None, LabelMode.FileName};
                }


        }

        protected void cycleLabelMode() {
                labelModeIndex++;

                if (labelModeIndex >= labelModes.length) {
                        labelModeIndex = 0;
                }

        }

        public String generateLabel(ProjectionPlotDataset dataset, int series,
                int item) {

                switch (labelModes[labelModeIndex]) {
                        case None:
                        default:
                                return "";

                        case FileName:
                                return dataset.getRawDataFile(item).toString();

                        case ParameterValue:
                                int groupNumber = dataset.getGroupNumber(item);
                                Object paramValue = dataset.getGroupParameterValue(groupNumber);
                                if (paramValue != null) {
                                        return paramValue.toString();
                                } else {
                                        return "";
                                }

                }

        }

        public String generateLabel(XYDataset dataset, int series, int item) {
                if (dataset instanceof ProjectionPlotDataset) {
                        return generateLabel((ProjectionPlotDataset) dataset, series, item);
                } else {
                        return null;
                }
        }

        public String generateLabel(XYZDataset dataset, int series, int item) {
                if (dataset instanceof ProjectionPlotDataset) {
                        return generateLabel((ProjectionPlotDataset) dataset, series, item);
                } else {
                        return null;
                }
        }
}
