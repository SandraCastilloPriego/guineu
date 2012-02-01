/*
 * Copyright 2007-2012 VTT Biotechnology
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

package guineu.modules.visualization.Rintensityboxplot;


import org.rosuda.javaGD.GDInterface;

/**
 * This is a minimal reimplementation of the GDInterface. When the device is opened,
 * it just creates a new JFrame, adds a new GDCanvas to it (R will plot to this GDCanvas)
 * and tells the program to exit when it is closed.
 */
public class BoxFrame extends GDInterface {
  //public JFrame f;

  public void gdOpen(double w, double h) {
   /* f = new JFrame("Box Plot");
    c = new GDCanvas(w, h);
    f.add((GDCanvas) c);
    f.pack();
    f.setVisible(true);
    f.setTitle("Box plot Result");
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);*/
          c = BoxPlotTask._gdc;
  }

}