/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.modules.database.openDatasetDB;

import guineu.main.GuineuCore;
import guineu.util.dialogs.ExitCode;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * File open dialog
 */
public class DatasetOpenDBDialog extends JDialog implements ActionListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    public JButton openDataset,  close;
    private FindPanelDataset findPanel;
    JTable table;
    private ExitCode exitCode = ExitCode.UNKNOWN;

    public DatasetOpenDBDialog() {

        super(GuineuCore.getDesktop().getMainFrame(),
                "Please select a dataset file to open...", true);

        logger.finest("Displaying dataset open dialog");


        this.createDatasetTable();
        findPanel = new FindPanelDataset();
        prepareFindPanel();
        add(findPanel, BorderLayout.NORTH);



        pack();
        setLocationRelativeTo(GuineuCore.getDesktop().getMainFrame());
    }

    public void prepareFindPanel() {
        findPanel.jButtonFind.addActionListener(this);
        findPanel.jButtonReset.addActionListener(this);
        findPanel.jComboBoxType.addItem("LCMS");
        findPanel.jComboBoxType.addItem("GCGC-Tof");
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event) {

        if (event.getSource() == findPanel.jButtonReset) {
            try {
                findPanel.reset();
            } catch (Exception e) {

            }
        }

        if (event.getSource() == findPanel.jButtonFind) {
            try {
                find();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (event.getSource() == openDataset) {
            try {
                exitCode = ExitCode.OK;
                setVisible(false);
            } catch (Exception ee) {
                ee.printStackTrace();
            }
        }

        if (event.getSource() == close) {
            try {
                exitCode = ExitCode.CANCEL;
                dispose();
            } catch (Exception e) {

            }
        }

    }

    public ExitCode getExitCode() {
        return exitCode;
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

    public void find() {
        try {
            table.setModel(new DataModelDataset());
            this.setColumnSize(1, 300, this.table);

            DataModelDataset model = (DataModelDataset) table.getModel();

            this.FilterSearch(findPanel.jTextFieldName, Column.Name.ordinal(), model);
            this.FilterSearch(findPanel.jTextFieldAuthor, Column.Author.ordinal(), model);
            this.FilterSearch(findPanel.jTextFieldDate, Column.Date.ordinal(), model);
            this.FilterSearch(findPanel.jTextFieldID, Column.ID.ordinal(), model);

            // ComboBox..			 
            if ((String) findPanel.jComboBoxType.getSelectedItem() != null && ((String) findPanel.jComboBoxType.getSelectedItem()).compareTo("") != 0) {
                for (int i = 0; i < model.getRowCount(); i++) {
                    final String modelValue = (String) model.getValueAt(i, 2);
                    if (modelValue.compareTo((String) findPanel.jComboBoxType.getSelectedItem()) != 0) {
                        model.removeRow(i);
                        --i;
                    }
                }
            }
            table.repaint();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "To find dataset failed", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    /**
     * Deletes the rows in the dataset table.
     * @param entry TextField
     * @param column place in the table 
     */
    private void FilterSearch(JTextField entry, int column, DataModelDataset model) {


        if (entry.getText() != null && !entry.getText().isEmpty()) {
            for (int i = 0; i < model.getRowCount(); i++) {
                final String modelValue = (String) model.getValueAt(i, column);
                if (modelValue == null) {
                    model.removeRow(i);
                    --i;
                    continue;
                }
                if (!modelValue.matches(".*" + entry.getText() + ".*")) {
                    model.removeRow(i);
                    --i;
                }

            }
        }
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
        scrollPane.setPreferredSize(new Dimension(800, 600));
        add(scrollPane, BorderLayout.CENTER);

        //size columns
        this.setColumnSize(1, 300, table);

        // Button's panel
        final JPanel bpanel = new JPanel();
        openDataset = new JButton("Open");
        openDataset.addActionListener(this);

        close = new JButton("Close");
        close.addActionListener(this);
        bpanel.add(openDataset);
        bpanel.add(close);

        add(bpanel, BorderLayout.PAGE_END);
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
