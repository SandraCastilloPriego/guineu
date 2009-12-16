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
package guineu.modules.file.introExperimentsDB;

import guineu.data.Dataset;
import guineu.data.impl.ExperimentDataset;
import guineu.database.intro.InDataBase;
import guineu.database.intro.InOracle;
import guineu.taskcontrol.Task;
import guineu.taskcontrol.TaskStatus;
import java.sql.Connection;

/**
 *
 * @author scsandra
 */
public class IntroExperimentDBTask implements Task {

    private TaskStatus status = TaskStatus.WAITING;
    private String errorMessage;
    private IntroExperimentParameters parameters;
    private String logPath,  CDFPath;
    private InDataBase db;
    private Dataset[] datasets;

    public IntroExperimentDBTask(IntroExperimentParameters parameters, Dataset[] datasets) {
        this.datasets = datasets;
        this.parameters = parameters;
        //excelPath = (String) parameters.getParameterValue(IntroExperimentParameters.excelPath);
        logPath = (String) parameters.getParameterValue(IntroExperimentParameters.logPath);
        CDFPath = (String) parameters.getParameterValue(IntroExperimentParameters.CDFPath);

        db = new InOracle();
    }

    public String getTaskDescription() {
        return "Opening Dataset... ";
    }

    public double getFinishedPercentage() {
        return db.getProgress();
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
            saveExperiments();
        } catch (Exception e) {
            status = TaskStatus.ERROR;
            errorMessage = e.toString();
            return;
        }
    }

    public void saveExperiments() {
        try {
            for (Dataset experiment : datasets) {
                status = TaskStatus.PROCESSING;
                Connection connection = db.connect();
                db.tableEXPERIMENT(connection, ((ExperimentDataset)experiment).getExperiments(), logPath, CDFPath);
                status = TaskStatus.FINISHED;
            }
        } catch (Exception e) {
            e.printStackTrace();
            status = TaskStatus.ERROR;
        }
    }
}
