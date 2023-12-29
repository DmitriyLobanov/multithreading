package com.lobanov.multitrading.image.proccessor;

import com.lobanov.multitrading.image.dto.Patient;

import java.util.List;

public interface ClusterProcessor {
    void clusterPatients(List<Patient> patients);
}
