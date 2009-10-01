/*
 * Copyright 2007-2008 VTT Biotechnology
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
package guineu.modules.database.openDataDB;

import guineu.data.Parameter;
import guineu.data.impl.SimpleParameterSet;
import guineu.main.GuineuCore;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;

/**
 *
 * @author scsandra
 */
public class DataOpenDBDialog extends ParameterSetupDialog implements
		ActionListener, PropertyChangeListener {

	SimpleParameterSet parameters;

	public DataOpenDBDialog(SimpleParameterSet parameters) {
		super("'s parameter setup dialog ", parameters);
		this.parameters = parameters;

		for (Parameter p : parameters.getParameters()) {
			JComponent field = getComponentForParameter(p);
			field.addPropertyChangeListener("value", this);
			if (field instanceof JCheckBox) {
				((JCheckBox) field).addActionListener(this);
			}
			if (field instanceof JComboBox) {
				((JComboBox) field).addActionListener(this);
			}
		}
		addComponents();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	private void addComponents() {

		pack();
		setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());
	}
}
