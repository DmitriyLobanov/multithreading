package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.dto.Cluster;
import com.lobanov.multitrading.image.dto.DataPoint;
import org.apache.commons.math3.ml.clustering.CentroidCluster;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ClusterPlot extends JFrame {

    private List<CentroidCluster<DataPoint>> clusters;

    public ClusterPlot(List<CentroidCluster<DataPoint>> clusters) {
        this.clusters = clusters;
        setTitle("K-Means Clustering Results");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(new PlotPanel());
    }

    private class PlotPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawClusters(g);
        }

        private void drawClusters(Graphics g) {
            Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.PINK};
            int colorIndex = 0;
            for (CentroidCluster<DataPoint> cluster : clusters) {
                g.setColor(colors[colorIndex % colors.length]);
                for (DataPoint point : cluster.getPoints()) {
                    int x = (int) (point.creatineMean() * getWidth());
                    int y = (int) (point.hco3Mean() * getHeight());
                    g.fillOval(x, y, 5, 5);
                }
                colorIndex++;
            }
        }
    }

    public static void display(List<CentroidCluster<DataPoint>> clusters) {
        SwingUtilities.invokeLater(() -> {
            ClusterPlot cv = new ClusterPlot(clusters);
            cv.setVisible(true);
        });
    }
}
