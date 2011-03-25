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
package guineu.modules.R;

import guineu.data.Dataset;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

/**
 *
 * @author scsandra
 */
public class R implements ActionListener, GuineuModule, TaskListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Desktop desktop;

    public R() {

        this.desktop = GuineuCore.getDesktop();
        desktop.addMenuItem(GuineuMenu.STATISTICS, "R",
                "TODO write description",
                KeyEvent.VK_R, this, null, null);
    }

    public void taskStarted(Task task) {
        logger.info("R");
    }

    public void taskFinished(Task task) {
    }

    public ParameterSet getParameterSet() {
        return null;
    }
   

    @Override
    public String toString() {
        return "R";
    }

    public void actionPerformed(ActionEvent arg0) {        
        Dataset dataset = GuineuCore.getDesktop().getSelectedDataFiles()[0];
        if (dataset != null) {
            RConsole console = new RConsole(GuineuCore.getDesktop().getMainFrame(), false, dataset);
            console.setVisible(true);
        }
    }
}
