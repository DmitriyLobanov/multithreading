package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.configuration.ApplicationProperties;
import com.lobanov.multitrading.image.dto.Cluster;
import com.lobanov.multitrading.image.dto.DataPoint;
import com.lobanov.multitrading.image.dto.Patient;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.apache.commons.math3.ml.clustering.KMeansPlusPlusClusterer;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClusterProcessorImpl implements ClusterProcessor {

    private final ApplicationProperties applicationProperties;

    @Override
    public void clusterPatients(List<Patient> patients) {

        ExecutorService executorService = Executors.newFixedThreadPool(applicationProperties.getThreadsNumber());

        List<Future<List<CentroidCluster<DataPoint>>>> futures = new ArrayList<>();
        List<DataPoint> normalizedData = normalizeData(patients);

        for (int k = 3; k <= 5; k++) {
            final int clusterCount = k;
            Future<List<CentroidCluster<DataPoint>>> future = executorService.submit(() -> performKMeans(normalizedData, clusterCount));
            futures.add(future);
        }

        for (Future<List<CentroidCluster<DataPoint>>> future : futures) {
            try {
                List<CentroidCluster<DataPoint>> clusters = future.get();
                ClusterPlot.display(clusters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        executorService.shutdown();
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


}
