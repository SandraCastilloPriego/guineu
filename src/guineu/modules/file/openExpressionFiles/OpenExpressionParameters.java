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
package guineu.modules.file.openExpressionFiles;

import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.FileNameParameter;
import guineu.parameters.parametersType.StringParameter;
import guineu.util.dialogs.ExitCode;

public class OpenExpressionParameters extends SimpleParameterSet {

        public static final StringParameter datasetName = new StringParameter(
                "Dataset name",
                "Write the name of the dataset");
        public static final FileNameParameter assayfilename = new FileNameParameter(
                "Assaydata File",
                "Path of the Assaydata file.");
        public static final FileNameParameter featurefilename = new FileNameParameter(
                "Feature File",
                "Path of the Feature file.");
        public static final FileNameParameter phenofilename = new FileNameParameter(
                "Pheno File",
                "Path of the Pheno file.");

        public OpenExpressionParameters() {
                super(new Parameter[]{datasetName, assayfilename, featurefilename, phenofilename});
        }

        @Override
        public ExitCode showSetupDialog() {
                OpenExpressionFileSetupDialog dialog = new OpenExpressionFileSetupDialog(this);
                dialog.setVisible(true);
                return dialog.getExitCode();
        }
}
