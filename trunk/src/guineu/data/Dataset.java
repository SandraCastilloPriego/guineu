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
package guineu.data;

import java.util.List;
import java.util.Vector;

/**
 * Interface for data set
 *
 * @author scsandra
 */
public interface Dataset {

        /**
         * Constructs an exact copy of it self and returns it.
         *
         * @return Exact copy of itself
         */
        public Dataset clone();

        /**
         * Every dataset has a name to allow the user to identify it
         * Returns the name of the data set.
         *
         * @return String with the name of the data set
         */
        public String getDatasetName();

        /**
         * Returns the name of each sample or column into the data set depending on what
         * kind of data set is it. In the case of LC-MS data they will be only the name
         * of the samples or experiments, but in the case of "Other dataset" it will
         * correspond to all the columns into the dataset.
         *
         * @return Column's name
         */
        public Vector<String> getAllColumnNames();

        /**
         * Returns number of columns. It doesn't take into account the columns corresponding
         * to a parameter of the data such as "m/z" or "retention time".
         *
         * @return Int with the number of columns
         */
        public int getNumberCols();

        /**
         * Returns the number of rows in the data set.
         *
         * @return Int with the number of rows
         */
        public int getNumberRows();

        /**
         * Sets the name of the dataset.
         *
         * @param String with the name of the dataset
         */
        public void setDatasetName(String name);

        /**
         * The type of the data set can be LC-MS, GCxGC-Tof or others.
         * @see guineu.data.DatasetType
         *
         * @return DatasetType class
         */
        public DatasetType getType();

        /**
         * Sets the type of the data set. It can be LC-MS, GCxGC-Tof or others.
         * @see guineu.data.DatasetType
         *
         * @param type DatasetType
         */
        public void setType(DatasetType type);

        /**
         * Returns the row of the data set indicated by the user with its index into the
         * list of rows.
         *
         * @param row Row index
         * @return PeakListRow
         */
        public PeakListRow getRow(int row);

        /**
         * Removes the row of the data set indicated by the user with its index into the
         * list of rows.
         *
         * @param row Row index
         */
        public void removeRow(PeakListRow row);

        /**
         * Adds a new name to the list of columns names.
         *
         * @param columnName String with a new column name
         */
        public void AddColumnName(String columnName);

        /**
         * Adds a new name to the list of columns names in the position indicated.
         *
         * @param columnName String with a new column name
         * @param position int with the position of this colmun in the list
         */
        public void AddColumnName(String columnName, int position);

        /**
         * Returns all the rows in the data set.
         *
         * @return List with all the rows
         */
        public List<PeakListRow> getRows();

        /**
         * Adds a new row into the data set.
         *
         * @param peakListRow Row
         */
        public void AddRow(PeakListRow peakListRow);

        /**
         * Returns general information about the data set.
         * It will be written by the user.
         *
         * @return String with general information about the data set
         */
        public String getInfo();

        /**
         * Adds general information about the data set.
         *
         * @param info String with information about the data set
         */
        public void setInfo(String info);

        /**
         * Adds a new parameter value for one column. The parameters define the
         * metadata related with the columns.
         *
         * @param columnName String with the column name
         * @param parameterName String with the parameter name
         * @param parameterValue String with the value of the parameter
         */
        public void addParameterValue(String columnName, String parameterName, String parameterValue);

        /**
         * Deletes the value of on parameter for all the columns.
         *
         * @param parameterName String with the name of the parameter
         */
        public void deleteParameter(String parameterName);

        /**
         * Returns the name of all the parameters defined.
         *
         * @return Vector of Strings with the name of all the parameters
         */
        public Vector<String> getParametersName();

        /**
         * Returns the value of one concrete parameter in one column.
         *
         * @param columnName String with the column's name
         * @param parameterName String with the name of the parameter
         * @return String with the parameter value in the columnName
         */
        public String getParametersValue(String columnName, String parameterName);

        /**
         * Returns a list of all possible values of one parameter in the columns.
         *
         * @param parameter String with the parameter's name
         * @return Vector of Strings with all possible values of this parameter
         */
        public Vector<String> getParameterAvailableValues(String parameter);
}
