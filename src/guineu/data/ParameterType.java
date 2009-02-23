

package guineu.data;

public enum ParameterType {

    /**
     * Parameter values represented by String instance
     */
    STRING,

    /**
     * Parameter values represented by Integer instance
     */
    INTEGER,

    /**
     * Parameter values represented by Double instance
     */
    DOUBLE,

    /**
     * Parameter values represented by Range instance (range of double values)
     */
    RANGE,

    /**
     * Parameter values represented by Boolean instance
     */
    BOOLEAN,
    
    /**
     * Parameter values represented by a list of selection
     */
    MULTIPLE_SELECTION,
    
    /**
     * File name selection
     */
    FILE_NAME,
    
    /**
     * A list which can be ordered, e.g. by dragging
     */
    ORDERED_LIST

}
