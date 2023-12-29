package com.lobanov.multitrading.image.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @CsvBindByName(column = "Creatinine_mean")
    private double creatineMean;

    @CsvBindByName(column = "HCO3_Mean")
    private double HCO3Mean;
}
