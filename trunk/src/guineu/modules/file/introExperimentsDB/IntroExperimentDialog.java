/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.file.introExperimentsDB;

import guineu.main.GuineuCore;
import guineu.util.dialogs.ExitCode;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;

/**
 *
 * @author  scsandra
 */
public class IntroExperimentDialog extends JDialog implements ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private IntroExperimentParameters parameters;
    private JFileChooser ExcelfileChooser,  CDFfileChooser;
    private String lastPath;
    private JDialog excelDialog,  CDFDialog;
    private ExitCode exit = ExitCode.UNKNOWN;

    public IntroExperimentDialog(IntroExperimentParameters parameters, String lastPath) {
        super(GuineuCore.getDesktop().getMainFrame(),
                "Please fill the data...", true);

        this.parameters = parameters;
        this.lastPath = lastPath;
        logger.finest("Displaying intro experiments dialog");
        initComponents();
        pack();
        setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());

    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTextFieldLog = new javax.swing.JTextField();
        jTextFieldCDF = new javax.swing.JTextField();
        CDFDirFC = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jButtonDatabase = new javax.swing.JButton();

        jPanel1.setPreferredSize(new java.awt.Dimension(700, 200));
        jPanel1.setRequestFocusEnabled(false);

        jPanel2.setLayout(new java.awt.GridLayout(2, 1, 10, 15));

        jLabel3.setText("Log Directori:");
        jPanel2.add(jLabel3);

        jLabel4.setText("CDF Directori:");
        jPanel2.add(jLabel4);

        jPanel3.setLayout(new java.awt.GridLayout(2, 0, 0, 10));
        jPanel3.add(jTextFieldLog);
        jPanel3.add(jTextFieldCDF);

        CDFDirFC.setText("File chooser");
        CDFDirFC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CDFDirFCActionPerformed(evt);
            }
        });

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        jButtonDatabase.setText("Write Database");
        jButtonDatabase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDatabaseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CDFDirFC)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(250, Short.MAX_VALUE)
                .addComponent(jButtonDatabase, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonClose, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                    .addComponent(CDFDirFC))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonDatabase)
                    .addComponent(jButtonClose))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void CDFDirFCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CDFDirFCActionPerformed
        try {
            CDFDialog = new JDialog();
            CDFfileChooser = new JFileChooser();
            CDFfileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
            if (lastPath != null) {
                CDFfileChooser.setCurrentDirectory(new File(lastPath));
            }
            CDFfileChooser.setMultiSelectionEnabled(false);
            CDFfileChooser.addActionListener(this);
            CDFfileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            CDFDialog.add(CDFfileChooser);
            CDFfileChooser.setVisible(true);
            setVisible(false);
            CDFDialog.setVisible(true);
            CDFDialog.setSize(new Dimension(300, 500));
            CDFDialog.pack();
            CDFDialog.setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());

        } catch (Exception exception) {

        }
}//GEN-LAST:event_CDFDirFCActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        exit = ExitCode.CANCEL;
        dispose();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void jButtonDatabaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDatabaseActionPerformed
        parameters.setParameterValue(IntroExperimentParameters.logPath, jTextFieldLog.getText().toString());
        parameters.setParameterValue(IntroExperimentParameters.CDFPath, jTextFieldCDF.getText().toString());
        exit = ExitCode.OK;
        dispose();
    }//GEN-LAST:event_jButtonDatabaseActionPerformed

    public ExitCode getExitCode() {
        return exit;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final String command = e.getActionCommand();
        if (command.equals(JFileChooser.APPROVE_SELECTION)) {
            if (e.getSource() == ExcelfileChooser) {
                String path = ExcelfileChooser.getSelectedFile().getPath();
                try {
                    excelDialog.setVisible(false);
                    setVisible(true);

                } catch (final Exception ee) {
                    System.out.println(ee.getCause());
                }
            } else if (e.getSource() == CDFfileChooser) {
                String path = CDFfileChooser.getSelectedFile().getPath();
                try {
                    jTextFieldCDF.setText(path);
                    CDFDialog.setVisible(false);
                    setVisible(true);
                } catch (final Exception ee) {
                    System.out.println(ee.getCause());
                }
            }
        } else if (command.equals(JFileChooser.CANCEL_SELECTION)) {
            if (e.getSource() == ExcelfileChooser) {

                try {
                    excelDialog.setVisible(false);
                    setVisible(true);

                } catch (final Exception ee) {
                    System.out.println(ee.getCause());
                }
            } else if (e.getSource() == CDFfileChooser) {

                try {

                    CDFDialog.setVisible(false);
                    setVisible(true);
                } catch (final Exception ee) {
                    System.out.println(ee.getCause());
                }
            }
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton CDFDirFC;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDatabase;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextFieldCDF;
    private javax.swing.JTextField jTextFieldLog;
    // End of variables declaration//GEN-END:variables
}
