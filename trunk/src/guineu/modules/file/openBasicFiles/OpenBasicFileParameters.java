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
package guineu.modules.file.openBasicFiles;

import guineu.modules.file.openLCMSDatasetFile.*;
import guineu.main.GuineuCore;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.util.dialogs.ExitCode;
import java.io.File;
import javax.swing.JFileChooser;

public class OpenBasicFileParameters extends SimpleParameterSet {

        
        public static final FileNamesParameter fileNames = new FileNamesParameter();

        public OpenBasicFileParameters() {
                super(new Parameter[]{fileNames});
        }

        @Override
        public ExitCode showSetupDialog() {

                JFileChooser chooser = new JFileChooser();                

                File lastFiles[] = getParameter(fileNames).getValue();
                if ((lastFiles != null) && (lastFiles.length > 0)) {
                        File currentDir = lastFiles[0].getParentFile();
                        if (currentDir.exists()) {
                                chooser.setCurrentDirectory(currentDir);
                        }
                }

                chooser.setMultiSelectionEnabled(true);

                int returnVal = chooser.showOpenDialog(GuineuCore.getDesktop().getMainFrame());

                if (returnVal != JFileChooser.APPROVE_OPTION) {
                        return ExitCode.CANCEL;
                }

                File selectedFiles[] = chooser.getSelectedFiles();

                getParameter(fileNames).setValue(selectedFiles);

                return ExitCode.OK;

        }
}
