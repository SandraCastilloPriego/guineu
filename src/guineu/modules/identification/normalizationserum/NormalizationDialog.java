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
package guineu.modules.identification.normalizationserum;

import guineu.main.GuineuCore;
import guineu.util.components.HelpButton;
import guineu.util.dialogs.ExitCode;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 *
 * @author  scsandra
 */
public class NormalizationDialog extends javax.swing.JDialog implements ActionListener {

    private Vector<StandardUmol> standards;
    private ExitCode exit = ExitCode.UNKNOWN;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private JButton btnHelp;

    /** Creates new form NormalizationDialog */
    public NormalizationDialog(Vector<StandardUmol> standards, String helpID) {
        super(GuineuCore.getDesktop().getMainFrame(),
                "Please fill the standards...", true);

        this.standards = standards;
        initComponents();

        StandardsDataModel model = new StandardsDataModel(this.standards);
        UnknownsDataModel unknownModel = new UnknownsDataModel(this.standards);

        this.jTable1.setModel(model);
        this.jTable2.setModel(unknownModel);
        this.jButtonClose.addActionListener(this);
        this.jButtonOk.addActionListener(this);
        this.jButtonReset.addActionListener(this);
        this.setSize(305, 410);
        // Help button
        btnHelp = new HelpButton(helpID);
        this.jPanel4.add(btnHelp);
        logger.finest("Displaying Normalization Serum dialog");
    }

    public void fillStandards() {
        try {
            ((StandardsDataModel) this.jTable1.getModel()).fillStandards();
            ((UnknownsDataModel) this.jTable2.getModel()).fillStandards();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error, You have not introduced a correct value.", "Error", JOptionPane.ERROR_MESSAGE);

        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jButtonOk = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();
        jButtonReset = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(320, 300));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        jPanel5.setPreferredSize(new java.awt.Dimension(100, 230));
        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.PAGE_AXIS));

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("umol / l.blood sample  --- umol / g sample"));
        jPanel1.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(jTable1);

        jPanel1.add(jScrollPane1);

        jPanel5.add(jPanel1);

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Unknow compounds"));
        jPanel6.setPreferredSize(new java.awt.Dimension(500, 250));
        jPanel6.setRequestFocusEnabled(false);
        jPanel6.setLayout(new javax.swing.BoxLayout(jPanel6, javax.swing.BoxLayout.LINE_AXIS));

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable2.setCellSelectionEnabled(true);
        jScrollPane2.setViewportView(jTable2);

        jPanel6.add(jScrollPane2);

        jPanel5.add(jPanel6);

        getContentPane().add(jPanel5);

        jPanel4.setMaximumSize(new java.awt.Dimension(300, 30));
        jPanel4.setMinimumSize(new java.awt.Dimension(168, 30));
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 50));

        jButtonOk.setText("  Ok ");
        jPanel4.add(jButtonOk);

        jButtonClose.setText("Close");
        jPanel4.add(jButtonClose);

        jButtonReset.setText("Reset");
        jButtonReset.setMaximumSize(new java.awt.Dimension(90, 27));
        jButtonReset.setMinimumSize(new java.awt.Dimension(80, 27));
        jButtonReset.setPreferredSize(new java.awt.Dimension(80, 27));
        jPanel4.add(jButtonReset);

        getContentPane().add(jPanel4);
    }// </editor-fold>//GEN-END:initComponents

    public ExitCode getExitCode() {
        return exit;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonClose;
    public javax.swing.JButton jButtonOk;
    private javax.swing.JButton jButtonReset;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.jButtonOk) {
            exit = ExitCode.OK;
            fillStandards();
            dispose();
        } else if (e.getSource() == this.jButtonClose) {
            exit = ExitCode.CANCEL;
            dispose();
        } else if (e.getSource() == this.jButtonReset) {
            this.reset();
        }


    }

    public void reset() {
        try {
            ((StandardsDataModel) this.jTable1.getModel()).resetStandards();
            ((UnknownsDataModel) this.jTable2.getModel()).resetStandards();
        } catch (Exception e) {
        }
    }
}
