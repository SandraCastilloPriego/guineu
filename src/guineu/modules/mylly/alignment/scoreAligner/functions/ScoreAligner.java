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
package guineu.modules.mylly.alignment.scoreAligner.functions;

import guineu.data.impl.datasets.SimpleGCGCDataset;
import guineu.data.impl.peaklists.SimplePeakListRowGCGC;
import guineu.modules.mylly.alignment.scoreAligner.ScoreAlignmentParameters;
import guineu.modules.mylly.alignment.scoreAligner.scorer.QuantMassScoreCalc;
import guineu.modules.mylly.alignment.scoreAligner.scorer.ScoreCalculator;
import guineu.modules.mylly.alignment.scoreAligner.scorer.SpectrumDotProd;
import guineu.modules.mylly.datastruct.GCGCData;
import guineu.modules.mylly.datastruct.GCGCDatum;
import guineu.modules.mylly.datastruct.InputSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CyclicBarrier;

/**
 * @author jmjarkko
 */
public class ScoreAligner implements Aligner {

        public final static String name = "Spectrum aligner";
        private int peaksTotal;
        private int peaksDone;
        private volatile boolean aligningDone;
        private volatile Thread[] threads;
        private volatile List<List<GCGCDatum>> peakList;
        private final List<GCGCData> originalPeakList;
        private int originalOrder[];
        private String names[];
        private ScoreCalculator calc;
        private SimpleGCGCDataset alignment;
        private ScoreAlignmentParameters params;

        public ScoreAligner(InputSet data, ScoreAlignmentParameters params, ScoreCalculator calc) {
                this(data, params);
                setScoreCalculator(calc);
        }

        public ScoreAligner(InputSet input, ScoreAlignmentParameters params) {
                this(input != null ? input.getData() : null, params);
        }

        public ScoreAligner(List<GCGCData> dataToAlign, ScoreAlignmentParameters params) {

                this.params = params;
                setScoreCalculator(new SpectrumDotProd());
                //setScoreCalculator(new RTScore());
                List<GCGCData> tempList = new ArrayList<GCGCData>();
                if (dataToAlign != null) {
                        tempList.addAll(dataToAlign);
                }
                originalPeakList = java.util.Collections.unmodifiableList(tempList);


                copyAndSort(dataToAlign);
        }

        private void copyAndSort(List<GCGCData> dataToAlign) {
                Comparator<GCGCData> c = new Comparator<GCGCData>() {

                        public int compare(GCGCData o1, GCGCData o2) {
                                return o2.compoundCount() - o1.compoundCount();
                        }
                };

                if (dataToAlign != null) {

                        List<GCGCData> copyOfData = new ArrayList<GCGCData>(dataToAlign);
                        java.util.Collections.sort(copyOfData, c);
                        originalOrder = new int[copyOfData.size()];
                        for (int i = 0; i < copyOfData.size(); i++) {
                                int ix = 0;
                                for (int j = 0; j < dataToAlign.size(); j++) {
                                        if (copyOfData.get(i) == dataToAlign.get(j)) {
                                                ix = j;
                                        }
                                }
                                originalOrder[i] = ix;
                        }
                        names = new String[copyOfData.size()];

                        peakList = new ArrayList<List<GCGCDatum>>();
                        for (int i = 0; i < copyOfData.size(); i++) {
                                names[i] = dataToAlign.get(i).getName();
                                GCGCDatum[] peakData = copyOfData.get(i).toArray();
                                List<GCGCDatum> peaksInOneFile = new LinkedList<GCGCDatum>();
                                for (GCGCDatum d : peakData) {
                                        peaksInOneFile.add(d);
                                }
                                peakList.add(peaksInOneFile);
                        }
                        List<List<GCGCDatum>> temp = purgeList(peakList);
                        peakList.clear();
                        peakList.addAll(temp);
                } else {
                        peakList = null;
                }
        }

        private List<AlignmentPath> getAlignmentPaths() throws CancellationException {
                List<AlignmentPath> paths;
                List<AlignmentPath> standards;

                standards = alignQuantified();
                paths = generatePathsThreaded(new SpectrumDotProd(), peakList);
                standards.addAll(paths);
                return standards;
        }

        /**
         * Removes peaks which have similarity less than some given similarity
         * in <code>params</code>.
         * @param aList
         * @return
         */
        private List<List<GCGCDatum>> purgeList(List<List<GCGCDatum>> aList) {
                ArrayList<List<GCGCDatum>> newList = new ArrayList<List<GCGCDatum>>();
                for (List<GCGCDatum> l : aList) {
                        LinkedList<GCGCDatum> newSubList = new LinkedList<GCGCDatum>();
                        for (GCGCDatum d : l) {
                                if (d.getSimilarity() >= params.getParameter(ScoreAlignmentParameters.minSimilarity).getValue() || d.hasQuantMass()) {
                                        newSubList.add(d);
                                }
                        }
                        newList.add(newSubList);
                }
                return newList;
        }

        private List<AlignmentPath> alignQuantified() {
                return generatePaths(new QuantMassScoreCalc(), peakList);
        }

        private List<AlignmentPath> generatePaths(ScoreCalculator c, List<List<GCGCDatum>> listOfPeakFiles) {
                List<AlignmentPath> completePaths = new LinkedList<AlignmentPath>();

                for (int i = 0; i < listOfPeakFiles.size(); i++) {
                        Collection<AlignmentPath> paths;
                        int totalAddedPaths = 0;
                        int totalPaths = listOfPeakFiles.get(i).size();
                        while (true) {
                                paths = generatePath(i, c, peakList);
                                if (paths == null) {
                                        return null;
                                } //Meaning we should stop
                                else if (paths.size() == 0) {
                                        break;
                                }
                                /**
                                 * TODO See, if this can be done faster
                                 * without cropping the paths-list after each added
                                 * path.
                                 */
                                while (paths.size() > 0) {
                                        Iterator<AlignmentPath> iter = paths.iterator();
                                        AlignmentPath best = iter.next();
                                        iter.remove();
                                        while (iter.hasNext()) {
                                                AlignmentPath cand = iter.next();
                                                if (best.containsSame(cand)) {
                                                        iter.remove();
                                                }
                                        }
                                        completePaths.add(best);
                                        totalAddedPaths++;
                                        removePeaks(best, listOfPeakFiles);
                                        peaksDone += best.nonEmptyPeaks();
                                }

                                if (totalAddedPaths == totalPaths) {
                                        break;
                                }
                        }
                }
                return completePaths;
        }

        private List<AlignmentPath> generatePathsThreaded(final ScoreCalculator c, final List<List<GCGCDatum>> peaksToUse) throws CancellationException {
                final List<AlignmentPath> paths = Collections.synchronizedList(new LinkedList<AlignmentPath>());
                final List<AlignmentPath> completePaths = new ArrayList<AlignmentPath>();
                final int numThreads = Runtime.getRuntime().availableProcessors();
                final AlignerThread aligners[] = new AlignerThread[numThreads];


                Runnable barrierTask = new Runnable() {

                        public void run() {
                                Collections.sort(paths);
                                while (paths.size() > 0) {
                                        Iterator<AlignmentPath> iter = paths.iterator();
                                        AlignmentPath best = iter.next();
                                        iter.remove();
                                        while (iter.hasNext()) {
                                                AlignmentPath cand = iter.next();
                                                if (best.containsSame(cand)) {
                                                        iter.remove();
                                                }
                                        }
                                        completePaths.add(best);
                                        removePeaks(best, peaksToUse);
                                        peaksDone += best.nonEmptyPeaks();
                                }

                                //Empty the list for further use
                                paths.clear();
                                int currentCol = -1;
                                for (int i = 0; i < peaksToUse.size(); i++) {
                                        if (peaksToUse.get(i).size() > 0) {
                                                currentCol = i;
                                                break;
                                        }
                                }
                                if (currentCol == -1) {
                                        aligningDone = true;
                                        return;
                                }

                                ThreadInfo threadInfos[] = calculateIntervals(numThreads, currentCol, peaksToUse);
                                for (int i = 0; i < numThreads; i++) {
                                        aligners[i].setThreadInfo(threadInfos[i]);
                                }
                        }
                };

                CyclicBarrier barrier = new CyclicBarrier(numThreads, barrierTask);

                //Preliminary setup of thread working
                {
                        int currentCol = 0;
                        ThreadInfo threadInfos[] = calculateIntervals(numThreads, currentCol, peaksToUse);
                        for (int i = 0; i < numThreads; i++) {
                                aligners[i] = new AlignerThread(threadInfos[i], barrier, paths, c, peaksToUse);
                        }
                }

                threads = new Thread[aligners.length];
                for (int i = 0; i < aligners.length; i++) {
                        threads[i] = (new Thread(aligners[i]));
                        threads[i].start();
                }
                for (int i = 0; i < aligners.length; i++) {
                        try {
                                threads[i].join();
                        } catch (InterruptedException e) {
                                break;
                                //TODO Add perhaps more resilence to unforeseen turns of events.
                                //At least now there should not be any case when this main thread
                                //would be interrupted.
                        }
                }
                return completePaths;
        }

        private Collection<AlignmentPath> generatePath(int col, ScoreCalculator c, List<List<GCGCDatum>> listOfPeakFiles) {
                Queue<AlignmentPath> paths = new AlignmentPathQueue(1024);
                for (GCGCDatum cur : listOfPeakFiles.get(col)) {
                        if (cur != null && c.isValid(cur)) {
                                AlignmentPath p = generatePath(col, c, cur, peakList);
                                if (p != null) {
                                        paths.offer(p);
                                }
                        }
                }
                List<AlignmentPath> sortedPaths = new LinkedList<AlignmentPath>(paths);
                java.util.Collections.sort(sortedPaths);
                return sortedPaths;
        }

        private AlignmentPath generatePath(int col, ScoreCalculator c,
                GCGCDatum base, List<List<GCGCDatum>> listOfPeaksInFiles) {
                int len = listOfPeaksInFiles.size();
                AlignmentPath path = new AlignmentPath(len, base, col);
                for (int i = (col + 1) % len; i != col; i = (i + 1) % len) {
                        GCGCDatum bestPeak = null;
                        double bestPeakScore = c.getWorstScore();
                        for (GCGCDatum curPeak : listOfPeaksInFiles.get(i)) {
                                if (curPeak == null || !c.isValid(curPeak)) {
                                        //Either there isn't any peak left or it doesn't fill
                                        // requirements of current score calculator (for example,
                                        // it doesn't have a name).
                                        continue;
                                }                               
                                double score = c.calculateScore(path, curPeak, params);
                                if (score < bestPeakScore) {
                                        bestPeak = curPeak;
                                        bestPeakScore = score;
                                }
                        }

                        double gapPenalty = params.getParameter(ScoreAlignmentParameters.rt1Lax).getValue() * params.getParameter(ScoreAlignmentParameters.rt1Penalty).getValue() + params.getParameter(ScoreAlignmentParameters.rt2Lax).getValue() * params.getParameter(ScoreAlignmentParameters.rt2Penalty).getValue() + params.getParameter(ScoreAlignmentParameters.rtiLax).getValue() * params.getParameter(ScoreAlignmentParameters.rtiPenalty).getValue();

                        if (bestPeak != null && bestPeakScore < gapPenalty) {
                                path.add(i, bestPeak, bestPeakScore);
                        } else {
                                path.addGap(i, gapPenalty);
                        }
                }
                return path;
        }

        private void removePeaks(AlignmentPath p, List<List<GCGCDatum>> listOfPeaks) {
                for (int i = 0; i < p.length(); i++) {
                        GCGCDatum d = p.getPeak(i);
                        if (d != null) {
                                listOfPeaks.get(i).remove(d);
                        }
                }
        }

        private boolean aligningDone() {
                return aligningDone;
        }

        private ThreadInfo[] calculateIntervals(int threads, int col, List<List<GCGCDatum>> listOfPeaks) {
                int diff = listOfPeaks.get(col).size() / threads;
                ThreadInfo threadInfos[] = new ThreadInfo[threads];
                for (int i = 0; i < threads; i++) {
                        threadInfos[i] = new ThreadInfo(i * diff, ((i == threads - 1) ? listOfPeaks.get(col).size() : (i + 1) * diff), col);
                }
                return threadInfos;
        }

        public double getProgress() {
                return ((double) this.peaksDone / (double) this.peaksTotal);
        }

        /* (non-Javadoc)
         * @see gcgcaligner.AbstractAligner#align()
         */
        public SimpleGCGCDataset align() {

                if (alignment == null) //Do the actual alignment if we already do not have the result
                {
                        peaksTotal = 0;
                        for (int i = 0; i < peakList.size(); i++) {
                                peaksTotal += peakList.get(i).size();
                        }
                        alignment = new SimpleGCGCDataset(names, params, this);
                        List<AlignmentPath> addedPaths = getAlignmentPaths();
                        int ID = 1;
                        for (AlignmentPath p : addedPaths) {
                                //Convert alignments to original order of files and add them to final
                                //Alignment data structure
                                SimplePeakListRowGCGC row = (SimplePeakListRowGCGC) p.convertToAlignmentRow(ID++).clone();
                                if (row.getDifference() < 0) {
                                        row.setDifference(0);
                                }
                                alignment.addAlignmentRow(row);

                        }
                }
                alignment.setGCGCDataConcentration();

                SimpleGCGCDataset curAlignment = alignment;
                return curAlignment;
        }

        public ScoreAligner setScoreCalculator(ScoreCalculator calc) {
                this.calc = calc;
                return this;
        }

        public String getName() {
                return toString();
        }

        public String toString() {
                return calc == null ? name : calc.name();
        }

        public ScoreAlignmentParameters getParameters() {
                return params;
        }

        private class AlignerThread implements Runnable {

                private ThreadInfo ti;
                private CyclicBarrier barrier;
                private List<AlignmentPath> readyPaths;
                private ScoreCalculator calc = new SpectrumDotProd();
                private List<List<GCGCDatum>> listOfAllPeaks;

                public void setThreadInfo(ThreadInfo ti) {
                        this.ti = ti;
                }

                public AlignerThread(ThreadInfo ti,
                        CyclicBarrier barrier,
                        List<AlignmentPath> readyPaths,
                        ScoreCalculator c, List<List<GCGCDatum>> peakList) {
                        this.ti = ti;
                        this.barrier = barrier;
                        this.readyPaths = readyPaths;
                        this.calc = c;
                        this.listOfAllPeaks = peakList;
                }

                private void align() {
                        List<GCGCDatum> myList = listOfAllPeaks.get(ti.currentColumn()).subList(ti.startIx(), ti.endIx());
                        Queue<AlignmentPath> myPaths = new LinkedList<AlignmentPath>();
                        for (GCGCDatum cur : myList) {
                                if (cur != null && calc.isValid(cur)) {
                                        AlignmentPath p = generatePath(ti.currentColumn(), calc, cur, listOfAllPeaks);
                                        if (p != null) {
                                                myPaths.offer(p);
                                        }
                                }
                        }
                        readyPaths.addAll(myPaths);
                }

                public void run() {
                        while (!aligningDone()) {
                                align();
                                //Exceptions cause wrong results but do not report
                                //that in any way
                                try {
                                        barrier.await();
                                } catch (InterruptedException e) {
                                        return;
                                } catch (BrokenBarrierException e2) {
                                        return;
                                } catch (CancellationException e3) {
                                        return;
                                }
                        }
                }
        }

        private static class ThreadInfo {

                /**
                 * Start index is inclusive, end index exclusive.
                 */
                private int startIx;
                private int endIx;
                private int column;

                public ThreadInfo(int startIx, int endIx, int col) {
                        this.startIx = startIx;
                        this.endIx = endIx;
                        this.column = col;
                }

                public int currentColumn() {
                        return column;
                }

                public int endIx() {
                        return endIx;
                }

                public int startIx() {
                        return startIx;
                }

                public String toString() {
                        return column + " , [" + startIx + "," + endIx + "]";
                }
        }

        public boolean isConfigurable() {
                return true;
        }

        protected void resetThings() {
                copyAndSort(originalPeakList);
                alignment = null;
        }

        protected void doCancellingActions() {
                for (Thread th : threads) {
                        if (th != null) {
                                th.interrupt();
                        }
                }

        }
}

