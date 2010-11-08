/*
 * Copyright 2007-2010 VTT Biotechnology
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
package guineu.database.retrieve;

import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

/**
 * Functions for retrievement of data from the database.
 *
 * @author scsandra
 */
public interface DataBase {

        /**
         * Retruns a connection to the database. It is a VTT internal database.
         *
         * @return Return the connection
         */
        public Connection connect();

        /**
         * Returns all avaiable information of every data set in the database.
         *
         * @return Array with every data set information
         */
        public String[][] getDatasetInfo();

        /**
         * Returns a list of sample names of a concrete dataset.
         *
         * @param ID Data set ID
         * @return List of sample names
         */
        public Vector<String> getSampleNames(int ID);

        /**
         * Loads every row information of one LC-MS data set from the database.
         *
         * @param dataset Data set where the rows will be saved
         */
        public void getLCMSRows(SimpleLCMSDataset dataset);

        /**
         * Loads every row information of one GCxGC-MS data set from the database.
         *
         * @param dataset
         */
        public void getGCGCRows(SimpleGCGCDataset dataset);

        /**
         * Returns the progress of the current tasks.
         *
         * @return Progress
         */
        public float getProgress();

        /**
         * Returns a list of project names.
         *
         * @return List of project names
         */
        public String[] getProjectList();

        /**
         * Returns a list with all studies information
         *
         * @return List with studies information
         */
        public List<String[]> getStudiesInfo();

        /**
         * Takes the sample description from the database:
         * "LABEL"
         * "TYPE"
         * "SUBTYPE"
         * "ORGANISM"
         *
         * @param sampleName Name of the sample
         * @return description of the sample
         */
        public String[] getParameters(String sampleName);

        /**
         * Deletes the dataset from database matching the dataset's name.
         * @param datasetName name of the dataset to be removed
         */
        public void deleteDataset(String datasetName, String password);
}
