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
package guineu.modules.mylly.filter.linearNormalizer;

import guineu.data.Dataset;
import guineu.data.DatasetType;
import guineu.data.PeakListRow;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.datasets.SimpleLCMSDataset;
import guineu.data.impl.peaklists.SimplePeakListRowLCMS;
import guineu.modules.mylly.alignment.scoreAligner.functions.AlignmentSorterFactory;
import guineu.modules.mylly.datastruct.GCGCDatum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jarkko Miettinen
 */
public class LinearNormalizer {

        private final static double BASE_LEVEL = 100.0;
        private final double baseLevel;
        private final PeakListRow onlyStandard;
        private final List<PeakListRow> _standards;
        private final double[] ends;
        private volatile double _done;
        private double _total;

        public LinearNormalizer(Collection<PeakListRow> standards, Dataset input) {
                if (standards.size() == 0) {
                        throw new IllegalArgumentException("No standards given!");
                }
                ends = new double[standards.size()];
                baseLevel = BASE_LEVEL;

                if (standards.size() > 1) {
                        onlyStandard = null;

                        ArrayList<PeakListRow> tempRows = new ArrayList<PeakListRow>(standards);
                        sort(tempRows);
                        _standards = tempRows;

                        // after sorting the standards by RT, it creates a list with the average of
                        // the retention time 1 of one standard and the next standard. It will be use
                        // and delimitation to choose the standard that will be used for each row.
                        for (int i = 0; i < tempRows.size(); i++) {
                                if (tempRows.get(i).getClass().toString().contains("GCGC")) {
                                        double curPoint = ((SimplePeakListRowGCGC) tempRows.get(i)).getRT1();
                                        double nextPoint = (i == tempRows.size() - 1 ? Double.POSITIVE_INFINITY : ((SimplePeakListRowGCGC) tempRows.get(i + 1)).getRT1());
                                        double end = (curPoint + nextPoint) / 2;
                                        ends[i] = end;
                                } else if (tempRows.get(i).getClass().toString().contains("LCMS")) {
                                        double curPoint = ((SimplePeakListRowLCMS) tempRows.get(i)).getRT();
                                        double nextPoint = (i == tempRows.size() - 1 ? Double.POSITIVE_INFINITY : ((SimplePeakListRowLCMS) tempRows.get(i + 1)).getRT());
                                        double end = (curPoint + nextPoint) / 2;
                                        ends[i] = end;
                                }
                        }
                } else if (standards.size() == 1) {
                        _standards = null;
                        Iterator<PeakListRow> i = standards.iterator();
                        onlyStandard = i.next();
                } else {
                        throw new IllegalArgumentException("Empty standard list");
                }

                _total = input == null ? 0 : input.getNumberRows();
        }

        public LinearNormalizer(Collection<PeakListRow> standards) {
                this(standards, null);
        }

        private void sort(List<PeakListRow> rows) {
                if (rows.get(0).getClass().toString().contains("GCGC")) {
                        Collections.sort(rows, AlignmentSorterFactory.getComparator(AlignmentSorterFactory.SORT_MODE.rt2));
                        Collections.sort(rows, AlignmentSorterFactory.getComparator(AlignmentSorterFactory.SORT_MODE.rt1));
                } else if (rows.get(0).getClass().toString().contains("LCMS")) {
                        Collections.sort(rows, AlignmentSorterFactory.getComparator(AlignmentSorterFactory.SORT_MODE.rt));
                }
        }

        protected Dataset actualMap(Dataset input) {
                _total = input.getNumberRows();

                try {
                        if (input.getType() == DatasetType.GCGCTOF) {

                                return gcgcCall((SimpleGCGCDataset) input);
                        } else {
                                return lcmsCall((SimpleLCMSDataset) input);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                }
        }

        private int findProperIndex(double RT) {
                int index = java.util.Arrays.binarySearch(ends, RT);
                if (index < 0) {
                        index = -(index + 1);
                }
                return index;
        }

        public String getName() {
                return "Linear normalizer";
        }

        public SimpleGCGCDataset gcgcCall(SimpleGCGCDataset dataset) throws Exception {
                SimpleGCGCDataset normalized = new SimpleGCGCDataset(dataset.getColumnNames(),
                        dataset.getParameters(),
                        dataset.getAligner());
                if (onlyStandard == null) //Multiple standards
                {
                        double[][] coeffs = new double[this._standards.size()][];
                        for (int i = 0; i < this._standards.size(); i++) {
                                SimplePeakListRowGCGC curStd = (SimplePeakListRowGCGC) this._standards.get(i);
                                double[] curCoeffs = new double[curStd.getNumberPeaks()];
                                for (int j = 0; j < curCoeffs.length; j++) {
                                        curCoeffs[j] = baseLevel / curStd.getPeaks()[j];
                                }
                                coeffs[i] = curCoeffs;
                        }
                        ArrayList<SimplePeakListRowGCGC> rows = new ArrayList<SimplePeakListRowGCGC>();

                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowGCGC scaled = (SimplePeakListRowGCGC) dataset.getAlignment().get(i).clone();
                                int ix = findProperIndex(scaled.getRT1());
                                scaled.scaleArea(coeffs[ix], dataset.getColumnNames());
                                rows.add(scaled);

                        }
                        normalized.addAll(rows);
                } else //Only one standard
                {
                        List<GCGCDatum> stds = ((SimplePeakListRowGCGC) onlyStandard).getDatumArray();
                        double[] coeffs = new double[stds.size()];
                        for (int i = 0; i < stds.size(); i++) {
                                coeffs[i] = baseLevel / stds.get(i).getArea();
                        }
                        ArrayList<SimplePeakListRowGCGC> rows = new ArrayList<SimplePeakListRowGCGC>();
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowGCGC scaled = (SimplePeakListRowGCGC) dataset.getAlignment().get(i).clone();
                                scaled.scaleArea(coeffs, dataset.getColumnNames());
                                rows.add(scaled);
                        }
                        normalized.addAll(rows);
                }
                _done = _total;
                return normalized;
        }

        public double getDone() {
                return _done;
        }

        public double getTotal() {
                return _total;
        }

        private SimpleLCMSDataset lcmsCall(SimpleLCMSDataset dataset) {
                SimpleLCMSDataset normalized = new SimpleLCMSDataset(dataset.getDatasetName());
                for (String columnName : dataset.getAllColumnNames()) {
                        normalized.addColumnName(columnName);
                }
                if (onlyStandard == null) //Multiple standards
                {
                        double[][] coeffs = new double[this._standards.size()][];
                        for (int i = 0; i < this._standards.size(); i++) {
                                SimplePeakListRowLCMS curStd = (SimplePeakListRowLCMS) this._standards.get(i);
                                double[] curCoeffs = new double[curStd.getNumberPeaks()];
                                for (int j = 0; j < curCoeffs.length; j++) {
                                        curCoeffs[j] = baseLevel / curStd.getPeaks()[j];
                                }
                                coeffs[i] = curCoeffs;
                        }
                        ArrayList<SimplePeakListRowLCMS> rows = new ArrayList<SimplePeakListRowLCMS>();

                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowLCMS scaled = (SimplePeakListRowLCMS) dataset.getRows().get(i).clone();
                                int ix = findProperIndex(scaled.getRT());
                                if (!this._standards.contains(dataset.getRows().get(i))) {
                                        scaled.scaleArea(coeffs[ix], dataset.getAllColumnNames().toArray(new String[0]));
                                }
                                rows.add(scaled);

                        }
                        normalized.addAll(rows);
                } else //Only one standard
                {
                        Double[] stds = ((SimplePeakListRowLCMS) onlyStandard).getPeaks();
                        double[] coeffs = new double[stds.length];
                        for (int i = 0; i < stds.length; i++) {
                                coeffs[i] = baseLevel / stds[i];
                        }
                        ArrayList<SimplePeakListRowLCMS> rows = new ArrayList<SimplePeakListRowLCMS>();
                        for (int i = 0; i < dataset.getNumberRows(); i++) {
                                SimplePeakListRowLCMS scaled = (SimplePeakListRowLCMS) dataset.getRows().get(i).clone();
                                if (dataset.getRows().get(i) != onlyStandard) {
                                        scaled.scaleArea(coeffs, dataset.getAllColumnNames().toArray(new String[0]));
                                }
                                rows.add(scaled);
                        }
                        normalized.addAll(rows);
                }
                _done = _total;
                return normalized;
        }
}
