/*
Copyright 2007-2008 VTT Biotechnology
This file is part of GUINEU.
 */
package guineu.desktop.impl;

import guineu.data.Dataset;

import guineu.data.Tables.experimenttable.ExperimentDataModel;
import guineu.data.impl.DatasetType;
import guineu.data.maintable.DatasetDataModel;
import guineu.data.impl.SimpleDataset;
import guineu.desktop.Desktop;
import guineu.main.GuineuCore;
import guineu.modules.file.openOtherFiles.DatasetDataModel_concatenate;
import guineu.modules.file.introExperimentsDB.IntroExperimentDB;
import guineu.modules.file.saveDatasetDB.SaveFileDB;
import guineu.modules.file.saveDatasetFile.SaveFile;
import guineu.util.GUIUtils;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.components.DragOrderedJList;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Vector;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * This class implements a selector of raw data files and alignment results
 */
public class ItemSelector extends JPanel implements ActionListener,
        MouseListener, ListSelectionListener {

    public static final String DATA_FILES_LABEL = "Dataset Files";
    private DragOrderedJList DatasetFiles;
    private Vector<Dataset> DatasetFilesModel = new Vector<Dataset>();
    private DefaultListModel DatasetNamesModel = new DefaultListModel();
    private JPopupMenu dataFilePopupMenu;
    private int copies = 0;

    /**
     * Constructor
     */
    public ItemSelector(Desktop desktop) {

        // Create panel for raw data objects
        JPanel rawDataPanel = new JPanel();
        JLabel rawDataTitle = new JLabel(DATA_FILES_LABEL);

        DatasetFiles = new DragOrderedJList(DatasetNamesModel);
        DatasetFiles.setCellRenderer(new ItemSelectorListRenderer());
        DatasetFiles.addMouseListener(this);
        DatasetFiles.addListSelectionListener(this);
        JScrollPane rawDataScroll = new JScrollPane(DatasetFiles);

        rawDataPanel.setLayout(new BorderLayout());
        rawDataPanel.add(rawDataTitle, BorderLayout.NORTH);
        rawDataPanel.add(rawDataScroll, BorderLayout.CENTER);
        rawDataPanel.setMinimumSize(new Dimension(150, 10));



        // Add panels to a split and put split on the main panel
        setPreferredSize(new Dimension(200, 10));
        setLayout(new BorderLayout());
        add(rawDataPanel, BorderLayout.CENTER);

        dataFilePopupMenu = new JPopupMenu();
        GUIUtils.addMenuItem(dataFilePopupMenu, "Change Name", this, "CHANGE_NAME");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Show Dataset", this, "SHOW_DATASET");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Save Dataset in a File", this, "SAVE_DATASET");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Save Dataset into the Database", this, "SAVE_DATASET_DB");
        GUIUtils.addMenuItem(dataFilePopupMenu, "Remove", this, "REMOVE_FILE");


    }

    void addSelectionListener(ListSelectionListener listener) {
        DatasetFiles.addListSelectionListener(listener);
    }

    // Implementation of action listener interface
    public void actionPerformed(ActionEvent e) {

        String command = e.getActionCommand();
        
        if (command.equals("CHANGE_NAME")) {
            Dataset[] selectedFiles = getSelectedDatasets();  
            /**TO FINISH **/
        }

        if (command.equals("REMOVE_FILE")) {
            removeData();
        }

        if (command.equals("SHOW_DATASET")) {
            showData();
        }

        if (command.equals("SAVE_DATASET_DB")) {
            Dataset[] selectedFiles = getSelectedDatasets();            
            if (selectedFiles[0] != null && (selectedFiles[0].getType() == DatasetType.LCMS || selectedFiles[0].getType() == DatasetType.GCGCTOF)) {
                SaveFileDB save = new SaveFileDB(selectedFiles);
                save.initModule();
            } else if (selectedFiles[0] != null && selectedFiles[0].getType() == DatasetType.EXPERIMENTINFO) {
                IntroExperimentDB introExperiments = new IntroExperimentDB(selectedFiles);
                introExperiments.initModule();
            }
        }

        if (command.equals("SAVE_DATASET")) {
            Dataset[] selectedFiles = getSelectedDatasets();
            if (selectedFiles[0] != null && (selectedFiles[0].getType() == DatasetType.LCMS || selectedFiles[0].getType() == DatasetType.GCGCTOF)) {
                SaveFile save = new SaveFile(selectedFiles);
                save.initModule();
            }else{
                SaveFile save = new SaveFile(selectedFiles);
                save.initModule();
            }
        }


    }

    private void showData() {
        Dataset[] selectedFiles = getSelectedDatasets();
        Desktop desktop = GuineuCore.getDesktop();
        for (Dataset file : selectedFiles) {
            if (file != null) {
                DataTableModel model = null;               
                if (file.getType() == DatasetType.LCMS || file.getType() == DatasetType.GCGCTOF) {
                    model = new DatasetDataModel(file);
                } else if (file.getType() == DatasetType.EXPERIMENTINFO) {
                    model = new ExperimentDataModel(file);
                }else {
                    model = new DatasetDataModel_concatenate(file);
                }
                DataTable table = new PushableTable(model);
                table.formatNumbers(file.getType());
                DataInternalFrame frame = new DataInternalFrame(file.getDatasetName(), table.getTable(), new Dimension(800, 800));
                desktop.addInternalFrame(frame);
            }
        }


    }

    private void removeData() {
        Dataset[] selectedFiles = getSelectedDatasets();

        for (Dataset file : selectedFiles) {
            if (file != null) {
                DatasetFilesModel.removeElement(file);
                DatasetNamesModel.removeElement(file.getDatasetName());
            }
        }
    }
    
    
    public void removeData(Dataset file) {       
            if (file != null) {
                DatasetFilesModel.removeElement(file);
                DatasetNamesModel.removeElement(file.getDatasetName());
            }
        
    }


    /**
     * Returns selected raw data objects in an array
     */
    public Dataset[] getSelectedDatasets() {

        Object o[] = DatasetFiles.getSelectedValues();

        Dataset res[] = new Dataset[o.length];

        for (int i = 0; i < o.length; i++) {
            for (Dataset dataset : DatasetFilesModel) {
                if (dataset.getDatasetName().compareTo((String) o[i]) == 0) {
                    res[i] = dataset;
                }
            }
        }

        return res;

    }

    /**
     * Sets the active raw data item in the list
     */
    public void setActiveRawData(SimpleDataset rawData) {
        DatasetFiles.setSelectedValue(rawData, true);
    }

    public void mouseClicked(MouseEvent e) {

        if ((e.getClickCount() == 2) && (e.getButton() == MouseEvent.BUTTON1)) {
            showData();
        }

    }

    public void mouseEntered(MouseEvent e) {
    // ignore

    }

    public void mouseExited(MouseEvent e) {
    // ignore
    }

    public void mousePressed(MouseEvent e) {

        if (e.isPopupTrigger()) {
            if (e.getSource() == DatasetFiles) {
                dataFilePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }

    }

    public void mouseReleased(MouseEvent e) {
        if (e.isPopupTrigger()) {
            if (e.getSource() == DatasetFiles) {
                dataFilePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }

    public void valueChanged(ListSelectionEvent event) {

        Object src = event.getSource();

        // Update the highlighting of peak list list in case raw data list
        // selection has changed and vice versa.
        if (src == DatasetFiles) {
            DatasetFiles.repaint();
        }

    }

    public void addNewFile(Dataset dataset) {
        for (int i = 0; i < DatasetNamesModel.getSize(); i++) {
            if (dataset.getDatasetName().matches(DatasetNamesModel.getElementAt(i).toString())) {
                dataset.setDatasetName(dataset.getDatasetName() + "_" + ++copies);
            }
        }
        DatasetFilesModel.addElement(dataset);
        DatasetNamesModel.addElement(dataset.getDatasetName());
        this.DatasetFiles.revalidate();
        this.DatasetFiles.repaint();
    }
}
