/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package guineu.modules.dataanalysis.heatmaps;

import javax.swing.JFrame;

import org.rosuda.javaGD.GDCanvas;
import org.rosuda.javaGD.GDInterface;

/**
 * This is a minimal reimplementation of the GDInterface. When the device is opened,
 * it just creates a new JFrame, adds a new GDCanvas to it (R will plot to this GDCanvas)
 * and tells the program to exit when it is closed.
 */
public class HMFrame extends GDInterface {
  public JFrame f;

  public void gdOpen(double w, double h) {
    f = new JFrame("Heat map");
    c = new GDCanvas(w, h);
    f.add((GDCanvas) c);
    f.pack();
    f.setVisible(true);
    f.setTitle("Heat map Result");
    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

}