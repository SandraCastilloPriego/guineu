
package guineu.modules.identification.CustomIdentification;

import guineu.data.Parameter;
import guineu.data.ParameterType;
import guineu.data.impl.SimpleParameter;
import guineu.data.impl.SimpleParameterSet;



/**
 * 
 */
public class CustomDBSearchParameters extends SimpleParameterSet {

	public static final Parameter dataBaseFile = new SimpleParameter(
			ParameterType.FILE_NAME, "Database file",
			"Name of file that contains information for peak identification");

	public static final Parameter fieldSeparator = new SimpleParameter(
			ParameterType.STRING, "Field separator",
			"Character(s) used to separate fields in the exported file",
			(Object) ",");

	public static final Parameter fieldOrder = new SimpleParameter(
			ParameterType.ORDERED_LIST, "Field order",
			"Order of items in which they are readed from database file", null,
			FieldItem.values());

	public static final Parameter ignoreFirstLine = new SimpleParameter(
			ParameterType.BOOLEAN, "Ignore first line",
			"Ignore the first line of database file", null, true, null, null,
			null);

	public static final Parameter mzTolerance = new SimpleParameter(
			ParameterType.DOUBLE, "m/z tolerance",
			"Tolerance mass difference to set an identification to one peak",
			"m/z", new Double(1.0), new Double(0.0), null);

	public static final Parameter rtTolerance = new SimpleParameter(
			ParameterType.DOUBLE,
			"Time tolerance",
			"Maximum allowed difference of time to set an identification to one peak",
			"sec", new Double(60.0), new Double(0.0), null);

	public CustomDBSearchParameters() {
		super(new Parameter[] { dataBaseFile, fieldSeparator, fieldOrder,
				ignoreFirstLine, mzTolerance, rtTolerance });
	}

}
