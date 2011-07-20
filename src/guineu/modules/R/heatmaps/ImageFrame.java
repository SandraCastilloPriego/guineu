/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package guineu.modules.R.heatmaps;

/**
 *
 * @author scsandra
 */
import java.awt.*;
import java.awt.event.*;

public class ImageFrame extends Frame {

        Image img;
        int height, width;

        public ImageFrame(String imageName, int height, int width) {
                super("Heat Map preview");
                this.height = height;
                this.width = width;
                MediaTracker mt = new MediaTracker(this);
                img = Toolkit.getDefaultToolkit().getImage(imageName);
                mt.addImage(img, 0);
                setSize(height, width);
                setVisible(true);
                addWindowListener(new WindowAdapter() {

                        public void windowClosing(WindowEvent we) {
                                dispose();
                        }
                });
        }

        public void update(Graphics g) {
                paint(g);
        }

        public void paint(Graphics g) {
                if (img != null) {
                        g.drawImage(img, this.height, this.width, this);
                } else {
                        g.clearRect(0, 0, getSize().width, getSize().height);
                }
        }
}
