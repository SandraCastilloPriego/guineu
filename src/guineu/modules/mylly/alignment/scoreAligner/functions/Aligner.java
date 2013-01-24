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
import guineu.modules.mylly.alignment.scoreAligner.scorer.ScoreCalculator;

/**
 * All Aligners should implement a null-argument constructor.
 * 
 * @author Jarkko Miettinen
 */
public interface Aligner {

        /**
         * Aligns all the sample files creating a new data set.
         *
         * @return New GCxGC-MS data set as a result of the alignment
         */
        public SimpleGCGCDataset align();

        /**
         * Sets the type of score calculator to perform the alignment. The result
         * of the alignment will depend on the kind of score calculator used. 
         * 
         * @param calc Score calculator class
         * @return Aligner 
         */
        public Aligner setScoreCalculator(ScoreCalculator calc);

        /**
         *       
         * @return Name of the final data set 
         */
        public String getName();

        /**
         * 
         * @return Progress of the algorithm
         */
        public double getProgress();
}
