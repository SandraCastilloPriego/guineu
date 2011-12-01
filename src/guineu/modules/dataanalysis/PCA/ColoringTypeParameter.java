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
package guineu.modules.dataanalysis.PCA;

import guineu.main.GuineuCore;
import guineu.parameters.UserParameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComboBox;

import org.w3c.dom.Element;

/**
 * Simple Parameter implementation
 * 
 * 
 */
/**
 * @author Taken from MZmine2
 * http://mzmine.sourceforge.net/
 */
public class ColoringTypeParameter implements
        UserParameter<ColoringType, JComboBox> {

        private String name, description;
        private ColoringType value;
        private List<String> metaData;

        public ColoringTypeParameter() {
                this.name = "Coloring type";
                this.description = "Defines how points will be colored";
        }

        @Override
        public String getName() {
                return name;
        }

        @Override
        public String getDescription() {
                return description;
        }

        @Override
        public JComboBox createEditingComponent() {
                ArrayList<Object> choicesList = new ArrayList<Object>();
                try {
                        choicesList.add(ColoringType.NOCOLORING);
                        choicesList.add(ColoringType.COLORBYFILE);
                        this.metaData = GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName();
                        for (String metaD : this.metaData) {
                                choicesList.add(new ColoringType("Color by " + metaD));
                        }
                        Object choices[] = choicesList.toArray();
                        JComboBox editor = new JComboBox(choices);
                        if (value != null) {
                                editor.setSelectedItem(value);
                        }
                        return editor;
                } catch (Exception e) {
                        return new JComboBox();
                }
        }

        @Override
        public ColoringType getValue() {
                return value;
        }

        @Override
        public void setValue(ColoringType value) {
                this.value = value;
        }

        @Override
        public ColoringTypeParameter clone() {
                ColoringTypeParameter copy = new ColoringTypeParameter();
                copy.metaData = this.metaData;
                copy.setValue(this.getValue());
                return copy;
        }

        @Override
        public void setValueFromComponent(JComboBox component) {
                value = (ColoringType) component.getSelectedItem();
        }

        @Override
        public void setValueToComponent(JComboBox component, ColoringType newValue) {
                component.setSelectedItem(newValue);
        }

        @Override
        public void loadValueFromXML(Element xmlElement) {
                String elementString = xmlElement.getTextContent();
                if (elementString.length() == 0) {
                        return;
                }
                String attrValue = xmlElement.getAttribute("type");
                if (GuineuCore.getDesktop().getSelectedDataFiles().length >= 1) {
                        this.metaData = GuineuCore.getDesktop().getSelectedDataFiles()[0].getParametersName();
                }
                if (attrValue.equals("parameter")) {
                        for (String data : metaData) {

                                if (data.equals(elementString)) {
                                        value = new ColoringType(data);
                                        break;
                                }

                        }
                } else {
                        value = new ColoringType(elementString);
                }
        }

        @Override
        public void saveValueToXML(Element xmlElement) {
                if (value == null) {
                        return;
                }
                if (value.isByParameter()) {
                        xmlElement.setAttribute("type", "parameter");
                        xmlElement.setTextContent(value.getParameter().getName());
                } else {
                        xmlElement.setTextContent(value.toString());
                }

        }

        public boolean checkValue(Collection<String> errorMessages) {
                if (value == null) {
                        errorMessages.add(name + " is not set");
                        return false;
                }
                return true;
        }
}
