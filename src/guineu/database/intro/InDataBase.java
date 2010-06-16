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
import guineu.data.impl.Bexperiments;
import guineu.data.impl.SimpleLCMSDataset;
import guineu.data.impl.SimpleGCGCDataset;
import guineu.data.impl.SimpleOtherDataset;
import guineu.data.impl.SimpleParameterSet;
import java.io.IOException;
import java.sql.Connection;
import java.util.Vector;

/**
 *
 * @author scsandra
 */
public interface InDataBase {

    public Connection connect();

    public float getProgress();

    public void lcms(Connection conn, SimpleLCMSDataset lcms_known, String tipe, String author, String DatasetName, String parameters, String study) throws IOException;

    public void gcgctof(Connection conn, SimpleGCGCDataset lcms_known, String tipe, String author, String DatasetName, String study) throws IOException;

    public void qualityControlFiles(Connection conn, SimpleOtherDataset qualityDataset) throws IOException;

    public void WriteExcelFile(Dataset lcms_known, String path, SimpleParameterSet parameters);

    public void WriteCommaSeparatedFile(Dataset lcms_known, String path, SimpleParameterSet parameters);

    public void tableEXPERIMENT(Connection connection, Vector<Bexperiments> experiment, String logPath, String CDFPath);

    public void deleteDataset(Connection conn, int datasetID);
}
