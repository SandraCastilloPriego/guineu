/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.modules.file.saveDatasetFile;

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
import guineu.util.dialogs.ExitCode;

/**
 * File open dialog
 */
public class DatasetSaveDialog extends JDialog implements ActionListener {

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private JFileChooser fileChooser;
	private File datasetFile;
        private SaveFileParameters currentParameters;
        private ExitCode exit = ExitCode.UNKNOWN;
	public DatasetSaveDialog(SaveFileParameters currentParameters, String lastpath) {

		super(GuineuCore.getDesktop().getMainFrame(),
				"Please select folder to save...", true);

                this.currentParameters = currentParameters;
		logger.finest("Displaying dataset save dialog");

		fileChooser = new JFileChooser();
                fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
                if (lastpath != null) {
			fileChooser.setCurrentDirectory(new File(lastpath));
		}
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.addActionListener(this);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
                ExampleFileFilter csv = new ExampleFileFilter();
                csv.addExtension("csv");
		csv.setDescription("Comma Separated Files");
		fileChooser.addChoosableFileFilter(csv);                
		fileChooser.setFileFilter(csv);

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
                                deskParameters.setLastSavePath(datasetFile.getPath());
                                
                                String path = datasetFile.getPath();
                                if(fileChooser.getFileFilter().getDescription().matches(".*Excel.*")){
                                    if(!path.matches(".*xls")){
                                        path+= ".xls";
                                    }
                                    currentParameters.setParameterValue(SaveFileParameters.type, "Excel");                                    
                                }else{
                                    if(!path.matches(".*csv")){
                                        path+= ".csv";
                                    }
                                    currentParameters.setParameterValue(SaveFileParameters.type, "Comma");  
                                }                               
                                currentParameters.setParameterValue(SaveFileParameters.path, path);
                                
			} catch (Throwable e) {
				JOptionPane.showMessageDialog(this,
						"Could not open dataset file", "Dataset opening error",
						JOptionPane.ERROR_MESSAGE);
				logger.fine("Could not open dataset file." + e.getMessage());
			}
                        exit = ExitCode.OK;
		}
		// discard this dialog
		dispose();
	}

        public ExitCode getExitCode(){
            return exit;
        }
	public String getCurrentDirectory() {
		return this.datasetFile.toString();
	}
}
