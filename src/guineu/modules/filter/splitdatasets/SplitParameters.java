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
package guineu.modules.filter.splitdatasets;

import guineu.main.GuineuCore;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.util.dialogs.ExitCode;
import java.util.List;

public class SplitParameters extends SimpleParameterSet {
       public static final String parameterOption = "No parameter";
       public static final ComboParameter<Object> ValueSource = new ComboParameter<Object>(
                "Parameter name", "Name of the parameter used to split the data set", new Object[]{parameterOption});
        public SplitParameters() {
                super(new UserParameter[]{ValueSource});
        }
        
        @Override
        public ExitCode showSetupDialog() {

                List<String> sampleParameters = GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName();

                Object parametersChoices[] = new Object[sampleParameters.size() + 1];
                parametersChoices[0] = SplitParameters.ValueSource;
                
                System.arraycopy(sampleParameters.toArray(new String[0]), 0, parametersChoices, 1,
                        sampleParameters.size());
                getParameter(SplitParameters.ValueSource).setChoices(
                        parametersChoices);

                return super.showSetupDialog();
        }
}
