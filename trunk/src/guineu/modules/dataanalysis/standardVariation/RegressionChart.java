package guineu.modules.dataanalysis.standardVariation;



import java.awt.Color;
import java.awt.Dimension;

import java.util.Vector;
import javax.swing.JInternalFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;



public class RegressionChart extends JInternalFrame {
	public RegressionChart(Vector data, String Name, String Y){
            super(Name, 
                  true, //resizable
                  true, //closable
                  true, //maximizable
                  true);//iconifiable
            this.setSize(500, 500);
            final XYSeries s1 = new XYSeries("Series 1");    
            for(int i = 0; i < data.size(); i++){
                s1.add((Double)data.elementAt(i), new Double(i));	    	
            }

            final XYSeriesCollection dataset = new XYSeriesCollection();
            dataset.addSeries(s1);



            final JFreeChart chart = ChartFactory.createXYLineChart(
                Name,          // chart title
                "X",               // domain axis label
                Y,                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                true,                     // include legend
                true,
                false
            );

           // final XYPlot plot = chart.getXYPlot(); 

            chart.setBackgroundPaint(Color.white);
           // plot.setOutlinePaint(Color.black);
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(500, 420));
            setContentPane(chartPanel);
            //this.add(chartPanel);
            chartPanel.setVisible(true);
	    
	}
}
