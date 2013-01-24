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
package guineu.modules.filter.Alignment.SerumHuNormalization;

import guineu.main.GuineuCore;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.DoubleParameter;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.IntegerParameter;
import guineu.parameters.parametersType.StringParameter;

public class SerumHuNormalizationParameters extends SimpleParameterSet {
        private static String[] parameters = GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName().toArray(new String[0]);
       
        
        public static final FileNameParameter filename = new FileNameParameter(
                "Plots filename",
                "Name of plot file. If the file exists, it won't be overwritten.");
        public static final FileNameParameter infoFilename = new FileNameParameter(
                "Information filename",
                "Name of information file. If the file exists, it won't be overwritten.");
        public static final ComboParameter<String>  order = new ComboParameter<String>(
                "Running order parameter",
                "Name of the running order parameter", parameters);
        public static final ComboParameter<String> identifier = new ComboParameter<String>(
                "Sample identification parameter",
                "Name of the parameter that contains the sample indentification. 0 if the sample is a control sample or 1 otherwise.", parameters);
        public static final ComboParameter<String> batches = new ComboParameter<String>(
                "Batch parameter",
                "Name of the parameter that contains bacth indentification.", parameters);
        public static final DoubleParameter loessBand = new DoubleParameter(
                "Loess Bandwidth",
                "Loess Bandwidth", new Double(0.0));
        public static final IntegerParameter iterations = new IntegerParameter(
                "Robustness iterations",
                "Robustness iterations", new Integer(0));
        public static final BooleanParameter extrapolation = new BooleanParameter(
                "Extrapolation",
                "Select this option if you want to extrapolate the loess function.",
                new Boolean(false));

        public SerumHuNormalizationParameters() {
                super(new UserParameter[]{filename, infoFilename, order, identifier, batches, loessBand, iterations, extrapolation});
                order.setChoices(parameters);
                identifier.setChoices(parameters);
                batches.setChoices(parameters);
        }        
        
}
