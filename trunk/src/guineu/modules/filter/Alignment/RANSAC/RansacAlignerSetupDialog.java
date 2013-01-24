/*
 * Copyright 2007-2013 VTT Biotechnology
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
package guineu.modules.filter.Alignment.RANSAC;

import guineu.data.Dataset;
import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.main.GuineuCore;
import guineu.util.Range;
import guineu.util.dialogs.ParameterSetupDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

/**
 * This class extends ParameterSetupDialog class, including a spectraPlot. This
 * is used to preview how the selected mass detector and his parameters works
 * over the raw data file.
 */
public class RansacAlignerSetupDialog extends ParameterSetupDialog implements
        ActionListener {

        // Dialog components
        private JPanel pnlPlotXY, peakListsPanel;
        private JCheckBox preview;
        private AlignmentRansacPlot chart;
        private JComboBox peakListsComboX, peakListsComboY;
        private JButton alignmentPreviewButton;
        private RansacAlignerParameters parameters;

        /**
         * @param parameters
         * @param massDetectorTypeNumber
         */
        public RansacAlignerSetupDialog(RansacAlignerParameters parameters, String helpID) {
                super(parameters, null);
                this.parameters = parameters;
                addComponents();
        }

        public void actionPerformed(ActionEvent event) {

                super.actionPerformed(event);
                Object src = event.getSource();

                if (src == preview) {
                        if (preview.isSelected()) {
                                // Set the height of the preview to 200 cells, so it will span
                                // the whole vertical length of the dialog (buttons are at row
                                // no
                                // 100). Also, we set the weight to 10, so the preview component
                                // will consume most of the extra available space.
                                mainPanel.add(pnlPlotXY, 3, 0, 1, 200, 10, 10);
                                peakListsPanel.setVisible(true);
                                updateMinimumSize();
                                pack();
                                setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());
                        } else {
                                mainPanel.remove(pnlPlotXY);
                                peakListsPanel.setVisible(false);
                                updateMinimumSize();
                                pack();
                                setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());
                        }
                }

                if (src == alignmentPreviewButton) {
                        Dataset peakListX = (Dataset) peakListsComboX.getSelectedItem();
                        Dataset peakListY = (Dataset) peakListsComboY.getSelectedItem();


                        // Ransac Alignment
                        List<AlignStructMol> list = this.getVectorAlignment(peakListX, peakListY);
                        super.updateParameterSetFromComponents();
                        RANSAC ransac = new RANSAC(parameters);
                        ransac.alignment(list);

                        // Plot the result
                        this.chart.removeSeries();
                        this.chart.addSeries(list, peakListX.getDatasetName() + " vs " + peakListY.getDatasetName(), this.parameters.getParameter(RansacAlignerParameters.Linear).getValue());
                        this.chart.printAlignmentChart(peakListX.getDatasetName() + " RT", peakListY.getDatasetName() + " RT");
                }

        }

        /**
         * This function add all the additional components for this dialog over the
         * original ParameterSetupDialog.
         *
         */
        private void addComponents() {

                // Elements of pnlpreview
                JPanel pnlpreview = new JPanel(new BorderLayout());
                preview = new JCheckBox(" Show preview of RANSAC alignment ");
                preview.addActionListener(this);
                preview.setHorizontalAlignment(SwingConstants.CENTER);
                pnlpreview.add(new JSeparator(), BorderLayout.NORTH);
                pnlpreview.add(preview, BorderLayout.CENTER);
                pnlpreview.add(Box.createVerticalStrut(10), BorderLayout.SOUTH);

                pnlpreview.add(new JSeparator(), BorderLayout.NORTH);
                pnlpreview.add(preview, BorderLayout.CENTER);

                // Panel for the combo boxes with the peak lists
                peakListsPanel = new JPanel();
                peakListsPanel.setLayout(new BoxLayout(peakListsPanel,
                        BoxLayout.PAGE_AXIS));


                JPanel comboPanel = new JPanel();
                Dataset[] peakLists = GuineuCore.getDesktop().getSelectedDataFiles();
                peakListsComboX = new JComboBox();
                peakListsComboY = new JComboBox();
                for (Dataset peakList : peakLists) {
                        peakListsComboX.addItem(peakList);
                        peakListsComboY.addItem(peakList);
                }
                comboPanel.add(peakListsComboX);
                comboPanel.add(peakListsComboY);

                // Preview button
                alignmentPreviewButton = new JButton("Preview Alignmnet");
                alignmentPreviewButton.addActionListener(this);
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(alignmentPreviewButton, BorderLayout.CENTER);

                peakListsPanel.add(comboPanel);
                peakListsPanel.add(buttonPanel);
                peakListsPanel.setVisible(false);

                JPanel pnlVisible = new JPanel(new BorderLayout());
                pnlVisible.add(pnlpreview, BorderLayout.NORTH);
                pnlVisible.add(peakListsPanel, BorderLayout.CENTER);

                // Panel for XYPlot
                pnlPlotXY = new JPanel(new BorderLayout());
                Border one = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
                Border two = BorderFactory.createEmptyBorder(10, 10, 10, 10);
                pnlPlotXY.setBorder(BorderFactory.createCompoundBorder(one, two));
                pnlPlotXY.setBackground(Color.white);

                chart = new AlignmentRansacPlot();
                pnlPlotXY.add(chart, BorderLayout.CENTER);

                mainPanel.add(pnlVisible, 0, getNumberOfParameters() + 3, 3, 1, 0, 0);

                updateMinimumSize();
                pack();
                setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());

        }

        /**
         * Create the vector which contains all the possible aligned peaks.
         * @return vector which contains all the possible aligned peaks.
         */
        private List<AlignStructMol> getVectorAlignment(Dataset peakListX, Dataset peakListY) {

                List<AlignStructMol> alignMol = new ArrayList<AlignStructMol>();

                for (PeakListRow row : peakListX.getRows()) {

                        // Calculate limits for a row with which the row can be aligned
                        double mzTolerance = parameters.getParameter(RansacAlignerParameters.MZTolerance).getValue().getTolerance();
                        double rtToleranceValueAbs = parameters.getParameter(RansacAlignerParameters.RTTolerance).getValue().getTolerance();
                        double mzMin = ((Double) row.getVar("getMZ")) - mzTolerance;
                        double mzMax = ((Double) row.getVar("getMZ")) + mzTolerance;
                        double rtMin, rtMax;
                        double rtToleranceValue = rtToleranceValueAbs;
                        rtMin = ((Double) row.getVar("getRT")) - rtToleranceValue;
                        rtMax = ((Double) row.getVar("getRT")) + rtToleranceValue;

                        // Get all rows of the aligned peaklist within parameter limits
                        List<PeakListRow> candidateRows = this.getRowsInsideScanAndMZRange(peakListY,
                                new Range(rtMin, rtMax), new Range(mzMin, mzMax));

                        for (PeakListRow candidateRow : candidateRows) {
                                alignMol.add(new AlignStructMol((SimplePeakListRowLCMS) row, (SimplePeakListRowLCMS) candidateRow));
                        }
                }
                return alignMol;
        }

        private List<PeakListRow> getRowsInsideScanAndMZRange(Dataset peakListY, Range rangeRT, Range rangeMZ) {
                List<PeakListRow> rangeRows = new ArrayList<PeakListRow>();
                for (PeakListRow row : peakListY.getRows()) {
                        if (rangeMZ.contains((Double) row.getVar("getMZ")) && rangeRT.contains((Double) row.getVar("getRT"))) {
                                rangeRows.add(row);
                        }
                }
                return rangeRows;
        }
}
