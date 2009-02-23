/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
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
                "TODO write description", KeyEvent.VK_E, this, null); 
    }

    public ParameterSet getParameterSet() {
        return null;
    }

    public void setParameters(ParameterSet parameterValues) {
        
    }

    public void actionPerformed(ActionEvent e) {
        while (((TaskControllerImpl) GuineuCore.getTaskController())
					.getTaskExists()) {
				// wait unitl all task to finish
			}
        GuineuCore.exitGuineu();
    }
    
}
