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
package guineu.modules.statistics.PCA;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Support class for Principle Components Analysis. This does some light lifting, but the real work
 * is done in the Jama code.
 *
 * @author Gabe Johnson <johnsogg@cmu.edu>
 */
public class PCA {

    Matrix covMatrix;
    EigenvalueDecomposition eigenstuff;
    double[] eigenvalues;
    Matrix eigenvectors;
    SortedSet<PrincipleComponent> principleComponents;

    public PCA(double[][] input) {
        double[][] cov = getCovariance(input);
        covMatrix = new Matrix(cov);
        eigenstuff = covMatrix.eig();
        eigenvalues = eigenstuff.getRealEigenvalues();
        eigenvectors = eigenstuff.getV();
       // eigenvectors.print(6, 3);
        double[][] vecs = eigenvectors.getArray();
        int numComponents = eigenvectors.getColumnDimension(); // same as num rows.
        principleComponents = new TreeSet<PrincipleComponent>();
        for (int i = 0; i < numComponents; i++) {
            double[] eigenvector = new double[numComponents];
            for (int j = 0; j < numComponents; j++) {
                eigenvector[j] = vecs[i][j];
            }
            principleComponents.add(new PrincipleComponent(eigenvalues[i], eigenvector));
        }
    }

    public List<PrincipleComponent> getDominantComponents(int n) {
        List<PrincipleComponent> ret = new ArrayList<PrincipleComponent>();
        int count = 0;
        for (PrincipleComponent pc : principleComponents) {
            ret.add(pc);
            count++;
            if (count >= n) {
                break;
            }
        }
        return ret;
    }

    public int getNumComponents() {
        return eigenvalues.length;
    }    

    public static double[][] getCovariance(double[][] input) {
        int numDataVectors = input.length;
        int n = input[0].length;

        double[] sum = new double[n];
        double[] mean = new double[n];
        for (int i = 0; i < numDataVectors; i++) {
            double[] vec = input[i];
            for (int j = 0; j < n; j++) {
                sum[j] = sum[j] + vec[j];
            }
        }
        for (int i = 0; i < sum.length; i++) {
            mean[i] = sum[i] / numDataVectors;
        }
        double[][] ret = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                double v = getCovariance(input, i, j, mean);
                ret[i][j] = v;
                ret[j][i] = v;
            }
        }
        return ret;
    }

    /**
     * Gives covariance between vectors in an n-dimensional space. The two input arrays store values
     * with the mean already subtracted. Read the code.
     */
    private static double getCovariance(double[][] matrix, int colA, int colB, double[] mean) {
        double sum = 0;
        for (int i = 0; i < matrix.length; i++) {
            double v1 = matrix[i][colA] - mean[colA];
            double v2 = matrix[i][colB] - mean[colB];
            sum = sum + (v1 * v2);
        }
        int n = matrix.length;
        double ret = (sum / (n - 1));
        return ret;
    }
}


