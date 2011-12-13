/*
 * Copyright 2007-2011 VTT Biotechnology
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
package guineu.modules.mylly.openFiles;

import com.csvreader.CsvReader;
import guineu.data.GCGCColumnName;
import guineu.modules.mylly.datastruct.ComparablePair;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.modules.mylly.datastruct.GCGCDatumWithConcentration;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author scsandra
 */
public class GCGCFileReader {

        private Character _separator;
        private boolean _filterClassified;
        private boolean _findSpectrum;

        public GCGCFileReader(String separator, boolean filterClassified) {
                if (separator.equals("\\t")) {
                        _separator = '\t';
                } else {
                        _separator = separator.toCharArray()[0];
                }
                _filterClassified = filterClassified;
                _findSpectrum = true;
        }

        public List<GCGCDatum> readGCGCDataFile(File file) throws IOException {

                List<GCGCDatum> peaks;

                FileReader fr = null;

                try {
                        fr = new FileReader(file);
                } catch (FileNotFoundException e) {
                        throw e;
                } catch (NullPointerException e2) {
                        throw e2;
                }

                CsvReader reader = new CsvReader(fr, _separator);
                reader.readRecord();
                String[] header = reader.getValues();

                // Then go through the file row at a time
                {
                        String splitRow[];
                        peaks = new ArrayList<GCGCDatum>();
                        int cont = 1;
                        while (reader.readRecord()) {
                                splitRow = reader.getValues();
                                boolean foundRetentionIndex = false;
                                boolean foundConc = false;
                                boolean filter = false;

                                double rt1;
                                double rt2;
                                double area;
                                double retentionIndex;
                                double quantMass;
                                double pValue = 0.0;
                                double qValue = 0.0;
                                double conc = -1;
                                boolean useConc = false;
                                List<ComparablePair<Integer, Integer>> spectrum = null;

                                int similarity = 800;
                                String CAS, name;

                                rt1 = rt2 = area = retentionIndex = similarity = -1;
                                quantMass = GCGCDatum.NO_QUANT_MASS;
                                name = "Not Found";
                                CAS = "";

                                for (int i = 0; i < splitRow.length; i++) {
                                        String curStr = splitRow[i];

                                        if (header[i].matches(GCGCColumnName.RT1.getRegularExpression())) {
                                                String rts[] = curStr.split(",");
                                                try {
                                                        rt1 = Double.parseDouble(rts[0]);
                                                        rt2 = Double.parseDouble(rts[1]);

                                                } catch (NumberFormatException e) {
                                                        // e.printStackTrace();
                                                        rt1 = 0;
                                                        rt2 = 0;
                                                }
                                        } else if (header[i].matches(GCGCColumnName.RTI.getRegularExpression())) {
                                                try {
                                                        retentionIndex = "".equals(curStr) ? 0 : Double.parseDouble(curStr);
                                                        foundRetentionIndex = true;
                                                } catch (NumberFormatException e) {
                                                        retentionIndex = 0;
                                                }
                                        } else if (header[i].matches(".*Area.*")) {
                                                try {
                                                        area = Double.parseDouble(curStr);

                                                } catch (NumberFormatException e) {
                                                        area = 0;
                                                }
                                        } else if (header[i].matches(".*Concentration.*")) {
                                                if (!(curStr.isEmpty() || curStr == null)) {
                                                        try {
                                                                conc = Double.parseDouble(curStr);
                                                                foundConc = true;                                                              
                                                        } catch (NumberFormatException e) {
                                                                e.printStackTrace();
                                                                conc = 0;
                                                        }

                                                }
                                        } else if (header[i].matches(GCGCColumnName.CAS.getRegularExpression())) {
                                                CAS = curStr;
                                        } else if (header[i].matches(GCGCColumnName.NAME.getRegularExpression())) {
                                                name = curStr;
                                        } else if (header[i].matches(".*Similarity.*")) {
                                                try {
                                                        similarity = "".equals(curStr) ? 0 : Integer.parseInt(curStr);
                                                } catch (NumberFormatException e) {
                                                        similarity = 0;
                                                }
                                        } else if (_filterClassified && header[i].contains("Classification")) {

                                                if (curStr != null && !"".equals(curStr)) {
                                                        //We have some classification for the peak so drop it
                                                        filter = true;
                                                        break;
                                                }
                                        } else if (header[i].matches(GCGCColumnName.SPECTRUM.getRegularExpression()) && _findSpectrum) {
                                                try {
                                                        spectrum = parseSpectrum(curStr);

                                                } catch (NumberFormatException e) {
                                                }
                                        } else if (header[i].matches(GCGCColumnName.MASS.getRegularExpression())) {
                                                try {
                                                        quantMass = Double.parseDouble(curStr);
                                                } catch (NumberFormatException e) {
                                                        quantMass = -1;
                                                }
                                        }else if (header[i].matches(GCGCColumnName.P.getRegularExpression())) {
                                                try {
                                                        pValue = Double.parseDouble(curStr);
                                                } catch (NumberFormatException e) {
                                                        pValue = 0.0;
                                                }
                                        }else if (header[i].matches(GCGCColumnName.Q.getRegularExpression())) {
                                                try {
                                                        qValue = Double.parseDouble(curStr);
                                                } catch (NumberFormatException e) {
                                                        qValue = 0.0;
                                                }
                                        }else if (header[i].matches("Type")) {
                                                if (curStr.contains("Not Found")) {
                                                        filter = true;
                                                }
                                        }

                                }

                                if (!filter) {
                                        if (similarity < 0) {
                                                similarity = 800;
                                        }

                                        GCGCDatum currentRow;
                                        if (foundConc) {
                                                currentRow = new GCGCDatumWithConcentration(cont++, rt1, rt2, retentionIndex,
                                                        quantMass, pValue, qValue, similarity, area, CAS, name, useConc, file.getName(),
                                                        spectrum, conc);
                                        } else if (foundRetentionIndex) {
                                                currentRow = new GCGCDatum(cont++, rt1, rt2, retentionIndex,
                                                        quantMass, pValue, qValue, similarity, area, conc, useConc,
                                                        CAS, name, file.getName(), spectrum);
                                        } else {
                                                currentRow = new GCGCDatum(cont++, rt1, rt2, quantMass,
                                                        area, conc, pValue, qValue, useConc, similarity, CAS, name, file.getName(), spectrum);
                                        }
                                        peaks.add(currentRow);
                                }
                        }
                }
                return peaks;
        }

        private List<ComparablePair<Integer, Integer>> parseSpectrum(String str) {
                String peaks[] = str.split(" ");
                ArrayList<ComparablePair<Integer, Integer>> peakList = new ArrayList<ComparablePair<Integer, Integer>>(peaks.length);


                for (String st : peaks) {
                        String tokens[] = st.split(":");
                        ComparablePair<Integer, Integer> p = new ComparablePair<Integer, Integer>(
                                new Integer(tokens[0]),
                                new Integer(tokens[1]));
                        peakList.add(p);


                }
                return peakList;

        }
}
