package com.lobanov.multitrading.image.tasks.taskA;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = "com.lobanov.multitrading")
@RequiredArgsConstructor
@Slf4j
public class Application {

    private final TaskBService taskAService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            var startTime = System.currentTimeMillis();
            log.info("Start time {}", startTime);
            taskAService.doBTask();
            log.info("end time time {}", System.currentTimeMillis() -  startTime);

        };
    }

}
