/*
    Copyright 2007-2008 VTT Biotechnology

    This file is part of GUINEU.
*/
package guineu.modules.filter.Alignment;

import guineu.modules.filter.Alignment.data.AlignStructMol;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.Vector;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class AlignmentChart extends JInternalFrame {
    XYSeriesCollection dataset;
    JFreeChart chart;

    public AlignmentChart(String name){
        super(name, true, true, true, true);          
        try{
            this.setSize(900, 800);                
            this.dataset = new XYSeriesCollection(); 
            this.chart = ChartFactory.createXYLineChart(
                    "Alignment",          
                    "RT1",              
                    "RT2",                 
                    dataset,                 
                    PlotOrientation.VERTICAL,
                    true,                    
                    true,
                    false
                );
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setDisplayToolTips(true);
            this.add(chartPanel);
        }catch(Exception e){}
    } 

    /**
     * Remove all series from the chart
     */
    public void removeSeries(){
        try{
            this.dataset.removeAllSeries();
        }catch(Exception e){}
    }

    /**
     * Add new serie.
     * @param v Vector with the alignments
     * @param Name Name of the type of lipids in this alignment
     */
    public void addSeries(Vector<AlignStructMol> v, String Name){
        try{
            
          /*  XYSeries s1 = new XYSeries(this.getGroup(v.elementAt(0).lipid1) +"-Aligned");
            XYSeries s2 = new XYSeries(this.getGroup(v.elementAt(0).lipid1) +"-Non aligned");
            XYSeries nos1 = new XYSeries("unknown-Aligned");
            XYSeries nos2 = new XYSeries("unknown-Non Aligned");
            for(int i = 0; i < v.size(); i++){
                AlignStructMol aS = v.elementAt(i);
                
                if(aS.Aligned){   
                    if(aS.lipid1.getMolName().matches(".*unknown.*")){
                        nos1.add(aS.lipid1.getAverageRT(), aS.lipid2.getAverageRT());
                    }else{
                        s1.add(aS.lipid1.getAverageRT(), aS.lipid2.getAverageRT());
                    }
                }else{
                    if(aS.lipid1.getMolName().matches(".*unknown.*")){
                        nos2.add(aS.lipid1.getAverageRT(), aS.lipid2.getAverageRT());
                    }else{
                        s2.add(aS.lipid1.getAverageRT(), aS.lipid2.getAverageRT());
                    }
                }
                
            }
            this.dataset.addSeries(nos1);
            this.dataset.addSeries(nos2);
            this.dataset.addSeries(s1);
            this.dataset.addSeries(s2);  */



        }catch(Exception e){}
    }
	
    
    /* public String getGroup(SimpleLipid lipid1){
        
        if(lipid1.getAverageRT() < 300){
            return "Lyso";
        }
        if(lipid1.getAverageRT() >= 300 && lipid1.getAverageRT() < 410){           
            return "GPCho";           
        }
        if(lipid1.getAverageRT() >= 410){
            return "TAG";
        }
        return " ---- ";
    } */
     
    /**
     * Print the chart
     */   
    public void printAlignmentChart(){
        try{          
            XYPlot plot = chart.getXYPlot();
            NumberAxis xAxis = new NumberAxis("RT 1");
            NumberAxis yAxis = new NumberAxis("RT 2");
            xAxis.setAutoRangeIncludesZero(false);
            yAxis.setAutoRangeIncludesZero(false);
            plot.setDomainAxis(xAxis);
            plot.setRangeAxis(yAxis); 
           
            XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
            renderer.setLinesVisible(false);
            renderer.setShapesVisible(true);           
            plot.setRenderer(renderer);
            
            
            XYItemRenderer renderer2 = plot.getRenderer();
            renderer2.setToolTipGenerator(
                new StandardXYToolTipGenerator(
                    StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                    new DecimalFormat("#,##0.00"), new DecimalFormat("#,##0.00")
                )
            );
            chart.setBackgroundPaint(Color.white);
            plot.setOutlinePaint(Color.black);
            		
         }catch(Exception e){}
		
    }        
    
}
