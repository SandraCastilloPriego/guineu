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
package guineu.modules.dataanalysis.patternrecognition;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.UserParameter;
import guineu.parameters.parametersType.ComboParameter;
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.util.List;

public class PatternRecognitionParameters extends SimpleParameterSet {

        public static final ComboParameter<String> timePointParameter = new ComboParameter<String>(
                "Time points",
                "Select the time points used to calculate the pattern",
                new String[0]);
        public static final ComboParameter<String> groupParameter = new ComboParameter<String>(
                "case/control",
                "Select the case/control parameter",
                new String[0]);

        public PatternRecognitionParameters() {
                super(new UserParameter[]{timePointParameter, groupParameter});
        }

        @Override
        public ExitCode showSetupDialog() {
                Dataset dataset = GuineuCore.getDesktop().getSelectedDataFiles()[0];
                // Update the parameter choices
                List<String> timePointChoices = dataset.getParametersName();
                String[] tpChoices = new String[timePointChoices.size() + 1];
                int cont = 0;
                for (String p : timePointChoices) {
                        tpChoices[cont++] = p;
                }

                getParameter(PatternRecognitionParameters.timePointParameter).setChoices(tpChoices);
                getParameter(PatternRecognitionParameters.groupParameter).setChoices(tpChoices);

                ParameterSetupDialog dialog = new ParameterSetupDialog(this, null);
                dialog.setVisible(true);
                return dialog.getExitCode();
        }
}
