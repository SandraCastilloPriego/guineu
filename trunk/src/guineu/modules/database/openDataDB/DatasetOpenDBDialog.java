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
import guineu.util.dialogs.ExitCode;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * File open dialog
 */
public class DatasetOpenDBDialog extends ParameterSetupDialog implements ActionListener, PropertyChangeListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private JTable table;
	private SimpleParameterSet parameters;

	public DatasetOpenDBDialog(SimpleParameterSet parameters) {
		super("DB Search", parameters);

		this.parameters = parameters;
		logger.finest("Displaying dataset open dialog");
	
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
        this.componentsPanel.add(new JButton("hola"), BorderLayout.EAST);

		this.createDatasetTable();		

		pack();
		setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());
	}

	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	/*public void actionPerformed(ActionEvent event) {
		super.actionPerformed(event);
	


	}*/

	public void propertyChange(PropertyChangeEvent evt) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public int[] getSelectedDataset() {		
        int[] selectedRows = table.getSelectedRows();
        int[] selectedDatasets = new int[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            selectedDatasets[i] = Integer.parseInt((String) table.getValueAt(selectedRows[i], 0));
        }
        return selectedDatasets;
    }

    public String[] getSelectedType() {
        int[] selectedRows = table.getSelectedRows();
        String[] selectedDatasets = new String[selectedRows.length];
        for (int i = 0; i < selectedRows.length; i++) {
            selectedDatasets[i] = (String) table.getValueAt(selectedRows[i], 2);
        }
        return selectedDatasets;
    }
	

	/**
	 * Find panel
	 * JtexField[x]		table column
	 * 		name          --> 1
	 * 		Author        --> 3
	 * 		Date          --> 4
	 * 		ID            --> 0
	 * */
	enum Column {

		ID, Name, nothing, Author, Date
	}

	/**
	 * Creates a table with the list of all dataset in the database.
	 *
	 */
	private void createDatasetTable() {
		//table of datasets
		DataModelDataset DatasetModel = new DataModelDataset();
		table = new JTable(DatasetModel);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		final DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		table.setDefaultRenderer(Object.class, centerRenderer);
		table.setAutoCreateRowSorter(true);
		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setPreferredSize(new Dimension(700, 280));
		componentsPanel.add(scrollPane, BorderLayout.CENTER);

		//size columns
		this.setColumnSize(1, 300, table);
	}

	/**
	 * Sets the size of a column
	 * @param column
	 * @param size
	 */
	private void setColumnSize(int column, int size, JTable table) {
		TableColumn col = table.getColumnModel().getColumn(column);
		col.setPreferredWidth(size);
	}
}
