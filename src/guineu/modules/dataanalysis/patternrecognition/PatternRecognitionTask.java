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
package guineu.modules.dataanalysis.patternrecognition;

import guineu.data.Dataset;
import guineu.parameters.ParameterSet;
import guineu.taskcontrol.AbstractTask;
import guineu.taskcontrol.TaskStatus;

/**
 *
 * @author scsandra
 */
public class PatternRecognitionTask extends AbstractTask {

        

        private Dataset dataset;
        private String  timePoints, groupParameter;
        private double finishedPercentage = 0.0f;

        public PatternRecognitionTask(Dataset dataset, ParameterSet parameters) {
                this.dataset = dataset;
                timePoints = parameters.getParameter(PatternRecognitionParameters.timePointParameter).getValue();               
                groupParameter = parameters.getParameter(PatternRecognitionParameters.groupParameter).getValue();
              
        }

        public String getTaskDescription() {
                return "Patter recognition... ";
        }

        public double getFinishedPercentage() {
                return finishedPercentage;
        }

        public void run() {
                try {
                        setStatus(TaskStatus.PROCESSING);

                       
                        setStatus(TaskStatus.FINISHED);

                } catch (Exception e) {
                        setStatus(TaskStatus.ERROR);
                        errorMessage = e.toString();
                        return;
                }
        }

     
}
