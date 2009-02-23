/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.modules.file.openExperimentsData;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


import com.sun.java.ExampleFileFilter;
import guineu.desktop.impl.DesktopParameters;
import guineu.main.GuineuCore;

/**
 * File open dialog
 */
public class DatasetOpenDialog extends JDialog implements ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private JFileChooser fileChooser;
	private File datasetFile;

	public DatasetOpenDialog(File lastpath) {

		super(GuineuCore.getDesktop().getMainFrame(),
				"Please select a experiment file to open...", true);

		logger.finest("Displaying experiment open dialog");

		fileChooser = new JFileChooser();		             
                if (lastpath != null) {
			fileChooser.setCurrentDirectory(lastpath);
		}
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addActionListener(this);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);		
              
                ExampleFileFilter xls = new ExampleFileFilter();
		xls.addExtension("xls");
		xls.setDescription("Excel Files");
		fileChooser.addChoosableFileFilter(xls);                
		fileChooser.setFileFilter(xls);
                
		add(fileChooser, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());
	}

	/**
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent event) {

		String command = event.getActionCommand();

		// check if user clicked "Open"

		if (command.equals("ApproveSelection")) {			
			try {
                                datasetFile = fileChooser.getSelectedFile();
                                DesktopParameters deskParameters = (DesktopParameters) GuineuCore
                                                    .getDesktop().getParameterSet();
                                deskParameters.setLastOpenProjectPath(datasetFile.getPath()); 
			} catch (Throwable e) {
				JOptionPane.showMessageDialog(this,
						"Could not open experiment file", "Dataset opening error",
						JOptionPane.ERROR_MESSAGE);
				logger.fine("Could not open experiment file." + e.getMessage());
			}
		}
		// discard this dialog
		dispose();
	}

	public String getCurrentDirectory() {
		return this.datasetFile.toString();
	}
}
