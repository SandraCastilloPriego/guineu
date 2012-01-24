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
package guineu.modules.dataanalysis.correlations;

import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.BooleanParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.parameters.parametersType.DoubleParameter;

public class CorrelationParameters extends SimpleParameterSet {

        public static final String[] correlationType = {"Pearsons", "Spearmans", "Covariance"};
        public static final ComboParameter<String> correlationTypeSelection = new ComboParameter<String>(
                "Output file type", "Output file type", correlationType, correlationType[0]);

        public static final DoubleParameter cutoff = new DoubleParameter(
                "p-value cutoff", "p-value cutoff", 0.05);

        public static final BooleanParameter show = new BooleanParameter("Show p-value",
                "Show p-value", true);
        
        public CorrelationParameters() {
                super(new Parameter[]{correlationTypeSelection, cutoff, show});
        }

        
}
