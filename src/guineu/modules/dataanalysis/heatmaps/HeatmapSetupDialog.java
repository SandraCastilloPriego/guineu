/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.dataanalysis.heatmaps;

import guineu.data.Dataset;
import guineu.main.GuineuCore;
import guineu.util.dialogs.ParameterSetupDialog;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class HeatmapSetupDialog extends ParameterSetupDialog {

        private JComboBox selDataCombo, refGroupCombo;
        private String previousParameterSelection;
        private Dataset dataset;

        public HeatmapSetupDialog(HeatMapParameters parameters, Dataset dataset) {
                super(parameters, null);

                this.dataset = dataset;

                // Get a reference to the combo boxes
                selDataCombo = (JComboBox) this.getComponentForParameter(HeatMapParameters.selectionData);
                refGroupCombo = (JComboBox) this.getComponentForParameter(HeatMapParameters.referenceGroup);

                // Save a reference to current "Sample parameter" value
                previousParameterSelection = (String) selDataCombo.getSelectedItem();

                // Call parametersChanged() to rebuild the reference group combo
                parametersChanged();

        }

        @Override
        public void parametersChanged() {
        
                // Get the current value of the "Sample parameter" combo
                String currentParameterSelection = (String) selDataCombo.getSelectedItem();
                if (currentParameterSelection == null) {
                        return;
                }

                // If the value has changed, update the "Reference group" combo
                if (!currentParameterSelection.equals(previousParameterSelection)) {
                        List<String> values = new ArrayList<String>();                       
                        // Obtain all possible values

                        values = dataset.getParameterAvailableValues(currentParameterSelection);

                        // Update the parameter and combo model
                        String newValues[] = values.toArray(new String[0]);
                        super.parameterSet.getParameter(HeatMapParameters.referenceGroup).setChoices(newValues);
                        refGroupCombo.setModel(new DefaultComboBoxModel(newValues));

                        previousParameterSelection = currentParameterSelection;
                }

                this.updateParameterSetFromComponents();

        }
}
