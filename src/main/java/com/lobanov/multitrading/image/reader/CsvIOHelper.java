package com.lobanov.multitrading.image.reader;

import com.lobanov.multitrading.image.dto.Patient;

import java.util.List;

public interface CsvIOHelper {

    List<Patient> readPatients();
}
