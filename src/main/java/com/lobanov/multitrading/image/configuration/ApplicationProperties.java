package com.lobanov.multitrading.image.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class ApplicationProperties {
    @Value("${trashhold}")
    private Integer trashHold;
    @Value("${errosian-step}")
    private Integer errosianStep;
    @Value("${threads-num}")
    private Integer threadsNumber;
}
