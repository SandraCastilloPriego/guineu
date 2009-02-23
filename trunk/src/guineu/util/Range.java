/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.util;

/**
 * This class represents a range of doubles.
 */
public class Range {

    private double min, max;

    /**
     * Create a range with only one value, representing both minimum and
     * maximum. Such range can later be extended using extendRange().
     * 
     * @param minAndMax Range minimum and maximum
     */
    public Range(double minAndMax) {
        this(minAndMax, minAndMax);
    }

    /**
     * Create a range from min to max.
     * 
     * @param min Range minimum
     * @param max Range maximum
     */
    public Range(double min, double max) {
        if (min > max) {
            throw (new IllegalArgumentException(
                    "Range minimum must be <= maximum"));
        }
        this.min = min;
        this.max = max;
    }

    /**
     * Copy constructor.
     * 
     * @param range Range to copy
     */
    public Range(Range range) {
        this(range.getMin(), range.getMax());
    }

    /**
     * @return Range minimun
     */
    public double getMin() {
        return min;
    }

    /**
     * @return Range maximum
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns true if this range contains given value.
     * 
     * @param value Value to check
     * @return True if range contains this value
     */
    public boolean contains(double value) {
        return ((min <= value) && (max >= value));
    }

    /**
     * Returns true if this range contains the whole given range as a subset.
     * 
     * @param checkMin Minimum of given range
     * @param checkMax Maximum of given range
     * @return True if this range contains given range
     */
    public boolean containsRange(double checkMin, double checkMax) {
        return ((checkMin >= min) && (checkMax <= max));
    }

    /**
     * Returns true if this range contains the whole given range as a subset.
     * 
     * @param checkRange Given range
     * @return True if this range contains given range
     */
    public boolean containsRange(Range checkRange) {
        return containsRange(checkRange.getMin(), checkRange.getMax());
    }

    /**
     * Returns true if this range lies within the given range.
     * 
     * @param checkMin Minimum of given range
     * @param checkMax Maximum of given range
     * @return True if this range lies within given range
     */
    public boolean isWithin(double checkMin, double checkMax) {
        return ((checkMin <= min) && (checkMax >= max));
    }

    /**
     * Returns true if this range lies within the given range.
     * 
     * @param checkRange Given range
     * @return True if this range lies within given range
     */
    public boolean isWithin(Range checkRange) {
        return isWithin(checkRange.getMin(), checkRange.getMax());
    }

    /**
     * Extends this range (if necessary) to include the given value
     * 
     * @param value Value to extends this range
     */
    public void extendRange(double value) {
        if (min > value)
            min = value;
        if (max < value)
            max = value;
    }

    /**
     * Extends this range (if necessary) to include the given range
     * 
     * @param extension Range to extends this range
     */
    public void extendRange(Range extension) {
        if (min > extension.getMin())
            min = extension.getMin();
        if (max < extension.getMax())
            max = extension.getMax();
    }

    /**
     * Returns the size of this range.
     * 
     * @return Size of this range
     */
    public double getSize() {
        return (max - min);
    }

    /**
     * Returns the average point of this range.
     * 
     * @return Average
     */
    public double getAverage() {
        return ((min + max) / 2);
    }

    /**
     * Returns the String representation
     * 
     * @return This range as string
     */
    public String toString() {
        return String.valueOf(min) + " - " + String.valueOf(max);
    }
}
