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
package guineu.modules.database.tools;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;

public class CheckNode extends DefaultMutableTreeNode {

        public final static int SINGLE_SELECTION = 0;
        public final static int DIG_IN_SELECTION = 4;
        public int selectionMode;
        public boolean isSelected;

        public CheckNode() {
                this(null);
        }

        public CheckNode(Object userObject) {
                this(userObject, true, false, DIG_IN_SELECTION);
        }

        public CheckNode(Object userObject, boolean allowsChildren,
                boolean isSelected, int selectionMode) {
                super(userObject, allowsChildren);
                this.isSelected = isSelected;
                setSelectionMode(selectionMode);
        }

        public void setSelectionMode(int mode) {
                selectionMode = mode;
        }

        public int getSelectionMode() {
                return selectionMode;
        }

        public void setSelectedOnlyMe(boolean isSelected) {
                this.isSelected = isSelected;
        }

        public void setSelected(boolean isSelected) {
                this.isSelected = isSelected;

                if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
                        Enumeration e = children.elements();
                        while (e.hasMoreElements()) {
                                CheckNode node = (CheckNode) e.nextElement();
                                node.setSelected(isSelected);
                        }
                }
        }

        public boolean isSelected() {
                return isSelected;
        }
}
