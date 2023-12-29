package com.lobanov.multitrading.image.dto;

import org.apache.commons.math3.ml.clustering.Clusterable;

public record DataPoint(double creatineMean, double hco3Mean) implements Clusterable {
    @Override
    public double[] getPoint() {
        return new double[]{creatineMean, hco3Mean};
    }
}