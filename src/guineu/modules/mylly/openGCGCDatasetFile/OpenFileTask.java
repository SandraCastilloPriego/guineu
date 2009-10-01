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
package guineu.modules.mylly.openGCGCDatasetFile;

import guineu.data.datamodels.DatasetGCGCDataModel;
import guineu.data.parser.impl.GCGCParserXLS;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.parser.Parser;
import guineu.data.parser.impl.GCGCParserCSV;
import guineu.desktop.Desktop;
import guineu.taskcontrol.Task;
import guineu.util.Tables.DataTable;
import guineu.util.Tables.DataTableModel;
import guineu.util.Tables.impl.PushableTable;
import guineu.util.internalframe.DataInternalFrame;
import java.awt.Dimension;
import java.io.IOException;

/**
 *
 * @author scsandra
 */
public class OpenFileTask implements Task {

    private String fileDir;
    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private Desktop desktop;
    private Parser parser;

    public OpenFileTask(String fileDir, Desktop desktop) {
        if (fileDir != null) {
            this.fileDir = fileDir;
        }
        this.desktop = desktop;
    }

    public String getTaskDescription() {
        return "Opening File... ";
    }

    public double getFinishedPercentage() {
        if (parser != null) {
			return parser.getProgress();
		} else {
			return 0.0f;
		}
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void cancel() {
        status = TaskStatus.CANCELED;
    }

    public void run() {
        try {
            this.openFile();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public void openFile() {
        status = TaskStatus.PROCESSING;
        try {
            if (fileDir.matches(".*xls")) {
                try {
                    Parser parserName = new GCGCParserXLS(fileDir, null);
                    String[] sheetsNames = ((GCGCParserXLS) parserName).getSheetNames(fileDir);
                    for (String Name : sheetsNames) {
                        try {
                            if (status != TaskStatus.CANCELED) {
                                parser = new GCGCParserXLS(fileDir, Name);
                                parser.fillData();
                                this.open(parser);
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else if (fileDir.matches(".*csv")) {
                try {
                    if (status != TaskStatus.CANCELED) {
                        parser = new GCGCParserCSV(fileDir);
                        parser.fillData();
                        this.open(parser);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

        /*parser.fillData();
        SimpleGCGCDataset dataset =  (SimpleGCGCDataset) parser.getDataset();
        desktop.AddNewFile(dataset);

        //creates internal frame with the table
        DataTableModel model = new DatasetGCGCDataModel(dataset);
        DataTable table = new PushableTable(model);
        table.formatNumbers(dataset.getType());
        DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

        desktop.addInternalFrame(frame);
        frame.setVisible(true);*/

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        status = TaskStatus.FINISHED;
    }

    public void open(Parser parser) {
        try {
            if (status != TaskStatus.CANCELED) {
                SimpleGCGCDataset dataset = (SimpleGCGCDataset) parser.getDataset();
                desktop.AddNewFile(dataset);

                //creates internal frame with the table
                DataTableModel model = new DatasetGCGCDataModel(dataset);
                DataTable table = new PushableTable(model);
                table.formatNumbers(dataset.getType());
                DataInternalFrame frame = new DataInternalFrame(dataset.getDatasetName(), table.getTable(), new Dimension(800, 800));

                desktop.addInternalFrame(frame);
                frame.setVisible(true);
            }
        } catch (Exception exception) {
            // exception.printStackTrace();
        }
    }
}
