package com.example.flowershoptr.dto.Custom;

import lombok.Data;

@Data
public class CustomBouquetRequestDTO {
    private String occasion;
    private String budgetRange;
    private String preferences;
    private String customerName;
    private String phone;


}