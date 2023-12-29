package com.lobanov.multitrading.image.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Cluster {
    private List<DataPoint> points;
    private DataPoint centroid;

    public Cluster(DataPoint centroid) {
        this.centroid = centroid;
        this.points = new ArrayList<>();
    }

}