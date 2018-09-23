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
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author Giulio Auriemma
 */
public class BarChartMaker {
    private static final DefaultCategoryDataset DATA = new DefaultCategoryDataset();
    
    /**
     * Adds a point to the given series. 
     * 
     * @param value     the value
     * @param series    the bar name
     * @param category  the set name
     */
    public static void addValue(double value, String series, String category){
        DATA.addValue(value, series, category);
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
        final JFreeChart chart = ChartFactory.createBarChart(
            title,
            x_axis, 
            y_axis, 
            DATA,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        
        chart.getCategoryPlot().getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        
        try {
            File file = new File(fileName);
            file.createNewFile();
            
            ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
        } catch (IOException ex) {
            Logger.getLogger(XYChartMaker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
