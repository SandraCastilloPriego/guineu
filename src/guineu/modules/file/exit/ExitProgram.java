/*
 * Copyright 2007-2010 VTT Biotechnology
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

package guineu.modules.file.exit;

import guineu.data.ParameterSet;
import guineu.desktop.Desktop;
import guineu.desktop.GuineuMenu;
import guineu.main.GuineuCore;
import guineu.main.GuineuModule;
import guineu.taskcontrol.impl.TaskControllerImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 *
 * @author scsandra
 */
public class ExitProgram implements GuineuModule, ActionListener {
    private Desktop desktop;
    
        
    public void initModule() {

        this.desktop = GuineuCore.getDesktop(); 
        desktop.addMenuSeparator(GuineuMenu.FILE);
        desktop.addMenuItem(GuineuMenu.FILE, "Exit..",
                "Exit MZmine", KeyEvent.VK_E, this, null, null);
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
        
    }

    public void actionPerformed(ActionEvent e) {      
        GuineuCore.exitGuineu();
    }
    
}
