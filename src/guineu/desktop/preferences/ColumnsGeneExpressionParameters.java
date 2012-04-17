/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guineu.desktop.preferences;

import guineu.data.ExpressionDataColumnName;
import guineu.parameters.Parameter;
import guineu.parameters.SimpleParameterSet;
import guineu.parameters.parametersType.MultiChoiceParameter;

/**
 *
 * @author scsandra
 */

public class ColumnsGeneExpressionParameters extends SimpleParameterSet {


public static final MultiChoiceParameter<ExpressionDataColumnName> ExpressionData = new MultiChoiceParameter<ExpressionDataColumnName>(
			"Select columns for GCxGC-MS", "Select columns for GCxGC-MS", ExpressionDataColumnName.values());

        public ColumnsGeneExpressionParameters() {
                super(new Parameter[]{ExpressionData});
        }

}
