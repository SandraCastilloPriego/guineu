/*
 * Copyright 2007-2011 VTT Biotechnology
 *
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
 * Guineu; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package guineu.modules.R.heatmaps;

import guineu.parameters.Parameter;
import guineu.parameters.UserParameter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import org.w3c.dom.Element;

public class ParameterSelection implements
        UserParameter<String, JComboBox> {

        private String name, description;
        String value = "No parameters";
        private List<String> metaData;

        public ParameterSelection(List<String> metaData) {
                this.name = "Parameter selection";
                this.description = "Defines the parameter that will be used to scale the data";
                if(metaData.isEmpty()){
                        metaData.add("No parameters");
                }
                this.metaData = metaData;
        }

        public String getDescription() {
                return description;
        }

        public JComboBox createEditingComponent() {
                ArrayList<Object> choicesList = new ArrayList<Object>();
                for (String metaD : this.metaData) {
                        choicesList.add(metaD);
                }
                Object choices[] = choicesList.toArray();
                JComboBox editor = new JComboBox(choices);
                if (value != null) {
                        editor.setSelectedItem(value);
                }
                return editor;
        }

        public String getValue() {
                return value;
        }

        public void setValue(String newValue) {
                this.value = newValue;
        }

        public void setValueFromComponent(JComboBox component) {
                value = (String) component.getSelectedItem();
        }

        public void setValueToComponent(JComboBox component, String newValue) {
                component.setSelectedItem(newValue);
        }

        public String getName() {
                return this.name;
        }

        public void loadValueFromXML(Element xmlElement) {
                String elementString = xmlElement.getTextContent();
                if (elementString.length() == 0) {
                        return;
                }
                String attrValue = xmlElement.getAttribute("type");
                if (attrValue.equals("parameter")) {
                        for (String data : metaData) {
                                if (data.equals(elementString)) {
                                        value = data;
                                        break;
                                }
                        }
                } else {
                        value = elementString;
                }
        }

        public void saveValueToXML(Element xmlElement) {
                if (value == null) {
                        return;
                }
                xmlElement.setAttribute("type", "parameter");
                xmlElement.setTextContent(value);
        }

        public Parameter clone() {
                ParameterSelection copy = new ParameterSelection(this.metaData);
                copy.setValue(this.getValue());
                return copy;
        }
}
