package com.lobanov.multitrading.image;


import com.lobanov.multitrading.image.tasks.TaskAService;
import com.lobanov.multitrading.image.tasks.TaskBService;
import com.lobanov.multitrading.image.tasks.TaskCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.swing.*;

@SpringBootApplication(scanBasePackages = "com.lobanov.multitrading")
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class Application {

    private final TaskBService taskBService;
    private final TaskAService taskAService;
    private final TaskCService taskCService;

    public static void main(String[] args) {

        System.setProperty("java.awt.headless", "false");
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("myframe");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setVisible(true);
        });
        SpringApplication.run(Application.class, args);

    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var startTime = System.currentTimeMillis();
            taskCService.doTask();
            log.info("end time time {}", System.currentTimeMillis() -  startTime);

        };
    }

}
