package com.lobanov.multitrading.image.reader;

import com.lobanov.multitrading.image.dto.Patient;
import com.opencsv.CSVReader;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CsvIOHelperImpl implements CsvIOHelper {

    private final ResourceLoader resourceLoader;

    @Override
    public List<Patient> readPatients() {
        List<Patient> patients = new ArrayList<>();

        try {
            InputStreamReader reader = new InputStreamReader(
                    resourceLoader.getResource("classpath:BD-Patients.csv").getInputStream(),
                    StandardCharsets.UTF_8
            );

            CsvToBean<Patient> csvToBean = new CsvToBeanBuilder<Patient>(reader)
                    .withType(Patient.class)
                    .build();

            for (Patient patient : csvToBean) {
                patients.add(patient);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return patients;
    }
}
