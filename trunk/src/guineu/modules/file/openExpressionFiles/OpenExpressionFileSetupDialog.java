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
package guineu.modules.file.openExpressionFiles;

import guineu.parameters.parametersType.FileNameComponent;
import guineu.util.dialogs.ParameterSetupDialog;

public class OpenExpressionFileSetupDialog extends ParameterSetupDialog {

        private FileNameComponent assay, feature, pheno;

        public OpenExpressionFileSetupDialog(OpenExpressionParameters parameters) {
                super(parameters, null);

                // Get a reference to the combo boxes
                assay = (FileNameComponent) this.getComponentForParameter(OpenExpressionParameters.assayfilename);
                feature = (FileNameComponent) this.getComponentForParameter(OpenExpressionParameters.featurefilename);
                pheno = (FileNameComponent) this.getComponentForParameter(OpenExpressionParameters.phenofilename);


                // Call parametersChanged() to rebuild the reference group combo
                parametersChanged();

        }

        @Override
        public void parametersChanged() {
                String newPath = assay.getValue().getPath();
                feature.setPath(newPath);
                pheno.setPath(newPath);
                this.updateParameterSetFromComponents();

        }
}
