package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.configuration.ApplicationProperties;
import com.lobanov.multitrading.image.dto.DataPoint;
import com.lobanov.multitrading.image.dto.Patient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ClusterProcessorImpl implements ClusterProcessor {

    private final ApplicationProperties applicationProperties;

    @Override
    public void clusterPatients(List<Patient> patients) {

        ExecutorService executorService = Executors.newFixedThreadPool(applicationProperties.getThreadsNumber());

        List<Future<List<CentroidCluster<DataPoint>>>> clusterFutures = new ArrayList<>();
        List<DataPoint> normalizedData = normalizeData(patients);

        for (int k = 3; k <= 5; k++) {
            final int clusterCount = k;
            Future<List<CentroidCluster<DataPoint>>> future = executorService.submit(() -> performKMeans(normalizedData, clusterCount));
            clusterFutures.add(future);
        }

        for (Future<List<CentroidCluster<DataPoint>>> future : clusterFutures) {
            try {
                List<CentroidCluster<DataPoint>> clusters = future.get();
                log.info("\n");
                log.info("Количесвто класстеров: {}", clusters.size());
                ClusterPlot.display(clusters);
                processClusterCenters(clusters);
                // Расчет индекса Данна для каждого набора кластеров
                Future<Double> dunnIndexFuture = executorService.submit(() -> calculateDunnIndex(clusters));
                double dunnIndex = dunnIndexFuture.get();
                log.info("Индекс Данна: {}", dunnIndex);
                log.info("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
    }

    private void processClusterCenters(List<CentroidCluster<DataPoint>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            CentroidCluster<DataPoint> cluster = clusters.get(i);
            double[] center = cluster.getCenter().getPoint();
            log.info("Центр кластера {}: {}", i + 1, Arrays.toString(center));
        }
    }

    private List<CentroidCluster<DataPoint>> performKMeans(List<DataPoint> normalizedData, int k) {
        KMeansPlusPlusClusterer<DataPoint> clustered = new KMeansPlusPlusClusterer<>(k, 10000, new EuclideanDistance());
        return clustered.cluster(normalizedData);
    }

    private List<DataPoint> normalizeData(List<Patient> patients) {
        double creatineMin = patients.parallelStream()
                .mapToDouble(Patient::getCreatineMean)
                .min()
                .orElseThrow(IllegalStateException::new);
        double creatineMax = patients.parallelStream()
                .mapToDouble(Patient::getCreatineMean)
                .max()
                .orElseThrow(IllegalStateException::new);
        double hco3Min = patients.parallelStream()
                .mapToDouble(Patient::getHCO3Mean)
                .min()
                .orElseThrow(IllegalStateException::new);
        double hco3Max = patients.parallelStream()
                .mapToDouble(Patient::getHCO3Mean)
                .max()
                .orElseThrow(IllegalStateException::new);

        return patients.parallelStream()
                .map(patient -> new DataPoint(
                        (patient.getCreatineMean() - creatineMin) / (creatineMax - creatineMin),
                        (patient.getHCO3Mean() - hco3Min) / (hco3Max - hco3Min)
                ))
                .collect(Collectors.toList());
    }

    // Функция для расчета индекса Данна
    private double calculateDunnIndex(List<CentroidCluster<DataPoint>> clusters) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(applicationProperties.getThreadsNumber());

        // Задача для вычисления минимального расстояния между кластерами
        Callable<Double> minDistanceTask = () -> {
            double minDistance = Double.MAX_VALUE;
            for (int i = 0; i < clusters.size(); i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double distance = calculateDistanceBetweenClusters(clusters.get(i), clusters.get(j));
                    minDistance = Math.min(minDistance, distance);
                }
            }
            return minDistance;
        };

        // Задача для вычисления максимального диаметра кластера
        Callable<Double> maxDiameterTask = () -> {
            double maxDiameter = 0;
            for (CentroidCluster<DataPoint> cluster : clusters) {
                double diameter = calculateClusterDiameter(cluster);
                maxDiameter = Math.max(maxDiameter, diameter);
            }
            return maxDiameter;
        };

        Future<Double> minDistanceFuture = executor.submit(minDistanceTask);
        Future<Double> maxDiameterFuture = executor.submit(maxDiameterTask);

        double minDistance = minDistanceFuture.get();
        double maxDiameter = maxDiameterFuture.get();

        executor.shutdown();

        return minDistance / maxDiameter;
    }

    // Функция для вычисления максимального диаметра кластера
    private double calculateClusterDiameter(CentroidCluster<DataPoint> cluster) {
        double maxDiameter = 0;
        List<DataPoint> points = cluster.getPoints();
        for (int i = 0; i < points.size(); i++) {
            for (int j = i + 1; j < points.size(); j++) {
                double distance = calculateDistance(points.get(i), points.get(j));
                maxDiameter = Math.max(maxDiameter, distance);
            }
        }
        return maxDiameter;
    }

    // Функция для вычисления расстояния между двумя кластерами
    private double calculateDistanceBetweenClusters(CentroidCluster<DataPoint> cluster1, CentroidCluster<DataPoint> cluster2) {
        double minDistance = Double.MAX_VALUE;
        for (DataPoint point1 : cluster1.getPoints()) {
            for (DataPoint point2 : cluster2.getPoints()) {
                double distance = calculateDistance(point1, point2);
                minDistance = Math.min(minDistance, distance);
            }
        }
        return minDistance;
    }

    private static double calculateDistance(DataPoint point1, DataPoint point2) {
        double[] coordinates1 = point1.getPoint();
        double[] coordinates2 = point2.getPoint();

        double sumSquaredDifferences = 0;
        for (int i = 0; i < coordinates1.length; i++) {
            sumSquaredDifferences += Math.pow(coordinates1[i] - coordinates2[i], 2);
        }

        return Math.sqrt(sumSquaredDifferences);
    }

}
