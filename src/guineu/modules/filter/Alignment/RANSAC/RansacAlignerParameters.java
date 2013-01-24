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
package guineu.modules.filter.Alignment.RANSAC;

import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.*;
import guineu.util.dialogs.ExitCode;
import java.text.NumberFormat;

public class RansacAlignerParameters extends SimpleParameterSet {

        public static final NumberFormat percentFormat = NumberFormat.getPercentInstance();
        public static final NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        public static final StringParameter peakListName = new StringParameter(
                "Peak list name", "Peak list name");
        public static final MZToleranceParameter MZTolerance = new MZToleranceParameter(
                "m/z tolerance",
                "Maximum allowed M/Z difference");
        public static final RTToleranceParameter RTToleranceValueAbs = new RTToleranceParameter(
                "RT tolerance after correction",
                "Maximum allowed absolute RT difference after the algorithm correction for the retention time");
        public static final RTToleranceParameter RTTolerance = new RTToleranceParameter(
                "RT tolerance",
                "Maximum allowed absolute RT difference");
        public static final IntegerParameter Iterations = new IntegerParameter(
                "RANSAC Iterations",
                "Maximum number of iterations allowed in the algorithm", 1000);
        public static final PercentParameter NMinPoints = new PercentParameter(
                "Minimun Number of Points",
                "Minimum number of aligned peaks required to fit the model");
        public static final DoubleParameter Margin = new DoubleParameter(
                "Threshold value",
                "Threshold value for determining when a data point fits a model",
                3.0);
        public static final BooleanParameter Linear = new BooleanParameter(
                "Linear model",
                "Switch between polynomial model or lineal model",
                false);

        public RansacAlignerParameters() {
                super(new UserParameter[]{peakListName, MZTolerance, RTToleranceValueAbs,
                                RTTolerance, Iterations, NMinPoints, Margin, Linear});
        }

        public ExitCode showSetupDialog() {
                RansacAlignerSetupDialog dialog = new RansacAlignerSetupDialog(this, null);
                dialog.setVisible(true);
                return dialog.getExitCode();
        }
}
