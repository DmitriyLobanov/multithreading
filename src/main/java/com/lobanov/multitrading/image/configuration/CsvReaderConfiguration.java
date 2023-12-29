package com.lobanov.multitrading.image.configuration;

import com.lobanov.multitrading.image.dto.Patient;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;

@Configuration
@RequiredArgsConstructor
public class CsvReaderConfiguration {

    private final ApplicationProperties applicationProperties;

//    @Bean(name = "taskExecutor")
//    public Executor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(applicationProperties.getThreadsNumber());
//        executor.setMaxPoolSize(applicationProperties.getThreadsNumber());
//        executor.setQueueCapacity(500);
//        executor.setThreadNamePrefix("CSVRead-");
//        executor.initialize();
//        return executor;
//    }
//
//    @Bean
//    public CSVReader csvReader() throws Exception {
//        ClassPathResource resource = new ClassPathResource("BD-Patients.csv");
//        InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);
//        return new CSVReader(reader);
//    }
//
//    @Bean
//    public CSVWriter csvWriter(ResourceLoader resourceLoader) throws Exception {
//        Writer writer = new FileWriter(resourceLoader.getResource("classpath:BD-Patients.csv").getFile());
//        return new CSVWriter(writer);
//    }
//
//    @Bean
//    public CsvToBean<Patient> csvToBean(CSVReader csvReader) {
//        ColumnPositionMappingStrategy<Patient> strategy = new ColumnPositionMappingStrategy<>();
//        strategy.setType(Patient.class);
//        strategy.setColumnMapping("Creatinine_mean", "HCO3_mean");
//
//        return new CsvToBeanBuilder<Patient>(csvReader)
//                .withMappingStrategy(strategy)
//                .build();
//    }
}
