/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
    
 */


package guineu.util.components;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.OverlayLayout;

/**
 * Progress bar with a text label displaying % of completion
 */
public class LabeledProgressBar extends JPanel {

    private JLabel label;
    private JProgressBar progressBar;

    public LabeledProgressBar() {

        setLayout(new OverlayLayout(this));

        label = new JLabel();
        label.setAlignmentX(0.5f);
        label.setFont(label.getFont().deriveFont(11f));
        add(label);

        progressBar = new JProgressBar(0, 100);
        progressBar.setBorderPainted(false);
        add(progressBar);

    }

    public LabeledProgressBar(double value) {
        this();
        setValue(value);
    }

    public void setValue(double value) {
        int percent = (int) (value * 100);
        progressBar.setValue(percent);
        label.setText(percent + "%");
    }
    
    public void setValue(double value, String text) {
        int percent = (int) (value * 100);
        progressBar.setValue(percent);
        label.setText(text);
    }

}
