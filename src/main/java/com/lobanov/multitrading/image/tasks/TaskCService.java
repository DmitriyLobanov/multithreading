package com.lobanov.multitrading.image.tasks;

import com.lobanov.multitrading.image.dto.Patient;
import com.lobanov.multitrading.image.proccessor.ClusterProcessorImpl;
import com.lobanov.multitrading.image.reader.CsvIOHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskCService {

    private final CsvIOHelper csvIOHelper;
    private final ClusterProcessorImpl clusterProcessor;

    public void doTask() {
        List<Patient> patients = csvIOHelper.readPatients();
        clusterProcessor.clusterPatients(patients);
    }
}
