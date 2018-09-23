/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Giulio Auriemma
 */
public class XYChartMaker {
    private static final XYSeriesCollection DATA = new XYSeriesCollection();
    
    /**
     * Adds a new dataset to be mapped on the charts. Remember that the dataset name is a key and only
     * a dataset with a given name could exist.
     * 
     * @param name the name of the new dataset
     * @return     the name if a dataset with that name does not exists, null otherwise
     */
    public static String newDataSeries(String name){
        try{
            DATA.addSeries(new XYSeries(name));
            return name;
        }catch(IllegalArgumentException e){
            return null;
        }
    }
    
    /**
     * Adds a point to the given series. 
     * 
     * @param series the name of the series
     * @param x      the x coordinate
     * @param y      the y coordinate
     */
    public static void addPoint(String series, double x, double y){
        XYSeries curr = DATA.getSeries(series);
        
        if(curr != null) curr.add(x, y);
    }
    
    /**
     * Saves the chart as a png image.
     * 
     * @param fileName the name of the png file
     * @param title    the title of the chart
     * @param x_axis   the name of x axis
     * @param y_axis   the name of y axis
     */
    public static void drowChart(String fileName, String title, String x_axis, String y_axis){
        final JFreeChart chart = ChartFactory.createXYLineChart(
            title,
            x_axis, 
            y_axis, 
            DATA,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        chart.getXYPlot().getDomainAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        chart.getXYPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        try {
            File file = new File(fileName);
            file.createNewFile();
            
            ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
        } catch (IOException ex) {
            Logger.getLogger(XYChartMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
