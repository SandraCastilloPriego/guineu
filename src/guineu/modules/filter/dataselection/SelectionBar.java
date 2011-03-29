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
package guineu.modules.filter.dataselection;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.LCMSColumnName;
import guineu.data.PeakListRow;
import guineu.desktop.Desktop;
import guineu.util.Tables.DataTableModel;
import guineu.util.internalframe.DataInternalFrame;
import javax.swing.JTable;

/**
 *
 * @author  scsandra
 */
public class SelectionBar extends javax.swing.JPanel {

        /** Creates new form SelectionBar */
        private Desktop desktop;
        private JTable selectedTable;

        public SelectionBar(Desktop desktop) {
                this.desktop = desktop;
                initComponents();

        }

        /** This method is called from within the constructor to
         * initialize the form.
         * WARNING: Do NOT modify this code. The content of this method is
         * always regenerated by the Form Editor.
         */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jButtonNone = new javax.swing.JButton();
        jButtonAll = new javax.swing.JButton();
        jButtonInvert = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox();
        jButtonSelect = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButtonDeleteRows = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection panel"));

        jButtonNone.setText("None");
        jButtonNone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNoneActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonNone);

        jButtonAll.setText("All");
        jButtonAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAllActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonAll);

        jButtonInvert.setText("Invert");
        jButtonInvert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInvertActionPerformed(evt);
            }
        });
        jPanel2.add(jButtonInvert);

        jLabel1.setText("    Select by:  ");
        jPanel3.add(jLabel1);

        jTextField1.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField1.setPreferredSize(new java.awt.Dimension(100, 20));
        jTextField1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTextField1MouseClicked(evt);
            }
        });
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });
        jPanel3.add(jTextField1);

        jComboBox1.setPreferredSize(new java.awt.Dimension(100, 22));
        jPanel3.add(jComboBox1);

        jButtonSelect.setText("Select");
        jButtonSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSelectActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonSelect);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 129, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE))
        );

        jButton1.setText("X");
        jButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        jButtonDeleteRows.setText("Delete rows");
        jButtonDeleteRows.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteRowsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jButtonDeleteRows)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButtonDeleteRows)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel1, 0, 154, Short.MAX_VALUE)
                .addGap(11, 11, 11))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonNoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNoneActionPerformed
            try {
                    Dataset dataset = desktop.getSelectedDataFiles()[0];
                    for (PeakListRow row : dataset.getRows()) {
                            row.setSelectionMode(false);
                    }
            } catch (Exception exception) {
            }
}//GEN-LAST:event_jButtonNoneActionPerformed

    private void jButtonSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSelectActionPerformed
            select();
}//GEN-LAST:event_jButtonSelectActionPerformed

    private void jButtonAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAllActionPerformed
            try {
                    Dataset dataset = desktop.getSelectedDataFiles()[0];
                    for (PeakListRow row : dataset.getRows()) {
                            row.setSelectionMode(true);
                    }
            } catch (Exception exception) {
            }
}//GEN-LAST:event_jButtonAllActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            try {
                    this.desktop.getMainFrame().remove(this);
                    this.desktop.getMainFrame().validate();
            } catch (Exception exception) {
            }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
            select();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jTextField1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTextField1MouseClicked
            try {
                    if (selectedTable == null || selectedTable != ((DataInternalFrame) desktop.getSelectedFrame()).getTable()) {
                            Dataset dataset = desktop.getSelectedDataFiles()[0];
                            this.jComboBox1.removeAllItems();

                            if (dataset.getType() == DatasetType.LCMS) {
                                    for (LCMSColumnName name : LCMSColumnName.values()) {
                                            this.jComboBox1.addItem(name);
                                    }
                            }

                            this.jComboBox1.revalidate();
                    }
            } catch (Exception exception) {
            }
    }//GEN-LAST:event_jTextField1MouseClicked

    private void jButtonDeleteRowsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteRowsActionPerformed
            try {
                    JTable table = ((DataInternalFrame) desktop.getSelectedFrame()).getTable();
                    ((DataTableModel) table.getModel()).removeRows();
            } catch (Exception exception) {
            }
}//GEN-LAST:event_jButtonDeleteRowsActionPerformed

    private void jButtonInvertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInvertActionPerformed
            try {
                    Dataset dataset = desktop.getSelectedDataFiles()[0];
                    for (PeakListRow row : dataset.getRows()) {
                            if (row.isSelected()) {
                                    row.setSelectionMode(false);
                            } else {
                                    row.setSelectionMode(true);
                            }
                    }
            } catch (Exception exception) {
            }
}//GEN-LAST:event_jButtonInvertActionPerformed

        private void select() {
                try {
                        this.selectedTable =((DataInternalFrame) desktop.getSelectedFrame()).getTable();
                        LCMSColumnName columnName = (LCMSColumnName) this.jComboBox1.getItemAt(this.jComboBox1.getSelectedIndex());
                        Dataset dataset = desktop.getSelectedDataFiles()[0];

                        for (PeakListRow row : dataset.getRows()) {
                                if (row.getVar(columnName.getGetFunctionName()).toString().matches(".*" + this.jTextField1.getText() + ".*")) {
                                        row.setSelectionMode(true);
                                }
                        }
                        selectedTable.repaint();
                } catch (Exception exception) {
                }
        }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonAll;
    private javax.swing.JButton jButtonDeleteRows;
    private javax.swing.JButton jButtonInvert;
    private javax.swing.JButton jButtonNone;
    private javax.swing.JButton jButtonSelect;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
