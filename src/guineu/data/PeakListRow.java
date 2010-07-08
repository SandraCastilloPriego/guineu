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

import java.util.Vector;

/**
 * Definition of a Row.
 *
 * @author SCSANDRA
 */
public interface PeakListRow {

        /**
         * Returns the identification number of the row.
         *
         * @return int with the ID of the row
         */
        public int getID();

        /**
         * Sets the identification number of the row.
         *
         * @param i ID number
         */
        public void setID(int i);

        /**
         * Adds a new double value (peak) to the list of "peaks". The peak
         *
         * @param columnName
         * @param value
         */
        public void setPeak(String columnName, Double value);

        public void setPeak(String columnName, String value);

        public Object getPeak(String columnName);

        public Object getPeak(int column, Vector<String> sampleNames);

        public Object[] getPeaks();

        public void removePeaks();

        /**
         * Removes the peaks which are not in the columns given by the parameter.
         *
         * @param columnName Array with the name of the columns that won't be removed
         */
        public void removeNoSamplePeaks(String[] columnName);

        public int getNumberPeaks();

        public PeakListRow clone();

        public boolean isSelected();

        public Object getVar(String varName);

        public void setVar(String varName, Object value);
}
