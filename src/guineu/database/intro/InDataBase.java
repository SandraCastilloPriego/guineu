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
package guineu.database.intro;

import guineu.data.Dataset;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimpleBasicDataset;
import guineu.data.impl.SimpleParameterSet;
import java.io.IOException;
import java.sql.Connection;

/**
 *
 * @author scsandra
 */
public interface InDataBase {

        /**
         * Connects to the database
         *
         * @return Connection
         */
        public Connection connect();

        /**
         * Returns a number from 0 to 1 which represents the progress of the task.
         *
         * @return Progress of the task
         */
        public float getProgress();

        /**
         * Puts a LC-MS data set into the database.
         *
         * @param conn Database connection
         * @param dataset LC-MS Data set
         * @param type Type of the data set (in this case LC-MS)
         * @param author Name of the person who perfomed the process
         * @param datasetName Data set name
         * @param parameters Path of MZmine parameters
         * @param study Study of the dataset
         * @throws IOException
         */
        public void lcms(Connection conn, SimpleLCMSDataset dataset, String type, String author, String datasetName, String parameters, String study) throws IOException;

        /**
         * Puts a GCxGC-MS data set into the database.
         *
         * @param conn Database connection
         * @param dataset GCxGC-MS Data set
         * @param type Type of the data set (in this case GCxGC-MS)
         * @param author Name of the person who perfomed the process
         * @param datasetName Data set name
         * @param study Study of the dataset
         * @throws IOException
         */
        public void gcgctof(Connection conn, SimpleGCGCDataset dataset, String type, String author, String datasetName, String study) throws IOException;

        /**
         * Puts the quality control files into the database.
         *
         * @param conn Database connection
         * @param qualityDataset Data set
         * @throws IOException
         */
        public void qualityControlFiles(Connection conn, SimpleBasicDataset qualityDataset) throws IOException;

        /**
         * Writes the data set into an excel file.
         *
         * @param dataset Data set
         * @param path Path where the new file will be created
         * @param parameters Parameters for saving the file (columns saved in the new file)
         */
        public void WriteExcelFile(Dataset dataset, String path, SimpleParameterSet parameters);

       /**
        * Writes the data set into a CSV file.
        *
        * @param dataset Data set
        * @param path Path where the new file will be created
        * @param parameters Parameters for saving the file (columns saved in the new file)
        */
        public void WriteCommaSeparatedFile(Dataset dataset, String path, SimpleParameterSet parameters);

        /**
         * Deletes the dataset from the database.
         *
         * @param conn Database connection
         * @param datasetID ID of the data set into de database
         */
        public void deleteDataset(Connection conn, int datasetID);
}
