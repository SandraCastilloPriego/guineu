/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.data.impl;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.util.Range;
import java.text.NumberFormat;


/**
 * Simple Parameter implementation
 */
public class SimpleParameter implements Parameter {

    private ParameterType type;
    private String name, description, units;
    private Object defaultValue, minValue, maxValue, possibleValues[];
    private NumberFormat defaultNumberFormat;

    public SimpleParameter(ParameterType type, String name, String description) {
        this(type, name, description, null, null, null, null, null, null);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            String units) {
        this(type, name, description, units, null, null, null, null, null);
    }
 
    public SimpleParameter(ParameterType type, String name, String description,
            String units, NumberFormat format) {
        this(type, name, description, units, null, null, null, null, format);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            String units, Object defaultValue, NumberFormat format) {
        this(type, name, description, units, defaultValue, null, null, null,
                format);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            String units, Object defaultValue, Object possibleValues[],
            NumberFormat format) {
        this(type, name, description, units, defaultValue, null, null,
                possibleValues, format);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            Object defaultValue) {
        this(type, name, description, null, defaultValue, null, null, null,
                null);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            Object defaultValue, Object possibleValues[]) {
        this(type, name, description, null, defaultValue, null, null,
                possibleValues, null);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            String units, Object defaultValue, Object minValue, Object maxValue) {
        this(type, name, description, units, defaultValue, minValue, maxValue,
                null);
    }

    public SimpleParameter(ParameterType type, String name, String description,
            String units, Object defaultValue, Object minValue,
            Object maxValue, NumberFormat format) {
        this(type, name, description, units, defaultValue, minValue, maxValue,
                null, format);
    }

    /**
     * @param type
     * @param name
     * @param description
     * @param units
     * @param defaultValue
     * @param minValue
     * @param maxValue
     * @param possibleValues
     * @param format
     */
    private SimpleParameter(ParameterType type, String name,
            String description, String units, Object defaultValue,
            Object minValue, Object maxValue, Object[] possibleValues,
            NumberFormat defaultNumberFormat) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.units = units;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.possibleValues = possibleValues;
        this.defaultNumberFormat = defaultNumberFormat;

        if ((possibleValues != null) && (possibleValues.length == 0))
            throw new IllegalArgumentException(
                    "Possible values array must not be 0 size");

        switch (type) {
        case BOOLEAN:
            if ((defaultValue != null) && (!(defaultValue instanceof Boolean)))
                throw new IllegalArgumentException("Invalid default value type");
            break;
        case INTEGER:
            if ((defaultValue != null) && (!(defaultValue instanceof Integer)))
                throw new IllegalArgumentException("Invalid default value type");
            if ((minValue != null) && (!(minValue instanceof Integer)))
                throw new IllegalArgumentException("Invalid min value type");
            if ((maxValue != null) && (!(maxValue instanceof Integer)))
                throw new IllegalArgumentException("Invalid max value type");
            if (possibleValues != null) {
                for (Object posVal : possibleValues) {
                    if (!(posVal instanceof Integer))
                        throw new IllegalArgumentException(
                                "Invalid possible values type");
                }
            }
            break;
        case DOUBLE:
            if ((defaultValue != null) && (!(defaultValue instanceof Number)))
                throw new IllegalArgumentException("Invalid default value type");
            if ((minValue != null) && (!(minValue instanceof Number)))
                throw new IllegalArgumentException("Invalid min value type");
            if ((maxValue != null) && (!(maxValue instanceof Number)))
                throw new IllegalArgumentException("Invalid max value type");
            if (possibleValues != null) {
                for (Object posVal : possibleValues) {
                    if (!(posVal instanceof Number))
                        throw new IllegalArgumentException(
                                "Invalid possible values type");
                }
            }
            break;
        case RANGE:
            if ((defaultValue != null) && (!(defaultValue instanceof Range)))
                throw new IllegalArgumentException("Invalid default value type");
            if ((minValue != null) && (!(minValue instanceof Number)))
                throw new IllegalArgumentException("Invalid min value type");
            if ((maxValue != null) && (!(maxValue instanceof Number)))
                throw new IllegalArgumentException("Invalid max value type");
            if (possibleValues != null) {
                throw new IllegalArgumentException(
                        "Range parameters do not support selection from possible values");
            }
            break;
        }

    }

    /**
     * @see net.sf.Guineu.data.Parameter#getType()
     */
    public ParameterType getType() {
        return type;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getName()
     */
    public String getName() {
        return name;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getDescription()
     */
    public String getDescription() {
        return description;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getUnits()
     */
    public String getUnits() {
        return units;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getPossibleValues()
     */
    public Object[] getPossibleValues() {
        return possibleValues;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getDefaultValue()
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getMinimumValue()
     */
    public Object getMinimumValue() {
        return minValue;
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getMaximumValue()
     */
    public Object getMaximumValue() {
        return maxValue;
    }

    public String toString() {
        return getName();
    }

    /**
     * @see net.sf.Guineu.data.Parameter#getNumberFormat()
     */
    public NumberFormat getNumberFormat() {
        return defaultNumberFormat;
    }

}
