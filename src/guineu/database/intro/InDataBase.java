/*
Copyright 2007-2008 VTT Biotechnology
This file is part of MULLU.
MULLU is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation.
MULLU is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.
You should have received a copy of the GNU General Public License
along with MULLU; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package guineu.database.intro;

import guineu.data.Dataset;
import guineu.data.impl.Bexperiments;
import guineu.data.impl.SimpleDataset;
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

    public void lcms(Connection conn, SimpleDataset lcms_known, String tipe, String author) throws IOException;

    public void gcgctof(Connection conn, SimpleDataset lcms_known, String tipe, String author) throws IOException;

    public void WriteExcelFile(Dataset lcms_known, String path, SimpleParameterSet parameters);

    public void WriteCommaSeparatedFile(Dataset lcms_known, String path, SimpleParameterSet parameters);

    public void tableEXPERIMENT(Connection connection, Vector<Bexperiments> experiment, String logPath, String CDFPath);

    public void deleteDataset(Connection conn, int datasetID);
}
