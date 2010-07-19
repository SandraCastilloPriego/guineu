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

import guineu.data.impl.SimpleLCMSDataset;
import java.sql.Connection;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public interface DataBase {

        /**
         * Connects to the database
         * @return Return the connection
         */
        public Connection connect();

        /**
         *
         * @return
         */
        public String[][] getDatasetInfo();

        /**
         *
         * @param sample_names
         * @return
         */
        // public Vector<String> get_dataset_experiment(Vector sample_names);
        /**
         *
         * @param ID
         * @return
         */
        public Vector<String> getSampleNames(int ID);

        public void getLCMSRows(SimpleLCMSDataset dataset);

        public float getProgress();

        public String[] getProjectList();

        public List<String[]> getStudiesInfo();
}
