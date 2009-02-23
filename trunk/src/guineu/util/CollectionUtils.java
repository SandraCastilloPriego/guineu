/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.util;

import java.util.Collection;
import java.util.Iterator;

/**
 * Collection API related utilities
 */
public class CollectionUtils {

    /**
     * Returns an array of ints consisting of the elements of the specified
     * collection.
     * 
     * @param collection Collection of Integers
     * @return Array of ints
     */
    public static int[] toIntArray(Collection<Integer> collection) {
        int array[] = new int[collection.size()];
        int index = 0;
        Iterator<Integer> it = collection.iterator();
        while (it.hasNext()) {
            array[index++] = it.next();
        }
        return array;
    }

    /**
     * Converts an array of ints to an array of Integers
     * 
     * @param array Array of ints
     * @return Array of Integers
     */
    public static Integer[] toIntegerArray(int array[]) {
        Integer newArray[] = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
            newArray[i] = new Integer(array[i]);
        return newArray;
    }

    /**
     * Returns an array of doubles consisting of the elements of the specified
     * collection.
     * 
     * @param collection Collection of Doubles
     * @return Array of doubles
     */
    public static float[] toFloatArray(Collection<Float> collection) {
        float array[] = new float[collection.size()];
        int index = 0;
        Iterator<Float> it = collection.iterator();
        while (it.hasNext()) {
            array[index++] = it.next();
        }
        return array;
    }
    
    /**
     * Returns an array of doubles consisting of the elements of the specified
     * collection.
     * 
     * @param collection Collection of Doubles
     * @return Array of doubles
     */
    public static double[] toDoubleArray(Collection<Double> collection) {
        double array[] = new double[collection.size()];
        int index = 0;
        Iterator<Double> it = collection.iterator();
        while (it.hasNext()) {
            array[index++] = it.next();
        }
        return array;
    }

    /**
     * Checks if the haystack array contains all elements of needles array
     * 
     * @param haystack array of ints
     * @param needles array of ints
     * @return true if haystack contains all elements of needles
     */
    public static boolean isSubset(int haystack[], int needles[]) {
        needleTraversal: for (int i = 0; i < needles.length; i++) {
            for (int j = 0; j < haystack.length; j++) {
                if (needles[i] == haystack[j])
                    continue needleTraversal;
            }
            return false;
        }
        return true;
    }

    /**
     * Checks if the haystack array contains a specified element
     * 
     * @param haystack array of objects
     * @param needle object
     * @return true if haystack contains needle
     */
     public static <T> boolean arrayContains(T haystack[], T needle) {
        for (T test : haystack) {
            if (needle.equals(test))
                return true;
        }
        return false;
    }
    


}
